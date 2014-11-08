package solution;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in the Bayesian Network.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class Node {
	
	/** The name of this node **/
	private String name; 
	private int index;
	
	/** The list of parents of this node **/
	private List<Node> parents;

	/**
	 * Node constructor.
	 * 
	 * @param name - Name of node
	 */
	public Node(String name) {
		this.name = name;
		parents = new ArrayList<Node>();
	}
	
	/** 
	 * Adds a new parent to this node
	 * 
	 * @param parent - The new parent to add
	 */
	public void addParent(Node parent) {
		// Check given Node is not already a parent
		if (parent != null && !parents.contains(parent)) {
			parents.add(parent);
		}
	}
	
	/**
	 * Not everyone has good parents, so here's a simple
	 * method to remove them!
	 * 
	 * Disclaimer: Does not work in real life
	 * 
	 * @param parent
	 */
	public void removeParent(Node parent) {
		if(parents.contains(parent)) {
			parents.remove(parent);
		}
	}
	
	/**
	 * The equivalent of running away from home
	 */
	public void removeAllParents() {
		parents.clear();
	}
	
	/**
	 * Returns if this node has parents
	 * 
	 * @return true if has parents, else false
	 */
	public boolean hasParents() {
		return (parents.size() != 0);
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Returns the list of parents for this node
	 * 
	 * @return List of nodes which are parents
	 */
	public List<Node> getParents() {
		return parents;
	}
	
	/**
	 * Returns the list of ancestors for this node
	 * 
	 * @return List of all nodes which are ancestors
	 */
	public List<Node> getAncestors() {
		List<Node> ancestors = new ArrayList<Node>();
		
		// recursion
		for (Node parent : parents) {
			ancestors.addAll(parent.getAncestors());
		}
		
		return ancestors;
	}
	
	/**
	 * @return The name of the node
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * This equals method pays no heed to parents, or to the index, when
	 * making its comparisons.
	 * 
	 * @param node
	 * @return
	 */
	public boolean equals(Node node) {
		// Check if the same object
		if (this.equals((Object)node)) {
			return true;
		}
		// Check if same name (ignore parents)
		if (this.getName().equals(node.getName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return The index of the node
	 */
	public int getIndex() {
		return this.index;
	}
	
	public String toString() {
		return name + "(" + this.index + ")";
	}
}