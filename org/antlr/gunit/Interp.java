/*
 [The "BSD licence"]
 Copyright (c) 2007 Leon, Jen-Yuan Su
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.gunit;

import org.antlr.runtime.*;

import java.util.ArrayList;
import java.util.List;

/** The main gUnit interpreter entry point. 
 * 	Read a gUnit script, run unit tests or generate a junit file. 
 */
public class Interp {
	
	protected String grammarName;				// targeted grammar for unit test
	protected String treeGrammarName = null;	// optional, required for testing tree grammar
	protected String header = null;				// optional, required if using java package
	protected List<gUnitTestSuite> ruleTestSuites = new ArrayList<gUnitTestSuite>();	// testsuites for each testing rule
	protected StringBuffer unitTestResult = new StringBuffer();
    
	public static void main(String[] args) throws Exception {
		/** Pull char from where? */
		CharStream input = null;
		/** Generate junit codes */
		if ( args.length>0 && args[0].equals("-o") ) {
			if ( args.length==2 ) {
				input = new ANTLRFileStream(args[1]);
			    Interp interpreter = new Interp();
				interpreter.gen(input);
			}
			else {
				input = new ANTLRInputStream(System.in);
				Interp interpreter = new Interp();
				interpreter.gen(input);
			}
		}
		/** Run gunit tests */
		else if ( args.length==1 ) {
			input = new ANTLRFileStream(args[0]);
		    Interp interpreter = new Interp();
			interpreter.exec(input);
			System.out.print(interpreter.unitTestResult.toString());	// unit test result
		}
		else {
			input = new ANTLRInputStream(System.in);
			Interp interpreter = new Interp();
			interpreter.exec(input);
			System.out.print(interpreter.unitTestResult.toString());	// unit test result
		}
	}
	
	public void exec(CharStream input) throws Exception {
		gUnitLexer lexer = new gUnitLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		gUnitParser parser = new gUnitParser(tokens, this);
		parser.gUnitDef();	// parse gunit script and save elements to interpreter fields
		gUnitExecuter executer = new gUnitExecuter(this);
		executer.execTest();
	}
	
	public void gen(CharStream input) throws Exception {
		gUnitLexer lexer = new gUnitLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		gUnitParser parser = new gUnitParser(tokens, this);
		parser.gUnitDef();	// parse gunit script and save elements to interpreter fields
		JUnitCodeGen generater = new JUnitCodeGen(this);
		generater.compile();
	}
}
