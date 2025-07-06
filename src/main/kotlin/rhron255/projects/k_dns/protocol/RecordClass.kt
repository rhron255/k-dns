package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.getOrThrow
import java.nio.ByteBuffer

@Suppress("unused")
enum class RecordClass(val code: Short, val description: String) {
    IN(0x0001, "Internet"),
    CS(0x0002, "CSNET?"),
    CH(0x0003, "Chaos class"),
    HS(0x0004, "Hesiod"),
    ANY(0x00FF, "Any class");

    companion object {
        @Throws(IllegalArgumentException::class)
        fun fromShort(short: Short): RecordClass = entries.find { it.code == short }
            .getOrThrow { IllegalStateException("Unknown record class value $short") }

        fun fromBytes(byteBuffer: ByteBuffer) = fromShort(byteBuffer.getShort())
    }
}