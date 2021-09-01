package whiteBoardGUI;

import whiteBoardSockets.*;
import java.util.*;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JLabel;

import dataSource.ColorConvert;
import dataSource.DataArray;
import dataSource.DataSource;

public class PaintCanvas extends Canvas implements DrawStyle, MouseListener,MouseMotionListener
{	/**
	 * PaintCanvas class governs the canvas related operations
	 */

	private static final long serialVersionUID = 1L;
	private Point pencilPoint = null;
	private Image screenBuffer;
	private Graphics screenGc;

	private int id; 
	private int type; 
	private Point startPoint; 
	private Point endPoint; 

	private int startX;
	private int startY;

	private DataArray arr;
	private DataSource data; 
	private String dataBuffer;
	private static PaintCanvas paintCanvas = new PaintCanvas();

	private int erasersize;
	private int pencilsize;
	private Color pencilColor;
	private Color eraserColor;
	private Stroke pencilStroke;
	private Stroke eraserStroke;
	private String text;

	public void setText(String text) {
		this.text = text;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public void setBolder(int bolder) {
		this.bolder = bolder;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	private String fonttype;
	private int bolder;
	private int fontsize;
	public ColorPanel color = null;
	public PencileStroke strokesize = null;

	public static PaintCanvas getInstance() {
		return paintCanvas;
	}

	public PaintCanvas() {
		type = LINE; 
		pencilStroke = new BasicStroke(1.0f);
		eraserStroke = new BasicStroke(5.0f);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		color = new ColorPanel(this);
		strokesize = new PencileStroke(this);
		pencilColor = this.getForeground();
		eraserColor = Color.white;
		startPoint = new Point(0, 0);
		endPoint = new Point(0, 0);
		arr = new DataArray();

		init();
	}


	public void init() {

		this.setBackground(Color.white);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setVisible(true);
	}

	public void setPencilColor(Color color) {
		this.pencilColor = color;
	}

	public void setPencilSize(int size) {
		this.pencilsize = size;
	}

	public void setEraserColor(Color color) {
		System.out.println("In set eraser color");
		this.eraserColor = Color.white;//changed it form color
	}
	public void setEraserSize(int size) {
		this.pencilsize = size;
	}
	public void setPencilStroke(Stroke stroke) {
		pencilStroke = stroke;
	}

	public void setEraserStroke(Stroke stroke) {
		eraserStroke = stroke;
	}

	
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();

		id = Client.getInstance().id;

		arr.addData(new DataSource(id, type,new Point(startX, startY), new Point(startX,
				startY), pencilColor, eraserColor,pencilStroke, eraserStroke, text, fonttype, bolder,
				fontsize));

		dataBuffer = new String(id + "." + type + "." + pencilsize + "."
				+ erasersize + "." + ColorConvert.Color2String(pencilColor)+"."+ColorConvert.Color2String(Color.white)+"."+startX + "." + startY + "." + text + "." +fonttype + "." +bolder + "." +fontsize);
		System.out.println("Mouse pressed: " + dataBuffer);
		sendGraphicMessage();

	}

	public void mouseReleased(MouseEvent e) {

		if (type != FREE_DRAW&&type!= ERASER) {
			int i;
			for (i = arr.array.size() - 1; this.id != arr.array.get(i).getId(); i--)
				;
			arr.array.get(i).setEndPoint(e.getPoint());
		}

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		dataBuffer = new String("x.x.x");
		sendGraphicMessage();
	}

	public void mouseDragged(MouseEvent e) {
	

		if (type == FREE_DRAW|| type == ERASER) {

			arr.addData(new DataSource(id, type,new Point(startX, startY), new Point(startX,startY), pencilColor, eraserColor,pencilStroke, eraserStroke, text, fonttype, bolder,fontsize));
			startX = e.getX();
			startY = e.getY();

		} else {
			int i;
			for (i = arr.array.size() - 1; this.id != arr.array.get(i).getId(); i--)
				;
			arr.array.get(i).setEndPoint(new Point(e.getX(), e.getY()));
		}

		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		repaint();
		dataBuffer = new String(this.id + "." + e.getX() + "." + e.getY());
		sendGraphicMessage();

	}
	//For handling open file event scenario
	public void openSavedData(ArrayList<String> rData)
	{
		
		for (int i = 0; i < rData.size(); ++i) 
		{
			String ss[] = null;
			String data;
			data = rData.get(i);
			System.out.println("In handleFileData:"+data);
			ss = data.split("\\.");
			System.out.println("Split Successfull");
			//System.out.print(ss[0]);
            if (ss.length > 4) 
            { 
            	System.out.println("Calling startmsg!!");
                parseStartMsg(ss,data);
            } 
            else 
            {
            	System.out.println("Calling dragmsg!!");
                parseDragMsg(ss,data); 
            }
			System.out.println("Relopping");
		}
	}
	public void parseStartMsg(String ss[],String data)
	{	
		int id = 0, type = 0, scaleX = 0, scaleY = 0;
		Point startPoint = null;

		id = Integer.parseInt(ss[0]); 
		//if (id == this.id)
			//return; 
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
		arr.addData(
				new DataSource(id, type,new Point(scaleX, scaleY), new Point(scaleX,
						scaleY), pencilColor, eraserColor,pencilStroke, eraserStroke, text, fonttype, bolder,
						fontsize));
		sendGraphicMessage(data);
	}
	

	public void parseDragMsg(String ss[],String data) 
	{
		int id = 0, scaleX = 0, scaleY = 0;
		Point endPoint = null;
		System.out.println("In Handle message:"+data);
		if (data.equals("x.x.x"))
		{
			System.out.print("Returned");
			return;
			
		}
		try
		{
		id = Integer.parseInt(ss[0]);
		}
		catch(Exception e)
		{
			System.out.println("Error:" +e.getMessage());
		}		
		scaleX = Integer.parseInt(ss[1]);
		scaleY = Integer.parseInt(ss[2]);
		endPoint = new Point(scaleX, scaleY);
		System.out.println("No problem");

		int i;
		for (i = arr.array.size() - 1; id != arr.array
				.get(i).getId(); i--);
		System.out.println("No problem1");
		if (arr.array.get(i).getPaintType() == FREE_DRAW) 
		{
			arr.addData(
					new DataSource(id, type,new Point(pencilPoint),
							new Point(endPoint), pencilColor,eraserColor,pencilStroke,eraserStroke,text,
							fonttype,bolder,fontsize)); 
			pencilPoint = endPoint;

		} 
		else if(arr.array.get(i).getPaintType() == ERASER)
		{
			arr.addData(
					new DataSource(id, type,new Point(pencilPoint),
							new Point(endPoint), pencilColor,eraserColor,pencilStroke,eraserStroke,text,
							fonttype,bolder,fontsize)); 
			pencilPoint = endPoint;
		}
		else
		{
			arr.array.get(i).setEndPoint( 
					new Point(endPoint));
		}
		repaint();
		sendGraphicMessage(data);
	}
	

	public void sendGraphicMessage() {
		try {
			Client.getInstance().out.write(dataBuffer);
			Client.getInstance().out.newLine();
			Client.getInstance().out.flush();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
	public void sendGraphicMessage(String data) {
		try {
			Client.getInstance().out.write(data);
			Client.getInstance().out.newLine();
			Client.getInstance().out.flush();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}

	public void update(Graphics g) {
		screenBuffer = createImage(getWidth(), getHeight());
		screenGc = screenBuffer.getGraphics();
		paint(screenGc);
		screenGc.dispose();
		g.drawImage(screenBuffer, 0, 0, null);
		screenBuffer.flush();
	}

	public void paint(Graphics g) {

		synchronized (this.arr) {
			Iterator<DataSource> i = arr.iterator();
			while (i.hasNext()) {
				data = i.next();

				startPoint = data.getStartPoint();
				endPoint = data.getEndPoint();
				GraphicAction.drawGraphics(data.getPaintType(), data.getText(),
						startPoint, endPoint, data.getPencilcolor(),
						data.getPencilStroke(), data.getEarsercolor(),
						data.getEraserStroke(), (Graphics2D) g);
			}
		}
	}
	public void clear()
	{
		arr = new DataArray();
		repaint();
	}

	public DataArray getArr() {
		return arr;
	}
	public void setType(int type) {
		this.type = type;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {

	}
}
