package DBMS;

import DBMS.Field.Type;

public class DataUnit {
	
	private String name;
	private Type type;
	
	private int int_data;
	private String str_data;
	private int length;
	
	public DataUnit(String name, Type type, int intData, String strData) {
		this.name = name;
		this.type = type;
		this.int_data = intData;
		this.str_data = strData;
		if(type.equals(Type.VARCHAR)) {
			this.length = strData.length();
		} else {
			this.length = 0;
		}
	}
	
	public DataUnit()
	{
		this("", Type.NULL, 0, "");
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public void setIntData(int data) {
		this.int_data = data;
	}
	
	public int getIntData() {
		return this.int_data;
	}
	
	public void setStrData(String data) {
		this.str_data = data;
	}
	
	public String getStrData() {
		return this.str_data;
	}
	
	public int getLength() {
		return this.length;
	}
}
