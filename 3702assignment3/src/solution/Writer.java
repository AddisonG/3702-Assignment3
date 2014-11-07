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
	
	public static void writeFile(String[] args, BayesianNetwork bayonet) throws IOException {
		String filename = args[0];
		String mode = args[1];
		
		switch (mode) {
			case "task1":
				filename = "cpt-" + filename + ".txt";
				task1(filename, bayonet);
				break;
			case "task2":
				task2(bayonet);
				break;
			// add more cases, as above
			default:
				break;
		}
	}
	
	private static void task1(String filename, BayesianNetwork bayonet) throws IOException {
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
		}
		
		log(INFO, "Finished Writing!");
		
		writer.close();
	}
	
	
	private static void task2(BayesianNetwork bayonet) {
		double likelihood = bayonet.calculateMaximumLikelihood();
		double logLikelihood = bayonet.calculateLogLikelihood();
		
		System.err.println("Maximum Likelihood estimate is: " + likelihood);
		System.err.println("Maximum Log Likelihood estimate is: " + logLikelihood);
	}
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}