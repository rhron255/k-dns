package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType

class IpResource(
    name: String,
    type: RecordType,
    resourceClass: RecordClass,
    ttl: Int,
    rdlength: Short,
    rdata: List<Int>
) : ResourceRecord<Int>(
    name,
    type,
    resourceClass,
    ttl,
    rdlength,
    rdata,
) {
    override fun getDataSizeInBytes(): Int {
        return rdata.size * Int.SIZE_BYTES
    }

    override fun getDataAsByteArray(): ByteArray {
        val byteArray: ByteArray = ByteArray(getDataSizeInBytes())
        rdata.forEachIndexed { index, int ->
            byteArray[index] = ((int and 0xff000000.toInt()) shr 24).toByte()
            byteArray[index + 1] = ((int and 0x00ff0000) shr 16).toByte()
            byteArray[index + 2] = ((int and 0x0000ff00) shr 8).toByte()
            byteArray[index + 3] = int.toByte()
        }
        return byteArray
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
        rdata = rdata ?: this.rdata,
    )
}