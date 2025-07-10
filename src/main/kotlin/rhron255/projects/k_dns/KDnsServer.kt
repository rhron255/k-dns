package rhron255.projects.k_dns

import mu.two.KotlinLogging
import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsMessage.Companion.DNS_DATAGRAM_SIZE
import rhron255.projects.k_dns.protocol.DnsQuestion
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import rhron255.projects.k_dns.protocol.resource_records.IpResource
import rhron255.projects.k_dns.protocol.resource_records.ResourceRecord
import rhron255.projects.k_dns.utils.infoPhaseLog
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.system.exitProcess

// TODO move IP retrieval to cache which queries upstream dns on miss.
fun getIpResource(domainName: String, ip: String): ResourceRecord<*> = IpResource(
    domainName,
    RecordType.A,
    RecordClass.IN,
    360,
    4,
    ByteBuffer.wrap(
        ip.split('.')
            .map(String::toInt)
            .map(Int::toByte)
            .toByteArray()
    ),
)

class KDnsServer {
    companion object {
        val logger = KotlinLogging.logger(KDnsServer::class.java.name)
    }

    private fun getRecords(questions: List<DnsQuestion>) =
        questions.map { getIpResource(it.question, "10.20.11.7") }

    fun start() {
        val udpServer = logger.infoPhaseLog("KDnsApplication starting on port 53") {
            DatagramChannel
                .open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEPORT, true)
                .bind(InetSocketAddress(53))
        }

        if (!udpServer.isOpen) {
            exitProcess(-1)
        }

        logger.info("KDnsApplication started on port 53")
        while (true) {
            if (udpServer.isOpen) {
                val buffer = ByteBuffer.allocate(DNS_DATAGRAM_SIZE)
                val socket: InetSocketAddress = udpServer.receive(buffer) as InetSocketAddress
                val message = DnsMessage(buffer.array())

                logger.info { "Received message $message from $socket" }

                val response = DnsMessage(
                    message.header.copy(
                        isQuestion = false,
                        responseCode = DnsResponseCode.NO_ERROR,
                        answerCount = 1
                    ), message.questions, getRecords(message.questions)
                )

                val size = response.toBytes().size
                DatagramSocket(53).use {
                    it.connect(socket)
                    it.send(DatagramPacket(response.toBytes(), size))
                }
            }
        }
    }
}