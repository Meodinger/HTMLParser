package ink.meodinger.htmlparser.parser

import ink.meodinger.htmlparser.internal.StringStream
import ink.meodinger.htmlparser.internal.TokenStream
import java.io.BufferedReader


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Token Type
 */
enum class HTokenType { SYMBOL, STRING, COMMENT, IDENTIFIER, TEXT, EOF }

/**
 * Token
 */
data class HToken(val type: HTokenType, val value: String)

/**
 * Tokenizer
 */
fun tokenize(htmlText: String): List<HToken> {
    val list = ArrayList<HToken>()
    val tokenStream = TokenStream(StringStream(htmlText))
    while (!tokenStream.eof()) list.add(tokenStream.next())

    return list
}