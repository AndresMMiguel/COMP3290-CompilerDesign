import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class CD23Parser {
    private static CD23Scanner scanner = new CD23Scanner();
    private static String[] scannerArgs = {"C:\\Users\\cswif\\Desktop\\compilerdesign\\scripts\\CD23ExampleAST.txt"};
    private static ArrayList<Token> tokenList;
    private static Token currentToken;
    private static Token lookAheadToken;
    private static Stack<Token> tokenStack;
    
    private static void transferTokensToStack(){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

    private static void updateCurrnetToken(){
        currentToken = tokenStack.pop();
    }

    private static void peekNextToken(){
        lookAheadToken = tokenStack.peek();
    }


    public static void main (String[] args) throws IOException{
        tokenList = scanner.main(scannerArgs);
        transferTokensToStack();
        updateCurrnetToken();
        peekNextToken();

    }
}
