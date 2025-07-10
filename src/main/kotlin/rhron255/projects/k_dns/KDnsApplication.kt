package rhron255.projects.k_dns

class KDnsApplication


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        KDnsServer().start()
    } else if (args.size == 2) {
        if (args[0] == "-") {
            KDnsClient(args[1]).start()
        } else {
            KDnsClient(args[1], args[0]).start()
        }
    } else {
        print("Usage: kdns <domain-name> <dns-address> OR kdns - <dns-address>")
    }
}