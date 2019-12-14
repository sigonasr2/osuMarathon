package osuMapCombiner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class ListItem{
	String songTitle;
	File file;
	File songLoc;
	double songDuration=0;
	double hpDrainRate=7;
	double circleSize=7;
	double overallDifficulty=7;
	double approachRate=7;
	double sliderMultiplier=1;
	double sliderTickRate=1;
	double beatDuration=0;
	String[] data;
	List<String> timingPoints;
	List<String> hitObjects;
	List<String> breaks;
	boolean failed=false;
	public ListItem(File f) {
		//this.songTitle=s;
		timingPoints = new ArrayList<String>();
		hitObjects = new ArrayList<String>();
		breaks = new ArrayList<String>();
		songTitle = f.getName();
		System.out.println("Song Title: "+songTitle);
		data = utils.readFromFile(f.getAbsolutePath());
		String line = "";
		int i =0 ;
		
		String targetString = "AudioFilename: ";
		
		do {
			line = data[i++];
		} while (!line.contains(targetString));
		songLoc = new File(f.getParent()+File.separatorChar+line.replace(targetString, ""));
		System.out.println("SongLoc: "+songLoc);
		
		File filer = new File(songTitle.replace(".osu", ".mp3"));
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		String command = "ffmpeg -i \""+songLoc.getAbsolutePath()+"\" -f mp3 -b:a 128k -ar 44100 -ac 2 \""+songTitle.replace(".osu", ".mp3")+"\"";
		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor exec = new DefaultExecutor();
		exec.setStreamHandler(streamHandler);
		try {
			exec.execute(cmdLine);
			//System.out.println(filer.getName()+": "+mp3utils.GetSongDuration(filer.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		filer.deleteOnExit();
		
		String[] data2 = outputStream.toString().split("\n");
		for (int h=0;h<data2.length;h++) {
			if (data2[h].contains("Duration:")) {
				String[] s = data2[h].split(",");
				String timestamp = s[0].replace("Duration: ", "").trim();
				String[] timestamp_s = timestamp.split(":");
				int hrs = Integer.parseInt(timestamp_s[0]);
				int min = Integer.parseInt(timestamp_s[1]);
				double sec = Double.parseDouble(timestamp_s[2]);
				
				songDuration += hrs*60*60*1000;
				songDuration += min*60*1000;
				songDuration += sec*1000;
			}
		}
		
		osuMapCombiner.duration += songDuration;
		//osuMapCombiner.duration += songDuration = mp3utils.GetSongDuration(songLoc.getAbsolutePath());
		System.out.println("Song duration of "+songTitle+" : "+songDuration+"ms");
		

		targetString = "Mode: ";
		do {
			line = data[i++];
		} while (!line.contains(targetString));
		int mode = Integer.parseInt(line.replace(targetString, "").trim());
		if (mode!=0) {
			JOptionPane.showMessageDialog(osuMapCombiner.main.f, "This is not an osu! mode map! Parsing has been canceled.");
			failed=true;
			return;
		}
		
		targetString="[Difficulty]";
		do {
			line = data[i++];
		} while (!line.contains(targetString));
		
		for (int j=0;j<6;j++) {
			line = data[i++];
			switch (line.split(":")[0]) {
				case "HPDrainRate": {
					hpDrainRate = Double.parseDouble(line.split(":")[1]);
				}break;
				case "CircleSize": {
					circleSize = Double.parseDouble(line.split(":")[1]);
				}break;
				case "OverallDifficulty": {
					overallDifficulty = Double.parseDouble(line.split(":")[1]);
				}break;
				case "ApproachRate": {
					approachRate = Double.parseDouble(line.split(":")[1]);
				}break;
				case "SliderMultiplier": {
					sliderMultiplier = Double.parseDouble(line.split(":")[1]);
				}break;
				case "SliderTickRate": {
					sliderTickRate = Double.parseDouble(line.split(":")[1]);
				}break;
			}
		}
		targetString="[Events]";
		do {
			line = data[i++];
		} while (!line.contains(targetString));

		do {
			line=data[i++];
			if (line.trim().length()>3) {
				if (line.trim().startsWith("2,") && line.split(",").length==3) {
					breaks.add(line);
				}
			}
		} while (line.trim().length()>3);
		
		targetString="[TimingPoints]";
		do {
			line = data[i++];
		} while (!line.contains(targetString));
		
		do {
			line=data[i++];
			if (line.trim().length()>3) {
				timingPoints.add(line);
			}
		} while (line.trim().length()>3);
		
		targetString="[HitObjects]";
		do {
			line = data[i++];
		} while (!line.contains(targetString));

		do {
			if (i<data.length) {
				line=data[i++];
				if (Convert.debugOutput) {
					System.out.println(line);
				}
				if (line.trim().length()>3) {
					hitObjects.add(line);
				}
			}
		} while (i<data.length && line.trim().length()>3);
	}
	public String getData() {
		return songTitle;
	}
	public String toString() {
		return songTitle;
	}
}
