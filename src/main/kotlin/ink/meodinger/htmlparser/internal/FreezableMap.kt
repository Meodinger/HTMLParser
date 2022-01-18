package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
class FreezableMap<K, V>(isFreezing: Boolean = false) : MutableMap<K ,V>, Freezable {

    private val innerMap: MutableMap<K, V> = HashMap()
    override var freezing: Boolean = isFreezing

    override val size: Int get() = innerMap.size
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() {
        if (freezing) throw UnsupportedOperationException("Entries unavailable when freezing")
        return innerMap.entries
    }
    override val keys: MutableSet<K> get() {
        if (freezing) throw UnsupportedOperationException("Keys unavailable when freezing")
        return innerMap.keys
    }
    override val values: MutableCollection<V> get() {
        if (freezing) throw UnsupportedOperationException("Values unavailable when freezing")
        return innerMap.values
    }

    override fun containsKey(key: K): Boolean = innerMap.containsKey(key)
    override fun containsValue(value: V): Boolean = innerMap.containsValue(value)

    override operator fun get(key: K): V? = innerMap[key]

    override fun put(key: K, value: V): V? {
        if (freezing) throw UnsupportedOperationException("Put unavailable when freezing")
        return innerMap.put(key, value)
    }
    override fun putAll(from: Map<out K, V>) {
        if (freezing) throw UnsupportedOperationException("Put unavailable when freezing")
        return innerMap.putAll(from)
    }
    override fun remove(key: K): V? {
        if (freezing) throw UnsupportedOperationException("Remove unavailable when freezing")
        return innerMap.remove(key)
    }
    override fun clear() {
        if (freezing) throw UnsupportedOperationException("Clear unavailable when freezing")
        return innerMap.clear()
    }

    override fun isEmpty(): Boolean = innerMap.isEmpty()





}