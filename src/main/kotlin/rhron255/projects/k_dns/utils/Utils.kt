package rhron255.projects.k_dns.utils

fun String.isIpAddress(): Boolean {
    val parts = split(".")
    if (parts.size != 4) {
        return false
    }
    return parts.all { it.toIntOrNull()?.let { it in 0..255 } ?: false }
}