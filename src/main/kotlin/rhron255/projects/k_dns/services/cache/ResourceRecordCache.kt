package rhron255.projects.k_dns.services.cache

import org.springframework.stereotype.Service
import rhron255.projects.k_dns.protocol.resource_records.ResourceRecord

@Service
class ResourceRecordCache() : AbstractMutableCollection<ResourceRecord<*>>() {
    data class RRCObject(
        val record: ResourceRecord<*>,
        val timestamp: Long = System.currentTimeMillis(),
    ) {

        override fun equals(other: Any?) = if (other is RRCObject) other.record == record else super.equals(other)
        override fun hashCode() = record.hashCode()
        fun isExpired() = timestamp + (record.ttl * 1000) < System.currentTimeMillis()
    }

    private val cache = ConcurrentHashSet<RRCObject>()
    override val size: Int
        get() = cache.size

    override operator fun contains(element: ResourceRecord<*>): Boolean {
        val cacheResult = cache.find { it.record == element } ?: return false
        if (cacheResult.isExpired()) {
            cache.remove(cacheResult)
            return false
        }
        return true
    }

    override fun iterator(): MutableIterator<ResourceRecord<*>> =
        object : MutableIterator<ResourceRecord<*>>, AbstractIterator<ResourceRecord<*>>() {
            private val internalIterator = cache.iterator()
            override fun computeNext() {
                while (internalIterator.hasNext()) {
                    val next = internalIterator.next()
                    if (next.isExpired()) {
                        internalIterator.remove()
                    } else {
                        setNext(next.record)
                        return
                    }
                }
                done()
            }

            override fun remove() = internalIterator.remove()
        }

    override fun add(element: ResourceRecord<*>) = cache.add(RRCObject(element))

}