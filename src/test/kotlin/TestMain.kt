import ink.meodinger.htmlparser.parser.tokenize
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */


fun main() {
    val connection = URL("https://blog.csdn.net/TSY_1222/article/details/100536947").openConnection() as HttpsURLConnection
    connection.connect()

    val startTime = Date().time
    val list = tokenize(connection.inputStream.reader(StandardCharsets.UTF_8).readText())
    val endTime = Date().time
    println(endTime - startTime)
}