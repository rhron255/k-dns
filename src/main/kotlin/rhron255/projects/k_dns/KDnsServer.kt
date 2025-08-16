package rhron255.projects.k_dns

import mu.two.KotlinLogging
import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsMessage.Companion.DNS_DATAGRAM_SIZE
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import rhron255.projects.k_dns.services.DnsMessageBuilder
import rhron255.projects.k_dns.services.ResourceRecordService
import rhron255.projects.k_dns.utils.infoPhaseLog
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.system.exitProcess

class KDnsServer(
    val upstream: String,
) {
    companion object {
        val logger = KotlinLogging.logger(KDnsServer::class.java.name)
    }

    fun start() {
        val udpServer = logger.infoPhaseLog("KDnsApplication starting on port 53") {
            DatagramChannel
                .open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEPORT, true)
                .bind(InetSocketAddress("0.0.0.0", 53))
        }

        if (!udpServer.isOpen) {
            exitProcess(-1)
        }

        val resourceRecordService = ResourceRecordService(upstream)

        while (true) {
            if (udpServer.isOpen) {
                val buffer = ByteBuffer.allocate(DNS_DATAGRAM_SIZE)
                val socket: InetSocketAddress = udpServer.receive(buffer) as InetSocketAddress
                val message = DnsMessage(buffer.array())

                logger.info { "Received message $message from $socket" }

                val record = resourceRecordService.findIpRecordByName(message.questions[0].question)

                val response = with(DnsMessageBuilder.response(message.questions)) {
                    setId(message.header.queryID)
                    if (record != null) {
                        addAnswer(record)
                    } else {
                        setResponseCode(DnsResponseCode.NAME_ERROR)
                    }
                    build()
                }


                val size = response.toBytes().size
                DatagramSocket(53).use {
                    it.connect(socket)
                    it.send(DatagramPacket(response.toBytes(), size))
                }
            }
        }
    }
}