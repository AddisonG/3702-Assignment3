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
				
				bayonet = createDAG(bayonet, mode);
				filename = "bn-" + filename + ".txt";
				try {
					Writer.writeDAG(filename, bayonet);
				} catch(IOException e) {
					log(ERROR, "Error Writing File!");
					e.printStackTrace();
					System.exit(1);
				}
				break;
			case "task7":			// Create file that has CPT data when no parents are given
				
				// Create Bayesian Network from file
				bayonet = Reader.readFileNoParents(filepath);
				
				bayonet = createDAG(bayonet, mode);
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
	 * @param bayonet - The network of nodes and data with no node relationships
	 * 
	 * @return the new network with edges between nodes
	 */
	public static BayesianNetwork createDAG(BayesianNetwork bayonet, String mode) {
		
		log(INFO, mode);
		
		// create no edge (Note this is initialization for task6 as well)
		bayonet = createNoEdgeDAG(bayonet);
		
		// If task 7, create best tree network
		if(mode.equals("task7")) {
			log(INFO, "Creating full DAG");
			bayonet = createFullDAG(bayonet);
			log(INFO, "Creating spanning tree");
			bayonet = createMinimumSpanningTree(bayonet);
		}
		
		// While time taken < 2 mins, 55 seconds
		long startTime = System.currentTimeMillis();
		double bestScore = -2500; // Really low number
		while((System.currentTimeMillis() - startTime) < 1000 * 175) {
			
			log(INFO, "\n\n");
			
			
			
		
			boolean changed = false;
			BayesianNetwork bestNetwork = new BayesianNetwork(bayonet);
		
			// Possible actions: create an edge (each pair of nodes), remove an edge from list, change direction of edge
		
			// For each possible action
			
			Map<String, Node> nodes = bayonet.getNodes();
			List<Edge> edges = bayonet.getEdges();
			
			for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
				
				Node node = nodeElement.getValue();
				
				String debugStr = node.getName() + ": ";
				for(Node ancestor : node.getAncestors()) {
					debugStr += ancestor.toString();
				}
				log(INFO, debugStr + "\n");
			}
			
			
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
				
				// Get equivalent edge in new network
				Edge newEdge = tempNetwork.getEquivalentEdge(edges.get(i));
			
				// remove the edge from copy
				tempNetwork.removeEdge(newEdge);
				
				log(INFO, "Tried removing edge " + newEdge.getParent() + ", " + newEdge.getChild());
				
				// Check DAG is still valid
				if(tempNetwork.checkValidDAG()) {
				
					double score = tempNetwork.calculateScore();
					
					log(INFO, "Old score is " + bestScore + ", new score is " + score);
				
					// Calculate if score is better
					if(score > bestScore) {
						
						log(INFO, "New best score");
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
				
				// Get equivalent edge in new network
				Edge newEdge = tempNetwork.getEquivalentEdge(edges.get(i));
				
				log(INFO, "Tried reversing edge " + newEdge.getParent() + ", " + newEdge.getChild());
			
				// reverse the edge from copy
				tempNetwork.reverseEdge(newEdge);
				
				// Check DAG is valid
				if(tempNetwork.checkValidDAG()) {
				
					double score = tempNetwork.calculateScore();
					
					log(INFO, "Old score is " + bestScore + ", new score is " + score);
				
					// Calculate if score is better
					if(score > bestScore) {
				
						log(INFO, "New best score, edge is now reverse");
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
	
	
	/**
	 * Creates a complete graph with an edge from every node to every node
	 * 
	 * @param bayonet
	 * @return
	 */
	public static BayesianNetwork createFullDAG(BayesianNetwork bayonet) {
		
		log(INFO, "Creating DAG");
		
		// For each node
		Map<String, Node> nodes = bayonet.getNodes();
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node1 = nodeElement.getValue();
			
			log(INFO, "Node 1 is " + node1);
		
			// For each other node
			for (Map.Entry<String, Node> nodeElement2 : nodes.entrySet()) {
				
				Node node2 = nodeElement2.getValue();
				
				log(INFO, "Node 2 is " + node2);
				
				if(!node1.equals(node2)) {
		
					// Create a new edge between the two nodes
					Edge newEdge = new Edge(node1, node2);
					bayonet.addEdge(newEdge);
					log(INFO, "Added edge");
				}
			}
		}
		
		return bayonet;
	}
	
	
	public static BayesianNetwork createNoEdgeDAG(BayesianNetwork bayonet) {
		// For each node
		Map<String, Node> nodes = bayonet.getNodes();
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node = nodeElement.getValue();
			
			// Remove all parents from node
			node.removeAllParents();
			
			// empty list of edges
			bayonet.removeEdges();
		}
		
		// return the network
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
	
	
	public static BayesianNetwork createMinimumSpanningTree(final BayesianNetwork bayonet) {
		
		// Create empty Map of nodes
		Map<String, Node> nodes = new HashMap<String, Node>();
		
		// Create empty list of edges
		ArrayList<Edge> newEdges = new ArrayList<Edge>();
		List<Edge> edges = bayonet.getEdges();
		
		log(INFO, edges.size() + "");
		
		// Sort list of edges by smallest weight
		Collections.sort(edges, new Comparator<Edge>() {
			@Override
			public int compare(Edge edge1, Edge edge2) {
				Double weight1 = bayonet.getWeight(edge1.getParent(), edge1.getChild());
				Double weight2 = bayonet.getWeight(edge1.getParent(), edge1.getChild());
				return (weight1.compareTo(weight2));
			}
		});
		
		log(DEBUG, "sorted weights is " + edges);
		
		// For each edge in ascending order
		for(Edge edge : edges) {
		
			// If both nodes on edge are not in list of nodes
			if(nodes.isEmpty() || !(nodes.containsValue(edge.getParent()) && nodes.containsValue(edge.getChild()))) {
		
				
				log(DEBUG, "Adding edge " + edge);
				// Add edge to new list
				newEdges.add(edge);
				
				
				
		
				// Add each node to node list
				if(!nodes.containsValue(edge.getParent())) {
					log(DEBUG, "Adding Node " + edge.getParent());
					nodes.put(edge.getParent().getName(), edge.getParent());
				}
				
				if(!nodes.containsValue(edge.getChild())) {
					log(DEBUG, "Adding node " + edge.getChild());
					nodes.put(edge.getChild().getName(), edge.getChild());
				}
			}
		}
		
		// Set edges of network to new edge list
		bayonet.removeEdges();
		bayonet.setEdges(newEdges);
	
		return bayonet;
	}
	
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}