package rhron255.projects.k_dns

import java.net.StandardSocketOptions.SO_REUSEPORT
import java.nio.channels.DatagramChannel
import kotlin.system.exitProcess

@Suppress("unused")
class KDnsApplication

fun main(args: Array<String>) {
    if (args[0] == "server") {
        val canReusePort = SO_REUSEPORT in DatagramChannel.open().supportedOptions()
        if (!canReusePort) {
            throw UnsupportedOperationException("Socket reuse not supported! Are you running on windows?")
        }
        KDnsServer(args[1], "127.0.0.1", 53).start()
    } else if (args[0] == "client") {
        if (args[1] == "-") {
            KDnsClient(args[2]).start()
        } else {
            KDnsClient(args[2], args[1]).start()
        }
    } else {
        print("Usage: kdns server <upstream-address> OR kdns client <domain-name> <dns-address> OR kdns client - <dns-address>")
        exitProcess(-1)
    }
}