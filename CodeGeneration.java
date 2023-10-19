///////////////////////////////////////////////////////////////////////////////

// Title:           CD23 Parser
// Files:           SyntaxNode.java, SymbolForTable.java, CD23Parser.java, NonTerminalMethods.java
// Semester:        Semester 2 2023
//Course:           COMP3290 COMPILER DESIGN
// Authors:         Cameron Swift (c3445524)
//                  Andres Moreno Miguel (c3465977)
// Info:            This class is the main class of the code generation.

///////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.Stack;

public class CodeGeneration {

    public void generateCode(Stack<SyntaxNode> nodeStack, ArrayList<SymbolForTable> symbolTable){

        System.out.println();
        SymbolForTable symbol;
        while(nodeStack.isEmpty() != true){
            System.out.println(nodeStack.pop());
        }

        for(int x = 0; x < symbolTable.size(); x++){
            symbol = symbolTable.get(x);
            symbol.displaySymbol();
        }
    }
    
}

//   
//   Output:
//   Output for Part B will produce a valid SM23 Module File (using the same filename as the source, with the
//   extension .mod) – assuming a valid source file is provided – in the same directory as the compiler, as well as a
//   listing (with the extension .lst) to the same location.
//   For example, the source file test.cd will compile to a Module File called test.mod
//   Additionally, the contents of the compilation will be displayed in the terminal window. The output (both to file
//   and on screen) will be formatted as per section The Structure of the Module File from the SM User Guide
//   document.
//   The contents of the Module File will look similar to the form:
//   6
//   41 03 52 91 00 00 00 00
//   61 43 91 00 00 00 08 61
//   43 91 00 00 00 16 81 00
//   00 00 00 81 00 00 00 08
//   11 43 00 00 00 00 16 64
//   67 72 00 00 00 00 00 00
//   .
//   .
//   .
//   If compilation is successful, you will report a short message to the terminal stating that compilation has been
//   successful; such as:
//   module-name compiled successfully
//   …where module-name is replaced with the name of your module file (without the .mod extension). Any
//   compilation errors should also be reported to the Terminal, along with a message that informs the user that the
//   compilation failed.



//--------------------------------------------------------------------------------------------------------------------------
//  3. Group Report (weight: 5%):
//  Accompanying your submission (regardless of whether or not your group attempted only Part 3A, or both Part
//  3A and Part 3B) will be a Report. Your report will contain the following sections:

//  a) Any changes, refactoring, or refinement of the language that have been made (fixing Left Recursion issues,
//  attempts to bring the grammar closer to LL(1), etc.) through the project.

//  b) A Semantic Analysis Overview detailing:
//  • what has been successfully implemented, and the approach used;
//  • what Semantic Analysis has been attempted;
//  • what Semantic Analysis is not complete or unsuccessful;
//  • any assumptions made, with respect to Semantic Analysis.

//  c) Details of what aspects of Code Generation have been:
//  • implemented successfully;
//  • implemented partially (please attach sample code to demonstrate);
//  • not implemented;
//  • any Machine-Independent Optimisations you have implemented (such as constant folding).

//  d) A ½ to 1 page overview on what extensions you feel would be useful to both the Language (CD) and the
//  Machine Environment (SM).

//  Your report should be formatted as a report! This means you will include name and student number of each
//  group member, appropriate headings, and address the above criteria.
//  Your report should be between two and four A4 pages in length.
//  Marks will also be awarded in part for how closely your assessment of your implementation matches what has been
//  accomplished – ie: tell the truth, get better marks in both the report, and the project parts. Obviously the inverse
//  also applies