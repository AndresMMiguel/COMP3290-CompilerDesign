///////////////////////////////////////////////////////////////////////////////

// Title:           Grammar Methods
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class contains all the recursive methods to build the parser tree and fill the symbol table

///////////////////////////////////////////////////////////////////////////////

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;

//Hashmap methods
//  hashMap.put(key, object)
//  SymbolForTable currentSymbolInfo = hashMap.get(key)
//  hashMap.remove(key)
//  boolean containsKey = hashMap.containsKey(key)
//  hashMap.keySet()

public class NonTerminalMethods {
    private static Token currentToken;
    private static Token lookAheadToken;
    private static Stack<Token> tokenStack = new Stack<Token>();
    private static HashMap<String, SymbolForTable> symbolTable = new HashMap<>();
    private static SyntaxNode node;



    public static void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

        //updates current token
        private static void updateTokens(){
            currentToken = tokenStack.pop();
            lookAheadToken = tokenStack.peek();
        }

        // creates node child in the vacant position
        private static void createChild (SyntaxNode parent, SyntaxNode child){
            if (parent.getLeft() == null){
                parent.setLeft(child);
            }else if (parent.getRight() == null){
                parent.setRight(child);
            }else{
                parent.setMiddle(parent.getRight());
                parent.setRight(child);
            }
        }
    
        private static void burnTokens(){
            boolean keepBurning = true;
            while(keepBurning == true){
                updateTokens();
                if(currentToken.getTokenEnumString().equals("TBEGN")){
                    keepBurning = false;
                }
                if(currentToken.getTokenEnumString().equals("TSEMI")){
                    keepBurning = false;
                }
                if(currentToken.getTokenEnumString().equals("TMAIN")){
                    keepBurning = false;
                }
                if(currentToken.getTokenEnumString().equals("TFUNC")){
                    keepBurning = false;
                }
            }
        }
        public void superMethod (){
            updateTokens();
            nprog();
        }


    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private void nprog(){
        if(currentToken.getTokenEnumString().equals("TCD23")){
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                node = new SyntaxNode("NPROG", currentToken.getLexeme(), currentToken.getTokenEnumString());

                globals(node);
                funcs(node);
                mainbody(node);
            }else{
                System.out.println("Error: Expected an identifier in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
                burnTokens();
            }
        }else{
            System.out.println("Error: Your program must start with 'CD23'");
            burnTokens();
        }
    }
    
    //NGLOB <globals> ::= <consts> <types> <arrays>
    private void globals(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TCONS") ||           // if there are globals
        lookAheadToken.getTokenEnumString().equals("TTYPS") ||
        lookAheadToken.getTokenEnumString().equals("TARRS")){
            if(parent.getLeft() == null){
                node = new SyntaxNode("NGLOB", lookAheadToken.getLexeme(), lookAheadToken.getTokenEnumString());
                parent.setLeft(node);
                consts(node);
                types(node);
                arrays(node);
            }else{
                System.out.println("Error: Expected globals in line: " + lookAheadToken.getLineNumber());
                burnTokens();
            }
        }
    }

    //NILIST <initlist> ::= <init> , <initlist>
    //Special <initlist> ::= <init>
    private void consts(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TCONS")){   // if there are constants
            updateTokens();
            initlist(parent);
        }
    }

    private void initlist(SyntaxNode parent){
        SyntaxNode temp = init();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NILIST", null, null);
            if (parent.getNodeValue().equals("NILIST") || parent.getLeft() != null){
                parent.setRight(node);
            }else{
                parent.setLeft(node);
            }
            node.setLeft(temp);
            initlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
        }else{
            parent.setRight(temp);
            
        }
    }

    //NINIT <init> ::= <id> is <expr>
    private SyntaxNode init(){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            node = new SyntaxNode("NINIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            if (lookAheadToken.getTokenEnumString().equals("TTTIS")){
                updateTokens();
                // expr(node);
                updateTokens();
            }else{
                System.out.println("Error: Expected keyword 'is' in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
                burnTokens();
            }
        }else{
            System.out.println("Error: Expected global variable initialization in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
            node = new SyntaxNode("NUNDF", null, null);
            burnTokens();
        }
        return node;
    }

    //Special <types> ::= types <typelist> | ε
    private void types(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TTYPS")){
            updateTokens();
            typelist(parent);
        }
    }

    //Special <arrays> ::= arrays <arrdecls> | ε
    private void arrays(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TARRS")){
            updateTokens();
            arrdecls(parent);
        }
    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private void funcs(SyntaxNode parent){
        SyntaxNode temp = func();
        if (lookAheadToken.getTokenEnumString().equals("TFUNC")){
            node = new SyntaxNode("NFUNCS", null, null);
            createChild(parent, node);
            createChild(node, temp);
            funcs(node);
        }
        createChild(parent, temp);
    }

    //NMAIN <mainbody> ::= main <slist> begin <stats> end CD23 <id>
    private void mainbody(SyntaxNode parent){
         if (lookAheadToken.getTokenEnumString().equals("TMAIN")){     // if there is mainbody
            node = new SyntaxNode("NMAIN", null, null);
            if(parent.getLeft() == null){                
                parent.setLeft(node);
            }else if (parent.getRight() == null){
                parent.setRight(node);
            }else if (parent.getRight().getNodeValue() != "NFUNC"){
                System.out.println("Error: Expected main after functions, in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
                burnTokens();
            }else{
                parent.setMiddle(parent.getRight());
                parent.setRight(node);
            }
            
         }
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private void typelist(SyntaxNode parent){
        SyntaxNode temp = type();
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            node = new SyntaxNode("TTYPEL", null, null);
            createChild(parent, node);
            createChild(node, temp);
            typelist(node);
        }
        createChild(parent, temp);
    }

    private SyntaxNode type(){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            if (lookAheadToken.getTokenEnumString().equals("TTTIS")){
                String type = currentToken.getLexeme();
                String lexeme = currentToken.getTokenEnumString();
                updateTokens();
                if (lookAheadToken.getTokenEnumString().equals("TARAY")){   //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
                    updateTokens();
                    node = new SyntaxNode("NATYPE", lexeme, type);
                    if (lookAheadToken.getTokenEnumString().equals("TLBRK")){
                        updateTokens();
                        expr(node);
                        if (lookAheadToken.getTokenEnumString().equals("TRBRK")){
                            updateTokens();
                            if (lookAheadToken.getTokenEnumString().equals("TTTOF")){
                                updateTokens();
                                if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
                                    updateTokens();
                                    // Symbol table: structid of the array
                                    if (lookAheadToken.getTokenEnumString().equals("TTEND")){
                                        updateTokens();
                                    }// Error
                                }// Error
                            }// Error
                        }// Error
                    }// Error
                    return node;
                }else{                      //NRTYPE <type> ::= <structid> is <fields> end
                    node = new SyntaxNode("NRTYPE", lexeme, type);
                    fields(node);
                    if (lookAheadToken.getTokenEnumString().equals("TTEND")){
                        updateTokens();
                        return node;
                    }
                }        
            }
        }
        node = new SyntaxNode("NUNDF", null, null);
        return node;
    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private void fields(SyntaxNode parent){
        SyntaxNode temp = sdecl(declPrefix());
        optFields(temp, parent);
    }

    private void optFields(SyntaxNode child, SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            node = new SyntaxNode("NFLIST", null, null);
            createChild(parent, node);
            createChild(node, child);
            fields(node);
        }
        createChild(parent, child);
    }

    private String[] declPrefix (){
        String[]tokenInfo = new String[2];
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            if(lookAheadToken.getTokenEnumString().equals("TCOLN")){
                tokenInfo[0] = currentToken.getLexeme();
                tokenInfo[1] = currentToken.getTokenEnumString();
                updateTokens();
            }//Error
        }//Error
        return tokenInfo;
    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private SyntaxNode sdecl(String[]identInfo){
        node = new SyntaxNode("NSDECL", identInfo[0], identInfo[1]);
        stype();
        return node;
    }

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    private void arrdecls(SyntaxNode parent){
        SyntaxNode node = arrdecl(declPrefix());
        optArrdecl(parent, node);
    }

    //NARRD <arrdecl> ::= <id> : <typeid>
    private SyntaxNode arrdecl(String[]identInfo){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            node = new SyntaxNode("NARRD", identInfo[0], identInfo[1]);
        }// Error
        return node;
    }

    private void optArrdecl (SyntaxNode parent, SyntaxNode child){
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NALIST", null, null);
            createChild(parent, node);
            createChild(node, child);
            arrdecls(node);
        }else{
            createChild(parent, child);
        }
    }

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>
    private SyntaxNode func(){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TFUNC")){
            updateTokens();
            if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                node = new SyntaxNode("NFUND", currentToken.getLexeme(), currentToken.getTokenEnumString());
                if (lookAheadToken.getTokenEnumString().equals("TLPAR")){
                    updateTokens();
                    plist(node);
                    if (lookAheadToken.getTokenEnumString().equals("TRPAR")){
                        updateTokens();
                        if (lookAheadToken.getTokenEnumString().equals("TCOLN")){
                            updateTokens();
                            rtype();
                            funcbody(parent);
                        }
                    }
                }
            }// Error
        }// Error
        return node;
    }

    //Special <rtype> ::= <stype> | void
    private void rtype(){
        if (lookAheadToken.getTokenEnumString().equals("TVOID")){
            updateTokens();
        }else{
            stype();
        }
    }

    //Special <plist> ::= <params> | ε
    private void plist(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN") ||
        lookAheadToken.getTokenEnumString().equals("TCNST")){
            params(parent);
        }
    }

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>
    private void params (SyntaxNode parent){
        node = param();
        optParams(parent, node);
    }

    private SyntaxNode param (){
        if (lookAheadToken.getTokenEnumString().equals("TCNST")){       //NARRC <param> ::= const <arrdecl>
            updateTokens();
            node = new SyntaxNode("NARRC", null, null);
            SyntaxNode temp = arrdecl(declPrefix());
            createChild(node, temp);
        }else{
            if (lookAheadToken.getTokenEnumString().equals("TINT") ||           //NSIMP <param> ::= <sdecl>
            lookAheadToken.getTokenEnumString().equals("TREAL") ||
            lookAheadToken.getTokenEnumString().equals("TBOOL")){
                node = new SyntaxNode("NSIMP", null, null);
                SyntaxNode temp = sdecl(declPrefix());
                createChild(node, temp);
            }else{                                                              //NARRP <param> ::= <arrdecl>
                node = new SyntaxNode("NARRP", null, null);
                SyntaxNode temp = arrdecl(declPrefix());
                createChild(node, temp);
            }
        }
        return node;
    }

    private void optParams (SyntaxNode parent, SyntaxNode child){
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NPLIST", null, null);
            createChild(parent, node);
            createChild(node, child);
            params(node);
        }
        createChild(parent, child);
    }


    //Special <funcbody> ::= <locals> begin <stats> end
    private void funcbody (SyntaxNode parent){
        locals(parent);
        if (lookAheadToken.getTokenEnumString().equals("TBEGN"));{
            updateTokens();
            stats(parent);
            if (lookAheadToken.getTokenEnumString().equals("TTEND")){
                updateTokens();
            }// Error
        }// Error
    }

    //Special <locals> ::= <dlist> | ε
    private void locals(SyntaxNode parent){
        
    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>

    //Special <decl> ::= <sdecl> | <arrdecl>

    //Special <stype> ::= integer | real | boolean
    private void stype(){
        if (lookAheadToken.getTokenEnumString().equals("TINTG") ||
        lookAheadToken.getTokenEnumString().equals("TREAL") ||
        lookAheadToken.getTokenEnumString().equals("TBOOL")){
            updateTokens();
            // Symbol table leaf value
        }
    }

    //NSTATS <stats> ::= <stat> ; <stats> | <strstat> <stats>
    //Special <stats> ::= <stat>; | <strstat>

    //Special <strstat> ::= <forstat> | <ifstat>

    //Special <stat> ::= <reptstat> | <asgnstat> | <iostat>

    //Special <stat> ::= <callstat> | <returnstat>

    //NFORL <forstat> ::= for ( <asgnlist> ; <bool> ) <stats> end

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>

    //Special <asgnlist> ::= <alist> | ε

    //NASGNS <alist> ::= <asgnstat> , <alist>
    //Special <alist> ::= <asgnstat>

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end

    //Special <asgnstat> ::= <var> <asgnop> <bool>

    //NASGN <asgnop> ::= =
    //NPLEQ <asgnop> ::= +=
    //NMNEQ <asgnop> ::= -=
    //NSTEA <asgnop> ::= *=
    //NDVEQ <asgnop> ::= /=

    //NINPUT <iostat> ::= In >> <vlist>

    //NOUTP <iostat> ::= Out << <prlist>
    //NOUTL <iostat> ::= Out << Line
    //NOUTL <iostat> ::= Out << <prlist> << Line
//------------------------------------------------------------------^^cameron^^---------------------------------------------------
//-------------------------->>Andres<<------------------------------------------------------------------------------------------------
    //NCALL <callstat> ::= <id> ( <elist> ) | <id> ( ) is the same as NFCALL (already done)
    private void callstat(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            // Store identifier in the symbol table
            if(lookAheadToken.getTokenEnumString().equals("(")){
                node = new SyntaxNode("NCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
                updateTokens();
                createChild(parent, node);
                if(lookAheadToken.getTokenEnumString().equals("TNOTT") ||   // this is the <elist> path
                lookAheadToken.getTokenEnumString().equals("TIDEN") ||
                lookAheadToken.getTokenEnumString().equals("TILIT") ||
                lookAheadToken.getTokenEnumString().equals("TFLIT") ||
                lookAheadToken.getTokenEnumString().equals("TTRUE") ||
                lookAheadToken.getTokenEnumString().equals("TFALS") ||
                lookAheadToken.getTokenEnumString().equals("(")){
                    elist(node);
                }else if(lookAheadToken.getTokenEnumString().equals("TRPAR")){
                }else{
                    System.out.println("Error: Expected a valid parameter");
                    burnTokens();
                }
            }else{
                System.out.println("Error: Expected left parenthesis");
                burnTokens();
            }
        }else{
            System.out.println("Error: Expected an identifier");
            burnTokens();
        }
    }

    //NRETN <returnstat> ::= return void | return <expr>
    private void returnstat(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TRETN")){
            updateTokens();
            node = new SyntaxNode("NRETN", currentToken.getLexeme(), currentToken.getTokenEnumString());
            if(lookAheadToken.getTokenEnumString().equals("TVOID")){
                updateTokens();
                // Store TRETN in symbol table with void return.
            }else{
                // Store TRETN in symbol table with object return.
                expr(parent);
            }
        }
    }

    //NVLIST <vlist> ::= <var> , <vlist>
    private void vlist(SyntaxNode parent){
        SyntaxNode temp = var();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NVLIST", null, null);
            if (parent.getNodeValue().equals("NVLIST") || parent.getLeft() != null){
                parent.setRight(node);
            }else{
                parent.setLeft(node);
            }
            node.setLeft(temp);
            
            
            optVlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
            
        }else{
            parent.setRight(temp);
            
        }
        // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
    }

    private SyntaxNode var(){
        if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            // Symbol table - identifier
            node = optExpr();
            return node;
        }else{
            System.out.println("Error: Expected a variable in line " + currentToken.getLineNumber());
            burnTokens();
            return new SyntaxNode("TUNDF", null, null);
        }
    }

    private SyntaxNode optExpr(){
        if (lookAheadToken.getTokenEnumString().equals("[")){
            updateTokens();
            expr(parent);
            return optId();
        }else{  //NSIMV <var> ::= <id>
            node = new SyntaxNode("NSIMV", currentToken.getLexeme(), currentToken.getTokenEnumString());
            // 
            return node;
        }
    }
    
    private SyntaxNode optId(){
        if (lookAheadToken.getTokenEnumString().equals(".")){   //NARRV <var> ::= <id>[<expr>] . <id>
            updateTokens();
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                // Symbol table - identifier
                node = new SyntaxNode("NARRV", currentToken.getLexeme(), currentToken.getTokenEnumString());
                return node;
            }else{
                System.out.println("Error: Expected an array identifier in line " + currentToken.getLineNumber());
                burnTokens();
                node = new SyntaxNode("NUNDF", null, null);
                return node;
            }
        }else{  //NAELT <var> ::= <id>[<expr>]
            node = new SyntaxNode("NAELT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            return node;
        }
    }  

    private void optVlist(SyntaxNode parent){
        SyntaxNode temp = var();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NVLIST", null, null);
            if (parent.getNodeValue().equals("NVLIST") || parent.getLeft() != null){
                parent.setRight(node);
            }else{
                parent.setLeft(node);
            }
            node.setLeft(temp);
            
            
            optVlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
            
        }else{
            parent.setRight(temp);
            
        }
        // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
    }

    //NEXPL <elist> ::= <bool> , <elist>
    private void elist(SyntaxNode parent){
        bool(parent);
        optElist(parent);
        // Create new node NEXPL
    }

    private void optElist(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            elist(parent);
        }
        // If no condition satisfied, then let's suppose optElist == epsilon (Special <elist> ::= <bool>)
    }

    //NBOOL <bool> ::= <bool> <logop> <rel> 
    private void bool(SyntaxNode parent){
        updateTokens();
        // Create new node NBOOL
        rel(parent);
        bool2(parent);
    }

    private void bool2(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TTAND") ||
        lookAheadToken.getTokenEnumString().equals("TTTOR") ||
        lookAheadToken.getTokenEnumString().equals("TTXOR")){
            logop();
            rel(parent);
            bool2(parent);
        }
        // If no condition satisfied, then let's suppose bool' == epsilon (Special <bool> ::= <rel>)
    }

    //NNOT <rel> ::= ! <expr> <relop> <expr>
    private void rel(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("!")){
            updateTokens();
            // Create new node NNOT
            expr(parent);
            relop();
            expr(parent);
        }else{
            expr(parent);
            optRel();
        }
    }

    private void optRel(){
        if(lookAheadToken.getTokenEnumString().equals("TEQEQ") ||   //Special <rel> ::= <expr> <relop><expr>
        lookAheadToken.getTokenEnumString().equals("TNEQL") ||
        lookAheadToken.getTokenEnumString().equals("TGRTR") ||
        lookAheadToken.getTokenEnumString().equals("TLESS") ||
        lookAheadToken.getTokenEnumString().equals("TLEQ") ||
        lookAheadToken.getTokenEnumString().equals("TGEQL")){
            relop();
            expr(parent);
        }
        // If no condition satisfied, then let's suppose optRel == epsilon (Special <rel> ::= <expr>)
    }

    
    private void logop(){
        switch(lookAheadToken.getTokenEnumString()){
            case "TTAND":   //NAND <logop> ::= &&
                updateTokens();
                // Create new node NAND
            break;

            case "TTTOR":   //NOR <logop> ::= ||
                updateTokens();
                // Create new node NOR
            break;

            case "TTXOR":   //NXOR <logop> ::= &|
                updateTokens();
                // Create new node NXOR
            break;

            default:
            break;
        }
    }

    private void relop(){
        switch(lookAheadToken.getTokenEnumString()){
            case "TEQEQ":   //NEQL <relop> ::= ==
                updateTokens();
                // Create new node NEQL
            break;

            case "TNEQL":   //NNEQ <relop> ::= !=
                updateTokens();
                // Create new node NOR
            break;

            case "TGRTR":   //NGRT <relop> ::= >
                updateTokens();
                // Create new node NXOR
            break;
            case "TLESS":   //NLSS <relop> ::= <
                updateTokens();
                // Create new node NXOR
            break;
            case "TLEQ":   //NLEQ <relop> ::= <=
                updateTokens();
                // Create new node NXOR
            break;
            case "TGEQL":   //NGEQ <relop> ::= >=
                updateTokens();
                // Create new node NXOR
            break;

            default:
            break;
        }
    }    

    // Special <expr> ::= <term> <expr'>
    private void expr(SyntaxNode parent){
        term(parent);
        expr2(parent);
    }

    //Function for the grammar productions of expr'
    private void expr2 (SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("+")){   //NADD <expr'> ::= + <term> <expr'>
            updateTokens();
            node = new SyntaxNode("NADD", null, null);
            createChild(parent, node);
            
            term(node);
            expr2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("-")){  //NSUB <expr> ::= <expr> - <term>
            updateTokens();
            node = new SyntaxNode("NSUB", null, null);
            createChild(parent, node);
            
            term(node);
            expr2(node);
        }
        // If no condition satisfied, then let's suppose expr' == epsilon (Special <expr> ::= <term>)
    }

    //Special <term> ::= <fact> <term'>
    private void term(SyntaxNode parent){
        fact(parent);
        term2(parent);
    }

    // Function for the grammar productions of term'
    private void term2(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("*")){   //NMUL <term> ::= <term> * <fact>
            updateTokens();
            node = new SyntaxNode("NMUL", null, null);
            createChild(parent, node);
            
            fact(node);
            term2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("/")){     //NDIV <term> ::= <term> / <fact>
            updateTokens();
            node = new SyntaxNode("NDIV", null, null);
            createChild(parent, node);
            
            fact(node);
            term2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("%")){  //NMOD <term> ::= <term> % <fact>
            updateTokens();
            node = new SyntaxNode("NMOD", null, null);
            createChild(parent, node);
            
            fact(node);
            term2(node);
        }
        // If no condition satisfied, then let's suppose term' == epsilon (Special <term> ::= <fact>)
    }
    
    //Special <fact> ::= <exponent>
    private void fact(SyntaxNode parent){
        exponent(parent);
        fact2(parent);
    }

    // Function for the grammar productions of fact'
    private void fact2(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("^")){    //NPOW <fact> ::= <fact> ^ <exponent>
            updateTokens();
            node = new SyntaxNode("NPOW", null, null);
            createChild(parent, node);
            
            exponent(node);
            fact2(node);
        }
        // If no condition satisfied, then let's suppose fact' == epsilon (Special <fact> ::= <exponent>)
    }

    private void exponent(SyntaxNode parent){
        switch(lookAheadToken.getTokenEnumString()){
            case "TIDEN":   //Special <exponent> ::= <var> or Special <exponent> ::= <fncall>
                updateTokens();
                var();
            break;

            case "TILIT":   //NILIT <exponent> ::= <intlit>
                updateTokens();
                node = new SyntaxNode("NILIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, node);
            break;

            case "TFLIT":   // <exponent> ::= <reallit>
                updateTokens();
                node = new SyntaxNode("NFLIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, node);
            break;

            case "TTRUE":   // <exponent> ::= true
                updateTokens();
                node = new SyntaxNode("NTRUE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, node);
            break;

            case "TFALS":   // <exponent> ::= false
                updateTokens();
                node = new SyntaxNode("NFALS", currentToken.getLexeme(), currentToken.getTokenEnumString());
                createChild(parent, node);
            break;

            case "(":   //Special <exponent> ::= ( <bool> )
                updateTokens();
                bool(parent);
            break;
        }
    }  

    //NPRLST <prlist> ::= <printitem> , <prlist>
    private void prlist(SyntaxNode parent){
        // Create new node NPRLST
        printitem(parent);
        optprlist(parent);
    }

    private void printitem(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TSTRG")){    //NSTRG <printitem> ::= <string>
            updateTokens(); 
            node = new SyntaxNode("NSTRG", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(parent, node);
            // Symbol table - string
        }else{
            expr(parent); //Special <printitem> ::= <expr>
        }
    }

    private void optprlist(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            prlist(parent);
        }
        // If no condition satisfied, then let's suppose optprlist == epsilon (Special <prlist> ::= <printitem>)
    }


}



/*
Writing a Recursive Descent parser:


 *  void A() {
1)      Choose an A-production, A → X1 X2 . . . Xk ;
2)      for ( i = 1 to k ) {
3)          if ( Xi is a nonterminal )
4)              call procedure Xi () ;
5)          else if ( Xi equals the current input symbol a )
6)              advance the input to the next symbol;
7)          else /* an error has occurred * / ;
        }
    }
------------------------------------------------------------------------
Use next token (lookahead) to choose (PREDICT) which production to
‘mimic’.

• Ex: B → b C D

    B() {
        if (lookahead == ‘b’)
        { match(‘b’); C(); D(); }
        else ...
    }
------------------------------------------------------------------------
Also need the following:
    match(symbol) {
        if (symbol == lookahead)
            lookahead = nexttoken()
        else error() }

    main() {
        lookahead = nextoken();
        S();    /// S is the start symbol 
        if (lookahead == EOF) then accept
        else reject
        }
    error() { ...
        }

------------------------------------------------------------------------
    S() {
        if (lookahead == a ) { match(a);B(); } S → a B
        else if (lookahead == b) { match(b); C(); } S → b C
        else error("expecting a or b");
    }

    B() {
        if (lookahead == b)
        {match(b); match(b); C();} B → b b C
        else error();
    }

    C() {
        if (lookahead == c)
        { match(c) ; match(c) ;} C → c c
        else error();
    }
 */