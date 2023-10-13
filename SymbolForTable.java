///////////////////////////////////////////////////////////////////////////////

// Title:           Symbol Table
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This

///////////////////////////////////////////////////////////////////////////////

import java.util.List;

public class SymbolForTable {
    //id[0] line[1] col[2] type[3] base[4]  offset[5]  glyph[6]
    private String id;
    private Integer line;
    private Integer col;
    private String type;
    private Integer base;
    private Integer offset;
    private String glyph;
    private List<Object> parameterList;

/* simple symbol table example

//preloaded keywords
id:integer   Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"integer" 
id:real      Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"real"
id:boolean   Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"boolean"
//user id's literals
id:Week7     Line:1  Col:6  Type:          Base:   Offset:   GLypth:"week7"
id:i         Line:4  Col:5  Type:integer   Base:   Offset:   GLypth:"i"
id:j         Line:4  Col:16 Type:integer   Base:   Offset:   GLypth:"j"
id:k         Line:4  Col:27 Type:integer   Base:   Offset:   GLypth:"k"
*/
    public SymbolForTable(String id, Integer line, Integer col, String type, String glyph){
        this.id = id;
        this.line = line;
        this.col = col;
        this.type = type;
        this.base = null;
        this.offset = null;
        this.glyph = glyph;    
    }

    public SymbolForTable(String id, Integer line, Integer col, String type, String glyph, List<Object> parameters){
        this.id = id;
        this.line = line;
        this.col = col;
        this.type = type;
        this.base = null;
        this.offset = null;
        this.glyph = glyph;
        this.parameterList = parameters;
    }

    public String getid(){
        return id;
    }
    public Integer getLine(){
        return line;
    }
    public Integer getCol(){
        return col;
    }
    public String getType(){
        return type;
    }
    public Integer getBase(){
        return base;
    }
    public Integer getOffset(){
        return offset;
    }
    public String getGlyph(){
        return glyph;
    }
    public void setBase(Integer base){
        this.base = base;
    }
    public void setOffset(Integer offset){
        this.offset = offset;
    }
    public void addParameter(Object parmeter){
        parameterList.add(parmeter);
    }
}
