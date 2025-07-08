package rhron255.projects.k_dns

import mu.two.KotlinLogging
import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.protocol.ResourceRecord
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import rhron255.projects.k_dns.utils.infoPhaseLog
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.system.exitProcess

class KDnsApplication

val logger = KotlinLogging.logger(KDnsApplication::class.java.name)
val testResourceRecord = ResourceRecord(
    "test.ts",
    RecordType.A,
    RecordClass.IN,
    360,
    9,
    listOf("0.0.0.0")
)

private const val DNS_DATAGRAM_SIZE = 512

fun main(args: Array<String>) {
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

            val header = message.header.copy(
                questionCount = 0,
                isQuestion = false,
                responseCode = DnsResponseCode.NO_ERROR,
                answerCount = 1
            ).toBytes().array()

            val response = ByteBuffer.allocate(header.size + testResourceRecord.toBytes().size)
            response.put(header).put(testResourceRecord.toBytes())
            val size = response.array().size
            DatagramSocket(53).use {
                it.connect(socket)
                it.send(DatagramPacket(response.array(), size))
            }
        }
    }
}
