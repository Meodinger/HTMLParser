package ink.meodinger.htmlparser

import org.junit.Test
import java.nio.charset.StandardCharsets
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertIs


/**
 * Author: Meodinger
 * Date: 2022/1/18
 * Have fun with my code!
 */

class TestParser {

    private var text: String = ""

    @BeforeTest
    fun loadHtml() {
        text = TestParser::class.java.getResource("test-page.html")!!
            .openStream().bufferedReader(StandardCharsets.UTF_8).readText()
    }

    @Test
    fun testNoTrimComment() {
        val html = parse(text, false)

        assertEquals("DOCTYPE html", html.type)
        assertEquals("zh-cn", html.lang)

        // head
        assertEquals("title", html.head.children[1].nodeType)
        assertIs<HNode.HText>(html.head.children[1].children[0])
        assertEquals("script", html.head.children[3].nodeType)
        assertEquals("style", html.head.children[5].nodeType)

        // body
        assertEquals("h1", html.body.children[1].nodeType)
        assertEquals("title", html.body.children[1].attributes["class"])
        assertEquals("", html.body.children[1].attributes["hidden"])
        assertEquals("alert('foo');", html.body.children[2].attributes["onclick"])
    }

    @Test
    fun test() {
        val html = parse(text)

        assertEquals("DOCTYPE html", html.type)
        assertEquals("zh-cn", html.lang)

        // head
        assertEquals("title", html.head.children[0].nodeType)
        assertIs<HNode.HText>(html.head.children[0].children[0])
        assertEquals("script", html.head.children[1].nodeType)
        assertEquals("style", html.head.children[2].nodeType)

        // body
        assertEquals("h1", html.body.children[0].nodeType)
        assertEquals("title", html.body.children[0].attributes["class"])
        assertEquals("", html.body.children[0].attributes["hidden"])
        assertEquals("alert('foo');", html.body.children[1].attributes["onclick"])
    }

}