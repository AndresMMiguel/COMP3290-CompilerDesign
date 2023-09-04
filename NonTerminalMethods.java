

public class NonTerminalMethods {
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