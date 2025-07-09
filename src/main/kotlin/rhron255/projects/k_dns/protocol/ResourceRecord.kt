package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.readDomainName
import java.nio.ByteBuffer

// TODO make the rdata different for different record class and types.
//  either with a factory of other implementations. I'm thinking factory for now
data class ResourceRecord(
    val name: String,
    val type: RecordType,
    val resourceClass: RecordClass,
    // in seconds
    val ttl: Int,
    // rdata length in bytes
    val rdlength: Short,
    // the actual data - can be domain names, IPs and such
    val rdata: List<String>
) {
    companion object {
        private const val BINARY_HEADER_BYTE_SIZE = 10
    }

    constructor(buffer: ByteBuffer) : this(
        name = buffer.readDomainName(),
        type = RecordType.fromBytes(buffer),
        resourceClass = RecordClass.fromBytes(buffer),
        ttl = buffer.getInt(),
        rdlength = buffer.getShort(),
        rdata = TODO("Not yet implemented - need to be implemented for each record type")
    )

    // TODO find a way to return the name and not the reference.
    fun toBytes(): ByteArray =
        ByteBuffer.allocate(2 + rdata.sumOf { it.length } + BINARY_HEADER_BYTE_SIZE + 1).apply {
//            This value references the question's domain name - 1100_0000_1100_0000
//            12 bytes into the message - where the header ends
//            The first two bits indicate a pointer
            putShort(0xc00c.toShort())
            putShort(type.code)
            putShort(resourceClass.code)
            putInt(ttl)
            putShort(rdlength)
            rdata.map { it.toByteArray(Charsets.US_ASCII) }.forEach { put(it) }
        }.array()
}