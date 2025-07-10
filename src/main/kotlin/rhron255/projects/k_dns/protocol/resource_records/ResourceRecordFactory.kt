package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import java.nio.ByteBuffer

class ResourceRecordFactory {
    companion object {
        private val instance = ResourceRecordFactory()
        fun getInstance() = instance
    }

    val typeClassResourceMap = buildMap {
        put(RecordType.A to RecordClass.IN, ::getIpResource)
    }

    fun getResource(recordType: RecordType, recordClass: RecordClass): ResourceRecord<*> {
        typeClassResourceMap[recordType to recordClass]?.let {
            TODO("Not yet implemented - might not be needed")
        }
        TODO("Not yet implemented - might not be needed")
    }

    // TODO move IP retrieval to cache which queries upstream dns on miss.
    fun getIpResource(domainName: String, ip: String): ResourceRecord<*> = IpResource(
        domainName,
        RecordType.A,
        RecordClass.IN,
        360,
        4,
        listOf(
            ByteBuffer.wrap(
                ip.split('.')
                    .map(String::toInt)
                    .map(Int::toByte)
                    .toByteArray()
            ).getInt()
        ),
    )
}