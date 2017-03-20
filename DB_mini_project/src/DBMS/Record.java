package DBMS;

import java.util.*;
import DBMS.Field.Type;
import ExceptionHandlers.InsertionException;


public class Record {
	private ArrayList<Field> fieldList;		//order?	//should be default order
	private Hashtable<String, DataUnit> fields;
	private Table parentTable;
	
	public Record(ArrayList<Field> schema) {
		this.fields = new Hashtable<String, DataUnit>();
		this.fieldList = new ArrayList<Field>();
		//this.fieldList = parent.getAttributesList();
		for(int i = 0; i < schema.size(); i++) {
			fieldList.add(schema.get(i));				// will create errow ?
			this.fields.put(schema.get(i).getName(), new DataUnit());
		}
	}
	
	public Record(ArrayList<TransactionData> schema, Table parent) throws InsertionException {				//???????????????????????????????????
		this.fields = new Hashtable<String, DataUnit>();
		this.parentTable = parent;
		this.fieldList = parent.getAttributesList();
		for(int i = 0; i < schema.size(); i++) {
			//fieldList.add(schema.get(i));
			try {
				boolean finder = false;
				for(int j = 0; j < this.fieldList.size(); j++) {
					if(this.fieldList.get(j).getName().compareTo(schema.get(i).getName()) == 0) {
						finder = true;
						break;
					}
				}
				if(finder == false) {
					throw new InsertionException("CAN'T FIND THIS ATTRIBUTE");
				}
				
				this.fields.put(schema.get(i).getName(), new DataUnit(schema.get(i).getName(), schema.get(i).getType(), schema.get(i).int_data, schema.get(i).str_data));
			} catch (InsertionException ex) {
				System.out.println("CAN'T FIND THIS ATTRIBUTE");
				//throw ex;
			}
		}
	}
	
	public DataUnit getData(String name)
	{
		return fields.get(name);
	}
	
	public void setNewData(String name, Type type, int intData, String strData)		// if exist name handle
	{
		fields.put(name, new DataUnit(name, type, intData, strData));
	}
	
	public void setData(String name, String data) { 	// if not exist name handle
		Type type = fields.get(name).getType();
		if(type == Type.INT) {
			fields.get(name).setIntData(Integer.parseInt(data));
		} else if(type == Type.VARCHAR) {
			fields.get(name).setStrData(data);
		}
	}
	
	public void addField(Field field) {
		this.fieldList.add(field);
	}
	
	/*Print Function*/
	/*************************************************************************/
	
	public void printRecord(boolean firstRecord, boolean lastRecord, HashMap<String, Integer> lenLimit)
	{
		//String[] s;
		//s = new String[this.fieldList.size()];
		int maxLen = 40;
		int rowLen = 11;
		
		if(firstRecord == true) {
			System.out.print("|-");
			for(int i = 0; i < this.fieldList.size(); i++) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {				//judge onle once?
					rowLen = 11;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					rowLen = 21;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					rowLen = 31;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					rowLen = 41;
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				
				for(int j = 0; j < rowLen; j++) {
					System.out.print("--");
				}
				if(i == this.fieldList.size() - 1) {
					System.out.print("--");
				} else {
					System.out.print("--");
				}
			}
			System.out.print("\n");
			
			
			System.out.print("||");
			for(int i = 0; i < this.fieldList.size(); i++) {
				//s[i] = this.fieldList.get(i).getName();
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					System.out.print( String.format("%-10s ||", this.fieldList.get(i).getName() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					System.out.print( String.format("%-20s ||", this.fieldList.get(i).getName() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					System.out.print( String.format("%-30s ||", this.fieldList.get(i).getName() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					System.out.print( String.format("%-40s ||", this.fieldList.get(i).getName() ));
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				
			}
			System.out.print("\n");
		
			System.out.print("|-");
			for(int i = 0; i < this.fieldList.size(); i++) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					rowLen = 11;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					rowLen = 21;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					rowLen = 31;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					rowLen = 41;
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				
				for(int j = 0; j < rowLen; j++) {
					System.out.print("--");
				}
				if(i == this.fieldList.size() - 1) {
					System.out.print("--");
				} else {
					System.out.print("--");
				}
			}
			System.out.print("\n");
		}
		
		System.out.print("||");

		for(int i = 0; i < this.fieldList.size(); i++) {
			String tmp = this.fieldList.get(i).getName();

			if(this.fields.get(tmp).getType().equals(Type.INT)) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					System.out.print( String.format("%-10s ||", this.fields.get(tmp).getIntData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					System.out.print( String.format("%-20s ||", this.fields.get(tmp).getIntData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					System.out.print( String.format("%-30s ||", this.fields.get(tmp).getIntData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					System.out.print( String.format("%-40s ||", this.fields.get(tmp).getIntData() ));
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				//System.out.print( String.format("%-10s ¢x", this.fields.get(tmp).getIntData() ));
			} else if(this.fields.get(tmp).getType().equals(Type.VARCHAR)) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					System.out.print( String.format("%-10s ||", this.fields.get(tmp).getStrData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					System.out.print( String.format("%-20s ||", this.fields.get(tmp).getStrData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					System.out.print( String.format("%-30s ||", this.fields.get(tmp).getStrData() ));
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					System.out.print( String.format("%-40s ||", this.fields.get(tmp).getStrData() ));
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				//System.out.print( String.format("%-10s ¢x", this.fields.get(tmp).getStrData() ));
			} else {
				System.out.print("WTF");
			}
		}
		System.out.print("\n");
		
		if (lastRecord == false) {
		
			System.out.print("|-");
			for(int i = 0; i < this.fieldList.size(); i++) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					rowLen = 11;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					rowLen = 21;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					rowLen = 31;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					rowLen = 41;
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				
				for(int j = 0; j < rowLen; j++) {
					System.out.print("--");
				}
				if(i == this.fieldList.size() - 1) {
					System.out.print("--");
				} else {
					System.out.print("--");
				}
			}
			System.out.print("\n");
		} else {
			System.out.print("|-");
			for(int i = 0; i < this.fieldList.size(); i++) {
				if(lenLimit.get(this.fieldList.get(i).getName()) < 11) {
					rowLen = 11;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 21) {
					rowLen = 21;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 31) {
					rowLen = 31;
				} else if (lenLimit.get(this.fieldList.get(i).getName()) < 41) {
					rowLen = 41;
				} else {
					System.out.println("ERROR");  	// error handleing
				}
				
				for(int j = 0; j < rowLen; j++) {
					System.out.print("--");
				}
				if(i == this.fieldList.size() - 1) {
					System.out.print("--");
				} else {
					System.out.print("--");
				}
			}
			System.out.print("\n");
		}
		
	}
}