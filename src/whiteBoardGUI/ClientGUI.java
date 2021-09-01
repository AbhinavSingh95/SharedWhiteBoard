package whiteBoardGUI;

import java.awt.EventQueue;

import javax.swing.*;

/**
 * @author Abhinav Singh, University of Melbourne
 */
public class ClientGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1257, 827);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		menuBar();
		toolBar();
	}
		
	private void menuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 1257, 22);
		JMenu File = new JMenu("File(F)");
		JMenuItem New=new JMenuItem("New(N)");
		JMenuItem Open=new JMenuItem("Open(O)");
		JMenuItem Save=new JMenuItem("Save(S)");
		JMenuItem SaveAs=new JMenuItem("Save As(A)");
		JMenuItem Exit = new JMenuItem("Exit(X)");
		File.add(New);
		File.add(Open);
		File.add(Save);
		File.add(SaveAs);
		File.add(Exit);
		JMenu Edit = new JMenu("Edit(E)");
		JMenuItem showToolbox=new JMenuItem("ShowToolbox(N)");
		Edit.add(showToolbox);
		JMenu Help = new JMenu("Help(H)");
		JMenuItem Helpo=new JMenuItem("Help");
		JMenuItem About=new JMenuItem("About(F)");
		Help.add(Helpo);
		Help.add(About);
		menuBar.add(File);
		menuBar.add(Edit);
		menuBar.add(Help);
		frame.getContentPane().add(menuBar);
	}
	private void toolBar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.HORIZONTAL);
		toolBar.setBounds(0, 22, 110, 546);
		
		frame.getContentPane().add(toolBar);
		
		JButton btnNewButton = new JButton("New button");
		toolBar.add(btnNewButton);
	}
}
