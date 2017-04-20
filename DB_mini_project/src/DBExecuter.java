
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import DBMS.*;
import DBMS.Field.Type;
import ExceptionHandlers.InsertionException;
import ExceptionHandlers.Warning;
import Parser.*;

public class DBExecuter {
	
	public DBExecuter(String path) throws Exception{
		// for debug
		Scanner scan = new Scanner(System.in);
		parser p = new parser();	
		ArrayList<SqlCommand> input = p.getCommands("tables.txt");
		input.addAll(p.getCommands("tweets.sql"));
		input.addAll(p.getCommands("user1.sql"));
		input.addAll(p.getCommands("SQL.txt"));
		Database database = new Database();
		
		for(SqlCommand s : input){
			
			if (s.getError() == ErrorType.No){
				Table table;
				if(s.getActionType() == ActionType.CreateTable){
					
					ArrayList<Field> tableList = new ArrayList<Field>();
					table = new Table(s.getTableName(), database);
					table.setisTmpTable(false);
					Field.Type type;
					
					String data[] = new String[3];
					for(Map<String, String> m: s.getAttributes())
					{
						if(m.get("DataType").compareTo("int") == 0){
							type = Field.Type.INT;
						}else{
							type = Field.Type.VARCHAR;
						}
						tableList.add(new Field(m.get("name"), 
								table, type, Integer.parseInt(m.get("length"))));	
					}
					if(s.getPrimaryKey().compareTo("") != 0)
						table.addTableFieldByField(tableList, s.getPrimaryKey());
					else
						table.addTableFieldByField(tableList);
					
					//table.printTableField();
					if(!table.errorFlag)
						database.creat(table);
					// TODO error handle
				}else if(s.getActionType() == ActionType.InsertInto){
					
					table = database.getTableByName(s.getTableName());
					
					if(s.haveAttributeName())
					{
						ArrayList<TransactionData> tableList = new ArrayList<TransactionData>();
						
						boolean right = true;
						for(Map<String, String> m: s.getAttributes())
						{
							for(Map.Entry<String, String> entry : m.entrySet())
							{
								if(right){
									TransactionData transAction = new TransactionData(entry.getValue(), entry.getKey(), table);
									if(transAction.createSuccessly){
										tableList.add(transAction);
									}
									right = transAction.createSuccessly;
								}
							}
							
						}
						if(right)
							table.addTableMember(tableList);
					}
					else
					{
						table.addTableMemberByDefault(s.getContent());
					}
					
					// TODO error handle
					
				}else if(s.getActionType() == ActionType.SelectFrom){
					
					boolean isWhere = s.getCompareTree() == null ? false:true;
					boolean isAggre;
					
					String Aggre = "", Aggreattri = "";
					if(s.getAggreFunc() != null){
						isAggre = true;
						if(s.getAggreFunc() == ActionType.SUM)
							Aggre = "sum";
						else if(s.getAggreFunc() == ActionType.COUNT)
							Aggre = "count";
						Aggreattri = s.getSelectAttr()[0];
					}else{
						isAggre = false;
					}
					ArrayList<String> nameList = new ArrayList<String>();
					ArrayList<String> attributeList = new ArrayList<String>();
					
					for(Map<String, String> m: s.getNickName()){
						nameList.add(m.get("tableName"));
					}
					for(int i = 0; i < s.getSelectAttr().length; i++){
						attributeList.add(s.getSelectAttr()[i]);
					}
					System.out.println("Select:");
					table = database.Select(nameList, attributeList, isWhere, s.getCompareTree(), isAggre, Aggre, Aggreattri);
					if(table != null)
						table.printAllRecord();
					
					scan.nextLine();
				}else{
					System.out.println("No action");
				}
				
				
				
			}else if(s.getError() == ErrorType.MissLPar){
				System.out.println("Missing Left Par");
			}else if(s.getError() == ErrorType.MissRPar){
				System.out.println("Missing Left Par");
			}else if(s.getError() == ErrorType.MissComma){
				System.out.println("Missing Comma");
			}else if(s.getError() == ErrorType.MultiComma){
				System.out.println("Multi Comma");
			}else if(s.getError() == ErrorType.UnKnownType){
				System.out.println("No support such type");
			}else if(s.getError() == ErrorType.SingleQuote){
				System.out.println("Missing Quote");
			}else if(s.getError() == ErrorType.MultiPoint){
				System.out.println("Missing Point");
			}else if(s.getError() == ErrorType.UnKnownAggr){
				System.out.println("Unknown Aggre Function");
			}else if(s.getError() == ErrorType.UnKnownOp){
				System.out.println("Unknown Operator");
			}else{
				System.out.println("No Group By");
			}
				
		}
		
	}
	
}
