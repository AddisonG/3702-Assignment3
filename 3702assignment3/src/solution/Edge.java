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
	private Node node2; 	// The noed this edge goes to
	
	public Edge(Node n1, Node n2) {
		node1 = n1;
		node2 = n2;
	}
	
	public Node getParent() {
		return node1;
	}
	
	public Node getChild() {
		return node2;
	}
	
	public Double getWeight() {
		// TODO
		return (double) 0;
	}

}
