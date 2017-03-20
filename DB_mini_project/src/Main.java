
import java.util.*;
import DBMS.Database;
import DBMS.Table;
import DBMS.TransactionData;
import ExceptionHandlers.InsertionException;
import ExceptionHandlers.Warning;
import Parser.SqlCommand;
import Parser.parser;
import DBMS.Field.Type;
import Test.ExceptionTest;
import Parser.*;


public class Main {
	
	public static void main(String[] args) throws Warning, InsertionException {
		try
		{
			DBExecuter dbExecuter = new DBExecuter("Input.txt");
		}
		catch(Exception e)
		{
			
		}
		finally
		{
		
		}
	}
}
