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
    private static SyntaxNode root;


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

        //updates current token if condition satisfied
        private static void match(String token){
            if (lookAheadToken.getTokenEnumString().equals(token)){
                updateTokens();
            }else{
                System.out.println("Error: Expected token " + token + " in line: " + lookAheadToken.getLineNumber());
                //Call to error function
            }
        
        }

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
    
        public SyntaxNode superMethod (){
            nprog();
            return root;
        }


    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private void nprog(){
        root = new SyntaxNode("NUNDF", null, null);
            match("TCD23");
            match("TIDEN");
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
        SyntaxNode initNode = init();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode temp = new SyntaxNode("NILIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, initNode);
            createChild(parent.getListLastNode(parent, "NILIST"), temp);
            initlist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NILIST"), initNode);
        }
        return parent;
    }

    //NINIT <init> ::= <id> is <expr>
    private SyntaxNode init(){
        match("TIDEN");
        SyntaxNode initNode = new SyntaxNode("NINIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TTTIS");
        initNode = const_lit(initNode);
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
        SyntaxNode funcNode = func();
        if (lookAheadToken.getTokenEnumString().equals("TFUNC")){
            SyntaxNode temp = new SyntaxNode("NFUNCS", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, funcNode);
            createChild(parent.getListLastNode(parent, "NFUNCS"), temp);
            funcs(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NFUNCS"), funcNode);
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
        createChild(parent, mainNode);
        return parent;
    }

    //NSDLST <slist> ::= <sdecl> , <slist>
    //Special <slist> ::= <sdecl>
    private SyntaxNode slist(SyntaxNode parent){
        SyntaxNode sdeclNode = sdecl();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode sdlistNode = new SyntaxNode("NSDLST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(sdlistNode, sdeclNode);
            createChild(parent.getListLastNode(parent, "NSDLST"), sdlistNode);
            slist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NSDLST"), sdeclNode);
        }
        return parent;
    }

    //NTYPEL <typelist> ::= <type> <typelist>
    //Special <typelist> ::= <type>
    private SyntaxNode typelist(SyntaxNode parent){
        SyntaxNode typeNode = type();
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            SyntaxNode temp = new SyntaxNode("NTYPEL", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, typeNode);
            createChild(parent.getListLastNode(parent, "NTYPEL"), temp);
            typelist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NTYPEL"), typeNode);
        }
        return parent;
    }

    private SyntaxNode type(){
        SyntaxNode typeNode = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            match("TIDEN");
            match("TTTIS");
            if (lookAheadToken.getTokenEnumString().equals("TARAY")){
                match("TARAY");
                typeNode = new SyntaxNode("NATYPE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                match("TLBRK");
                typeNode = expr(typeNode);
                match("TRBRK");
                match("TTTOF");
                match("TIDEN");
                match("TTEND");
            }else{
                typeNode = new SyntaxNode("NRTYPE", currentToken.getLexeme(), currentToken.getTokenEnumString());
                typeNode = fields(typeNode);
                match("TTEND");
            }
        }
        return typeNode;
    }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private SyntaxNode fields(SyntaxNode parent){
        declPrefix();
        SyntaxNode sdeclNode = sdecl();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode temp = new SyntaxNode("NFLIST",  currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, sdeclNode);
            createChild(parent.getListLastNode(parent, "NFLIST"), temp);
            fields(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NFLIST"), sdeclNode);
        }
        return parent;
    }

    private void declPrefix (){
        match("TIDEN");
        match("TCOLN");
        // STORE THIS IDENTIFIERS IN THE SYMBOL TABLE
    }

    //NSDECL <sdecl> ::= <id> : <stype>
    private SyntaxNode sdecl(){
        stype();
        SyntaxNode temp = new SyntaxNode("NSDECL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        return temp;
    }

    //NALIST <arrdecls> ::= <arrdecl> , <arrdecls>
    //Special <arrdecls> ::= <arrdecl>
    private SyntaxNode arrdecls(SyntaxNode parent){
        declPrefix();
        SyntaxNode declNode = arrdecl();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode listNode = new SyntaxNode("NALIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(listNode, declNode);
            createChild(parent.getListLastNode(parent, "NALIST"), listNode);
            arrdecls(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NALIST"), declNode);
        }
        return parent;
    }

    //NARRD <arrdecl> ::= <id> : <typeid>
    private SyntaxNode arrdecl(){
        match("TIDEN");
        // STORE THIS ARRAY TYPE IN THE SYMBOL TABLE
        SyntaxNode temp = new SyntaxNode("NARRD", currentToken.getLexeme(), currentToken.getTokenEnumString());
        return temp;
    }

    //NFUND <func> ::= func <id> ( <plist> ) : <rtype> <funcbody>
    private SyntaxNode func(){
        match("TFUNC");
        match("TIDEN");
        // STORE THIS FUNCTION IDENTIFIER IN THE SYMBOL TABLE
        SyntaxNode funcNode = new SyntaxNode("NFUND", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        funcNode = plist(funcNode);
        match("TRPAR");
        match("TCOLN");
        rtype();
        funcNode = funcbody(funcNode);
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
    private SyntaxNode plist(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN") ||
        lookAheadToken.getTokenEnumString().equals("TCNST")){
            parent = params(parent);
        }
        return parent;
    }

    //NPLIST <params> ::= <param> , <params>
    //Special <params> ::= <param>
    private SyntaxNode params(SyntaxNode parent){
        SyntaxNode paramNode = param();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode temp = new SyntaxNode("NPLIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, paramNode);
            createChild(parent.getListLastNode(parent, "NPLIST"), temp);
            params(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NPLIST"), paramNode);
        }
        return parent;
    }

    //NSIMP <param> ::= <sdecl>
    //NARRP <param> ::= <arrdecl>
    //NARRC <param> ::= const <arrdecl>
    private SyntaxNode param(){
        SyntaxNode paramNode;
        if (lookAheadToken.getTokenEnumString().equals("TCNST")){       //NARRC <param> ::= const <arrdecl>
            match("TCNST");
            paramNode = new SyntaxNode("NARRC", currentToken.getLexeme(), currentToken.getTokenEnumString());
            declPrefix();
            SyntaxNode temp = arrdecl();
            createChild(paramNode, temp);
        }else{
            declPrefix();
            if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
                match("TIDEN");
                paramNode = new SyntaxNode("NARRP", currentToken.getLexeme(), currentToken.getTokenEnumString());
                declPrefix();
                SyntaxNode temp = arrdecl();
                createChild(paramNode, temp);
            }else{
                paramNode = new SyntaxNode("NSIMP", currentToken.getLexeme(), currentToken.getTokenEnumString());
                SyntaxNode temp = sdecl();
                createChild(paramNode, temp);
            }
        }
        return paramNode;
    }

    //Special <funcbody> ::= <locals> begin <stats> end
    private SyntaxNode funcbody (SyntaxNode parent){
        parent = locals(parent);
        match("TBEGN");
        parent = stats(parent); 
        match("TTEND");
        return parent;
    }

    //Special <locals> ::= <dlist> | ε
    private SyntaxNode locals(SyntaxNode parent){
        if (!lookAheadToken.getTokenEnumString().equals("TBEGN")){
            parent = dlist(parent);
        }
        return parent;
    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>
    private SyntaxNode dlist(SyntaxNode parent){
        SyntaxNode declNode = decl();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            SyntaxNode dlistNode = new SyntaxNode("NDLIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(dlistNode, declNode);
            createChild(parent.getListLastNode(parent, "NDLIST"), dlistNode);
            dlist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NDLIST"), declNode);
        }
        return parent;
    }

    //Special <decl> ::= <sdecl> | <arrdecl>
    private SyntaxNode decl(){
        declPrefix();
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            temp = arrdecl();
        }else{
            temp = sdecl();
        }
        return temp;
    }

    //Special <stype> ::= integer | real | boolean
    private void stype(){
        if (lookAheadToken.getTokenEnumString().equals("TINTG") ||
        lookAheadToken.getTokenEnumString().equals("TREAL") ||
        lookAheadToken.getTokenEnumString().equals("TTRUE") ||
        lookAheadToken.getTokenEnumString().equals("TFALS")){
            updateTokens();
            // STORE THIS IDENTIFIER DECLARATION TYPE IN THE SYMBOL TABLE (and link it to its identifier)
        }else{
            System.out.println("Error: Expected an integer, real or boolean in line: " + currentToken.getLineNumber());
        }
    }

    //NSTATS <stats> ::= <stat> ; <stats> | <strstat> <stats>
    //Special <stats> ::= <stat>; | <strstat>
    private SyntaxNode stats(SyntaxNode parent){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        // If strstat
        if (lookAheadToken.getTokenEnumString().equals("TTFOR") ||
        lookAheadToken.getTokenEnumString().equals("TIFTH")){
            temp = strstat(temp);
        // If stat
        }else{
            temp = stat(temp);
            match("TSEMI");
        }
        if (!lookAheadToken.getTokenEnumString().equals("TTEND")){
                SyntaxNode statsNode = new SyntaxNode("NSTATS", currentToken.getLexeme(), currentToken.getTokenEnumString());
                statsNode.copyChildren(temp, statsNode);
                createChild(parent.getListLastNode(parent, "NSTATS"), statsNode);
                stats(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NSTATS"), temp.getLeft());
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
        SyntaxNode forNode = new SyntaxNode("NFORL", null, null);
        match("TLPAR");
        forNode = asgnlist(forNode);
        match("TSEMI");
        createChild(forNode, bool());
        match("TRPAR");
        forNode = stat(forNode);
        match("TTEND");
        createChild(parent, forNode);
        return parent;
    }

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>
    private SyntaxNode repstat(SyntaxNode parent){
        match("TREPT");
        SyntaxNode reptNode = new SyntaxNode("NREPT", null, null);
        match("TLPAR");
        reptNode = asgnlist(reptNode);
        match("TRPAR");
        reptNode = stat(reptNode);
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
        SyntaxNode asgnNode = asgnstat();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode alistNode = new SyntaxNode("NASGNS", null, null);
            createChild(alistNode, asgnNode);
            createChild(parent.getListLastNode(parent, "NASGNS"), alistNode);
            alist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NASGNS"), asgnNode);
        }
        return parent;
    }

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end
    private SyntaxNode ifstat(SyntaxNode parent){
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
        match("TELSE");
        return parent;
    }

    //Special <asgnstat> ::= <var> <asgnop> <bool>
    private SyntaxNode asgnstat(){
        SyntaxNode varNode = var();
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

            case "TPEQL":
                match("TPEQL");
                temp = new SyntaxNode("NPLEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TMNEQ":
                match("TMNEQ");
                temp = new SyntaxNode("NMNEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            case "TSTEQ":
                match("TSTEQ");
                temp = new SyntaxNode("NSTEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());

            case "TDVEQ":
                match("TDVEQ");
                temp = new SyntaxNode("NDVEQ", currentToken.getLexeme(), currentToken.getTokenEnumString());
            break;

            default:
            //Error
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
        SyntaxNode callNode = new SyntaxNode("NCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        if (!lookAheadToken.getTokenEnumString().equals("TRPAR")){
            callNode = elist(callNode);
        }
        match("TRPAR");
        createChild(parent, callNode);
        return parent;
    }

    //NRETN <returnstat> ::= return void | return <expr>
    private SyntaxNode returnstat(SyntaxNode parent){
        match("TRETN");
        SyntaxNode retNode = new SyntaxNode("NRETN", currentToken.getLexeme(), currentToken.getTokenEnumString());
        if(!lookAheadToken.getTokenEnumString().equals("TVOID")){
            retNode = expr(retNode);
            // STORE THIS RETURN TYPE IN THE SYMBOL TABLE
        }else{
            // STORE VOID RETURN TYPE IN THE SYMBOL TABLE
        }
        createChild(parent, retNode);
        return parent;
    }

    //NVLIST <vlist> ::= <var> , <vlist>
    private SyntaxNode vlist(SyntaxNode parent){
        match("TIDEN");
        SyntaxNode temp = var();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            SyntaxNode vlistNode = new SyntaxNode("NVLIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(vlistNode, temp);
            createChild(parent.getListLastNode(parent, "NVLIST"), vlistNode);
            vlist(parent);
        }else{  // (Special <vlist> ::= <var>)
            createChild(parent.getListLastNode(parent, "NVLIST"), temp);
        }
        return parent;
    }

    private SyntaxNode var(){
        SyntaxNode varNode = new SyntaxNode("NSIMV", currentToken.getLexeme(), currentToken.getTokenEnumString());
        // match("TIDEN");
        if (lookAheadToken.getTokenEnumString().equals("TLBRK")){
            match("TLBRK");
            SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
            temp = expr(temp);
            match("TRBRK");
            if(lookAheadToken.getTokenEnumString().equals("TDOTT")){
                match("TDOTT");
                varNode =  new SyntaxNode("NARRV", currentToken.getLexeme(), currentToken.getTokenEnumString());
                varNode.copyChildren(temp, varNode);
                match("TIDEN");
            }else{
                varNode = new SyntaxNode("NAELT", currentToken.getLexeme(), currentToken.getTokenEnumString());
                varNode.copyChildren(temp, varNode);
            }
        }
        return varNode;
    }

    //NEXPL <elist> ::= <bool> , <elist>
    private SyntaxNode elist(SyntaxNode parent){
        SyntaxNode boolNode = bool();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode temp = new SyntaxNode("NEXPL", currentToken.getLexeme(), currentToken.getTokenEnumString());
            createChild(temp, boolNode);
            createChild(parent.getListLastNode(parent, "NEXPL"), temp);
            elist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NEXPL"), boolNode);
        }
        return parent;
    }

    //NBOOL <bool> ::= <bool> <logop> <rel>
    private SyntaxNode bool(){
        SyntaxNode boolNode = new SyntaxNode("NBOOL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        boolNode = bool2(boolNode);
        return boolNode;
    }

    private SyntaxNode bool2(SyntaxNode parentNode){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        SyntaxNode relNode = rel(temp);
        if(lookAheadToken.getTokenEnumString().equals("TTAND") ||
        lookAheadToken.getTokenEnumString().equals("TTTOR") ||
        lookAheadToken.getTokenEnumString().equals("TTXOR")){
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
    private SyntaxNode rel(SyntaxNode parent){
        SyntaxNode relNode = new SyntaxNode("NUNDF", null, null);
        if(lookAheadToken.getTokenEnumString().equals("TNOTT")){
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
            lookAheadToken.getTokenEnumString().equals("TLEQ") ||
            lookAheadToken.getTokenEnumString().equals("TGEQL")){
                SyntaxNode relopNode = optRel();
                relopNode.copyChildren(exprTemp, relopNode);
                relopNode = expr(relopNode);
                createChild(parent, relopNode); 
            }else{
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

            case "TLEQ":
                match("TLEQ");
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
        if(lookAheadToken.getTokenEnumString().equals("^")){    //NPOW <fact> ::= <fact> ^ <exponent>
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
    private SyntaxNode fncall(){
        match("TIDEN");
        SyntaxNode fnNode = new SyntaxNode("NFCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        match("TLPAR");
        if (!lookAheadToken.getTokenEnumString().equals("TRPAR")){
            fnNode = elist(fnNode);
        }
        match("TRPAR");
        return fnNode;
    }

    //NPRLST <prlist> ::= <printitem> , <prlist>
    //Special <prlist> ::= <printitem>)
    private SyntaxNode prlist(SyntaxNode parent){
        SyntaxNode temp = new SyntaxNode("NUNDF", null, null);
        temp = printitem(temp);
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            match("TCOMA");
            SyntaxNode prlistNode = new SyntaxNode("NPRLS", currentToken.getLexeme(), currentToken.getTokenEnumString());
            prlistNode.copyChildren(temp, prlistNode);
            createChild(parent.getListLastNode(parent, "NPRLST"), prlistNode);
            prlist(parent);
        }else{
            createChild(parent.getListLastNode(parent, "NPRLST"), temp.getLeft());
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