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
        Stack<Token> tempStack = new Stack<Token>();
        while(arrayLength >= 0){
            tempStack.push(tokenList.get(arrayLength));
            arrayLength= arrayLength-1;
        }
        tokenStack = tempStack;
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
//            if(currentToken.equals()){ //functions
//                root.setMIddle(nFuncs());
//                updateTokens();
//            }
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
        //enter codbase here---------------------------------------------------
        return NINIT;
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
    private SyntaxNode special(){
        SyntaxNode SPECIAL = new SyntaxNode( , );
        //enter codbase here---------------------------------------------------
        return SPECIAL;
    }

    //NFUNCS <funcs> ::= <func> <funcs>
    //Special <funcs> ::= ε
    private static SyntaxNode nFuncs(){
        SyntaxNode NFUNCS = new SyntaxNode("NFUNCS", "NFUNCS");

        //enter codebase for nfuncs here-------------------------

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
    }

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private static SyntaxNode nTypel(){
        SyntaxNode NTYPEL = new SyntaxNode( , );
        //enter codbase here---------------------------------------------------
        return NTYPEL;
    }

    //NRTYPE <type> ::= <structid> is <fields> end
    private static SyntaxNode nRType(){
        SyntaxNode NRTYPE = new SyntaxNode( , );
        //enter codbase here---------------------------------------------------
        return NRTYPE;
    }

    //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
    private static SyntaxNode nAType(){
        SyntaxNode NATYPE = new SyntaxNode( , );
        //enter codbase here---------------------------------------------------
        return NATYPE;
    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private static SyntaxNode nFList(){

    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private static SyntaxNode nSdecl(){
        SyntaxNode NSDECL = new SyntaxNode(currentToken.getLexeme(), "NSDECL");
        updateTokens();
        //currentToken = TCOLN    lookAhead = type token
        //error if not in this order!!!!
        //create symbol and add to table/hashmap------------------------------------------
        updateTokens();
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
    private static SyntaxNode nPList(){
        SyntaxNode NPLIST = new SyntaxNode( , );

        return NPLIST;
    }
    
    //NSIMP <param> ::= <sdecl>
    private static SyntaxNode nSimp(){
        SyntaxNode NSIMP = new SyntaxNode( , );

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
        }
        if(currentToken.getTokenEnumString().equals("TOUTP")){
            if(currentToken.getTokenEnumString().equals("TLSLS")){
                NSTATS.setRight(nOutl());
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