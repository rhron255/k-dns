package rhron255.projects.k_dns.protocol.resource_records

import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.utils.readDomainName
import java.nio.ByteBuffer

typealias RecordBuilder = (String, RecordType, RecordClass, Int, Short, ByteBuffer) -> ResourceRecord<*>

object ResourceRecordFactory {

    fun getRecordReader(type: RecordType, recordClass: RecordClass): RecordBuilder {
        return when (type to recordClass) {
            RecordType.A to RecordClass.IN -> ::IpResource
            RecordType.CNAME to RecordClass.IN -> ::CanonicalNameAliasResource
            else -> TODO("$type -> $recordClass not yet implemented")
        }
    }

    fun getResource(buffer: ByteBuffer): ResourceRecord<*> {
        val name = buffer.readDomainName()
        val type = RecordType.fromBytes(buffer)
        val resourceClass = RecordClass.fromBytes(buffer)
        val ttl = buffer.getInt()
        val rdlength = buffer.getShort()
        return getRecordReader(type, resourceClass)(
            name, type, resourceClass, ttl, rdlength, buffer
        )
    }
}
