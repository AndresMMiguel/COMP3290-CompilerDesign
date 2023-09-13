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
    private static SyntaxNode parent;
    private static SyntaxNode node;
    private static ArrayList<SyntaxNode> nodeList = new ArrayList<SyntaxNode>();


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

        // creates child in the vacant position
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
    
        public ArrayList<SyntaxNode> superMethod (){
            updateTokens();
            nprog();
            return nodeList;
        }

        private static void burnTokens(){
            while(!currentToken.getTokenEnumString().equals("TSEMI")){
                updateTokens();
            }
        }

        private static void handleError(int errorCode){
            if(errorCode == 1){
                System.out.println("Error: Your program must start with 'CD23'");
            }
            if(errorCode == 2){
                System.out.println("Error: Expected an identifier in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
            }
            if(errorCode == 3){
                System.out.println("Error: Expected globals in line: " + lookAheadToken.getLineNumber());
            }
            if(errorCode == 4){
                System.out.println("Error: Expected keyword 'is' in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
            }
            if(errorCode == 5){
                System.out.println("Error: Expected global variable initialization in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
                node = new SyntaxNode("NUNDF", null, null);
            }
            if(errorCode == 6){
                System.out.println("Error: Expected functions after globals, in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
            }
            if(errorCode == 7){
                System.out.println("Error: Expected main after functions, in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
            }
            if(errorCode == 8){
                System.out.println("Error: Expected a valid parameter");
            }
            if(errorCode == 9){
                System.out.println("Error: Expected left parenthesis");
            }
            if(errorCode == 10){
                System.out.println("Error: Expected an identifier");
            }
            if(errorCode == 11){
                System.out.println("Error: Expected a variable in line " + currentToken.getLineNumber());
            }
            if(errorCode == 12){
                System.out.println("Error: Expected an array identifier in line " + currentToken.getLineNumber());
            }
            burnTokens();
        }


    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private void nprog(){
        if(currentToken.getTokenEnumString().equals("TCD23")){
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                parent = new SyntaxNode("NPROG", currentToken.getLexeme(), currentToken.getTokenEnumString());
                nodeList.add(parent);
                //create symboltable entry-----------------------------------------
                globals(parent);
                funcs(parent);
                mainbody(parent);
            }else{
                handleError(2);
            }
        }else{
            handleError(1);
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
                nodeList.add(node);
                //create symboltable entry-----------------------------------------
                consts(node);
                types(node);
                arrays(node);
            }else{
                handleError(3);
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
            nodeList.add(node);
            nodeList.add(temp);
            initlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
            nodeList.add(temp);
        }else{
            parent.setRight(temp);
            nodeList.add(temp);
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
                handleError(4);
            }
        }else{
            handleError(5);
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
        
    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private void funcs(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TFUNC")){     // if there are functions
            node = new SyntaxNode("NFUNCS", null, null);
            //create symboltable entry-----------------------------------------
            if(parent.getLeft() == null){                
                parent.setLeft(node);
            }else if (parent.getLeft().getNodeValue() != "NGLOB"){
                handleError(6);
            }else{
                parent.setRight(node);
            }
            nodeList.add(node);
        }
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
                handleError(7);
            }else{
                parent.setMiddle(parent.getRight());
                parent.setRight(node);
            }
            nodeList.add(node);
         }
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private void typelist(SyntaxNode parent){
        SyntaxNode temp = type();
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            if (lookAheadToken.getTokenEnumString().equals("TTTIS")){
                String type = currentToken.getLexeme();
                String lexeme = currentToken.getTokenEnumString();
                updateTokens();
                if (lookAheadToken.getTokenEnumString().equals("TARAY")){   //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
                    updateTokens();
                    node = new SyntaxNode("NATYPE", lexeme, type);
                    nodeList.add(node);
                    if (lookAheadToken.getTokenEnumString().equals("[")){
                        updateTokens();
                        // expr(node);
                        updateTokens();
                    }
                }else{                      //NRTYPE <type> ::= <structid> is <fields> end
                    fields();
                }
            }
        }
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
                    if (lookAheadToken.getTokenEnumString().equals("[")){
                        updateTokens();
                        // expr(node);
                        updateTokens();
                    }
                    return node;
                }else{                      //NRTYPE <type> ::= <structid> is <fields> end
                    node = new SyntaxNode("NRTYPE", lexeme, type);
                    fields();
                    return node;
                }
            }
        }
    }
    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private void fields(){

    }

    //NSDECL <sdecl> ::= <id> : <stype>
    //create symboltable entry-----------------------------------------

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    //create symboltable entry-----------------------------------------

    //NARRD <arrdecl> ::= <id> : <typeid>
    //create symboltable entry-----------------------------------------

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>

    //Special <rtype> ::= <stype> | void

    //Special <plist> ::= <params> | ε

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>

    //NSIMP <param> ::= <sdecl>
    //create symboltable entry-----------------------------------------

    //NARRP <param> ::= <arrdecl>
    //create symboltable entry-----------------------------------------

    //NARRC <param> ::= const <arrdecl>
    //create symboltable entry-----------------------------------------

    //Special <funcbody> ::= <locals> begin <stats> end

    //Special <locals> ::= <dlist> | ε

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>

    //Special <decl> ::= <sdecl> | <arrdecl>

    //Special <stype> ::= integer | real | boolean

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
                    handleError(8);
                }
            }else{
                handleError(9);
            }
        }else{
            handleError(10);
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
            nodeList.add(node);
            nodeList.add(temp);
            optVlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
            nodeList.add(temp);
        }else{
            parent.setRight(temp);
            nodeList.add(temp);
        }
        // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
    }

    private SyntaxNode var(){
        if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateTokens();
            //create symboltable entry-----------------------------------------
            node = optExpr();
            return node;
        }else{
            handleError(11);
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
            // nodeList.add(node);
            return node;
        }
    }
    
    private SyntaxNode optId(){
        if (lookAheadToken.getTokenEnumString().equals(".")){   //NARRV <var> ::= <id>[<expr>] . <id>
            updateTokens();
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                //create symboltable entry-----------------------------------------
                node = new SyntaxNode("NARRV", currentToken.getLexeme(), currentToken.getTokenEnumString());
                return node;
            }else{
                handleError(12);
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
            nodeList.add(node);
            nodeList.add(temp);
            optVlist(node);
        }else if (parent.getLeft() == null){
            parent.setLeft(temp);
            nodeList.add(temp);
        }else{
            parent.setRight(temp);
            nodeList.add(temp);
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
            nodeList.add(node);
            term(node);
            expr2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("-")){  //NSUB <expr> ::= <expr> - <term>
            updateTokens();
            node = new SyntaxNode("NSUB", null, null);
            createChild(parent, node);
            nodeList.add(node);
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
            nodeList.add(node);
            fact(node);
            term2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("/")){     //NDIV <term> ::= <term> / <fact>
            updateTokens();
            node = new SyntaxNode("NDIV", null, null);
            createChild(parent, node);
            nodeList.add(node);
            fact(node);
            term2(node);
        }else if (lookAheadToken.getTokenEnumString().equals("%")){  //NMOD <term> ::= <term> % <fact>
            updateTokens();
            node = new SyntaxNode("NMOD", null, null);
            createChild(parent, node);
            nodeList.add(node);
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
            nodeList.add(node);
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
            //create symboltable entry-----------------------------------------
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