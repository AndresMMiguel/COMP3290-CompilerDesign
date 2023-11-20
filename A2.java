///////////////////////////////////////////////////////////////////////////////

// Title:           CD23 Parser
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class is the main class of the parser. It calls the scanner to extract the list of tokens. It calls the first method (Start Symbol) of the parse tree

///////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;


public class A2 {
    private static A1 scanner = new A1();
    private static String[] path = {"C:/Users/amore/Documents/ETSIT-UON/University of Newcastle/COMP6290-Compiler_Design/Assignmets/Assignment2/TestSuite/c.txt"};
    private static ArrayList<Token> tokenList;
    private static NonTerminalMethods nonTerminalMethods = new NonTerminalMethods();
    private static SyntaxNode root;
    private static Stack<SyntaxNode> nodeStack = new Stack<SyntaxNode>();
    private static Integer column = 0;

    private static void printNodes (SyntaxNode root, PrintWriter pw){
        nodeStack.push(root);
        printElement(nodeStack,pw);
        while(nodeStack.size() > 0){
            if(nodeStack.lastElement().getLeft() != null){
                nodeStack.push(nodeStack.lastElement().getLeft());
                printElement(nodeStack, pw);
            }else if(nodeStack.lastElement().getMiddle() != null){
                nodeStack.push(nodeStack.lastElement().getMiddle());
                printElement(nodeStack, pw);
            }else if(nodeStack.lastElement().getRight() != null){
                nodeStack.push(nodeStack.lastElement().getRight());
                printElement(nodeStack, pw);
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
    

    private static void printElement(Stack<SyntaxNode> nodeStack, PrintWriter pw){
        String output = nodeStack.lastElement().getNodeValue() + " ";
        while(output.length() % 7 != 0){
            output = output.concat(" ");
        }
        // System.out.print(output);
        pw.print(output);
        column++;
        if (column >= 10){
            column = 0;
            // System.out.println();
            pw.println();
        }
        if (!nodeStack.lastElement().getSymbolValue().equals("")){
            output = nodeStack.lastElement().getSymbolValue() + " ";
            while(output.length() % 7 != 0){
                output = output.concat(" ");
            }
        // System.out.print(output);
        pw.print(output);
        column++;
        if (column >= 10){
            column = 0;
            // System.out.println();
            pw.println();
        }
        }
        
    }


    public static void main (String[] args) throws IOException{
        if (args.length > 0){
            path = args;
        }            

        String filePath = path[0].replace("\\", "/");
        filePath = filePath.substring(filePath.lastIndexOf('/') + 1 , filePath.lastIndexOf('.')) + ".lst";
        FileWriter file = new FileWriter(filePath);
        PrintWriter pw = new PrintWriter(file);
        
        // Scanner
        scanner.main(path);
        tokenList = scanner.getTokenList();
        ArrayList<String> lexErrors = scanner.getLexErrors();
        if (lexErrors.size()>0){
            for(String error : lexErrors){
                System.out.println(error);
                pw.println(error);              
            }
            System.out.println("\nFound "+ lexErrors.size() + " errors");
            pw.println("\nFound "+ lexErrors.size() + " errors");
            pw.println("\n\nYour program should be fixed before continuing to the code generation");
            file.close();
            System.exit(0);
        }

        // Parser
        nonTerminalMethods.transferTokensToStack(tokenList);
        root = nonTerminalMethods.superMethod(filePath);
        ArrayList<String> syntaxErrors = nonTerminalMethods.getSyntaxErrors();
        ArrayList<String> semanticErrors = nonTerminalMethods.getSemanticErrors();        

        if (syntaxErrors.size()>0){
            for(String error : syntaxErrors){
                System.out.println(error);
                pw.println(error);
            }
            pw.println("\n\nYour program should be fixed before continuing to the code generation");         
        }else if(semanticErrors.size()>0){
            printNodes(root, pw);
            pw.println();
            for(String error : semanticErrors){
                System.out.println(error);
                pw.println(error);
            }
            System.out.println("\nFound "+ semanticErrors.size() + " errors");
            pw.println("\nFound "+ semanticErrors.size() + " errors");
            pw.println("\n\nYour program should be fixed before continuing to the code generation");
        }else{
        System.out.println("No errors were found");
        printNodes(root, pw);
        pw.println("\n\nYour program can continue to the code generation");
        }
        file.close();

        // Code generation
    }
}
