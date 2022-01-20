@file:Suppress("unused")

package ink.meodinger.htmlparser


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * HTML Page, with html as HNode
 */
class HPage(val html: HNode, val type: String = "!DOCTYPE HTML") {

    val lang: String get() = html.attributes.getOrElse("lang") { "en" }

    val head: HNode get() = html.children[0]
    val body: HNode get() = html.children[1]

    override fun toString(): String = "HPage($type, $html)"

}