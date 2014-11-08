package solution;

import java.io.*;
import java.util.*;

/**
 * Static class writes data to text files.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class Writer extends Global {
	public final static int MODE = INFO; // Current debug mode
	
	
	public static void writeCPT(String filename, BayesianNetwork bayonet) throws IOException {
		String ls = System.getProperty("line.separator");
		Map<String, Node> nodes = bayonet.getNodes();
		FileWriter writer = new FileWriter("solutions/" + filename);
		
		// For every node in the network
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node = nodeElement.getValue();
			String line = "";
			
			log(INFO, "\nWriting node: " + node.getName());
			
			// Get name of node
			line += node.getName();
			
			// Add names of parents to line
			List<Node> parents = node.getParents();
			for (Node parent: parents) {
				log(INFO, "  Parent: " + parent.getName());
				line += " " + parent.getName();
			}
			
			// Write line to file
			writer.write(line + ls);
			
			// Get conditional probability for each parent combination
			List<Double> probabilities = bayonet.getAllProbabilities(node);
			
			// Put all probabilities in a string
			String probabilitiesLine = "";
			for (int i = 0; i < probabilities.size(); i++) {
				if (i != 0) {
					// Add a space if not the first in the line
					probabilitiesLine += " ";
				}
				probabilitiesLine += probabilities.get(i);
			}
			
			// Write line to file
			writer.write(probabilitiesLine + ls);
			log(INFO, "Finished Writing node");
		}
		
		writer.write(bayonet.calculateLogLikelihood() + ls);
		
		log(INFO, "Finished Writing!");
		
		writer.close();
	}
	
	public static void writeDAG(String filename, BayesianNetwork bayonet) throws IOException {
		String ls = System.getProperty("line.separator");
		Map<String, Node> nodes = bayonet.getNodes();
		FileWriter writer = new FileWriter("solutions/" + filename);
		
		// Print name and parents of each node
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node = nodeElement.getValue();
			String line = "";
			
			log(INFO, "\nWriting node: " + node.getName());
			
			// Get name of node
			line += node.getName();
			
			// Add names of parents to line
			List<Node> parents = node.getParents();
			for (Node parent: parents) {
				log(INFO, "  Parent: " + parent.getName());
				line += " " + parent.getName();
			}
			
			// Write line to file
			writer.write(line + ls);
		}
		
		// Print each node in a block of name and parents, then CPT
		for (Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
			
			Node node = nodeElement.getValue();
			String line = "";
			
			log(INFO, "\nWriting node: " + node.getName());
			
			// Get name of node
			line += node.getName();
			
			// Add names of parents to line
			List<Node> parents = node.getParents();
			for (Node parent: parents) {
				log(INFO, "  Parent: " + parent.getName());
				line += " " + parent.getName();
			}
			
			// Write line to file
			writer.write(line + ls);
			
			// Get conditional probability for each parent combination
			List<Double> probabilities = bayonet.getAllProbabilities(node);
			
			// Put all probabilities in a string
			String probabilitiesLine = "";
			for (int i = 0; i < probabilities.size(); i++) {
				if (i != 0) {
					// Add a space if not the first in the line
					probabilitiesLine += " ";
				}
				probabilitiesLine += probabilities.get(i);
			}
			
			// Write line to file
			writer.write(probabilitiesLine + ls);
		}
		
		writer.write(bayonet.calculateLogLikelihood() + " " + bayonet.calculateScore() + ls);
		
		log(INFO, "Finished Writing!");
		
		writer.close();
	}
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}