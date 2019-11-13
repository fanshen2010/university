package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ChatSocket extends Thread {
	
	Socket socket;
	Integer userId;
	String userName;
	
	public ChatSocket(Socket s){
		this.socket = s;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void out(String msg){
		try {
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			writer.writeUTF(msg);
			writer.flush();
			System.out.println("sent to client: "+msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream reader = new DataInputStream((socket.getInputStream()));
	        
	        String requestStr;
            String requestType;
            String requestElse;
            
			while(true){
				requestStr = reader.readUTF(); 
				if(requestStr!=null && !requestStr.isEmpty()){
					System.out.println("get from client: "+requestStr);
					requestType = requestStr.substring(0,requestStr.indexOf("\n"));
					
					if("groupToJoin".equals(requestType)){						// show unjoined group
						ChatManager.getChatManager().groupToJoin(this);
					}else if("groupToQuit".equals(requestType)){				// show joined group
						ChatManager.getChatManager().groupToQuit(this);
					}else if("signDown".equals(requestType)){					// signDown
						ChatManager.getChatManager().signDown(this);
					}else{
						requestElse = requestStr.substring(requestStr.indexOf("\n")+1);
						if("groupShow".equals(requestType)){						// show user in one of my group
							ChatManager.getChatManager().groupShow(this, requestElse);
						}else if("groupAdd".equals(requestType)){						// add group
							ChatManager.getChatManager().groupAdd(this,requestElse);
						}else if("groupJoin".equals(requestType)){						// join group
							ChatManager.getChatManager().groupJoin(this,requestElse);
						}else if("groupQuit".equals(requestType)){						// quit group
							ChatManager.getChatManager().groupQuit(this,requestElse);
						}else if("signUp".equals(requestType)){
							String name = requestElse.substring(0,requestElse.indexOf("\n"));
							String password = requestElse.substring(requestElse.indexOf("\n")+1);
							ChatManager.getChatManager().signUp(this,name,password);
						}else if("register".equals(requestType)){
							String name = requestElse.substring(0,requestElse.indexOf("\n"));
							String password = requestElse.substring(requestElse.indexOf("\n")+1);
							ChatManager.getChatManager().register(this,name,password);
						}else if("msg".equals(requestType)){
							String requestGroup = requestElse.substring(0,requestElse.indexOf("\n"));
							String requestGroupElse = requestElse.substring(requestElse.indexOf("\n")+1);
							ChatManager.getChatManager().publish(this,requestGroup,requestGroupElse);
						}else if("restart".equals(requestType)){
							String name = requestElse.substring(0,requestElse.indexOf("\n"));
							String password = requestElse.substring(requestElse.indexOf("\n")+1);
							ChatManager.getChatManager().restart(this,name,password);
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			System.out.println("UnsupportedEncodingException");
			ChatManager.getChatManager().signDown(this);
			//e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			ChatManager.getChatManager().signDown(this);
			//e1.printStackTrace();
		}
	}
	
}
