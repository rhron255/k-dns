package rhron255.projects.k_dns.services.cache

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ConcurrentHashSet<T>() : AbstractSet<T>(), MutableSet<T> {
    companion object {
        val PRESENT: Any = Any()

    }

    val map: ConcurrentHashMap<T & Any, Any> = ConcurrentHashMap()


    override fun iterator(): MutableIterator<T> {
        return map.keys.iterator()
    }


    override fun isEmpty() = map.isEmpty()

    override operator fun contains(element: T) = map.containsKey(element)

    override fun add(element: T): Boolean = map.put(element!!, PRESENT) == null

    override fun remove(element: T) = map.remove(element) === PRESENT


    override fun clear() = map.clear()

    override fun spliterator(): Spliterator<T> = map.keys.spliterator()

    override fun toArray(): Array<Any> = map.keys.toArray()

    override val size: Int
        get() = map.size
}