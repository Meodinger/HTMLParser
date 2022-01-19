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
    data class Token(val type: TokenType, val value: String) {
        fun isEOF(): Boolean = type == TokenType.EOF
        fun isText(): Boolean = type == TokenType.TEXT
        fun isSymbol(): Boolean = type == TokenType.SYMBOL
        fun isString(): Boolean = type == TokenType.STRING
        fun isComment(): Boolean = type == TokenType.COMMENT
        fun isIdentifier(): Boolean = type == TokenType.IDENTIFIER

        fun isAttributeAssign(): Boolean = isSymbol() && (value == "=")
        fun isSymbolSlash(): Boolean = isSymbol() && (value == "/")
        fun isTagStart(): Boolean = isSymbol() && (value == "<")
        fun isTagEnd(): Boolean = isSymbol() && (value == ">")
    }

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
        private val Whitespaces: CharArray = charArrayOf(' ', '\n', '\t', '\r')
        private val Symbols: CharArray = charArrayOf('=', '<', '>', '/')

        private fun isCommentStart(char: Char): Boolean = CommentHeads.contains(char)
        private fun isStringStart(char: Char): Boolean = StringQuotes.contains(char)
        private fun isIdentifier(char: Char): Boolean = Identifiers.contains(char)
        private fun isWhitespace(char: Char): Boolean = Whitespaces.contains(char)
        private fun isSymbol(char: Char):Boolean = Symbols.contains(char)
    }

    /*
    |    type    |     value     |
    |------------|---------------|
    | symbol     |          =<>/ |
    | comment    |            !* |
    | string     |        '*'"*" |
    | identifier |  a-zA-Z0-9_-: |
    | text       |             * |
    */

    private var current: Token? = null
    private var textMayOccur: Boolean = false
    private var inScript: Boolean = false
    private var inStyle: Boolean = false

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
            if (textMayOccur && (inScript && (char == '/' || char == '!' || isStringStart(char)))) {
                // In case a script block occurred with //comment or !any or "string" at first
                takeText()
            } else if (isSymbol(char)) {
                if (char == '<') textMayOccur = false
                if (char == '>') textMayOccur = true
                takeSymbol()
            } else if (isCommentStart(char)) {
                takeComment()
            } else if (isStringStart(char)) {
                takeString(char)
            } else if (textMayOccur) {
                takeText()
            } else if (isIdentifier(char)) {
                takeIdentifier()
            } else croak("Unknown char: `$char`")


        if (token.type == TokenType.IDENTIFIER && token.value == "script" && stringStream.peek() == '>') inScript = true
        else if (inScript && token.type == TokenType.SYMBOL && token.value == "/") inScript = false

        if (token.type == TokenType.IDENTIFIER && token.value == "style" && stringStream.peek() != '=') inStyle = true
        else if (inStyle && token.type == TokenType.SYMBOL && token.value == "/") inStyle = false

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
                readWhile { it != '>' }
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
        val endMark = if (inScript) "</script>" else if (inStyle) "</style>" else "<"

        while (true) {
            // NOTE: Find a better way
            val index0 = stringStream.nextIndexOf(endMark)
            val index1 = stringStream.nextIndexOf('\"')
            val index2 = stringStream.nextIndexOf('\'')

            val index = index0.coerceAtMost(index1).coerceAtMost(index2)
            if (index != index0) {
                val quote = if (index == index1) '\"' else '\''

                val text = readTo(index)
                val string = takeString(quote).value
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
