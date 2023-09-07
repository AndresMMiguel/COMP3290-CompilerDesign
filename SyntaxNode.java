public class SyntaxNode {
    
    //one child: left only
    //two children: left & right
    private String symbolValue;
    private String symbolType;
    private SyntaxNode left;
    private SyntaxNode middle;
    private SyntaxNode right;

    public SyntaxNode(String value, String type){
        this.symbolValue = value;
        this.symbolType = type;
        left = null;
        middle = null;
        right = null;
    }

    public void setLeft(SyntaxNode right){
        this.right = right;
    }

    public void setMiddle(SyntaxNode middle){
        this.middle = middle;
    }

    public void setRight(SyntaxNode right){
        this.right = right;
    }

    public SyntaxNode getLeft(){
        return this.left;
    }

    public SyntaxNode getMiddle(){
        return this.middle;
    }

    public SyntaxNode getRight(){
        return this.right;
    }   

    public String getSymbolValue(){
        return symbolValue;
    }

    public String getSymbolType(){
        return symbolType;
    }
}
