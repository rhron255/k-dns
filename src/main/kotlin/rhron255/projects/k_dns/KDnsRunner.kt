package rhron255.projects.k_dns

import org.springframework.beans.factory.getBean
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class KDnsRunner : CommandLineRunner, ApplicationContextAware {
    private lateinit var context: ApplicationContext
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    override fun run(args: Array<String>) {
        if (args.contains("--server") || args.isEmpty()) {
            context.getBean<KDnsServer>().start()
        } else {
            if (args.size == 3) {
                val (address, _, upstream) = args
                KDnsClient(upstream, address).start()
            } else if (args.size == 2) {
                val (_, upstream) = args
                KDnsClient(upstream).start()
            } else {
                throw IllegalArgumentException("Can only set one upstream DNS")
            }
        }
    }
}