package DBMS;

import java.util.*;

import DBMS.Field.Type;
import ExceptionHandlers.InsertionException;
import ExceptionHandlers.Warning;

public class Database {

	private ArrayList<String> tableList;
	private Hashtable<String, Table> tables;
	
	public Database() {
		this.tableList = new ArrayList<String>();
		this.tables = new Hashtable<String, Table>();
	}
	
	public void creat(String name)
	{
		tableList.add(name);
		tables.put(name, new Table(name, this));
	}
	
	public void creat(Table table)
	{
		tableList.add(table.getName());
		tables.put(table.getName(), table);
	}
	
	public Table getTableByName(String name)
	{
		return tables.get(name);
	}
	
	public Table Select(ArrayList<String> nameList, ArrayList<String> arrtbuteList, boolean isWhere, String[] whereTree, boolean isAggre, String Aggre , String Aggreattri) throws Warning, InsertionException {
		Table tmpTable = new Table("tmpTable", this);
		tmpTable.setisTmpTable(true);
		
		boolean isAlais = arrtbuteList.get(0).indexOf(".") != -1 ? true:false;
		boolean isAll = false;
	    Hashtable<String, Integer> attrhash = new Hashtable<String, Integer>();
	    ArrayList<String> inputData = new ArrayList<String>();
	    Hashtable<String, Table> parentTable = new Hashtable<String, Table>();
	    ArrayList<String> tmpList = new ArrayList<String>();
	    
	    if(!isAlais){
	    	for(String s: arrtbuteList){
	    		int cnt = 0;
	    		for(String n:nameList){
	    			Table t = this.getTableByName(n);
	    			if(t.getStringAttributeList().contains(s)){
	    				tmpList.add(n+"."+s);
	    				cnt++;
	    			}else if(s.equals("*")){
		    			tmpList.add(n+"."+s);
		    		}
	    		}
	    		if(cnt > 1){
	    			System.out.println("Ambiguous attribute");
	    			return null;
	    		}
	    	}
	    	arrtbuteList = (ArrayList<String>)tmpList.clone();
		    tmpList.clear();
		    isAlais = true;
	    }
	    
	    
	    for(String s: arrtbuteList){
	    	if(s.indexOf("*") != -1){ 
	    		if(s.indexOf(".") != -1){
	    			String[] name = s.split("\\.");
	    			if(this.getTableByName(name[0]) == null){
	    				System.out.println("No such alias table");
	    				return null;
	    			}
	    			ArrayList<Field> attrs = this.getTableByName(name[0]).getAttributesList();
	    			for(Field f: attrs){
    					tmpList.add(name[0] + "." + f.getName());
    				}
	    		}else{
	    			for(String t:nameList){
	    				ArrayList<Field> attrs = this.getTableByName(t).getAttributesList();
	    				for(Field f: attrs){
	    					tmpList.add(f.getName());
	    				}
	    			}
	    			
	    		}
	    	}else{
	    		if(s.indexOf(".") != -1){
	    			String[] name = s.split("\\.");
	    			if(this.getTableByName(name[0]) == null){
	    				System.out.println("No such alias table");
	    				return null;
	    			}
	    		}
	    		tmpList.add(s);
	    	}
	    }
	    arrtbuteList = (ArrayList<String>)tmpList.clone();
	  
	    int[] used = new int[arrtbuteList.size()];
	    //check repeat attr 
	    for (int j = 0; j < arrtbuteList.size(); j++) {
	    	if (attrhash.containsKey(arrtbuteList.get(j)) == true) {
	    		System.out.println("REPEAT ATTRIBUTE");
	    		return null;
	    	} else {
	    		attrhash.put(arrtbuteList.get(j), j);
	    	}
	    }

	    for (int j = 0; j < arrtbuteList.size(); j++) {
	    	used[j] = 0;
	    }
	    
	    // map table and attr
		for (int  i = 0; i < nameList.size(); i++) {
			for (int j = 0; j < arrtbuteList.size(); j++) {
				
				if (tables.containsKey(nameList.get(i))) {
					if(isAlais) {
						String[] names = arrtbuteList.get(j).split("\\.");
						Integer index = tables.get(names[0]).getAttributeIndex(names[1]);
						if(index != null) {
							used[j] = 1;
							//System.out.println(index);
							Field tmp = tables.get(names[0]).getAttributesList().get((int)index);
							tmpTable.addTableFieldByString(names[0]+"."+tmp.getName(), tmp.getType(), tmp.getStrLenLimit());
							parentTable.put(arrtbuteList.get(j), tables.get(names[0]));
						}else{
							System.out.println("No such attribute in table" + names[0]);
						}
					} else {
						
						Integer index = tables.get(nameList.get(i)).getAttributeIndex(arrtbuteList.get(j));
						
						if(index != null) {
							used[j] = 1;
							//System.out.println(index);
							Field tmp = tables.get(nameList.get(i)).getAttributesList().get((int)index);
							tmpTable.addTableFieldByString(nameList.get(i)+"."+tmp.getName(), tmp.getType(), tmp.getStrLenLimit());
							parentTable.put(arrtbuteList.get(j), tables.get(nameList.get(i)));
							
						}else if(isAll){
							
						}
					}
				} else {
					System.out.println("NO SUCH TABLE");
					return null;	//?
				}
			}
		}
		
		for (int j = 0; j < arrtbuteList.size(); j++) {
	    	 if (used[j] == 0) {
	    		 System.out.println("HAVE NONE USE ATTRIBUTE");
	    		 return null;
	    	 }
	    }
		
		if(whereTree != null)
			for(int k = 0; k < whereTree.length; k++){
				if(whereTree[k].toCharArray()[whereTree[k].length()-1] == 'a' &&
						whereTree[k].indexOf(".") == -1){
					String att = whereTree[k].substring(0, whereTree[k].length()-1);
					String tableN = "";
					int cnt = 0;
					for(String t: nameList){
						if(this.getTableByName(t).getStringAttributeList().contains(att)){
							cnt++;
							tableN = t;
						}
					}
					if(cnt > 1){
						System.out.println("Ambiguous Condition");
						return null;
					}else if(cnt == 0){
						System.out.println("No such Condition attribute");
						return null;
					}
					whereTree[k] = tableN +"."+ whereTree[k];	
				}
			}
		
		
		if (isWhere) {
	    	tmpTable = this.Where(null, whereTree);
	    	
	    	if (!isAggre) {
	    		if(isAlais){
	    			
					return tmpTable.SelectRecord(arrtbuteList);
				}else{
					
					tmpTable = tmpTable.SelectRecord(arrtbuteList);
					return tmpTable;
				}
	    	}
	    }
		
		ArrayList<Table> TableList = new ArrayList<Table>();
		ArrayList<Integer> columuSize = new ArrayList<Integer>();
		for (int j = 0; j < nameList.size(); j++) {
			TableList.add(this.getTableByName(nameList.get(j)));
			columuSize.add(this.getTableByName(nameList.get(j)).getRecordList().size());
		}
		/*
		for (int j = 0; j < arrtbuteList.size(); j++) {
			if(isAlais){
				String[] names = arrtbuteList.get(j).split("\\.");
				columuList.add(parentTable.get(arrtbuteList.get(j)).getAttributeColumn(names[1]));
				
				columuSize.add(columuList.get(columuList.size()-1).getRecordList().size());
			}else{
				columuList.add(parentTable.get(arrtbuteList.get(j)).getAttributeColumn(arrtbuteList.get(j)));
				
				columuSize.add(columuList.get(columuList.size()-1).getRecordList().size());
			}
		}
		
		int[] counter = new int[columuList.size()];
		
		for (int i = 0; i < columuList.size(); i++) {
			counter[i] = 0;
		}
		*/
		
		/*
		Table cmpTable = null;
		boolean isSameTable = true;
		
		for(int j = 0; j < arrtbuteList.size(); j++){
			if(cmpTable == null)
				cmpTable = parentTable.get(arrtbuteList.get(j));
			else if(cmpTable != parentTable.get(arrtbuteList.get(j))){
				isSameTable = false;
				break;
			}
		}
		
		if(nameList.size() < 2 && isSameTable){
			if(isAlais){
				ArrayList<String> tmpList = new ArrayList<String>();
				for(String s:arrtbuteList){
					tmpList.add(s.split("\\.")[1]);
				}
				
				return this.getTableByName(nameList.get(0)).SelectRecord(tmpList);
			}else{
				return this.getTableByName(nameList.get(0)).SelectRecord(arrtbuteList);
			}
	    }*/
		ArrayList<String> tmpstr;
		if(TableList.size() >= 2) {
			for (int i = 0; i < TableList.get(0).getRecordList().size(); i++) {
				for (int j = 0; j < TableList.get(1).getRecordList().size(); j++) {
					inputData.clear();
					tmpstr = (TableList.get(0).getRecordList().get(i).RecordToString());
					for (int k = 0; k < tmpstr.size(); k++) {
						inputData.add(tmpstr.get(k));
					}
					tmpstr = (TableList.get(1).getRecordList().get(j).RecordToString());
					for (int k = 0; k < tmpstr.size(); k++) {
						inputData.add(tmpstr.get(k));
					}
					tmpTable.addTableMemberByDefault(inputData);
				}
			}
		}else if(!isAggre){
			
			if(isAlais){
				tmpList = new ArrayList<String>();
				for(String s:arrtbuteList){
					tmpList.add(s.split("\\.")[1]);
				}
				
				return this.getTableByName(nameList.get(0)).SelectRecord(tmpList);
			}else{
				return this.getTableByName(nameList.get(0)).SelectRecord(arrtbuteList);
			}
			
		}else {
			int sum = 0;
			Table outTable = new Table("tmpTable", this);
	    	outTable.setisTmpTable(true);
	    	ArrayList<String> input = new ArrayList<String>();
	    	ArrayList<String> aggre = new ArrayList<String>();
	    	
			if (Aggre.equals("count")) {
	    		sum = tmpTable.getRecordList().size();
	    		
	    		if(!Aggreattri.equals("*")){
	    			for(int i = 0; i < tmpTable.getRecordList().size(); i++){
	    				// TODO null data
	    				if(tmpTable.getRecordList().get(i).getData(Aggreattri).getType() == Type.NULL)
	    					sum--;
	    			}
	    		}
    			input.add(""+sum);
    			outTable.addTableFieldByString("CountOf"+Aggreattri, Type.INT, 0);
    			
    			outTable.addTableMemberByDefault(input);
    			
	    	} else if (Aggre.equals("sum")) {
	    		if(Aggreattri.equals("*")){
	    			for(Field f: tmpTable.getAttributesList()){
	    				if(f.getType() != Type.INT){
	    					System.out.println("Cannot do this sum operation");
	    					return null;
	    				}else{
	    					aggre.add(f.getName());
	    					outTable.addTableFieldByString("SumOf"+f.getName(), Type.INT, 0);
	    				}
	    			}
	    		}else{
	    			aggre.add(Aggreattri);
	    			outTable.addTableFieldByString("SumOf"+Aggreattri, Type.INT, 0);
	    		}
	    		for(String s:aggre){
	    			int idx = tmpTable.getAttributeIndex(s);
	    		
	    			Field tmp = tmpTable.getAttributesList().get(idx);
		    		if(tmp.getType() != Type.INT){
		    			System.out.println("This type cannot sum");
		    			return null;
		    		}else{
		    			sum = 0;
		    			for(int i = 0; i < tmpTable.getRecordList().size(); i++){
		    				sum += tmpTable.getRecordList().get(i).getData(s).getIntData();
		    			}
		    		}
			    	input.add(""+sum);
			    	
		    		
	    		}
	    	}
	    	
	    	return outTable;
	    }
		/*
		while (true) {
			if (counter[0] >= columuSize.get(0)) {
				
				break;
			}
			inputData.clear();
			for (int j = 0; j < arrtbuteList.size(); j++) {
				if(isAlais){
					String[] names = arrtbuteList.get(j).split("\\.");
					DataUnit data = parentTable.get(arrtbuteList.get(j)).getRecordList().get(counter[j]).getData(names[1]);
					Type tp = data.getType();
					if (tp == Type.INT) {
						inputData.add(String.valueOf(data.getIntData()));
					} else if (tp == Type.VARCHAR) {
						inputData.add(data.getStrData());
					} else if (tp == Type.NULL) {
						// ????????????
					}
				}else{
					DataUnit data = parentTable.get(arrtbuteList.get(j)).getRecordList().get(counter[j]).getData(arrtbuteList.get(j));
					Type tp = data.getType();
					if (tp == Type.INT) {
						inputData.add(String.valueOf(data.getIntData()));
					} else if (tp == Type.VARCHAR) {
						inputData.add(data.getStrData());
					} else if (tp == Type.NULL) {
						// ????????????
					}
					
				}
			}
			
			tmpTable.addTableMemberByDefault(inputData);
			//tmpTable.printAllRecord();
			counter[columuList.size() - 1]++;
			
			//System.out.println("test: "+ columuList.size());
			for (int i = columuList.size() - 1; i > 0; i--) {
				
				//System.out.println("test: "+i);
				if (counter[i] == columuSize.get(i)) {
					counter[i] = 0;
					counter[i-1]++;
				}
			}
		}
		*/
		//tmpTable.printTableField();
		
		return tmpTable;
	}
	
	public Table Where(Table T1, String[] whereTree) throws Warning, InsertionException {
		Table tmpTable = new Table("tmpTable", this);
		tmpTable.setisTmpTable(true);
		// 'B' for AND in OR
		if (whereTree[0].toCharArray()[whereTree[0].length() - 1] == 'L') {
			String[] whereTreeLeft = {whereTree[1], whereTree[3], whereTree[4]};
			String[] whereTreeRight = {whereTree[2], whereTree[5], whereTree[6]};
			Table LeftTable = this.Where(T1, whereTreeLeft);
			Table RightTable = this.Where(T1, whereTreeRight);
			
			if (whereTree[0].equals("andL")) {
				
				if(LeftTable.isInnerJoin){
					
					String[] name1 = whereTree[5].split("\\.");
					String[] name2;
					if(whereTree[5].equals(whereTree[3])){
						name2 = whereTree[4].split("\\.");
						tmpTable = this.innerJoin(RightTable, this.getTableByName(name2[0]), 
								name1[1].substring(0, name1[1].length()-1), name2[1].substring(0, name2[1].length()-1));
					}else if(whereTree[5].equals(whereTree[4])){
						name2 = whereTree[3].split("\\.");
						tmpTable = this.innerJoin(RightTable, this.getTableByName(name2[0]), 
								name1[1].substring(0, name1[1].length()-1), name2[1].substring(0, name2[1].length()-1));
					}else{
				
						String[] newWhereTree = new String[3];
						newWhereTree[0] = whereTree[2];
						newWhereTree[1] = whereTree[5];
						newWhereTree[2] = whereTree[6];
						tmpTable = Where(LeftTable,newWhereTree);
					}
					
					return tmpTable;
					
				}else if(RightTable.isInnerJoin){
					String[] name1 = whereTree[3].split("\\.");
					String[] name2;
					if(whereTree[3].equals(whereTree[5])){
						name2 = whereTree[6].split("\\.");
						tmpTable = this.innerJoin(LeftTable, this.getTableByName(name2[0]), 
								name1[1].substring(0, name1[1].length()-1), name2[1].substring(0, name2[1].length()-1));
					}else if(whereTree[3].equals(whereTree[6])){
						name2 = whereTree[5].split("\\.");
						tmpTable = this.innerJoin(LeftTable, this.getTableByName(name2[0]), 
								name1[1].substring(0, name1[1].length()-1), name2[1].substring(0, name2[1].length()-1));
					
					}else{
						String[] newWhereTree = new String[3];
						newWhereTree[0] = whereTree[1];
						newWhereTree[1] = whereTree[3];
						newWhereTree[2] = whereTree[4];
						tmpTable = Where(RightTable,newWhereTree);
					}
					
					
					return tmpTable;
				}else{
					
					tmpTable.addTableFieldByField(RightTable.getAttributesList());
					for(int i = 0; i < LeftTable.getRecordList().size(); i++){
						Record record = LeftTable.getRecordList().get(i);
						for(int j = 0; j < RightTable.getRecordList().size(); j++){
							if(record.equals(RightTable.getRecordList().get(j))){
								tmpTable.addTableMemberByDefault(record.RecordToString());
								break;
							}
						}
					}
					
					
				}

			} else if (whereTree[0].equals("orL")) {
				tmpTable.addTableFieldByField(LeftTable.getAttributesList());
				
				for (int i = 0; i < LeftTable.getRecordList().size(); i++){
					tmpTable.addTableMemberByDefault(LeftTable.getRecordList().get(i).RecordToString());
				}
				
				for (int i = 0; i < RightTable.getRecordList().size(); i++) {
					Record record = RightTable.getRecordList().get(i);
					for (int j = 0; j < LeftTable.getRecordList().size(); j++){
						if(record.equals(LeftTable.getRecordList().get(j))){
							continue;
						}
					}
					tmpTable.addTableMemberByDefault(record.RecordToString());
				}			
			}
			
		} else if (whereTree[0].toCharArray()[whereTree[0].length() - 1] == 'o') {
			ArrayList<String> inputData = new ArrayList<String>();
			
			if (whereTree[0].equals(">o")) {
				if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'i') {
					String[] name1;
					name1 = whereTree[1].split("\\.");
					
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}
					tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable = this.tables.get(name1[0]);
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					String attri = name1[1].substring(0, name1[1].length()-1);
					
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.INT) {
							System.out.println("attri not int type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (oriTable.getRecordList().get(i).getData(attri).getIntData() > Integer.parseInt(whereTree[2].substring(0, whereTree[2].length() - 1))) {
								
								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE1");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'i' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					
				} else {
					//??????????
				}
			}else if(whereTree[0].equals("<o")){
				if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'i') {
					String[] name1;
					name1 = whereTree[1].split("\\.");
					// TODO not everything has .
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table "+ name1[0]);
						return null;
					}
					tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable = this.tables.get(name1[0]);
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					String attri = name1[1].substring(0, name1[1].length()-1);
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.INT) {
							System.out.println("attri not int type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (oriTable.getRecordList().get(i).getData(attri).getIntData() < Integer.parseInt(whereTree[2].substring(0, whereTree[2].length() - 1))) {
								
								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE2");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'i' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					
				} else {
					//??????????
				}
			}else if(whereTree[0].equals("<>o")){
				if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'c') {
					String[] name1;
					name1 = whereTree[1].split("\\.");
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable = this.tables.get(name1[0]);
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					String attri = name1[1].substring(0, name1[1].length()-1);
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.VARCHAR) {
							System.out.println("attri not string type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (!oriTable.getRecordList().get(i).getData(attri).getStrData().equals(whereTree[2].substring(0, whereTree[2].length() - 1).toLowerCase())) {
								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE3");
						return null;
					}
					
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 's' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					String[] name1, name2;
					name1 = whereTree[2].split(".");
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}tmpTable.setName(name1[0]);
					
					Integer index = this.getTableByName(name1[0]).getAttributeIndex(name1[1]);
					if (index != null) {
						Table tmpT1 = this.getTableByName(name1[0]).getAttributeColumn(name1[1]);
						if (tmpT1.getRecordList().get(0).getData(name1[1]).getType() != Type.VARCHAR) {
							System.out.println("attri not string type");
							return null;
						}
						Field tmpField =  this.getTableByName(name1[0]).getAttributesList().get(index);
						tmpTable.addTableFieldByString(tmpField.getName(), tmpField.getType(), tmpField.getStrLenLimit());
						ArrayList<String> tmpstr = new ArrayList<String>();
						for (int i = 0; i < tmpT1.getRecordList().size(); i++) {
							if (!tmpT1.getRecordList().get(i).getData(name1[1]).getStrData().equals(name1[1])) {
								tmpstr.add(tmpT1.getRecordList().get(i).getData(name1[1]).getStrData());
							}
							
						}
						tmpTable.addTableMemberByDefault(tmpstr);
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE4");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'i') {
					String[] name1;
					name1 = whereTree[1].split("\\.");
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable = this.tables.get(name1[0]);
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					String attri = name1[1].substring(0, name1[1].length()-1);
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.INT) {
							System.out.println("attri not int type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (oriTable.getRecordList().get(i).getData(attri).getIntData() != Integer.parseInt(whereTree[2].substring(0, whereTree[2].length() - 1))) {
								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE5");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'i' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					
				} else {
					//??????????
				}
			}else if(whereTree[0].equals("=o")){
				if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					String[] name1, name2;
					name1 = whereTree[1].split("\\.");
					name2 = whereTree[2].split("\\.");
					tmpTable = this.innerJoin(this.getTableByName(name1[0]), this.getTableByName(name2[0]), 
							name1[1].substring(0, name1[1].length()-1), name2[1].substring(0, name2[1].length()-1));
					tmpTable.isInnerJoin = true;
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'c') {
	
					String[] name1;
					
					name1 = whereTree[1].split("\\.");
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable;
					String attri = "";
					if(T1 == null){
						oriTable = this.tables.get(name1[0]);
						attri = name1[1].substring(0, name1[1].length()-1);
					}else{
						oriTable = T1;
						attri = whereTree[1].substring(0, whereTree[1].length()-1);
					}
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.VARCHAR) {
							System.out.println("attri not string type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (oriTable.getRecordList().get(i).getData(attri).getStrData().equals(whereTree[2].substring(0, whereTree[2].length() - 1).toLowerCase())) {

								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE6");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 's' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'a' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'i') {
					String[] name1;
					name1 = whereTree[1].split("\\.");
					if (this.tables.containsKey(name1[0]) == false) {
						System.out.println("NO Table " + name1[0]);
						return null;
					}tmpTable.setName(name1[0]);
					//Table tmpT1 = new Table("tmpTable", this);
					Table oriTable = this.tables.get(name1[0]);
					tmpTable.addTableFieldByField(oriTable.getAttributesList());
					
					String attri = name1[1].substring(0, name1[1].length()-1);
					Integer index = oriTable.getAttributeIndex(attri);
					if (index != null) {
						if (oriTable.getRecordList().get(0).getData(attri).getType() != Type.INT) {
							System.out.println("attri not int type");
							return null;
						}
						for (int i = 0; i < oriTable.getRecordList().size(); i++) {
							if (oriTable.getRecordList().get(i).getData(attri).getIntData() == Integer.parseInt(whereTree[2].substring(0, whereTree[2].length() - 1))) {
								inputData = oriTable.getRecordList().get(i).RecordToString();
								tmpTable.addTableMemberByDefault(inputData);
							}
							
						}
						
					} else {
						System.out.println("NO SUCH ATTRIBUTE IN " + name1[0] +" TABLE");
						return null;
					}
				} else if (whereTree[1].toCharArray()[whereTree[1].length() - 1] == 'i' && whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'a') {
					
				} else {
					//??????????
				}
				
			}
		} else {
			//?????
		}
		
		
		/*if (whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'O') {
			if(whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'A') {
				// left child is attri right child is attri
				if (whereTree[0] == "=") {
					// inner join
					String[] name1, name2;
					name1 = whereTree[1].split(".");
					name2 = whereTree[2].split(".");
					
					
					return this.innerJoin(this.getTableByName(name1[0]), this.getTableByName(name2[0]), name1[1], name2[2]);
				}
			} else if () {
				// left child is attri right child is op
				if (whereTree.length == 3) {
					if (whereTree[1].indexOf(".") != -1) {
						String[] name1;
						name1 = whereTree[1].split(".");
						Table tmp = this.getTableByName(name1[0]).getAttributeColumn(name1[1]);
						
					}
				} else {
					whereTree[]
				}
			}
		} else if (whereTree[2].toCharArray()[whereTree[2].length() - 1] == 'A') {
			if() {
				// left child is op right child is attri
				
			} else {
				// left child is op right child is op
				
			}
		}*/
		
		
		
		return tmpTable;
	}
	
	
	//need error handle
	public Table innerJoin(Table T1, Table T2, String attr1, String attr2) throws Warning, InsertionException {
		Table tmpTable = new Table("tmpTable", this);
		tmpTable.setisTmpTable(true);
		
		Hashtable tmpHash = new Hashtable<String, Integer>();
		boolean findFlag;
		for (int i = 0; i < T1.getAttributesList().size(); i++) {
			Field tmp = T1.getAttributesList().get(i);
			tmpTable.addTableFieldByString(T1.getName()+"."+tmp.getName(), tmp.getType(), tmp.getStrLenLimit());	
		}	
		for (int i = 0; i < T2.getAttributesList().size(); i++) {
			Field tmp = T2.getAttributesList().get(i);
			tmpTable.addTableFieldByString(T2.getName()+"."+tmp.getName(), tmp.getType(), tmp.getStrLenLimit());
		}
		
		ArrayList<String> inputData = new ArrayList<String>();
		for (int i = 0; i < T1.getRecordList().size(); i++) {
			DataUnit tmpData1 = T1.getRecordList().get(i).getData(attr1);
			inputData.clear();
			findFlag = false;
			for (int j = 0; j < T2.getRecordList().size(); j++) {
				if (tmpData1.getType() == Type.VARCHAR) {
					String str1 = T1.getRecordList().get(i).getData(attr1).getStrData();
					String str2 = T2.getRecordList().get(j).getData(attr2).getStrData();
					
					if (str1.equals(str2)) {
						findFlag = true;
						for (int k = 0; k < T1.getAttributesList().size(); k++) {
							if (T1.getAttributesList().get(k).getType() == Type.VARCHAR) {
								inputData.add(T1.getRecordList().get(i).getData(T1.getAttributesList().get(k).getName()).getStrData());
							} else if (T1.getAttributesList().get(k).getType() == Type.INT) {
								inputData.add(String.valueOf(T1.getRecordList().get(i).getData(T1.getAttributesList().get(k).getName()).getIntData()));
							} else if (T1.getAttributesList().get(k).getType() == Type.NULL) {
								
							}
						}
						for (int k = 0; k < T2.getAttributesList().size(); k++) {
							if (T2.getAttributesList().get(k).getType() == Type.VARCHAR) {
								inputData.add(T2.getRecordList().get(j).getData(T2.getAttributesList().get(k).getName()).getStrData());
							} else if (T2.getAttributesList().get(k).getType() == Type.INT) {
								inputData.add(String.valueOf(T2.getRecordList().get(j).getData(T2.getAttributesList().get(k).getName()).getIntData()));
							} else if (T2.getAttributesList().get(k).getType() == Type.NULL) {
								
							}
						}
					}
				} else if (tmpData1.getType() == Type.INT) {
					
					int int1 = T1.getRecordList().get(i).getData(attr1).getIntData();
					int int2 = T2.getRecordList().get(j).getData(attr2).getIntData();
					if (int1 == int2) {
						findFlag = true;
						for (int k = 0; k < T1.getAttributesList().size(); k++) {
							if (T1.getAttributesList().get(k).getType() == Type.VARCHAR) {
								inputData.add(T1.getRecordList().get(i).getData(T1.getAttributesList().get(k).getName()).getStrData());
							} else if (T1.getAttributesList().get(k).getType() == Type.INT) {
								inputData.add(String.valueOf(T1.getRecordList().get(i).getData(T1.getAttributesList().get(k).getName()).getIntData()));
							} else if (T1.getAttributesList().get(k).getType() == Type.NULL) {
								
							}
						}
						for (int k = 0; k < T2.getAttributesList().size(); k++) {
							if (T2.getAttributesList().get(k).getType() == Type.VARCHAR) {
								inputData.add(T2.getRecordList().get(j).getData(T2.getAttributesList().get(k).getName()).getStrData());
							} else if (T2.getAttributesList().get(k).getType() == Type.INT) {
								inputData.add(String.valueOf(T2.getRecordList().get(j).getData(T2.getAttributesList().get(k).getName()).getIntData()));
							} else if (T2.getAttributesList().get(k).getType() == Type.NULL) {
								
							}
						}
					}
				} else if (tmpData1.getType() == Type.NULL) {
					//???
				}
				
			}
			if (findFlag) {
				tmpTable.addTableMemberByDefault(inputData);
			}
		}
		
		/*
		for (int i = 0; i < T1.getRecordList().size(); i++) {
			DataUnit tmpData = T1.getRecordList().get(i).getData(attr1);
			if (tmpData.getType() == Type.VARCHAR) {
				tmpHash.put(tmpData.getStrData(), i);
			} else if (tmpData.getType() == Type.INT) {
				tmpHash.put(String.valueOf(tmpData.getIntData()), i);
			} else if (tmpData.getType() == Type.NULL) {
				//???
			}
		}
		
		ArrayList<String> inputData = new ArrayList<String>();
		for (int i = 0; i < T2.getRecordList().size(); i++) {
			DataUnit tmpData = T2.getRecordList().get(i).getData(attr2);
			inputData.clear();
			findFlag = false;
			if (tmpData.getType() == Type.VARCHAR) {
				if (tmpHash.containsKey(tmpData.getStrData()) == true) {
					findFlag = true;
					Integer index = (Integer) tmpHash.get(tmpData.getStrData());
					for (int j = 0; j < T1.getAttributesList().size(); j++) {
						if (T1.getAttributesList().get(j).getType() == Type.VARCHAR) {
							inputData.add(T1.getRecordList().get(index).getData(T1.getAttributesList().get(j).getName()).getStrData());
						} else if (T1.getAttributesList().get(j).getType() == Type.INT) {
							inputData.add(String.valueOf(T1.getRecordList().get(index).getData(T1.getAttributesList().get(j).getName()).getIntData()));
						} else if (T1.getAttributesList().get(j).getType() == Type.NULL) {
							
						}
					}
					for (int j = 0; j < T2.getAttributesList().size(); j++) {
						if (T1.getAttributesList().get(j).getType() == Type.VARCHAR) {
							inputData.add(T2.getRecordList().get(i).getData(T2.getAttributesList().get(j).getName()).getStrData());
						} else if (T1.getAttributesList().get(j).getType() == Type.INT) {
							inputData.add(String.valueOf(T2.getRecordList().get(i).getData(T2.getAttributesList().get(j).getName()).getIntData()));
						} else if (T1.getAttributesList().get(j).getType() == Type.NULL) {
							
						}
					}
				}
			} else if (tmpData.getType() == Type.INT) {
				
				if (tmpHash.containsKey(String.valueOf(tmpData.getIntData())) == true) {
					findFlag = true;
					Integer index = (Integer) tmpHash.get(String.valueOf(tmpData.getIntData()));
					
					for (int j = 0; j < T1.getAttributesList().size(); j++) {
						if (T1.getAttributesList().get(j).getType() == Type.VARCHAR) {
							inputData.add(T1.getRecordList().get(index).getData(T1.getAttributesList().get(j).getName()).getStrData());
						} else if (T1.getAttributesList().get(j).getType() == Type.INT) {
							inputData.add(String.valueOf(T1.getRecordList().get(index).getData(T1.getAttributesList().get(j).getName()).getIntData()));
						} else if (T1.getAttributesList().get(j).getType() == Type.NULL) {
							
						}
					}
					Record record = T2.getRecordList().get(i);
					for (int j = 0; j < T2.getAttributesList().size(); j++) {
						
						if (T2.getAttributesList().get(j).getType() == Type.VARCHAR) {
							inputData.add(record.getData(T2.getAttributesList().get(j).getName()).getStrData());
						} else if (T2.getAttributesList().get(j).getType() == Type.INT) {
							inputData.add(String.valueOf(record.getData(T2.getAttributesList().get(j).getName()).getIntData()));
						} else if (T2.getAttributesList().get(j).getType() == Type.NULL) {
							
						}
						
					}
					
				}
				
			} else if (tmpData.getType() == Type.NULL) {
				//???
			}
			//tmpTable.addTableMemberByString(joinArribute, inputData);
			//for (int j = 0; j < inputData.size(); j++) {
			//	System.out.println(inputData.get(j));
			//}
			if (findFlag == true) {
				tmpTable.addTableMemberByDefault(inputData);
			}
		}
		//System.out.println(tmpTable.getRecordList().size());
		//tmpTable.printAllRecord();
		*/
		return tmpTable;
	}
	
	public Table MergeTowColumn(Table T1, Table T2) throws Warning, InsertionException {
		Table tmpTable = new Table("tmpTable", this);
		tmpTable.setisTmpTable(true);
		ArrayList<String> tmpstr = new ArrayList<String>();
		if (T1.getRecordList().size() != T2.getRecordList().size()) {
			return null;
		}
		if(T1.getAttributesList().size() == 1) {
			Field tmpField = T1.getAttributesList().get(0);
			tmpTable.addTableFieldByString(tmpField.getName(), tmpField.getType(), tmpField.getStrLenLimit());
		}
		if(T2.getAttributesList().size() == 1) {
			Field tmpField = T2.getAttributesList().get(0);
			tmpTable.addTableFieldByString(tmpField.getName(), tmpField.getType(), tmpField.getStrLenLimit());
		}
		for (int i = 0; i < T1.getRecordList().size(); i++) {
			if (T1.getRecordList().get(i).getData(T1.getAttributesList().get(0).getName()).getType() == Type.INT) {
				tmpstr.add(String.valueOf(T1.getRecordList().get(i).getData(T1.getAttributesList().get(0).getName()).getIntData()));
			} else if (T1.getRecordList().get(i).getData(T1.getAttributesList().get(0).getName()).getType() == Type.VARCHAR) {
				tmpstr.add(T1.getRecordList().get(i).getData(T1.getAttributesList().get(0).getName()).getStrData());
			} else if (T1.getRecordList().get(i).getData(T1.getAttributesList().get(0).getName()).getType() == Type.NULL) {
				//?????????
			}
			if (T2.getRecordList().get(i).getData(T2.getAttributesList().get(0).getName()).getType() == Type.INT) {
				tmpstr.add(String.valueOf(T2.getRecordList().get(i).getData(T2.getAttributesList().get(0).getName()).getIntData()));
			} else if (T2.getRecordList().get(i).getData(T2.getAttributesList().get(0).getName()).getType() == Type.VARCHAR) {
				tmpstr.add(T2.getRecordList().get(i).getData(T2.getAttributesList().get(0).getName()).getStrData());
			} else if (T2.getRecordList().get(i).getData(T2.getAttributesList().get(0).getName()).getType() == Type.NULL) {
				
			}
			tmpTable.addTableMemberByDefault(tmpstr);
		}
		return tmpTable;
		
	}
	
}
