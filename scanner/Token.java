package scanner;
import scanner.TokenType;

public class Token {
    TokenType tokenType;
    int line;
    String lexeme;
    Object literal;

    public Token(TokenType tokenType, int line, String lexeme, Object literal) {
        this.tokenType = tokenType;
        this.line = line;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return "scanner.Token{" +
                "tokenType=" + tokenType +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                '}';
    }
}
