package solution;

/**
 * An edge between two nodes in the bayesian network
 * 
 * @author Cameron Darragh, Addison Gourluck
 *
 */
public class Edge {
	
	private double weight;	// The weight of this edge (from mutual inclusion)
	private Node node1;		// The node this edge comes from
	private Node node2; 	// The node this edge goes to
	
	public Edge(Node n1, Node n2) {
		if (n1.equals(n2)) {
			System.exit(666);
		}
		node1 = n1;
		node2 = n2;
		
		if (!n1.getParents().contains(n2)) {
			//n1.addParent(n2); // Add n2 as a parent, if it isn't already
		}
	}
	
	public Node getParent() {
		return node1;
	}
	
	public Node getChild() {
		return node2;
	}
	
	public Double getWeight() {
		return weight;
	}
	
	public boolean equals(Edge edge) {
		if (node1.getName().equals(edge.getParent().getName()) && node2.getName().equals(edge.getChild().getName())) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return node1.getName() + node2.getName();
	}
}