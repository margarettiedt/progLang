/* Margaret Tiedt		lexer.java
 * For the GORT programming language :-)
 * */

import java.io.PushbackReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class lexer
{
	public String fileName;
	public static PushbackReader pr;
	public static int lineCount=1;
	public static boolean lastLexVar = false;
	

	public lexer(String fileNm) throws FileNotFoundException
	{
		fileName = fileNm;
		pr = new PushbackReader(new FileReader(fileName));
	}
	
	public lexeme lex() throws IOException
	{ 
		char ch;
		
		skipWhiteSpace();			// keep track of new lines, line #(counter) and comments, block comments
		ch = (char) pr.read();
	
		if (ch == 65535){ // reached the end of the input when read() returns -1 (65535 in hex)
			return new lexeme(Type.END_OF_INPUT); // special lexeme that identifies end of file
		} 
		
		switch(ch)
		{
			case ';':
				return new lexeme(Type.SEMI);
			case ',':
				return new lexeme(Type.COMMA);
			case '(':
				return new lexeme(Type.OPAREN);
			case ')':
				return new lexeme(Type.CPAREN);
			case '.':
				return new lexeme(Type.PERIOD);
			case '+':
				char x = (char) pr.read();
				if(x == '=')
					return new lexeme (Type.PLUS_EQUALS);
				else
				{
					pr.unread( (int) x );
					return new lexeme(Type.PLUS);
				}
			case '-':
				char y = (char) pr.read();
				if(y == '=')
					return new lexeme (Type.MINUS_EQUALS);
				else
				{
					pr.unread( (int) y );
					return new lexeme(Type.MINUS);
				}
			case '*':
				return new lexeme(Type.MULTIPLY);
			case '/':
				return new lexeme(Type.DIVIDE);
			case '=':
				return new lexeme(Type.ASSIGN);
			case '>':
				return new lexeme(Type.GREATERTHAN);
			case '<':
				return new lexeme(Type.LESSTHAN);
			case '%':
				return new lexeme(Type.MOD);
			case '{':
				return new lexeme(Type.OBRACE);
			case '}':
				return new lexeme(Type.CBRACE);
				
			default:
				//System.out.println(ch);
				if(Character.isDigit(ch))
				{
					pr.unread((int)ch);
					return lexNumber();
					
				}
				else if(Character.isAlphabetic(ch))
				{
					pr.unread((int)ch);
					return lexWord();
				}
				else if(ch == '\"')
				{
					return lexString();
				}
				else
					return new lexeme (Type.UNKNOWN, ch);
				
		}

	} // end lex
	
	public static lexeme lexNumber() throws IOException
	{
		String buffer = "";
		char ch = (char) pr.read();
		
		while(Character.isDigit(ch))
		{
			buffer += ch;
			ch = (char) pr.read();
		}
		
		pr.unread((int) ch );
		return new lexeme(Type.INTEGER, Integer.parseInt(buffer));
		
	} // end lexNumber
	
	public static lexeme lexWord() throws IOException
	{
		String buffer = "";
		char ch = (char) pr.read();
		
		while(Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_')
		{
			buffer += ch;
			ch = (char) pr.read();
		}
		
		pr.unread((int) ch );
		//Handle cases of KEYWORDS! 
		if (buffer.equals("FUN"))
			return new lexeme(Type.FUNCTION);
		else if (buffer.equals("var")) 
			return new lexeme(Type.VARIABLE);
		else if (buffer.equals("while")) 
			return new lexeme(Type.WHILE);
		else if (buffer.equals("if")) 
			return new lexeme(Type.IF);
		else if (buffer.equals("print")) 
			return new lexeme(Type.PRINT);
		else if (buffer.equals("else"))
			return new lexeme(Type.ELSE);
		//If there are any more keywords, add here..
		else
			return new lexeme(Type.ID, buffer);
	} // end lexWord
	
	public static lexeme lexString() throws IOException
	{
		String buffer = "";
		char ch = (char) pr.read();
		
		while(ch != '\"')
		{
			if(ch == '\\')
				ch = (char) pr.read();
			buffer += ch;
			ch = (char) pr.read();
		}
		
		return new lexeme(Type.STRING, buffer);
	} // end lexString
	
	public static void skipWhiteSpace() throws IOException
	{
		
		char ch = (char) pr.read();
		
		if(ch == '~') // Symbol for beginning of comment line
		{
			while(ch != '\n')
			{
				// Keep consuming chars until you reach a new line
				ch = (char) pr.read();
			}
		}
		
		else if (ch == 10 || ch == '\r' || ch == '\n')
		{
			while(ch == 10 || ch == '\r' || ch == '\n' || ch == ' ')
			{
				//System.out.println("--INSIDE NEWLINE ELSE IF");
				if(ch != ' ')
					lineCount++; 
				ch = (char) pr.read();
			}
			pr.unread((int) ch); //consumed one too many
			if(ch == '~')
			{
				while(ch != '\n')
				{
					// Keep consuming chars until you reach a new line
					ch = (char) pr.read();
				}
				lineCount++;
			}
			
		}
		
		else if (ch == ' ')
		{
			while(ch == ' ') //white space
			{
				//Keep consuming chars until no more white space
				ch = (char) pr.read(); 
			}
			
			pr.unread((int) ch); //consumed one too many
		}
		
		else
			pr.unread((int) ch); //Not a '~' or ' ', return character back to input
		
	} // end skipWhiteSpace
	
	public int getLineCount()
	{
		return lineCount;
	}
	
	public void closeStream() throws IOException
	{
		pr.close();
	}
	
} // end lexer
