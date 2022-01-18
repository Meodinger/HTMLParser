package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
class FreezableList<E>(isFreezing: Boolean = false) : MutableList<E>, Freezable{

    private val innerList: MutableList<E> = ArrayList()
    override var freezing: Boolean = isFreezing

    override val size: Int get() = innerList.size

    override fun indexOf(element: E): Int = innerList.indexOf(element)
    override fun lastIndexOf(element: E): Int = innerList.lastIndexOf(element)

    override fun contains(element: E): Boolean = innerList.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = innerList.containsAll(elements)

    override fun iterator(): MutableIterator<E> {
        if (freezing) throw UnsupportedOperationException("Iterator unavailable when freezing")
        return innerList.iterator()
    }
    override fun listIterator(): MutableListIterator<E> {
        if (freezing) throw UnsupportedOperationException("Iterator unavailable when freezing")
        return innerList.listIterator()
    }
    override fun listIterator(index: Int): MutableListIterator<E> {
        if (freezing) throw UnsupportedOperationException("Iterator unavailable when freezing")
        return innerList.listIterator(index)
    }

    override fun add(element: E): Boolean {
        if (freezing) throw UnsupportedOperationException("Add unavailable when freezing")
        return innerList.add(element)
    }
    override fun add(index: Int, element: E) {
        if (freezing) throw UnsupportedOperationException("Add unavailable when freezing")
        return innerList.add(index, element)
    }
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        if (freezing) throw UnsupportedOperationException("Add unavailable when freezing")
        return innerList.addAll(index, elements)
    }
    override fun addAll(elements: Collection<E>): Boolean {
        if (freezing) throw UnsupportedOperationException("Add unavailable when freezing")
        return innerList.addAll(elements)
    }
    override fun remove(element: E): Boolean {
        if (freezing) throw UnsupportedOperationException("Remove unavailable when freezing")
        return innerList.remove(element)
    }
    override fun removeAll(elements: Collection<E>): Boolean {
        if (freezing) throw UnsupportedOperationException("Remove unavailable when freezing")
        return innerList.removeAll(elements)
    }
    override fun removeAt(index: Int): E {
        if (freezing) throw UnsupportedOperationException("Remove unavailable when freezing")
        return innerList.removeAt(index)
    }
    override fun clear() {
        if (freezing) throw UnsupportedOperationException("Clear unavailable when freezing")
        return innerList.clear()
    }

    override operator fun get(index: Int): E = innerList[index]
    override operator fun set(index: Int, element: E): E {
        if (freezing) throw UnsupportedOperationException("Set unavailable when freezing")
        return innerList.set(index, element)
    }

    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun retainAll(elements: Collection<E>): Boolean {
        if (freezing) throw UnsupportedOperationException("Retain unavailable when freezing")
        return innerList.retainAll(elements)
    }
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = innerList.subList(fromIndex, toIndex)


}