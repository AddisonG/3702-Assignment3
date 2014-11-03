package solution;

/**
 * Main class for starting and running the program.
 * 
 * @author Cameron Darragh<br>Addison Gourluck
 */
public class Main extends Global {
	public final static int MODE = DEBUG; // Current debug mode

	/**
	 * @param args - filepath mode
	 */
	public static void main(String[] args) {
		
		if (args.length == 99) { // just for now
			log(ERROR, "Only give two arguments, a filepath and mode (????)."); // TODO
			System.exit(1);
		}
		
		// Never change this name
		BayesianNetwork bayonet = Reader.readFile("data/CPTNoMissingData-d1.txt");
		
		bayonet.calculateCPT(bayonet.getNodes().get("A"));
		
		// TODO take args as text file I think
		
		// TODO create Bayesian Network from file
		
		// TODO compute CPT of each node in network
		
		// TODO create output files of CPT
		
	}
}