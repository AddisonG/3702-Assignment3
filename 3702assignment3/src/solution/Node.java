/**
 * 
 */
package solution;

import java.util.ArrayList;

/**
 * @author Cameron Darragh, Addison Gourluck
 * 
 * A node in the Bayesian Network.
 *
 */
public class Node {
	
	/** The name of this node **/
	private String name; 
	
	/** The list of parents of this node **/
	private ArrayList<Node> parents;
	
	
	/**
	 * Constructor for node
	 */
	public Node() {
		// Initialize array lists
		parents = new ArrayList<Node>();
	}
	
	
	/** 
	 * Adds a new parent to this node
	 * 
	 * @param parent - the new parent to add
	 */
	public void addParent(Node parent) {
		// Check given Node is not already a parent
		if(!parents.contains(parent)) {
			parents.add(parent);
		}
	}
	
	
	/**
	 * Returns the list of parents for this node
	 * 
	 * @return	- list of nodes which are parents
	 */
	public ArrayList<Node> getParents() {
		return parents;
	}
	
	
	/**
	 * Sets the name of this node
	 * 
	 * @param newName - the new name to set
	 */
	public void setName(String newName) {
		this.name = newName;
	}
	
	
	/**
	 * Returns the name of the node
	 * 
	 * @return the name of the node
	 */
	public String getName() {
		return this.name;
	}

}