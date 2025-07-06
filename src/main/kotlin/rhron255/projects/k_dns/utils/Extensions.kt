package rhron255.projects.k_dns.utils

import mu.two.KLogger
import java.nio.ByteBuffer

fun ByteBuffer.getCurrentByte(): Byte = get(position())
fun ByteBuffer.getAsciiChar(): Char = get().toInt().toChar()
fun ByteBuffer.skip(numBytes: Int) = position(position() + numBytes)
fun <T> T?.getOrThrow(lambda: () -> Exception): T = this ?: throw lambda()

fun <T> KLogger.infoPhaseLog(msg: String, lambda: () -> T): T {
    info(msg)
    return runCatching { lambda() }.onSuccess {
        info("$msg ... [DONE]")
    }.onFailure { error("$msg ... [FAILED]", it) }.getOrThrow()
}