import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Cruncher{
	BufferedReader br;
	String name;
	String orderfile;
	String codefile;
	boolean window;
	ArrayList<String> names;	
	ArrayList<ArrayList<Double>> combinedData;
	ArrayList<ArrayList<Integer>> counts;
	String outputFileName = "output/Timecourse Output.csv";
	String participantFileLoc = "input/Participants.txt";
	ArrayList<ArrayList<Double>> accuracies;
	

	public static void main(String[] args){
		try {
			Cruncher c = new Cruncher();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public Cruncher(){
		try {	
			boolean txtInput;
			createOutputFolders();

			System.out.println("\nIs input in (1).rtfd files or (2).txt files? Enter 1 or 2:");
			String f = System.console().readLine();
			if (f.compareTo("1") == 0)
				txtInput = false;
			else 
				txtInput = true;

			System.out.println("\nWould you like to perform window analysis? Enter Y/N:");
			String w = System.console().readLine();
			if ((w.compareTo("N") == 0) || (w.compareTo("n") == 0))
				window = false;
			else {
				window = true;
				accuracies = new ArrayList<ArrayList<Double>>();
				PrintWriter winWriter = new PrintWriter(new FileWriter("output/Detailed Window Analysis Trial Accuracy.csv", true));
				winWriter.println("Participant, Order file, Trial Number, Trial Accuracy, Trial Type, Frames to Target, Frames to Distractor,");
				winWriter.close();
				winWriter = new PrintWriter(new FileWriter("output/Detailed Window Analysis Type Accuracy.csv", true));
				winWriter.println("Participant, Order file, Trial Type, Type Accuracy,");
				winWriter.close();
				
			}
			PrintWriter winWriter = new PrintWriter(new FileWriter("output/Detailed Trial Accuracy.csv", true));
			winWriter.println("Participant, Order file, Trial Number, Trial Accuracy, Trial Type, Frames to Target, Frames to Distractor,");
			winWriter.close();
			winWriter = new PrintWriter(new FileWriter("output/Detailed Type Accuracy.csv", true));
			winWriter.println("Participant, Order file, Trial Type, Type Accuracy,");
			winWriter.close();

			parseParticipants(txtInput);
			divide();
			TimeCourse t = new TimeCourse(outputFileName, combinedData);
			if (window){
				outputTypeAcc(t.getTrialNames());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	void createOutputFolders() {
		File outputFolder = new File("output");
		if (outputFolder.exists()){
			System.out.println("Output folder already exists. Please delete or rename the folder.");
			System.exit(-1);
		} else {
			outputFolder.mkdir();
			outputFolder = new File("output/clean");
			outputFolder.mkdir();
			outputFolder = new File("output/zeroes");
			outputFolder.mkdir();
			outputFolder = new File("output/collapsed");
			outputFolder.mkdir();
		}
	}
	
	void parseParticipants(boolean txtInput) throws Exception{
		br = new BufferedReader(new FileReader(participantFileLoc));
		String line = "";
		String[] details = null;
		String num;
		names = new ArrayList<String>(); 
		boolean errorsFound = false;
		boolean convFail;
		String fname;
		boolean first = true;

		while((line = br.readLine())!= null){
			details = line.split("\\s");
			name = details[0];
			names.add(name);
			codefile = details[1];
			orderfile = details[2];
			convFail = Convert.parseInput(name, codefile, txtInput); //returns true if errors are found
			errorsFound = errorsFound || convFail; 
			
			if (txtInput)
				fname = "input/" + codefile;
			else
				fname = "output/clean/" + name + "_clean.txt";
			
			if (!errorsFound){ //only runs timecourse if no errors in any files
				TimeCourse t = new TimeCourse(name, fname, orderfile); //reads clean file and creates zeroes and collapsed files 
				combine(t.getCollapse()); //combines information from all participants collapsed files
				if (first){
					t.createDetailed();
					first = false;
				}
				t.outputcollapsed(t.getCollapse());
				if (window){
					accuracies.add(t.windowAnalyses(false));
				}
			}
		}
		
		if (errorsFound){
			System.out.println("Errors found! Check error log for details.");
			System.exit(-1);
		}
			
	}

	void combine(ArrayList<ArrayList<Double>> data){
		if (combinedData == null){
			combinedData = new ArrayList<ArrayList<Double>>();
			counts = new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i < data.size(); i++)
				combinedData.add(data.get(i));
			for (int i = 0; i < data.size(); i++){
				counts.add(new ArrayList<Integer>());
				for(int j = 0; j < data.get(i).size(); j++)
					if (data.get(i).get(j) != -1)
						counts.get(i).add(1);
					else
						counts.get(i).add(0);
			}
						
		} else {
			for(int i = 0; i < data.size(); i++)
				for(int j = 0; j < data.get(i).size(); j++){
					if (j >= combinedData.get(i).size()){
						combinedData.get(i).add(data.get(i).get(j));
						if (data.get(i).get(j) != -1)
							counts.get(i).add(1);
						else
							counts.get(i).add(0);
					}
					else if (data.get(i).get(j) != -1 && combinedData.get(i).get(j) != -1){
						double d = combinedData.get(i).get(j);
						int c = counts.get(i).get(j);
						combinedData.get(i).set(j, d + data.get(i).get(j));
						counts.get(i).set(j, c + 1);
					} else if (data.get(i).get(j) == -1){
						double d = combinedData.get(i).get(j);
						double m = Math.max(d, data.get(i).get(j));
						combinedData.get(i).set(j, m);
					} else {
						combinedData.get(i).set(j, data.get(i).get(j));
						counts.get(i).set(j, 1);
					}
				}
		}
	}

	void divide(){
		int size = names.size();
		for(int i = 0; i < combinedData.size(); i++)
			for(int j = 0; j < combinedData.get(i).size(); j++){
				double d = combinedData.get(i).get(j);
				int c = counts.get(i).get(j);
				if (d >= 0) {
					double res = d/((double) c);
					combinedData.get(i).set(j, res);
				}
			}			
	}

	void outputTypeAcc(ArrayList<String> trialNames){
		int types = accuracies.get(0).size();
		int numParticipants = names.size();


		for(int i = 0; i < types; i++) {
			double sum = 0.0;
			for(int j = 0; j < numParticipants; j++)
				sum = sum + accuracies.get(j).get(i);
			
			double avgAcc = (sum/(double) numParticipants);
			System.out.println(trialNames.get(i) + " = " + avgAcc);
		}
	}
	
}