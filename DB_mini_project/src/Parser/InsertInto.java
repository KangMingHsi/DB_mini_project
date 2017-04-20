package Parser;

import java.util.*;

public class InsertInto extends SqlCommand {

	public String tableName = new String();
	public String input = new String();
	public boolean attributeName;
	public Map<String, String> attributes = new HashMap<String, String>();
	ArrayList<String> names = new ArrayList<String>();
	public ErrorType error;
	
	public InsertInto(String name, String s) {
		tableName = name;
		input = s;
	}
	
	public InsertInto(ErrorType err){
		error = err;
	}
	
	public InsertInto(){
		
	}
	public ArrayList<String> getContent(){
		return names;
	}
	
	public boolean haveAttributeName(){
		return attributeName;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.InsertInto;
	}

	@Override
	public String getTableName() {
		return tableName;
	}
	
	public String getPrimaryKey(){
		return "";
	}
	
	public ArrayList<Map<String, String>> getAttributes(){
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(attributes);
		return list;
	}
	
	public void parse(ErrorType er){
		
		error = er;
		//System.out.println(input);
		
			String[] data = input.split("\\(");
			
			/*for(String s : data){
				System.out.println(s);
			}
			System.out.println("**********");*/
			
			for(int i=0; i<data.length; i++){
				data[i] = data[i].replace('(', ' ');
				data[i] = data[i].replace(')', ' ');
				data[i] = data[i].replace('\'', ' ');
				data[i] = data[i].replaceAll("(?i)values", "");
				//data[i] = data[i].replaceAll("\\s+","");
				//System.out.println(data[i]);
			}
			
			if(data.length == 2){	//have name
				
				attributeName = true;
				String[] name = data[0].split(",");
				String[] value = data[1].split(",");
				for(int i=0; i<name.length; i++){
					value[i] = value[i].trim();
					name[i] = name[i].trim();
					name[i] = name[i].toLowerCase();
					attributes.put(value[i], name[i]);
				}
				
			} else{	//don't have name
				
				attributeName = false;
				String[] value = data[0].split(",");
				for(String s : value){
					s = s.trim();
					attributes.put(s, "");
					names.add(s);
				}
				
			}
	}

	@Override
	public ArrayList<Map<String, String>> getNickName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSelectAll() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ActionType getAggreFunc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCompareTree() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSelectAttr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorType getError() {
		// TODO Auto-generated method stub
		return error;
	}

}
