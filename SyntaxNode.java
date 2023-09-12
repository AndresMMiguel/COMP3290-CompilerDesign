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
    private String nodeValue;
    private String symbolValue;
    private String symbolType;
    private SyntaxNode left;
    private SyntaxNode middle;
    private SyntaxNode right;

    public SyntaxNode(String nodeValue, String symbolValue, String type){
        this.nodeValue = nodeValue;
        this.symbolValue = symbolValue;
        this.symbolType = type;
        left = null;
        middle = null;
        right = null;
    }

    public void setLeft(SyntaxNode left){
        this.left = left;
    }

    public void setMiddle(SyntaxNode middle){
        this.middle = middle;
    }

    public void setRight(SyntaxNode right){
        this.right = right;
    }

    public String getNodeValue(){
        return this.nodeValue;
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
