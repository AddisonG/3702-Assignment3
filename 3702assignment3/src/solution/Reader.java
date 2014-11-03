package solution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Static class used to read in the files and initialise the appropriate
 * classes with the read data.
 * 
 * @author Cameron Darragh<br>Addison Gourluck
 */
public class Reader extends Global {
	public final static int MODE = DEBUG; // Current debug mode
	
	/**
	 * Opens and reads the file at the given path, returning the Bayesian
	 * Network contained within the data.
	 * 
	 * @param filePath - path to file to read
	 * @return a new network representation of that file
	 * @throws IOException 
	 */
	public static BayesianNetwork readFile(String filePath) {
		BayesianNetwork network = null;
		BufferedReader br = null;
		
		log(INFO, "Loading file at " + filePath);
		
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log(ERROR, "Error opening file.");
			System.exit(1);
		}
		
		try {
			network = extractData(br);
		} catch (IOException e) {
			e.printStackTrace();
			log(ERROR, "Error reading data.");
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			log(ERROR, "Error closing file.");
			System.exit(1);
		}
		
		log(INFO, "Finished reading from file.");
		return network;
	}
	
	/**
	 * Extracts the data from the buffered reader, and returns it as a Bayesian
	 * Network.
	 * 
	 * @param br - Already opened buffered reader
	 * @return Bayesian network
	 * @throws IOException
	 */
	private static BayesianNetwork extractData(BufferedReader br)
			throws IOException {
		String line;
		Scanner s;
		
		line = br.readLine();
		s = new Scanner(line);
		int numNodes = s.nextInt();
		int numData = s.nextInt();
		s.close();
		
		log(INFO, numNodes + " nodes, with " + numData + " lines of data.");
		
		// Initialise hashmap so that it won't have to rehash.
		Map<String, Node> nodes = new HashMap<String, Node>((int) Math.ceil(numNodes / 0.75));
		
		// Read in and setup each node
		for (int i = 0; i < numNodes; i++) {
			line = br.readLine();
			
			s = new Scanner(line);
			String name = s.next();
			
			log(DEBUG, "\nAdding: " + name);
			// Create or set current node, and its index
			Node current;
			nodes.putIfAbsent(name, new Node(name));
			current = nodes.get(name);
			current.setIndex(i);
			
			// Add parents
			while (s.hasNext()) {
				String parent = s.next();
				log(DEBUG, "  Parent: " + parent);
				if (nodes.containsKey(parent)) {
					// parent already exists, add it
					current.addParent(nodes.get(parent));
				} else {
					// parent doesn't exist, create and add it
					nodes.put(parent, new Node(parent));
					current.addParent(nodes.get(parent));
				}
			}
		}
		
		List<Boolean[]> data = new ArrayList<Boolean[]>(numData);
		// Read in each data point
		for (int i = 0; i < numData; i++) {
			line = br.readLine();
			s = new Scanner(line);
			
			boolean[] row = new boolean[numNodes];
			
			for (int j = 0; j < numNodes; j++) {
				row[j] = s.nextInt() == 1 ? true : false;
			}
		}
		
		// Create Bayesian Network
		return new BayesianNetwork(nodes, data);
	}
}