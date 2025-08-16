package rhron255.projects.k_dns.services

import rhron255.projects.k_dns.KDnsClient
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import rhron255.projects.k_dns.protocol.resource_records.IpResource
import rhron255.projects.k_dns.services.cache.ResourceRecordCache
import java.nio.ByteBuffer

fun getIpResource(domainName: String, ip: String = "142.250.75.110") = IpResource(
    domainName,
    RecordType.A,
    RecordClass.IN,
    360,
    4,
    ByteBuffer.wrap(
        ip.split('.')
            .map(String::toInt)
            .map(Int::toByte)
            .toByteArray()
    ),
)

class ResourceRecordService(
    upstream: String
) {
    private val cache = ResourceRecordCache()
    private val client = KDnsClient(upstream)

    fun findIpRecordByName(cname: String): IpResource? {
        val record = cache.find { it.name == cname && it is IpResource } as? IpResource?
        if (record == null) {
            return listOf(getIpResource(cname), null).random()
        }
        return record
    }
}