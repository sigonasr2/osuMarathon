package osuMapCombiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

public class Convert {
	
	public final static boolean debugOutput=true;
	
	public static void Convert() {
		String marathonName = "";
		
		List<ListItem> maps = new ArrayList<ListItem>();
		List<String> points = new ArrayList<String>();
		List<TimingPoint> timingDatabase = new ArrayList<TimingPoint>();
		double timePoint=0;
		double remainder=0;
		for (int i=0;i<osuMapCombiner.model.getSize();i++) {
			ListItem l = (ListItem)osuMapCombiner.model.get(i);
			maps.add(l);
			
			/*
			 * Remove any remainder times due to decimals, since timings like to be integers, and we could be 1ms off.
			 */
			remainder = timePoint-Math.floor(timePoint);
			while (remainder>=1) {
				timePoint++;
				remainder--;
			}
			
			for (int j=0;j<l.timingPoints.size();j++) {
				String timingPoint = l.timingPoints.get(j);
				String[] split = timingPoint.split(",");
				Double timing = Double.parseDouble(split[0]);
				/*if (j==0) {
					timing=(int)timePoint;
				} else {*/
					timing+=timePoint;
				//}
				StringBuilder newString = new StringBuilder(Double.toString(timing));
				for (int k=1;k<split.length;k++) {
					newString.append(",");
					newString.append(split[k]);
				}
				timingDatabase.add(new TimingPoint(l.sliderMultiplier,newString.toString()));
				points.add(newString.toString());
			}
			timePoint+=l.songDuration;
		}

		if (debugOutput) {
			System.out.println(points.size()+" timing points added: ");
			for (int i=0;i<points.size();i++) {
				System.out.println(points.get(i));
			}
		 }
		
		
		do {
			marathonName = JOptionPane.showInputDialog("Enter a name for the marathon map:");
		} while (marathonName.length()==0);
		
		
		
		List<String> marathonMap = new ArrayList<String>();
		System.out.println("Beginning marathon map conversion process...");
		
		marathonMap.add("osu file format v14");
		marathonMap.add("");
		marathonMap.add("[General]");
		marathonMap.add("AudioFilename: marathon.mp3");
		marathonMap.add("AudioLeadIn: 0");
		marathonMap.add("PreviewTime: "+(int)Math.floor(osuMapCombiner.duration/2));
		marathonMap.add("Countdown: 1");
		marathonMap.add("SampleSet: Soft");
		marathonMap.add("StackLeniency: 0.5");
		marathonMap.add("Mode: 0");
		marathonMap.add("LetterboxInBreaks: 0");
		marathonMap.add("WidescreenStoryboard: 0");
		marathonMap.add("");
		/*
		 * Artist:FELT
ArtistUnicode:FELT
Creator:MokouSmoke
Version:Lunatic
Source:東方星蓮船　～ Undefined Fantastic Object.
Tags:Maika 舞花 Mika 美歌 NAGI☆Flying Fantastica touhou ZUN Byakuren Hijiri 感情の摩天楼 Emotional Skyscraper Cosmic Mind C93
DistanceSpacing: 1.2
BeatDivisor: 4
GridSize: 4
TimelineZoom: 1
BeatmapID:0
BeatmapSetID:-1
		 */
		marathonMap.add("[Editor]");
		marathonMap.add("DistanceSpacing: 1.2");
		marathonMap.add("BeatDivisor: 4");
		marathonMap.add("GridSize: 4");
		marathonMap.add("TimelineZoom: 1");
		marathonMap.add("");
		marathonMap.add("[Metadata]");
		marathonMap.add("Title:"+marathonName);
		marathonMap.add("TitleUnicode:"+marathonName);
		marathonMap.add("Artist:["+maps.size()+" songs]");
		marathonMap.add("Creator:osu!Marathon");
		marathonMap.add("Version:Marathon");
		marathonMap.add("Source:"+marathonName);
		marathonMap.add("Tags:"+marathonName);
		marathonMap.add("BeatmapID:0");
		marathonMap.add("BeatmapSetID:-1");
		marathonMap.add("");
		
		
		double averageHPDrain = AverageValues(DifficultyValues.HPDrainRate,maps);
		double averageCircleSize = AverageValues(DifficultyValues.CircleSize,maps);
		double averageOverallDifficulty = AverageValues(DifficultyValues.OverallDifficulty,maps);
		double averageApproachRate = AverageValues(DifficultyValues.ApproachRate,maps);
		double averageSliderMultiplier = AverageValues(DifficultyValues.SliderMultiplier,maps);
		
		double averageSliderTickRate = AverageValues(DifficultyValues.SliderTickRate,maps);
		
		double maxSliderMultiplier = 0;
		for (int i=0;i<maps.size();i++) {
			maxSliderMultiplier = Math.max(maps.get(i).sliderMultiplier,maxSliderMultiplier);
		}
		
		System.out.println("Averages: "+averageHPDrain+"/"+averageCircleSize+"/"+averageOverallDifficulty+"/"+averageOverallDifficulty+"/"+averageApproachRate+"/"+averageSliderMultiplier+"/"+averageSliderTickRate);

		marathonMap.add("[Difficulty]");
		marathonMap.add("HPDrainRate:"+(int)averageHPDrain);
		marathonMap.add("CircleSize:"+(int)averageCircleSize);
		marathonMap.add("OverallDifficulty:"+(int)averageOverallDifficulty);
		marathonMap.add("ApproachRate:"+(int)averageApproachRate);
		marathonMap.add("SliderMultiplier:"+maxSliderMultiplier);
		marathonMap.add("SliderTickRate:"+(int)averageSliderTickRate);
		marathonMap.add("");
/*[Events]
//Background and Video events
//Break Periods
//Storyboard Layer 0 (Background)
//Storyboard Layer 1 (Fail)
//Storyboard Layer 2 (Pass)
//Storyboard Layer 3 (Foreground)
//Storyboard Layer 4 (Overlay)
//Storyboard Sound Samples
 * 
 */
		marathonMap.add("[Events]");
		marathonMap.add("//Background and Video events\r\n");
		ListItem firstmap = maps.get(0);
		marathonMap.add("0,0,\""+firstmap.songTitle+"_BG.png\"");
		marathonMap.add("//Break Periods\r\n");

		timePoint=0;
		remainder=0;
		for (int i=0;i<maps.size();i++) {
			ListItem map = maps.get(i);
			remainder = timePoint-Math.floor(timePoint);
			while (remainder>=1) {
				timePoint++;
				remainder--;
			}
			List<String> breaks = maps.get(i).breaks;
			if (timePoint==0) {
				marathonMap.addAll(breaks);
			} else {
				for (int j=0;j<breaks.size();j++) {
					String[] b = breaks.get(j).split(",");
					b[1] = Integer.toString(Integer.parseInt(b[1])+(int)timePoint);
					b[2] = Integer.toString(Integer.parseInt(b[2])+(int)timePoint);
					StringBuilder breakString = new StringBuilder(b[0]);
					for (int k=1;k<b.length;k++) {
						breakString.append(",");
						breakString.append(b[k]);
					}
					marathonMap.add(breakString.toString());
				}
			}
			timePoint += map.songDuration;
		}
		int breaksEndIndex = marathonMap.size()-1; // Store the last break so we can append to it a little later. 
		
		marathonMap.add("//Storyboard Layer 0 (Background)\r\n");
		timePoint=firstmap.songDuration;
		remainder=0;
		for (int i=1;i<maps.size();i++) {
			ListItem map = maps.get(i);
			while (remainder>=1) {
				timePoint++;
				remainder--;
			}
			
			marathonMap.add("Sprite,Background,Centre,\""+map.songTitle+"_BG.png\",320,240");
			marathonMap.add(" F,0,"+(int)(timePoint-5000)+","+(int)(timePoint)+",0,1");
			marathonMap.add(" F,0,"+(int)(timePoint)+","+(int)(timePoint+map.songDuration-5000)+",1");
			marathonMap.add(" F,0,"+(int)(timePoint+map.songDuration-5000)+","+(int)(timePoint+map.songDuration)+",1,0");
			
			timePoint += map.songDuration;
		}
				marathonMap.add("//Storyboard Layer 1 (Fail)\r\n" + 
				"//Storyboard Layer 2 (Pass)\r\n" + 
				"//Storyboard Layer 3 (Foreground)\r\n" + 
				"//Storyboard Layer 4 (Overlay)\r\n" + 
				"//Storyboard Sound Samples");
		marathonMap.add("");
		marathonMap.add("[TimingPoints]");
		for(int i=0;i<timingDatabase.size();i++) {
			String[] timingPoint = timingDatabase.get(i).timingPointData.split(Pattern.quote(","));
			//marathonMap.add(timingDatabase.get(i));
			if (Double.parseDouble(timingPoint[1])>=0) {
				marathonMap.add(timingDatabase.get(i).timingPointData);
				marathonMap.add(timingPoint[0]+","+((-((maxSliderMultiplier/timingDatabase.get(i).initialSliderVelocity)*100)))+","+timingPoint[2]+","+timingPoint[3]+","+timingPoint[4]+","+timingPoint[5]+","+timingPoint[6]+","+timingPoint[7]);
			} else {
				marathonMap.add(timingPoint[0]+","+((-(1d/(100/(-Double.parseDouble(timingPoint[1])))*(maxSliderMultiplier/timingDatabase.get(i).initialSliderVelocity)))*100)+","+timingPoint[2]+","+timingPoint[3]+","+timingPoint[4]+","+timingPoint[5]+","+timingPoint[6]+","+timingPoint[7]);
			}
			
		}
		marathonMap.add("");
		
		marathonMap.add("[HitObjects]");

		timePoint=0;
		remainder=0;
		Integer breakStartPoint = -1;
		Integer breakEndPoint = -1;
		for (int i=0;i<maps.size();i++) {
			ListItem map = maps.get(i);
			remainder = timePoint-Math.floor(timePoint);
			while (remainder>=1) {
				timePoint++;
				remainder--;
			}
			for (int j=0;j<map.hitObjects.size();j++) {
				String hitobject = map.hitObjects.get(j);
				String[] split = hitobject.split(",");
				int originalTime = Integer.parseInt(split[2]);
				split[2] = Integer.toString(Integer.parseInt(split[2])+(int)timePoint);
				if (j==0 && breakStartPoint!=-1) {
					//Setup a break.
					breakEndPoint = Integer.parseInt(split[2])-1000;
					marathonMap.add(breaksEndIndex,"2,"+breakStartPoint+","+breakEndPoint); // Store the last break so we can append to it a little later.
					breakStartPoint = -1;
				} else {
					breakStartPoint = Integer.parseInt(split[2])+1000;
				}
				if (j==0) {
					//Reset the combo color if this is the first note of the next song. (Third bit indicates new combo)
					int numb = Integer.parseInt(split[3]);
					if (numb==1 || numb==2 || numb==8) {
						numb+=4;
					}
					split[3] = Integer.toString(numb);
				}
				if (split.length>=6) {
					//Might be a spinner.
					if (!split[5].contains("|") && !split[5].contains(":")) {
						//This is a spinner.
						split[5] = Integer.toString(Integer.parseInt(split[5])+(int)timePoint);
					} else {
						//This is a slider.
					}
				}
				//Re-write the hit object.
				StringBuilder hitObj = new StringBuilder(split[0]);
				for (int k=1;k<split.length;k++) {
					hitObj.append(",");
					hitObj.append(split[k]);
				}
				marathonMap.add(hitObj.toString());
			}
			timePoint += map.songDuration;
		}
		marathonMap.add("");
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose your osu! Songs folder to save the marathon map");
		int val = chooser.showOpenDialog(null);
		while (val!=JFileChooser.APPROVE_OPTION) {
			val = chooser.showOpenDialog(null);
		}
		File saveDir = chooser.getSelectedFile();
		File targetDir = new File(saveDir.getAbsolutePath(),marathonName);
		targetDir.mkdirs();
		String songCombiners = "";
		for (int i=0;i<maps.size();i++) {
			try {
				File filer = new File(maps.get(i).songLoc.getName());
				utils.copyFile(maps.get(i).songLoc, filer);
				filer.deleteOnExit();
				filer = new File(maps.get(i).songTitle+"_BG.png");
				utils.copyFile(filer, new File(targetDir,maps.get(i).songTitle+"_BG.png"));
				filer.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			songCombiners+=" \""+maps.get(i).songLoc.getName()+"\"";
		}
		File checkForDeletion = new File("marathon.mp3");
		checkForDeletion.delete();
		/*for (int i=0;i<maps.size();i++) {
			File filer = new File(maps.get(i).songLoc.getName().replace(".mp3", "_A.mp3"));
			String command = "ffmpeg -i \""+maps.get(i).songLoc.getName()+"\" -f mp3 -b:a 128k -ar 44100 -ac 2 \""+maps.get(i).songLoc.getName().replace(".mp3", "_A.mp3")+"\"";
			CommandLine cmdLine = CommandLine.parse(command);
			DefaultExecutor exec = new DefaultExecutor();
			try {
				exec.execute(cmdLine);

				System.out.println(filer.getName()+": "+mp3utils.GetSongDuration(filer.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			filer.deleteOnExit();
		}*/
		
		//Next, concatenate all files together.
		File f = new File("list.txt");
		FileWriter fw2;
		try {
			fw2 = new FileWriter(f);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			for (int i=0;i<maps.size();i++) {
				File filer = new File(maps.get(i).songTitle.replace(".osu", ".mp3"));
				bw2.write("file '"+filer.getAbsolutePath().replace("'", "'\\''")+"'\n");
			}
			bw2.close();
			fw2.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		f.deleteOnExit();
		
		String command = "ffmpeg -f concat -safe 0 -i list.txt -c copy marathon.mp3";
		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor exec = new DefaultExecutor();
		try {
			exec.execute(cmdLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		command = "mp3val marathon.mp3 -f -nb";
		cmdLine = CommandLine.parse(command);
		exec = new DefaultExecutor();
		try {
			exec.execute(cmdLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		File finalFile2 = new File("marathon.mp3");
		try {
			utils.copyFile(finalFile2, new File(targetDir,"marathon.mp3"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFile2.deleteOnExit();
		
		File beatmapLoc = new File(targetDir,marathonName+" [Marathon].osu");
		FileWriter fw;
		try {
			fw = new FileWriter(beatmapLoc);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i=0;i<marathonMap.size();i++) {
				bw.write(marathonMap.get(i));
				bw.write("\n");
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(osuMapCombiner.main.f, "Marathon map successfully created! Search for "+marathonName+" in osu! to play it!");
	}
	
	public static double AverageValues(DifficultyValues val,List<ListItem> maps) {
		double total = 0;
		for (int i=0;i<maps.size();i++) {
			switch (val) {
				case HPDrainRate:{
					total += maps.get(i).hpDrainRate;
				}break;
				case CircleSize:{
					total += maps.get(i).circleSize;
				}break;
				case OverallDifficulty:{
					total += maps.get(i).overallDifficulty;
				}break;
				case ApproachRate:{
					total += maps.get(i).approachRate;
				}break;
				case SliderMultiplier:{
					total += maps.get(i).sliderMultiplier;
				}break;
				case SliderTickRate:{
					total += maps.get(i).sliderTickRate;
				}break;
			}
		}
		return total/maps.size();
	}
}
