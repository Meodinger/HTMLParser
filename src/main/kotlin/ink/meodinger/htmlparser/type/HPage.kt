package ink.meodinger.htmlparser.type

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

class HPage(val type: HType = HType.HTML) : HNode() {

    override val nodeType: String = "HTMLPage"
    override val children: MutableList<HNode> = ArrayList()
    override val attributes: MutableMap<String, String> = HashMap()

    val lang: String get() = attributes.getOrElse("lang") { "en" }
}