package solution;

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
		
		String dataset = args[0];
		String filePath = "data/" + dataset + ".txt";
		String mode = args[1];
		
		// Create Bayesian Network from file
		BayesianNetwork bayonet = Reader.readFile(filePath);
		
		
		switch(mode) {
		case "task1":
			String fileName = "cpt-" + dataset + ".txt";
			Writer.writeFile(fileName , bayonet);
			break;
		}

		
	}
}