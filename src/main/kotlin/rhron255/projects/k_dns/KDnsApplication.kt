package rhron255.projects.k_dns

import kotlin.system.exitProcess

@Suppress("unused")
class KDnsApplication


fun main(args: Array<String>) {
    if (args[0] == "server") {
        KDnsServer(args[1]).start()
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