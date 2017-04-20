package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectFrom extends SqlCommand {

	String nickName;
	String input;
	String[] attributes;
	ArrayList<Map<String, String>> tableName = new ArrayList<Map<String, String>>();
	boolean selectAll = false;
	ActionType aggreFunc;
	String[] cmpTree;
	ErrorType error;
	
	public SelectFrom() {
		// TODO Auto-generated constructor stub
	}

	public SelectFrom(String string) {
		// TODO Auto-generated constructor stub
		this.input = string; 
	}

	@Override
	public ActionType getActionType() {
		return ActionType.SelectFrom;
	}

	@Override
	public String getTableName() {
		return null;
	}
	
	@Override
	public ArrayList<Map<String, String>> getNickName() {
		// TODO Auto-generated method stub
		return tableName;
	}

	@Override
	public String getPrimaryKey() {
		return null;
	}

	@Override
	public ArrayList<Map<String, String>> getAttributes() {
		return null;
	}

	@Override
	public boolean haveAttributeName() {
		return false;
	}

	@Override
	public ArrayList<String> getContent() {
		return null;
	}
	
	@Override
	public boolean isSelectAll() {
		// TODO Auto-generated method stub
		return selectAll;
	}
	
	String checkNickName(String name) {
		
		String ans;
		
		for(Map<String, String> m : tableName){
			
			ans = m.get("nickName");
			if ( ans != null && ans.equals(name) ){
				return m.get("tableName");
			}
			
		}
		
		return name;
		
	}
	
	String handleAttr(String s, String table){
		
		String map = new String();
		String[] entry = s.split("\\.");
		if(s.equals("")){
			//System.out.println("Syntax error, multiple comma");
			error = ErrorType.MultiComma;
		}
		if (entry.length == 1) {			//only one table
				
			map = s;
				
		} 
		else if(entry.length == 2){			//may probably with nickname
				
			if(entry[1].equals("*"))
				selectAll = true;
			map = checkNickName(entry[0]) + "." +entry[1];
				
		} 
		else{
				
			//System.out.println("Syntax error");
			error = ErrorType.MultiPoint;
			//
			s = null;
				
		}
		return map;
		
	}
	
	String  changeNickName(String str){
		
		str = str.toLowerCase();
		String[] s = str.split("\\.");
		if(s.length != 1){
			//System.out.println("--"+str+"--");
			//System.out.println("**"+checkNickName(s[0])+"**");
			str = checkNickName(s[0]) + "." + s[1];
			
		}
		return str;
		
	}
	
	String checkAttr(String s){
		
		String ans;
		
		String[] ss = s.split("\\'");
		
		if( ss.length == 2 ){
			
			ans = ss[1].trim() + "c";
			
		}
		else{
			
			try {
		        Integer.parseInt( s );
		        ans = s + "i";
		    }
		    catch( Exception e ) {
		        ans = changeNickName(s) + "a";
		    }
			
		}
		
		return ans;
		
	}
	
	void detectCmp(String where, int index){
		
		where = where.trim();
		
		String[] s;
		if ( where.split( "=" ).length > 1){
			s = where.split( "=" );
			cmpTree[index] = "=o";
			cmpTree[index*2+1] = checkAttr(s[0].trim()).trim();
			cmpTree[index*2+2] = checkAttr(s[1].trim()).trim();
		}
		else if ( where.split( ">" ).length > 1 ){
			s = where.split( ">" );
			cmpTree[index] = ">o";
			cmpTree[index*2+1] = checkAttr(s[0].trim()).trim();
			cmpTree[index*2+2] = checkAttr(s[1].trim()).trim();
		}
		else if ( where.split( "<" ).length > 1 ){
			s = where.split( "<" );
			cmpTree[index] = "<o";
			cmpTree[index*2+1] = checkAttr(s[0].trim()).trim();
			cmpTree[index*2+2] = checkAttr(s[1].trim()).trim();
		}
		else if ( where.split( "<>" ).length > 1 ){
			s = where.split( "<>o" );
			cmpTree[index] = "<>";
			cmpTree[index*2+1] = checkAttr(s[0].trim()).trim();
			cmpTree[index*2+2] = checkAttr(s[1].trim()).trim();
		}
		else{
			System.out.println("Unknown operator");
			error = ErrorType.UnKnownOp;
		}
		
	}
	
	void checkComma(String s){
		
		String[] ss = s.split(" ");
		if(ss.length > 1){
			System.out.println("Syntax error, missing comma");
		}
		
	}
	
	
	public void parse(ErrorType er){
		
		error = er;
		
		//System.out.println(input);
		
		//System.out.println(input);
		
		String[] selEntry = input.split("(?i)select|(?i)from|(?i)where");
		/*for(String s : selEntry){
			System.out.println(s);
		}*/
		
		/*handle FROM*/
		Map<String, String> tmp;
		//System.out.println(selEntry[2]);
		selEntry[2] = selEntry[2].toLowerCase();
		selEntry[2] = selEntry[2].trim();
		String[] t = selEntry[2].split("\\,");
		for(String s : t){
			s = s.trim();
			if(s.equals("")){
				System.out.println("Syntax error, multiple comma");
				error = ErrorType.MultiComma;
			}
			/*if(erTmp.length > 1)
				System.out.println("Syntax error, missing comma b");*/
			tmp = new HashMap<String, String>();
			String[] t2 = s.split("as", 2);
			if(t2.length == 1){
				s = s.trim();
				checkComma(s);
				tmp.put("tableName", s);
				tmp.put("nickName", null);
			}
			else if(t2.length == 2){
				t2[0] = t2[0].trim();
				checkComma(t2[0]);
				t2[1] = t2[1].trim();
				checkComma(t2[1]);
				tmp.put("tableName", t2[0]);
				tmp.put("nickName", t2[1]);
			}
			tableName.add(tmp);
		}
		
		/*handle SELECT*/
		//Map<String, String> map= new HashMap<String, String>();
		selEntry[1] = selEntry[1].toLowerCase();
		selEntry[1] = selEntry[1].trim();
		
		String[] attr = selEntry[1].split("\\,");
		
		attributes = new String[attr.length];
		int index = 0;
		
		for(String s : attr){
			s = s.trim();
			String[] erTmp = s.split(" ");
			if(erTmp.length > 1){
				System.out.println("Syntax error, missing comma");
				error = ErrorType.MissComma;
			}
			String[] ss = s.split("\\(");
			
			if ( ss.length != 1 ){
				
				//System.out.println(ss[0] + "," + ss[1]);
				ss[0] = ss[0].trim();
				if ( ss[0].equals("count") ){
					aggreFunc = ActionType.COUNT;
					if(attr.length != 1){
						System.out.println("You should add 'Group By'");
						error = ErrorType.NoGroupBy;
					}
				}
				else if ( ss[0].equals("sum") ){
					aggreFunc = ActionType.SUM;
					if(attr.length != 1){
						System.out.println("You should add 'Group By'");
						error = ErrorType.NoGroupBy;
					}
				}
				else{
					System.out.println("Unknown Aggregation Function.");
					error = ErrorType.UnKnownAggr;
				}
				ss[1] = ss[1].replace(')', ' ');
				ss[1] = ss[1].trim();
				s = ss[1];
			}
			
			/*if(s.equals("*"))
				selectAll = true;	*/
			
			attributes[index] = handleAttr(s, selEntry[2]);
			index++;
			
			//System.out.println(s);
			
		}
		
		/*handle where*/
		if ( selEntry.length > 3 ){
			
			selEntry[3] = selEntry[3].trim();
			//System.out.println(selEntry[3]);
			String[] where = selEntry[3].split(" (?i)and ");
			
			if( where.length > 1){
				cmpTree = new String[7];
				//contain logic operator AND
				cmpTree[0] = "andL";
				detectCmp(where[0], 1);
				detectCmp(where[1], 2);
			}
			else{
				
				where = selEntry[3].split(" (?i)or ");
				if(where.length > 1){
					//contain logic operator OR
					cmpTree = new String[7];
					cmpTree[0] = "orL";
					detectCmp(where[0], 1);
					detectCmp(where[1], 2);
				}
				else{
					//no logic operator
					cmpTree = new String[3];
					detectCmp(where[0], 0);
				}
				
			}
				
			/*for(String s : where)
				System.out.println(s);*/
		
		
		}
		else{
			
			cmpTree = null;
			
		}
		
		/*for(String m : cmpTree){
				
			    System.out.println(m);
			
		}
		
		System.out.println("Selcet all is " + selectAll);
		System.out.println(aggreFunc);
		System.out.println("------------------------");*/
		

		
	}
	
	@Override
	public ActionType getAggreFunc() {
		// TODO Auto-generated method stub
		return aggreFunc;
	}

	@Override
	public String[] getCompareTree() {
		// TODO Auto-generated method stub
		return cmpTree;
	}

	@Override
	public String[] getSelectAttr() {
		// TODO Auto-generated method stub
		return attributes;
	}

	@Override
	public ErrorType getError() {
		// TODO Auto-generated method stub
		return error;
	}




}
