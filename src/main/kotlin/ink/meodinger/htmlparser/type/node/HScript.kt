package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
class HScript(
    attributes: Map<String, String> = emptyMap(),
    private val scriptText: String
) : HNode("HTMLScript", attributes) {

    override val children: MutableList<HNode> = FreezableList(true)

    val src: String get() = attributes.getOrElse("src") { "" }
    val type: String get() = attributes.getOrElse("type") { "" }

}