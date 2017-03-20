class parser

	getComments(): return an arrayList of SqlCommand
	
abstract class SqlCommand
	
	getActionType(): return 1 create / -1 insert
	
	getTableName(): return String table's name
	
	getPrimaryKey(): return a string if there has primaryKey, else return ""
	
	getAttributes(): return a arraylist of Map<String, String> for
					 create:{"name":attr name, "DataType": data type, "length": int}  if data type is int, then length = 0
					 insert:{}
	
	haveAttributeName():return true insert with attr/ false return without att	or creat
	
	2017.3.14 08:20 update: insert values with case sensitive and white space