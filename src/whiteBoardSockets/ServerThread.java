package whiteBoardSockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import whiteBoardGUI.User;

public class ServerThread extends Thread{
	ArrayList<ServerThread> al;
	ArrayList<User> users = null;
	
	String chat;
	Socket s;
	ServerSocket ss;
	InputStreamReader in;
	BufferedReader bf=null;
	PrintWriter pr;
	
	public ServerThread(String chat, Socket s, ArrayList<ServerThread> al, ArrayList<User> users) {
		this.chat= chat;
		this.s=s;
		this.al=al;
		this.users = users;
	}
	
	public void run() {
		try {
			synchronized(al)
			{
			al.add(this);
			}
			
			in=new InputStreamReader(s.getInputStream());
			bf=new BufferedReader(in);
			
			pr=new PrintWriter(s.getOutputStream());
			
			while(s.isConnected()) {
				try {
					String str=bf.readLine();
					//System.out.println(str);
					if(str==null)
					{
						System.out.println("Killed the thread");
						break;
					}
					String a=str.split(" ")[1];
					System.out.println(a);
					if(a.equals("activeusers"))
					{
						System.out.println("In active users");
						String userinfo = "";
						synchronized(users)
						{
							for(User u: users)
							{
								if(u.getName()!=null)
								{
									//System.out.println("Inside null if");
									userinfo+= u.getName();
									userinfo+=";";
								}
							}
						}
					
						for(ServerThread x: al) {
							if(x.pr!=null) {
								//System.out.println(userinfo);
								x.pr.println(userinfo);
								x.pr.flush();
							}
							
					}
						continue;
					}//End of Active users info
					chat+=str+"\n";
					for(ServerThread x: al) {
						if(x.pr!=null) {
							x.pr.println(str);
							x.pr.flush();
						}
					}
				} catch(Exception e) {
					System.out.println("Caught Error at ServerThread"+ e.getMessage());
					System.out.println("Killed the thread");
					Thread.currentThread().interrupt();
					
				}
			}
			
		} catch (Exception e) {
			System.out.println("Caught Error at ServerThread 2"+ e.getMessage());

		}
	}
}

