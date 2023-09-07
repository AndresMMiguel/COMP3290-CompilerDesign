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


    public static void transferTokensToStack(ArrayList<Token> tokenList){
        int arrayLength = tokenList.size();
        arrayLength = arrayLength-1;
        while(arrayLength >= 0){
            tokenStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
    }

    //updates current token
    private static void updateCurrnetToken(){
        currentToken = tokenStack.pop();
    }

    //updates look ahead token
    private static void peekNextToken(){
        lookAheadToken = tokenStack.peek();
    }

    //updates both current and peek tokens
    private static void updateTokens(){
        updateCurrnetToken();
        peekNextToken();
    }
    
    //begining method of parsing non terminal methods
    public static Map<String, SymbolForTable> superMethod(){
        updateCurrnetToken();
        peekNextToken();
        nProg();
        return symbolTable;
    }

    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private static void nProg(){
        if(currentToken.equals("CD23")){
            root = new SyntaxNode("CD23", "NPROG");
            updateCurrnetToken();
            if(currentToken.equals()){ //globals
                root.setLeft(nGlob());
                updateTokens();
            }
            if(currentToken.equals()){ //functions
                root.setMIddle(nFuncs());
                updateTokens();
            }
            if(currentToken.equals("TMAIN")){ //main
                root.setRight(nMain());
                updateTokens();
            }
        }
        else{
            //error
        }
    }

    //NGLOB <globals> ::= <consts> <types> <arrays>
    private static SyntaxNode nGlob(){

    }

    //NILIST <initlist> ::= <init> , <initlist>
    //Special <initlist> ::= <init>
    private SymbolForTable nIList(){

    }

    //NINIT <init> ::= <id> is <expr>
    private SymbolForTable nINit(){

    }

    //Special <rtype> ::= <stype> | void
    //Special <plist> ::= <params> | ε
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
    private SymbolForTable special(){

    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private static SyntaxNode nFuncs(){

    }

    //NMAIN <mainbody> ::= main <slist> begin <stats> end CD23 <id>
    private static SyntaxNode nMain(){
        SyntaxNode main = new SyntaxNode("main", "NMAIN");
        updateCurrnetToken();
        if(currentToken.equals("TIDEN")){//NSDLST
            main.setLeft(nSdlst());
            updateTokens();
        }
        if(currentToken.equals("TBEGN")){//NSTATS
            main.setRight(nStats());
            updateTokens();
        }
        return main;
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>
    private static SyntaxNode nSdlst(){
        SyntaxNode NSDLST = new SyntaxNode("NSDLST", "NSDLST");
        if(currentToken.equals()){  //NSDECL
            NSDLST.setLeft(nSdecl());
            updateTokens();
        }
        if(currentToken.equals()){  //NSDECL
            NSDLST.setMiddle(nSdecl());
            updateTokens();
        }
        if(currentToken.equals("TIDEN")){ //NSDLST
            NSDLST.setRight(nSdlst());
            updateTokens();
        }
        return NSDLST;
    }

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private static SyntaxNode nTypel(){

    }

    //NRTYPE <type> ::= <structid> is <fields> end
    private static SyntaxNode nRType(){

    }

    //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
    private static SyntaxNode nAType(){

    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private static SyntaxNode nFList(){

    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private static SyntaxNode nSdecl(){
        SyntaxNode NSDECL = new SyntaxNode("NSDLST", "NSDECL");
        //create symbol and add to table/hashmap------------------------------------------
        return NSDECL;
    }

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    private static SyntaxNode nAList(){

    }

    //NARRD <arrdecl> ::= <id> : <typeid>
    private static SyntaxNode nAarrd(){

    }

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>
    private static SyntaxNode nFund(){

    }

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>
    private static SyntaxNode nPList(){

    }
    
    //NSIMP <param> ::= <sdecl>
    private static SyntaxNode nSimp(){

    }

    //NARRP <param> ::= <arrdecl>
    private static SyntaxNode nArrp(){

    }

    //NARRC <param> ::= const <arrdecl>
    private static SyntaxNode nArrc(){

    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>
    private static SyntaxNode nDList(){

    }

    //NSTATS <stats> ::= <stat> ; <stats> | <strstat> <stats>
    //Special <stats> ::= <stat>; | <strstat>
    private static SyntaxNode nStats(){
        SyntaxNode NSTATS = new SyntaxNode("NSTATS", "NSTATS");
        //create symbol and add to table/hashmap------------------------------------------
        if(currentToken.equals("TINPT")){
            if(lookAheadToken.equals("TGRGR")){
                NSTATS.setLeft(nSimv());
            }
            else{
                //error
            }
        }
        return NSTATS;
    }

    private static SyntaxNode nSimv(){
        SyntaxNode NSIMV = new SyntaxNode("", "NSIMV");
        //create symbol and add to table/hashmap------------------------------------------
        return NSIMV;
    }

    //NFORL <forstat> ::= for ( <asgnlist> ; <bool> ) <stats> end
    private static SyntaxNode nArrc(){

    }

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>
    //Special <asgnlist> ::= <alist> | ε
    private static SyntaxNode nRept(){

    }

    //NASGNS <alist> ::= <asgnstat> , <alist>
    //Special <alist> ::= <asgnstat>
    private static SymbolForTable nAsgns(){

    }

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end
    private static SymbolForTable nIft_(){

    }

    //NASGN <asgnop> ::= =
    //NPLEQ <asgnop> ::= +=
    //NMNEQ <asgnop> ::= -=
    //NSTEA <asgnop> ::= *=
    //NDVEQ <asgnop> ::= /=
    private static SymbolForTable asgnOp(){

    }

    //NINPUT <iostat> ::= In >> <vlist>
    private static SymbolForTable nInput(){

    }

    //NOUTP <iostat> ::= Out << <prlist>
    //NOUTL <iostat> ::= Out << Line
    //NOUTL <iostat> ::= Out << <prlist> << Line
    private static SymbolForTable nOut_(){

    }

//------------------------------------------------------------------^^cameron^^---------------------------------------------------
//-------------------------->>Andres<<------------------------------------------------------------------------------------------------
    //NCALL <callstat> ::= <id> ( <elist> ) | <id> ( )

    //NRETN <returnstat> ::= return void | return <expr>

    //NVLIST <vlist> ::= <var> , <vlist>
    //Special <vlist> ::= <var>

    //NSIMV <var> ::= <id>
    //NARRV <var> ::= <id>[<expr>] . <id>
    //NAELT <var> ::= <id>[<expr>]

    //NEXPL <elist> ::= <bool> , <elist>
    //Special <elist> ::= <bool>

    //NBOOL <bool> ::= <bool><logop> <rel>
    //Special <bool> ::= <rel>

    //NNOT <rel> ::= ! <expr> <relop> <expr>
    //Special <rel> ::= <expr> <relop><expr>
    //Special <rel> ::= <expr>

    //NAND <logop> ::= &&

    //NOR <logop> ::= ||

    //NXOR <logop> ::= &|

    //NEQL <relop> ::= ==

    //NNEQ <relop> ::= !=

    //NGRT <relop> ::= >
    //NGEQ <relop> ::= >=

    //NLSS <relop> ::= <
    //NLEQ <relop> ::= <=

    //NADD <expr> ::= <expr> + <term>
    //NSUB <expr> ::= <expr> - <term>

    //Special <expr> ::= <term>
    //NMUL <term> ::= <term> * <fact>
    //NDIV <term> ::= <term> / <fact>
    //NMOD <term> ::= <term> % <fact>
    //Special <term> ::= <fact>

    //NPOW <fact> ::= <fact> ^ <exponent>
    //Special <fact> ::= <exponent>

    //Special <exponent> ::= <var>

    //NILIT <exponent> ::= <intlit>

    //NFLIT <exponent> ::= <reallit>

    //Special <exponent> ::= <fncall>

    //NTRUE <exponent> ::= true

    //NFALS <exponent> ::= false

    //Special <exponent> ::= ( <bool> )

    //NFCALL <fncall> ::= <id> ( <elist> ) | <id> ( )

    //NPRLST <prlist> ::= <printitem> , <prlist>
    //Special <prlist> ::= <printitem>

    //Special <printitem> ::= <expr>

    //NSTRG <printitem> ::= <string>
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