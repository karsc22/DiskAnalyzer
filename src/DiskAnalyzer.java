import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

public class DiskAnalyzer{

	final static int kilo = 1024;
	public static void main(String[] args) {
		new DiskAnalyzer().start();
	}
	
	public void start() {
		ToolTipManager.sharedInstance().setInitialDelay(0);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1900, 800));
		frame.pack();
		frame.setVisible(true);
		
		Container container = frame.getContentPane();

		container.add(new Canvas(container));
		container.validate();
	    
	}
	
	public static String getSimple(long num) {
		if (num > kilo * kilo * kilo) return num / (kilo * kilo * kilo) + " GB";
		if (num > kilo * kilo) return num / (kilo * kilo) + " MB";
		if (num > kilo) return num / kilo + " KB";
		return num + " B";
	}

}
