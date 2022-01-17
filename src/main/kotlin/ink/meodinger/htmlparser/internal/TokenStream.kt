package ink.meodinger.htmlparser.internal

import ink.meodinger.htmlparser.parser.HToken
import ink.meodinger.htmlparser.parser.HTokenType


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Token Stream
 */
class TokenStream(private val stringStream: StringStream) {

    companion object {
        private val CommentHeads: CharArray = charArrayOf('!')
        private val StringQuotes: CharArray = charArrayOf('\"', '\'')
        private val Identifiers: CharArray = ArrayList<Char>().apply {
            add('_')
            add('-')
            add(':')
            addAll('a'..'z')
            addAll('A'..'Z')
            addAll('0'..'9')
        }.toCharArray()
        private val Whitespaces: CharArray = charArrayOf(' ', '\n', '\t')
        private val Symbols: CharArray = charArrayOf('=', '<', '>', '/')

        private fun isCommentStart(char: Char): Boolean = CommentHeads.contains(char)
        private fun isStringStart(char: Char): Boolean = StringQuotes.contains(char)
        private fun isIdentifierStart(char: Char): Boolean = Identifiers.contains(char)

        private fun isIdentifier(char: Char): Boolean = isIdentifierStart(char)
        private fun isWhitespace(char: Char): Boolean = Whitespaces.contains(char)
        private fun isSymbol(char: Char):Boolean = Symbols.contains(char)
    }

    /*
    |    type    |     value     |
    |------------|---------------|
    | symbol     |          =<>/ |
    | comment    |            !* |
    | string     |           "*" |
    | identifier |  a-zA-Z0-9_-: |
    | text       |             * |

    - skip whitespace;
    - if input.eof() returns true, end;
    - if input.peek() returns `"`, read a string;
    - if input.peek() returns a letter or `_` or `-`, read an identifier
    - if input.peek() returns a symbol, read a symbol;
    - if no matches, invoke input.croak() to throw an Error.
    */

    private var current: HToken? = null
    private var textMayOccur: Boolean = false

    fun next(): HToken {
        val token = current
        current = null
        return token ?: readNext()
    }
    fun peek(): HToken {
        return current ?: readNext().also { current = it }
    }
    fun eof(): Boolean {
        return stringStream.eof()
    }
    fun croak(message: String): Nothing {
        stringStream.croak(message)
    }

    private fun readNext(): HToken {
        readWhile(Companion::isWhitespace)
        if (stringStream.eof()) return HToken(HTokenType.EOF, "")

        val char = stringStream.peek()

        val token: HToken =
            if (isSymbol(char)) takeSymbol()
            else if (isCommentStart(char)) takeComment()
            else if (isStringStart(char)) takeString(char)
            else if (textMayOccur) {
                takeText()
            } else {
                if (isIdentifierStart(char)) takeIdentifier()
                else croak("Unknown char: `$char`")
            }

        if (token.value == "<") textMayOccur = false
        if (token.value == ">") textMayOccur = true

        return token
    }

    private fun readTo(endIndex: Int): String {
        val builder = StringBuilder()
        while (stringStream.pointer < endIndex) builder.append(stringStream.next())
        return builder.toString()
    }
    private fun readWhile(predictor: (Char) -> Boolean): String {
        val builder = StringBuilder()
        while (!stringStream.eof() && predictor(stringStream.peek())) builder.append(stringStream.next())
        return builder.toString()
    }
    private fun readUntil(end: Char): String {
        val builder = StringBuilder()
        while (!stringStream.eof()) {
            if (stringStream.peek() == end) {
                break
            } else {
                builder.append(stringStream.next())
            }
        }
        return builder.toString()
    }
    private fun readString(quote: Char): String {
        var transiting = false
        val builder = StringBuilder()

        while (!stringStream.eof()) {
            val char = stringStream.next()
            if (transiting) {
                builder.append(char)
                transiting = false
            } else if (char == '\\') {
                builder.append('\\')
                transiting = true
            } else if (char == quote) {
                break
            } else {
                builder.append(char)
            }
        }

        return builder.toString()
    }

    private fun takeSymbol(): HToken {
        return HToken(
            HTokenType.SYMBOL,
            stringStream.next().toString()
        )
    }
    private fun takeComment(): HToken {
        return HToken(
            HTokenType.COMMENT,
            readUntil('>')
        )
    }
    private fun takeString(quote: Char): HToken {
        stringStream.next()
        return HToken(
            HTokenType.STRING,
            readString(quote)
        )
    }
    private fun takeIdentifier(): HToken {
        return HToken(
            HTokenType.IDENTIFIER,
            readWhile(Companion::isIdentifier)
        )
    }
    private fun takeText(): HToken {
        val builder = StringBuilder()

        while (true) {
            // NOTE: Find a better way
            val index0 = stringStream.nextIndexOf("</")
            val index1 = stringStream.nextIndexOf('\"')
            val index2 = stringStream.nextIndexOf('\'')

            val index = index0.coerceAtMost(index1).coerceAtMost(index2)
            if (index != index0) {
                val quote = if (index == index1) '\"' else '\''

                val text = readTo(index)
                val string = takeString(quote).value // Take will update stream
                builder.append(text).append(quote).append(string).append(quote)
            } else {
                builder.append(readTo(index))
                break
            }
        }

        return HToken(
            HTokenType.TEXT,
            builder.toString()
        )
    }

}
