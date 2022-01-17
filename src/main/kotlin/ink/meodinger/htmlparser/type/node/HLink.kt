package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
abstract class HLink : HNode() {

    override val nodeType: String = "HTMLLink"
    override val children: MutableList<HNode> = FreezableList<HNode>().apply { freeze() }

}