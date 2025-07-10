package rhron255.projects.k_dns

import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsMessage.Companion.DNS_DATAGRAM_SIZE
import rhron255.projects.k_dns.protocol.DnsQuestion
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.protocol.header.DnsHeader
import rhron255.projects.k_dns.protocol.header.DnsOpcode
import rhron255.projects.k_dns.protocol.resource_records.CanonicalNameAliasResource
import rhron255.projects.k_dns.protocol.resource_records.IpResource
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
            sendReceiveDnsQuery(address)
            exitProcess(0)
        }
        println("KDnsClient started!")
        var input = getInput()
        while (true) {
            if (input == "exit") {
                exitProcess(0)
            }
            val dnsMessage = sendReceiveDnsQuery(input)

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
        }
    }

    private fun sendReceiveDnsQuery(input: String): DnsMessage {
        val dnsMessage = DnsMessage(
            DnsHeader(
//                TODO make this a UUID
                queryID = 1234,
                questionCount = 1,
                isQuestion = true,
                opcode = DnsOpcode.STANDARD_QUERY,
            ),
            listOf(
                DnsQuestion(
                    input,
                    RecordType.A,
                    RecordClass.IN
                )
            )
        ).toBytes()

        DatagramSocket().use {
            it.connect(InetSocketAddress(upstream, 53))
            it.send(
                DatagramPacket(dnsMessage, dnsMessage.size)
            )
            val dnsPacket = DatagramPacket(
                ByteArray(DNS_DATAGRAM_SIZE),
                DNS_DATAGRAM_SIZE
            )
            it.receive(dnsPacket)
            return DnsMessage(dnsPacket.data)
        }
    }
}