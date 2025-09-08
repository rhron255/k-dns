package rhron255.projects.k_dns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KDnsApplication

fun reconfigureLogger(args: Array<String>) {
    if (args.isEmpty() || args.contains("--server")) {
        System.setProperty("logging.config", "classpath:/server-logback.xml")
        // TODO figure out logback for the DNS client
    }
}

fun main(args: Array<String>) {
    reconfigureLogger(args)
    runApplication<KDnsApplication>(*args)
}
