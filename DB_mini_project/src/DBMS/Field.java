package DBMS;

import DBMS.Field.Type;
import java.util.Optional;

public class Field {
	private Table parent;
	//private Optional<Table> parent;
	private String name;
	protected int StrLenLimit;
	
	public static enum Type {
		INT, VARCHAR, FLOAT, TEXT, NULL
	}
	
	protected Type type; 
	
	public Field(String name, Table parent) {
		this.name = name;
		this.type = Type.NULL;
		this.parent = parent;
		this.StrLenLimit = 40;
	}
	
	public Field(String name, Table parent, Type type) {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.StrLenLimit = 40;
	}
	
	public Field(String name, Table parent, Type type, int strLimit) {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.StrLenLimit = strLimit;
	}
	
	public Field(String name, Type type, int strLimit) {
		this.name = name;
		this.type = type;
		this.StrLenLimit = strLimit;
	}
	
	public Field(String name, Type type) {
		this.name = name;
		this.type = type;
		this.StrLenLimit = 40;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getStrLenLimit() {
		return this.StrLenLimit;
	}
	
}
