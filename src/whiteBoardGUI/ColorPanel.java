package whiteBoardGUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class ColorPanel extends JPanel implements MouseListener {
	/**
	 * Class responsible for the color panel displayed on the whiteboard
	 */

	private static final long serialVersionUID = 1L;

	private int rColor[] = { 0x00, 0x80,0xA9, 0x8B, 0xFF,0xFF, 0xFF,0xFF, 0xFF,0x80, 0x00, 0x00,
			0x19,0x99, 0xFF, 0xDC,0xC0, 0xDE, 0xFF,0xFF, 0xFA,0xF5,
			0xF0,0x98, 0xAD, 0x87,0x7F,0xDA };

	private int gColor[] = { 0x00, 0x80,0xA9, 0x00, 0x00,0x45, 0xA5,0x8C, 0xFF,0x80, 0x80, 0x00,
			0x19 ,0x32,0xFF, 0xDC,0xC0, 0xB8, 0x45,0xA0, 0xD7,0xF5,
			0xE6,0xFB,0xFF, 0xCE,0xFF,0x70 };

	private int bColor[] = { 0x00, 0x80,0xA9, 0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00, 0xFF,
			0x70,0xCC, 0xFF, 0xDC,0xC0, 0x87, 0x00,0x7A, 0x60,0xDC,
			0x86,0x98, 0x2F, 0xFA,0xD4,0xD6 };

	private ColorGrid colorGrid[] = new ColorGrid[28];
	private ColorExplorer colorExplorer = new ColorExplorer();

	private Color foreColor = Color.BLACK;// forecolor
	private Color backColor = Color.WHITE;// backcolor
	private PaintCanvas area = null;

	public ColorPanel(PaintCanvas area) {
		JPanel grids = new JPanel(new GridLayout(2, 14, 1, 1));
		this.area = area;
		int i = 0;
		for (i = 0; i < colorGrid.length; i++) {
			Color color = new Color(rColor[i], gColor[i], bColor[i]);
			colorGrid[i] = new ColorGrid(color);
			grids.add(colorGrid[i]);
			colorGrid[i].addMouseListener(this);
			colorGrid[i]
					.setToolTipText("Click the left mouse button to adjust the foreground color, background color right adjustment!");
		}
		JPanel colorPanel = new JPanel(null);
		int colorPanelHeight = 40;
		int colorPanelWidth = 500;

		colorPanel.setPreferredSize(new Dimension(colorPanelWidth,
				colorPanelHeight));
		colorPanel.add(grids,BorderLayout.CENTER);
		colorPanel.add(colorExplorer);
		colorExplorer.setBounds(0, 2, colorPanelWidth, colorPanelHeight);
		grids.setBounds(colorPanelHeight + 5, 2, colorPanelWidth,
				colorPanelHeight);

		add(colorPanel);

	}

	

	private class ColorGrid extends JPanel {

		private static final long serialVersionUID = 1L;
		Color color = null;

		public ColorGrid(Color color) {
			this.color = color;
			setBorder(BorderFactory.createLoweredBevelBorder());
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(color);
			g.fillRect(2, 2, getWidth(), getHeight());
		}
	}

	private class ColorExplorer extends JPanel {

		private static final long serialVersionUID = 1L;

		public ColorExplorer() {
			setBorder(BorderFactory.createLoweredBevelBorder());
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(backColor);
			int width = getWidth();
			int height = getHeight();
			g.fill3DRect(width / 100, height / 2, width / 2, height / 2, true);
			g.setColor(foreColor);
			g.fill3DRect(width / 100, height / 20, width / 2, height / 2, true);
		}
	}

	public Color getBackColor() {
		return backColor;
	}

	public Color getForeColor() {
		return foreColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public void setForeColor(Color foreColor) {
		this.foreColor = foreColor;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		ColorGrid grid = (ColorGrid) e.getSource();
		int clickCount = e.getClickCount();

		if (clickCount == 1) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				System.out.println("came here");
				foreColor = grid.color;
				area.setPencilColor(foreColor);
				backColor = Color.WHITE;
				area.setEraserColor(backColor);
			} else {
				backColor = grid.color;
				area.setEraserColor(backColor);
			}
		} else if (clickCount == 2) {
			Color color = JColorChooser.showDialog(grid, "Edit Color",
					grid.color);
			if (color == null)
				return;
			foreColor = color;
			area.setPencilColor(foreColor);
		} else
			return;
		this.getParent().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
