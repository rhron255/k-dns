package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.utils.toLabelBytes
import java.nio.ByteBuffer

abstract class ResourceRecord<T>(
    val name: String,
    val type: RecordType,
    val resourceClass: RecordClass,
    // in seconds
    val ttl: Int,
    // rdata length in bytes
    val rdlength: Short,
    // the actual data - can be domain names, IPs and such
    val rdata: List<T>
) {
    private var usePointer = false
    var pointerOffset = 0

    companion object {
        private const val BINARY_HEADER_BYTE_SIZE = 10
        private const val LABEL_POINTER_SIZE = 2
        private const val POINTER_MARKER = 0xc0.toByte()
    }

    abstract fun getDataSizeInBytes(): Int
    abstract fun getDataAsByteArray(): ByteArray
    abstract fun copy(
        name: String? = null,
        type: RecordType? = null,
        resourceClass: RecordClass? = null,
        ttl: Int? = null,
        rdlength: Short? = null,
        rdata: List<T>? = null,
    ): ResourceRecord<T>

    fun setPointer(offset: Int) {
        usePointer = true
        pointerOffset = offset
    }

    fun toBytes(): ByteArray {
        val buffer = if (usePointer) {
            ByteBuffer.allocate(LABEL_POINTER_SIZE + getDataSizeInBytes() + BINARY_HEADER_BYTE_SIZE + 1).apply {
//            This value references the question's domain name - 1100_0000_1100_0000
//            12 bytes into the message - where the header ends
//            The first two bits indicate a pointer
                put(POINTER_MARKER)
                put(pointerOffset.toByte())
            }
        } else {
            ByteBuffer.allocate(
                name.toLabelBytes().array().size + getDataSizeInBytes() + BINARY_HEADER_BYTE_SIZE + 1
            ).apply {
                put(name.toLabelBytes())
            }
        }
        return buffer.apply {
            putShort(type.code)
            putShort(resourceClass.code)
            putInt(ttl)
            putShort(rdlength)
            put(getDataAsByteArray())
        }.array()
    }
}