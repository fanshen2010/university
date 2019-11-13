package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerListener extends Thread {

	@Override
	public void run() {
		Socket clientSocket = null;
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(12345); 
			System.out.println("server started....");
			while(true){
//				accept() will block main thread (first visit run twice in HTTP)
				System.out.println("clientSocket1");
				clientSocket = serverSocket.accept();
				System.out.println("clientSocket2");
				ChatSocket cs = new ChatSocket(clientSocket);
				cs.start();
//				ChatManager.getChatManager().add(cs);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
