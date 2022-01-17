package ink.meodinger.htmlparser.type.node

import ink.meodinger.htmlparser.type.HNode

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
abstract class HHead : HNode() {

    override val nodeType: String = "HTMLHead"

    abstract val metas: List<HMeta>

}