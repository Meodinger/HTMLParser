package ink.meodinger.htmlparser.type

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.internal.FreezableMap

/**
 * Author: Meodinger
 * Date: 2022/1/18
 * Have fun with my code!
 */
class HComment(val comment: String) : HNode("Comment") {

    override val attributes: MutableMap<String, String> = FreezableMap(true)
    override val children: MutableList<HNode> = FreezableList(true)

}