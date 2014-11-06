package solution;

import java.io.*;
import java.util.*;

/**
 * Static class writes data to text files.
 * 
 * @author Cameron Darragh Addison Gourluck
 */
public class Writer extends Global {
	public final static int MODE = DEBUG; // Current debug mode
	
	
	public static void writeFile(String fileName, BayesianNetwork bayonet) {
		
		try {
			
			String ls = System.getProperty("line.separator");
			Map<String, Node> nodes = bayonet.getNodes();
			FileWriter writer = new FileWriter("solutions/" + fileName);
			
			// For every node in the network
			for(Map.Entry<String, Node> nodeElement : nodes.entrySet()) {
				
				Node node = nodeElement.getValue();
				
				String line = "";
			
				log(INFO, "Writing node: " + node.getName());
				
				// Get name of node
				line += node.getName();
			
				// Add names of parents to line
				List<Node> parents = node.getParents();
				for(Node parent: parents) {
					log(INFO, "  Parent: " + parent.getName());
					line += " " + parent.getName();
				}
			
				// Write line to file
				writer.write(line + ls);
			
				// Get conditional probability for each parent combination
				ArrayList<Double> probabilities = bayonet.getAllProbabilities(node);
				
				log(INFO, "Probabilities to write: ");
				log(INFO, probabilities.toString());
			
				// Put all probabilities in a string
				String probabilitiesLine = "";
				for (int i = 0; i < probabilities.size(); i++) {
					if(i != 0) {
						// Add a space if not the first in the line
						probabilitiesLine += " ";
					}
					probabilitiesLine += probabilities.get(i);
				}
				
				log(INFO, "Writing probability Line: " + probabilitiesLine);
				
				// Write line to file
				writer.write(probabilitiesLine + ls);
			}
			
			log(INFO, "Finished Writing!");
			
			writer.close();
		
		} catch(IOException e) {
			log(ERROR, "Error Writing File!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}