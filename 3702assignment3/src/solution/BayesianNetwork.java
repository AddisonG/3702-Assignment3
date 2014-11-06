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
	public final static int MODE = INFO; // Current debug mode
	
	/** The list of nodes in this network **/
	private Map<String, Node> nodes;
	
	/** The data table of this network. Outer list is each row, 
	 * inner list is each node **/
	private List<List<Boolean>> data;
	
	public BayesianNetwork(Map<String, Node> nodes, List<List<Boolean>> data2) {
		this.nodes = nodes;
		this.data = data2;
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
	
	/**
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
			
			// Calculate the amount of data for these lists
			double count = countBooleanData(trueList, falseList);
			
			ArrayList<Node> temp = (ArrayList<Node>) trueList.clone();
			temp.remove(0);
			
			// Calculate the amount of data for these lists without original node
			double dataCount = countBooleanData(temp, falseList);
			
			// Avoid divide by zero
			if (dataCount == 0) {
				count++;
				dataCount++;
			}
			
			double probability = count/dataCount;
			
			log(DEBUG, "Probability is " + probability);
			
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
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}