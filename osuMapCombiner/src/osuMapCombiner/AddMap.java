package osuMapCombiner;

import java.io.File;
import java.io.FileFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
			osuMapCombiner.model.addElement(new ListItem(chooser.getSelectedFile()));
		}
	}
	
}