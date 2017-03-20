package Parser;
import java.util.*;

public class CreateTable extends SqlCommand {
	
	public String tableName = new String();
	public String input = new String();
	public ArrayList<Map<String, String>> maps = new ArrayList<Map<String, String>>();
	public String primaryKey = new String();
	public ArrayList<String> content = new ArrayList<String>();
	
	public CreateTable(String name, String s) {
		tableName = name;	
		input = s;
	}
	
	public CreateTable(){
		
	}

	@Override
	public int getActionType(){
		return 1;
	}
	
	public String getTableName(){
		return tableName;
	}
	
	public ArrayList<Map<String, String>> getAttributes(){
		return maps;
	}
	
	public String getPrimaryKey(){
		return primaryKey;
	}
	
	public boolean haveAttributeName(){
		return false;
	}
	
	public ArrayList<String> getContent(){
		return content;
	}
	
	public boolean parse(){
		
		String[] datas = input.split("\\,");
		
		
		for(int j=0; j < datas.length; j++){
			String data = datas[j];
			data = data.trim();
			//System.out.println(data);
			//check ()
			Stack<Character> st = new Stack<Character>();
			if(j == datas.length-1){
				st.push('(');
			}
			for (int i = 0; i < data.length(); i++){
			    char c = data.charAt(i);        
			    if(c == '(') st.push(c);
			    else if(c == ')'){
			    	if(st.empty()){
			    		//System.out.println(data);
			    		System.out.println("Syntex Error, missing left parenthesis");
			    		return false;
			    	}else{
			    		st.pop();
			    	}
			    }
			    //System.out.println(Arrays.toString(st.toArray()));
			}
			if(!st.empty()){
				System.out.println("Syntex Error, missing right parenthesis");
				return false;
			}
			
			content.add(data);
			String[] attributes = data.split(" +|\\(|\\)");
			/*for(String str : attributes){
				System.out.println(str);
			}*/
			List<String> list = new ArrayList<String>(Arrays.asList(attributes));
			list.removeAll(Arrays.asList("",null));
			
			/*for(String s : list){
				System.out.println(s);
			}*/
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", list.get(0));
			map.put("DataType", list.get(1));
			if(list.get(1).equals("int")){
				map.put("length", "0");
				if(list.size()>2){
					primaryKey = list.get(0);
				} else{
					if(primaryKey == null)
						primaryKey = null;
				}
			} else if(list.get(1).equals("varchar")){
				map.put("length", list.get(2));
				if(list.size()>3){
					primaryKey = list.get(0);
				} else{
					if(primaryKey == null)
						primaryKey = null;
				}
			}else{
				System.out.println("Syntax error, unknown type '" + list.get(1) + "'");
				return false;
			}
			maps.add(map);
			
		}
		String str = content.get(content.size()-1);
		str = str.replace(')', ' ');
		content.remove(content.size()-1);
		content.add(str);
		
		return true; 
		
	}

}
