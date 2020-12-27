package scanner;

import Driver.Force;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static scanner.TokenType.*;

public class Scanner {
    private final String sourceCode; //source code of program
    private int line=1; //to check which line we are on
    private int start=0; //to mark the start of lexeme (for substring purpose)
    private int current=0; // will point to character being scanned in the whole code
    private List<Token> tokens=new ArrayList<>(); //store fot all the tokens in source code
    private static final Map<String, TokenType> keywords; //for reserved keywords

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public Scanner(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public List<Token> scanTokens(){
        //check if program any source code is yet to be scanned
        while(!isAtEnd()){
            //mark the start of lexeme equal to current character index being pointed
            start=current;
            //scan token starting from start and add it in list
            scanToken();
        }
        //add eof token at the end of list of tokens.
        tokens.add(new Token(EOF,line,"",null));
        return tokens;

    }

    private boolean isAtEnd(){
        //return false if the current value have reached higher or equal to total length of source code.
        return current>=sourceCode.length();
    }


    private void scanToken(){
        //get the next character (start of this lexeme)
        char c=advance();
        switch (c){
            //one char lexemes
            case '(':addToken(LEFT_PAREN);break;
            case ')':addToken(RIGHT_PAREN);break;
            case '{': addToken(LEFT_BRACE);break;
            case '}': addToken(RIGHT_BRACE);break;
            case ',':addToken(COMMA);break;
            case '.':addToken(DOT);break;
            case '-':addToken(MINUS);break;
            case '+':addToken(PLUS);break;
            case ';':addToken(SEMICOLON);break;
            case '*':addToken(STAR);break;
            //two character maybe lexemes
            case '!':addToken(match('=')?BANG_EQUAL:BANG);break;
            case '=':addToken(match('=')?EQUAL_EQUAL:EQUAL);break;
            case '>':addToken(match('=')?GREATER_EQUAL:GREATER);break;
            case '<':addToken(match('=')?LESS_EQUAL:LESS);break;
            //comment or slash lexeme
            case '/':{
                if(match('/')){
                    //its a comment so keep moving until its the end of program or a new line
                    while(peek()!='\n' && !isAtEnd()){
                        advance();
                    }

                }else{
                    //it is just a slash operator
                    addToken(SLASH);
                }
                break;
            }
            //whitespaces : ignore these
            case ' ':
            case '\t':
            case '\r':
                break;
            //new line: increment line counter
            case '\n': line++; break;
            //string lexeme
            case '"':string();break;
            default:
                if(isDigit(c)){
                    //digit lexeme
                    number();
                }else if(isAlpha(c)){
                    //it is an identifier or a keywork : choose by maximal munch
                    identifier();
                }else{
                    //koi pta nai
                    Force.error(line,"Unexpected character dude");break;
                }

        }
    }
    private void identifier(){
        //go ahead until it is an alphanumeric
        while (isAlphaNumeric(peek())) advance();
        //extract the lexeme
        String text=sourceCode.substring(start,current);
        //check if it is a keyword
        TokenType type=keywords.get(text);
        if(type==null)type=IDENTIFIER;
        addToken(type);
    }
    private void string(){
        //move until other " comes or program ends
        while(peek()!='"' && !isAtEnd()){
            if(peek()=='\n'){
                line++;
            }
            advance();
        }

        //if loop ends before program ends that means string havent terminated yet
        if(isAtEnd()){
            Force.error(line,"Unterminated string");
        }
        //add token string by removing " and " from it.
        addToken(STRING,sourceCode.substring(start+1,current-1));

    }
    private void number(){
        //go ahead until we get digits
        while(isDigit(peek()))advance();
        //if the current digit is . and next is some number
        if(peek()=='.' && isDigit(peekNext())){
            advance(); //skip .
            //go ahead until this ends
            while(isDigit(peek()))advance();
        }
        //add this as a double object
        addToken(NUMBER,Double.parseDouble(sourceCode.substring(start,current)));

    }
    private char advance(){
        //return the next character from current character and inc current
        current++;
        return sourceCode.charAt(current-1);
    }

    private void addToken(TokenType type){
        //add token with literal null if there is not literal
        addToken(type,null);
    }

    private void addToken(TokenType type,Object literal){
        //add token in the list of tokens
        //take substring (lexeme from start of lexeme to current char)
        String lexeme=sourceCode.substring(start,current);
        //add it in list
        tokens.add(new Token(type,line,lexeme,literal));
    }

    private boolean match(char expected){
        //conditional advance
        //if the expected char is current pointed char then inc current and return true otherwise false
        if(isAtEnd())return false;
        //get the pointed char
        char next=sourceCode.charAt(current);
        //if it is expected return true with inc current
        if(next==expected){
            current++;
            return true;
        }
        //return false if char didnt match
        return false;
    }


    private char peek(){
        //one char lookahead
        if(isAtEnd())return '\0';
        return sourceCode.charAt(current);
    }
    private boolean isDigit(char c){
        return c>='0' && c<='9';
    }

    private char peekNext(){
        //two chars lookahead
        if(current+1>=sourceCode.length()){
            return '\0';
        }

        return sourceCode.charAt(current+1);
    }

    private boolean isAlpha(char c){
        //check whether the char is a alpha char
        return (c>='a' && c<='z') || (c>='A' && c<='Z') || (c=='_');
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c)|| isDigit(c);
    }
}
