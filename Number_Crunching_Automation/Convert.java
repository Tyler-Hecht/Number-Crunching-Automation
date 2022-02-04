import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Convert{
	String filename; 
	static BufferedReader br;

	public static void main(String args[]){
		try{
			Convert m = new Convert(args);
		}

		catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	} 

	public Convert(String [] args)throws Exception{
		parseInput(args[0], args[1], Boolean.parseBoolean(args[2]));
	}
	

	public static boolean parseInput(String name, String fileLoc, boolean txtFile) throws Exception{
		br = new BufferedReader(new FileReader("input/"+ fileLoc));
		String line = "";
		PrintStream console = System.out;
		PrintStream fstream = System.out;
		if (!txtFile){
			fstream = new PrintStream("output/clean/" + name + "_clean.txt");
			System.setOut(fstream);
		}
		PrintWriter errors = new PrintWriter(new FileWriter("Error Log.txt", true));
		 
		
		boolean errorsFound = false;
		int lineCount = 0; 
		int trialCount = 0;
		boolean encounteredB = false;


		while((line = br.readLine()) != null){
			if (line.length() > 0) {
				String tabSplitby = "\\W|[^0-9BSLRCT]+"; //we only care about digits and letters BSLRC
				String[] res = line.split(tabSplitby);
				ArrayList<String> result = resConvert(res);
				if (result.size() > 0){
					String firstEl = result.get(0);
					char firstC = firstEl.charAt(0);
					if (firstC == 'B' || firstC == 'S' || firstC == 'L' || firstC == 'R' || firstC == 'C'){
						lineCount++;
					
						if (firstC == 'B' || firstC == 'S'){
							trialCount++;
							if (result.size() != 2) {
								if (result.size() < 2){	//|| isNumeric(result.get(2))
									errors.println("Error! File " + fileLoc + ", Line " + lineCount);
									errors.println(line);
									errors.println("Check " + name + "'s input file for error in formatting.");
									errorsFound = true;
									errors.close();
								}
							} else if (firstC == 'B' && !encounteredB){
								encounteredB = true;
							}
						} else if (firstC == 'L' || firstC == 'R' || firstC == 'C'){
							if (result.size() != 3 && encounteredB) {	
								errors.println("Error! File " + fileLoc + ", Line " + lineCount);
								errors.println(line);
								errors.println("Check " + name + "'s input file for error in formatting.");
								errorsFound = true;
								errors.close();
							}
						}
						if (!errorsFound && !txtFile && encounteredB){
							System.setOut(fstream);
							if (firstC == 'B' || firstC == 'S'){
								System.out.print(result.get(0) + " " + result.get(1));
							} else if (firstC == 'L' || firstC == 'R' || firstC == 'C'){
								System.out.print(result.get(0) + " " + result.get(1) + " " + result.get(2));
							}	
							System.out.println();
						}

						
					}			
				}
			}
		}
		System.setOut(console);	
		System.out.println(name + "'s input file has " + trialCount/2 + " trials.");

		return errorsFound;
	}



	//count res size
	public static int resSize(String[] res){
		int count = 0;
		for(int i = 0; i < res.length; i++)
			if (res[i].compareTo("") != 0)
				count++;

		return count;
	}

	public static ArrayList<String> resConvert(String[] res){
		ArrayList<String> result = new ArrayList<String>();
		boolean flag = false; //becomes true once we encounter BSLRC
		for(int i = 0; i < res.length; i++)
			if (res[i].compareTo("") != 0){
				if (!flag) {
					if (res[i].compareTo("B") == 0 || res[i].compareTo("S") == 0 || res[i].compareTo("L") == 0 || res[i].compareTo("R") == 0 || res[i].compareTo("C") == 0)
						flag = true;
					if (res[i].compareTo("LT") == 0 || res[i].compareTo("RT") == 0 || res[i].compareTo("RB") == 0 || res[i].compareTo("LB") == 0)
						flag = true;
				}

				if (flag)
					result.add(res[i]);
			}
		
		return result;
	}

	public static boolean isNumeric(String n){
		if (n ==null)
			return false;

		try{
			int num = Integer.parseInt(n);
		} catch (NumberFormatException nfe){
			return false;
		}
		return true;
	
	}
}