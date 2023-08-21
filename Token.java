///////////////////////////////////////////////////////////////////////////////

// Title:                   TOKEN
// Main Class File:         CD23Scanner.java
// File:                    Token.java
// Semester:                Semester 2 2023
//Course:                   COMP3290 COMPILER DESIGN
// Authors:                 Cameron Swift (c3445524)
//                          Andres Moreno Miguel (c3465977)
// Info:                    This class is for managing the token objects

///////////////////////////////////////////////////////////////////////////////

import java.util.Arrays;
import java.util.List;

public class Token {
    
    private enum Tokens{
        TTEOF,
        // keywords
        TCD23, TCONS, TTYPS, TTTIS, TARRS, TMAIN, TBEGN, TTEND, TARAY, 
        TTTOF, TFUNC, TVOID, TCNST, TINTG, TREAL, TBOOL, TTFOR, TREPT, 
        TUNTL, TIFTH, TELSE, TINPT, TOUTP, TOUTL,TRETN, TTRUE, TFALS,
        // operators/delimiters
        TCOMA, TSEMI, TLBRK, TRBRK, TLPAR, TRPAR, TEQUL,
        TPLUS, TMINS, TSTAR, TDIVD, TPERC, TCART, TLESS,
        TGRTR, TCOLN, TDOTT, TLEQL, TGEQL, TNEQL, TEQEQ,
        TPLEQ, TMNEQ, TSTEQ, TDVEQ, TNOTT, TTAND, TTTOR,
        TTXOR, TGRGR, TLSLS,
        // tuple value tokens
        TIDEN, TILIT, TFLIT, TSTRG, TUNDF
    }

    private static String[]keywordsArray = {"cd23", "constants", "types", "is", "arrays", "main", "begin", "end", "array", "of", "func", "void", "const", "integer", "real", "boolean", "for", "repeat", "until", "if", "else", "in", "out", "line", "return", "true", "false"};
    private Tokens token;
    private String lexeme;
    private int line;
    private int column;

    public Token(){
        this.token = Tokens.TUNDF;
        this.lexeme = "";
        this.line = 0;
        this.column = 0;
    }

    public Token(String lexeme, Integer line, Integer column){
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public Token(Tokens enumToken, Integer line, Integer column){
        this.token = enumToken;
        this.lexeme = "";
        this.line = line;
        this.column = column;
    }

    public Token(Tokens enumToken, String lexeme, Integer line, Integer column){
        this.token = enumToken;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public Tokens getTokenEnum(){
        return token;
    }

    public String getLexeme(){
        return lexeme;
    }

    public int getLineNumber(){
        return line;
    }

    public int getColumnNumber(){
        return column;
    }

    public Token returnIdentToken(String lexeme, Integer line, Integer column){
        List<String> keywords = Arrays.asList(keywordsArray);
        if (keywords.contains(lexeme.toLowerCase())){
            Token token = new Token(Tokens.values()[keywords.indexOf(lexeme.toLowerCase())+1], line, column);
            return token;
        }
        else{
            Token token = new Token(Tokens.TIDEN, lexeme, line, column);
            return token;
        }
    }

    public Token returnStringToken(String lexeme, Integer line, Integer column){
        Token token = new Token(Tokens.TSTRG, lexeme, line, column);
        return token;
    }

    public Token returnIntToken(String lexeme, Integer line, Integer column){
        Token token = new Token(Tokens.TILIT, lexeme, line, column);
        return token;
    }

    public Token returnFloatToken(String lexeme, Integer line, Integer column){
        Token token = new Token(Tokens.TFLIT, lexeme, line, column);
        return token;
    }

    public Token returnItemToken(List<String> lexItem, String lexeme, Integer line, Integer column){
        Token token = new Token(Tokens.values()[lexItem.indexOf(lexeme)+28], line, column);
        return token;
    }

    public Token returnUndefinedToken(String lexeme, Integer line, Integer column){
        Token token = new Token(Tokens.TUNDF, lexeme, line, column);
        return token;
    }

    public Token returnTEOF(Integer line, Integer column){
        Token token = new Token(Tokens.TTEOF,"", line, column);
        return token;
    }
}
