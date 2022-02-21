package ink.meodinger.htmlparser

import ink.meodinger.htmlparser.internal.StringStream
import ink.meodinger.htmlparser.internal.TokenStream
import ink.meodinger.htmlparser.internal.TokenStream.Token
import ink.meodinger.htmlparser.internal.TokenStream.TokenType


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Parser
 */
fun parse(htmlText: String): HPage {
    // Build a TokenStream
    val tokenStream = TokenStream(StringStream(htmlText))

    fun Token.except(type: TokenType, value: String? = null): Token {
        return also {
            if(it.type != type || (value != null && it.value != value))
                tokenStream.croak("Except $type:${value ?: '*'}, got ${it.type}:${it.value}")
        }
    }

    fun parseNode(): HNode {
        if (tokenStream.peek().isEOF()) return HNode.EOF

        // Start tag head
        tokenStream.next().except(TokenType.SYMBOL, "<")

        // Check if is comment
        if (tokenStream.peek().isComment()) {
            val comment = HNode.HComment(tokenStream.next().value)
            tokenStream.next().except(TokenType.SYMBOL, ">")
            return comment
        }

        // Read tag name
        val name = tokenStream.next().except(TokenType.IDENTIFIER).value
        val isSingleTag = SingleTagList.contains(name)

        var nextToken: Token

        // Read attributes
        val attributes = HashMap<String, String>()
        nextToken = tokenStream.peek()
        while (!nextToken.isSymbolEnd() && !(isSingleTag && nextToken.isSymbolSlash())) {
            val attr = tokenStream.next().except(TokenType.IDENTIFIER).value

            val next = tokenStream.peek()
            if (next.isSymbolAssign()) {
                tokenStream.next() // Take assignment symbol
                val valueToken = tokenStream.next()
                val value = when (valueToken.type) {
                    TokenType.STRING -> valueToken.value
                    TokenType.IDENTIFIER -> {
                        // ill-format
                        val builder = StringBuilder()
                        builder.append(valueToken.value)

                        // May ill-format like `type=text/javascript`
                        if (tokenStream.peek().isSymbolSlash() && tokenStream.peek(2).isIdentifier()) {
                            builder.append(tokenStream.next().value).append(tokenStream.next().value)
                        }

                        // I won't add more ill-format fix like href=http://xxx.yyy.

                        builder.toString()
                    }
                    else -> tokenStream.croak("Expected String or Identifier, got $valueToken")
                }
                attributes[attr] = value
            } else {
                // this attribute is a standalone attribute
                attributes[attr] = ""
            }
            nextToken = tokenStream.peek()
        }

        // Start tag end
        if (isSingleTag) {
            if (tokenStream.peek().isSymbolSlash()) {
                // If is SingleTag and actually has the slash, take it and return
                tokenStream.next()
                tokenStream.next().except(TokenType.SYMBOL, ">")
            } else {
                // Else may the SingleTag has its tail
                tokenStream.next().except(TokenType.SYMBOL, ">")

                tokenStream.mark()
                if (
                    tokenStream.next().isSymbolStart() &&
                    tokenStream.next().isSymbolSlash() &&
                    tokenStream.next().value == name &&
                    tokenStream.next().isSymbolEnd()
                ) {
                    // gotcha!
                } else {
                    // not the tail, just forget the slash
                    tokenStream.reset()
                }
                tokenStream.unmarked()
            }
            return HNode(name, attributes)
        } else {
            tokenStream.next().except(TokenType.SYMBOL, ">")
        }

        // Read children
        val children = ArrayList<HNode>()
        nextToken = tokenStream.peek()
        while (true) {
            if (nextToken.isSymbolStart()) {
                if (tokenStream.peekChar() == '/') {
                    break // The end tag
                } else {
                    children.add(parseNode())
                }
            } else if (nextToken.isText()) {
                children.add(HNode.HText(tokenStream.next().value))
            } else tokenStream.croak("Unexpected token: $nextToken")

            nextToken = tokenStream.peek()
        }

        // End tag
        tokenStream.next().except(TokenType.SYMBOL, "<")
        tokenStream.next().except(TokenType.SYMBOL, "/")
        tokenStream.next().except(TokenType.IDENTIFIER, name)
        tokenStream.next().except(TokenType.SYMBOL, ">")

        return HNode(name, attributes, children)
    }

    // Read Document Type
    tokenStream.next().except(TokenType.SYMBOL, "<")
    val type = tokenStream.next().except(TokenType.COMMENT).value
    tokenStream.next().except(TokenType.SYMBOL, ">")

    return HPage(parseNode(), type)
}

private val SingleTagList: Array<String> = arrayOf(
    "br", "hr", "img", "input", "link", "meta", "area", "base",
    "basefont", "param", "col", "frame", "embed", "keygen", "source",
    // Below are not documented but occurred tags
    "path"
)
