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
import java.util.Stack;

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
    private static SyntaxNode root;
    private static Stack<SyntaxNode> nodeStack = new Stack<SyntaxNode>();
    // private static Map<String, SymbolForTable> symbolTable = new HashMap<>();

    private static void printNodes (SyntaxNode root){
        nodeStack.push(root);
        System.out.println(root.getNodeValue() + " " + root.getSymbolValue());
        while(nodeStack.size() > 0){
            if(nodeStack.lastElement().getLeft() != null){
                nodeStack.push(nodeStack.lastElement().getLeft());
                printElement(nodeStack);
            }else if(nodeStack.lastElement().getMiddle() != null){
                nodeStack.push(nodeStack.lastElement().getMiddle());
                printElement(nodeStack);
            }else if(nodeStack.lastElement().getRight() != null){
                nodeStack.push(nodeStack.lastElement().getRight());
                printElement(nodeStack);
            }else if (nodeStack.size() > 1){
                if (nodeStack.elementAt(nodeStack.size()-2).getLeft() != null){
                    nodeStack.pop();
                    nodeStack.lastElement().setLeft(null);
                }else if (nodeStack.elementAt(nodeStack.size()-2).getMiddle() != null){
                    nodeStack.pop();
                    nodeStack.lastElement().setMiddle(null);
                }else if (nodeStack.elementAt(nodeStack.size()-2).getRight() != null){
                    nodeStack.pop();
                    nodeStack.lastElement().setRight(null);
                }
            }else{
                nodeStack.pop();
            }
        }
    }

    private static void printElement(Stack<SyntaxNode> nodeStack){
        System.out.print(nodeStack.lastElement().getNodeValue());
        if (nodeStack.lastElement().getSymbolValue() != null){
            System.out.println(" " + nodeStack.lastElement().getSymbolValue());
        }else{
            System.out.println();
        }
    }

    public static void main (String[] args) throws IOException{
        if (args.length > 0)
            scannerArgs = args;
        tokenList = scanner.main(scannerArgs);
        nonTerminalMethods.transferTokensToStack(tokenList);
        root = nonTerminalMethods.superMethod();
        printNodes(root);        
    }
}
