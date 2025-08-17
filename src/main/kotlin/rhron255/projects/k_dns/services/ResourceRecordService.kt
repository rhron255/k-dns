package rhron255.projects.k_dns.services

import rhron255.projects.k_dns.KDnsClient
import rhron255.projects.k_dns.protocol.resource_records.IpResource
import rhron255.projects.k_dns.services.cache.ResourceRecordCache

class ResourceRecordService(
    upstream: String
) {
    private val cache = ResourceRecordCache()
    private val client = KDnsClient(upstream)

    fun findIpRecordByName(cname: String): List<IpResource> {
        val record = cache.find { it.name == cname && it is IpResource } as? IpResource?
        if (record == null) {
            val response = client.sendReceiveDnsQuery(cname)
            cache.addAll(response.answers + response.additionalResourceRecords + response.authorityResourceRecords)
            return response.answers.filterIsInstance<IpResource>()
        }
        return listOf(record)
    }
}