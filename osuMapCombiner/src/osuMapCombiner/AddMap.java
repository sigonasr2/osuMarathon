package osuMapCombiner;

import java.io.File;
import java.io.FileFilter;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

class Task extends SwingWorker<Void,Void>{
	File f;
	Task(File f) {
		this.f=f;
	}
	/*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
		ListItem temp = new ListItem(f);
		if (!temp.failed) {
			osuMapCombiner.model.addElement(temp);
			if (osuMapCombiner.model.getSize()>=2) {
				osuMapCombiner.main.button2.setEnabled(true);
			}
		}
    	return null;
    }
}

public class AddMap {
	JFileChooser chooser = new JFileChooser();
	
	public AddMap() {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("osu! Beatmaps", "osu");
		chooser.addChoosableFileFilter(filter);
	}
	
	public void openDialog() {
		int val = chooser.showOpenDialog(null);
		if (val==JFileChooser.APPROVE_OPTION) {
			AddMap(chooser.getSelectedFile());
		}
	}

	public static void AddMap(File f) {
		Task task = new Task(f);
		task.execute();
	}
	
}