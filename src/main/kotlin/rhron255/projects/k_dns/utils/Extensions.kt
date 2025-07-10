package rhron255.projects.k_dns.utils

import mu.two.KLogger
import java.nio.ByteBuffer

fun ByteBuffer.getCurrentByte(): Byte = get(position())
fun ByteBuffer.getAsciiChar(): Char = get().toInt().toChar()
fun ByteBuffer.skip(numBytes: Int): ByteBuffer = position(position() + numBytes)
fun <T> T?.getOrThrow(lambda: () -> Exception): T = this ?: throw lambda()

fun <T> KLogger.infoPhaseLog(msg: String, lambda: () -> T): T {
    info(msg)
    return runCatching { lambda() }.onSuccess {
        info("$msg ... [DONE]")
    }.onFailure { error("$msg ... [FAILED]", it) }.getOrThrow()
}

/**
 * Reads a domain name as specified in the RFC.
 * Supports reading with pointer records.
 * Either way, position is set after the content
 * (i.e after the pointer or after the text if it is there)
 */
fun ByteBuffer.readDomainName(): String {
    val isPointer = getCurrentByte() == 0xc0.toByte()
    val domainName: String
    if (isPointer) {
        val ogPosition = position()
        get()
        val pointingTo = get().toInt()
        position(pointingTo)
        domainName = innerReadDomainName()
//        Skipping past already read pointer
        position(ogPosition + 2)
    } else {
        domainName = innerReadDomainName()
    }
    return domainName
}

private fun ByteBuffer.innerReadDomainName(): String {
    val questionBuilder = StringBuilder()
    while (true) {
        val length = this.get().toInt()
        (0 until length).forEach { _ ->
            questionBuilder.append(this.getAsciiChar())
        }
        if (this.getCurrentByte() == 0x00.toByte()) {
            this.get()
            break
        } else {
            questionBuilder.append(".")
        }
    }
    return questionBuilder.toString()
}

fun String.toLabelBytes(): ByteBuffer {
    val buffer = ByteBuffer.allocate(this.length + this.count { it == '.' })
    this.split(".").forEach {
        buffer.put(it.length.toByte())
        buffer.put(it.toByteArray(Charsets.US_ASCII))
    }
    return buffer.position(0)
}

fun Int.toByteArray(): ByteArray {
    val buffer = ByteArray(Int.SIZE_BYTES)
    buffer[0] = ((this and 0xff000000.toInt()) shr 24).toByte()
    buffer[1] = ((this and 0x00ff0000) shr 16).toByte()
    buffer[2] = ((this and 0x0000ff00) shr 8).toByte()
    buffer[3] = this.toByte()
    return buffer
}