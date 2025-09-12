package rhron255.projects.k_dns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rhron255.projects.k_dns.utils.isIpAddress
import java.net.StandardSocketOptions
import java.nio.channels.DatagramChannel
import kotlin.reflect.jvm.jvmName
import kotlin.system.exitProcess

@SpringBootApplication
class KDnsApplication

fun reconfigureLogger(args: Array<String>) {
    if (args.isEmpty() || args.contains("--server")) {
        System.setProperty("logging.config", "classpath:/server-logback.xml")
        // TODO figure out logback for the DNS client
    } else {
        System.setProperty("logging.level.root", "warn")
        System.setProperty("spring.main.web-application-type", "none")
    }
}

fun validateArgs(args: Array<String>) {
    if ("--server" in args) {
        return
    } else {
        val upstreamAddress = if ("-u" in args) {
            args[args.indexOf("-u") + 1]
        } else if ("--upstream" in args) {
            args[args.indexOf("--upstream") + 1]
        } else {
            null
        }
        if (upstreamAddress != null && upstreamAddress.isIpAddress()) {
            runCatching {
                KDnsClient(upstreamAddress)
                    .sendReceiveDnsQuery(upstreamAddress)
            }.onFailure {
                print(
                    it::class.jvmName + ": " + (it.message ?: "Failed to connect to upstream DNS at $upstreamAddress")
                )
                exitProcess(-1)
            }
        } else {
            print("Usage: ...")
            exitProcess(-1)
        }
    }
}

fun main(args: Array<String>) {
    validateArgs(args)
    reconfigureLogger(args)
    val canReusePort =
        StandardSocketOptions.SO_REUSEPORT in DatagramChannel.open().use { it.supportedOptions() }
    if (!canReusePort) {
        throw UnsupportedOperationException("Socket reuse not supported! Are you running on windows?")
    }
    runApplication<KDnsApplication>(*args)
}
