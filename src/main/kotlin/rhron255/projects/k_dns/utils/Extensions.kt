package rhron255.projects.k_dns.utils

import mu.two.KLogger
import java.nio.ByteBuffer

fun ByteBuffer.getCurrentByte(): Byte = get(position())
fun ByteBuffer.getAsciiChar(): Char = get().toInt().toChar()
fun ByteBuffer.skip(numBytes: Int): ByteBuffer = position(position() + numBytes)
inline fun <T> T?.getOrThrow(crossinline lambda: () -> Exception): T = this ?: throw lambda()

fun <T> KLogger.infoPhaseLog(msg: String, lambda: () -> T): T {
    info(msg)
    return runCatching { lambda() }.onSuccess {
        info("$msg ... [DONE]")
    }.onFailure { error("$msg ... [FAILED]", it) }.getOrThrow()
}

/**
 * Reads a domain name as specified in the RFC.
 * Supports reading with pointer records.
 * Also supports inner pointers...
 */
fun ByteBuffer.readDomainName(): String {
    val question = StringBuilder()
    while (true) {
        val isPointer = getCurrentByte() == 0xc0.toByte()
        if (isPointer) {
            val ogPosition = position()
            get()
            val pointingTo = get().toInt()
            position(pointingTo)
            question.append(readDomainName())
//            +2 in order to skip pointer and reference bytes.
            position(ogPosition + 2)
            break
        } else if (this.getCurrentByte() == 0x00.toByte()) {
            break
        } else {
            val length = this.get().toInt()
            repeat(length) {
                question.append(this.getAsciiChar())
            }
            if (this.getCurrentByte() == 0x00.toByte()) {
                this.get()
                break
            }
            question.append(".")
        }
    }
    return question.toString()
}

fun String.toLabelBytes(): ByteBuffer {
    if (this.isBlank()) {
        return ByteBuffer.allocate(0)
    }
    // +1 for preceding section length and another +1 for null byte at the end
    val buffer = ByteBuffer.allocate(this.length + 2)
    split(".").forEach {
        buffer.put(it.length.toByte())
        buffer.put(it.toByteArray(Charsets.US_ASCII))
    }
    buffer.put(0b0)
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
