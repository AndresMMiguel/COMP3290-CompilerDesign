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
import java.util.List;

import javax.swing.plaf.synth.SynthPasswordFieldUI;

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
    private static HashMap<String, ArrayList<SymbolForTable>> symbolTable = new HashMap<>();
    private static SyntaxNode root;
    private static SyntaxNode node;



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

        //updates next tokens based on the array
        private static void updateTokens(String[]array){
            for(int i = 0; i < array.length; i++){
                if (lookAheadToken.getTokenEnumString().equals(array[i])){
                    updateTokens();
                }else{
                    System.out.println("Error: expected token " + array[i]);
                    break;
                }
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

        private static void createBaseSymbolTable(){
            symbolTable.put("integer", new ArrayList<SymbolForTable>());
            symbolTable.put("real", new ArrayList<SymbolForTable>());
            symbolTable.put("boolean", new ArrayList<SymbolForTable>());
            symbolTable.put("function", new ArrayList<SymbolForTable>());
        }

        private static void createSymbolForTable(String tokenType){
            SymbolForTable temp;
            if(tokenType.equals("TINTG")){
                temp = new SymbolForTable(currentToken.getLexeme(), currentToken.getLineNumber(), currentToken.getColumnNumber(), "integer", currentToken.getLexeme());
            }
            else if(tokenType.equals("TREAL")){
                temp = new SymbolForTable(currentToken.getLexeme(), currentToken.getLineNumber(), currentToken.getColumnNumber(), "real", currentToken.getLexeme());
            }
            else if(tokenType.equals("TFUNC")){
                temp = new SymbolForTable(currentToken.getLexeme(), currentToken.getLineNumber(), currentToken.getColumnNumber(), "func", currentToken.getLexeme(), null);
            }
            else{
                temp = new SymbolForTable(currentToken.getLexeme(), currentToken.getLineNumber(), currentToken.getColumnNumber(), "boolean", currentToken.getLexeme());
            }
            symbolTable.get(temp.getType()).add(temp);
        }
    
        public SyntaxNode superMethod (){
            createBaseSymbolTable();
            nprog();
            return root;
        }


    //NPROG <program> ::= CD23 <id> <globals> <funcs> <mainbody>
    private void nprog(){
        root = new SyntaxNode("NUNDF", null, null);
        if(lookAheadToken.getTokenEnumString().equals("TCD23")){
            match("TCD23");
            match("TIDEN");
            root = new SyntaxNode("NPROG", currentToken.getLexeme(), currentToken.getTokenEnumString());
            root = globals(root);
            // root = funcs(root);
            // root = mainbody(root);
        }else{
            System.out.println("Error: Expected an identifier in line: " + lookAheadToken.getLineNumber() + " column: " + lookAheadToken.getColumnNumber());
        }
    }
    
    //NGLOB <globals> ::= <consts> <types> <arrays>
    private SyntaxNode globals(SyntaxNode parent){
        if(lookAheadToken.getTokenEnumString().equals("TCONS") ||           // if there are globals
        lookAheadToken.getTokenEnumString().equals("TTYPS") ||
        lookAheadToken.getTokenEnumString().equals("TARRS")){
                SyntaxNode globalNode = new SyntaxNode("NGLOB", lookAheadToken.getLexeme(), lookAheadToken.getTokenEnumString());
                globalNode = consts(globalNode);
                globalNode = types(globalNode);
                createChild(parent, globalNode);
                // node = arrays(node);
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

    // private void initlist(){
    //     init();
    //     if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
    //         match("TCOMA");
    //         SyntaxNode temp = new SyntaxNode("NILIST", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //         createChild(temp, node);
    //         node = root.getListLastNode(root, "NGLOB");
    //         createChild(node.getListLastNode(node, "NILIST"), temp);
    //         initlist();
    //     }else{
    //         SyntaxNode temp = node;
    //         node = root.getListLastNode(root, "NGLOB");
    //         createChild(node.getListLastNode(node, "NILIST"), temp);
    //     }
    // }

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
    // private void init(){
    //     match("TIDEN");
    //     node = new SyntaxNode("NINIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //     match("TTTIS");
    //     const_lit();
    // }

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

    // private void const_lit(){
    //     SyntaxNode temp;
    //     switch (lookAheadToken.getTokenEnumString()){
    //         case "TILIT":
    //             match("TILIT");
    //             temp = new SyntaxNode("NILIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //             createChild(node, temp);
    //         break;

    //         case "TFLIT":
    //             match("TFLIT");
    //             temp = new SyntaxNode("NFLIT", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //             createChild(node, temp);
    //         break;

    //         case "TTRUE":
    //             match("TTRUE");
    //             temp = new SyntaxNode("NTRUE", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //             createChild(node, temp);
    //         break;

    //         case "TFALS":
    //             match("TFALS");
    //             temp = new SyntaxNode("NFALS", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //             createChild(node, temp);
    //         break;
    //     }
    // }

    //Special <types> ::= types <typelist> | ε
    private SyntaxNode types(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TTYPS")){
            match("TTYPS");
            parent = typelist(parent);
        }
        return parent;
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

    // private SyntaxNode type(){
    //     if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
    //         updateTokens();
    //         if (lookAheadToken.getTokenEnumString().equals("TTTIS")){
    //             String type = currentToken.getLexeme();
    //             String lexeme = currentToken.getTokenEnumString();
    //             updateTokens();
    //             if (lookAheadToken.getTokenEnumString().equals("TARAY")){   //NATYPE <type> ::= <typeid> is array [ <expr> ] of <structid> end
    //                 updateTokens();
    //                 node = new SyntaxNode("NATYPE", lexeme, type);
    //                 if (lookAheadToken.getTokenEnumString().equals("TLBRK")){
    //                     updateTokens();
    //                     expr(node);
    //                     if (lookAheadToken.getTokenEnumString().equals("TRBRK")){
    //                         updateTokens();
    //                         if (lookAheadToken.getTokenEnumString().equals("TTTOF")){
    //                             updateTokens();
    //                             if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
    //                                 updateTokens();
    //                                 // Symbol table: structid of the array
    //                                 if (lookAheadToken.getTokenEnumString().equals("TTEND")){
    //                                     updateTokens();
    //                                 }// Error
    //                             }// Error
    //                         }// Error
    //                     }// Error
    //                 }// Error
    //                 return node;
    //             }else{                      //NRTYPE <type> ::= <structid> is <fields> end
    //                 node = new SyntaxNode("NRTYPE", lexeme, type);
    //                 fields(node);
    //                 if (lookAheadToken.getTokenEnumString().equals("TTEND")){
    //                     updateTokens();
    //                     return node;
    //                 }
    //             }        
    //         }
    //     }
    //     node = new SyntaxNode("NUNDF", null, null);
    //     return node;
    // }

    //NFLIST <fields> ::= <sdecl> , <fields>
    //Special <fields> ::= <sdecl>
    private SyntaxNode fields(SyntaxNode parent){
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

    // private String[] declPrefix (){
    //     String[]tokenInfo = new String[2];
    //     if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
    //         updateTokens();
    //         if(lookAheadToken.getTokenEnumString().equals("TCOLN")){
    //             tokenInfo[0] = currentToken.getLexeme();
    //             tokenInfo[1] = currentToken.getTokenEnumString();
    //             updateTokens();
    //         }//Error
    //     }//Error
    //     return tokenInfo;
    // }

    //NSDECL <sdecl> ::= <id> : <stype>
    private SyntaxNode sdecl(){
        match("TIDEN");
        match("TCOLN");
        stype();
        SyntaxNode temp = new SyntaxNode("NSDECL", currentToken.getLexeme(), currentToken.getTokenEnumString());
        return temp;
    }

    // private SyntaxNode sdecl(String[]identInfo){
    //     node = new SyntaxNode("NUNDF", null, null);
    //     if (lookAheadToken.getTokenEnumString().equals("TINT") ||
    //     lookAheadToken.getTokenEnumString().equals("TREAL") ||
    //     lookAheadToken.getTokenEnumString().equals("TBOOL")){
    //         node = new SyntaxNode("NSDECL", identInfo[0], identInfo[1]);
    //         stype();
    //     }
    //     return node;
    // }

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
                            funcbody(node);
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
            if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
                node = new SyntaxNode("NARRP", null, null);
                SyntaxNode temp = arrdecl(declPrefix());
                createChild(node, temp);
            }else{
                node = new SyntaxNode("NSIMP", null, null);
                SyntaxNode temp = sdecl(declPrefix());
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
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            dlist(parent);
        }
    }

    //NDLIST <dlist> ::= <decl> , <dlist>
    //Special <dlist> ::= <decl>
    private void dlist(SyntaxNode parent){
        node = decl();
        optDlist(parent, node);
    }

    //Special <decl> ::= <sdecl> | <arrdecl>
    private SyntaxNode decl(){
        String[]infoIdent = declPrefix();
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            node = arrdecl(infoIdent);
        }else{
            node = sdecl(infoIdent);
        }
        return node;
    }

    private void optDlist(SyntaxNode parent, SyntaxNode child){
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            node = new SyntaxNode("NDLIST", null, null);
            createChild(parent, node);
            createChild(node, child);
            dlist(node);
        }
        createChild(parent, child);
    }

    //Special <stype> ::= integer | real | boolean
    private void stype(){
        if (lookAheadToken.getTokenEnumString().equals("TINTG") ||
        lookAheadToken.getTokenEnumString().equals("TREAL") ||
        lookAheadToken.getTokenEnumString().equals("TTRUE") ||
        lookAheadToken.getTokenEnumString().equals("TFALS")){
            updateTokens();
            // Symbol table leaf value
        }else{
            // Error
        }
    }

    //NSTATS <stats> ::= <stat> ; <stats> | <strstat> <stats>
    //Special <stats> ::= <stat>; | <strstat>
    private void stats(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TTFOR") ||
        lookAheadToken.getTokenEnumString().equals("TIFTH")){
            node = strstat();
            optStat(parent, node);
        }else{
            node = stat();
            optStat(parent, node);
        }
    }

    private void optStat(SyntaxNode parent, SyntaxNode child){
        if (!lookAheadToken.getTokenEnumString().equals("TTEND")){
            node = new SyntaxNode("NSTATS", null, null);
            createChild(parent, node);
            createChild(node, child);
            stats(node);
        }
        createChild(parent, child);
    }

    //Special <strstat> ::= <forstat> | <ifstat>
    private SyntaxNode strstat(){
        node = new  SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TTFOR")){
            node = forstat();
        }else{
            node = ifstat();
        }
        return node;
    }

    //Special <stat> ::= <reptstat> | <asgnstat> | <iostat>
    //Special <stat> ::= <callstat> | <returnstat>
    private SyntaxNode stat(){
        node = new  SyntaxNode("NUNDF", null, null);
        switch (lookAheadToken.getTokenEnumString()){
            case "TREPT":   //Special <stat> ::= <reptstat>
                node = repstat();
            break;

            case "TIDEN":
                updateTokens();
                if (lookAheadToken.getTokenEnumString().equals("TLPAR")){   //Special <stat> ::= <callstat>
                    node = callstat();
                }else{      //Special <stat> ::= <asgnstat>
                    node = asgnstat();
                }
            break;

            case "TINPT":   //Special <stat> ::= <iostat>
                node = iostat();
            break;

            case "TOUTP":   //Special <stat> ::= <iostat>
                node = iostat();
            break;

            case "TRETN":   //Special <stat> ::= <returnstat>
                node = returnstat();
            break;

            default:
            break;
        }
        return node;
    }

    //NFORL <forstat> ::= for ( <asgnlist> ; <bool> ) <stats> end
    private SyntaxNode forstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TTFOR")){
            updateTokens();
            node = new SyntaxNode("NFORL", null, null);
            if (lookAheadToken.getTokenEnumString().equals("TLPAR")){
                updateTokens();
                asgnlist(node);
                if (lookAheadToken.getTokenEnumString().equals("TSEMI")){
                    updateTokens();
                    bool(node);
                    if (lookAheadToken.getTokenEnumString().equals("TRPAR")){
                        updateTokens();
                        stats(node);
                        if (lookAheadToken.getTokenEnumString().equals("TTEND")){
                            updateTokens();
                        }
                    }
                }// Error
            }// Error
        }// Error
        return node;
    }

    //NREPT <repstat> ::= repeat ( <asgnlist> ) <stats> until <bool>
    private SyntaxNode repstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TREPT")){
            updateTokens();
            node = new SyntaxNode("NREPT", null, null);
            if (lookAheadToken.getTokenEnumString().equals("TLPAR")){
                updateTokens();
                asgnlist(node);
                if (lookAheadToken.getTokenEnumString().equals("TRPAR")){
                    updateTokens();
                    stats(node);
                    if (lookAheadToken.getTokenEnumString().equals("TUNTL")){
                        updateTokens();
                        bool(node);
                    }// Error
                }// Error
            }// Error
        }
        return node;
    }

    //Special <asgnlist> ::= <alist> | ε
    private void asgnlist(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TIDEN")){
            alist(parent);
        }
    }

    //NASGNS <alist> ::= <asgnstat> , <alist>
    //Special <alist> ::= <asgnstat>
    private void alist(SyntaxNode parent){
        SyntaxNode temp = asgnstat();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NASGNS", null, null);
            createChild(parent, node);
            createChild(node, temp);
            alist(node);
        }
        createChild(parent, temp);
    }

    //NIFTH <ifstat> ::= if ( <bool> ) <stats> end
    //NIFTE <ifstat> ::= if ( <bool> ) <stats> else <stats> end
    private SyntaxNode ifstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TIFTH")){
            updateTokens();
            node  = new SyntaxNode("NIFTH", null, null);
            if (lookAheadToken.getTokenEnumString().equals("TLPAR")){
                updateTokens();
                bool(node);
                if (lookAheadToken.getTokenEnumString().equals("TRPAR")){
                    updateTokens();
                    stats(node);
                    optElse(node);
                    if (lookAheadToken.getTokenEnumString().equals("TTEND")){
                        updateTokens();
                    }// Error
                }// Error
            }// Error
        }
        return node;
    }

    private void optElse (SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals("TELSE")){
            updateTokens();
            node = new SyntaxNode("NIFTE", null, null);
            stats(node);
        }
    }

    //Special <asgnstat> ::= <var> <asgnop> <bool>
    private SyntaxNode asgnstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if (currentToken.getTokenEnumString().equals("TIDEN")){
            SyntaxNode temp = var();
            node = asgnop(temp);
            bool(node);
        }
        return node;
    }

    //NASGN <asgnop> ::= =
    //NPLEQ <asgnop> ::= +=
    //NMNEQ <asgnop> ::= -=
    //NSTEA <asgnop> ::= *=
    //NDVEQ <asgnop> ::= /=
    private SyntaxNode asgnop(SyntaxNode child){
        node = new SyntaxNode("NUNDF", null, null);
        switch (lookAheadToken.getTokenEnumString()){
            case "TEQUL":
                updateTokens();
                node = new SyntaxNode("NASGN", null, null);
                createChild(node, child);
            break;

            case "TPEQL":
                updateTokens();
                node = new SyntaxNode("NPLEQ", null, null);
                createChild(node, child);
            break;

            case "TMNEQ":
                updateTokens();
                node = new SyntaxNode("NMNEQ", null, null);
                createChild(node, child);
            break;

            case "TSTEQ":
                updateTokens();
                node = new SyntaxNode("NSTEQ", null, null);
                createChild(node, child);
            break;

            case "TDVEQ":
                updateTokens();
                node = new SyntaxNode("NDVEQ", null, null);
                createChild(node, child);
            break;

            default:
            //Error
            break;
        }
        return node;
    }

    //NINPUT <iostat> ::= In >> <vlist>
    //NOUTP <iostat> ::= Out << <prlist>
    //NOUTL <iostat> ::= Out << Line
    //NOUTL <iostat> ::= Out << <prlist> << Line
    private SyntaxNode iostat(){
        node = new SyntaxNode("NUNDF", null, null);
        if (lookAheadToken.getTokenEnumString().equals("TINPT")){
            updateTokens();
            node = new SyntaxNode("NINPUT", null, null);
            if (lookAheadToken.getTokenEnumString().equals("TGRGR")){
                updateTokens();
                vlist(node);
            }// Error
        }else{
            String[]array = {"TOUTP", "TLSLS"};
            updateTokens(array);
             if (lookAheadToken.getTokenEnumString().equals("TOUTL")){
                updateTokens();
                node = new SyntaxNode("NOUTL", null, null);
             }else{
                SyntaxNode temp = node;
                prlist(temp);
                if (lookAheadToken.getTokenEnumString().equals("TLSLS")){
                    updateTokens();
                    if (lookAheadToken.getTokenEnumString().equals("TLINE")){
                        updateTokens();
                        node = new SyntaxNode("NOUTL", null, null);
                        node.setLeft(temp.getLeft()); node.setMiddle(temp.getMiddle()); node.setRight(temp.getRight());
                    }else{
                        //Error
                    }
                }else{
                    node = new SyntaxNode("NOUTP", null, null);
                    node.setLeft(temp.getLeft()); node.setMiddle(temp.getMiddle()); node.setRight(temp.getRight());
                }
             }
            
        }
        // if (lookAheadToken.getTokenEnumString().equals("TOUTP")){
        //     updateTokens();
        //     if (lookAheadToken.getTokenEnumString().equals("TLSLS")){
        //         updateTokens();
        //         if (lookAheadToken.getTokenEnumString().equals("TOUTL")){
        //             updateTokens();
        //             node = new SyntaxNode("NOUTL", null, null);
        //         }else{
        //             prlist(node);
        //         }
        //     }
        // }
        return node;
    }

    //NCALL <callstat> ::= <id> ( <elist> ) | <id> ( ) is the same as NFCALL (already done)
    private SyntaxNode callstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if(currentToken.getTokenEnumString().equals("TIDEN")){
            // Store identifier in the symbol table
            if(lookAheadToken.getTokenEnumString().equals("(")){
                node = new SyntaxNode("NCALL", currentToken.getLexeme(), currentToken.getTokenEnumString());
                updateTokens();
                if(lookAheadToken.getTokenEnumString().equals("TNOTT") ||   // this is the <elist> path
                lookAheadToken.getTokenEnumString().equals("TIDEN") ||
                lookAheadToken.getTokenEnumString().equals("TILIT") ||
                lookAheadToken.getTokenEnumString().equals("TFLIT") ||
                lookAheadToken.getTokenEnumString().equals("TTRUE") ||
                lookAheadToken.getTokenEnumString().equals("TFALS") ||
                lookAheadToken.getTokenEnumString().equals("(")){
                    elist(node);
                }else if(lookAheadToken.getTokenEnumString().equals("TRPAR")){
                    updateTokens();
                }else{
                    System.out.println("Error: Expected a valid parameter");
                }
            }else{
                System.out.println("Error: Expected left parenthesis");
            }
        }else{
            System.out.println("Error: Expected an identifier");
        }
        return node;
    }

    //NRETN <returnstat> ::= return void | return <expr>
    private SyntaxNode returnstat(){
        node = new SyntaxNode("NUNDF", null, null);
        if(lookAheadToken.getTokenEnumString().equals("TRETN")){
            updateTokens();
            node = new SyntaxNode("NRETN", currentToken.getLexeme(), currentToken.getTokenEnumString());
            if(lookAheadToken.getTokenEnumString().equals("TVOID")){
                updateTokens();
                // Store TRETN in symbol table with void return.
            }else{
                // Store TRETN in symbol table with object return.
                expr(node);
            }
        }
        return node;
    }

    //NVLIST <vlist> ::= <var> , <vlist>
    private void vlist(SyntaxNode parent){
        SyntaxNode temp = var();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NVLIST", null, null);
            createChild(parent, node);
            createChild(node, temp);
            optVlist(node);
        }else{  // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
            createChild(parent, temp);
        }
    }

    private SyntaxNode var(){
        SyntaxNode varNode = new SyntaxNode("NUNDF", null, null);
        match("TIDEN");
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
        }else{
            varNode = new SyntaxNode("NSIMV", currentToken.getLexeme(), currentToken.getTokenEnumString());
        }
        return varNode;
    }

    // private SyntaxNode optExpr(){
    //     if (lookAheadToken.getTokenEnumString().equals("[")){
    //         updateTokens();
    //         SyntaxNode temp = new SyntaxNode("NAELT", null, null);
    //         expr(temp);
    //          if (lookAheadToken.getTokenEnumString().equals("]")){
    //             updateTokens();
    //             node = optId(temp);
    //          }else{
    //             System.out.println("Error: Expected a closing bracket in line: " + currentToken.getLineNumber());
    //          }
    //         return node;
    //     }else{  //NSIMV <var> ::= <id>
    //         node = new SyntaxNode("NSIMV", currentToken.getLexeme(), currentToken.getTokenEnumString());
    //         return node;
    //     }
    // }
    
    private SyntaxNode optId(SyntaxNode parent){
        if (lookAheadToken.getTokenEnumString().equals(".")){   //NARRV <var> ::= <id>[<expr>] . <id>
            updateTokens();
            if(lookAheadToken.getTokenEnumString().equals("TIDEN")){
                updateTokens();
                // Symbol table - identifier
                node = new SyntaxNode("NARRV", currentToken.getLexeme(), currentToken.getTokenEnumString());
                node.setLeft(parent.getLeft()); node.setMiddle(parent.getMiddle()); node.setRight(parent.getRight());
                return node;
            }else{
                System.out.println("Error: Expected an array identifier in line " + currentToken.getLineNumber());
                node = new SyntaxNode("NUNDF", null, null);
                return node;
            }
        }else{  //NAELT <var> ::= <id>[<expr>]
            node = new SyntaxNode("NAELT", currentToken.getLexeme(), currentToken.getTokenEnumString());
            node.setLeft(parent.getLeft()); node.setMiddle(parent.getMiddle()); node.setRight(parent.getRight());
            return node;
        }
    }  

    private void optVlist(SyntaxNode parent){
        SyntaxNode temp = var();
        if (lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            node = new SyntaxNode("NVLIST", null, null);
            createChild(parent, node);
            createChild(node, temp);
            optVlist(node);
        }else{      // If no condition satisfied, then let's suppose optVlist == epsilon (Special <vlist> ::= <var>)
            createChild(parent, temp);
        }
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

    private void optElist(SyntaxNode parent, SyntaxNode node){
        if(lookAheadToken.getTokenEnumString().equals("TCOMA")){
            updateTokens();
            createChild(parent, node);
            elist(node);
        }else{      // If no condition satisfied, then let's suppose optElist == epsilon (Special <elist> ::= <bool>)
            parent.setLeft(node.getLeft());
        }
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
    private SyntaxNode expr (SyntaxNode parent){
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