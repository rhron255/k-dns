package rhron255.projects.k_dns.protocol

import java.nio.ByteBuffer

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

    fun toBytes(): ByteArray =
        ByteBuffer.allocate(name.length + rdata.size + BINARY_HEADER_BYTE_SIZE).apply {
            put(name.toByteArray(Charsets.US_ASCII))
            putShort(type.code)
            putShort(resourceClass.code)
            putInt(ttl)
            putShort(rdlength)
            rdata.map { it.toByteArray(Charsets.US_ASCII) }.forEach { put(it) }
        }.array()
}