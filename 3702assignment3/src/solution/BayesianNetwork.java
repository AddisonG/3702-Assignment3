/**
 * 
 */
package solution;

import java.util.ArrayList;

/**
 * @author Cameron Darragh, Addison Gourluck
 * 
 * Represents the Bayesian Network with nodes.
 * Serves as a controller for node calculations
 *
 */
public class BayesianNetwork {
	
	/** The list of nodes in this network **/
	private ArrayList<Node> nodes;
	
	/** The data table of this network. Outer Arraylist is each row, 
	 * inner Arraylist is each node **/
	private ArrayList<ArrayList<Boolean>> data;
	
	
	
	
	public BayesianNetwork(ArrayList<Node> nodes, ArrayList<ArrayList<Boolean>> data) {
		this.data = data;
		this.nodes = nodes;
	}
	
	
	/**
	 * Adds a new node to the network
	 * 
	 * @param newNode - node to be added
	 */
	public void addNode(Node newNode) {
		nodes.add(newNode);
	}
	
	
	public void setData(ArrayList<ArrayList<Boolean>> newData) {
		this.data = newData;
	}
	
	
	
	/**
	 * Returns the count where the logical parameters
	 * are valid. 
	 * 
	 * Any nodes not listed are not checked.
	 * 
	 * @param trues - List of nodes which must be true
	 * @param falses - List of nodes which must be false
	 * @return -  Amount of rows where all true nodes are true and all false nodes are false
	 */
	public int getTruthCount(ArrayList<Node> trues, ArrayList<Node> falses) {
		
		/** Amount of valid rows **/
		int count = 0;
		
		/** List of table index of each true node for easy lookup **/
		ArrayList<Integer> truesIndex = new ArrayList<Integer>();
		
		/** List of table index of each false node for easy lookup **/
		ArrayList<Integer> falsesIndex = new ArrayList<Integer>();
		
		// For each node in each list
		if(trues != null) {
			for(Node node: trues) {
				// Get column index of this node so we don't have to look it up each time
				truesIndex.add(nodes.indexOf(node));
			}
		}
		
		if (falses != null) {
			for(Node node: falses) {
				falsesIndex.add(nodes.indexOf(node));
			}
		}
			
		// For each row in the data
		for(ArrayList<Boolean> row: data) {
			
			boolean valid = true;
			
			// Check all truth columns are true
			for(int trueCheck: truesIndex) {
				// If false went meant to be true, invalid
				if(row.get(trueCheck) == false) {
					valid = false;
					break;
				}
			}
			
			// Check all false columns are false
			for(int falseCheck: falsesIndex) {
				// If true went meant to be false, invalid
				if(row.get(falseCheck) == true) {
					valid = false;
					break;
				}
			}
			
			// If above loops were good, add one to count
			if(valid) {
				count++;
			}
		}
		
		return count;
	}
	
	
	/**
	 * Returns the amount of rows where a node and its
	 * parents are all true
	 * 
	 * @param node - Node to calculate CPT for
	 * @return
	 */
	public double calculateCPT(Node node) {
		
		// Get amount of data where this node == true AND parent(s) == true
		ArrayList<Node> trueList = new ArrayList<Node>();
		// Add the node and its parents to our list to check
		trueList.add(node);
		// TODO get parent node from string
		trueList.addAll(node.getParents());
		
		int trueCount = getTruthCount(trueList, null);
		
		// Get total amount of data.
		int dataLength = data.size();
		
		// Ensure no divide by 0 occurs using Bayesian Correction
		if(dataLength == 0) {
			dataLength++;
			trueCount++;
		}
		
		// TODO divide A by B to get probability
		double probability = trueCount / dataLength;
		
		// Return probability
		return probability;
	}
	
	
	/**
	 * Finds the node with the given name
	 * 
	 * @param nodeName - name of requested node
	 * @return node with matching name
	 * @return null if no node was found
	 */
	public Node getNodeByName(String nodeName) {
		// Loop over every node
		for (Node node: nodes) {
			// If node has the requested name, return it
			if(node.getName() == nodeName) {
				return node;
			}
		}
		// Return null if no node
		return null;
	}

}
