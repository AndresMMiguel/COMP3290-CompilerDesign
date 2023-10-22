///////////////////////////////////////////////////////////////////////////////

// Title:           Symbol Table
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This

///////////////////////////////////////////////////////////////////////////////


public class SymbolTableRecord {
    
    private static enum Type{
        PROGRAMNAME,
        INTEGER,
        REAL,
        BOOLEAN,
        STRUCTURE,
        FIELD,
        ARRAYDEF,
        ARRAY,
        FUNCTION,
        PARAMETERS
    }

    private Type type;
    private Symbol symbol;
    private Boolean isConstant;
    private String structid;
    private String functionid;
    private Integer arrayLenght;
    private String returnType;
    private String scope;

    public SymbolTableRecord(Symbol symbol){
        this.type = getEnum(symbol);
        this.symbol = symbol;
        this.isConstant = false;
    }
    
    private static Type getEnum(Symbol symbol){
        for(int i=0; i<10; i++){
            if (Type.values()[i].name().equals(symbol.getType())){
                return Type.values()[i];
            }
        }
        return Type.PROGRAMNAME;
    }

    public Type getType(){
        return this.type;
    }
    public Symbol getSymbol(){
        return this.symbol;
    }
    public String getStrutcId(){
        return this.structid;
    }
    public String getFunctionId(){
        return this.functionid;
    }
    public Integer getArrayLenght(){
        return this.arrayLenght;
    }
    public String getReturnType(){
        return this.returnType;
    }
    public String getScope(){
        return this.scope;
    }
    public Boolean isConstant(){
        return this.isConstant;
    }
    public void setType(String type){
        try{
            this.type = Type.valueOf(type);
        }catch(Exception e){
            this.type = Type.PROGRAMNAME;
        }
    }
    public void setConstant(Boolean isConstant){
        this.isConstant = isConstant;
    }
    public void setStructId(String structid){
        this.structid = structid;
    }
    public void setFunctionId(String functionid){
        this.functionid = functionid;
    }
    public void setArrayLenght(Integer arrayLenght){
        this.arrayLenght = arrayLenght;
    }
    public void setReturnType(String returnType){
        this.returnType = returnType;
    }
    public void setScope(String scope){
        this.scope = scope;
    }
}
