import java.io.*;
import Test.ExceptionTest;

public class HellowJava {

	public static void main(String[] args) {

		ExceptionTest test = new ExceptionTest();
		
		try {
			test.Test(1);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}

}
