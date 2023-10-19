import java.util.ArrayList;

public class RootSymbolTable {

    private ArrayList<SymbolForTable> symbolTable = new ArrayList<SymbolForTable>();
    private SyntaxNode root;


    public void setSymbolTable(ArrayList<SymbolForTable> symbolTable){
        this.symbolTable = symbolTable;
    }

    public ArrayList<SymbolForTable> getSymbolTable(){
        return symbolTable;
    }

    public void setRoot(SyntaxNode root){
        this.root = root;
    }

    public SyntaxNode getRoot(){
        return root;
    }

}
