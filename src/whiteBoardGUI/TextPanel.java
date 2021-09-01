package whiteBoardGUI;

import java.awt.BasicStroke;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TextPanel extends JPanel implements ItemListener {

    
    private PaintCanvas drawPanel;
    private Choice fonttype;
    private Choice bolder;
    private Choice fontsize;

    public TextPanel(PaintCanvas area) {
        this.drawPanel=area;
        JPanel entirePanel = new JPanel(null);
        entirePanel.setLayout(new GridLayout(2, 1));
        int PanelHeight = 50;
        int PanelWidth = 350;
        entirePanel.setPreferredSize(new Dimension(PanelWidth, PanelHeight));
        entirePanel.setLayout(new GridLayout(1, 0, 15, 15));
        entirePanel.setBounds(new Rectangle(0, 0, 100, 160));
        entirePanel.setBorder(new TitledBorder(null, "Text",TitledBorder.LEFT, TitledBorder.TOP));
        fontsize = new Choice();
        {
            for (int i = 12; i <= 64; i += 2) {
                String size = String.valueOf(i);
                fontsize.add(size);
            }
        }
        fonttype = new Choice();
        GraphicsEnvironment fonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String ss[] = fonts.getAvailableFontFamilyNames();
        {
            for (int j = 0; j < ss.length; j++)
                fonttype.add(ss[j]);
        }

        String bold[] = { "Font.PLAIN", "Font.ITALIC", "Font.BOLD" };
        bolder = new Choice();
        {
            for (int i = 0; i < bold.length; i++) {
                bolder.add(bold[i]);
            }
        }

        fonttype.addItemListener(this);
        bolder.addItemListener(this);
        fontsize.addItemListener(this);
      
        entirePanel.add(fonttype);
        entirePanel.add(bolder);
        entirePanel.add(fontsize);
        
        add(entirePanel);
    }
    

    public void itemStateChanged(ItemEvent e) {
        int i = 0;

        if (e.getSource() == fonttype) {
            i = fonttype.getSelectedIndex();
        }
        if (e.getSource() == bolder) {
            i = bolder.getSelectedIndex();
            if (i == 0) {
                drawPanel.setBolder(Font.PLAIN);
            } else if (i == 1) {
                drawPanel.setBolder(Font.ITALIC);
            } else if (i == 2) {
                drawPanel.setBolder(Font.BOLD);
            }
        }
        if (e.getSource() == fontsize) {
            i = fontsize.getSelectedIndex();
        }
    }
}
