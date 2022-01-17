package ink.meodinger.htmlparser.type

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */
enum class HType(private val type: String) {
    HTML("HTML"),
    XHTML("XHTML");

    fun of(type: String): HType {
        return when(type.uppercase()) {
            HTML.type -> HTML
            XHTML.type -> XHTML
            else -> throw IllegalArgumentException("Invalid type")
        }
    }
}