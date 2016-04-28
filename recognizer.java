import java.io.FileNotFoundException;
import java.io.IOException;

public class recognizer {
	
	public static lexeme currentLex;
	public static lexer rLexer;
	
	public recognizer (String fileName) throws FileNotFoundException
	{
		rLexer = new lexer(fileName);
	}
	
	/* * * * * * * * * * * * * * * * * 
	 *       HELPER  FUNCTIONS       *
	 * (match, check, advance, etc.) *
	 * * * * * * * * * * * * * * * * */
	
	public static void match(Type type) throws IOException
	{
		matchNoAdvance(type);
		advance();
	}
	
	public static void matchNoAdvance(Type type)
	{
		if(!check(type))
			throw new UnsupportedOperationException("Expected a lexeme of type " + type + 
					", recieved lexeme of type " + currentLex.t + " at line " + rLexer.getLineCount());
	}
	
	public static boolean check(Type type)
	{
		return currentLex.t == type;
	}
	
	public static void advance() throws IOException
	{
		currentLex = rLexer.lex();
	}
	
	/* * * * * * * * * * * * * * * * * 
	 *    Grammar Rules FUNCTIONS    *
	 * * * * * * * * * * * * * * * * */
	
	public static void program() throws IOException
	{
		if(optStmtListPending())
		{
			optStatementList();
		}
	}
	
	public static void expression() throws IOException
	{
		System.out.println("===Inside expression with " + currentLex.t + " " + currentLex.svalue);
		primary();
		if(opPending())
		{
			operator();
			System.out.println("&&& finished operator");
			expression();
			System.out.println("%%% finished expr");
			
		}
	}
	
	public static void primary() throws IOException
	{
		if(numberPending())
		{
			number();
		}
		else if(check(Type.ID))
		{
			System.out.println("**HIT");
			match(Type.ID);
			if(check(Type.OPAREN))
			{
				match(Type.OPAREN);
				optExprList();
				System.out.println("~~w/ current Lex" + currentLex.t + " " + currentLex.svalue);
				match(Type.CPAREN);
			}
		
		}
		else if(check(Type.STRING))
		{
			match(Type.STRING);
		}
		else
		{
			System.out.println("~~w/ current Lex" + currentLex.svalue);
			match(Type.OPAREN);
			expression();
			match(Type.CPAREN);
		}
	}
	
	public static void number() throws IOException
	{
		if(check(Type.INTEGER))
		{
			match(Type.INTEGER);
		}
	}
	
	public static void optExprList() throws IOException
	{
		if(exprListPending())
		{
			System.out.println("@@@ Inside optExList with " + currentLex.t + " " + currentLex.svalue);
			exprList();
		}
	}
	
	public static void exprList() throws IOException
	{
		if(exprPending())
		{
			System.out.println("       Inside exprList if with " + currentLex.t + " " + currentLex.svalue);
			expression();
			if(check(Type.COMMA))
			{
				System.out.println(".....about to match comma " + currentLex.t + " " + currentLex.svalue);
				match(Type.COMMA);
				exprList();
			}
		}
	}
	
	public static void operator() throws IOException
	{
		if(check(Type.PLUS))
		{
			match(Type.PLUS);
		}
		else if(check(Type.MINUS))
		{
			match(Type.MINUS);
		}
		else if(check(Type.DIVIDE))
		{
			match(Type.DIVIDE);
		}
		else if(check(Type.MULTIPLY))
		{
			match(Type.MULTIPLY);
		}
		else if(check(Type.MOD))
		{
			match(Type.MOD);
		}
		else if(check(Type.PLUS_EQUALS))
		{
			match(Type.PLUS_EQUALS);
		}
		else if(check(Type.MINUS_EQUALS))
		{
			match(Type.MINUS_EQUALS);
		}
		else if(condOpsPending())
		{
			System.out.println("consumed primary " + currentLex.t + " " + currentLex.svalue);
			condOps();
		}
		else
			match(Type.ASSIGN);
	}
	
	public static void condOps() throws IOException
	{
		if(check(Type.GREATERTHAN))
		{
			match(Type.GREATERTHAN);
		}
		else if(check(Type.LESSTHAN))
		{
			match(Type.LESSTHAN);
		}
	}
	
	public static void ifStatement() throws IOException
	{
		match(Type.IF);
		match(Type.OPAREN);
		expression();
		match(Type.CPAREN);
		block();
		optElse();	
	}
	
	public static void block() throws IOException
	{
		match(Type.OBRACE);
		optStatementList();
		match(Type.CBRACE);
	}
	
	public static void optStatementList() throws IOException
	{
		System.out.println("IN OSL " + currentLex.t + " " + currentLex.svalue);
		if(stmtListPending())
		{
			System.out.println("++IN opt statmentList IF " + currentLex.t + " " + currentLex.svalue);
			statementList();
		}
	}
	
	public static void statementList() throws IOException
	{
		if(stmtPending())
		{
			statement();
			if(stmtListPending())
				statementList();
		}
	}
	
	public static void statement() throws IOException
	{
		if(exprPending()){
			System.out.println("--In statement : exprPend if.");
			expression();
			match(Type.SEMI);
		}
		else if(whileLoopPending())
		{
			whileLoop();
		}
		else if(ifStatementPending())
		{
			ifStatement();
		}
		else if(functionCallPending())
		{
			functionCall();
		}
		else if(functionDefPending())
		{
			functionDefinition();
		}
		else if(assignPending())
		{
			assignment();
		}
		else if(check(Type.PRINT))
		{
			match(Type.PRINT);
			match(Type.OPAREN);
			match(Type.STRING);
			match(Type.CPAREN);
			match(Type.SEMI);
			
		}
		else
		{
			match(Type.VARIABLE);
			match(Type.ID);
			optInit();
			match(Type.SEMI);
		}
	}
	
	public static void whileLoop() throws IOException
	{
		match(Type.WHILE);
		match(Type.OPAREN);
		expression();
		match(Type.CPAREN);
		block();
	}
	
	public static void functionCall() throws IOException
	{
		match(Type.ID);
		match(Type.OPAREN);
		optExprList();
		match(Type.CPAREN);
		//match(Type.SEMI);
	}
	
	public static void functionDefinition() throws IOException
	{
		System.out.println("    INSIDE FUNCTION DEF");
		match(Type.FUNCTION);
		functionCall();
		block();
		match(Type.SEMI);
	}
	
	public static void assignment() throws IOException
	{
		match(Type.ID);
		match(Type.ASSIGN);
		expression();
		match(Type.SEMI);
	}
	
	public static void optInit() throws IOException
	{
		if(check(Type.ASSIGN))
		{
			match(Type.ASSIGN);
			expression();
		}
	}
	
	public static void optElse() throws IOException
	{
		if(check(Type.ELSE))
		{
			match(Type.ELSE);
			block();
		}
	}
	
	public static void list() throws IOException
	{
		item();
		if(check(Type.COMMA))
		{
			match(Type.COMMA);
			list();
		}
	}
	
	// might be an incomplete definition, check back
	public static void item() throws IOException
	{
		if(check(Type.INTEGER))
		{
			match(Type.INTEGER);
		}
		else if(check(Type.STRING))
		{
			match(Type.STRING);
		}
		else
			match(Type.INTEGER);
	}
	
	/* * * * * * * * * * * * * * * * * 
	 *       PENDING FUNCTIONS       *
	 * * * * * * * * * * * * * * * * */
	
	public static boolean opPending() 
	{
		return (check(Type.PLUS) || check (Type.MINUS) || check(Type.DIVIDE) || check(Type.MULTIPLY) ||
				check(Type.MOD) || check(Type.ASSIGN) || check(Type.PLUS_EQUALS) || check(Type.MINUS_EQUALS)|| condOpsPending());
	}
	
	public static boolean condOpsPending()
	{
		return (check(Type.GREATERTHAN) || check(Type.LESSTHAN));
	}
	
	public static boolean exprPending()
	{
		return primPending();
	}
	
	public static boolean itemPending()
	{
		return (check(Type.INTEGER) || check(Type.STRING));
	}
	
	public static boolean numberPending()
	{
		return check(Type.INTEGER);
	}
	
	public static boolean primPending()
	{
		return numberPending() || check(Type.STRING) ||check(Type.ID) || check(Type.OPAREN);
	}
	
	public static boolean exprListPending()
	{
		return exprPending();
	}
	
	public static boolean stmtPending()
	{
		return( exprPending() || assignPending() || ifStatementPending() || whileLoopPending()
				|| functionCallPending() || functionDefPending() || check(Type.PRINT) || check(Type.VARIABLE));
	}
	
	public static boolean stmtListPending()
	{
		return stmtPending();
	}
	
	public static boolean optStmtListPending()
	{
		return stmtListPending();
	}
	
	public static boolean ifStatementPending()
	{
		return (check(Type.IF));
	}
	
	public static boolean whileLoopPending()
	{
		return (check(Type.WHILE));
	}
	
	public static boolean functionCallPending()
	{
		return (check(Type.ID));
	}
	
	public static boolean functionDefPending()
	{
		return (check(Type.FUNCTION));
	}
	
	public static boolean assignPending()
	{
		return (check(Type.ID));
	}
	
	public static void parse(String fileName) throws IOException
	{
		rLexer = new lexer(fileName);
		currentLex = rLexer.lex();
		program();
		match(Type.END_OF_INPUT);
		rLexer.closeStream();
		System.out.println("Number of Lines: "+ rLexer.getLineCount());
		System.out.println("Legal");
	}
	
	// ******************************************
	
	public static void main(String args[]) throws IOException
	{
		parse(args[0]);
	}
	
}
