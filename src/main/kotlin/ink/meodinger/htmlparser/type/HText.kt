package ink.meodinger.htmlparser.type

import ink.meodinger.htmlparser.internal.FreezableList
import ink.meodinger.htmlparser.internal.FreezableMap

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

class HText(val text: String) : HNode("PlainText") {

    override val attributes: MutableMap<String, String> = FreezableMap(true)
    override val children: MutableList<HNode> = FreezableList(true)

}