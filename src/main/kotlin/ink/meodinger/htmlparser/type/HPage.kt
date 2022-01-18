package ink.meodinger.htmlparser.type

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

class HPage(val type: HType = HType.HTML, ) : HNode("HTMLPage") {

    enum class HType(private val type: String) {
        HTML("HTML"),
        XHTML("XHTML");

        companion object {
            fun of(type: String): HType {
                return when(type.uppercase()) {
                    HTML.type -> HTML
                    XHTML.type -> XHTML
                    else -> throw IllegalArgumentException("Invalid type")
                }
            }
        }

    }

    val lang: String get() = attributes.getOrElse("lang") { "en" }

}