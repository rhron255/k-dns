package rhron255.projects.k_dns

class KDnsApplication



fun main(args: Array<String>) {
    if ("--client" in args) {
        TODO("DNS client not yet implemented")
    } else {
        KDnsServer().start()
    }
}