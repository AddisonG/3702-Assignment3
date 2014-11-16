package solution;

import java.io.IOException;
import java.util.*;

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
		
		BayesianNetwork bayonet;
		
		switch (mode) {
			case "task1":			// Create file that calculates CPT of each node
				
				// Create Bayesian Network from file
				bayonet = Reader.readFile(filepath);
				
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
				
				// Create Bayesian Network from file
				bayonet = Reader.readFile(filepath);
				
				printLikelihoods(bayonet);
				break;
				
			case "task4":			// Create file that has CPT data when no parents are given
				
				// Create Bayesian Network from file
				bayonet = Reader.readFileNoParents(filepath);
				
				bayonet = createDAG(bayonet);
				filename = "bn-" + filename + ".txt";
				try {
					Writer.writeDAG(filename, bayonet);
				} catch(IOException e) {
					log(ERROR, "Error Writing File!");
					e.printStackTrace();
					System.exit(1);
				}
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
	 * Creates the DAG, appropriately setting the parents
	 * for nodes on a data set that does not give node parents.
	 * 
	 * This is used for task 4
	 * 
	 * @param bayonet - The network of nodes and data with no node relationships
	 * 
	 * @return the new network with edges between nodes
	 */
	public static BayesianNetwork createDAG(BayesianNetwork bayonet) {
		
		// Create fully connected graph G
		//bayonet = createFullDAG(bayonet);
		
		// Create minimum weight spanning tree
		//bayonet = createMinimumSpanningTree(bayonet);
		
		// Create naive DAG while spanning doesnt work
		bayonet = createNaiveDAG(bayonet);
		
		// While time taken < 2 mins, 55 seconds
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) < 1000 * 17.5) {
		
			boolean changed = false;
			BayesianNetwork bestNetwork = new BayesianNetwork(bayonet);
			double bestScore = bayonet.calculateScore();
		
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
						
						// Here's the problem. Its creating a new edge with the nodes from the previous network
						
						// Get the equivalent nodes from the new network so not updating the same nodes
						Node newNode1 = tempNetwork.getNodeByName(node1.getName());
						Node newNode2 = tempNetwork.getNodeByName(node2.getName());
				
						// Create edge between these nodes
						Edge newEdge = new Edge(newNode1, newNode2);
				
						// Add the edge to the network
						if(tempNetwork.addEdge(newEdge)) {
						
							log(INFO, "Tried adding new edge " + newNode1 + ", " + newNode2);
							
							// Check DAG is still valid
							if(tempNetwork.checkValidDAG()) {
					
								// Calculate the score
								double score = tempNetwork.calculateScore();
								
								log(INFO, "Old score is " + bestScore + ", new score is " + score);
						
								// If score is better
								if(score > bestScore) {
									
									log(INFO, "New best score! Added edge " + node1 + ", " + node2);
						
									// Set this as new best network
									bestScore = score;
									bestNetwork = new BayesianNetwork(tempNetwork);
									changed = true;
								}
							} else {
								log(INFO, "New edge was invalid");
							}
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
				
				// Check DAG is still valid
				if(tempNetwork.checkValidDAG()) {
				
					double score = tempNetwork.calculateScore();
				
					// Calculate if score is better
					if(score > bestScore) {
				
						// Set this as new best network
						bestScore = score;
						bestNetwork = new BayesianNetwork(tempNetwork);
						changed = true;
					}
				}
			}
			
			
			// Reverse action
			
			// For each edge in bayonet
			
			for(int i = 0; i < edges.size(); i++) {
				
				// Copy the  bayonet
				BayesianNetwork tempNetwork = new BayesianNetwork(bayonet);
			
				// reverse the edge from copy
				tempNetwork.reverseEdge(edges.get(i));
				
				// Check DAG is valid
				if(tempNetwork.checkValidDAG()) {
				
					double score = tempNetwork.calculateScore();
				
					// Calculate if score is better
					if(score > bestScore) {
				
						// Set this as new best network
						bestScore = score;
						bestNetwork = new BayesianNetwork(tempNetwork);
						changed = true;
					}
				}
			}
			
		
			// If network didn't change, return
			if(!changed) {
				log(INFO, "Optimal network found");
				return bayonet;
				
			} else {
				// Set bayonet to be the best
				bayonet = new BayesianNetwork(bestNetwork);
				log(DEBUG, "New best network!");
			}
		}
		
		return bayonet;
	}
	
	
	public static BayesianNetwork createFullDAG(BayesianNetwork bayonet) {
		
		// For each node
		Map<String, Node> nodes = bayonet.getNodes();
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node1 = nodeElement.getValue();
		
			// For each following node
			for (Map.Entry<String, Node> nodeElement2 : nodes.entrySet()) {
				
				Node node2 = nodeElement2.getValue();
		
				// Create a new edge between the two nodes
				Edge newEdge = new Edge(node1, node2);
				bayonet.addEdge(newEdge);
			}
		}
		
		return bayonet;
	}
	
	
	public static BayesianNetwork createNaiveDAG(BayesianNetwork bayonet) {
		
		// Get first node in network
		Map<String, Node> nodes = bayonet.getNodes();
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node1 = nodeElement.getValue();
		
			// Add all other nodes as parents
			for (Map.Entry<String, Node> nodeElement2 : nodes.entrySet()) {
				Node node2 = nodeElement2.getValue();
				if (!node1.equals(node2)) {
					log(DEBUG, node1 + " added child " + node2);
					bayonet.addEdge(new Edge(node1, node2));
				}
			}
			return bayonet;
		}
		return bayonet;
	}
	
	
	@SuppressWarnings("null")
	public static BayesianNetwork createMinimumSpanningTree(BayesianNetwork bayonet) {
		
		// Create empty Map of nodes
		Map<String, Node> nodes = null;
		
		// Create empty list of edges
		ArrayList<Edge> newEdges = new ArrayList<Edge>();
		List<Edge> edges = bayonet.getEdges();
		
		// Sort list of edges by smallest weight
		Collections.sort(edges, new Comparator<Edge>() {
			@Override
			public int compare(Edge edge1, Edge edge2) {
				return edge1.getWeight().compareTo(edge2.getWeight());
			}
		});
		
		log(DEBUG, "sorted weights is " + edges);
		
		// For each edge in ascending order
		for(Edge edge : edges) {
		
			// If both nodes on edge are not in list of nodes
			if(!nodes.containsValue(edge.getParent()) && !nodes.containsValue(edge.getChild())) {
		
				// Add edge to new list
				newEdges.add(edge);
		
				// Add each node to node list
				if(!nodes.containsValue(edge.getParent())) {
					nodes.put(edge.getParent().getName(), edge.getParent());
				}
				
				if(!nodes.containsValue(edge.getChild())) {
					nodes.put(edge.getChild().getName(), edge.getChild());
				}
			}
		}
		
		// Set edges of network to new edge list
		bayonet.setEdges(newEdges);
	
		return bayonet;
	}
	
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}