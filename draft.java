import java.io.IOException;
import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;
import java.util.Stack;

public class draft {

    private static CD23Scanner scanner = new CD23Scanner();
    private static String[] path = {"C:/Users/amore/Documents/ETSIT-UON/University of Newcastle/COMP6290-Compiler_Design/Assignmets/Assignment2/CD23Example.txt" };
    private static ArrayList<Token> tokenList;
    //private static NonTerminalMethods nonTerminalMethods = new NonTerminalMethods();
    //private static Map<String, SymbolForTable> symbolTable = new HashMap<>();
    private static Stack<Token> tokenStack = new Stack<Token>();


    public static void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

    public static void main (String[] args) throws IOException{
        tokenList = scanner.main(path);
        transferTokensToStack(tokenList);
        // System.out.println(tokenList);
        while(!tokenStack.isEmpty()){
            System.out.println(tokenStack.pop().getTokenEnum());
        }
    }
}
