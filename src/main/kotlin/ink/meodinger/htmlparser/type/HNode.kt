package ink.meodinger.htmlparser.type


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
abstract class HNode {

    abstract val nodeType: String
    abstract val children: MutableList<HNode>
    abstract val attributes: MutableMap<String, String>

    val id: String get() = attributes.getOrElse("id") { "" }

}