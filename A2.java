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
import java.util.Stack;


public class A2 {
    private static A1 scanner = new A1();
    private static String[] path = {"C:/Users/amore/Documents/ETSIT-UON/University of Newcastle/COMP6290-Compiler_Design/Assignmets/Assignment2/TestPrograms/Fibonacci.cd"};
    private static ArrayList<Token> tokenList;
    private static NonTerminalMethods nonTerminalMethods = new NonTerminalMethods();
    private static SyntaxNode root;
    private static Stack<SyntaxNode> nodeStack = new Stack<SyntaxNode>();
    private static Integer column = 0;

    private static void printNodes (SyntaxNode root){
        nodeStack.push(root);
        printElement(nodeStack);
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
        String output = nodeStack.lastElement().getNodeValue() + " ";
        while(output.length() % 7 != 0){
            output = output.concat(" ");
        }
        System.out.print(output);
        column++;
        if (column >= 10){
            column = 0;
            System.out.println();
        }
        if (!nodeStack.lastElement().getSymbolValue().equals("")){
            output = nodeStack.lastElement().getSymbolValue() + " ";
            while(output.length() % 7 != 0){
                output = output.concat(" ");
            }
        System.out.print(output);
        column++;
        if (column >= 10){
            column = 0;
            System.out.println();
        }
        }
        
    }


    public static void main (String[] args) throws IOException{
        if (args.length > 0)
            path = args;

        // Scanner
        scanner.main(path);
        tokenList = scanner.getTokenList();
        ArrayList<String> lexErrors = scanner.getLexErrors();
        if (lexErrors.size()>0){
            for(String error : lexErrors){
                System.out.println(error);                
            }
            System.exit(0);
        }

        // Parser
        nonTerminalMethods.transferTokensToStack(tokenList);
        root = nonTerminalMethods.superMethod();
        System.out.println();
        printNodes(root);
        System.out.println("\n\n");    
    }
}
