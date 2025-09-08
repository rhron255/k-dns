package rhron255.projects.k_dns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KDnsApplication

fun main(args: Array<String>) {
//    if (args[0] == "client") {
////        System.setProperty("logging.config", "classpath:/client-logback.xml")
//    } else {
//        // TODO figure out logback for the DNS server
//    }
    runApplication<KDnsApplication>(*args)
}
