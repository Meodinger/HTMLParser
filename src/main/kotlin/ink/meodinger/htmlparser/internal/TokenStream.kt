package ink.meodinger.htmlparser.internal


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Token Stream
 */
class TokenStream(private val stringStream: StringStream) : Stream<TokenStream.Token> {

    /**
     * Token Type
     */
    enum class TokenType { SYMBOL, STRING, COMMENT, IDENTIFIER, TEXT, EOF }

    /**
     * Token
     */
    data class Token(val type: TokenType, val value: String)

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

    private var current: Token? = null
    private var textMayOccur: Boolean = false
    private var inScript: Boolean = false

    private var marked: Boolean = false
    private var markCurrent: Token? = null
    private var markTextFlag: Boolean = false
    private var markScriptFlag: Boolean = false

    override fun next(): Token {
        val token = current
        current = null
        return token ?: takeNext()
    }
    override fun peek(): Token {
        return current ?: takeNext().also { current = it }
    }
    override fun eof(): Boolean {
        return stringStream.eof()
    }
    override fun croak(message: String): Nothing {
        stringStream.croak(message)
    }
    override fun mark() {
        stringStream.mark()
        markCurrent = current
        markTextFlag = textMayOccur
        markScriptFlag = inScript

        marked = true
    }
    override fun reset() {
        if (!marked) croak("Cannot reset when not marked")

        stringStream.reset()
        current = markCurrent
        textMayOccur = markTextFlag
        inScript = markScriptFlag
    }
    override fun unmarked() {
        stringStream.unmarked()
        marked = false
    }

    fun peek(num: Int) : Token {
        if (num <= 0) throw IllegalArgumentException("Should bigger than 0")
        if (num == 1) return peek()

        mark()
        for (i in 1 until num) next()
        val token = peek()
        reset()
        unmarked()

        return token
    }

    private fun takeNext(): Token {
        readWhile(Companion::isWhitespace)
        if (stringStream.eof()) return Token(TokenType.EOF, "")

        val char = stringStream.peek()

        val token: Token =
            if (isSymbol(char)) {
                if (textMayOccur && inScript && char == '/') {
                    // Script starts with comment
                    takeText()
                } else {
                    takeSymbol()
                }
            } else if (isCommentStart(char)) {
                takeComment()
            } else if (isStringStart(char)) {
                takeString(char)
            } else if (textMayOccur) {
                takeText()
            } else {
                if (isIdentifierStart(char)) takeIdentifier()
                else croak("Unknown char: `$char`")
            }

        if (token.value == "<") textMayOccur = false
        if (token.value == ">") textMayOccur = true

        if (token.type == TokenType.IDENTIFIER && token.value == "script") inScript = true
        if (inScript && token.type == TokenType.SYMBOL && token.value == "/") inScript = false

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

    private fun takeSymbol(): Token {
        return Token(
            TokenType.SYMBOL,
            stringStream.next().toString()
        )
    }
    private fun takeComment(): Token {
        stringStream.next()
        val isSimpleComment = stringStream.peek() == '-'

        if (isSimpleComment) {
            stringStream.next()
            stringStream.next()
            val content = readTo(stringStream.nextIndexOf("-->"))
            stringStream.next()
            stringStream.next()
            return Token(
                TokenType.COMMENT,
                "!--$content--"
            )
        } else {
            return Token(
                TokenType.COMMENT,
                readUntil('>')
            )
        }
    }
    private fun takeString(quote: Char): Token {
        stringStream.next()
        return Token(
            TokenType.STRING,
            readString(quote)
        )
    }
    private fun takeIdentifier(): Token {
        return Token(
            TokenType.IDENTIFIER,
            readWhile(Companion::isIdentifier)
        )
    }
    private fun takeText(): Token {
        val builder = StringBuilder()
        val endMark = if (inScript) "</" else "<"

        while (true) {
            // NOTE: Find a better way
            val index0 = stringStream.nextIndexOf(endMark)
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

        return Token(
            TokenType.TEXT,
            builder.toString()
        )
    }

}
