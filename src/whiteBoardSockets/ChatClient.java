package whiteBoardSockets;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.net.*;
public class ChatClient {

	private JFrame frame;
	Socket s;
	PrintWriter pr;
	Scanner key;
	
	InputStreamReader in;
	BufferedReader bf;
	boolean flag=false;
	String name;
	static InetAddress netaddress = null;
	
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args,InetAddress address)throws Exception {
		netaddress = address;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try
				{
					ChatClient window = new ChatClient(args[0]);
					window.frame.setVisible(true);
					window.frame.setAlwaysOnTop(true);
				}
				catch(Exception e)
				{
					System.out.println("Failed at chatclient");
					
				}
					
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public ChatClient(String uName, InetAddress address) throws Exception {
		this.name = uName;
		netaddress = address;
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 * @throws UnknownHostException 
	 */
	private void initialize() throws Exception {
		
		
		
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("Button.select"));
		frame.setBounds(100, 100, 259, 436);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(6, 316, 247, 51);
		frame.getContentPane().add(textArea_1);
		
		JButton btnEnter = new JButton("Send");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String str=textArea_1.getText();
					pr.println(name+": "+str);
					pr.flush();
					textArea_1.setText("");
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "not connected!");
				}
				
			}
		});
		JTextArea textArea = new JTextArea();
		textArea.setSize(240, 222);
		textArea.setLocation(2, 2);
		frame.getContentPane().add(textArea);
		textArea.setEditable(false);
		
		btnEnter.setBounds(72, 379, 123, 29);
		
		frame.getContentPane().add(btnEnter);
		JScrollPane scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 259, 251);
		frame.getContentPane().add(scrollPane);
		
		//Automatically setting up the connection without any action listner
		try {	
			s = new Socket(netaddress,4444);
			pr=new PrintWriter(s.getOutputStream());
			key=new Scanner(System.in);
			
			in=new InputStreamReader(s.getInputStream());
			bf=new BufferedReader(in);
			ClientThread ct=new ClientThread(bf,textArea);
			ct.start();
			//name=JOptionPane.showInputDialog("enter a username");
			//JOptionPane.showMessageDialog(null, "connected! username: "+name);
			flag=true;
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Connection error!");
			}
		
		
		/*JButton btnConnect = new JButton("connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(flag==true) {
					JOptionPane.showMessageDialog(null, "already connected!");
				}
				else {
					
					try {	
					s = new Socket("localhost",4444);
					pr=new PrintWriter(s.getOutputStream());
					key=new Scanner(System.in);
					
					in=new InputStreamReader(s.getInputStream());
					bf=new BufferedReader(in);
					ClientThread ct=new ClientThread(bf,textArea);
					ct.start();
					//name=JOptionPane.showInputDialog("enter a username");
					//JOptionPane.showMessageDialog(null, "connected! username: "+name);
					flag=true;
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Connection error!");;
					}
				}
				
			}
		});
		
		btnConnect.setBounds(19, 379, 123, 29);
		frame.getContentPane().add(btnConnect);*/
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		
	}
}

