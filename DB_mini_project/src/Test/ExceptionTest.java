package Test;
import ExceptionHandlers.*;

public class ExceptionTest {
	
	public void Test(int num) throws Exception 
	{
		if(num == 0)
			throw new CreationException("Create");
		else if(num == 1)
			throw new FormatException("Format");
		else if(num == 2)
			throw new InsertionException("Insertion");
		else if(num == 3)
			throw new TypeException("Type");
		else if(num == 4)
			throw new Warning("Warning");
	}
}