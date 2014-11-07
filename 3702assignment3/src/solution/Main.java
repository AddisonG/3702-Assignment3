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
		String mode = args[1];
		
		// Create Bayesian Network from file
		BayesianNetwork bayonet = Reader.readFile(filepath);
		
		switch (mode) {
			case "task1":			// Create file that calculates CPT of each node
				filename = "cpt-" + filename + ".txt";
				try {
					Writer.writeCPT(filename, bayonet);
				} catch(IOException e) {
					log(ERROR, "Error Writing File!");
					e.printStackTrace();
					System.exit(1);
				}
				break;
				
			case "task2":			// Outputs the likelihood and log likelihood of given data
				printLikelihoods(bayonet);
				break;
				
			case "task4":			// Create file that has CPT data when no parents are given
				bayonet = createDAGs(bayonet);
				//TODO write new bayonet to file
				break;
				
			// add more cases, as above
			default:
				break;
		}
	}
	
	
	private static void printLikelihoods(BayesianNetwork bayonet) {
		double likelihood = bayonet.calculateMaximumLikelihood();
		double logLikelihood = bayonet.calculateLogLikelihood();
		
		System.err.println("Maximum Likelihood estimate is: " + likelihood);
		System.err.println("Maximum Log Likelihood estimate is: " + logLikelihood);
	}
	
	
	/**
	 * Creates the DAGs, appropriately setting the parents
	 * for nodes on a data set that does not give node parents.
	 * 
	 * This is used for task 4
	 * 
	 * @param bayonet - The network of nodes and data with no node relationships
	 * 
	 * @return the new network with edges between nodes
	 */
	public static BayesianNetwork createDAGs(BayesianNetwork bayonet) {
		
		// While time taken < 3 minutes
		
			// For each possible action
		
				// Perform the action
		
				// If the score is improved
		
				// Set this network as the new best
		
			// If network didn't change, return
		
		return bayonet;
	}
	
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}