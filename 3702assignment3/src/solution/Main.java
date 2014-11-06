package solution;

import java.io.IOException;

/**
 * Main class for starting and running the program.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class Main extends Global {
	public final static int MODE = DEBUG; // Current debug mode

	/**
	 * @param args - filepath mode
	 */
	public static void main(String[] args) {
		
		if (args.length != 2) { // just for now
			log(ERROR, "Usage: dataset mode");
			System.exit(1);
		}
		
		String filename = args[0];
		String filepath = "data/" + filename + ".txt";
		
		// Create Bayesian Network from file
		BayesianNetwork bayonet = Reader.readFile(filepath);
		
		try {
			Writer.writeFile(args, bayonet);
		} catch(IOException e) {
			log(ERROR, "Error Writing File!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}