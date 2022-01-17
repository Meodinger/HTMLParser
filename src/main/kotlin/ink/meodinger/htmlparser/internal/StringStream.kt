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

    private val array: CharArray = string.toCharArray()
    private val size = array.size

    private var pointer: Int = 0
    private var row: Int = 1
    private var col: Int = 1

    private var markP: Int = -1
    private var markR: Int = -1
    private var markC: Int = -1

    fun next(): Char {
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

    fun peek(): Char {
        if (eof()) croak("EOF")
        return array[pointer]
    }

    fun eof(): Boolean {
        return pointer == size
    }

    fun croak(message: String): Nothing {
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

}