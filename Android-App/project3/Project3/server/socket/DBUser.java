package socket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUser {
	
	public Boolean userRegister(String userName,String password){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "INSERT INTO user (name,password) values('"+userName+"','"+password+"')";
			PreparedStatement posted = conn.prepareStatement(sql);
			posted.executeUpdate();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Boolean userAddGroup(Integer userId, String groupName){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "INSERT INTO groups (name) values('"+groupName+"')";
			PreparedStatement posted = conn.prepareStatement(sql);
			posted.executeUpdate();
			
			Group group = getGroupByName(groupName);
			insertUserGroup(userId,group.getId());
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Group userJoinGroup(Integer userId, String groupName){
		try{
			Group group = getGroupByName(groupName);
			insertUserGroup(userId, group.getId());
			return group;
		}catch(Exception e){
			return null;
		}
	}
	
	public Group userQuitGroup(Integer userId, String groupName){
		try{
			Group group = getGroupByName(groupName);
			Connection conn= MyDBDriver.getConnection();
			String sql = "DELETE FROM usergroup where userId='"+userId+"' and groupId='"+group.getId()+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			posted.executeUpdate();
			return group;
		}catch(Exception e){
			return null;
		}
	}
	
	public Map<String,String> getUserRecord(String userName){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT g.name, g.msg FROM user u, groups g, usergroup ug "
					+ " where u.name='"+userName+"'"
					+ " and ug.groupId=g.id"
					+ " and ug.userId=u.id";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			Map<String,String> groupMsg = new HashMap<String,String>(); 
			while(result.next()){
				String gmsg = result.getString("msg");
				if(gmsg==null){
					gmsg="";
				}
				groupMsg.put(result.getString("name"), gmsg);
			}
			return groupMsg;
		}catch(Exception e){
			return null;
		}
	}
	
	public List<String> getGroupUser(String groupName){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT u.name FROM usergroup ug, user u, groups g "
					+ " where g.name='"+groupName+"'"
					+ " and ug.groupId=g.id"
					+ " and ug.userId=u.id";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			List<String> groupList = new ArrayList<String>();
			while(result.next()){
				groupList.add(result.getString("name"));
			}
			return groupList;
		}catch(Exception e){
			return null;
		}
	}
	
	public List<String> getJoinedGroup(Integer userId){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql =" SELECT g.name FROM usergroup ug, user u, groups g "
					+ " where u.id='"+userId+"'"
					+ "  and ug.groupId=g.id"
					+ "  and ug.userId=u.id ";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			List<String> groupList = new ArrayList<String>();
			while(result.next()){
				groupList.add(result.getString("name"));
			}
			return groupList;
		}catch(Exception e){
			return null;
		}
	}
	
	public List<String> getUnJoinedGroup(Integer userId){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT gs.name FROM groups gs"
					+ " where gs.id not in ("
					+ "  SELECT g.id FROM usergroup ug, user u, groups g "
					+ "  where u.id='"+userId+"'"
					+ "   and ug.groupId=g.id"
					+ "   and ug.userId=u.id )";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			List<String> groupList = new ArrayList<String>();
			while(result.next()){
				groupList.add(result.getString("name"));
			}
			return groupList;
		}catch(Exception e){
			return null;
		}
	}
	
	public Boolean groupUpdateMsg(String groupName,String msg){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT msg From groups where name='"+groupName+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			String msgsql = "";
			while(result.next()){
				String gmsg = result.getString("msg");
				if(gmsg==null){
					msgsql = msg;
				}else{
					msgsql = gmsg+"\n"+msg;
				}
			}
			
			sql = "UPDATE groups set msg='"+msgsql+"' where name='"+groupName+"'";
			posted = conn.prepareStatement(sql);
			posted.executeUpdate(sql);
			
			return true;
		}catch(Exception e){
			return null;
		}
		
	}
	
	public Boolean checkRegister(String userName){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT * FROM user where name='"+userName+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			while(result.next()){
				return true;
			}
			return false;
		}catch(Exception e){
			return false;
		}
	}
	
	public User checkLogin(String userName, String password){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT id, name FROM user where name='"+userName+"' and password='"+password+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			while(result.next()){
				User user = new User();
				user.setId(result.getInt("id"));
				user.setName(result.getString("name"));
				return user;
			}
			return null;
		}catch(Exception e){
			return null;
		}
	}
	
	public Boolean checkGroupAdd(String name){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT * FROM groups where name='"+name+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			while(result.next()){
				return true;
			}
			return false;
		}catch(Exception e){
			return false;
		}
	}

	
	
	
	//==========================================================
	
	public Group getGroupByName(String groupName){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "SELECT id,name,msg FROM groups where name='"+groupName+"'";
			PreparedStatement posted = conn.prepareStatement(sql);
			ResultSet result = posted.executeQuery(sql);
			
			Group group = new Group();
			while(result.next()){
				group.setId(result.getInt("id"));
				group.setName(result.getString("name"));
				group.setMsg(result.getString("msg"));
			}
			return group;
		}catch(Exception e){
			return null;
		}
	}
	
	public Boolean insertUserGroup(Integer userId, Integer groupId){
		try{
			Connection conn= MyDBDriver.getConnection();
			String sql = "INSERT INTO usergroup (userId,groupId) values('"+userId+"','"+groupId+"')";
			PreparedStatement posted = conn.prepareStatement(sql);
			posted.executeUpdate();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
}
