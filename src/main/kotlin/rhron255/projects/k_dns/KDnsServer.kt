package rhron255.projects.k_dns

import mu.two.KotlinLogging
import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsMessage.Companion.DNS_DATAGRAM_SIZE
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import rhron255.projects.k_dns.services.DnsMessageBuilder
import rhron255.projects.k_dns.services.ResourceRecordService
import rhron255.projects.k_dns.utils.infoPhaseLog
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.StandardProtocolFamily
import java.net.StandardSocketOptions.SO_REUSEADDR
import java.net.StandardSocketOptions.SO_REUSEPORT
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.system.exitProcess

class KDnsServer(
    val upstream: String,
    val interfaceAddress: String,
    val port: Int,
) {

    companion object {
        val logger = KotlinLogging.logger(KDnsServer::class.java.name)
    }

    fun start() {
        val udpServer = logger.infoPhaseLog("KDnsApplication starting on port 53") {
            DatagramChannel
                .open(StandardProtocolFamily.INET)
                .apply {
                    setOption(SO_REUSEADDR, true)
                    setOption(SO_REUSEPORT, true)
                }
                .bind(InetSocketAddress(interfaceAddress, port))
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

                val records = resourceRecordService.findIpRecordByName(message.questions[0].question)

                val response = with(DnsMessageBuilder.response(message.questions)) {
                    setId(message.header.queryID)
                    if (!records.isEmpty()) {
                        records.forEach(::addAnswer)
                    } else {
                        setResponseCode(DnsResponseCode.NAME_ERROR)
                    }
                    build()
                }


                val size = response.toBytes().size
                DatagramSocket(port).use {
                    it.connect(socket)
                    it.send(DatagramPacket(response.toBytes(), size))
                }
            }
        }
    }
}