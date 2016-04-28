/* Margaret Tiedt		scanner.java
 * For the GORT programming language :-)
 * */

import java.io.IOException;

public class scanner {
	
	public scanner(String fileName) throws IOException
	{
		// Dummy constructor
		System.out.println("Welcome to Scanner");
	}
	
	public static void main(String args[]) throws IOException
	{
		lexeme token;
		lexer l = new lexer(args[0]);
		boolean endSeen = false;
		
		token = l.lex();
		while(!endSeen)
		{
			if(token.t == Type.END_OF_INPUT)
			{
				System.out.println(token.t.toString());
				endSeen = true;
				break;
			}
			System.out.println(token);
			token = l.lex();
		}
		
		
	}
}
