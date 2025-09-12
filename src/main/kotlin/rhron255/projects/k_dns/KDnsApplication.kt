package rhron255.projects.k_dns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rhron255.projects.k_dns.utils.verifyPortReuse

@SpringBootApplication
class KDnsApplication


fun main(args: Array<String>) {
    verifyPortReuse()
    runApplication<KDnsApplication>(*args)
}
