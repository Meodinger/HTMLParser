package ink.meodinger.htmlparser


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * HTML node, all elements are nodes
 */
open class HNode(type: String, attributes: Map<String, String> = emptyMap(), children: List<HNode> = emptyList()) {

    companion object {
        val EOF: HNode = HNode("EOF")
    }

    class HText(val text: String) : HNode("PlainText") {
        override fun toString(): String = "PlainText(\"$text\")"
    }
    class HComment(val comment: String) : HNode("Comment") {
        override fun toString(): String = "Comment(\"$comment\")"
    }

    val nodeType: String = type
    open val attributes: MutableMap<String, String> = HashMap(attributes)
    open val children: MutableList<HNode> = ArrayList(children)

    override fun toString(): String = "HNode($nodeType, $attributes, $children)"

}