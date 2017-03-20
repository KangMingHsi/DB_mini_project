package DBMS;

import java.util.*;


public class Database {

	private ArrayList<String> tableList;
	private Hashtable<String, Table> tables;
	
	public Database() {
		this.tableList = new ArrayList<String>();
		this.tables = new Hashtable<String, Table>();
	}
	
	public void creat(Table table)
	{
		tableList.add(table.getName());
		tables.put(table.getName(), table);
	}
	
	public void creat(String name)
	{
		tableList.add(name);
		tables.put(name, new Table(name, this));
	}
	
	public Table getTableByName(String name)
	{
		return tables.get(name);
	}
}