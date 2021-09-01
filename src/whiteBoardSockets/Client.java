package whiteBoardSockets;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import whiteBoardGUI.DrawStyle;
import whiteBoardGUI.PaintCanvas;
import whiteBoardGUI.BoardFrame;

import dataSource.*;

public class Client extends Thread implements DrawStyle 
{
    String userName;
    InetAddress address=null;
    int ID;
    private Socket socket;
    private final int PORT = 4988;
    private BufferedReader in; 
    public BufferedWriter out; 
    private PaintCanvas canvas = PaintCanvas.getInstance();
    public int id = 0;
    private Point pencilPoint = null; 
    private String messageBuffer;
    private String[] ss; 
    
    private Color pencilColor;
	private Color eraserColor;
	private Stroke pencilStroke;
	private Stroke eraserStroke;
	private String text;
	private String fonttype;
	private int bolder;
	private int fontsize;
	private int type;
	public ArrayList<String> savedData = new ArrayList<String>();

    private static Client panelClient = new Client();

    public static Client getInstance() {
        return panelClient;
    }
    public static void main(String[] args) {
        Client.getInstance();   
    }
    private void setUp() {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serverIP;
        String portString;
        JFrame frame = new JFrame();
        int inputPort = 0;
        boolean vaildPort = false;
        
        
        do {
            
            try {
                JTextField nameText= new JTextField();
                JTextField IPText= new JTextField();
                JTextField portText= new JTextField();
                //For generating radom usernames
                nameText.setText("NewUser"+(int)(Math.random()*100));
                IPText.setText(InetAddress.getLocalHost().getHostAddress());
                portText.setText("7777");
                Object setUpMessage[] = {new JLabel("Please Enter Preferred Name"),nameText,new JLabel("Please Enter the Server IP"),IPText,new JLabel("Enter Port Number"),portText};
                    JOptionPane optionPane = new JOptionPane();
                    optionPane.setMessage(setUpMessage);
                    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                    optionPane.setOptionType(optionPane.OK_CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog(null, "Width 100");
                    dialog.setVisible(true);
                    int value = ((Integer)optionPane.getValue()).intValue();
                    if (value == JOptionPane.OK_OPTION) {
                        userName=(String)(nameText.getText());
                        if (userName.length()==0){
                            throw (new UserNameNullException());
                        }
                        serverIP=(String)IPText.getText();
                        if (serverIP.length()==0){
                            serverIP=InetAddress.getLocalHost().getHostAddress();
                        }
                        address = InetAddress.getByName(serverIP);
                        
                        if (((String)(portText.getText())).length()==0){
                            portString="1024";
                        }else{
                            portString=(String)portText.getText();
                        }
                        inputPort = Integer.parseInt(portString);
                        if (!(inputPort >= 1024) && (inputPort <= 65535)) {
                            JOptionPane.showMessageDialog(null,"Port Out of Bounds","Warning", JOptionPane.ERROR_MESSAGE);
                        } else {
                            socket = new Socket(address,inputPort);
                            vaildPort = true;
                        }
                    }else{
                        System.exit(0);
                    }
            } catch (UserNameNullException e)  {
                JOptionPane.showMessageDialog(null,
                        "Please Enter a Valid User Name", "Error!!",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Please Enter a Valid Number", "Error!!",
                        JOptionPane.ERROR_MESSAGE);
             }catch (HeadlessException e1) { 
                 JOptionPane.showMessageDialog(null,
                         "Missing Head", "Error!!",
                         JOptionPane.ERROR_MESSAGE);
            } catch (UnknownHostException e1) {
                JOptionPane.showMessageDialog(null,
                        "UnknownHostException", "Error!!",
                        JOptionPane.ERROR_MESSAGE);
            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unkown exception",
                        "Fatal Error!!", JOptionPane.ERROR_MESSAGE);
            }       
        } while (!vaildPort);
        System.out.println(inputPort);

    }

    public Client() {
            this.setUp();
            try {
                
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                out.write(userName);
                out.newLine();
                out.flush();
                id = Integer.parseInt(in.readLine());
                BoardFrame app = BoardFrame.getInstance(false);
                app.setVisible(true);
                app.setuName(userName,address);
                this.start(); 
            }catch(SocketException e){
                JOptionPane.showMessageDialog(null,"Can't Joing the Current WhiteBoard Session", "Warning!!",JOptionPane.ERROR_MESSAGE);
                System.exit(5);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, "Fatal Error!!");
                System.exit(5);
            }
            catch(Exception e)
            {
                
            }    

    }
    public void run()
    {
        try {

            incomingMsg();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Read Message Failed!!");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
    public void incomingMsg() throws IOException 
    {

        while (true)
        {

            messageBuffer = in.readLine();
            if(messageBuffer==null)
            {
            	JOptionPane.showMessageDialog(null,
                        "Server Closed", "Error",
                        JOptionPane.ERROR_MESSAGE);
            	System.exit(0);
            }
            if(messageBuffer.equals("NEW"))
            {
            	System.out.println("Reached for clrscr");
            	BoardFrame.getInstance(false).clrscr();
            	continue;
            	
            }
            System.out.println(messageBuffer);
            //Deals with the situation when server wants to kick the user out or user decides to quit
            if (messageBuffer.equalsIgnoreCase("KICK")||messageBuffer.equalsIgnoreCase("quit"))
            {
                JOptionPane.showMessageDialog(null,"Forced Quit or Kicked Out", "Warning",JOptionPane.ERROR_MESSAGE);
                //Exit the execution
                System.exit(0); 
                out.close();             
            }
            	//Splitting the received message
                ss = messageBuffer.split("\\.");
                savedData.add(messageBuffer);
                System.out.println("Getting data");
                //Deciding whether start message or drag message
                if (ss.length > 4) { 
                    parseStartMsg();
                } else {
                    parseDragMsg(); 
                }
            }
        }

	public void parseStartMsg()
	{
		int id = 0, type = 0, scaleX = 0, scaleY = 0;
		Point startPoint = null;

		id = Integer.parseInt(ss[0]); 
		if (id == this.id)
			return; 
		type = Integer.parseInt(ss[1]); 
		pencilStroke = new BasicStroke((float) Integer.parseInt(ss[2]));
		eraserStroke = new BasicStroke((float) Integer.parseInt(ss[3]));
		pencilColor = ColorConvert.String2Color(ss[4]);
		eraserColor = ColorConvert.String2Color(ss[5]);
		scaleX = Integer.parseInt(ss[6]); 
		scaleY = Integer.parseInt(ss[7]);
		startPoint = new Point(scaleX, scaleY); 
		pencilPoint = startPoint; 
		text = ss[8];
		fonttype=ss[9];
		bolder = Integer.parseInt(ss[10]);
		fontsize = Integer.parseInt(ss[11]);
		canvas.getArr().addData(
				new DataSource(id, type,new Point(scaleX, scaleY), new Point(scaleX,
						scaleY), pencilColor, eraserColor,pencilStroke, eraserStroke, text, fonttype, bolder,
						fontsize));
	}

	public void parseDragMsg() 
	{
		int id = 0, scaleX = 0, scaleY = 0;
		Point endPoint = null;
		System.out.println("In Handle message:"+messageBuffer);
		if (messageBuffer.equals("x.x.x"))
			return;
		if(messageBuffer.equals("NEW"))
		{
			BoardFrame.getInstance(false).clrscr();
		}
		try
		{
		id = Integer.parseInt(ss[0]);
		}
		catch(Exception e)
		{
			System.out.println("Error:" +e.getMessage());
		}
		if (id == this.id)
			return;
		scaleX = Integer.parseInt(ss[1]);
		scaleY = Integer.parseInt(ss[2]);
		endPoint = new Point(scaleX, scaleY);
		System.out.println("No problem");

		int i;
		for (i = canvas.getArr().array.size() - 1; id != canvas.getArr().array
				.get(i).getId(); i--);
		System.out.println("No problem1");
		if (canvas.getArr().array.get(i).getPaintType() == FREE_DRAW) 
		{
			canvas.getArr().addData(new DataSource(id, type,new Point(pencilPoint),new Point(endPoint), pencilColor,eraserColor,pencilStroke,eraserStroke,text,fonttype,bolder,fontsize)); 
			pencilPoint = endPoint;

		} 
		else if(canvas.getArr().array.get(i).getPaintType() == ERASER)
		{
			canvas.getArr().addData(
					new DataSource(id, type,new Point(pencilPoint),
							new Point(endPoint), pencilColor,eraserColor,pencilStroke,eraserStroke,text,
							fonttype,bolder,fontsize)); 
			pencilPoint = endPoint;
		}
		else
		{
			canvas.getArr().array.get(i).setEndPoint( 
					new Point(endPoint));
		}
		canvas.repaint(); 
	}
	public void handleFileData(ArrayList<String> dataRecovered)
    {
    	for (int i = 0; i < dataRecovered.size(); ++i) 
		{
			messageBuffer = dataRecovered.get(i);
			System.out.println("In handleFileData:"+messageBuffer);
			ss = messageBuffer.split("\\.");
			System.out.println("Split Successfull");
			//System.out.print(ss[0]);
            if (ss.length > 4) 
            { 
            	System.out.println("Calling startmsg");
                parseStartMsg();
            } 
            else 
            {
            	System.out.println("Calling dragmsg");
                parseDragMsg(); 
            }
			System.out.println("Relopping");
		}
    }
    public Socket getSocket() 
    {
        return this.socket;
    }
    public ArrayList<String> getData()
    {
    	return savedData;
    }
}
class UserNameNullException extends Exception 
{
	//Class is left empty on user's discretion for handling the null name error as required
}
