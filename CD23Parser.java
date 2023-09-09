///////////////////////////////////////////////////////////////////////////////

// Title:           CD23 Parser
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class is the main class of the parser. It calls the scanner to extract the list of tokens. It calls the first method (Start Symbol) of the parse tree

///////////////////////////////////////////////////////////////////////////////

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Hashmap methods
//  hashMap.put(key, object)
//  SymbolForTable currentSymbolInfo = hashMap.get(key)
//  hashMap.remove(key)
//  boolean containsKey = hashMap.containsKey(key)
//  hashMap.keySet()

public class CD23Parser {
    private static CD23Scanner scanner = new CD23Scanner();
    private static String[] scannerArgs = {"C:/Users/amore/Documents/ETSIT-UON/University of Newcastle/COMP6290-Compiler_Design/Assignmets/Assignment2/CD23Example.txt"};
    private static ArrayList<Token> tokenList;
    private static NonTerminalMethods nonTerminalMethods = new NonTerminalMethods();
    private static Map<String, SymbolForTable> symbolTable = new HashMap<>();

    public static void main (String[] args) throws IOException{
        tokenList = scanner.main(scannerArgs);
        nonTerminalMethods.transferTokensToStack(tokenList);
        symbolTable = nonTerminalMethods.superMethod();

    }
}
