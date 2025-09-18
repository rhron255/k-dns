package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.utils.readDomainName
import rhron255.projects.k_dns.utils.toLabelBytes
import java.nio.ByteBuffer

class CanonicalNameAliasResource(
    name: String,
    type: RecordType,
    resourceClass: RecordClass,
    ttl: Int,
    rdlength: Short,
    buffer: ByteBuffer
) : ResourceRecord<String>(
    name,
    type,
    resourceClass,
    ttl,
    rdlength,
    readRdata(buffer, rdlength),
) {
    companion object {
        fun readRdata(buffer: ByteBuffer, rdlength: Short): List<String> {
            var remainingBytes = rdlength.toInt()
            return buildList {
                while (remainingBytes > 0) {
                    val name = buffer.readDomainName()
                    add(name)
                    remainingBytes -= name.length
                    // Two additional bytes are read:
                    // the null byte at the end of the string, and the one before with the length of the cname entry
                    remainingBytes -= 2
                }
            }
        }
    }

    override fun getDataSizeInBytes(): Int {
        return rdata[0].toLabelBytes().array().size
    }

    override fun getDataAsByteArray(): ByteArray {
        return rdata[0].toLabelBytes().array()
    }

    override fun copy(
        name: String?,
        type: RecordType?,
        resourceClass: RecordClass?,
        ttl: Int?,
        rdlength: Short?,
        rdata: List<String>?
    ): ResourceRecord<String> {
        TODO("Not yet implemented")
    }
}
