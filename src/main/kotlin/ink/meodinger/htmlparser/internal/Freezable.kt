package ink.meodinger.htmlparser.internal

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
interface Freezable {

    var freezing: Boolean

    fun freeze() { freezing = true }
    fun unfreeze() { freezing = false }

}