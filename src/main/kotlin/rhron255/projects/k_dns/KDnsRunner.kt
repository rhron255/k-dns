package rhron255.projects.k_dns

import org.springframework.beans.factory.getBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.net.StandardSocketOptions
import java.nio.channels.DatagramChannel

@Component
@Suppress("unused")
class KDnsRunner : ApplicationRunner, ApplicationContextAware {
    private lateinit var context: ApplicationContext
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("server") || (args.optionNames.isEmpty() && args.nonOptionArgs.isEmpty())) {
            val canReusePort = StandardSocketOptions.SO_REUSEPORT in DatagramChannel.open().use { it.supportedOptions() }
            if (!canReusePort) {
                throw UnsupportedOperationException("Socket reuse not supported! Are you running on windows?")
            }
            context.getBean<KDnsServer>().start()
        } else {
            val upstream = args.getOptionValues("upstream")?.firstOrNull()
                ?: throw IllegalArgumentException("Can only set one upstream DNS")
            val query = args.nonOptionArgs.firstOrNull()
            KDnsClient(upstream, query).start()
        }
    }
}