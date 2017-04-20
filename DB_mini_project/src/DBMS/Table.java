package DBMS;

import java.util.*;
import DBMS.Field.Type;
import ExceptionHandlers.InsertionException;
import ExceptionHandlers.Warning;

/* * * * * * * * * * * * * * * * * * * * * * * 
 * initial value
 * havePrimaryKey false 
 * * * * * * * * * * * * * * * * * * * * * * */

public class Table {
	private String tableName;
	private ArrayList<Field> tuple;
	private Hashtable<String, Integer> attributeIndex;
	private ArrayList<Record> membersWithOutPrimaryKey;
	private Hashtable<String, Record> membersWithPrimaryKey;
	//private Hashtable<String, ArrayList<Record>> attributeContent;
	private Field primaryKey;
	private boolean havePrimaryKey;
	private Database parentDatabase;
	private HashMap<String, Integer> longestValueLength;
	public boolean errorFlag, isInnerJoin;
	private boolean isTmpTable;
	
	
	/*Table Constructor*/
	/*************************************************************************/
	
	public Table(String name, Database parent) {
		this.tuple = new ArrayList<Field>();
		this.membersWithOutPrimaryKey = new ArrayList<Record>();
		this.membersWithPrimaryKey = new Hashtable<String, Record>();
		this.tableName = name;
		this.parentDatabase = parent;
		this.longestValueLength = new HashMap<String, Integer>();
		this.havePrimaryKey = false;
		this.attributeIndex = new Hashtable<String, Integer>();
		this.isTmpTable = false;
		this.isInnerJoin = false;
		//this.attributeContent = new Hashtable<String, ArrayList<Record>>();
	}
	
	public Table(String name, ArrayList<Field> schema, Field primaryKey, Database parent) {
		this.tuple = schema;
		this.membersWithOutPrimaryKey = new ArrayList<Record>();
		this.membersWithPrimaryKey = new Hashtable<String, Record>();
		this.primaryKey = primaryKey;
		this.tableName = name;
		this.parentDatabase = parent;
		this.longestValueLength = new HashMap<String, Integer>();
		this.havePrimaryKey = false;
		this.attributeIndex = new Hashtable<String, Integer>();
		this.isTmpTable = false;
		this.isInnerJoin = false;
		//this.attributeContent = new Hashtable<String, ArrayList<Record>>();
	}
	
	/*************************************************************************/
	
	public void setName(String name) {
		this.tableName = name;
	}
	
	public String getName() {
		return this.tableName;
	}
	
	public Record getRecordByPrimaryKey(String key) {
		return membersWithPrimaryKey.get(key);
	}
	
	public ArrayList<Field> getAttributesList() {
		return this.tuple;
	}
	
	public ArrayList<Record> getRecordList() {
		return this.membersWithOutPrimaryKey;
	}
	
	public void printPrimaryKey() {
		System.out.println(primaryKey.getName());
	}
	
	public void setPrimary(boolean b) {
		this.havePrimaryKey = b;
	}
	
	public void setisTmpTable(boolean b) {
		this.isTmpTable = b;
	}
	
	public void printPrimary() {
		System.out.println(this.havePrimaryKey);
	}
	
	public void emptyMember() {
		membersWithOutPrimaryKey.clear();
	}
	
	public ArrayList<String> getStringAttributeList() {
		ArrayList<String> tmpStr = new ArrayList<String>();
		for (int i = 0; i < this.tuple.size(); i++) {
			tmpStr.add(tuple.get(i).getName());
		}
		return tmpStr;
	}
	
	public Integer getAttributeIndex(String name) {
		if (this.attributeIndex.containsKey(name) == true) {
			return this.attributeIndex.get(name);
		} else {
			return null;
		}
	}
	
	public Table getAttributeColumn(String attr) throws Warning, InsertionException {
		Table tmpTable = new Table("tmpTable", this.parentDatabase);
		tmpTable.setisTmpTable(true);
		Field tmpField = this.getAttributesList().get(this.getAttributeIndex(attr));
		tmpTable.addTableFieldByString(tmpField.getName(), tmpField.getType(), tmpField.getStrLenLimit());
		ArrayList<String> tmpString = new ArrayList<String>();
		for (int i = 0; i < this.getRecordList().size(); i++) {
			tmpString.clear();
			if (this.getRecordList().get(i).getData(attr).getType() == Type.INT) {
				//System.out.println(String.valueOf(this.getRecordList().get(i).getData(attr).getIntData()));
				tmpString.add(String.valueOf(this.getRecordList().get(i).getData(attr).getIntData()));
				tmpTable.addTableMemberByDefault(tmpString);
			} else if (this.getRecordList().get(i).getData(attr).getType() == Type.VARCHAR) {
				tmpString.add(this.getRecordList().get(i).getData(attr).getStrData());
				tmpTable.addTableMemberByDefault(tmpString);
			} else if (this.getRecordList().get(i).getData(attr).getType() == Type.NULL) {
				//????????
			}
		}
		
		System.out.println(tmpTable.getRecordList().size());
		
		return tmpTable;
	}
	
	/*Input Interface for field*/
	/*************************************************************************/
	
	
	/*multiple attribute, by field, with primary key(in string)*/
	public void addTableFieldByField(ArrayList<Field> schema, String primaryKey)
	{
		Type primaryType = Type.NULL;
		for(int i = 0; i < schema.size(); i++) {
			
			this.tuple.add(schema.get(i));
			this.attributeIndex.put(schema.get(i).getName(), i);
			if(schema.get(i).getName().equals(primaryKey)) {
				primaryType = schema.get(i).getType();				// need a not found error handle?
			}
			//this.longestValueLength.put(schema.get(i).getName(), 0);
			this.longestValueLength.put(schema.get(i).getName(), schema.get(i).getName().length());
	    }
		this.primaryKey = new Field(primaryKey, this, primaryType);
		this.havePrimaryKey = true;
		
	}
	
	/*multiple attribute, by field, with primary key(in field)*/
	public void addTableFieldByField(ArrayList<Field> schema, Field primaryKey)
	{
		for(int i = 0; i < schema.size(); i++) {
			
			this.tuple.add(schema.get(i));
			this.attributeIndex.put(schema.get(i).getName(), this.tuple.size());
			//this.longestValueLength.put(schema.get(i).getName(), 0);
			this.longestValueLength.put(schema.get(i).getName(), schema.get(i).getName().length());
	    }
		this.primaryKey = primaryKey;
		this.havePrimaryKey = true;
	}
	
	/*multiple attribute, by string, with primary key(string)*/
	public void addTableFieldByString(ArrayList<String> attributeName, ArrayList<String> attributeType, String primaryKey)
	{
		Type primaryType = Type.NULL;
		
		if(attributeName.size() != attributeType.size()) {
			System.out.println("INPUT SIZE NOT FIT");
		}
		
		for(int i = 0; i < attributeName.size(); i++){
			
			if(attributeName.get(i).equals(primaryKey)) {
				if(attributeType.get(i).equals("INT")) {
					primaryType = Type.INT;
				} else if(attributeType.get(i).equals("VARCHAR")) {
					primaryType = Type.VARCHAR;
				} else {
					System.out.println("INSERT TYPE NOT ALLOW");
				}
			}
			
			if(attributeType.get(i).equals("INT")) {
				this.tuple.add(new Field(attributeName.get(i), this, Type.INT, 0));
				this.attributeIndex.put(attributeName.get(i), this.tuple.size()-1);
			} else if(attributeType.get(i).equals("VARCHAR")) {
				this.tuple.add(new Field(attributeName.get(i), this, Type.VARCHAR, 40));
				this.attributeIndex.put(attributeName.get(i), this.tuple.size()-1);
			} else {
				System.out.println("INSERT TYPE NOT ALLOW");
			}
	    }
		
		this.primaryKey = new Field(primaryKey, this, primaryType, 40);		//40?
		havePrimaryKey = true;
	}
	
	/*single attribute, by string*/
	public void addTableFieldByString(String name, Type type)
	{
		this.tuple.add(new Field(name, this, type));
		this.attributeIndex.put(name, this.tuple.size()-1);
		for(int i = 0; i < membersWithOutPrimaryKey.size(); i++) {
			//membersWithOutPrimaryKey.get(i).addField(new Field(name, type));			// need error test 
			membersWithOutPrimaryKey.get(i).setNewData(name, type, 0, "");
		}
		//this.longestValueLength.put(name, 0);
		this.longestValueLength.put(name, name.length());
	}
	
	/*single attribute, by string with LenLimit*/
	public void addTableFieldByString(String name, Type type, int lenLimit)
	{
		this.tuple.add(new Field(name, this, type, lenLimit));
		this.attributeIndex.put(name, this.tuple.size()-1);
		for(int i = 0; i < membersWithOutPrimaryKey.size(); i++) {
			//membersWithOutPrimaryKey.get(i).addField(new Field(name, type));			// need error test 
			membersWithOutPrimaryKey.get(i).setNewData(name, type, 0, "");
		}
		//this.longestValueLength.put(name, 0);
		this.longestValueLength.put(name, name.length());
	}
	
	/*multiple attribute, by field, without primary key*/
	public void addTableFieldByField(ArrayList<Field> schema)
	{
		for(int i = 0; i < schema.size(); i++) {
			
			this.tuple.add(schema.get(i));
			
			this.attributeIndex.put(schema.get(i).getName(), this.tuple.size()-1);
			this.longestValueLength.put(schema.get(i).getName(), 0);
	    }
	}
	
	/*multiple attribute, by string, without primary*/
	public void addTableFieldByString(ArrayList<String> attributeName, ArrayList<String> attributeType)
	{
		if(attributeName.size() != attributeType.size()) {
			System.out.println("INPUT SIZE NOT FIT");
		}
		
		for(int i = 0; i < attributeName.size(); i++){
			if(attributeType.get(i).equals("INT")) {
				this.tuple.add(new Field(attributeName.get(i), this, Type.INT, 0));
				this.attributeIndex.put(attributeName.get(i), this.tuple.size()-1);
			} else if(attributeType.get(i).equals("VARCHAR")) {
				this.tuple.add(new Field(attributeName.get(i), this, Type.VARCHAR, 40));
				this.attributeIndex.put(attributeName.get(i), this.tuple.size()-1);
			} else {
				System.out.println("INSERT TYPE NOT ALLOW");
			}
	    }
	}
	
	
	/*************************************************************************/
	
	public void addTableField(ArrayList<Field> schema, String primaryKey, int strLimit)
	{
		Type primaryType = Type.NULL;
		for(int i = 0; i < schema.size(); i++) {
			
			this.tuple.add(schema.get(i));
			if(schema.get(i).getName().equals(primaryKey)) {
				primaryType = schema.get(i).getType();				// need a not found error handle?
			}
	    }
		this.primaryKey = new Field(primaryKey, this, primaryType, strLimit);
		this.havePrimaryKey = true;
	}
	
	
	/*Input Interface for record*/
	/**
	 * @throws InsertionException 
	 * @throws Warning ***********************************************************************/
	
	public boolean addTableMemberByDefault (ArrayList<String> inputData) throws Warning, InsertionException
	{	
		ArrayList<TransactionData> tmp;
		tmp = new ArrayList<TransactionData>();
		
		
		if(this.havePrimaryKey == true) {
			for(int i = 0; i < this.tuple.size(); i++) {
				if(this.tuple.get(i).getName().equals(primaryKey.getName())) {

					if(i >= inputData.size()) {
						System.out.println("NO PRIMARY KEY INPUT");
						return false;
					}
				}
			}
		}
		
		
		for(int i = 0; i < this.tuple.size(); i++) {
			if(i < inputData.size()) {
				tmp.add(new TransactionData(this.tuple.get(i).getName(), this.tuple.get(i).getType(), inputData.get(i), this));
			} else {
				if(this.tuple.get(i).getType() == Type.INT) {
					tmp.add(new TransactionData(this.tuple.get(i).getName(), this.tuple.get(i).getType(), "0", this));
				} else if(this.tuple.get(i).getType() == Type.VARCHAR) {
					tmp.add(new TransactionData(this.tuple.get(i).getName(), this.tuple.get(i).getType(), "", this));
				}
			}
			if(tmp.get(tmp.size() - 1).createSuccessly == false) {
				return false;
			}
			
		}
		//System.out.println("77777777"+this.isTmpTable);
		return this.addTableMember(tmp);
	}
	
	public boolean addTableMemberByString (ArrayList<String> inputName, ArrayList<String> inputData) throws Warning, InsertionException
	{	
		ArrayList<TransactionData> tmp;
		tmp = new ArrayList<TransactionData>();
		Type type = Type.NULL;
		
		boolean finder = false;
		for(int i = 0; i < inputName.size(); i++) {
			finder = false;
			//System.out.println(inputName.get(i));
			for (int j= 0; j < this.tuple.size(); j++) {	
				if (inputName.get(i).equals(this.tuple.get(j).getName())) {
					finder = true;
					break;
				}
				
			}
			if(finder == false) {
				System.out.printf("CAN'T FIND THIS ATTRIBUTE %s\n", inputName.get(i));
				return false;
			}
		}
		
		
		
		for(int i = 0; i < this.tuple.size(); i++) {
			int index = -1;
			type = this.tuple.get(i).getType();
			for (int j= 0; j < inputName.size(); j++) {				// not a good search
				if (this.tuple.get(i).getName().equals(inputName.get(j))) {
					index = j;
					break;
				}
			}
			if(index == -1) {
				if(type == Type.INT) {
					tmp.add(new TransactionData(this.tuple.get(i).getName(), type, "0", this));
				} else if(type == Type.VARCHAR) {
					tmp.add(new TransactionData(this.tuple.get(i).getName(), type, "", this));
				}
			} else {
				if(type == Type.INT) {
					tmp.add(new TransactionData(inputName.get(index), type, inputData.get(index), this));
				} else if(type == Type.VARCHAR) {
					tmp.add(new TransactionData(inputName.get(index), type, inputData.get(index), this));
				}
			}
			if(tmp.get(tmp.size() - 1).createSuccessly == false) {
				return false;
			}
		}
		
		return this.addTableMember(tmp);
	}
	
	public boolean addTableMember(ArrayList<TransactionData> inputData) throws InsertionException
	{	
		
		if(havePrimaryKey == true) {
			for(int i = 0; i < inputData.size(); i++){
				if(inputData.get(i).getName().equals(primaryKey.getName())) {
					
					//repeat primary key check
					String pName = primaryKey.getName();
					for(int j = 0; j < membersWithOutPrimaryKey.size(); j++) {
						if(inputData.get(i).getType() == Type.INT) {			//check type with primary key?
							if(inputData.get(i).int_data == membersWithOutPrimaryKey.get(j).getData(pName).getIntData()) {
								System.out.println("Duplicate primary key");
								return false;
							}
						} else if(inputData.get(i).getType() == Type.VARCHAR) {
							if(inputData.get(i).str_data == membersWithOutPrimaryKey.get(j).getData(pName).getStrData()) {
								System.out.println("Duplicate primary key");
								return false;
							}
						} else {
							System.out.println("INSERT TYPE NOT ALLOW");
							return false;
						}
					}
				}
			}
		}
		
		Record newRecord = new Record(inputData, this);
		
		if(this.havePrimaryKey == false && this.isTmpTable == false) {
			boolean checkSame = true;
			//this.membersWithOutPrimaryKey.size()
			for(int i = 0; i < this.membersWithOutPrimaryKey.size(); i++) {
				//System.out.println(i);
				//System.out.println(this.membersWithOutPrimaryKey.get(i).getData("name").getStrData());
				checkSame = true;
				for(int j = 0; j < this.tuple.size(); j++) {
					if(this.tuple.get(j).getType() == Type.INT) {
						//System.out.println(newRecord.getData(this.tuple.get(j).getName()).getIntData() + " " + membersWithOutPrimaryKey.get(i).getData(this.tuple.get(j).getName()).getIntData());
						if(newRecord.getData(this.tuple.get(j).getName()).getIntData() != membersWithOutPrimaryKey.get(i).getData(this.tuple.get(j).getName()).getIntData()) {
							checkSame = false;
							break;
						}
						
					} else if(this.tuple.get(j).getType() == Type.VARCHAR) {
						//System.out.println(newRecord.getData(this.tuple.get(j).getName()).getStrData() + " " + membersWithOutPrimaryKey.get(i).getData(this.tuple.get(j).getName()).getStrData());
						if(newRecord.getData(this.tuple.get(j).getName()).getStrData() != membersWithOutPrimaryKey.get(i).getData(this.tuple.get(j).getName()).getStrData()) {
							checkSame = false;
							break;
						}
					}
					else {
						System.out.println(this.tuple.get(j).getName());
					}
				}
				if (checkSame == true) {
					break;
				}
			}
			if(checkSame == true && this.membersWithOutPrimaryKey.size() > 0) {
				System.out.println("SAME RECORD WITH NO PRIMARY KEY");
				return false;
			}
		}
		
		//membersWithOutPrimaryKey.add(new Record(inputData, this));
		this.membersWithOutPrimaryKey.add(newRecord);
		
		for(int i = 0; i < inputData.size(); i++) {
			if(inputData.get(i).getType() == Type.INT) {
				if(String.valueOf(inputData.get(i).int_data).length() > this.longestValueLength.get(inputData.get(i).getName())) {
					this.longestValueLength.put(inputData.get(i).getName(), String.valueOf(inputData.get(i).int_data).length());
				}
			} else if(inputData.get(i).getType() == Type.VARCHAR) {
				if(inputData.get(i).str_data.length() > 40) {
					throw new InsertionException("INPUT STRING TOO LONG");
				}
				
				boolean finder = false;		// use error handler
				for(int j = 0; j < this.tuple.size(); j++) {
					if(this.tuple.get(j).getName() == inputData.get(i).getName()) {
						finder = true;
						break;
					}
				}
				if(finder == false) {
					continue;
				}
				
				if(inputData.get(i).str_data.length() > this.longestValueLength.get(inputData.get(i).getName())) {
					this.longestValueLength.put(inputData.get(i).getName(), inputData.get(i).str_data.length());
				}
				
			} else {
				throw new InsertionException("UNKNOW TYPE INSERTION");
			}
		}
		
		if(havePrimaryKey == true) {
			for(int i = 0; i < inputData.size(); i++){
				if(inputData.get(i).getName().equals(primaryKey.getName())) {
					
					if(primaryKey.getType() == Type.INT) {
						membersWithPrimaryKey.put(String.valueOf(inputData.get(i).int_data), membersWithOutPrimaryKey.get(membersWithOutPrimaryKey.size() - 1));
					} else if(primaryKey.getType() == Type.VARCHAR) {
						membersWithPrimaryKey.put(inputData.get(i).str_data, membersWithOutPrimaryKey.get(membersWithOutPrimaryKey.size() - 1));
					} else {
						throw new InsertionException("UNKNOW TYPE INSERTION");
					}
					
				}
			}
		}
		return true;

	}
	
	// a powerful function to update ob' 'ov
	public void updateFieldByField(String withThisField, String withThisValue, String intoThisField, String intoThisValue) {
		
		boolean finder = false;		// use error handler
		for(int j = 0; j < this.tuple.size(); j++) {
			if(this.tuple.get(j).getName() == withThisField) {
				finder = true;
				break;
			}
		}
		if(finder == false) {
			System.out.println("CAN'T FIND THIS INPUT ATTRIBUTE");
			return;
		}
		
		finder = false;		// use error handler
		for(int j = 0; j < this.tuple.size(); j++) {
			if(this.tuple.get(j).getName() == intoThisField) {
				System.out.println("CAN'T FIND THIS ATTRIBUTE TO UPDATE");
				finder = true;
				break;
			}
		}
		if(finder == false) {
			return;
		}
		
		for(int i = 0; i < membersWithOutPrimaryKey.size(); i++) {
			if(membersWithOutPrimaryKey.get(i).getData(withThisField).getType() == Type.INT) {
				if(membersWithOutPrimaryKey.get(i).getData(withThisField).getIntData() == Integer.parseInt(withThisValue)) {
					if(membersWithOutPrimaryKey.get(i).getData(intoThisField).getType() == Type.INT) {
						membersWithOutPrimaryKey.get(i).getData(intoThisField).setIntData(Integer.parseInt(intoThisValue));
					} else if(membersWithOutPrimaryKey.get(i).getData(intoThisField).getType() == Type.VARCHAR) {
						membersWithOutPrimaryKey.get(i).getData(intoThisField).setStrData(intoThisValue);
					}
				}
			} else if(membersWithOutPrimaryKey.get(i).getData(withThisField).getType() == Type.VARCHAR) {
				if(membersWithOutPrimaryKey.get(i).getData(withThisField).getStrData() == withThisValue) {
					if(membersWithOutPrimaryKey.get(i).getData(intoThisField).getType() == Type.INT) {
						membersWithOutPrimaryKey.get(i).getData(intoThisField).setIntData(Integer.parseInt(intoThisValue));
					} else if(membersWithOutPrimaryKey.get(i).getData(intoThisField).getType() == Type.VARCHAR) {
						membersWithOutPrimaryKey.get(i).getData(intoThisField).setStrData(intoThisValue);
					}
				}
			}
			
		}
	}
	
	/*************************************************************************/
	

	
	/*Print Function*/
	/*************************************************************************/
	
	public void printTableField()
	{
		int rowLen;
		System.out.println("Table: " + this.tableName);
		System.out.print("|-");
		for(int i = 0; i < this.tuple.size(); i++) {
			if(longestValueLength.get(this.tuple.get(i).getName()) < 11) {				//judge onle once?
				rowLen = 11;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 21) {
				rowLen = 21;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 31) {
				rowLen = 31;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 41) {
				rowLen = 41;
			} else {
				rowLen = 87;
				System.out.println("ERROR");  	// error handleing
			}
			
			for(int j = 0; j < rowLen; j++) {
				System.out.print("--");
			}
			if(i == this.tuple.size() - 1) {
				System.out.print("--");
			} else {
				System.out.print("--");
			}
		}
		System.out.print("\n");
		
		
		System.out.print("||");
		for(int i = 0; i < this.tuple.size(); i++) {
			//s[i] = this.fieldList.get(i).getName();
			if(longestValueLength.get(this.tuple.get(i).getName()) < 11) {
				System.out.print( String.format("%-10s ||", this.tuple.get(i).getName() ));
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 21) {
				System.out.print( String.format("%-20s ||", this.tuple.get(i).getName() ));
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 31) {
				System.out.print( String.format("%-30s ||", this.tuple.get(i).getName() ));
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 41) {
				System.out.print( String.format("%-40s ||", this.tuple.get(i).getName() ));
			} else {
				System.out.println("ERROR");  	// error handleing
			}
			
		}
		System.out.print("\n");
		System.out.print("|-");
		for(int i = 0; i < this.tuple.size(); i++) {
			if(longestValueLength.get(this.tuple.get(i).getName()) < 11) {				//judge onle once?
				rowLen = 11;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 21) {
				rowLen = 21;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 31) {
				rowLen = 31;
			} else if (longestValueLength.get(this.tuple.get(i).getName()) < 41) {
				rowLen = 41;
			} else {
				rowLen = 87;
				System.out.println("ERROR");  	// error handleing
			}
			
			for(int j = 0; j < rowLen; j++) {
				System.out.print("--");
			}
			if(i == this.tuple.size() - 1) {
				System.out.print("--");
			} else {
				System.out.print("--");
			}
		}
		System.out.print("\n");
	}
	
	public void printAllRecord()
	{
		System.out.println("Table: " + this.tableName);
	
		for(int i = 0; i < membersWithOutPrimaryKey.size(); i++) {
			if(membersWithOutPrimaryKey.size() == 1) {
				membersWithOutPrimaryKey.get(i).printRecord(true, true, this.longestValueLength);
			} else if(i == membersWithOutPrimaryKey.size() - 1) {
				membersWithOutPrimaryKey.get(i).printRecord(false, true, this.longestValueLength);
			} else if(i == 0) {
				membersWithOutPrimaryKey.get(i).printRecord(true, false, this.longestValueLength);
			} else {
				membersWithOutPrimaryKey.get(i).printRecord(false, false, this.longestValueLength);
			}
		}
		
	}
	
	public Table SelectRecord(ArrayList<String> selectArribute) throws Warning, InsertionException
	{
		ArrayList<String> tmpList = (ArrayList<String>)(selectArribute.clone());
		selectArribute.clear();
		if(this.getStringAttributeList().get(0).contains(".")){
			if(!tmpList.get(0).contains(".")){
				for(String s : tmpList){
					selectArribute.add(this.tableName + "." + s);
				}
			}else{
				selectArribute = tmpList;
			}
		}else{
			if(tmpList.get(0).contains(".")){
				for(String s : tmpList){
					selectArribute.add(s.split("\\.")[1]);
				}
			}else{
				selectArribute = tmpList;
			}
		}
		//System.out.println("Table: " + this.tableName);
		
		if(selectArribute.size() == 1 && selectArribute.get(0).equals("*")) {
			/*selectArribute.clear();
			for(int i = 0; i < tuple.size(); i++) {
				selectArribute.add(tuple.get(i).getName());
			}*/
			return this;
		}
		
		Table tmpTable = new Table("tmpTable", this.parentDatabase);
		tmpTable.setisTmpTable(true);
		
		for (int i = 0; i < selectArribute.size(); i++) {
			Field tmp = tuple.get(this.attributeIndex.get(selectArribute.get(i)));
			tmpTable.addTableFieldByString(tmp.getName(), tmp.getType(), tmp.getStrLenLimit());
		}
		ArrayList<String> inputData = new ArrayList<String>();
		DataUnit tmpData;
		for (int i = 0; i < membersWithOutPrimaryKey.size(); i++) {
			
			inputData.clear();
			for (int j = 0; j < selectArribute.size(); j++) {
				
				tmpData = membersWithOutPrimaryKey.get(i).getData(selectArribute.get(j));
				if (tmpData.getType() == Type.INT) {
					inputData.add(String.valueOf(tmpData.getIntData()));
				} else if (tmpData.getType() == Type.VARCHAR) {
					inputData.add(tmpData.getStrData());
				} else if (tmpData.getType() == Type.NULL) {
					System.out.println("6666666666");
				}
			}
			
			tmpTable.addTableMemberByString(selectArribute, inputData);
		}
		
		//tmpTable.printAllRecord();
		return tmpTable;
		
	}
	
	public Table WhereLimit(Table T1, ArrayList<String> condition) {
		Table tmpTable = new Table("tmpTable", this.parentDatabase);
		tmpTable.setisTmpTable(true);
		
		
		
		return tmpTable;
	}
	
	/*************************************************************************/
	
	
	
	// testing print
	public void printMaxLen() {
		for(int i = 0; i < this.tuple.size(); i++) {
			System.out.println(this.tuple.get(i).getName() + ": " + this.longestValueLength.get(this.tuple.get(i).getName()));
		}
		
	}
	
	
}
