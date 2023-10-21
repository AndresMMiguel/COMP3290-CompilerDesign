///////////////////////////////////////////////////////////////////////////////

// Title:           CD23SCANNER
// Files:           CD23Scanner.java, Token.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class contains the main program of the scanner for the language CD23.
//                  It manages the State Machine and the buffer to read source code and return tokens

///////////////////////////////////////////////////////////////////////////////

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class CD23Scanner {

    private static String path = "C:\\Users\\cswif\\Desktop\\compilerdesign\\scripts\\CD23ExampleAST.txt";
    private static String file;
    private static int charNum;
    private static int line;
    private static int column;
    private static boolean endOfFile;
    private static States currentState;
    private static ArrayList<Character> buffer;
    private static String[] lexItemArray = {",", ";", "[", "]", "(", ")", "=", "+", "-", "*", "/", "%", "^", "<", ">", ":", ".", "<=", ">=", "!=", "==", "+=", "-=", "*=", "/=", "!", "&&", "||", "&|", ">>","<<"};
    private static Token token;
    private static ArrayList<Token> tokenList;

    private enum States{
        START,
        IDENT,
        STRING,
        INT,
        FLOAT,
        ITEMS,
        SLCOMMENT,
        MLCOMMENT,
        ERROR
    }

    // Read the input code file, which is located in the variable: path
    private static String readFile(String path, Charset encoding) throws IOException{
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }

    // Take the next character of the file. Is useful because not every iteration of the main loop has to consume a character
    private static Character nextChar (){
        if(charNum < file.length()-1){
            Character nextChar = file.charAt(charNum);
            charNum++;
            return nextChar;
        }
        Character nextChar = file.charAt(charNum);
        endOfFile = true;
        return nextChar;
    }

    /**
     * Returns a string containing the lexeme of the token
     * stored in the buffer.
     * 
     * @param (Integer pos) Maximum index of the buffer to create the string
     * @return (String) Lexeme extracted from the buffer
     */
    private static String getLexem (Integer pos){
        String lexeme = "";
        for(int i = 0; i < pos; i++){
            lexeme += buffer.get(i);
        }
        return lexeme;
    }

    /**
     * Remove the elements of the buffer until its size is the desired one.
     * 
     * @param (Integer elements) Number of elements that will remain in the buffer
     */
    private static void clearBuffer (Integer elements){
        while(buffer.size()>elements){
            buffer.remove(0);
        }
    }


    public ArrayList<Token> main (String[] args) throws IOException{

        // To put the path of the file you want to compile in the command window this way: java CD23Scanner.java [path]
        if (args.length > 0)
            path = args[0];
        
        // Initializing the global variables
        file = readFile(path, StandardCharsets.UTF_8);
        charNum = 0;
        line = 1;
        column = 1;
        buffer = new ArrayList<>();
        endOfFile = false;
        currentState = States.START;
        List<String> lexItem = Arrays.asList(lexItemArray);
        token = new Token();
        tokenList = new ArrayList<Token>();

        /** Main loop for the STD:
         *  - START state: the initial/default one. We read the next character and decide what to do with it.
         *  - IDENT state: the buffer contains one initial letter. We continue reading till the end of the identifier and check if it is a keyword.
         *  - STRING state: the initial character in the buffer is ". We start a string token till the end of it. Careful with the newlines within the strings.
         *  - INT state: the initial character in the buffer is a digit. We start an integer token till the end of it or till a dot. We return the integer token or we change to the float state.
         *  - FLOAT state: the buffer contains a set of digits followed by a dot. Let's check if the following characters are digits or not. We return a float token or an integer token.
         *  - ITEM state: the initial character in the buffer is an operator. Let's check if there's a comment, a double operator or a single one and return the corresponding token.
         *  - COMMENT states: the STD has detected a comment. We ignore the following characters till the end of the comment.
         *  - ERROR state: there is some mistake in the code, let's report what type of error it is and stop analysing the file.
         */
        while(!endOfFile){
            switch(currentState){
                case START:
                    if(buffer.size()==0){
                        buffer.add(nextChar());
                    }
                    else if(buffer.get(0) == Character.toChars(9)[0]){
                        column += 4;
                        buffer.remove(0);
                    }   
                    else if(buffer.get(0) == ' '){ // if the character is whitespace, ignore it
                        column++;
                        buffer.remove(0);
                    }
                    else if(buffer.get(0) == Character.toChars(10)[0]){ // if the character is Line Feed, ignore it and restart column
                        column = 1;
                        buffer.remove(0);
                    } 
                    else if(buffer.get(0) == Character.toChars(13)[0]){ // if the character is Carriage Return, we start a new line
                        line++;
                        column = 1;
                        buffer.remove(0);
                    }
                    else if(Character.isLetter(buffer.get(0))){ // identifier or keyword
                        currentState = States.IDENT;
                    }
                    else if(buffer.get(0) == '"'){ //identifies the start of the string
                        currentState = States.STRING;
                    }
                    else if(Character.isDigit(buffer.get(0))){ // start building an integer
                        currentState = States.INT;
                    }
                    else if(lexItem.contains(getLexem(1)) || getLexem(1).equals("&") || getLexem(1).equals("|")){ // identify an item
                        currentState = States.ITEMS;
                    }else{
                        currentState = States.ERROR;
                    }
                break;

                case IDENT:
                    if(Character.isLetter(buffer.get(buffer.size()-1)) || Character.isDigit(buffer.get(buffer.size()-1))){
                        buffer.add(nextChar());
                        if(endOfFile){
                            column += buffer.size();
                            tokenList.add(token.returnIdentToken(getLexem(buffer.size()),line,column));
                        }
                    }
                    else{
                        column += buffer.size()-1;
                        tokenList.add(token.returnIdentToken(getLexem(buffer.size()-1),line,column)); // returns the corresponding token
                        clearBuffer(1);
                        currentState = States.START;
                    }
                break;

                case STRING:
                    buffer.add(nextChar());
                    if(endOfFile){
                        String error = "Lexical error Unterminated string: " + getLexem(buffer.size());
                        column += buffer.size();
                        tokenList.add(token.returnUndefinedToken(error,line,column));
                    }
                    else if(buffer.get(buffer.size()-1) == '"'){
                        column += buffer.size();
                        tokenList.add(token.returnStringToken(getLexem(buffer.size()),line,column));
                        buffer.clear();
                        currentState = States.START;
                    }
                    else if(buffer.get(buffer.size()-1) == Character.toChars(13)[0]){
                        String error = "Lexical error Unterminated string: " + getLexem(buffer.size()-1);
                        column += buffer.size()-1;
                        tokenList.add(token.returnUndefinedToken(error,line,column));
                        clearBuffer(1);
                        currentState = States.START;
                    }
                break;

                case INT:
                    if(Character.isDigit(buffer.get(buffer.size()-1))){
                        buffer.add(nextChar());
                    }
                    else if(buffer.get(buffer.size()-1) == '.'){
                        currentState = States.FLOAT;
                    }
                    else{   // try to cast the string of the integer to Long type and returning the token
                        try{
                            Long ilit = Long.parseLong(getLexem(buffer.size()-1));
                            column += buffer.size()-1;
                            tokenList.add(token.returnIntToken(ilit.toString(),line,column));
                            clearBuffer(1);
                            currentState = States.START;
                        }catch(Exception e){
                            String error = "Lexical error " + getLexem(buffer.size()-1) + " : Number overflow";
                            column += buffer.size()-1;
                            tokenList.add(token.returnUndefinedToken(error,line,column));
                            clearBuffer(1);
                            currentState = States.START;
                        }
                    }
                break;

                case FLOAT:
                    buffer.add(nextChar());
                    if(!Character.isDigit(buffer.get(buffer.size()-1))){    // if the next character is not a digit, the float has ended
                        if (Character.isDigit(buffer.get(buffer.size()-2))){    // if the character before is a digit, then it is a float. If not, it is an integer
                            try{
                                Double flit = Double.parseDouble(getLexem(buffer.size()-1));
                                column += buffer.size()-1;
                                tokenList.add(token.returnFloatToken(flit.toString(),line,column));
                                clearBuffer(1);
                                currentState = States.START;
                            }catch(Exception e){
                                String error = "Lexical error " + getLexem(buffer.size()-1) + " : Number overflow";
                                column += buffer.size()-1;
                                tokenList.add(token.returnUndefinedToken(error,line,column));
                                clearBuffer(1);
                                currentState = States.START;
                            }
                        }
                        else{
                            try{
                                Long ilit = Long.parseLong(getLexem(buffer.size()-2));
                                column += buffer.size()-2;
                                tokenList.add(token.returnIntToken(ilit.toString(),line,column));
                                clearBuffer(2);
                                currentState = States.START;
                            }catch(Exception e){
                                String error = "Lexical error " + getLexem(buffer.size()-2) + " : Number overflow";
                                column += buffer.size()-2;
                                tokenList.add(token.returnUndefinedToken(error,line,column));
                                clearBuffer(2);
                                currentState = States.START;
                            }
                        }
                    }
                break;

                case ITEMS:
                    while(buffer.size()<3){
                        buffer.add(nextChar());
                    }
                    if(getLexem(3).equals("/**")){  // check for Multi line comment
                        currentState = States.MLCOMMENT;
                        column += buffer.size();
                        buffer.clear();
                    }
                    else if(getLexem(3).equals("/--")){ // check for single line comment
                        currentState = States.SLCOMMENT;
                        buffer.clear();
                    }
                    else if(lexItem.contains(getLexem(2))){ // check for double operator
                        column += 2;
                        tokenList.add(token.returnItemToken(lexItem, getLexem(2),line,column));
                        clearBuffer(1);
                        currentState = States.START;  
                    }
                    else if(lexItem.contains(getLexem(1))){   // check for single operator
                        column ++;
                        tokenList.add(token.returnItemToken(lexItem,getLexem(1),line,column));
                        buffer.remove(0);
                        currentState = States.START;
                    }
                    else{    // taking & and | as lexical error
                        if(getLexem(1).equals("&") || getLexem(1).equals("|")){
                            String error = "Lexical error: " + buffer.get(0);
                            column++;
                            tokenList.add(token.returnUndefinedToken(error,line,column));
                            buffer.remove(0);
                            currentState = States.START;
                        }
                    }
                break;

                case SLCOMMENT:
                    if(nextChar() == Character.toChars(13)[0]){
                        buffer.add(file.charAt(charNum - 1));
                        currentState = States.START;
                    }
                break;

                case MLCOMMENT:
                    buffer.add(nextChar());
                    column++;
                    if(getLexem(buffer.size()-1).contains("**/")){
                        currentState = States.START;
                        clearBuffer(1);
                    }
                    else if(buffer.get(buffer.size()-1) == Character.toChars(13)[0]){
                        line++;
                        column = 1;
                        buffer.clear();
                    }
                    else if(buffer.get(buffer.size()-1) == Character.toChars(10)[0]){
                        column = 1;
                        buffer.clear();
                    }
                break;

                case ERROR:
                    String error = "Lexical error: " + buffer.get(0);
                    column++;
                    tokenList.add(token.returnUndefinedToken(error,line,column));
                    buffer.remove(0);
                    currentState = States.START;
                break;

                default:
                    System.out.println("ERROR: State not defined");
                break;

            }
        }

        tokenList.add(token.returnTEOF(line, column));
        printTokens();
        return tokenList;
    }

    // Function to print the tokens from the scanner as per spec in assignment 1
    private static void printTokens(){
        int printCounter = 0;
        Token undefinded = token.returnUndefinedToken("",line,column);
        Token stringLit = token.returnStringToken("",line,column);
        for(Token token:tokenList){
            //if(token.getLexeme().equals("")){
            if(token.getTokenEnum() == undefinded.getTokenEnum()){
                //System.out.print('\n');
                System.out.println(token.getTokenEnum());
                System.out.println(token.getLexeme());
                printCounter = 0;
            }
            else if(printCounter < 10){
                System.out.print(token.getTokenEnum()+" " + token.getLexeme()+" ");
                printCounter++;
            }
            else if(token.getTokenEnum() == stringLit.getTokenEnum()){
                System.out.println(token.getTokenEnum()+" "+ token.getLexeme()+'\n');
                printCounter = 0;
            }
            //else{
                //System.out.println( token.getTokenEnum() + token.getLexeme());
                //printCounter = 0;
            //}
            if(printCounter >= 10){
                printCounter = 0;
                System.out.print('\n');
            }
        }
        System.out.println("\n\n\nLexical Errors");
        System.out.println("=============================================================");
        for(Token token:tokenList){
            if(token.getLexeme().contains("Lexical error")){
                String message = "";
                for(int i = 14; i < token.getLexeme().length(); i++){
                    message+= token.getLexeme().charAt(i);
                }
                System.out.println("lexical error (" + token.getLineNumber() + "," + token.getColumnNumber() + ") : " + message);
            }
        }
    }
}