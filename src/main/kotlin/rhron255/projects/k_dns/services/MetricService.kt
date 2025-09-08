package rhron255.projects.k_dns.services

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import rhron255.projects.k_dns.protocol.DnsMessage

@Service
class MetricService(private val meterRegistry: MeterRegistry) {
    fun recordQuery(dnsMessage: DnsMessage) {
        Counter.builder("kdns.queries.total")
            .tag("type", dnsMessage.questions[0].questionType.name)
            .tag("class", dnsMessage.questions[0].questionClass.name)
            .register(meterRegistry)
            .increment()
    }

//    TODO We could analyse IP changes, domain check anomalies,
//     records fetched, etc to detect intrusions and block domains.
}