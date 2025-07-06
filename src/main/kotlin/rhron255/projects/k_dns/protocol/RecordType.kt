package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.getOrThrow
import java.nio.ByteBuffer

@Suppress("unused")
enum class RecordType(val code: Short, val description: String, val category: Category = Category.DNS) {
    A(0x0001, "Host Address"),
    NS(0x0002, "Authoritative name server"),
    MD(0x0003, "Mail destination", Category.MAIL),
    MF(0x0004, "Mail forwarder", Category.MAIL),
    CNAME(0x0005, "Canonical alias name"),
    SOA(0x0006, "Zone of authority marker"),
    MB(0x0007, "Mailbox domain name", Category.MAIL),
    MG(0x0008, "Mail group member", Category.MAIL),
    MR(0x0009, "Mail rename domain name", Category.MAIL),
    NULL(0x000A, "NULL RR"),
    WKS(0x000B, "Well Know Service description"),
    PTR(0x000C, "Domain Name pointer"),
    HINFO(0x000D, "Host Information"),
    MINFO(0x000E, "Mail box or list Information", Category.MAIL),
    MX(0x000F, "Mail exchange", Category.MAIL),
    TXT(0x0010, "Text record"),
    AXFR(0x00FC, "Zone transfer"),
    MAILB(0x00FD, "Mailbox records", Category.MAIL),
    MAILA(0x00FE, "Mail agents", Category.MAIL),
    ALL(0x00FF, "All");

    companion object {
        @Throws(IllegalArgumentException::class)
        fun fromShort(short: Short): RecordType = entries.find { it.code == short }
            .getOrThrow { IllegalStateException("Unknown record type value $short") }

        fun fromBytes(byteBuffer: ByteBuffer) = fromShort(byteBuffer.getShort())
    }
}