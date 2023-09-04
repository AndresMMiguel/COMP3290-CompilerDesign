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
    private static String[] scannerArgs = {"C:\\Users\\cswif\\Desktop\\compilerdesign\\scripts\\CD23ExampleAST.txt"};
    private static ArrayList<Token> tokenList;
    private static Token currentToken;
    private static Token lookAheadToken;
    private static Stack<Token> tokenStack;
    
    //transfers tokens from scanner to a stack for parsing
    private static void transferTokensToStack(){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

    //updates current token
    private static void updateCurrnetToken(){
        currentToken = tokenStack.pop();
    }

    //updates look ahead token
    private static void peekNextToken(){
        lookAheadToken = tokenStack.peek();
    }


    public static void main (String[] args) throws IOException{
        tokenList = scanner.main(scannerArgs);
        Map<String, SymbolForTable> symbolTable = new HashMap<>();
        transferTokensToStack();
        updateCurrnetToken();
        peekNextToken();

    }
}
