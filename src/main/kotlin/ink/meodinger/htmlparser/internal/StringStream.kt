package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * String Char Stream
 */
class StringStream(string: String) : Stream<Char> {

    val array: CharArray = string.toCharArray()
    val size = array.size

    var pointer: Int = 0
        private set
    private var row: Int = 1
    private var col: Int = 1

    private var markP: Int = -1
    private var markR: Int = -1
    private var markC: Int = -1

    override fun next(): Char {
        if (eof()) croak("EOF")
        val char = array[pointer++]

        if (char == '\n') {
            row++
            col = 1
        } else {
            col++
        }

        return char
    }

    override fun peek(): Char {
        if (eof()) croak("EOF")
        return array[pointer]
    }

    override fun eof(): Boolean {
        return pointer == size
    }

    override fun croak(message: String): Nothing {
        throw IllegalStateException("[$row:$col] $message")
    }

    fun mark() {
        markP = pointer
        markR = row
        markC = col
    }

    fun reset() {
        if (markP == -1) croak("Cannot reset when not marked")
        pointer = markP
        row = markR
        col = markC
    }

    /**
     * Return this::count if not found
     */
    fun nextIndexOf(char: Char, from: Int = pointer): Int {
        var index = from
        while (index < size && array[index] != char) index++
        return index
    }

    /**
     * Return this::count if not found
     */
    fun nextIndexOf(string: String, from: Int = pointer): Int {
        val charArray = string.toCharArray()
        val count = charArray.size
        val head = charArray[0]
        var index = from

        outer@ while (index < size) {
            index = nextIndexOf(head, index)
            if (index + count > size) return size

            var i = -1
            while (++i < count) {
                if (array[index + i] != charArray[i]) {
                    index += count
                    continue@outer
                }
            }
            return index
        }

        return size
    }

}