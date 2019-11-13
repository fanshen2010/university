package socket;

import java.util.HashMap;
import java.util.Map;

public class User {
	
	private Integer id;
	private String name;
	private String password;
	private Integer state;
	private Map<String,String> groupMsg = new HashMap<String,String>(); 
	
	public void setId(Integer id) {
	    this.id = id;
	}
	
	public void setName(String name) {
	    this.name = name;
	}
	
	public void setPassword(String password) {
	    this.password = password;
	}
	
	public Integer getId() {
	    return id;
	}
	
	public String getName() {
	    return name;
	}
	
	public String getPassword() {
	    return password;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	public Map<String, String> getGroupMsg() {
		return groupMsg;
	}

	public void setGroupMsg(Map<String, String> groupMsg) {
		this.groupMsg = groupMsg;
	}
	
	public void addGroupMsg(String groupName,String msg) {
		this.groupMsg.put(groupName, msg);
	}
	
}
