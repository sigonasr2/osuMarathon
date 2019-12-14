package osuMapCombiner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

public class mp3utils {
	/***
	 * @deprecated
	 * Returns song duration in milliseconds (mp3).
	 */
	/*public static double GetSongDuration(String filename) {
		Header h = null;
	    FileInputStream file = null;
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
		return duration;
	}*/
}
