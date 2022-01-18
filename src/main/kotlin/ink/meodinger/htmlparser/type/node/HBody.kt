package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
class HBody(
    attributes: Map<String, String> = emptyMap(),
    children: List<HNode> = emptyList()
) : HNode("HTMLBody", attributes, children) {

}