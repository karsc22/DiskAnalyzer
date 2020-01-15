import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JLabel;

public class Folder extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private boolean isFolder;
	
	public Folder(File file, long size, int x, int y, int width, int height) {
		isFolder = file.isDirectory();
		this.setLocation(x, y);
		this.setSize(width, height);
		String s = "  (" + DiskAnalyzer.getSimple(size) +")";
		setToolTipText(file.getAbsolutePath() + s);
		if (file.getParentFile() != null) {
			setText(file.getName() + s);
		} else {
			setText(file.getAbsolutePath() + s);
		}
		
		this.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (isFolder) {
			g2.setColor(Color.cyan);
		} else {
			g2.setColor(Color.pink);
		}
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.black);
		g2.drawRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}

}
