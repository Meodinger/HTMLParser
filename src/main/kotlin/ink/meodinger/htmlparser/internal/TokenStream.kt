package ink.meodinger.htmlparser.internal


/**
 * Author: Meodinger
 * Date: 2022/1/17
 * Have fun with my code!
 */

/**
 * Token Stream
 */
class TokenStream(private val stringStream: StringStream) {

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

        fun isSymbolAssign(): Boolean = isSymbol() && (value == "=")
        fun isSymbolSlash(): Boolean = isSymbol() && (value == "/")
        fun isSymbolStart(): Boolean = isSymbol() && (value == "<")
        fun isSymbolEnd(): Boolean = isSymbol() && (value == ">")
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
        private val Whitespaces: CharArray = charArrayOf(' ', '\n', '\t', '\r', '\uFEFF')
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
    private var mayText: Boolean = false
    private var inCode: Boolean = false

    private var marked: Boolean = false
    private var markCurrent: Token? = null
    private var markTextFlag: Boolean = false
    private var markCodeFlag: Boolean = false

    fun peek(): Token {
        return current ?: takeNext().also { current = it }
    }
    fun next(): Token {
        val token = current
        current = null
        return token ?: takeNext()
    }
    fun eof(): Boolean {
        return stringStream.eof()
    }
    fun croak(message: String): Nothing {
        stringStream.croak(message)
    }
    fun mark() {
        stringStream.mark()
        markCurrent = current
        markTextFlag = mayText
        markCodeFlag = inCode

        marked = true
    }
    fun reset() {
        if (!marked) croak("Cannot reset when not marked")

        stringStream.reset()
        current = markCurrent
        mayText = markTextFlag
        inCode = markCodeFlag
    }
    fun unmarked() {
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

    /**
     * StringStream.peek()
     * @return The char after the peeked token.
     */
    fun peekChar(): Char {
        return stringStream.peek()
    }

    private fun takeNext(): Token {
        readWhile(Companion::isWhitespace)
        if (eof()) return Token(TokenType.EOF, "")

        val char = stringStream.peek()

        val token: Token =
            if (mayText && char != '<') {
                takeText()
            } else if (isSymbol(char)) {
                if (char == '<') mayText = false
                if (char == '>') mayText = true
                takeSymbol()
            } else if (isCommentStart(char)) {
                takeComment()
            } else if (isStringStart(char)) {
                takeString(char)
            } else if (isIdentifier(char)) {
                takeIdentifier()
            } else croak("Unknown char: `$char`")


        if ((token.type == TokenType.IDENTIFIER) && ((token.value == "script" || token.value == "style") && stringStream.peek() == '>')) inCode = true
        else if (inCode && token.type == TokenType.SYMBOL && token.value == "/") inCode = false

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
        return Token(TokenType.SYMBOL, stringStream.next().toString())
    }
    private fun takeComment(): Token {
        stringStream.next()
        return if (stringStream.peek() == '-') {
            stringStream.next()
            stringStream.next()
            val content = readTo(stringStream.nextIndexOf("-->"))
            stringStream.next()
            stringStream.next()
            // Common comment
            Token(TokenType.COMMENT, "!--$content--")
        } else {
            // Document type, not '>' in this comment
            Token(TokenType.COMMENT, readWhile { it != '>' })
        }
    }
    private fun takeString(quote: Char): Token {
        stringStream.next()
        return Token(TokenType.STRING, readString(quote))
    }
    private fun takeIdentifier(): Token {
        return Token(TokenType.IDENTIFIER, readWhile(Companion::isIdentifier))
    }
    private fun takeText(): Token {
        return if (inCode) {
            val builder = StringBuilder()

            while (true) {
                /* NOTE: Find a better way
                 * We treat all <script> or <style> text as
                 * an array of plain text and string
                 */
                val index0 = stringStream.nextIndexOf("</")
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

            Token(TokenType.TEXT, builder.toString())
        } else {
            Token(TokenType.TEXT, readWhile { it != '<' })
        }
    }

}
