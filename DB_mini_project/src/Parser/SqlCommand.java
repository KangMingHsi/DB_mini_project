/**
 * 
 */
package Parser;

import java.util.ArrayList;
import java.util.Map;

public abstract class SqlCommand {
	
	abstract public ActionType getActionType();// 1: create -1: insert
	
	abstract public String getTableName();//table name of the action
	
	abstract public ArrayList<Map<String, String>> getNickName();
	
	abstract public String getPrimaryKey();
	
	abstract public ArrayList<Map<String, String>> getAttributes();
	
	abstract public String[] getSelectAttr();
	
	abstract public boolean haveAttributeName();
	
	abstract public ArrayList<String> getContent();
	
	abstract public boolean isSelectAll();
	
	abstract public ActionType getAggreFunc();
	
	abstract public String[] getCompareTree();
	
	abstract public ErrorType getError();
}
