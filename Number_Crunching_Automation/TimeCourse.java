import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TimeCourse{
	String orderfile; //name of order order file
	String codefile; //name of the supercoder file that has been cleaned
	int trials = 0; //number of trials
	int types = 0; //number of types of trials
	int maxLength;
	double frameRate;
	String name; //name of participant
	BufferedReader br; 
	ArrayList<String> targets; //targets read from the order file
	ArrayList<String> trialNames; //names of types of trials read from trialTypes file
	ArrayList<ArrayList<Integer>> zeroes;
	ArrayList<ArrayList<Integer>> trialTypes; 
	ArrayList<ArrayList<Integer>> collapsedTypes; 
	ArrayList<ArrayList<Double>> collapsedZeroes;
	ArrayList<Integer> windows; //temporory arraylist of windows read in from trialtypes file
	ArrayList<ArrayList<Integer>> winAll; //array list of array list of windows assigned to each trial type
	ArrayList<Double> typeAccuracies;

	public static void main(String[] args){
		try{
			TimeCourse m = new TimeCourse(args[0], args[1], args[2]);
		}

		catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}

	// java TimeCourse ASP01 test_clean.txt order2.txt
	public TimeCourse(String arg1, String arg2, String arg3) throws Exception{
		name = arg1;
		codefile = arg2;
		orderfile = arg3;

		readTypes();
		readTarget();

		zeroes = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < trials; i++)
			zeroes.add(new ArrayList<Integer>());
		parseClean();
		
		trim_zeroes();

		windowAnalyses(true);

		collapsedZeroes = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < types; i++)
			collapsedZeroes.add(new ArrayList<Double>());
		collapse_zeroes();
		
		liststocsv("output/collapsed/" + name + "_collapsed.csv", collapsedZeroes);

		
	}

	public TimeCourse(String s, ArrayList<ArrayList<Double>> d) throws Exception{
		readTypes();
		liststocsv(s, d);
	}

	ArrayList<ArrayList<Double>> getCollapse(){
		return collapsedZeroes;
	}

	ArrayList<String> getTrialNames(){
		return trialNames;
	}

	void readTypes() throws Exception{
		br = new BufferedReader(new FileReader("orders/trialtypes.txt"));
		String line = "";
		trialNames = new ArrayList<String>();
		windows = new ArrayList<Integer>();
		line = br.readLine(); //frame rate
		String[] res = line.split(" = ");
		frameRate = Double.parseDouble(res[1]);
		line = br.readLine(); //maxlength;
		res = line.split(" = ");
		maxLength = Integer.parseInt(res[1]);
		
		while((line = br.readLine())!=null){
			res = line.split("\\s");
			trialNames.add(res[1]);
			int i = 2;
			while(i < res.length){
				String[] nums = res[i].split(":");
				int windowStart = Integer.parseInt(nums[0]);
				int windowEnd = Integer.parseInt(nums[1]);
				windows.add(types);
				windows.add(windowStart);
				windows.add(windowEnd);
				i++;
			}
			types++;
		}

		trialTypes = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < types; i++)
			trialTypes.add(new ArrayList<Integer>());
	}

	void initializeWinAll(boolean fullTrial){
		winAll = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < types; i++)
			winAll.add(new ArrayList<Integer>());

		if (fullTrial){
			for(int i = 0; i < types; i++){
				winAll.get(i).add(0);
				winAll.get(i).add(maxLength - 1);
			}
		} else {
			int k = 0;
			while(k < windows.size()){
				int type = windows.get(k);
				int windowStart = windows.get(k + 1);
				int windowEnd = windows.get(k + 2);
				winAll.get(type).add(windowStart);
				winAll.get(type).add(windowEnd);
				k = k + 3;
			}
		}

	}

	ArrayList<Double> windowAnalyses(boolean fullTrial) throws Exception{
		initializeWinAll(fullTrial);
		PrintWriter winTrialWriter;
		PrintWriter winTypeWriter;
		if (fullTrial){
			winTrialWriter = new PrintWriter(new FileWriter("output/Detailed Trial Accuracy.csv", true));
			winTypeWriter = new PrintWriter(new FileWriter("output/Detailed Type Accuracy.csv", true));
			collapsedTypes = new ArrayList<ArrayList<Integer>>();
			for(int i = 0; i < types; i++)
				collapsedTypes.add(new ArrayList<Integer>());
			
		} else {
			winTrialWriter = new PrintWriter(new FileWriter("output/Detailed Window Analysis Trial Accuracy.csv", true));
			winTypeWriter = new PrintWriter(new FileWriter("output/Detailed Window Analysis Type Accuracy.csv", true));
		}

		typeAccuracies = new ArrayList<Double>();

		for(int i = 0; i < types; i++){
			int type = i;
			double typeSum = 0;
			double typeCount = 0.0; //how many trials of a particular type
			for(int j = 0; j < trialTypes.get(i).size(); j++){
				int trial = trialTypes.get(i).get(j);
				int countz = 0;
				int counto = 0;
				for(int m = 0; m < winAll.get(type).size(); m = m+2){
					int windowStart = winAll.get(type).get(m);
					int windowEnd = winAll.get(type).get(m+1);
					for(int n = windowStart; n < windowEnd && n < zeroes.get(trial).size(); n++){
						if (zeroes.get(trial).get(n) == 1)
							counto++;
						else if (zeroes.get(trial).get(n) == 0)
							countz++;
					}	
				}
				double trialaccuracy = ((double) counto)/(countz + counto);
				winTrialWriter.print(name + "," + orderfile + "," + "Trial " + (trial + 1) + ",");
				winTrialWriter.print(trialaccuracy + "," + trialNames.get(type) + ",");
				winTrialWriter.print(counto + "," + countz + ",");


				if ((countz + counto) >= 15){
					typeSum = typeSum + counto;
					typeCount = typeCount + counto + countz;
					winTrialWriter.println();
					if (fullTrial){
						collapsedTypes.get(type).add(trial);
					}
				} else {
					winTrialWriter.println("DROPPED");
				}
			}
			
			
			double typeAcc = typeSum/typeCount;
			winTypeWriter.println(name + "," + orderfile + "," + trialNames.get(type) + "," + typeAcc);
			typeAccuracies.add(typeAcc);
		}
		winTrialWriter.println();
		winTrialWriter.close();
		winTypeWriter.println();
		winTypeWriter.close();

		return typeAccuracies;
			
	}

	void readTarget()throws Exception{
		br = new BufferedReader(new FileReader("orders/" + orderfile));
		String line = "";
		targets = new ArrayList<String>();
		int trialnum = 0;
		int type;
		String[] res;
		while((line = br.readLine())!= null){
			if (line.length() > 0){
				res = line.split("\\s");
				targets.add(res[0]);
				type = Integer.parseInt(res[1]);
				trialTypes.get(type).add(trialnum);
				trials++;
				trialnum++;
			}
		}
	}

	void parseClean()throws Exception{
		br = new BufferedReader(new FileReader(codefile));
		String line = "";
		String splitBy = "\\s";
		int i = 0;
		int lineCount = 1;
		int prev = 0;
		int current, next;

		PrintStream stream = new PrintStream("output/zeroes/" + name + "_zeroes.csv");
		PrintStream error = new PrintStream("Error Log.txt");
		PrintStream console = System.out; 
		System.setOut(stream);

		while((line = br.readLine())!= null && i < trials){
			String[] res = line.split(splitBy);
			if (line.charAt(0) == 'B'){
				try {
					prev = Integer.parseInt(res[1]);
				} catch (NumberFormatException e) {
					System.setOut(error);
					System.out.println("Error! Extra space/character in code at line " + lineCount);
					System.out.println("Check after code and onsets/offsets for extra space or unintended characters.\n");
					System.setOut(console);
					System.out.println("Error detected.");
					System.setOut(stream);
				}
			} else if (line.charAt(0) == 'L' || line.charAt(0) == 'R' || line.charAt(0) == 'C') {
				try {
					current = Integer.parseInt(res[1]);
					next = Integer.parseInt(res[2]);
					add_space(current - prev, i);

					if (res[0].compareTo(targets.get(i)) == 0)
						add_ones(next - current, i);
					else
						add_zeroes(next - current, i);

					prev = next;
				} catch (NumberFormatException e) {
					System.setOut(error);
					System.out.println("Error! Extra space/character in code at line " + lineCount + " in file " + name);
					System.out.println("Check after code and onsets/offsets for extra space or unintended characters.");
					System.setOut(console);
					System.out.println("Error detected.");
					System.setOut(stream);
				}

			} else if (line.charAt(0) == 'S') {
				try {
					current = Integer.parseInt(res[1]);
					add_space(current - prev, i);
					i++;
					System.out.println();
				} catch (NumberFormatException e) {
					System.setOut(error);
					System.out.println("Error! Extra space/character in code at line " + lineCount);
					System.out.println("Check after code and onsets/offsets for extra space or unintended characters.");
					System.setOut(console);
					System.out.println("Error detected.");
					System.setOut(stream);
				}

			}
			lineCount++;
		}
		System.setOut(console);			
	}

	void add_space(int n, int trialnum){
		for(int i = 0; i < n; i++){
			zeroes.get(trialnum).add(-1);
			System.out.print(" ,");
		}
	}

	void add_zeroes(int n, int trialnum){
		for(int i = 0; i < n; i++) {
			zeroes.get(trialnum).add(0);
			System.out.print("0,");
		}
	}

	void add_ones(int n, int trialnum){
		for(int i = 0; i < n; i++){
			zeroes.get(trialnum).add(1);
			System.out.print("1,");
		}
	}

	void trim_zeroes(){
		for(int i = 0; i < zeroes.size(); i++){
			if (zeroes.get(i).size() > maxLength){
				int sizeDiff = zeroes.get(i).size() - maxLength;
				
				for(int j = 0; j < sizeDiff; j++){
					//remove from beginning
					//zeroes.get(i).remove(0);
					//remove from end
					int index = zeroes.get(i).size() - 1;
					zeroes.get(i).remove(index);
				}

				
			}
		}
	}

	void collapse_zeroes(){
		for(int i = 0; i < types; i++){
			int t = collapsedTypes.get(i).size();

			for(int k = 0; k < maxLength; k++){
				int sum = 0;
				int count = 0;
				for(int j = 0; j < t; j++){
					if (k  < zeroes.get(collapsedTypes.get(i).get(j)).size()){
						int num = zeroes.get(collapsedTypes.get(i).get(j)).get(k);
						if (num == 0)
							count++;
						if (num == 1){
							count++;
							sum++;
						}	
					}	
				}
				if (count == 0)
					collapsedZeroes.get(i).add(-1.0);
				else{
					double result = ((double) sum)/count;
					collapsedZeroes.get(i).add(result);	
				}
			}			
		}
	}

	void liststocsv(String extension, ArrayList<ArrayList<Double>> data) throws Exception{
		PrintStream stream = new PrintStream(extension);
		PrintStream console = System.out; 
		System.setOut(stream);

		double frameLength = 1.0/frameRate;
		double timeLine = 0;
		System.out.print("Time,");
		for(int i = 0; i < maxLength; i++){
			timeLine = timeLine + frameLength;
			System.out.print(String.format("%.3f", timeLine) + ", ");
		}
		System.out.println();
					

		for(int i = 0; i < types; i++){
			System.out.print(trialNames.get(i) + ", ");
			for(int j = 0; j < Math.min(data.get(i).size(), maxLength); j++) //300
				if (data.get(i).get(j) != -1)
					System.out.print(data.get(i).get(j) + ", ");
				else
					System.out.print(",");
			System.out.println();
		}
		System.setOut(console);		
	}

	public void createDetailed() throws Exception{
		PrintWriter coll = new PrintWriter(new FileWriter("output/Detailed Timecourse Output.csv", true));

		double frameLength = 1.0/frameRate;
		double timeLine = 0;
		coll.print("Participant,Time,");
		for(int i = 0; i < maxLength; i++){
			timeLine = timeLine + frameLength;
			coll.print(String.format("%.3f", timeLine) + ", ");
		}
		coll.println();
	}

	void outputcollapsed(ArrayList<ArrayList<Double>> data) throws Exception{
		PrintWriter coll = new PrintWriter(new FileWriter("output/Detailed Timecourse Output.csv", true));
			
		for(int i = 0; i < types; i++){
			coll.print(name + ", ");
			coll.print(trialNames.get(i) + ", ");
			for(int j = 0; j < Math.min(data.get(i).size(), maxLength); j++) //300
				if (data.get(i).get(j) != -1)
					coll.print(data.get(i).get(j) + ", ");
				else
					coll.print(",");
			coll.println();
		}
		coll.println();
		coll.close();	
	}
}