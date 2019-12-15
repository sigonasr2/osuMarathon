package osuMapCombiner;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

public class osuMapCombiner extends JPanel implements ActionListener, ListSelectionListener{
	
	AddMap mapdialog = new AddMap();
	public static DefaultListModel model = new DefaultListModel();
	public static JList l = new JList(model);
	public static double duration = 0;
	
	public static JFrame f = new JFrame();
	public static osuMapCombiner main;
	public JButton button = new JButton("+");
	public JButton button2 = new JButton("Combine");
	public JButton button3 = new JButton("Remove");
	public JButton button4 = new JButton("Duplicate");
	public static JProgressBar progressBar = new JProgressBar();
	
	osuMapCombiner() {
		osuMapCombiner.main = this;
		l.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane pane = new JScrollPane(l);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		l.setDragEnabled(true);
		l.setDropMode(DropMode.INSERT);
		l.setTransferHandler(new ListTransferHandler());
		
		l.setPreferredSize(new Dimension(280,360));
		l.addListSelectionListener(this);
		
		Component c = Box.createRigidArea(new Dimension(240,32));
		
		button.setActionCommand("Add");
		button.setPreferredSize(new Dimension(42,42));
		button.addActionListener(this);
		button2.setActionCommand("Combine");
		button2.setPreferredSize(new Dimension(360,24));
		button2.addActionListener(this);
		button3.setActionCommand("Remove");
		button3.setPreferredSize(new Dimension(120,24));
		button3.addActionListener(this);
		button3.setEnabled(false);
		button4.setActionCommand("Duplicate");
		button4.setPreferredSize(new Dimension(120,24));
		button4.addActionListener(this);
		button4.setEnabled(false);
		
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setSize(240, 32);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		button2.setEnabled(false);
		
		FlowLayout layout = new FlowLayout();
		f.setLayout(layout);
		
		f.add(l);
		f.add(button3);
		f.add(button4);
		//f.add(c);
		f.add(progressBar);
		f.add(button);
		f.add(button2);
		f.pack();
		
		f.setSize(360, 520);
		
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	    		osuMapCombiner program = new osuMapCombiner();
	        }
		});
		/*
		 * DETECT SONG DURATION
		Header h = null;
	    FileInputStream file = null;
	    String filename="E:/〇su/Songs/713989 FELT - Time and again (1)/audio.mp3";
	    try {
	        file = new FileInputStream(filename);
	    } catch (FileNotFoundException ex) {
	    }
	    Bitstream bitstream = new  Bitstream(file);
	    try {
	        h = bitstream.readFrame();

	    } catch (BitstreamException ex) {
	    }
	    long tn = 0;
		try {
			tn = file.getChannel().size();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    double duration = h.total_ms((int) tn);
	    
	    System.out.println(duration);
		
		
		String command = "mp3wrap.exe output.mp3 \"E:/〇su/Songs/33911 Yousei Teikoku - Hades_ The Rise (1)/02.mp3\" \"E:/〇su/Songs/13223 Demetori - Emotional Skyscraper ~ World'\\''s End (1)/Demetori -  Nada Upasana Pundarika - 06 - .mp3\" \"E:/〇su/Songs/713989 FELT - Time and again (1)/audio.mp3\"";
		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor exec = new DefaultExecutor();
		try {
			exec.execute(cmdLine);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Add": {
				mapdialog.openDialog();
			}break;
			case "Duplicate": {
				for (int i=0;i<l.getSelectedIndices().length;i++) {
					AddMap.AddMap(((ListItem)model.getElementAt(l.getSelectedIndices()[i])).file);
				}
			}break;
			case "Remove": {
				for (int i=0;i<l.getSelectedIndices().length;i++) {
					//System.out.println(Arrays.toString(l.getSelectedIndices()));
					model.removeElementAt(l.getSelectedIndices()[i--]);
				}
			}break;
			case "Combine":{
				if (model.getSize()>=2) {
						int pane = JOptionPane.showConfirmDialog(f,"A marathon map with all "+model.getSize()+" maps will be created. The total duration of the marathon map will be "+
								(int)Math.floor(duration/1000)+"s long. Proceed?"
					);
					if (pane == JOptionPane.YES_OPTION) {
						Convert.Convert();
					}
				} else {
					button2.setEnabled(false);
					JOptionPane.showMessageDialog(f, "You must have selected at least 2 songs to create a marathon map!");
				}
			}break;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		button3.setEnabled(l.getSelectedIndex()!=-1);
		button4.setEnabled(l.getSelectedIndex()!=-1);
	}

	public static void updateProgressBarAmt(int i, int j) {
		progressBar.setMaximum(j);
		progressBar.setMinimum(0);
		progressBar.setValue(i);
	}
}
