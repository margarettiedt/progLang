/* Margaret Tiedt		lexeme.java
 * For the GORT programming language :-)
 * */

public class lexeme {
	public String svalue;
	public int ivalue = -555;
	public double rvalue = 5.5555;
	public Type t;
	
	public lexeme(Type ty)
	{
		t=ty;
	}
	
	public lexeme(Type ty, String str)
	{
		t = ty;
		svalue = str;
	}
	
	public lexeme(Type ty, int ival)
	{
		t = ty;
		ivalue = ival;
	}
	
	public lexeme(Type ty, double rval)
	{
		t = ty;
		rvalue = rval;
	}
	
	
	public String toString()
	{
		if(this.svalue != null)
		{
			if(this.t == Type.STRING)
				return this.t + " " + "\"" + this.svalue + "\"";
			else
				return this.t + " " +  this.svalue;
		}
		else if(this.ivalue != -555)
			return this.t + " " + this.ivalue;
		else if(this.rvalue != 5.5555)
			return this.t + " " + this.rvalue;
		else
			return ""+ this.t;
	}
	

}
