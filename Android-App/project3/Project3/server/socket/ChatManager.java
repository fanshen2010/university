package socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

public class ChatManager {
	
	// online param
	private static Set<ChatSocket> csList = new HashSet<ChatSocket>();;
	//private static Map<String,Set<ChatSocket>> csGroup= new HashMap<String,Set<ChatSocket>>();
	private static final ChatManager cm = new ChatManager();
	private DBUser dbUser = new DBUser();
	
	public static ChatManager getChatManager(){
		return cm;
	}
	
	public void register(ChatSocket cs, String userName, String password){
		String msg ="register"+"\n";
		userName = userName.trim();
		password = password.trim();
		Boolean result = dbUser.checkRegister(userName);
		if(result!=true){
			result = dbUser.userRegister(userName,password);
			if(result==true){
				msg =msg+"registerSuccess";
			}else{
				msg =msg+"registerError";
			}
		}else{
			msg =msg+"registerError";
		}
		cs.out(msg);
	}
	
	public void signUp(ChatSocket cs, String userName, String password){
		String msg ="signUp"+"\n";
		userName = userName.trim();
		password = password.trim();
		User user = dbUser.checkLogin(userName, password);
		if(user!=null){
			for(ChatSocket csElem:csList){
				if(user.getName().equals(csElem.getUserName())){
					msg =msg+"signUpError"+"\n";
					cs.out(msg);
					return;
				}
			}
			msg =msg+"signUpSuccess"+"\n";
			//offline
			cs.setUserId(user.getId());
			cs.setUserName(user.getName());
			csList.add(cs);
			
			//msg
			Map<String,String> groupMsg = dbUser.getUserRecord(userName);
			msg = msg+ getJsonString(groupMsg);
		}else{
			msg =msg+"signUpError"+"\n";
		}
		cs.out(msg);
	}
	
	
	public void restart(ChatSocket cs, String userName, String password){
		String msg ="restart"+"\n";
		userName = userName.trim();
		password = password.trim();
		User user = dbUser.checkLogin(userName, password);
		if(user!=null){
			for(ChatSocket csElem:csList){
				if(user.getName().equals(csElem.getUserName())){
					csList.remove(csElem);
					break;
				}
			}
			msg =msg+"restartSuccess"+"\n";
			//offline
			cs.setUserId(user.getId());
			cs.setUserName(user.getName());
			csList.add(cs);
			
			//msg
			Map<String,String> groupMsg = dbUser.getUserRecord(userName);
			msg = msg+ getJsonString(groupMsg);
		}else{
			msg =msg+"restartError"+"\n";
		}
		cs.out(msg);
	}
	
	public void signDown(ChatSocket cs){
		String msg ="signDown"+"\n";
		//offline
		csList.remove(cs);
		//msg
		msg =msg+"";
		cs.out(msg);
	}
	
	public void groupJoin(ChatSocket cs, String groupName){
		String msg ="groupJoin"+"\n";
		groupName = groupName.trim();
		//offline
		Group group = dbUser.userJoinGroup(cs.getUserId(),groupName);
		//msg
		if(group!=null){
			msg =msg+group.getName()+"\n";
			if(group.getMsg()==null){
				msg =msg+"";
			}else{
				msg =msg+group.getMsg();
			}
		}else{
			msg =msg+"";
		}
		cs.out(msg);
	}
	
	public void groupQuit(ChatSocket cs, String groupName){
		String msg ="groupQuit"+"\n";
		groupName = groupName.trim();
		//offline
		Group group = dbUser.userQuitGroup(cs.getUserId(),groupName);
		//msg
		if(group!=null){
			msg =msg+group.getName();
		}else{
			msg =msg+"";
		}
		cs.out(msg);
	}
	
	public void groupAdd(ChatSocket cs, String groupName){
		String msg ="groupAdd"+"\n";
		groupName = groupName.trim();
		//offline
		Boolean result = dbUser.checkGroupAdd(groupName);
		if(result!=true){
			result = dbUser.userAddGroup(cs.getUserId(),groupName);
			if(result==true){
				msg =msg+groupName;
			}else{
				msg =msg+"";
			}
		}else{
			msg =msg+"";
		}
		cs.out(msg);
	}
	
	public void groupShow(ChatSocket cs, String groupName){
		String msg ="groupShow"+"\n";
		groupName = groupName.trim();
		//offline
		List<String> result = dbUser.getGroupUser(groupName);
		//msg
		if(!result.isEmpty()){
			for(String elem:result){
				msg = msg + elem +"\n";
			}
		}else{
			msg = msg +""+"\n";
		}
		cs.out(msg);
		
	}
	
	public void groupToJoin(ChatSocket cs){
		String msg ="groupToJoin"+"\n";
		//offline
		List<String> result = dbUser.getUnJoinedGroup(cs.getUserId());
		//msg
		if(!result.isEmpty()){
			for(String elem:result){
				msg = msg + elem +"\n";
			}
		}else{
			msg = msg +""+"\n";
		}
		cs.out(msg);
	}
	
	public void groupToQuit(ChatSocket cs){
		String msg ="groupToQuit"+"\n";
		//offline
		List<String> result = dbUser.getJoinedGroup(cs.getUserId());
		//msg
		if(!result.isEmpty()){
			for(String elem:result){
				msg = msg + elem +"\n";
			}
		}else{
			msg = msg +""+"\n";
		}
		cs.out(msg);
	}
	
	public void publish(ChatSocket cs, String groupName, String inmsg){
		groupName = groupName.trim();
		String msg = "msg"+"\n"+groupName+"\n"+cs.getUserName()+"\n"+inmsg;
		//offline
		dbUser.groupUpdateMsg(groupName, cs.getUserName()+"\n"+inmsg);
		//online
		List<String> userNames = dbUser.getGroupUser(groupName);
		String csName = cs.getUserName();
		for(ChatSocket csChatSocket: csList){
			String csChatSocketName = csChatSocket.getUserName();
			if(userNames.contains(csChatSocketName)){
				System.out.println("sendTouser: "+csChatSocket.getUserId());
				csChatSocket.out(msg);
			}
		}
		//cs.out(msg);
	}
	
	public String getJsonString(Map<String,String> groupMsg){
		JSONObject jObject = new JSONObject();
		for(String key:groupMsg.keySet()){
			jObject.put(key, groupMsg.get(key));
		}
		return jObject.toString();
	}
	
}
