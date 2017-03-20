/**
 * 
 */
package Parser;

import java.util.ArrayList;
import java.util.Map;

public abstract class SqlCommand {
	
	abstract public int getActionType();// 1: create -1: insert
	
	abstract public String getTableName();//table name of the action
	
	abstract public String getPrimaryKey();
	
	abstract public ArrayList<Map<String, String>> getAttributes();
	
	abstract public boolean haveAttributeName();
	
	abstract public ArrayList<String> getContent();
}