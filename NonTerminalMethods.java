///////////////////////////////////////////////////////////////////////////////

// Title:           Grammar Methods
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class contains all the recursive methods to build the parser tree and fill the symbol table

///////////////////////////////////////////////////////////////////////////////

import java.util.Stack;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class NonTerminalMethods {
    private static Token currentToken;
    private static Token lookAheadToken;
    private static Stack<Token> tokenStack = new Stack<Token>();
    private static HashMap<String, SymbolTableRecord> symbolTable = new HashMap<String, SymbolTableRecord>();
    private static SyntaxNode root;
    private static Boolean isBool = false;
    private static String tokenLexeme;
    private static Integer lineNumber;
    private static Integer colNumber;
    private ArrayList<String> semanticErrors = new ArrayList<String>();
    private ArrayList<String> syntaxErrors = new ArrayList<String>();
    private static String varType;
    private static boolean returnNeeded;
    private  String filePath = "";

    public ArrayList<String> getSyntaxErrors(){
        return this.syntaxErrors;
    }
    public ArrayList<String> getSemanticErrors(){
        return this.semanticErrors;
    }

    public void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
        lookAheadToken = tokenStack.peek();
    }

        //updates current token
        private static void updateTokens(){
            currentToken = tokenStack.pop();
            lookAheadToken = tokenStack.peek();
        }

        private void error(String err_message){
            try{
                FileWriter file = new FileWriter(filePath);
                PrintWriter pw = new PrintWriter(file);
                pw.println(err_message);
                file.close();
                System.exit(0);
            }catch(Exception e){
                System.out.println("Couldn't write the listing file in the compiler path");
            }
        }
        //updates current token if condition satisfied
        private void match(String token){
            if (lookAheadToken.getTokenEnumString().equals(token)){
                updateTokens();
            }
            else{
                String err_message = "Syntax error: Expected token " + token + " in line: " + currentToken.getLineNumber();
                System.out.println(err_message);
                syntaxErrors.add(err_message);
                error(err_message);
            }
        }

        // Idea for error recovery: pass the state from where the match comes, so we can burn tokens until the next block of code
        // private static void match(String token, String state){
        //     if (lookAheadToken.getTokenEnumString().equals(token)){
        //         updateTokens();
        //     }
        //     else{
        //         System.out.println("Error: Expected token " + token + " in line: " + currentToken.getLineNumber());
        //         switch (state){
        //             case "initlist":
        //             // Burn tokens until one of the tokens specified
        //                 token = burnTokens(["TTYPS", "TARRAYS", "TFUNC", "TMAIN"]);
        //                 switch(token){
        //                     case "TTYPS":
        //                         typelist();
        //                     break;
        //                 }
        //             break;
        //         }
        //     }
        // }


        // creates node child in the vacant position
        private static void createChild (SyntaxNode parent, SyntaxNode child){
            if (parent.getLeft() == null){
                parent.setLeft(child);
            }else if (parent.getRight() == null){
                parent.setRight(child);
            }else if (parent.getMiddle() == null){
                parent.setMiddle(parent.getRight());
                parent.setRight(child);
            }else{
                System.out.println("Error: Couldn't create a child since the parent node is full");
            }
        }

        private static void setSymbolInfo(){
            tokenLexeme = currentToken.getLexeme();
            lineNumber = currentToken.getLineNumber();
            colNumber = currentToken.getColumnNumber();
        }

    
        public SyntaxNode superMethod(String filePath){
            this.filePath = filePath;
            nprog();
            return root;
        }


    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private void nprog(){
        root = new SyntaxNode("NUNDF", null, null);
            match("TCD23");
            match("TIDEN");
            // CREATE NEW INPUT IN THE SYMBOL TABLE
            setSymbolInfo();
            Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber,"PROGRAMNAME");
            SymbolTableRecord STR = new SymbolTableRecord(symbol);
            symbolTable.put(tokenLexeme, STR);
            // Continue the program
            root = new SyntaxNode("NPROG", currentToken.getLexeme(), currentToken.getTokenEnumString());
            root = globals(root);
            root = funcs(root);
            root = mainbody(root);
    }
    
    //NGLOB <globals> ::= <consts> <types> <arrays>
    private SyntaxNode globals(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TCONS") ||           // if there are globals
        lookAheadToken.getTokenEnumString().equals("TTYPS") ||
        lookAheadToken.getTokenEnumString().equals("TARRS")){
                SyntaxNode globalNode = new SyntaxNode("NGLOB", lookAheadToken.getLexeme(), lookAheadToken.getTokenEnumString());
                globalNode = consts(globalNode);
                globalNode = types(globalNode);
                globalNode = arrays(globalNode);
                createChild(parent, globalNode);
        }
        return parent;
    }

    // Special <consts> ::= constants <initlist> | ε
    private SyntaxNode consts(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TCONS")){   // if there are constants
            match("TCONS");
            parent = initlist(parent);
        }
            return parent;
    }

    private SyntaxNode initlist(SyntaxNode parent){
        SyntaxNode initNode = new SyntaxNode("NILIST", "", "");
        SyntaxNode temp = init();
        createChild(initNode, temp);
        createChild(parent.getListLastNode(parent, "NILIST"), initNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA") ||
        !lookAheadToken.getTokenEnumString().equals("TTYPS") &&
        !lookAheadToken.getTokenEnumString().equals("TARRS") &&
        !lookAheadToken.getTokenEnumString().equals("TFUNC") &&
        !lookAheadToken.getTokenEnumString().equals("TMAIN")){
            match("TCOMA");
            initlist(parent);
        }
        return parent;
    }

    //NINIT <init> ::= <id> is <expr>
    private SyntaxNode init(){
        match("TIDEN");
        setSymbolInfo();
        // Create symbol
        Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber);
        SyntaxNode initNode = new SyntaxNode("NINIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TTTIS");
        initNode = const_lit(initNode);        
        switch(currentToken.getTokenEnumString()){
            case "TILIT":
                symbol.setType("INTEGER");
            break;

            case "TFLIT":
                symbol.setType("REAL");
            break;

            default:
                symbol.setType("BOOLEAN");
            break;
        }
        symbol.setValue(currentToken.getLexeme());
        SymbolTableRecord STR = new SymbolTableRecord(symbol);
        STR.setConstant(true);
        STR.setScope("global");
        symbolTable.put(tokenLexeme, STR);
        return initNode;
    }

    private SyntaxNode const_lit(SyntaxNode parent){
        SyntaxNode temp;
        switch (lookAheadToken.getTokenEnumString()){
            case "TILIT":
                match("TILIT");
                temp = new SyntaxNode("NILIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, temp);
            break;

            case "TFLIT":
                match("TFLIT");
                temp = new SyntaxNode("NFLIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, temp);
            break;

            case "TTRUE":
                match("TTRUE");
                temp = new SyntaxNode("NTRUE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, temp);
            break;

            case "TFALS":
                match("TFALS");
                temp = new SyntaxNode("NFALS", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, temp);
            break;

            default:
                String err_message = "Syntax error: Expected integer, real or boolean in line " + currentToken.getLineNumber();
                System.out.println(err_message);
                syntaxErrors.add(err_message);
                error(err_message);
            break;
        }
        return parent;
    }

    //Special <types> ::= types <typelist> | ε
    private SyntaxNode types(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TTYPS")){
            match("TTYPS");
            parent = typelist(parent);
        }
        return parent;
    }

    //Special <arrays> ::= arrays <arrdecls> | ε
    private SyntaxNode arrays(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TARRS")){
            match("TARRS");
            parent = arrdecls(parent);
        }
        return parent;
    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private SyntaxNode funcs(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TFUNC")){
            SyntaxNode temp = new SyntaxNode("NFUNCS","", "");
            SyntaxNode funcNode = func();
            createChild(temp, funcNode);
            createChild(parent.getListLastNode(parent, "NFUNCS"), temp);
            if (lookAheadToken.getTokenEnumString().equals("TFUNC")){
                funcs(parent);
            }
        }
        return parent;
    }

    //NMAIN <mainbody> ::= main <slist> begin <stats> end CD23 <id>
    private SyntaxNode mainbody(SyntaxNode parent){
         match("TMAIN");
         SyntaxNode mainNode = new SyntaxNode("NMAIN", currentToken.getLexeme(), currentToken.getTokenEnumString());
        mainNode = slist(mainNode);
        match("TBEGN");
        mainNode = stats(mainNode);
        match("TTEND");
        match("TCD23");
        match("TIDEN");
        try{
            if (!symbolTable.get(currentToken.getLexeme()).getSymbol().getid().equals(currentToken.getLexeme())){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                ", column " + currentToken.getColumnNumber() +
                " - the program name at the beggining and at the end must match";
                semanticErrors.add(err_message);
            }
        }catch(Exception e){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - the program name at the beggining and at the end must match";
            semanticErrors.add(err_message);
        }
        createChild(parent, mainNode);
        return parent;
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>
    private SyntaxNode slist(SyntaxNode parent){
        if (!lookAheadToken.getTokenEnumString().equals("TBEGN")){
            declPrefix();
            Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber);
            SyntaxNode sdeclNode = sdecl();
            switch(currentToken.getTokenEnumString()){
            case "TINTG":
                symbol.setType("INTEGER");
            break;

            case "TREAL":
                symbol.setType("REAL");
            break;

            case "TBOOL":
                symbol.setType("BOOLEAN");
            break;

            default:
            break;
            }
            SymbolTableRecord STR = new SymbolTableRecord(symbol);
            symbolTable.put(symbol.getid(), STR);
            SyntaxNode sdlistNode = new SyntaxNode("NSDLST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(sdlistNode, sdeclNode);
            createChild(parent.getListLastNode(parent, "NSDLST"), sdlistNode);
            if (!lookAheadToken.getTokenEnumString().equals("TBEGN")){
                match("TCOMA");
                slist(parent);
            }
        }else if (parent.getListLastNode(parent, "NSDLST") == parent ||
        currentToken.getTokenEnumString().equals("TCOMA")){
            String err_message = "Syntax error: It should be declarations in line " + lookAheadToken.getLineNumber();
            System.out.println(err_message);
            syntaxErrors.add(err_message);
            error(err_message);
        }
        
        return parent;
    }

    //NTYPEL <typelist> ::= <type> <typelist>
    private SyntaxNode typelist(SyntaxNode parent){
        SyntaxNode typeNode = type();
        SyntaxNode typeListNode = new SyntaxNode("NTYPEL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        createChild(typeListNode, typeNode);
        createChild(parent.getListLastNode(parent, "NTYPEL"), typeListNode);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")||
        !lookAheadToken.getTokenEnumString().equals("TARRS") &&
        !lookAheadToken.getTokenEnumString().equals("TFUNC") &&
        !lookAheadToken.getTokenEnumString().equals("TMAIN")){
            typelist(parent);
        }
        return parent;
    }

    private SyntaxNode type(){
        SyntaxNode typeNode = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            match("TIDEN");
            setSymbolInfo();
            match("TTTIS");
            if (lookAheadToken.getTokenEnumString().equals("TARAY")){
                // SYMBOL TABLE ENTRY
                Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber, "ARRAYDEF");
                SymbolTableRecord STR = new SymbolTableRecord(symbol);
                match("TARAY");
                typeNode = new SyntaxNode("NATYPE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                match("TLBRK");
                // STORE LENGHT IN SYMBOL TABLE ENTRY (How? If it is an expression like 1+3?)
                typeNode = expr(typeNode);
                switch(typeNode.getLeft().getNodeValue()){
                    case "NILIT":
                        STR.setArrayLenght(Integer.parseInt(typeNode.getLeft().getSymbolValue()));
                    break;

                    case "NSIMV":
                        try{
                            if(!symbolTable.get(typeNode.getLeft().getSymbolValue()).isConstant() ||
                            !symbolTable.get(typeNode.getLeft().getSymbolValue()).getSymbol().getType().equals("INTEGER")){
                                String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                                ", column " + currentToken.getColumnNumber() +
                                " - the length of the array must be known at compile time, so it should be an integer or a constant";
                                semanticErrors.add(err_message);
                            }else{
                                STR.setArrayLenght(Integer.parseInt(symbolTable.get(typeNode.getLeft().getSymbolValue()).getSymbol().getValue()));
                            }
                        }catch(Exception e){
                        }
                    break;

                    default:
                        String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                        ", column " + currentToken.getColumnNumber() +
                        " - the length of the array must be known at compile time, so it should be an integer or a constant";
                        semanticErrors.add(err_message);
                    break;
                }
                match("TRBRK");
                match("TTTOF");
                // STORE STRUCTID OF THE ARRAY IN SYMBOL TABLE ENTRY
                match("TIDEN");
                try{
                    if (symbolTable.get(currentToken.getLexeme()).getSymbol().getid().equals(currentToken.getLexeme())){
                        STR.setStructId(currentToken.getLexeme());
                    }else{
                        String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                        ", column " + currentToken.getColumnNumber() +
                        " - the type of the array elements should be declared previously";
                        semanticErrors.add(err_message);
                    }
                }catch(Exception e){
                    String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                        ", column " + currentToken.getColumnNumber() +
                        " - the type of the array elements should be declared previously";
                        semanticErrors.add(err_message);
                }
                symbolTable.put(symbol.getid(), STR);
                match("TTEND");
            }else{
                typeNode = new SyntaxNode("NRTYPE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                // SYMBOL TABLE ENTRY
                Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber, "STRUCTURE");
                SymbolTableRecord STR = new SymbolTableRecord(symbol);
                symbolTable.put(tokenLexeme, STR);
                // STORE FIELDS OF THE STRUCT IN THE SYMBOL TABLE ENTRY (done inside fields)
                typeNode = fields(typeNode, tokenLexeme);
                match("TTEND");
            }
        }
        return typeNode;
    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private SyntaxNode fields(SyntaxNode parent, String structid){
        // STORE THE FIELDS IN THE SYMBOL TABLE
        SyntaxNode sdlistNode = new SyntaxNode("NFLIST",  currentToken.getLexeme(), currentToken.getTokenEnumString());
        declPrefix();
        // Missing type of the field. ie integer, real, boolean
        Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber, "FIELD");
        SyntaxNode sdeclNode = sdecl();
        switch(currentToken.getTokenEnumString()){
            case "TINTG":
                symbol.setType("INTEGER");
            break;

            case "TREAL":
                symbol.setType("REAL");
            break;

            case "TBOOL":
                symbol.setType("BOOLEAN");
            break;

            default:
            break;
        }
        SymbolTableRecord STR = new SymbolTableRecord(symbol);
        STR.setStructId(structid);
        symbolTable.put(tokenLexeme, STR);
        createChild(sdlistNode, sdeclNode);
        createChild(parent.getListLastNode(parent, "NFLIST"), sdlistNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")||
        !lookAheadToken.getTokenEnumString().equals("TTEND")){
            match("TCOMA");
            fields(parent, structid);
        }
        return parent;
    }

    private void declPrefix (){
        match("TIDEN");
        // STORE THIS IDENTIFIER IN THE SYMBOL TABLE IN ITS RESPECTIVE SYMBOL TYPE
        setSymbolInfo();
        match("TCOLN");
    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private SyntaxNode sdecl(){
        stype();
        SyntaxNode temp = new SyntaxNode("NSDECL", tokenLexeme, "");
        return temp;
    }

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    private SyntaxNode arrdecls(SyntaxNode parent){
        declPrefix();
        // STORE THIS ARRAY IN THE SYMBOL TABLE
        Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber, "ARRAY");
        SymbolTableRecord STR = new SymbolTableRecord(symbol);
        SyntaxNode declNode = arrdecl();
        // Check if array type already declared
        try{
            if(symbolTable.get(tokenLexeme).getType().equals("ARRAYDEF")){
                symbol.setType(tokenLexeme);
            }else{
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the array type of the parameter '" + symbol.getid() + "' should be declared previously";
                semanticErrors.add(err_message);
            }
        }catch(Exception e){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the array type of the parameter '" + symbol.getid() + "' should be declared previously";
                semanticErrors.add(err_message);
        }
        
        // Store symbol in the symbol table
        symbolTable.put(symbol.getid(), STR);
        SyntaxNode listNode = new SyntaxNode("NALIST", "", "");
        createChild(listNode, declNode);
        createChild(parent.getListLastNode(parent, "NALIST"), listNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")||
        !lookAheadToken.getTokenEnumString().equals("TFUNC") &&
        !lookAheadToken.getTokenEnumString().equals("TMAIN")){
            match("TCOMA");
            arrdecls(parent);
        }
        return parent;
    }

    //NARRD <arrdecl> ::= <id> : <typeid>
    private SyntaxNode arrdecl(){
        match("TIDEN");
        SyntaxNode temp = new SyntaxNode("NARRD", tokenLexeme, "");
        setSymbolInfo();
        return temp;
    }

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>
    private SyntaxNode func(){
        match("TFUNC");
        match("TIDEN");
        // STORE THIS FUNCTION IDENTIFIER IN THE SYMBOL TABLE
        setSymbolInfo();
        String functionid = tokenLexeme;
        Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber, "FUNCTION");
        SymbolTableRecord STR = new SymbolTableRecord(symbol);
        symbolTable.put(functionid, STR);
        SyntaxNode funcNode = new SyntaxNode("NFUND", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        // STORE THE PARAMETERS IN THE SYMBOL TABLE ENTRY OF THE FUNCTION
        funcNode = plist(funcNode, functionid);
        match("TRPAR");
        match("TCOLN");
        // Semantic error: missing return type
        if(lookAheadToken.getTokenEnumString().equals("TIDEN") ||
        lookAheadToken.getTokenEnumString().equals("TMAIN") ||
        lookAheadToken.getTokenEnumString().equals("TBEGN") ||
        lookAheadToken.getTokenEnumString().equals("TTEND")){
            String err_message = "Syntax error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - the function " + functionid + " should have a return type";
            System.out.println(err_message);
            syntaxErrors.add(err_message);
            error(err_message);
        }
        // STORE THE RETURN TYPE IN THE SYMBOL TABLE ENTRY OF THE FUNCTION
        rtype();
        switch(currentToken.getTokenEnumString()){
            case "TINTG":
                STR.setReturnType("integer");
            break;

            case "TREAL":
                STR.setReturnType("real");
            break;

            case "TVOID":
                STR.setReturnType("void");
            break;

            default:
                STR.setReturnType("boolean");
            break;
        }
        funcNode = funcbody(funcNode, functionid);
        return funcNode;
    }

    //Special <rtype> ::= <stype> | void
    private void rtype(){
        // STORE THIS RETURN TYPE IN THE SYMBOL TABLE
        if (lookAheadToken.getTokenEnumString().equals("TVOID")){
            match("TVOID");
        }else{
            stype();
        }
    }

    //Special <plist> ::= <params> | ε
    private SyntaxNode plist(SyntaxNode parent, String functionid){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN") ||
        lookAheadToken.getTokenEnumString().equals("TCNST")){
            parent = params(parent, functionid, 0);
        }
        return parent;
    }

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>
    private SyntaxNode params(SyntaxNode parent, String functionid, Integer paramPosition){
        SyntaxNode paramNode = param(functionid, paramPosition);
        SyntaxNode paramListNode = new SyntaxNode("NPLIST","", "");
        createChild(paramListNode, paramNode);
        createChild(parent.getListLastNode(parent, "NPLIST"), paramListNode);
        symbolTable.get(functionid).setNumParams(paramPosition + 1);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA") ||
        !lookAheadToken.getTokenEnumString().equals("TRPAR")){
            match("TCOMA");
            paramPosition++;
            params(parent,functionid, paramPosition);
        }        
        return parent;
    }

    //NSIMP <param> ::= <sdecl>
    //NARRP <param> ::= <arrdecl>
    //NARRC <param> ::= const <arrdecl>
    private SyntaxNode param(String functionid, Integer paramPosition){
        SyntaxNode paramNode;
        if (lookAheadToken.getTokenEnumString().equals("TCNST")){       //NARRC <param> ::= const <arrdecl>
            match("TCNST");
            paramNode = new SyntaxNode("NARRC", currentToken.getLexeme(), currentToken.getTokenEnumString());
            declPrefix();
            // Create symbol
            Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber);
            SyntaxNode temp = arrdecl();
            try{
                if(symbolTable.get(tokenLexeme).getSymbol().getid().equals(tokenLexeme)){
                    symbol.setType(tokenLexeme);
                    SymbolTableRecord STR = new SymbolTableRecord(symbol);
                    STR.setFunctionId(functionid);
                    STR.setType("PARAMETER");
                    STR.setParamPosition(paramPosition);
                    STR.setConstant(true);
                    symbolTable.put(symbol.getid(), STR);
                }
            }catch(Exception e){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the array type of the parameter '" + symbol.getid() + "' should be declared previously";
                semanticErrors.add(err_message);
            }
            createChild(paramNode, temp);
        }else{
            declPrefix();
            // Create symbol
            Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber);
            if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
                paramNode = new SyntaxNode("NARRP", currentToken.getLexeme(), currentToken.getTokenEnumString());
                SyntaxNode temp = arrdecl();
                // STORE PARAMETER IN THE SYMBOL TABLE
                try{
                    if(symbolTable.get(tokenLexeme).getSymbol().getid().equals(tokenLexeme)){
                        symbol.setType(tokenLexeme);
                        SymbolTableRecord STR = new SymbolTableRecord(symbol);
                        STR.setFunctionId(functionid);
                        STR.setType("PARAMETER");
                        STR.setParamPosition(paramPosition);
                        symbolTable.put(symbol.getid(), STR);
                    }else{
                        String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                        ", column " + currentToken.getColumnNumber() +
                        " - the array type of the parameter '" + symbol.getid() + "' should be declared previously";
                        semanticErrors.add(err_message);
                    }
                    
                }catch(Exception e){
                    String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                    ", column " + currentToken.getColumnNumber() +
                    " - the array type of the parameter '" + symbol.getid() + "' should be declared previously";
                    semanticErrors.add(err_message);
                }
                createChild(paramNode, temp);
            }else{
                paramNode = new SyntaxNode("NSIMP", currentToken.getLexeme(), currentToken.getTokenEnumString());
                SyntaxNode temp = sdecl();
                switch(currentToken.getTokenEnumString()){
                    case "TINTG":
                        symbol.setType("INTEGER");
                    break;

                    case "TREAL":
                        symbol.setType("REAL");
                    break;

                    default:
                        symbol.setType("BOOLEAN");
                    break;                    
                }
                SymbolTableRecord STR = new SymbolTableRecord(symbol);
                STR.setFunctionId(functionid);
                STR.setType("PARAMETER");
                STR.setParamPosition(paramPosition);
                symbolTable.put(symbol.getid(), STR);
                createChild(paramNode, temp);
            }
        }
        return paramNode;
    }

    //Special <funcbody> ::= <locals> begin <stats> end
    private SyntaxNode funcbody (SyntaxNode parent, String functionid){
        parent = locals(parent, functionid);
        match("TBEGN");
        returnNeeded = true;
        parent = stats(parent);
        if (returnNeeded){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - the function " + functionid + " should have at least one return statement";
            semanticErrors.add(err_message);
        }
        match("TTEND");
        return parent;
    }

    //Special <locals> ::= <dlist> | ε
    private SyntaxNode locals(SyntaxNode parent, String functionid){
        if (!lookAheadToken.getTokenEnumString().equals("TBEGN")){
            parent = dlist(parent, functionid);
        }
        return parent;
    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>
    private SyntaxNode dlist(SyntaxNode parent, String functionid){
        SyntaxNode declNode = decl(functionid);
        SyntaxNode dlistNode = new SyntaxNode("NDLIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
        createChild(dlistNode, declNode);
        createChild(parent.getListLastNode(parent, "NDLIST"), dlistNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA") ||
        !lookAheadToken.getTokenEnumString().equals("TBEGN")){
            match("TCOMA");
            dlist(parent, functionid);
        }
        return parent;
    }

    //Special <decl> ::= <sdecl> | <arrdecl>
    private SyntaxNode decl(String functionid){
        declPrefix();
        Symbol symbol = new Symbol(tokenLexeme, lineNumber, colNumber);
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            temp = arrdecl();
            symbol.setType("ARRAY");
            SymbolTableRecord STR = new SymbolTableRecord(symbol);
            symbol.setType(tokenLexeme);
            STR.setFunctionId(functionid);
            symbolTable.put(symbol.getid(), STR);
        }else{
            temp = sdecl();
            switch(currentToken.getTokenEnumString()){
                case "TINTG":
                    symbol.setType("INTEGER");
                break;

                case "TREAL":
                    symbol.setType("REAL");
                break;

                default:
                    symbol.setType("BOOLEAN");
                break;                    
            }
            SymbolTableRecord STR = new SymbolTableRecord(symbol);
            STR.setFunctionId(functionid);
            symbolTable.put(symbol.getid(), STR);
        }
        return temp;
    }

    //Special <stype> ::= integer | real | boolean
    private void stype(){
        if (lookAheadToken.getTokenEnumString().equals("TINTG") ||
        lookAheadToken.getTokenEnumString().equals("TREAL") ||
        lookAheadToken.getTokenEnumString().equals("TBOOL")){
            updateTokens();
        }else{
            String err_message = "Syntax error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - expected an integer, real or boolean type";
            System.out.println(err_message);
            syntaxErrors.add(err_message);
            error(err_message);
        }
    }

    private SyntaxNode stats(SyntaxNode parent){
        // If statement
        if (!lookAheadToken.getTokenEnumString().equals("TTEND") &&
        !lookAheadToken.getTokenEnumString().equals("TUNTL") &&
        !lookAheadToken.getTokenEnumString().equals("TELSE")){
            SyntaxNode statsNode = new SyntaxNode("NSTATS", currentToken.getLexeme(), currentToken.getTokenEnumString());
            // If strstat
            if (lookAheadToken.getTokenEnumString().equals("TTFOR") ||
            lookAheadToken.getTokenEnumString().equals("TIFTH")){
                statsNode = strstat(statsNode);
            // If stat
            }else{
                statsNode = stat(statsNode);
                match("TSEMI");
            }
            createChild(parent.getListLastNode(parent, "NSTATS"), statsNode);
            stats(parent);
        }else if(parent.getListLastNode(parent, "NSTATS") == parent){
            String err_message = "Syntax error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - It should be statements in line " + lookAheadToken.getLineNumber();
            System.out.println(err_message);
            syntaxErrors.add(err_message);
            error(err_message);
        }
        return parent;
    }
    //Special <strstat> ::= <forstat> | <ifstat>
    private SyntaxNode strstat(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TTFOR")){
            parent = forstat(parent);
        }else{
            parent = ifstat(parent);
        }
        return parent;
    }

    //Special <stat> ::= <reptstat> | <asgnstat> | <iostat>
    //Special <stat> ::= <callstat> | <returnstat>
    private SyntaxNode stat(SyntaxNode parent){
        switch (lookAheadToken.getTokenEnumString()){
            case "TREPT":   //Special <stat> ::= <reptstat>
                parent = repstat(parent);
            break;

            case "TIDEN":
            match("TIDEN");
            setSymbolInfo();
                if (lookAheadToken.getTokenEnumString().equals("TLPAR")){   //Special <stat> ::= <callstat>
                    parent = callstat(parent);
                }else{      //Special <stat> ::= <asgnstat>
                    SyntaxNode asgnNode = asgnstat();
                    createChild(parent, asgnNode);
                }
            break;

            case "TINPT":   //Special <stat> ::= <iostat>
                parent = iostat(parent);
            break;

            case "TOUTP":   //Special <stat> ::= <iostat>
                parent = iostat(parent);
            break;

            case "TRETN":   //Special <stat> ::= <returnstat>
                parent = returnstat(parent);
            break;

            default:
            break;
        }
        return parent;
    }

    //NFORL <forstat> ::= for ( <asgnlist> ; <bool> ) <stats> end
    private SyntaxNode forstat(SyntaxNode parent){
        match("TTFOR");
        SyntaxNode forNode = new SyntaxNode("NFORL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        forNode = asgnlist(forNode);
        match("TSEMI");
        createChild(forNode, bool());
        match("TRPAR");
        forNode = stats(forNode);
        match("TTEND");
        createChild(parent, forNode);
        return parent;
    }

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>
    private SyntaxNode repstat(SyntaxNode parent){
        match("TREPT");
        SyntaxNode reptNode = new SyntaxNode("NREPT", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        reptNode = asgnlist(reptNode);
        match("TRPAR");
        reptNode = stats(reptNode);
        match("TUNTL");
        createChild(reptNode, bool());
        createChild(parent, reptNode);
        return parent;
    }

    //Special <asgnlist> ::= <alist> | ε
    private SyntaxNode asgnlist(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            parent = alist(parent);
        }
        return parent;
    }

    //NASGNS <alist> ::= <asgnstat> , <alist>
    //Special <alist> ::= <asgnstat>
    private SyntaxNode alist(SyntaxNode parent){
        match("TIDEN");
        // Check variable declared previously
        SyntaxNode asgnNode = asgnstat();
        SyntaxNode alistNode = new SyntaxNode("NASGNS", currentToken.getLexeme(), currentToken.getTokenEnumString());
        createChild(alistNode, asgnNode);
        createChild(parent.getListLastNode(parent, "NASGNS"), alistNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA") ||
        !lookAheadToken.getTokenEnumString().equals("TSEMI") &&
        !lookAheadToken.getTokenEnumString().equals("TRPAR")){
            match("TCOMA");
            alist(parent);
        }
        return parent;
    }

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end
    private SyntaxNode ifstat(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TIFTH")){
        match("TIFTH");
        SyntaxNode ifNode = new SyntaxNode("NIFTH", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        createChild(ifNode, bool());
        match("TRPAR");
        ifNode = stats(ifNode);
        if(lookAheadToken.getTokenEnumString().equals("TELSE")){
            match("TELSE");
            SyntaxNode elseNode = new SyntaxNode("NIFTE", currentToken.getLexeme(), currentToken.getTokenEnumString());
            elseNode.copyChildren(ifNode, elseNode);
            elseNode = stats(elseNode);
            createChild(parent, elseNode);
        }else{
            createChild(parent, ifNode);
        }
        match("TTEND");
        }
        return parent;
    }

    //Special <asgnstat> ::= <var> <asgnop> <bool>
    private SyntaxNode asgnstat(){
        SyntaxNode varNode = var();
        if (varType.equals("CONSTANT")){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - the constant " + currentToken.getLexeme() + " cannot be changed";
            semanticErrors.add(err_message);
        }
        SyntaxNode asgnNode = asgnop();
        createChild(asgnNode, varNode);
        createChild(asgnNode, bool());
        return asgnNode;
    }

    //NASGN <asgnop> ::= =
    //NPLEQ <asgnop> ::= +=
    //NMNEQ <asgnop> ::= -=
    //NSTEQ <asgnop> ::= *=
    //NDVEQ <asgnop> ::= /=
    private SyntaxNode asgnop(){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        switch (lookAheadToken.getTokenEnumString()){
            case "TEQUL":
                match("TEQUL");
                temp = new SyntaxNode("NASGN", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TPLEQ":
                match("TPLEQ");
                temp = new SyntaxNode("NPLEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TMNEQ":
                match("TMNEQ");
                temp = new SyntaxNode("NMNEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TSTEQ":
                match("TSTEQ");
                temp = new SyntaxNode("NSTEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TDVEQ":
                match("TDVEQ");
                temp = new SyntaxNode("NDVEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            default:
                String err_message = "Syntax error: line " + currentToken.getLineNumber() + 
                ", column " + currentToken.getColumnNumber() +
                " - expected a valid assign operator";
                System.out.println(err_message);
                syntaxErrors.add(err_message);
                error(err_message);
            break;
        }
        return temp;
    }

    //NINPUT <iostat> ::= In >> <vlist>
    //NOUTP <iostat> ::= Out << <prlist>
    //NOUTL <iostat> ::= Out << Line
    //NOUTL <iostat> ::= Out << <prlist> << Line
    private SyntaxNode iostat(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TINPT")){
            match("TINPT");
            SyntaxNode inpNode = new SyntaxNode("NINPUT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            match("TGRGR");
            inpNode = vlist(inpNode);
            createChild(parent, inpNode);
        }else{
            match("TOUTP");
            match("TLSLS");
            if (lookAheadToken.getTokenEnumString().equals("TOUTL")){
                SyntaxNode outLineNode = new SyntaxNode("NOUTL", currentToken.getLexeme(), currentToken.getTokenEnumString());
                match("TOUTL");
                createChild(parent, outLineNode);
            }else{
                SyntaxNode outputNode = new SyntaxNode("NOUTP", currentToken.getLexeme(), currentToken.getTokenEnumString());
                outputNode = prlist(outputNode);
                if (lookAheadToken.getTokenEnumString().equals("TLSLS")){
                    match("TLSLS");
                    match("TOUTL");
                    SyntaxNode outLineNode = new SyntaxNode("NOUTL", currentToken.getLexeme(), currentToken.getTokenEnumString());
                    outLineNode.copyChildren(outputNode, outLineNode);
                    createChild(parent, outLineNode);
                }else{
                    createChild(parent, outputNode);
                }
            }
        }
        return parent;
    }

    //NCALL <callstat> ::= <id> ( <elist> ) | <id> ()
    private SyntaxNode callstat(SyntaxNode parent){
        // Check if function exists, number of parameters and types of them
        String functionid = tokenLexeme;
        try{
            if (!symbolTable.get(currentToken.getLexeme()).getSymbol().getid().equals(currentToken.getLexeme())){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                ", column " + currentToken.getColumnNumber() +
                " - the function " + tokenLexeme + " should be declared previously";
                semanticErrors.add(err_message);
            }
        }catch(Exception e){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
            ", column " + currentToken.getColumnNumber() +
            " - the function '" + tokenLexeme + "' should be declared previously";
            semanticErrors.add(err_message);
        }
        SyntaxNode callNode = new SyntaxNode("NCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        Integer numParams = symbolTable.get(functionid).getNumParams();
        if (!lookAheadToken.getTokenEnumString().equals("TRPAR")){            
            callNode = elist(callNode, functionid, 0, numParams);
        }else if(numParams > 0){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
            ", column " + currentToken.getColumnNumber() +
            " - the function '" + functionid + "' should have " + numParams + " parameters";
            semanticErrors.add(err_message);
        }
        match("TRPAR");
        createChild(parent, callNode);
        return parent;
    }

    //NRETN <returnstat> ::= return void | return <expr>
    private SyntaxNode returnstat(SyntaxNode parent){
        returnNeeded = false;
        match("TRETN");
        SyntaxNode retNode = new SyntaxNode("NRETN", currentToken.getLexeme(), currentToken.getTokenEnumString());
        if(!lookAheadToken.getTokenEnumString().equals("TVOID")){
            retNode = expr(retNode);
            // STORE THIS RETURN TYPE IN THE SYMBOL TABLE
        }else{
            match("TVOID");
            // STORE VOID RETURN TYPE IN THE SYMBOL TABLE
        }
        createChild(parent, retNode);
        return parent;
    }

    //NVLIST <vlist> ::= <var> , <vlist>
    private SyntaxNode vlist(SyntaxNode parent){  
        match("TIDEN");      
        SyntaxNode temp = var();
        SyntaxNode vlistNode = new SyntaxNode("NVLIST", "", "");
        createChild(vlistNode, temp);
        createChild(parent.getListLastNode(parent, "NVLIST"), vlistNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");            
            vlist(parent);
        }
        return parent;
    }

    private SyntaxNode var(){        
        SyntaxNode varNode = new SyntaxNode("NSIMV", currentToken.getLexeme(), currentToken.getTokenEnumString());
        // Store variable type
        try{
            if(symbolTable.get(currentToken.getLexeme()).isConstant()){
                varType = "CONSTANT";
            }else{
                varType = symbolTable.get(currentToken.getLexeme()).getType();
            }
        }catch(Exception e){
            String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
            ", column " + currentToken.getColumnNumber() +
            " - the variable '" + currentToken.getLexeme() + "' should be declared previously";
            semanticErrors.add(err_message);
        }

        if (lookAheadToken.getTokenEnumString().equals("TLBRK")){
            match("TLBRK");
            SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
            temp = expr(temp);
            // Check if index is out of bound. Can I?
            match("TRBRK");
            if(lookAheadToken.getTokenEnumString().equals("TDOTT")){
                match("TDOTT");
                varNode =  new SyntaxNode("NARRV", currentToken.getLexeme(), currentToken.getTokenEnumString());
                varNode.copyChildren(temp, varNode);
                match("TIDEN");
                // Check if field is already declared previously and store its type in the variable type
                try{
                    varType = symbolTable.get(currentToken.getLexeme()).getSymbol().getType();            
                }catch(Exception e){
                    String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                    ", column " + currentToken.getColumnNumber() +
                    " - the field '" + currentToken.getLexeme() + "' should be declared previously in the types section";
                    semanticErrors.add(err_message);
                }
            }else{
                varNode = new SyntaxNode("NAELT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                varNode.copyChildren(temp, varNode);
            }
        }
        return varNode;
    }

    //NEXPL <elist> ::= <bool> , <elist>
    private SyntaxNode elist(SyntaxNode parent, String functionid, Integer paramPosition, Integer numParams){        
        SyntaxNode boolNode = bool();
        SyntaxNode temp = new SyntaxNode("NEXPL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        createChild(temp, boolNode);
        createChild(parent.getListLastNode(parent, "NEXPL"), temp);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")||
        !lookAheadToken.getTokenEnumString().equals("TRPAR")){
            match("TCOMA");
            paramPosition++;        
            elist(parent, functionid, paramPosition, numParams);
        }else{
            if (paramPosition != numParams -1){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the function '" + functionid + "' should have " + numParams + " parameters";
                semanticErrors.add(err_message);
            }
        }
        return parent;
    }

    //NBOOL <bool> ::= <bool> <logop> <rel>
    private SyntaxNode bool(){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        temp = bool2(temp);
        if (isBool){
            isBool = false;
            SyntaxNode boolNode = new SyntaxNode("NBOOL", "", "");
            boolNode.copyChildren(temp, boolNode);
            return boolNode;
        }else{
            return temp.getLeft();
        }
    }

    private SyntaxNode bool2(SyntaxNode parentNode){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        SyntaxNode relNode = rel(temp);
        if(lookAheadToken.getTokenEnumString().equals("TTAND") ||
        lookAheadToken.getTokenEnumString().equals("TTTOR") ||
        lookAheadToken.getTokenEnumString().equals("TTXOR")){
            isBool = true;
            SyntaxNode logopNode = logop();
            logopNode.copyChildren(relNode, logopNode);
            createChild(parentNode.getBoolLastNode(parentNode), logopNode);
            bool2(parentNode);
        }
        else{
            createChild(parentNode.getBoolLastNode(parentNode),relNode.getLeft());
        }
        return parentNode;
    }

    //NNOT <rel> ::= ! <expr> <relop> <expr>
    //Special <rel> ::= <expr><relop><expr>
    //Special <rel> ::= <expr>
    private SyntaxNode rel(SyntaxNode parent){
        SyntaxNode relNode = new SyntaxNode("NUNDF", null, null);
        if(lookAheadToken.getTokenEnumString().equals("TNOTT")){
            isBool = true;
            match("TNOTT");
            SyntaxNode notNode = new SyntaxNode("NNOT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            SyntaxNode exprTemp = expr(relNode);
            SyntaxNode relopNode = optRel();
            relopNode.copyChildren(exprTemp, relopNode);
            createChild(notNode, relopNode);
            relopNode = expr(relopNode);
            createChild(parent, notNode);
        }else{
            SyntaxNode exprTemp = expr(relNode);
            if(lookAheadToken.getTokenEnumString().equals("TEQEQ") ||   //Special <rel> ::= <expr> <relop><expr>
            lookAheadToken.getTokenEnumString().equals("TNEQL") ||
            lookAheadToken.getTokenEnumString().equals("TGRTR") ||
            lookAheadToken.getTokenEnumString().equals("TLESS") ||
            lookAheadToken.getTokenEnumString().equals("TLEQL") ||
            lookAheadToken.getTokenEnumString().equals("TGEQL")){
                isBool = true;
                SyntaxNode relopNode = optRel();
                relopNode.copyChildren(exprTemp, relopNode);
                relopNode = expr(relopNode);
                createChild(parent, relopNode); 
            }else{                                          //Special <rel> ::= <expr>
                parent = exprTemp;
            }
        }
        return parent;
    }

    private SyntaxNode optRel(){
        SyntaxNode optRelNode = new SyntaxNode("NUNDF", null, null);
        switch (lookAheadToken.getTokenEnumString()){
            case "TEQEQ":
                match("TEQEQ");
                optRelNode = new SyntaxNode("NEQL", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TNEQL":
                match("TNEQL");
                optRelNode = new SyntaxNode("NNEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TGRTR":
                match("TGRTR");
                optRelNode = new SyntaxNode("NGRT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TLESS":
                match("TLESS");
                optRelNode = new SyntaxNode("NLSS", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TLEQL":
                match("TLEQL");
                optRelNode = new SyntaxNode("NLEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TGEQL":
                match("TGEQL");
                optRelNode = new SyntaxNode("NGEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;
        }
        return optRelNode;
    }

    
    private SyntaxNode logop(){
        SyntaxNode logopNode = new SyntaxNode("NUNDF", null, null);
        switch(lookAheadToken.getTokenEnumString()){
            case "TTAND":   //NAND <logop> ::= &&
                match("TTAND");
                logopNode = new SyntaxNode("NAND", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TTTOR":   //NOR <logop> ::= ||
                match("TTTOR");
                logopNode = new SyntaxNode("NOR", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TTXOR":   //NXOR <logop> ::= &|
                match("TTXOR");
                logopNode = new SyntaxNode("NXOR", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

        }
        return logopNode;
    }

    // Special <expr> ::= <term> <expr'>
    private SyntaxNode expr(SyntaxNode parent){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        SyntaxNode termNode = term(temp);
        if (lookAheadToken.getTokenEnumString().equals("TPLUS")){   //NADD <expr'> ::= + <term> <expr'>
            match("TPLUS");
            SyntaxNode addNode = new SyntaxNode("NADD", currentToken.getLexeme(), currentToken.getTokenEnumString());
            addNode.copyChildren(termNode, addNode);
            createChild(parent.getExprLastNode(parent), addNode);
            expr(parent);
        }else if (lookAheadToken.getTokenEnumString().equals("TMINS")){  //NSUB <expr> ::= <expr> - <term>
            match("TMINS");
            SyntaxNode subNode = new SyntaxNode("NSUB", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(subNode, termNode);
            subNode.copyChildren(termNode, subNode);
            createChild(parent.getExprLastNode(parent), subNode);
            expr(parent);
        }else{
            createChild(parent.getExprLastNode(parent), termNode.getLeft());
        }
        return parent;
    }

    //Special <term> ::= <fact> <term'>
    private SyntaxNode term(SyntaxNode parent){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        SyntaxNode factNode = fact(temp);
        if (lookAheadToken.getTokenEnumString().equals("TSTAR")){   //NMUL <term> ::= <term> * <fact>
            match("TSTAR");
            SyntaxNode mulNode = new SyntaxNode("NMUL", currentToken.getLexeme(), currentToken.getTokenEnumString());
            mulNode.copyChildren(factNode, mulNode);
            createChild(parent.getTermLastNode(parent), mulNode);
            term(parent);
        }else if (lookAheadToken.getTokenEnumString().equals("TDIVD")){     //NDIV <term> ::= <term> / <fact>
            match("TDIVD");
            SyntaxNode divNode = new SyntaxNode("NDIV", currentToken.getLexeme(), currentToken.getTokenEnumString());
            divNode.copyChildren(factNode, divNode);
            createChild(parent.getTermLastNode(parent), divNode);
            term(parent);
        }else if (lookAheadToken.getTokenEnumString().equals("TPERC")){  //NMOD <term> ::= <term> % <fact>
            match("TPERC");
            SyntaxNode modNode = new SyntaxNode("NMOD", currentToken.getLexeme(), currentToken.getTokenEnumString());
            modNode.copyChildren(factNode, modNode);
            createChild(parent.getTermLastNode(parent), modNode);
            term(parent);
        }else{
            createChild(parent.getTermLastNode(parent), factNode.getLeft());
        }
        return parent;
    }

    //Special <fact> ::= <exponent>
    private SyntaxNode fact(SyntaxNode parent){
        SyntaxNode expNode = exponent();
        if(lookAheadToken.getTokenEnumString().equals("TCART")){    //NPOW <fact> ::= <fact> ^ <exponent>
            match("TCART");
            SyntaxNode powNode = new SyntaxNode("NPOW", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(powNode, expNode);
            createChild(parent.getListLastNode(parent, "NPOW"), powNode);
            fact(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NPOW"), expNode);
        }
        return parent;
    }

    private SyntaxNode exponent(){
        SyntaxNode expNode = new SyntaxNode("NUNDF", null, null);
        switch(lookAheadToken.getTokenEnumString()){
            case "TIDEN":   //Special <exponent> ::= <var> or Special <exponent> ::= <fncall>
                match("TIDEN");
                setSymbolInfo();
                if (lookAheadToken.getTokenEnumString().equals("TLPAR")){
                    expNode = fncall();
                }else{
                    expNode = var();
                }
            break;

            case "TILIT":   //NILIT <exponent> ::= <intlit>
                match("TILIT");
                expNode = new SyntaxNode("NILIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TFLIT":   //NFLIT <exponent> ::= <reallit>
                match("TFLIT");
                expNode = new SyntaxNode("NFLIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TTRUE":   //NTRUE <exponent> ::= true
                match("TTRUE");
                expNode = new SyntaxNode("NTRUE", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TFALS":   //NFALS <exponent> ::= false
                match("TFALS");
                expNode = new SyntaxNode("NFALS", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TLPAR":   //Special <exponent> ::= ( <bool> )
                match("TLPAR");
                expNode = bool();
                match("TRPAR");
            break;
        }
        return expNode;
    }

    //NFCALL <fncall> ::= <id> (<elist>) | <id> ()
    // private SyntaxNode fncall(){
    //     SyntaxNode fnNode = new SyntaxNode("NFCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //     match("TLPAR");
    //     if (!lookAheadToken.getTokenEnumString().equals("TRPAR")){
    //         fnNode = elist(fnNode);
    //     }
    //     match("TRPAR");
    //     return fnNode;
    // }
        private SyntaxNode fncall(){
            // Check if function exists, number of parameters and types of them
            String functionid = tokenLexeme;
            try{
                if (!symbolTable.get(currentToken.getLexeme()).getSymbol().getid().equals(currentToken.getLexeme())){
                    String err_message = "Semantic Error: line " + currentToken.getLineNumber() + 
                    ", column " + currentToken.getColumnNumber() +
                    " - the function " + tokenLexeme + " should be declared previously";
                    semanticErrors.add(err_message);
                }
            }catch(Exception e){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the function " + tokenLexeme + " should be declared previously";
                semanticErrors.add(err_message);
            }
            SyntaxNode fcallNode = new SyntaxNode("NFCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
            match("TLPAR");
            Integer numParams = symbolTable.get(functionid).getNumParams();
            if (!lookAheadToken.getTokenEnumString().equals("TRPAR")){            
                fcallNode = elist(fcallNode, functionid, 0, numParams);
            }else if(numParams > 0){
                String err_message = "Semantic Error: line " + currentToken.getLineNumber() +
                ", column " + currentToken.getColumnNumber() +
                " - the function " + functionid + " should have " + numParams + " parameters";
                semanticErrors.add(err_message);
            }
            match("TRPAR");
            return fcallNode;
        }

    //NPRLST <prlist> ::= <printitem> , <prlist>
    //Special <prlist> ::= <printitem>)
    private SyntaxNode prlist(SyntaxNode parent){
        SyntaxNode prlistNode = new SyntaxNode("NPRLST", currentToken.getLexeme(), currentToken.getTokenEnumString());
        prlistNode = printitem(prlistNode);
        createChild(parent.getListLastNode(parent, "NPRLST"), prlistNode);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")||
        !lookAheadToken.getTokenEnumString().equals("TSEMI") &&
        !lookAheadToken.getTokenEnumString().equals("TLSLS")){
            match("TCOMA");            
            prlist(parent);
        }
        return parent;
    }

    private SyntaxNode printitem(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TSTRG")){    //NSTRG <printitem> ::= <string>
            match("TSTRG");
            SyntaxNode strNode = new SyntaxNode("NSTRG", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(parent, strNode);
        }else{
            parent = expr(parent); //Special <printitem> ::= <expr>
        }
        return parent;
    }


}