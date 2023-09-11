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
    private static Stack<Token> tokenStack;
    private static Map<String, SymbolForTable> symbolTable = new HashMap<>();
    private static SyntaxNode root;
    private static boolean parenthOpen = false;
    private static String holdTIDEN;

    private static void handleError(){
        System.out.println("syntax error has occured at line:"+currentToken.getLineNumber()+ " col:"+currentToken.getColumnNumber());
        while(!currentToken.getTokenEnumString().equals("TSEMI")){
            updateTokens();
        }
    }

    public static void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        Stack<Token> tempStack = new Stack<Token>();
        while(arrayLength >= 0){
            tempStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
        tokenStack = tempStack;
    }

    //updates current token
    private static void updateCurrentToken(){
        currentToken = tokenStack.pop();
    }

    //updates look ahead token
    private static void peekNextToken(){
        lookAheadToken = tokenStack.peek();
    }

    //updates both current and peek tokens
    private static void updateTokens(){
        updateCurrentToken();
        peekNextToken();
    }
    
    //begining method of parsing non terminal methods
    public static Map<String, SymbolForTable> superMethod(){
        updateCurrentToken();
        peekNextToken();
        nProg();
        return symbolTable;
    }

    //--------------------To DO---------------------------------------
    // 1) finish connecting nodes w/ child functions
    // 2) identify all tokens that create nodes
    // 3) locate spots in code to update tokens
    // 4) locate spots in code to peek ahead
    // 5) determine edge cases
    //----------------------------------------------------------------

    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private static void nProg(){
        if(currentToken.getTokenEnumString().equals("TCD23")){
            updateTokens();
            if(currentToken.getTokenEnumString().equals("TIDEN")){//sets root node identifier
                root = new SyntaxNode(currentToken.getLexeme(), "NPROG");
                updateTokens();
            }
            else{
                //error
            }

//            if(currentToken.equals()){ //globals
//                root.setLeft(nGlob());
//                updateTokens();
//            }
            if(currentToken.getTokenEnumString().equals("TFUNC")){ //functions
                if(root.getLeft() == null){
                    root.setLeft(nFuncs());
                }
                else{
                    root.setMiddle(nFuncs());
                }
                updateTokens();
            }
            if(currentToken.getTokenEnumString().equals("TMAIN")){
                root.setRight(nMain());
            }
        }
        else{
            //error
        }
    }

    //NGLOB <globals> ::= <consts> <types> <arrays>
    private static SyntaxNode nGlob(){
        SyntaxNode NGLOB = new SyntaxNode("NGLOB", "NGLOB");

        //enter codebase for nfuncs here-------------------------
    }

    //NILIST <initlist> ::= <init> , <initlist>
    //Special <initlist> ::= <init>
    private SyntaxNode nIList(){
        SyntaxNode NILIST = new SyntaxNode( , );
        //enter codbase here---------------------------------------------------
        return NILIST;
    }

    //NINIT <init> ::= <id> is <expr>
    private SyntaxNode nINit(){
        SyntaxNode NINIT = new SyntaxNode( , );
        //enter codbase here-------------------------------------------------------------------------------
        return NINIT;
    }

    //Special <rtype> ::= <stype> | void
    //Special <types> ::= types <typelist> | ε
    //Special <arrays> ::= arrays <arrdecls> | ε
    //Special <strstat> ::= <forstat> | <ifstat>
    //Special <funcbody> ::= <locals> begin <stats> end
    //Special <locals> ::= <dlist> | ε
    //Special <decl> ::= <sdecl> | <arrdecl>
    //Special <stype> ::= integer | real | boolean
    //Special <stat> ::= <reptstat> | <asgnstat> | <iostat>
    //Special <stat> ::= <callstat> | <returnstat>
    //Special <asgnstat> ::= <var> <asgnop> <bool>
    private SyntaxNode special(){
        SyntaxNode SPECIAL = new SyntaxNode( , );
        //enter codbase here-------------------------------------------------------------------------
        return SPECIAL;
    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private static SyntaxNode nFuncs(){
        updateTokens();
        SyntaxNode NFUNCS;
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            NFUNCS = new SyntaxNode(currentToken.getLexeme(), "NFUNC");
            NFUNCS.setLeft(nPList());
        }
        else{
            //error
        }

        return NFUNCS;
    }

    //NMAIN <mainbody> ::= main <slist> begin <stats> end CD23 <id>
    private static SyntaxNode nMain(){
        SyntaxNode NMAIN = new SyntaxNode("main", "NMAIN");
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            NMAIN.setLeft(nSdlst());//NSDLST 
        }
        if(currentToken.getTokenEnumString().equals("TBEGN")){
            NMAIN.setRight(nStats());//NSTATS
        }
        return NMAIN;
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>
    private static SyntaxNode nSdlst(){
        SyntaxNode NSDLST = new SyntaxNode("NSDLST", "NSDLST");
        if(currentToken.getTokenEnumString().equals("TIDEN")){ //token declaration
            NSDLST.setLeft(nSdecl());//NSDECL
        }
        if(currentToken.getTokenEnumString().equals("TCOMA")){ // another node for a token declaration
            NSDLST.setRight(nSdlst());//NSDLST
        }
        if(currentToken.getTokenEnumString().equals("TBEGN")){ // looks for TBEGN token or produces an error
            return NSDLST;
        }
        else{
            //error
        }
        return NSDLST;
    }

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private static SyntaxNode nTypel(){
        SyntaxNode NTYPEL = new SyntaxNode( , );
        //enter codbase here-------------------------------------------------------------------------
        return NTYPEL;
    }

    //NRTYPE <type> ::= <structid> is <fields> end
    private static SyntaxNode nRType(){
        SyntaxNode NRTYPE = new SyntaxNode( , );
        //enter codbase here------------------------------------------------------------------------
        return NRTYPE;
    }

    //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
    private static SyntaxNode nAType(){
        SyntaxNode NATYPE = new SyntaxNode( , );
        //enter codbase here------------------------------------------------------------------------------
        return NATYPE;
    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private static SyntaxNode nFList(){

    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private static SyntaxNode nSdecl(){
        SyntaxNode NSDECL = new SyntaxNode(currentToken.getLexeme(), "NSDECL");
        holdTIDEN = currentToken.getLexeme();
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TCOLN")){
            updateTokens();
            //check for appropriate token type, if not acceptable error
            //create symbol for token type and add to table/hashmap----------------------------------------------------------------
        }
        else{
            //error
        }
        updateTokens();
        return NSDECL;
    }

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    private static SyntaxNode nAList(){
        SyntaxNode NALIST = new SyntaxNode( , );

        return NALIST;
    }

    //NARRD <arrdecl> ::= <id> : <typeid>
    private static SyntaxNode nAarrd(){
        SyntaxNode NAARRD = new SyntaxNode( , );

        return NAARRD;
    }

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>
    private static SyntaxNode nFund(){
        SyntaxNode NFUND = new SyntaxNode( , );

        return NFUND;
    }

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>
    //Special <plist> ::= <params> | ε
    private static SyntaxNode nPList(){
        SyntaxNode NPLIST = new SyntaxNode("NPLIST", "NPLIST");
        if(lookAheadToken.getTokenEnumString().equals("TLPAR")){//checks for open parenth for func params
            parenthOpen = true;
            updateTokens();
        }
        else if(!lookAheadToken.getTokenEnumString().equals("TLPAR") && parenthOpen == false){
            //error
        }
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            holdTIDEN = currentToken.getLexeme();
            updateTokens();
            if(currentToken.getTokenEnumString().equals("TCOLN")){
                updateTokens();
                if(currentToken.getTokenEnumString().equals("TINTG")){
                    NPLIST.setLeft(nSimp());
                }
                if(currentToken.getTokenEnumString().equals("TBOOL")){
                    NPLIST.setLeft(nSimp());
                }
                if(currentToken.getTokenEnumString().equals("TREAL")){
                    NPLIST.setLeft(nSimp());
                }
                if(currentToken.getTokenEnumString().equals("")){//-------------------other arguments-----------------

                }
            }
            else{
                //error
            }
            if(currentToken.getTokenEnumString().equals("TCOMA")){//add new node for next param
                NPLIST.setRight(nPList());
            }
        }
        else{
            //error
        }
        if(currentToken.getTokenEnumString().equals("TRPAR")){
            parenthOpen = false;
        }
        return NPLIST;
    }
    
    //NSIMP <param> ::= <sdecl>
    private static SyntaxNode nSimp(){
        SyntaxNode NSIMP = new SyntaxNode(holdTIDEN,"NSIMP");
        //enter simple parameter into function hashtable entry-------------------------------------------------
        updateTokens();
        return NSIMP;
    }

    //NARRP <param> ::= <arrdecl>
    private static SyntaxNode nArrp(){
        SyntaxNode NAARP = new SyntaxNode( , );

        return NAARP;
    }

    //NARRC <param> ::= const <arrdecl>
    private static SyntaxNode nArrc(){
        SyntaxNode NAARC = new SyntaxNode( , );

        return NAARC;
    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>
    private static SyntaxNode nDList(){
        SyntaxNode NDLIST = new SyntaxNode( , );

        return NDLIST;
    }

    //NSTATS <stats> ::= <stat> ; <stats> | <strstat> <stats>
    //Special <stats> ::= <stat>; | <strstat>
    private static SyntaxNode nStats(){
        SyntaxNode NSTATS = new SyntaxNode("NSTATS", "NSTATS");
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TINPT")){
            if(lookAheadToken.getTokenEnumString().equals("TGRGR")){
                NSTATS.setLeft(nInput());
            }
            else{
                //error
            }
        }
        if(currentToken.getTokenEnumString().equals("TSEMI")){//tokens for a new stats
            NSTATS.setRight(nStats());
        }
        if(currentToken.getTokenEnumString().equals("TIDEN")){//assignment node
            if(lookAheadToken.getTokenEnumString().equals("TEQUL")){
                NSTATS.setLeft(nAsgns());
            }
            else{
                //error
            }
        }
        if(currentToken.getTokenEnumString().equals("TOUTP")){
            if(currentToken.getTokenEnumString().equals("TLSLS")){
                NSTATS.setRight(nOutl());
            }
            else{
                //error
            }
        }
        return NSTATS;
    }

    private static SyntaxNode nSimv(){
        SyntaxNode NSIMV = new SyntaxNode(currentToken.getLexeme(), "NSIMV");
        //check for symbol on table/hashmap------------------------------------------
        //error if not on hashmap
        updateTokens();
        return NSIMV;
    }

    //NFORL <forstat> ::= for ( <asgnlist> ; <bool> ) <stats> end
    private static SyntaxNode nArrc(){
        SyntaxNode NARRC = new SyntaxNode( , );

        return NARRC;
    }

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>
    //Special <asgnlist> ::= <alist> | ε
    private static SyntaxNode nRept(){
        SyntaxNode NREPT = new SyntaxNode( , );

        return NREPT;
    }

    //NASGNS <alist> ::= <asgnstat> , <alist>
    //Special <alist> ::= <asgnstat>
    private static SyntaxNode nAsgns(){
        SyntaxNode NASGN = new SyntaxNode("NASGN", "NASGN");
        NASGN.setLeft(nSimv());
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            if(lookAheadToken.getTokenEnumString().equals("TPLUS")){
                NASGN.setRight(nAdd());
            }
            if(lookAheadToken.getTokenEnumString().equals("TMINS")){//token for subtraction
                NASGN.setRight(nSub());
            }
            if(lookAheadToken.getTokenEnumString().equals("TDIVD")){//token for division
                NASGN.setRight(nDiv());
            }
            if(lookAheadToken.getTokenEnumString().equals("TSTAR")){//token for multiplication
                NASGN.setRight(nMul());
            }
            if(lookAheadToken.getTokenEnumString().equals("TPERC")){//token for modulos
                NASGN.setRight(nMod());
            }
            if(lookAheadToken.getTokenEnumString().equals("TCART")){//token for power
                NASGN.setRight(nPow());
            }
        }
    }

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end
    private static SyntaxNode nIft_(){
        SyntaxNode NIFT_ = new SyntaxNode( , );

        return NIFT_;
    }

    //NASGN <asgnop> ::= =
    //NPLEQ <asgnop> ::= +=
    //NMNEQ <asgnop> ::= -=
    //NSTEA <asgnop> ::= *=
    //NDVEQ <asgnop> ::= /=
    private static SyntaxNode asgnOp(){
        SyntaxNode ASGNOP = new SyntaxNode( , );

        return ASGNOP;
    }

    //NINPUT <iostat> ::= In >> <vlist>
    private static SyntaxNode nInput(){
        SyntaxNode NINPUT = new SyntaxNode("NINPUT", "NINPUT");
        updateTokens();
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            NINPUT.setLeft(nSimv());
        }
        return NINPUT;
    }

    //NOUTP <iostat> ::= Out << <prlist>
    //NOUTL <iostat> ::= Out << Line
    //NOUTL <iostat> ::= Out << <prlist> << Line
    private static SyntaxNode nOutl(){
        SyntaxNode NOUTL = new SyntaxNode("NOUTL", "NOUTL");
        updateTokens();
        updateTokens();
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            NOUTL.setLeft(nSimv());
        }
        return NOUTL;
    }

//------------------------------------------------------------------^^cameron^^---------------------------------------------------
//-------------------------->>Andres<<------------------------------------------------------------------------------------------------
     //NCALL <callstat> ::= <id> ( <elist> ) | <id> ( ) is the same as NFCALL (already done)
    private void callstat(){
        if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateCurrentToken();
            peekNextToken();
            // Store identifier in the symbol table
            if(lookAheadToken.getTokenEnumString().equals("(")){
                updateCurrentToken();
                peekNextToken();
                if(!lookAheadToken.getTokenEnumString().equals("TRPAR")){
                    elist();
                }
            }
        }
    }

    //NRETN <returnstat> ::= return void | return <expr>
    private void returnstat(){
        if(lookAheadToken.getTokenEnumString().equals("TRETN")){
            updateCurrentToken();
            peekNextToken();
            // Create new node NRETN
            if(lookAheadToken.getTokenEnumString().equals("TVOID")){
                updateCurrentToken();
                peekNextToken();
                // Store TRETN in symbol table with void return. Do it here?
            }else{
                // Store TRETN in symbol table with object return. How to do it, here or in the expr node?
                expr();
            }
        }
    }

    //NVLIST <vlist> ::= <var> , <vlist>
    private void vlist(){
        // Create new node NVLIST
        var();
        optVlist();
    }

    private void var(){
        if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
            updateCurrentToken();
            peekNextToken();
            // Symbol table - identifier
            optExpr();
        }
    }

    private void optExpr(){
        if (lookAheadToken.getTokenEnumString().equals("[")){
            updateCurrentToken();
            peekNextToken();
            expr();
            optId();
        }else{  //NSIMV <var> ::= <id>
            // Create new node NSIMV
        }
    }
    
    private void optId(){
        if (lookAheadToken.getTokenEnumString().equals(".")){   //NARRV <var> ::= <id>[<expr>] . <id>
            updateCurrentToken();
            peekNextToken();
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateCurrentToken();
                peekNextToken();
                // Symbol table - identifier
                // Create new node NARRV
            }
        }else{  //NAELT <var> ::= <id>[<expr>]
            // Create new node NAELT
        }
    }  

    private void optVlist(){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            vlist();
        }
        // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
    }

    //NEXPL <elist> ::= <bool> , <elist>
    private void elist(){
        bool();
        optElist();
        // Create new node NEXPL
    }

    private void optElist(){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateCurrentToken();
            peekNextToken();
            elist();
        }
        // If no condition satisfied, then let's suppose optElist == epsilon (Special <elist> ::= <bool>)
    }

    //NBOOL <bool> ::= <bool> <logop> <rel> 
    private void bool(){
        updateCurrentToken();
        peekNextToken();
        // Create new node NBOOL
        rel();
        bool2();
    }

    private void bool2(){
        if(lookAheadToken.getTokenEnumString().equals("TTAND") ||
        lookAheadToken.getTokenEnumString().equals("TTTOR") ||
        lookAheadToken.getTokenEnumString().equals("TTXOR")){
            logop();
            rel();
            bool2();
        }
        // If no condition satisfied, then let's suppose bool' == epsilon (Special <bool> ::= <rel>)
    }

    //NNOT <rel> ::= ! <expr> <relop> <expr>
    private void rel(){
        if(lookAheadToken.getTokenEnumString().equals("!")){
            updateCurrentToken();
            peekNextToken();
            // Create new node NNOT
            expr();
            relop();
            expr();
        }else{
            expr();
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
            expr();
        }
        // If no condition satisfied, then let's suppose optRel == epsilon (Special <rel> ::= <expr>)
    }

    
    private void logop(){
        switch(lookAheadToken.getTokenEnumString()){
            case "TTAND":   //NAND <logop> ::= &&
                updateCurrentToken();
                peekNextToken();
                // Create new node NAND
            break;

            case "TTTOR":   //NOR <logop> ::= ||
                updateCurrentToken();
                peekNextToken();
                // Create new node NOR
            break;

            case "TTXOR":   //NXOR <logop> ::= &|
                updateCurrentToken();
                peekNextToken();
                // Create new node NXOR
            break;

            default:
            break;
        }
    }

    private void relop(){
        switch(lookAheadToken.getTokenEnumString()){
            case "TEQEQ":   //NEQL <relop> ::= ==
                updateCurrentToken();
                peekNextToken();
                // Create new node NEQL
            break;

            case "TNEQL":   //NNEQ <relop> ::= !=
                updateCurrentToken();
                peekNextToken();
                // Create new node NOR
            break;

            case "TGRTR":   //NGRT <relop> ::= >
                updateCurrentToken();
                peekNextToken();
                // Create new node NXOR
            break;
            case "TLESS":   //NLSS <relop> ::= <
                updateCurrentToken();
                peekNextToken();
                // Create new node NXOR
            break;
            case "TLEQ":   //NLEQ <relop> ::= <=
                updateCurrentToken();
                peekNextToken();
                // Create new node NXOR
            break;
            case "TGEQL":   //NGEQ <relop> ::= >=
                updateCurrentToken();
                peekNextToken();
                // Create new node NXOR
            break;

            default:
            break;
        }
    }    

    // Special <expr> ::= <term> <expr'>
    private void expr(){
        term();
        expr2();
    }

    //Function for the grammar productions of expr'
    private void expr2 (){
        if (lookAheadToken.getTokenEnumString().equals("+")){   //NADD <expr'> ::= + <term> <expr'>
            updateCurrentToken();
            peekNextToken();
            // Create new node NADD
            term();
            expr2();
        }else if (lookAheadToken.getTokenEnumString().equals("-")){  //NSUB <expr> ::= <expr> - <term>
            updateCurrentToken();
            peekNextToken();
            // Create new node NSUB
            term();
            expr2();
        }
        // If no condition satisfied, then let's suppose expr' == epsilon (Special <expr> ::= <term>)
    }

    //Special <term> ::= <fact> <term'>
    private void term(){
        fact();
        term2();
    }

    // Function for the grammar productions of term'
    private void term2(){
        if (lookAheadToken.getTokenEnumString().equals("*")){   //NMUL <term> ::= <term> * <fact>
            updateCurrentToken();
            peekNextToken();
            // Create new node NMUL
            fact();
            term2();
        }else if (lookAheadToken.getTokenEnumString().equals("/")){ //NDIV <term> ::= <term> / <fact>
            updateCurrentToken();
            peekNextToken();
            // Create new node NDIV
            fact();
            term2();
        }else if (lookAheadToken.getTokenEnumString().equals("%")){ //NMOD <term> ::= <term> % <fact>
            updateCurrentToken();
            peekNextToken();
            // Create new node NMOD
            fact();
            term2();
        }
        // If no condition satisfied, then let's suppose term' == epsilon (Special <term> ::= <fact>)
    }
    
    //Special <fact> ::= <exponent>
    private void fact(){
        exponent();
        fact2();
    }

    // Function for the grammar productions of fact'
    private void fact2(){
        if(lookAheadToken.getTokenEnumString().equals("^")){    //NPOW <fact> ::= <fact> ^ <exponent>
            updateCurrentToken();
            peekNextToken();
            // Create new node NPOW
            exponent();
            fact2();
        }
        // If no condition satisfied, then let's suppose fact' == epsilon (Special <fact> ::= <exponent>)
    }

    private void exponent(){
        switch(lookAheadToken.getTokenEnumString()){
            case "TIDEN":   //Special <exponent> ::= <var> or Special <exponent> ::= <fncall>
            updateCurrentToken();
            peekNextToken();
            var();
            break;

            case "TILIT":   //NILIT <exponent> ::= <intlit>
                updateCurrentToken();
                peekNextToken();
                // Create new node NILIT
            break;

            case "TFLIT":   //NFLIT <exponent> ::= <reallit>
                updateCurrentToken();
                peekNextToken();
                // Create new node NFLIT
            break;

            case "TTRUE":   //NTRUE <exponent> ::= true
                updateCurrentToken();
                peekNextToken();
                // Create new node NTRUE
            break;

            case "TFALS":   //NFALS <exponent> ::= false
                updateCurrentToken();
                peekNextToken();
                // Create new node NFALS
            break;

            case "(":   //Special <exponent> ::= ( <bool> )
                updateCurrentToken();
                peekNextToken();
                bool();
            break;
        }
    }  

    //NPRLST <prlist> ::= <printitem> , <prlist>
    private void prlist(){
        // Create new node NPRLST
        printitem();
        optprlist();
    }

    private void printitem(){
        if(lookAheadToken.getTokenEnumString().equals("TSTRG")){    //NSTRG <printitem> ::= <string>
            updateCurrentToken();
            peekNextToken();
            // Create new node NSTRG
            // Symbol table - string
        }else{
            expr(); //Special <printitem> ::= <expr>
        }
    }

    private void optprlist(){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateCurrentToken();
            peekNextToken();
            prlist();
        }
        // If no condition satisfied, then let's suppose optprlist == epsilon (Special <prlist> ::= <printitem>)
    }
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
        else error(“expecting a or b”);
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