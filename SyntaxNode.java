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

    public SyntaxNode getLastNode (SyntaxNode temp){
        if (temp.getRight()!=null){
            temp = getLastNode(temp.getRight());
        }else if (temp.getMiddle()!=null){
            temp = getLastNode(temp.getMiddle());
        }else if (temp.getLeft()!=null){
            temp = getLastNode(temp.getLeft());
        }
        return temp;
    }

    public SyntaxNode getListLastNode(SyntaxNode temp, String value){
        if ((temp.getRight()!=null) && (temp.getRight().getNodeValue().equals(value))){
            temp = getListLastNode(temp.getRight(), value);
        }else if ((temp.getLeft()!=null) && (temp.getLeft().getNodeValue().equals(value))){
            temp = getListLastNode(temp.getLeft(), value);
        }
        return temp;
    }

    public SyntaxNode getBoolLastNode(SyntaxNode temp){
        if ((temp.getRight()!=null) && 
        (temp.getRight().getNodeValue().equals("NAND") || 
        temp.getRight().getNodeValue().equals("NOR") || 
        temp.getRight().getNodeValue().equals("NXOR"))){
            temp = getBoolLastNode(temp.getRight());
        }else if ((temp.getLeft()!=null) &&
        (temp.getRight().getNodeValue().equals("NAND") || 
        temp.getRight().getNodeValue().equals("NOR") || 
        temp.getRight().getNodeValue().equals("NXOR"))){
            temp = getBoolLastNode(temp.getLeft());
        }
        return temp;
    }

    public SyntaxNode getExprLastNode(SyntaxNode temp){
        if ((temp.getRight()!=null) && 
        (temp.getRight().getNodeValue().equals("NADD") ||
        temp.getRight().getNodeValue().equals("NSUB"))){
            temp = getBoolLastNode(temp.getRight());
        }else if ((temp.getLeft()!=null) &&
        (temp.getRight().getNodeValue().equals("NADD") || 
        temp.getRight().getNodeValue().equals("NSUB"))){
            temp = getBoolLastNode(temp.getLeft());
        }
        return temp;
    }

    public SyntaxNode getTermLastNode(SyntaxNode temp){
        if ((temp.getRight()!=null) && 
        (temp.getRight().getNodeValue().equals("NMUL") || 
        temp.getRight().getNodeValue().equals("NDIV") || 
        temp.getRight().getNodeValue().equals("NMOD"))){
            temp = getBoolLastNode(temp.getRight());
        }else if ((temp.getLeft()!=null) &&
        (temp.getRight().getNodeValue().equals("NMUL") || 
        temp.getRight().getNodeValue().equals("NDIV") || 
        temp.getRight().getNodeValue().equals("NMOD"))){
            temp = getBoolLastNode(temp.getLeft());
        }
        return temp;
    }

    public SyntaxNode copyChildren (SyntaxNode source, SyntaxNode dest){
        dest.setLeft(source.getLeft());
        dest.setMiddle(source.getMiddle());
        dest.setRight(source.getRight());
        return dest;
    }

    public String getSymbolValue(){
        return symbolValue;
    }

    public String getSymbolType(){
        return symbolType;
    }
}
