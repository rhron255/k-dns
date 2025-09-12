package rhron255.projects.k_dns.utils

import java.net.StandardSocketOptions
import java.nio.channels.DatagramChannel

fun String.isIpAddress(): Boolean {
    val parts = split(".")
    if (parts.size != 4) {
        return false
    }
    return parts.all { it.toIntOrNull()?.let { it in 0..255 } ?: false }
}

fun verifyPortReuse() {
    val canReusePort =
        StandardSocketOptions.SO_REUSEPORT in DatagramChannel.open().use { it.supportedOptions() }
    if (!canReusePort) {
        throw UnsupportedOperationException("Socket reuse not supported! Are you running on windows?")
    }
}