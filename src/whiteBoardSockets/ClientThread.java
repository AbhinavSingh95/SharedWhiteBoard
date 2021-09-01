package whiteBoardSockets;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	BufferedReader bf;
	JTextArea textArea;
	
	
	public ClientThread(BufferedReader bf, JTextArea textArea) {
		this.bf=bf;
		this.textArea=textArea;
	}
	
	public void run() {
		while(true) {
			try {
				String str1=bf.readLine();
				if(str1==null)
				{
					System.out.println("Null in chat client");
					break;
				}
				if(str1.contains(";"))
				{
					String[] a = str1.split(";");
					String temp=textArea.getText();
					temp+= "\n"+"Active Users:";
					for(int i = 0; i<a.length;i++)
					{
						temp+= "\n"+a[i];
					}
					textArea.setText(temp);
					continue;
				}
				String temp=textArea.getText();
				temp+="\n"+str1;
				textArea.setText(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Caught Error at ClientThread");
				e.printStackTrace();
				break;
			}	
		}
	}
	
}
