package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Bayesian Network with nodes.
 * Serves as a controller for node calculations.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class BayesianNetwork extends Global {
	public final static int MODE = DEBUG; // Current debug mode //TODO shouldn't this be in Main.java?
	
	/** The list of nodes in this network **/
	private Map<String, Node> nodes;
	
	/** The data table of this network. Outer Arraylist is each row, 
	 * inner Arraylist is each node **/
	private List<Boolean[]> data;
	
	public BayesianNetwork(Map<String, Node> nodes, List<Boolean[]> data) {
		this.nodes = nodes;
		this.data = data;
	}
	
	
	/**
	 * @return All of the nodes within this Bayesian Network
	 */
	public Map<String, Node> getNodes() {
		return nodes;
	}
	
	
	/**
	 * Returns the count where every node in a given data has value 'bool'
	 * 
	 * TODO if we're only using one list and looking for true, we don't need bool var
	 * TODO calling a boolean bool is like calling a string myString
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
		
		int trueCount;
		List<Node> trueList = new ArrayList<Node>();
		trueList.add(node);
		
		// Get total amount of data rows
		int dataLength = data.size();
		
		// If parents, get amount of data where this node == true AND parents == true
		if(node.hasParents()) {
			
			// Only count data rows that are true for node
			dataLength = getBooleanCount(trueList, true); // Since at this point only the node is in truelist
			
			// Add the node's parents to our list to check
			trueList.addAll(node.getParents());
			
			// Get the amount of rows where the above list is all true
			trueCount = getBooleanCount(trueList, true);
			
		} else {
			// If no parents, count is just where node is true
			trueCount = getBooleanCount(trueList, true);
		}
			
		// Ensure no divide by 0 occurs using Bayesian Correction
		if (dataLength == 0) {
			dataLength++;
			trueCount++;
		}
		
		// Return probability
		return trueCount / dataLength;
	}
}