/**
 * 
 */
package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Bayesian Network with nodes.
 * Serves as a controller for node calculations.
 * 
 * @author Cameron Darragh<br>Addison Gourluck
 */
public class BayesianNetwork {
	
	/** The list of nodes in this network **/
	private Map<String, Node> nodes;
	
	/** The data table of this network. Outer Arraylist is each row, 
	 * inner Arraylist is each node **/
	private List<Boolean[]> data;
	
	public BayesianNetwork(Map<String, Node> nodes, List<Boolean[]> data) {
		this.data = data;
		this.nodes = nodes;
	}
	
	/**
	 * Returns the count where every node in a given data has value 'bool'
	 * 
	 * @param family - List of nodes to be compared with bool
	 * @param bool - The boolean value compared with each value of family
	 * @return Amount of rows where all nodes in the family match bool
	 */
	public int getBooleanCount(List<Node> family, boolean bool) {
		int count = 0; // Number of valid rows
		
		// For each row in the data
		for (Boolean[] row: data) {
			boolean valid = true;
			
			// If any nodes value in this row differs from bool, discount the row
			for (Node n : family) {
				if (row[n.getIndex()] == bool) {
					valid = false;
					break;
				}
			}
			
			// If above loops were good, add one to count
			if (valid) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Returns the amount of rows where a node and its parents are all true.
	 * This is known as the node's conditional probability table (CPT).
	 * 
	 * @param node - Node to calculate CPT for
	 * @return
	 */
	public double calculateCPT(Node node) {
		// Get amount of data where this node == true AND parent(s) == true
		List<Node> trueList = new ArrayList<Node>();
		// Add the node and its parents to our list to check
		trueList.add(node);
		trueList.addAll(node.getParents());
		
		// Get the amount of rows where the above list is all true
		int trueCount = getBooleanCount(trueList, true);
		
		// Get total amount of data.
		int dataLength = data.size();
		
		// Ensure no divide by 0 occurs using Bayesian Correction
		if (dataLength == 0) {
			dataLength++;
			trueCount++;
		}
		
		// Return probability
		return trueCount / dataLength;
	}
}