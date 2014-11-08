package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Bayesian Network with nodes.
 * Serves as a controller for node calculations.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class BayesianNetwork extends Global {
	public final static int MODE = INFO; // Current debug mode
	
	/** The list of nodes in this network **/
	private Map<String, Node> nodes;
	
	/** The data table of this network. Outer list is each row, 
	 * inner list is each node **/
	private List<List<Boolean>> data;
	
	/** List of edges between nodes in the network. */
	private List<Edge> edges;
	
	
	public BayesianNetwork(Map<String, Node> nodes, List<List<Boolean>> data2) {
		this.nodes = nodes;
		this.data = data2;
		edges = new ArrayList<Edge>();
	}
	
	
	/**
	 * Create a new Bayesian network as a deep copy of the
	 * given network
	 * 
	 * @param bayonet
	 */
	public BayesianNetwork(BayesianNetwork bayonet) {
		
		// Initialize lists
		this.nodes = new HashMap<String, Node>(bayonet.getNodes().size());
		this.data = new ArrayList<List<Boolean>>();
		edges = new ArrayList<Edge>();
		
		Map<String, Node> newNodes = bayonet.getNodes();
		
		// Copy all of the nodes over
		for (Map.Entry<String, Node> nodeElement : newNodes.entrySet()) {
			String name = nodeElement.getKey();
			Node oldNode = nodeElement.getValue();
			
			Node newNode = new Node(name.toString());
			
			this.nodes.put(name, newNode);
		}
		
		// Copy the data over (for now we never modify data, so this is fine)
		this.data = bayonet.data;
		
		// Copy the edges over
		List<Edge> oldEdges = bayonet.getEdges();
		for(Edge edge : oldEdges) {
			
			// Get equivalent nodes from new node list
			Node newNode1 = nodes.get(edge.getParent().getName());
			Node newNode2 = nodes.get(edge.getChild().getName());
			
			// Create the edge between these nodes
			Edge newEdge = new Edge(newNode1, newNode2);
			
			// Add new edge to list of edges
			edges.add(newEdge);
		}
		
	}
	
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	
	/**
	 * @return All of the nodes within this Bayesian Network
	 */
	public Map<String, Node> getNodes() {
		return nodes;
	}
	
	
	/**
	 * Returns the amount of rows where everything in true list is true
	 * and everything in false list is false
	 * 
	 * @return Amount of rows where lists are valid
	 */
	public int countBooleanData(List<Node> trueList, List<Node> falseList) {
		int count = 0; // Number of valid rows
		
		log(DEBUG, " ");
		log(DEBUG, "Calculating boolean data");
		log(DEBUG, "Truelist: " + trueList.toString());
		log(DEBUG, "Falselist " + falseList.toString());
		
		if (data.size() == 0) {
			log(ERROR, "ERROR: No data found");
			System.exit(1);
		}
		
		// For each row in the data
		for (List<Boolean> row : data) {
			boolean valid = true;
			
			// If any nodes value in this row not true, discount the row
			for (Node n : trueList) {
				if (row.get(n.getIndex()) == false) {
					valid = false;
					break;
				}
			}
			
			// If any nodes value in this row not false, discount the row
			for (Node n : falseList) {
				if (row.get(n.getIndex()) == true) {
					valid = false;
					break;
				}
			}
			
			// If above loops were good, add one to count
			if (valid) {
				count++;
			}
		}
		
		log(DEBUG, "Count is " + count);
		log(DEBUG, " ");
		return count;
	}
	
	
	/**
	 * Returns the amount of rows where a node and its parents are all true.
	 * This is known as the node's conditional probability table (CPT).
	 * 
	 * Uses Maximum Likelihood Estimate
	 * 
	 * @param node - Node to calculate CPT for
	 * @return
	 */
	/*
	public double calculateCPT(Node node) {
		
		int trueCount;
		List<Node> trueList = new ArrayList<Node>();
		trueList.add(node);
		
		// Get total amount of data rows
		int dataLength = data.size();
		
		log(INFO, "Calculating CPT of " + (node.toString()));
		
		// If parents, get amount of data where this node == true AND parents == true
		if (node.hasParents()) {
			
			// Only count data rows that are true for node
			dataLength = countBooleanData(trueList, null); // Since at this point only the node is in truelist
			
			// Add the node's parents to our list to check
			trueList.addAll(node.getParents());
			
			// Get the amount of rows where the above list is all true
			trueCount = countBooleanData(trueList, null);
			
		} else {
			// If no parents, count is just where node is true
			trueCount = countBooleanData(trueList, null);
		}
			
		// Ensure no divide by 0 occurs using Bayesian Correction
		if (dataLength == 0) {
			dataLength++;
			trueCount++;
		}
		
		log(INFO, "Probability is " + (trueCount / dataLength));
		
		// Return probability
		return trueCount / dataLength;
	}
	*/
	
	/**
	 * Create a list of probabilities for
	 * all possible truth combinations of parents.
	 * 
	 * Note: List size will always be 2^n, where n is the number of parents.
	 * 
	 * I've found a solution using recursion, which seems efficient enough.
	 * See recursive method below
	 * 
	 * @param node - The node to get all probabilities for
	 * @return - All probabilities for that node
	 */
	public List<Double> getAllProbabilities(Node node) {
		
		// Get parents of node
		ArrayList<Node> parents = (ArrayList<Node>) node.getParents();
		ArrayList<Node> trueList = new ArrayList<Node>();
		ArrayList<Node> falseList = new ArrayList<Node>();
		trueList.add(node);
		
		log(INFO, "About to create probability list for " + trueList);
		
		// Get list of every possible true/false combination of all parents
		List<Double> probabilities = getRecursiveProbabilities(trueList, falseList, parents);
		
		log(INFO, "Finished creating probability list: " + probabilities.toString());
		
		return probabilities;
	}
	
	
	private double getProbability(ArrayList<Node> trueList, List<Node> falseList) {
		
		@SuppressWarnings("unchecked")
		ArrayList<Node> temp = (ArrayList<Node>) trueList.clone();
		temp.remove(0);
		
		// Calculate the amount of data for these lists
		double count = countBooleanData(trueList, falseList);
		
		// Calculate the amount of data for these lists without original node
		double dataCount = countBooleanData(temp, falseList);
		
		// Avoid divide by zero
		if (dataCount == 0) {
			count++;
			dataCount += 2;
		}
		
		double probability = count/dataCount;
		
		log(DEBUG, "Probability is " + probability);
		return probability;
	}
	
	
	/**
	 * TODO probabilities should only be calculated once per node
	 * 
	 * RECURSION WARNING
	 * 
	 * Recursively creates a sorted list of every possible true/false
	 * combination with the given list.
	 * 
	 * e.g. The first time this is called, truelist should have one node
	 * and remainingNodes will be that nodes parents. We add the first parent
	 * to a false list and call this method again, adding parents to false until
	 * we get to the base case. The base case is when there are no remaining
	 * nodes, so everything will be checked as true or false.
	 * 
	 * We return the probability of the base case (FF at first) in a list, and
	 * then run recursively again but with the last node being true (FT). We add
	 * this value to the list (Now [FF, FT]) and return it back up the recursion
	 * chain.
	 * 
	 * Eventually this gives us a list of all probabilities for each of the nodes
	 * originally in the remainingNodes list. e.g. for a node with three parents
	 * the list will be probabilities of [FFF, FFT, FTF, FTT, TFF, TFT, TTF, TTT].
	 * 
	 * If this is still hard to understand, ask me, but I think my logic is ok.
	 * 
	 * @param trueList	- List of nodes that must be true under all lower recurses
	 * @param falseList	- List of nodes that are false under all lower recurses
	 * @param remainingNodes - List of nodes to get all possible combinations of.
	 * 
	 * @return An ordered list of all probabilities for remainingNodes
	 */
	@SuppressWarnings("unchecked")
	private List<Double> getRecursiveProbabilities(ArrayList<Node> trueList, ArrayList<Node> falseList, ArrayList<Node> remainingNodes) {
		log(DEBUG, "True list: " + trueList);
		log(DEBUG, "False list: " + falseList);
		log(DEBUG, "Remainder list: " + remainingNodes);
		
		List<Double> probabilities = new ArrayList<Double>();
		
		// If no remaining nodes, return probability of these lists
		if (remainingNodes.size() == 0) {
			
			log(DEBUG, "At base case");
			
			double probability = getProbability(trueList, falseList);
			
			
			// Return the probability
			probabilities.add(probability);
			return probabilities;
		}
		
		Node newNode = remainingNodes.remove(0);
		
		// Get all probabilities when first remaining node is false
		falseList.add(newNode);
		
		// Create a copy of the arraylist as lower levels will modify given list
		ArrayList<Node> trueCopy = (ArrayList<Node>) trueList.clone();
		ArrayList<Node> falseCopy = (ArrayList<Node>) falseList.clone();
		ArrayList<Node> remainderCopy = (ArrayList<Node>) remainingNodes.clone();
		
		// Add all probabilities from recursion
		probabilities.addAll(getRecursiveProbabilities(trueCopy, falseCopy, remainderCopy));
		
		// Get all probabilities when first remaining node is true instead
		falseList.remove(newNode);
		trueList.add(newNode);
		
		// Create a copy of the arraylist as lower levels will modify given list
		trueCopy = (ArrayList<Node>) trueList.clone();
		falseCopy = (ArrayList<Node>) falseList.clone();
		remainderCopy = (ArrayList<Node>) remainingNodes.clone();

		probabilities.addAll(getRecursiveProbabilities(trueCopy, falseCopy, remainderCopy));
		
		return probabilities;
	}
	
	
	public double calculateMaximumLikelihood() {
		
		double likelihood = 1; // Starting at one because we multiply
		
		log(DEBUG, "Data size is: " + data.size());
		
		// For every row in the data
		for (int i = 0; i < data.size(); i++) {
			
			List<Boolean> row = data.get(i);
			log(DEBUG, "Checking row: " + row);
			double rowProbability = 1; // Starting at one because we multiply
		
			// For every node in the row
			for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
				
				// Get the probability of that node...
				Node node = nodeElement.getValue();
				
				log(DEBUG, " ");
				log(DEBUG, "Node checked is: " + node.toString());
		
				// Get probability where this node is [value of column in row],
				// including where each parent = [value of their column in row]
				
				ArrayList<Node> trueList = new ArrayList<Node>();
				ArrayList<Node> falseList = new ArrayList<Node>();
				trueList.add(node);
				
				
				// For every node in the row
				for (Map.Entry<String, Node> parentNodeElement : nodes.entrySet()) {
					
					// Get the probability of that node...
					Node parentNode = parentNodeElement.getValue();
					
					// Can't be your own parent
					if (parentNode.equals(node)) {
						continue;
					}
					
					// If this node is the parent of our node
					List<Node> parents = node.getParents();
					if(parents.contains(parentNode)) {
						
						log(DEBUG, "Found parent: " + parentNode.toString() + " for node: " + node.toString());
						int parentIndex = parentNode.getIndex();
				
						// If true add to true list
						if(row.get(parentIndex) == true) {
							trueList.add(parentNode);
						} else {
							// If false add to false list
							falseList.add(parentNode);
						}
					}
				}
				
				log(DEBUG, "True list: " + trueList);
				log(DEBUG, "False list: " + falseList);
				
				
				// Get probability for this node
				double nodeProbability = getProbability(trueList, falseList);
				
				// If this node is false, take opposite of probability
				int nodeIndex = node.getIndex();
				if(row.get(nodeIndex) == false) {
					nodeProbability = 1 - nodeProbability;
				}
				
				log(DEBUG, "Adjusted probability is: " + nodeProbability);
		
				// Multiply this probability by rest in row
				rowProbability *= nodeProbability;
			}
			log(DEBUG, "Row probability is: " + rowProbability);
			// Multiply this probability by other rows
			likelihood *= rowProbability;
		}
		
		log(DEBUG, "Total probability is: " + likelihood);
		
		// Return the total value
		return likelihood;
	}
	
	

	public double calculateLogLikelihood() {
		return Math.log(calculateMaximumLikelihood());
	}
	
	public double calculateScore() {
		int c = 1; // constant value
		return calculateLogLikelihood() - (c * data.size());
	}
	
	
	public boolean addEdge(Edge edge) {
		// Check edge isn't already in list of edges
		for(Edge existingEdge : edges) {
			
			if((edge.getChild().equals(existingEdge.getChild()) && edge.getParent().equals(existingEdge.getParent())) ||
					(edge.getChild().equals(existingEdge.getParent()) && edge.getParent().equals(existingEdge.getChild()))) {
				// Edge already exists!
				log(INFO, "Tried adding invalid edge");
				return false;
			}
			
		}
		
		
		if(!edges.contains(edge)) {
		
			// Add edge to list of edges
			edges.add(edge);
			
			// Add parent to node
			Node child = edge.getChild();
			Node parent = edge.getParent();
			child.addParent(parent);
			
		}
		return true;
	}
	
	public void removeEdge(Edge edge) {
		// Check edge is already in list of edges
		if(edges.contains(edge)) {
		
			// Remove edge from list
			edges.remove(edge);
			
			// Remove parent from node
			Node child = edge.getChild();
			Node parent = edge.getParent();
			child.removeParent(parent);
		}
	}
	
	
	public void setEdges(ArrayList<Edge> newEdges) {
		
		// Remove all existing parents
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			Node node = nodeElement.getValue();
			
			// Remove parents from node
			node.removeAllParents();
		}
		
		// For every edge in newEdges
		for(Edge edge : newEdges) {
			
			// Add the edge to the graph
			addEdge(edge);
		}
	}
	
	
	public void reverseEdge(Edge edge) {
		// Check edge is in list of edges
		if(edges.contains(edge)) {
		
			// Remove the edge
			removeEdge(edge);
			
			// Create new edge with order swapped
			Edge newEdge = new Edge(edge.getChild(), edge.getParent());
			
			// Add the edge with nodes swapped
			addEdge(newEdge);
		}
	}
	
	
	/**
	 * Checks that the DAG is valid for this network
	 * 
	 * TODO I think this should also make sure all nodes are connected
	 * in one graph
	 * 
	 * @return
	 */
	public boolean checkValidDAG() {
		
		// For each node in DAG
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {	
			
			Node node = nodeElement.getValue();
			List<Node> parents = node.getParents();
		
			// For each parent of node
			for(Node parent : parents) {
		
				// Recursively check DAG is valid
				boolean result = checkValidRecursively(node, parent);
				
				if(result == false) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * Recursively check a node by traveling through all combinations
	 * of its parents until we either have no parents left (valid) or
	 * we end up at the node we started from (invalid)
	 * 
	 * Uses depth-first search
	 * 
	 * @param checkNode - Node we are checking validity of
	 * @param currentNode - Current ancestor of node
	 * 
	 * @return true if node is not an ancestor of itself
	 * @return false if node checked was found
	 */
	public boolean checkValidRecursively(Node checkNode, Node currentNode) {
		
		// If currentNode = Node return false
		if(checkNode.equals(currentNode)) {
			return false;
		}
		
		// Else if currentNode has no parents return true
		if(currentNode.hasParents() == false) {
			return true;
		}
		
		// Else for every parent of current node
		List<Node> parents = currentNode.getParents();
		for(Node parent : parents) {
			// Recurse
			boolean result = checkValidRecursively(checkNode, parent);
			
			// If value returned was false return false
			if(result == false) {
				return false;
			}
		}
		
		// If we make it here then every parent was checked
		return true;
	}
	
	
	
	public boolean equals(BayesianNetwork other) {
		if(other.getNodes() != nodes) {
			return false;
		}
		
		if(data != other.getData()) {
			return false;
		}
		
		if(edges != other.getEdges()) {
			return false;
		}
		
		return true;
	}
	
	
	
	public List<List<Boolean>> getData() {
		return data;
	}


	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}