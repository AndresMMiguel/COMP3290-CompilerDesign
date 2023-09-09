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
    private static Stack<Token> tokenStack;
    private static HashMap<String, SymbolForTable> symbolTable = new HashMap<>();


    public static void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

        //updates current token
        private static void updateCurrentToken(){
            currentToken = tokenStack.pop();
        }
    
        //updates look ahead token
        private static void peekNextToken(){
            lookAheadToken = tokenStack.peek();
        }
    
    public static HashMap superMethod(){
        updateCurrentToken();
        peekNextToken();
        //calls methods from within
        //obatin symbol
        //insert symbolfortable into hashmap
        return symbolTable;
    }

    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    
    //NGLOB <globals> ::= <consts> <types> <arrays>

    //NILIST <initlist> ::= <init> , <initlist>
    //Special <initlist> ::= <init>

    //NINIT <init> ::= <id> is <expr>

    //Special <types> ::= types <typelist> | ε

    //Special <arrays> ::= arrays <arrdecls> | ε

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε

    //NMAIN <mainbody> ::= main <slist> begin <stats> end CD23 <id>

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>

    //NRTYPE <type> ::= <structid> is <fields> end

    //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>

    //NSDECL <sdecl> ::= <id> : <stype>

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>

    //NARRD <arrdecl> ::= <id> : <typeid>

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>

    //Special <rtype> ::= <stype> | void

    //Special <plist> ::= <params> | ε

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>

    //NSIMP <param> ::= <sdecl>

    //NARRP <param> ::= <arrdecl>

    //NARRC <param> ::= const <arrdecl>

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