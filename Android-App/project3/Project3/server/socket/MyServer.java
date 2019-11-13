package socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyServer {

	public static void main(String[] args) throws IOException {
		new ServerListener().start();
	}

}

