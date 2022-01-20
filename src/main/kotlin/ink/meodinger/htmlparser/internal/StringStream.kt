package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * String Char Stream
 */
class StringStream(string: String) {

    val array: CharArray = string.toCharArray()
    val size = array.size
    var pointer: Int = 0
        private set

    private var row: Int = 1
    private var col: Int = 1

    private var marked: Boolean = false
    private var markP: Int = 0
    private var markR: Int = 0
    private var markC: Int = 0

    fun peek(): Char {
        if (eof()) croak("EOF")
        return array[pointer]
    }

    fun next(): Char {
        val char = peek()

        if (char == '\n') {
            row++
            col = 1
        } else {
            col++
        }
        pointer++

        return char
    }

    fun eof(): Boolean {
        return pointer == size
    }

    fun croak(message: String): Nothing {
        val char = array[pointer - 1]
        throw IllegalStateException("[$row:$col]('$char',${char.code}) $message")
    }

    fun mark() {
        markP = pointer
        markR = row
        markC = col

        marked = true
    }

    fun reset() {
        if (!marked) croak("Cannot reset when not marked")

        pointer = markP
        row = markR
        col = markC
    }

    fun unmarked() {
        marked = false
    }

    /**
     * Find the first index of the given char
     * @return this::size if not found
     */
    fun nextIndexOf(char: Char, from: Int = pointer): Int {
        var index = from
        while (index < size && array[index] != char) index++
        return index
    }
    /**
     * Find the first index of the given CharSequence
     * @return this::size if not found
     */
    fun nextIndexOf(sequence: CharSequence, from: Int = pointer): Int {
        val count = sequence.length
        val head = sequence[0]
        var index = from

        outer@ while (index < size) {
            index = nextIndexOf(head, index)
            if (index + count > size) return size

            var i = -1
            while (++i < count) {
                if (array[index + i] != sequence[i]) {
                    index += i
                    continue@outer
                }
            }
            return index
        }

        return size
    }

    override fun toString(): String = "[$row:$col](${array[pointer]})"

}