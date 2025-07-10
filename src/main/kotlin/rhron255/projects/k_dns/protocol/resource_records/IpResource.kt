package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.utils.toByteArray
import java.nio.ByteBuffer

class IpResource(
    name: String,
    type: RecordType,
    resourceClass: RecordClass,
    ttl: Int,
    rdlength: Short,
    buffer: ByteBuffer
) : ResourceRecord<Int>(
    name,
    type,
    resourceClass,
    ttl,
    rdlength,
    readRdata(buffer, rdlength),
) {
    companion object {
        fun readRdata(buffer: ByteBuffer, rdlength: Short): List<Int> {
            val intsToRead = rdlength / 4
            val rdata = buildList { (0 until intsToRead).forEach { add(buffer.getInt()) } }
            return rdata
        }
    }

    override fun getDataSizeInBytes(): Int {
        return rdata.size * Int.SIZE_BYTES
    }

    override fun getDataAsByteArray(): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(getDataSizeInBytes())
        rdata.map { buffer.put(it.toByteArray()) }
        return buffer.array()
    }

    override fun copy(
        name: String?,
        type: RecordType?,
        resourceClass: RecordClass?,
        ttl: Int?,
        rdlength: Short?,
        rdata: List<Int>?
    ): ResourceRecord<Int> = IpResource(
        name = name ?: this.name,
        type = type ?: this.type,
        resourceClass = resourceClass ?: this.resourceClass,
        ttl = ttl ?: this.ttl,
        rdlength = rdlength ?: this.rdlength,
//        TODO Undo this ungodly sin and fix the damn constructor
        buffer = ByteBuffer.wrap(getDataAsByteArray())
    )

    override fun toString(): String {
        return "IpRecord{name=$name, ttl=$ttl, rdata=${
            getIpAddresses()
        }}"
    }

    fun getIpAddresses(): List<String> =
        rdata.map { it.toByteArray().joinToString(".") { it.toUByte().toString() } }
}