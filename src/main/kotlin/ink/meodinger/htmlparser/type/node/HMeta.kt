package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
class HMeta(
    attributes: Map<String, String> = emptyMap(),
) : HNode("HTMLMeta", attributes) {

    override val children: MutableList<HNode> = FreezableList(true)

}