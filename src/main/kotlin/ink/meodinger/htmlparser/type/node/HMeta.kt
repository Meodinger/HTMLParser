package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
abstract class HMeta : HNode() {

    override val nodeType: String = "HTMLMeta"
    override val children: MutableList<HNode> = FreezableList<HNode>().apply { freeze() }

}