package rhron255.projects.k_dns

import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsMessage.Companion.DNS_DATAGRAM_SIZE
import rhron255.projects.k_dns.protocol.DnsQuestion
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.protocol.header.DnsOpcode
import rhron255.projects.k_dns.protocol.resource_records.CanonicalNameAliasResource
import rhron255.projects.k_dns.protocol.resource_records.IpResource
import rhron255.projects.k_dns.services.DnsMessageBuilder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import kotlin.system.exitProcess

class KDnsClient(
    val upstream: String,
    val address: String? = null
) {
    private fun getInput(): String {
        print("> ")
        val input = readlnOrNull() ?: {
            println("Exiting...")
            exitProcess(-1)
        }
        return input as String
    }

    fun start(): Nothing {
        if (address != null) {
            sendReceiveDnsQuery(address, dnsOpcode = DnsOpcode.STANDARD_QUERY)
            exitProcess(0)
        }
        println("KDnsClient started!")

        var input = getInput()
        while (true) {
            if (input == "exit") {
                exitProcess(0)
            }
            try {
                val dnsMessage = sendReceiveDnsQuery(input, dnsOpcode = DnsOpcode.STANDARD_QUERY)

                println(with(StringBuilder()) {
                    appendLine("Server:\t${upstream}")
                    appendLine("Address:\t${upstream}#53")
                    appendLine()
                    appendLine(if (dnsMessage.header.authoritativeAnswer) "Authoritative answer:" else "Non-authoritative answer:")
                    appendLine("Name:\t${dnsMessage.answers[0].name}")
                    dnsMessage.answers.forEach {
                        if (it is IpResource) {
                            it.getIpAddresses().forEach {
                                appendLine("Address:\t${it}")
                            }
                        } else if (it is CanonicalNameAliasResource) {
                            appendLine("Aliases:\t ${it.rdata}")
                        }
                    }
                    deleteCharAt(length - 1)
                    toString()
                })
                input = getInput()

            } catch (e: Exception) {
                println("Error: ${e.message ?: e.toString()}")

                input = getInput()
            }
        }
    }

    fun sendReceiveDnsQuery(
        question: String,
        type: RecordType = RecordType.A,
        recordClass: RecordClass = RecordClass.IN,
        dnsOpcode: DnsOpcode = DnsOpcode.STANDARD_QUERY
    ): DnsMessage {
        val dnsMessage = DnsMessageBuilder
            .query()
            .addQuestion(
                DnsQuestion(question, type, recordClass),
            )
            .setOpcode(dnsOpcode)
            .build()

        return sendReceiveDnsMessage(dnsMessage)
    }

    fun sendReceiveDnsMessage(dnsMessage: DnsMessage): DnsMessage = DatagramSocket().use {
        val dnsBytes = dnsMessage.toBytes()
        it.connect(InetSocketAddress(upstream, 53))
        it.send(
            DatagramPacket(dnsBytes, dnsBytes.size)
        )
        val dnsPacket = DatagramPacket(
            ByteArray(DNS_DATAGRAM_SIZE),
            DNS_DATAGRAM_SIZE
        )
        it.receive(dnsPacket)
        return DnsMessage(dnsPacket.data)
    }
}