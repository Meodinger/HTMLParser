package ink.meodinger.htmlparser.type


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
open class HNode(type: String, attributes: Map<String, String> = emptyMap(), children: List<HNode> = emptyList()) {

    companion object {
        const val NODE_HEAD = "HTMLHead"
        const val NODE_BODY = "HTMLBody"
        const val NODE_META = "HTMLMeta"
        const val NODE_LINK = "HTMLLink"
        const val NODE_SCRIPT = "HTMLScript"
    }

    val nodeType: String = type
    open val attributes: MutableMap<String, String> = HashMap(attributes)
    open val children: MutableList<HNode> = ArrayList(children)

    val id: String get() = attributes.getOrElse("id") { "" }

    override fun toString(): String {
        return "HNode($nodeType, $attributes, $children)"
    }

}