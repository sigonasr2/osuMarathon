package osuMapCombiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class utils {

	public static String[] readFromFile(String filename) {
		File file = new File(filename);
		//System.out.println(file.getAbsolutePath());
		List<String> contents= new ArrayList<String>();
		if (file.exists()) {
			try(
					FileReader fw = new FileReader(filename);
				    BufferedReader bw = new BufferedReader(fw);)
				{
					String readline = bw.readLine();
					do {
						if (readline!=null) {
							//System.out.println(readline);
							contents.add(readline);
							readline = bw.readLine();
						}} while (readline!=null);
					fw.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return contents.toArray(new String[contents.size()]);
	}
	public static void writetoFile(String[] data, String filename) {
		  File file = new File(filename);
			try {

				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file,false);
				PrintWriter pw = new PrintWriter(fw);
				
				for (String s : data) {
					pw.println(s);
				}
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
	  
	  public static void copyFile(File source, File dest) throws IOException {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       	}finally{
	           sourceChannel.close();
	           destChannel.close();
	       }
	  }
	  
	  public static void deleteFile(String filename) {
		  File file = new File(filename);
		  if (file.exists()) {
			  file.delete();
		  }
	  }
}
