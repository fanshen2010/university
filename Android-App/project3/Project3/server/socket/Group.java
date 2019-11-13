package socket;

import java.util.HashMap;
import java.util.Map;

public class Group {

	private Integer id;
	private String name;
	private String msg;
	
	public void setId(Integer id) {
	    this.id = id;
	}
	
	public void setName(String name) {
	    this.name = name;
	}
	
	public Integer getId() {
	    return id;
	}
	
	public String getName() {
	    return name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
