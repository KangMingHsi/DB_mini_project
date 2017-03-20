package Parser;

import java.io.*;
import Parser.SqlCommand;
import java.util.*;

public class parser {
	
	public ArrayList<SqlCommand> commands = new ArrayList<SqlCommand>();
	
	public parser(){
		
	}
	
	public ArrayList<SqlCommand> getCommands(String path){
		try{
			//read file, put the content into "InputTmp"
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String ch = new String();
			String InputTmp = new String();
			while( (ch = br.readLine()) != null ){
				InputTmp += ch;
			}
			
			//split the commands
			String[] InputCommand = InputTmp.split("\\;");
			
			//check which action
			for (String s : InputCommand){
				boolean flag = false;
				//check ()
				Stack<Character> st = new Stack<Character>();
				for (int i = 0; i < s.length(); i++){
				    char c = s.charAt(i);        
				    if(c == '(') st.push(c);
				    else if(c == ')'){
				    	if(st.empty()){
				    		System.out.println("Syntex Error, missing left parenthesis");
				    		flag = true;
				    		continue;
				    	}else{
				    		st.pop();
				    	}
				    }
				}
				if(!st.empty()){
					System.out.println("Syntex Error, missing right parenthesis");
					flag = true;
				}
				if(flag) continue;
				
				
				String[] sqlEntry = s.split("\\(", 2);
				
				sqlEntry[0] = sqlEntry[0].trim();
				sqlEntry[1] = sqlEntry[1].trim();
				sqlEntry[0] = sqlEntry[0].toLowerCase();
				sqlEntry[1] = sqlEntry[1].toLowerCase();
				
				/*for(String str : sqlEntry){
					System.out.println(str);
				}*/
				
				String[] sqlAction = sqlEntry[0].split(" +");
				
				// See Whether it is insertion or create
				//if(sqlAction.length < 4){
					if(sqlAction[0].equals("create") & sqlAction[1].equals("table")){
						
						sqlEntry[1] = sqlEntry[1].toLowerCase();
						CreateTable c = new CreateTable(sqlAction[2], sqlEntry[1]);
						if(c.parse())
							commands.add(c);
						
					} else if(sqlAction[0].equals("insert") & sqlAction[1].equals("into")){
						
						sqlAction[2] = sqlAction[2].replaceAll("(?i)values", "");
						InsertInto i = new InsertInto(sqlAction[2], sqlEntry[1]);
						i.parse();
						commands.add(i);
						
					} else{
						System.out.println("Syntax error, unknown command");
					}
				/*} else{
					System.out.println("Syntax Error.");
				}*/
				//System.out.println("action: "+sqlAction[0]);
				//System.out.println("name: "+sqlAction[2]);
				//System.out.println("****************");
				
			}
			
			
			br.close();
			
		}catch(IOException e){
			System.out.println(e);
		}
		
		return commands;
		
	}
}