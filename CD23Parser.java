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
    private static String[] scannerArgs = {"C:\\Users\\cswif\\Desktop\\compilerdesign\\scripts\\assignment1\\a-2.txt"};
    private static ArrayList<Token> tokenList;
    private static NonTerminalMethods nonTerminalMethods = new NonTerminalMethods();
    private static Map<String, SymbolForTable> symbolTable = new HashMap<>();

    public static void main (String[] args) throws IOException{
        tokenList = scanner.main(scannerArgs);
        nonTerminalMethods.transferTokensToStack(tokenList);
        symbolTable = nonTerminalMethods.superMethod();

    }
}
