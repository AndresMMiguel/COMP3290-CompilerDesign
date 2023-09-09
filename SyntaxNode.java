///////////////////////////////////////////////////////////////////////////////

// Title:           SyntaxNode
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class 

///////////////////////////////////////////////////////////////////////////////

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

    public void setMIddle(SyntaxNode middle){
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
