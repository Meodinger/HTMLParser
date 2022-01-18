package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/18
 * Have fun with my code!
 */
interface Stream<out E> {

    fun next(): E
    fun peek(): E

    fun eof(): Boolean

    fun croak(message: String): Nothing

    fun mark()
    fun reset()
    fun unmarked()

}