package rhron255.projects.k_dns

import mu.two.KotlinLogging
import rhron255.projects.k_dns.protocol.DnsQuestionMessage
import rhron255.projects.k_dns.utils.infoPhaseLog
import java.net.InetSocketAddress
import java.net.StandardProtocolFamily
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class KDnsApplication

val logger = KotlinLogging.logger(KDnsApplication::class.java.name)

private const val DNS_DATAGRAM_SIZE = 512

fun main(args: Array<String>) {
    val udpServer = logger.infoPhaseLog("KDnsApplication starting on port 53") {
        DatagramChannel
            .open(StandardProtocolFamily.INET)
            .bind(InetSocketAddress(53))
    }
    while (true) {
        if (udpServer.isOpen) {
            logger.infoPhaseLog("KDnsApplication started on port 53") {
                val buffer = ByteBuffer.allocate(DNS_DATAGRAM_SIZE)
                val socket = udpServer.receive(buffer)
                val message = DnsQuestionMessage(buffer.array())
                logger.info { "Received message $message from $socket" }
            }
        }
    }
}
