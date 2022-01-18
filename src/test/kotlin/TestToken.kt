import ink.meodinger.htmlparser.internal.StringStream
import ink.meodinger.htmlparser.internal.TokenStream
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

    val list = ArrayList<TokenStream.Token>()
    val tokenStream = TokenStream(StringStream(connection.inputStream.reader(StandardCharsets.UTF_8).readText()))

    val startTime = Date().time
    while (!tokenStream.eof()) list.add(tokenStream.next())
    val endTime = Date().time

    println(list.size)
    println(endTime - startTime)
}