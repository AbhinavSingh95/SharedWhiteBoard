package whiteBoardGUI;

import java.awt.*;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;

import dataSource.ListData;
import dataSource.User;

import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import whiteBoardSockets.ChatClient;
import whiteBoardSockets.Client;
import java.net.*;
import java.io.*;
import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;

/**
 * @author Abhinav Singh, University of Melbourne
 * BoardFrame class is responsible for the look and feel of the whiteboard
 */
public class BoardFrame extends JFrame implements ActionListener, Runnable {
	private String openPath;
	//Container Holds all the contents
    private Container c = getContentPane();
    /**
     * Designed Menu Bar consists of File, Edit and Help options which are stored in menuOptions
     * The above mentioned are further divided and stored into menuItem 
     */
    private String menuOption[] = { "File(F)", "Edit(E)","Chat(C)","Help(H)" };
    private String menuItem[][] = {{ "New(N)", "Open(O)", "Save(S)", "Save As(A)","Exit(X)" }, {"Cls(C)"},{"ChatApp(V)"},{ "Help(H)", "About(F)" } };
    private JMenuItem jMenuItem[][] = new JMenuItem[4][5];
    private JMenu jMenu[];
    /**
     * JPanel array for the whiteboard which is sub divided into several panels for holding various functionalities
     * The description of the 6 sub divisions  are
     * 0 - The Entire Panel
     * 1 - Tool Box
     * 2 - Functions
     * 3 - Tools extension
     * 4 - Color Panel
     * 5 - Server Info
     * 6 - Color Panel 
     */
    private JPanel jPanel[] = new JPanel[6];
    private JLabel jLabel = new JLabel("ready");
    private int drawPanelWidth = 1500, drawPanelHeight = 1000;
    private PencileStroke setPanel;
    private EraserStroke setPanel2;
    private TextPanel setPanel3;
    private PaintCanvas drawPanel;
    private boardDrawPanel bigDrawPanel;
    private ColorPanel colorPanel;
    public String uName;
    public InetAddress address;
    // tools areas as follows
    private String toolButtonName[] = { "Pencil", "Line", "Rect", "Oval","Eraser", "Text", "Circle", "Round Rectangle", "Curved Line", "Background" };
    private JToggleButton toggleButton[];
    private Icon tool[] = new ImageIcon[10];
    private String toolFilePath[] = { "/whiteBoardGUI/pencil.gif", "/whiteBoardGUI/line.gif","/whiteBoardGUI/square.gif", "/whiteBoardGUI/circle.gif", "/whiteBoardGUI/eraser.gif", "/whiteBoardGUI/text.gif","/whiteBoardGUI/oval.gif", "/whiteBoardGUI/shade.gif", "/whiteBoardGUI/round.gif", "/whiteBoardGUI/curve.gif" };
    private final int FREE_DRAW = 0;
    private final int ERASER = 1;
    private final int RECTANGLE = 2;
    private final int OVAL = 3;
    private final int CIRCLE = 8;
    private final int LINE = 5;
    private final int POLYGON = 11;
    private final int TEXT = 7;
    private final int ROUND_RECT =6;
    private final int CURVE=4;
    private int defaultTool;
    private int isFilled;
    // server area
    private JLabel serverText;
    private WindowAdapter quit;
    private int change = 0;
    private String fileName;
    private String filePath;
    private int x2, y2;
    private int saveAs = 0;
    //Server related va
    private JList userList;
    private InetAddress inet = null;
    private JPanel serverInfo;
    private JPanel userInfo;
    private JPanel serverOper;
    private ArrayList<User> users = new ArrayList<User>(30);
    private ListData model = new ListData(users);
    private Comparator cmp = Collator.getInstance(java.util.Locale.ENGLISH);
    public ServerSocket serverSocket = null;
    public int port = 0;
    public static Socket socket = null;

    public static boolean server = true;
    public static BoardFrame frame = new BoardFrame();
    
    public void setuName(String uName, InetAddress address)
    {
    	this.uName = uName;
    	this.address = address;
    }

    public static BoardFrame getInstance(boolean server1) {
        return frame;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        BoardFrame.server = server;
    }

    public void run() {
        // set the port for the server;

        try {
            this.setInet(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.users.clear();
        this.setDefaultSetting();
        this.setScreenConfiguration();
        this.setPanel();
        this.setVisible(true);
    }

    public BoardFrame() {
        BoardFrame.server = false;
        this.run();

    }

    public InetAddress getInet() {
        return inet;
    }

    public void setInet(InetAddress inet) {
        this.inet = inet;
    }

    private void setDefaultSetting() {
        if (BoardFrame.server) {
            this.fileName = "Server";
        } else {
            this.fileName = "no_name";
        }
        setTitle(fileName);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        try {
            this.addWindowListener(quit = new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    int response = JOptionPane.showConfirmDialog(null,
                            "Do you want to Quit?", "Confirm",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.NO_OPTION) {

                    } else if (response == JOptionPane.YES_OPTION) {
                        try {
                            if (frame.server) {
                                Client.getInstance().out
                                        .write("serverquit");
                                Client.getInstance().out.newLine();
                                Client.getInstance().out.flush();
                            } else {
                                Client.getInstance().out.write("quit");
                                Client.getInstance().out.newLine();
                                Client.getInstance().out.flush();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        System.exit(1);
                    } else if (response == JOptionPane.CLOSED_OPTION) {

                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unkown Error", "warning",
                    JOptionPane.ERROR_MESSAGE);
        }
        this.defaultTool = 1;
        this.isFilled = 0;
        // set the icon
        this.drawPanelWidth = 1500;
        this.drawPanelHeight = 1000;
        this.setIconImage(Toolkit.getDefaultToolkit().createImage(
                "src/img/logo.gif"));

    }

    private void setupMenuBar() {

        JMenuBar barTemp = new JMenuBar();
        jMenu = new JMenu[menuOption.length];
        for (int i = 0; i < menuOption.length; i++) {
            jMenu[i] = new JMenu(menuOption[i]);
            jMenu[i].setMnemonic(menuOption[i].split("\\(")[1].charAt(0));
            barTemp.add(jMenu[i]);
            if(i==0 || i==1)
            {
            	jMenu[i].setEnabled(false);//Disabling menu options for the slave client
            }
        }
        for (int i = 0; i < menuItem.length; i++) 
        {
            for (int j = 0; j < menuItem[i].length; j++) {
                jMenu[i].addSeparator();
                jMenuItem[i][j] = new JMenuItem(menuItem[i][j].split("\\|")[0]);
                if (menuItem[i][j].split("\\|").length != 1)
                    jMenuItem[i][j].setAccelerator(KeyStroke.getKeyStroke(
                            Integer.parseInt(menuItem[i][j].split("\\|")[1]),
                            ActionEvent.CTRL_MASK));
                jMenuItem[i][j].addActionListener(this);
                jMenuItem[i][j].setMnemonic(menuItem[i][j].split("\\(")[1]
                        .charAt(0));
                jMenu[i].add(jMenuItem[i][j]);
            }
        }
        this.setJMenuBar(barTemp);
    }

    private void setScreenConfiguration() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = 0;
        int height = 0;
        if(BoardFrame.server)
        {
        	this.setSize(1024,680);
        }
        else
        {
        	 width = screen.width;
             height = screen.height;
             this.setSize(width, height);
             this.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    public void setPanel() {
        if (!BoardFrame.server) {
            this.setupMenuBar();
        }
        // layout - border layout
        c.setLayout(new BorderLayout());
        for (int i = 0; i < 6; i++) {
            jPanel[i] = new JPanel();
        }
        // settoolbox to panel 1
        this.setupToolBox();
        this.setDraw();
        this.setupCompleteDrawPanel();
        this.setColorBox();
        this.setFunctionBox();
        this.setupServerPanel();
        this.setLabel();
        if (BoardFrame.server == false) 
        {
            jPanel[3].setLayout(new FlowLayout(FlowLayout.LEFT));
            jPanel[3].add(jPanel[2], FlowLayout.LEFT);
            jPanel[3].add(jPanel[1], FlowLayout.LEFT);

            JPanel temp = new JPanel();
            temp.add(drawPanel);
            temp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            jPanel[0].setLayout(new BorderLayout());
            jPanel[0].add(temp, BorderLayout.CENTER);
            jPanel[0].add(jPanel[3], BorderLayout.NORTH);

            jPanel[0].add(jPanel[4], BorderLayout.SOUTH);
        } 
        else 
        {
            jPanel[0].add(jPanel[5], BorderLayout.CENTER);
        }

        c.add(jPanel[0], BorderLayout.CENTER);
        c.add(jLabel, BorderLayout.SOUTH);

        this.setVisible(true);

    }

    private void setupToolBox() {

        ButtonGroup buttonGroup = new ButtonGroup();
        JToolBar jToolBar = new JToolBar("Tool Box", JToolBar.HORIZONTAL);
        toggleButton = new JToggleButton[toolButtonName.length];
        for (int i = 0; i < toolButtonName.length; i++) {
            tool[i] = new ImageIcon(BoardFrame.class.getResource(toolFilePath[i]));
            // jToggleButton[i] = new JToggleButton(ButtonName[i]);
            toggleButton[i] = new JToggleButton(tool[i]);
            toggleButton[i].addActionListener(this);
            toggleButton[i].setFocusable(false);
            buttonGroup.add(toggleButton[i]);
            jToolBar.add(toggleButton[i]);
            toggleButton[i].setToolTipText(toolButtonName[i]);
        }

        // default selected one
        toggleButton[defaultTool].setSelected(true);
        jToolBar.setLayout(new GridLayout(1, 0, 15, 15));
        jToolBar.setBounds(new Rectangle(0, 0, 100, 160));
        jToolBar.setBorder(new TitledBorder(null, "Tools",
                TitledBorder.LEFT, TitledBorder.TOP));

        toggleButton[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(FREE_DRAW);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });

        toggleButton[1].setToolTipText("Select and Drag the Line");
        toggleButton[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(LINE);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[2].setToolTipText("Select square and drag for size adjustment");
        toggleButton[2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(RECTANGLE);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[3].setToolTipText("Select Oval and Drag for size adjustment");
        toggleButton[3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(OVAL);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[4].setToolTipText("Select and Drag the eraser for erasing drawing");
        toggleButton[4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(ERASER);
                setPanel2.setVisible(true);
                setPanel.setVisible(false);
                setPanel3.setVisible(false);

            }
        });
        toggleButton[5].setToolTipText("Enter and place the text");
        toggleButton[5].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(TEXT);
                setPanel2.setVisible(false);
                setPanel.setVisible(false);
                setPanel3.setVisible(true);
                String string = JOptionPane
                        .showInputDialog("Please input the text you want!");
                drawPanel.setText(string);
            }
        });
        toggleButton[6].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(OVAL);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[7].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setBackground(colorPanel.getBackColor());
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[8].setToolTipText("Select and Drag the round rectangle");
        toggleButton[8].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(ROUND_RECT);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        toggleButton[1].setToolTipText("Select and Drag the Curved Line");
        toggleButton[9].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(CURVE);
                setPanel2.setVisible(false);
                setPanel.setVisible(true);
                setPanel3.setVisible(false);
            }
        });
        this.jPanel[1].add(jToolBar,BorderLayout.CENTER);

    }

    private void setDraw() {

        drawPanel = PaintCanvas.getInstance();

    }

    private void setupCompleteDrawPanel() {
        bigDrawPanel = new boardDrawPanel();
        bigDrawPanel.setLayout(null);
        JPanel temp = new JPanel();
        // bigDrawPanel.add(drawPanel);
        bigDrawPanel.add(temp);

        temp.add(drawPanel);
        temp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(
                172, 168, 153)));
        drawPanel
                .setBounds(new Rectangle(2, 2, drawPanelWidth, drawPanelHeight));
        drawPanel.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                x2 = e.getX();
                y2 = e.getY();

                jLabel.setText("drawing " + x2 + "," + y2 + ".....");
            }

            public void mouseMoved(MouseEvent e) {
                x2 = e.getX();
                y2 = e.getY();

                jLabel.setText(x2 + "," + y2);
            }

        });

        bigDrawPanel.setBorder(BorderFactory
                .createBevelBorder(BevelBorder.LOWERED));
        bigDrawPanel.setBackground(new Color(128, 128, 128));

    }

    private void setColorBox() {
    	/**
    	 * Sets the color box
    	 */
        colorPanel = new ColorPanel(drawPanel);
        jPanel[1].setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel[1].add(colorPanel,BorderLayout.CENTER);
    }

    private void setFunctionBox() {
        setPanel = new PencileStroke(drawPanel);
        jPanel[2].add(setPanel);
        setPanel2 = new EraserStroke(drawPanel);
        jPanel[2].add(setPanel2);
        setPanel3 = new TextPanel(drawPanel);
        jPanel[2].add(setPanel3);
        setPanel2.setVisible(false);
        setPanel.setVisible(true);
        setPanel3.setVisible(false);
    }

    private void setupServerPanel() {
    	/**
    	 * Functions helps in setting the server panel
    	 */

        jPanel[5].setLayout(new BorderLayout());
        serverInfo = new JPanel();
        this.serverText = new JLabel("new server has been start on port <br>");
        String text = "<html><b>IP:</b>";
        text += this.inet.getHostAddress();
        text += "<br><b>Port:</b>";
        text += this.getPort();
        text += "<br></html>";
        this.serverText.setText(text);
        // serverText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        serverInfo.add(serverText);
        serverInfo.setLayout(new GridLayout(1, 0, 15, 15));
        serverInfo.setBounds(new Rectangle(0, 0, 100, 160));
        serverInfo.setBorder(new TitledBorder(null, "Server Info",
                TitledBorder.LEFT, TitledBorder.TOP));
        jPanel[5].add(serverInfo, BorderLayout.NORTH);

        userInfo = new JPanel();
        userList = new JList(model);
        userInfo.setLayout(new GridLayout(1, 0, 15, 15));
        userInfo.setBounds(new Rectangle(0, 0, 100, 160));
        userInfo.setBorder(new TitledBorder(null, "Active Users",
                TitledBorder.LEFT, TitledBorder.TOP));
        // userList.add(1, index)
        userList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        userInfo.add(userList);

        serverOper = new JPanel();
        JButton kick = new JButton("kick out");
        JButton fresh = new JButton("refresh");
        kick.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.removeElement(userList.getSelectedIndex());
            }
        });
        fresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                model.refresh();
            }
        });

        userInfo.add(new JScrollPane(userList), BorderLayout.CENTER);
        serverOper.add(kick, BorderLayout.NORTH);
        serverOper.add(fresh, BorderLayout.SOUTH);

        jPanel[5].add(userInfo, BorderLayout.CENTER);
        jPanel[5].add(serverOper, BorderLayout.SOUTH);
    }

    private void setLabel() {
        jLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    }

    public void saveFile() {
    	/**
    	 * Function responsible for Save and SaveAs operation
    	 */
        if (this.fileName == "no_name" || this.saveAs == 1) 
        {
            FileDialog savedialog = new FileDialog(this,"Enter a Valid File Path", FileDialog.SAVE);
            savedialog.setVisible(true);
            if (savedialog.getFile() != null) 
            {
                try {
                    filePath = savedialog.getDirectory();
                    fileName = savedialog.getFile();
                    FileOutputStream out1 = new FileOutputStream(filePath+fileName+".jpg");
                    Component component = drawPanel;
                    BufferedImage bi = (BufferedImage) component.createImage(
                            component.getWidth(), component.getHeight());
                    component.paint(bi.getGraphics());
                    BufferedOutputStream out = new BufferedOutputStream(out1);
                    ImageIO.write(bi,"jpeg",out);
                    out.flush();
                    out.close();
                    //For saving the file as list of values
                    FileWriter out2 = new FileWriter(filePath
                            + fileName + ".txt",false);
                    BufferedWriter outB = new BufferedWriter(out2);
                    ArrayList<String> savedData = Client.getInstance().getData();
                    for (int i = 0; i < savedData.size(); ++i) 
            		{
            			String data = savedData.get(i);
            			outB.write(data);
            			outB.newLine();
            			outB.flush();
            		}
                    outB.close();
                    out2.close();
                    
                    this.setTitle(fileName);
                    this.saveAs = 0;
                } catch (Exception EE) {
                }
            } else {
                this.saveAs = 0;
                return;
            }
            
        } 
        else {
            try {
            	System.out.println(filePath);
            	System.out.println(fileName);
            	String valTxt = openPath.replace(".jpg",".txt");
            	System.out.println("In save");
                FileOutputStream out2 = new FileOutputStream(openPath);
                Component component = drawPanel;
                BufferedImage bi = (BufferedImage) component.createImage(
                        component.getWidth(), component.getHeight());
                component.paint(bi.getGraphics());
                BufferedOutputStream out = new BufferedOutputStream(out2);
                ImageIO.write(bi,"jpeg",out);
                out.flush();
                out.close();
              //For saving the file as list of values
                FileWriter outer = new FileWriter(valTxt,false);
                BufferedWriter outB = new BufferedWriter(outer);
                ArrayList<String> savedData = Client.getInstance().getData();
                for (int i = 0; i < savedData.size(); ++i) 
        		{
        			String data = savedData.get(i);
        			outB.write(data);
        			outB.newLine();
        			outB.flush();
        		}
                outB.close();
                out2.close();
            } catch (Exception EE) {
            }
        }
    }

    public void exit() {
        this.quit.windowClosing(null);

    }

    public void open()throws IOException {
        JFileChooser fileDialog = new JFileChooser(".");
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.addChoosableFileFilter(new FileFilter() 
        {
            @Override
            public boolean accept(File f) 
            {
                if (f.isDirectory())
                {
                	return true;
                }
                    
                String name = f.getName().toLowerCase();
                if (name.endsWith(".txt")||name.endsWith("jpg"))
                {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() 
            {
                return "Image File";
            }
        });

        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.showOpenDialog(this);
        File file = fileDialog.getSelectedFile();
        if (file == null)
        {
        	return;
        }
        System.out.println("Out of return");  
        fileName = file.getName();
        filePath = file.getPath();
        openPath = filePath;
        System.out.println(fileName);
        System.out.println(filePath);
        this.setTitle(fileName);
        System.out.println("After Set title");
        System.out.println(filePath);
        System.out.println("Going to reader");
        FileReader out2 = new FileReader(openPath.replace(".jpg", ".txt"));
        BufferedReader br = new BufferedReader(out2);
        System.out.println("Out of reader");
        String st;
        ArrayList<String> dataRecovered = new ArrayList<String>();
        while ((st = br.readLine()) != null)
        {
          dataRecovered.add(st); 
        }
        System.out.println("Accumulated and now calling handleFileData");
        PaintCanvas.getInstance().openSavedData(dataRecovered);
        
    }
    public void actionPerformed(ActionEvent e) 
    {
    	/**
    	 * Initiates actions based on menu selection by the user
    	 * Some important menu options are New,Open,Save,SaveAs,Chat application
    	 */
        if (e.getSource() == jMenuItem[0][0]
                || e.getSource() == jMenuItem[1][0]) 
        {
        	System.out.println("In New Block");
        	int response = JOptionPane.showConfirmDialog(null,
                    "All content will be erased, Do you want to continue?", "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        	if(response == JOptionPane.NO_OPTION)
        	{
        		//Do Nothing
        	}
        	else
        	{
        		try
        		{
        		 Client.getInstance().out.write("NEW");
                 Client.getInstance().out.newLine();
                 Client.getInstance().out.flush();
        		}
        		catch(Exception e1)
        		{
        			System.out.println("New Response problem");
        		}
        		clrscr();
        	}
        } else if (e.getSource() == jMenuItem[0][1]) {
        	try {
            this.open();
        	}
        	catch(Exception e1)
        	{
        		System.out.println("In open image:"+e1.getMessage());
        	}
        } else if (e.getSource() == jMenuItem[0][2]) {
            this.saveFile();
        } else if (e.getSource() == jMenuItem[0][3]) {
            this.saveAs = 1;
            this.saveFile();
        } else if (e.getSource() == jMenuItem[0][4]) {
            this.exit();
        }
        else if(e.getSource() == jMenuItem[2][0])
        {
        	System.out.println("I am in Chat window options!!!!!!!!!!!!!!!!!!!!!");
        	new SwingWorker()
			{
				protected Object doInBackground()throws Exception
				{
					try
		        	{
		        	String a[] = {uName};
		        	//whiteBoardSockets.ChatClient.main(a,address);
		        	ChatClient chat = new ChatClient(uName,address);
		        	}
		        	catch(Exception e1)
		        	{
		        		System.out.println("Chat application failed while launching!!");
		        	}
					return null;
				}
			}.execute();
        	
        }
        else if(e.getSource() == jMenuItem[1][0])
        {
        	//Clear Screen Block
        	System.out.println("In clearscreen block");
        	try
    		{
    		 Client.getInstance().out.write("NEW");
             Client.getInstance().out.newLine();
             Client.getInstance().out.flush();
    		}
    		catch(Exception e1)
    		{
    			System.out.println("New Response problem");
    		}
    		clrscr();
        	
        }

    }
   

    public int getPort() {
        return port;
    }

    public void setPort(int inputPort) {

        this.port = inputPort;
    }
    public void clrscr()
    {
    	drawPanel.clear();
    }
    
    public class boardDrawPanel extends JPanel implements MouseListener,MouseMotionListener 
    {
    	/**
    	 *Administers all the mouse related events initiated on the drawing panel
    	 */
        public int x, y;
        float data[] = { 2 };
        public JPanel smallPanel1 = new JPanel(), smallPanel2 = new JPanel(),
                smallPanel3 = new JPanel();

        public boardDrawPanel() {
            this.setLayout(null);
            this.add(smallPanel1);
            this.add(smallPanel2);
            this.add(smallPanel3);

            smallPanel1.setBounds(new Rectangle(drawPanelWidth + 3,
                    drawPanelHeight + 3, 5, 5));
            smallPanel1.setBackground(new Color(0, 0, 0));
            smallPanel2.setBounds(new Rectangle(drawPanelWidth + 3,
                    drawPanelHeight / 2, 5, 5));
            smallPanel2.setBackground(new Color(0, 0, 0));
            smallPanel3.setBounds(new Rectangle(drawPanelWidth / 2,
                    drawPanelHeight + 3, 5, 5));
            smallPanel3.setBackground(new Color(0, 0, 0));
            smallPanel1.addMouseListener(this);
            smallPanel1.addMouseMotionListener(this);
            smallPanel2.addMouseListener(this);
            smallPanel2.addMouseMotionListener(this);
            smallPanel3.addMouseListener(this);
            smallPanel3.addMouseMotionListener(this);
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            drawPanelWidth = x;
            drawPanelHeight = y;

            smallPanel1.setLocation(drawPanelWidth + 3, drawPanelHeight + 3);
            smallPanel2
                    .setLocation(drawPanelWidth + 3, drawPanelHeight / 2 + 3);
            smallPanel3
                    .setLocation(drawPanelWidth / 2 + 3, drawPanelHeight + 3);
            drawPanel.setSize(x, y);
            repaint();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            if (e.getSource() == smallPanel2) {
                x = e.getX() + drawPanelWidth;
                y = drawPanelHeight;
            } else if (e.getSource() == smallPanel3) {
                x = drawPanelWidth;
                y = e.getY() + drawPanelHeight;
            } else {
                x = e.getX() + drawPanelWidth;
                y = e.getY() + drawPanelHeight;
            }
            repaint();
            jLabel.setText(x + "," + y);
        }

        public void mouseMoved(MouseEvent e) {

        }

        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            super.paint(g2d);

            g2d.setPaint(new Color(128, 128, 128));
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10, data, 0));
            g2d.draw(new Rectangle2D.Double(-1, -1, x + 3, y + 3));
        }
    }

}
