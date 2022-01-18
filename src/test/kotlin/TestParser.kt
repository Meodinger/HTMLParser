import ink.meodinger.htmlparser.parse
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection


/**
 * Author: Meodinger
 * Date: 2022/1/18
 * Have fun with my code!
 */

fun main() {
    val connection = URL("https://nekodict.com/words?q=%E8%8D%89").openConnection() as HttpsURLConnection
    connection.connect()
    val text = connection.inputStream.reader(StandardCharsets.UTF_8).readText()

    // println(text)

    val startTime = Date().time
    val page = parse(text)
    val endTime = Date().time

    println(endTime - startTime)

    val results = page.body.children[1].children[2]
    val first = results.children[0]
    println(first)
}