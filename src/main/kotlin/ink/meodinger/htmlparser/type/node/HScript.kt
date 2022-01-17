package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
abstract class HScript : HNode() {

    override val nodeType: String = "HTMLScript"
    override val children: MutableList<HNode> = FreezableList<HNode>().apply { freeze() }

    val src: String get() = attributes.getOrElse("src") { "" }
    val type: String get() = attributes.getOrElse("type") { "" }

}