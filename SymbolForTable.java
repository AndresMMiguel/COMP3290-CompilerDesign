public class SymbolForTable {
    //id[0] line[1] col[2] type[3] base[4]  offset[5]  glyph[6]
    private String id;
    private Integer line;
    private Integer col;
    private String type;
    private String base;
    private Integer offset;
    private String glyph;

/* simple symbol table example

//preloaded keywords
id:integer   Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"integer" 
id:real      Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"real"
id:boolean   Line:   Col:   Type:Keyword   Base:   Offset:   Glyph:"boolean"
//user id's literals
id:Week7     Line:1  Col:6  Type:          Base:   Offset:   Glypth:"week7"
id:i         Line:4  Col:5  Type:integer   Base:   Offset:   Glypth:"i"
id:j         Line:4  Col:16 Type:integer   Base:   Offset:   Glypth:"j"
id:k         Line:4  Col:27 Type:integer   Base:   Offset:   Glypth:"k"
*/

//look into offset as the scope potentially????
    public SymbolForTable(String id, Integer line, Integer col, String type, String glyph){
        this.id = id;
        this.line = line;
        this.col = col;
        this.type = type;
        this.base = null;
        this.offset = null;
        this.glyph = glyph;    
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
    public String getBase(){
        return base;
    }
    public Integer getOffset(){
        return offset;
    }
    public String getGlyph(){
        return glyph;
    }

}