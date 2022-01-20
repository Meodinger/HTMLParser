import ink.meodinger.htmlparser.parse
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * Author: Meodinger
 * Date: 2022/1/18
 * Have fun with my code!
 */

fun main() {
    // val connection = URL("https://nekodict.com/words?q=%E8%8D%89").openConnection()
    // connection.connect()
    // val text = connection.inputStream.reader(StandardCharsets.UTF_8).readText()


    val connection = URL("http://www.fhdq.net/bd/44.html").openConnection()
    connection.connect()
    val text = connection.inputStream.reader(StandardCharsets.UTF_8).readText()
        .replace('\uFEFF', ' ')
        .replace("=http://www.fhdq.net", "=\"\"") // error
        .replace("<span>特殊符号</span>", "<span>\"</span>")


    // println(text)

    val startTime = Date().time
    val page = parse(text)
    val endTime = Date().time

    println(endTime - startTime)
    println(page)
}