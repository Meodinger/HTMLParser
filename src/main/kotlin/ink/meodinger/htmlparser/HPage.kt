package ink.meodinger.htmlparser


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * HTML Page, with html as HNode
 */
class HPage(
    val html: HNode,
    val type: HType = HType.HTML
) {

    enum class HType(private val type: String) {
        HTML("HTML"),
        XHTML("XHTML");

        companion object {
            fun of(type: String): HType = when(type.uppercase()) {
                HTML.type -> HTML
                XHTML.type -> XHTML
                else -> throw IllegalArgumentException("Invalid type")
            }
        }

    }

    val lang: String get() = html.attributes.getOrElse("lang") { "en" }

    val head: HNode get() = html.children[0]
    val body: HNode get() = html.children[1]

}