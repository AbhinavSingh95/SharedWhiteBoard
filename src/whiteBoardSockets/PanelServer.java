package whiteBoardSockets;

import java.io.*;

import java.net.*;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import whiteBoardGUI.ListData;
import whiteBoardGUI.ServerPanel;
import whiteBoardGUI.User;
import whiteBoardGUI.BoardFrame;
import javax.swing.*;
public class PanelServer extends Thread {
    public final static int PORT = 7777;
    private JTextArea consoleArea;
    private BufferedReader in;
    public BufferedWriter out;
    ListData model = null;
    int id = 0;
    int port = 0;
    public PanelServer(ListData userdata,int port){
        this.port=port;
        this.model=userdata;
        consoleArea = whiteBoardGUI.ServerPanel.getConsole();
    }
    public void run() {
        OutputList output = new OutputList();
        DataRepository dataRepo = new DataRepository();
        ServerSocket s = null;
        try {
            s = new ServerSocket(port);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        do {
            System.out.print(id);
            try { 
              
                while (true) {
                    id++;
                    Socket socket = s.accept(); // Wait and accept a connection
                    User newUser = new User(id, socket);//Updating user record
                    new Multiple(socket, output, dataRepo, newUser, model,consoleArea);//Creating thread for multiple users
                }
            } catch (SocketException e) {
                consoleArea.append("Socket error(broken connection, address not reachable, bad data..)\n"+ e.getMessage());
                System.exit(0);
            } catch (IOException ioe) {
            	consoleArea.append("IOException ,this maybe caused by wrong input data\n "+ ioe);
                ioe.printStackTrace();
            } catch (NumberFormatException ne) {
            	consoleArea.append("A Correct Number is needed"+"\nNumberFormatException" + ne);
                ne.printStackTrace();
            } 
            finally 
            {
            	//No steps needed
            }

        } while (true);
    }


}

class Multiple extends Thread 
{
    private Socket client; 
    private BufferedReader in; 
    private BufferedWriter out; 
    private static int count; 
    private int id;
    public OutputList outputList; 
    public DataRepository dataRepo;
    private User user;
    private ListData model;
    public String[] ss;
    JTextArea consoleArea;
    public Multiple(Socket s, OutputList output, DataRepository dataRepo, User newUser, ListData model, JTextArea consoleArea) throws IOException {
        this.client = s;
        this.outputList = output;
        this.dataRepo = dataRepo;
        this.user= newUser;
        this.model=model;
        this.consoleArea = consoleArea;
        boolean decision=false;
        try
        {
        decision = parseLogonMsg();
        }
        catch(Exception e)
        {
        	System.out.println("Caught error in threads");
        	exit();
        	
        }
        if (decision)
        {
            this.start();
            model.add(this.user);
            model.refresh();  
        }
        else
        {
            out.write("quit");
            out.newLine();
            out.flush();
        }
    }
    public void run() 
    {

        try 
        {
          
                outputList.append(out);
                dataRepo.update(out);
                handleDrawMsg(); 
                outputList.remove(out);
                model.removeElement(this.user);
                model.refresh();
                synchronized(consoleArea)
                {
                	consoleArea.append("User left successfully id = " + this.id + " , "
                            + this.client);
                }
        }
        catch (IOException e) {

        } 
        finally 
        {
            try 
            {
                if (client != null)
                    client.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }

 
    public void handleDrawMsg() throws IOException {

        String dataBuffer = null;
        while (true) {
        	try
        	{
            dataBuffer = in.readLine(); 
        	}
        	catch(Exception e)
        	{
        		break;
        	}
        	if(dataBuffer == null)
        	{
        		break;
        	}
            if (dataBuffer.equals("quit"))
                break;
            dataRepo.append(dataBuffer);
            outputList.update(dataBuffer); 
        }
    }

    public boolean parseLogonMsg() throws IOException {
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(
                client.getOutputStream()));
        user.setName(in.readLine());
        int response = JOptionPane.showConfirmDialog(null, user.getName()+" want to join in?", "allow",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
              System.out.println(user.getName()+"quit!");
              return false;
            } else if (response == JOptionPane.YES_OPTION) {
              System.out.println(user.getName()+"join!");
            } else if (response == JOptionPane.CLOSED_OPTION) {
              System.out.println(user.getName()+"join!");
            }
            this.id = count++; 
            out.write(String.valueOf(id)); 
            out.newLine();
            out.flush();
            synchronized(consoleArea)
            {
            	 consoleArea.append("login success: id = " + this.id + " , " + this.client+"\n");
            }
       
        return true;
    }
    public void exit()
    {
    	System.out.println("Ending the current thread");
    	Thread.currentThread().interrupt();
    }
    
}
