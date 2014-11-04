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
		
		if (args.length == 99) { // just for now
			log(ERROR, "Usage: [filepath]... and mode (????)."); // TODO
			System.exit(1);
		}
		
		String filePath = "data/CPTNoMissingData-d1.txt"; // arg0;
		
		// Never change this name
		// Clever name, but the "o" in the middle ruins the perfection
		BayesianNetwork bayonet = Reader.readFile(filePath);
		
		// Get the CPT of A for some reason
		bayonet.calculateCPT(bayonet.getNodes().get("A"));
		
		// TODO take args as text file I think
		
		// TODO create Bayesian Network from file
		
		// TODO compute CPT of each node in network
		
		// TODO create output files of CPT
		
	}
}