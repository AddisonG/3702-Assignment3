package solution;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		
		// Create fully connected graph G
		bayonet = createFullDAG(bayonet);
		
		// Create minimum weight spanning tree
		bayonet = createMinimumSpanningTree(bayonet);
		
		// While time taken < 3 minutes
		// TODO use a timer or something here?
		int time = 0; // This is just temporary, so it doesnt loop infinitely
		while(time < 10000) {
		
			double bestScore = 0; // TODO bayonet.calculateScore();
			BayesianNetwork bestNetwork = bayonet;
		
			// Possible actions: create an edge (each pair of nodes), remove an edge from list, change direction of edge
		
			// For each possible action
			
			Map<String, Node> nodes = bayonet.getNodes();
			List<Edge> edges = bayonet.getEdges();
			
			// Add action
			
			// For each node
			for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
				
				Node node1 = nodeElement.getValue();
			
				// For each node
				for (Map.Entry<String, Node> nodeElement2 : nodes.entrySet()) {
					
					Node node2 = nodeElement2.getValue();
			
					// If nodes are not the same
					if(!node1.equals(node2)) {
			
						// Copy bayonet
						BayesianNetwork tempNetwork = new BayesianNetwork(bayonet);
				
						// Create edge between these nodes
						Edge newEdge = new Edge(node1, node2);
				
						// Add the edge to the network
						tempNetwork.addEdge(newEdge);
				
						// Calculate the score
						double score = 0;
						//TODO
				
						// If score is better
						if(score > bestScore) {
				
							// Set this as new best network
							bestScore = score;
							bestNetwork = tempNetwork;
						}
					}
				}
			}
			
			
			// Remove action
			
			// For each edge in bayonet
			for(int i = 0; i < edges.size(); i++) {
			
				// Copy the  bayonet
				BayesianNetwork tempNetwork = new BayesianNetwork(bayonet);
			
				// remove the edge from copy
				tempNetwork.removeEdge(edges.get(i));
				
				double score = 0; //TODO
			
				// Calculate if score is better
				if(score > bestScore) {
			
					// Set this as new best network
					bestScore = score;
					bestNetwork = tempNetwork;
				}
			}
			
			
			// Reverse action
			
			// For each edge in bayonet
			
			for(int i = 0; i < edges.size(); i++) {
				
				// Copy the  bayonet
				BayesianNetwork tempNetwork = new BayesianNetwork(bayonet);
			
				// remove the edge from copy
				tempNetwork.reverseEdge(edges.get(i));
				
				double score = 0; //TODO
			
				// Calculate if score is better
				if(score > bestScore) {
			
					// Set this as new best network
					bestScore = score;
					bestNetwork = tempNetwork;
				}
			}
			
		
			// If network didn't change, return
			if(bestNetwork.equals(bayonet)) {
				return bayonet;
				
			} else {
				// Set bayonet to be the best
				bayonet = bestNetwork;
			}
			time++;
		}
		
		return bayonet;
	}
	
	
	public static BayesianNetwork createFullDAG(BayesianNetwork bayonet) {
		
		// For each node
		
			// For each following node
		
				// Create a new edge between the two nodes
		
		return bayonet;
	}
	
	
	public static BayesianNetwork createMinimumSpanningTree(BayesianNetwork bayonet) {
		
		// Create empty list of nodes
		
		// Create empty list of edges
		
		// Sort list of edges by smallest weight
		
		// For each edge in ascending order
		
			// If both nodes on edge are not in list of nodes
		
				// Add edge to new list
		
				// Add each node to node list
		
		// Set edges of network to new edge list
		
	
		return bayonet;
	}
	
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}