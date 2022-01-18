package ink.meodinger.htmlparser.parser

import ink.meodinger.htmlparser.internal.StringStream
import ink.meodinger.htmlparser.internal.TokenStream
import ink.meodinger.htmlparser.internal.TokenStream.Token
import ink.meodinger.htmlparser.internal.TokenStream.TokenType
import ink.meodinger.htmlparser.type.HNode
import ink.meodinger.htmlparser.type.HPage
import ink.meodinger.htmlparser.type.HText
import ink.meodinger.htmlparser.type.HType

/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Parser
 */
fun parse(htmlText: String): HPage {
    val tokenStream = TokenStream(StringStream(htmlText))

    fun Token.except(type: TokenType): Token {
        return also { if (it.type != type) tokenStream.croak("Except $type, got $it") }
    }
    fun Token.except(type: TokenType, value: String): Token {
        return also { if(except(type).value != value) tokenStream.croak("Except value `$value`, got `${it.value}`") }
    }

    fun parseNode(): HNode {
        // Read tag name
        tokenStream.next().except(TokenType.SYMBOL, "<")
        val name = tokenStream.next().except(TokenType.IDENTIFIER).value

        // Read attributes
        val attributes = HashMap<String, String>()
        while (!tokenStream.peek().isTagEnd()) {
            val attr   = tokenStream.next().except(TokenType.IDENTIFIER)
            tokenStream.next().except(TokenType.SYMBOL, "=")
            val value  = tokenStream.next().except(TokenType.STRING)
            attributes[attr.value] = value.value
        }

        // If end here, return
        if (tokenStream.peek().isNodeEnd()) return HNode(name, attributes)

        // End tag start
        tokenStream.next().except(TokenType.SYMBOL, ">")

        // Read children
        val children = ArrayList<HNode>()
        var nextToken: Token = tokenStream.peek()
        while (true) {
            if (nextToken.isNodeEnd()) break

            if (nextToken.isTagStart()) children.add(parseNode())
            else if (nextToken.isText()) children.add(HText(tokenStream.next().value))
            else tokenStream.croak("Unexpected token: $nextToken")

            nextToken = tokenStream.peek()
        }

        // End tag end
        tokenStream.next().except(TokenType.SYMBOL, "</")
        tokenStream.next().except(TokenType.IDENTIFIER, name)
        tokenStream.next().except(TokenType.SYMBOL, ">")

        return HNode(name, attributes, children)
    }

    // Read Document Type
    tokenStream.next().except(TokenType.SYMBOL, "<")
    val type = HType.of(tokenStream.next().except(TokenType.COMMENT).value.split(" ")[1])
    tokenStream.next().except(TokenType.SYMBOL, ">")

    return HPage(type).apply {
        children.add(parseNode()) // head
        children.add(parseNode()) // body
    }
}


private fun Token.isAttributeAssign(): Boolean = isSymbol() && (value == "=")
private fun Token.isTagStart(): Boolean = isSymbol() && (value == "<")
private fun Token.isTagEnd(): Boolean = isNodeEnd() || (value == ">")
private fun Token.isNodeEnd(): Boolean = isSymbol() && (value == "/>")

private fun Token.isText(): Boolean = type == TokenType.TEXT
private fun Token.isSymbol(): Boolean = type == TokenType.SYMBOL
private fun Token.isString(): Boolean = type == TokenType.STRING
private fun Token.isComment(): Boolean = type == TokenType.COMMENT
private fun Token.isIdentifier(): Boolean = type == TokenType.IDENTIFIER