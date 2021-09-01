package whiteBoardSockets;
import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

import whiteBoardGUI.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class chatServer {
	
	ArrayList<User> users = null;
	String chat;
	public chatServer(ArrayList<User> users)
	{
		this.users = users;
		try
		{
		initialize();
		}
		catch(Exception e)
		{
			System.out.println("In the the chat error");
		}
	}
	private void initialize() throws Exception {
		System.out.println("In initialize function");
		ServerSocket ss=new ServerSocket(4444);//Hard Coded port
		ArrayList<ServerThread> al=new ArrayList<ServerThread>();
		while(true)
		{
			Socket s = ss.accept();
			ServerThread st=new ServerThread(chat, s,al,users);
			st.start();
			
		}
		
		}

}
