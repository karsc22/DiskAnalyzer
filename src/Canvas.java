import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class Canvas extends JComponent {
	private static final long serialVersionUID = 1L;
	
	boolean needsUpdate = true;
	HashMap<String, Long> sizes = new HashMap<String, Long>();
	int width = 1800;
	Graphics2D g;
	int borderX = 50;
	int borderY = 50;
	double ratio = 0;
	File rootFile = File.listRoots()[1];
	int x = 0;
	int y = 0;
	int files = 0;
	int dirs = 0;
	final JComboBox<String> comboBox;
	final JLabel status;
	Container container;
	JProgressBar pb;
	
	ArrayList<Folder> folders = new ArrayList<Folder>();
	
	public Canvas(final Container container) {
		this.container = container;
		addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            System.out.println("resized, c = " + c + ", " + c.getSize());
		        }
		});

	    comboBox = new JComboBox<String>();
	    for (File f : File.listRoots()) {
	    	comboBox.addItem(f.getAbsolutePath());
	    }
	    comboBox.setBounds(borderX, 15, 50, 30);
	    add(comboBox);
	    
	    JButton analyze = new JButton("Analyze");
	    analyze.setBounds(borderX + 70,15, 80, 30);
	    add(analyze);

	    
	    status = new JLabel();
	    status.setBounds(borderX + 180, 15, 180, 30);
	    add(status);
	    
	    pb = new JProgressBar();
	    pb.setBounds(borderX + 400, 15, 180, 30);
	    add(pb);
	    
	    analyze.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String) comboBox.getSelectedItem();
				status.setText("Analyzing " + selected + ", please wait...");
				
				Task task = new Task();
			    task.addPropertyChangeListener(new PropertyChangeListener(){
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("progress" == evt.getPropertyName()) {
							int progress = (Integer) evt.getNewValue();
							pb.setValue(progress);
						}
					}
			    });
			    task.execute();
			}
	    });
	}
	
	int progress;
	
	class Task extends SwingWorker<Void, Void> {
	    @Override
	    public Void doInBackground() {
			files = 0;
			dirs = 0;
			progress = 0;
			removeAllFolders();
			rootFile = File.listRoots()[comboBox.getSelectedIndex()];
			pb.setMaximum(rootFile.listFiles().length);
			System.out.println("max = " + pb.getMaximum());
			update(rootFile, this);
			createFolders(rootFile, 0, 0, this);
			status.setText("Files: " + files + "  Dirs: " + dirs);
			container.validate();
			container.repaint();
			return null;
	    }
	    
	    public void updateProgress(int p) {
	    	setProgress(p);
			System.out.println(p + " / " + pb.getMaximum());
	    }
	    
	}
	
	public int createFolders(File file, int depth, int x, Task t) {
		
		if (!sizes.containsKey(file.getAbsolutePath())) {
			return x;
		}
			
		int tempx = x;

		long size = sizes.get(file.getAbsolutePath());
		int w = (int) (size / ratio);
		if (w < 1) return x;
		
		if (depth == 1) {
			System.out.println("adding = " + file.getAbsolutePath() + " (" + DiskAnalyzer.getSimple(size) + ") at " + x + ", w = " + w);
		}
		addFolder(new Folder(file, size, borderX + x, 50*depth+borderY, w, 40));
		x += w;
		
		File[] files = file.listFiles();
		if (files == null) {return x; }
		for (File f2 : files) {
			tempx = createFolders(f2, depth+1, tempx, t);
		}
		return x;
	}
	
	public void update(File file, Task t) {
		long size = analyze(file, 0, t);
		ratio = size / width;
	}
	
	public void addFolder(Folder folder) {
		folders.add(folder);
		this.add(folder);
	}
	
	public void removeAllFolders() {
		for (Folder folder : folders) {
			this.remove(folder);
		}
		folders.clear();
	}
	
	
	public long analyze(File file, int depth, Task t) {
		if (depth > 18) return 0;

		if (depth == 1) {
			t.updateProgress(++progress);
		}
		
		if (!file.isDirectory()) {
			long len = file.length();
			files++;
			sizes.put(file.getAbsolutePath(), len);
			return len;
		}
		File[] files = file.listFiles();
		if (files == null) return 0;
		long size = 0;
		int numFiles = 0;
		for (File f : files) {
			size += analyze(f, depth+1, t);
			if (depth == 0) {
				System.out.println("just analyzed " + f.getAbsolutePath() + ", size = " + DiskAnalyzer.getSimple(size));
			}
			if (numFiles > 919) break;
			numFiles++;
		}
		dirs++;
		if (depth < 10) {
			sizes.put(file.getAbsolutePath(), size);
		}
		return size;
	}
	

}
