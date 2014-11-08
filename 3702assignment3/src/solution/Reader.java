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
 * @author Cameron Darragh Addison Gourluck
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
	
	
	public static BayesianNetwork readFileNoParents(String filePath) {
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
			network = extractDataNoParents(br);
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
			
			// Add node if doesn't exist
			if(!nodes.containsKey(name)) {
				nodes.put(name, new Node(name));
			}
			
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
		
		List<List<Boolean>> data = new ArrayList<List<Boolean>>(numData);
		// Read in each data point
		for (int i = 0; i < numData; i++) {
			line = br.readLine();
			s = new Scanner(line);
			
			List<Boolean> row = new ArrayList<Boolean>(numNodes);
			
			for (int j = 0; j < numNodes; j++) {
				if (s.nextInt() == 1) {
					row.add(true);
				} else {
					row.add(false);
				}
			}
			data.add(row);
		}
		
		log(DEBUG, "\nRead data as: ");
		for (List<Boolean> d : data) {
			log(DEBUG, d.toString().replaceAll("false", "0").replaceAll("true", "1"));
		}
		
		// Create Bayesian Network
		return new BayesianNetwork(nodes, data);
	}
	
	
	
	/**
	 * Extracts the data from the buffered reader, and returns it as a Bayesian
	 * Network.
	 * 
	 * This one is when no parent relations are given
	 * 
	 * @param br - Already opened buffered reader
	 * @return Bayesian network
	 * @throws IOException
	 */
	private static BayesianNetwork extractDataNoParents(BufferedReader br)
			throws IOException {
		String line;
		Scanner s;
		
		line = br.readLine();
		s = new Scanner(line);
		int numNodes = s.nextInt();
		int numData = s.nextInt();
		s.close();
		
		line = br.readLine();
		s = new Scanner(line);
		
		log(INFO, numNodes + " nodes, with " + numData + " lines of data.");
		
		// Initialise hashmap so that it won't have to rehash.
		Map<String, Node> nodes = new HashMap<String, Node>((int) Math.ceil(numNodes / 0.75));
		
		int i = 0;
		// Read in and setup each node
		while(s.hasNext()) {
			
			String name = s.next();
			
			log(DEBUG, "\nAdding: " + name);
			// Create or set current node, and its index
			Node current;
			
			// Add node if doesn't exist
			if(!nodes.containsKey(name)) {
				nodes.put(name, new Node(name));
			}
			
			current = nodes.get(name);
			current.setIndex(i);
			i++;
		}
		s.close();
		
		List<List<Boolean>> data = new ArrayList<List<Boolean>>(numData);
		// Read in each data point
		for (i = 0; i < numData; i++) {
			line = br.readLine();
			s = new Scanner(line);
			
			List<Boolean> row = new ArrayList<Boolean>(numNodes);
			
			for (int j = 0; j < numNodes; j++) {
				if (s.nextInt() == 1) {
					row.add(true);
				} else {
					row.add(false);
				}
			}
			data.add(row);
		}
		
		log(DEBUG, "\nRead data as: ");
		for (List<Boolean> d : data) {
			log(DEBUG, d.toString().replaceAll("false", "0").replaceAll("true", "1"));
		}
		
		log(DEBUG, "Size of nodes is: " + nodes.size());
		
		// Create Bayesian Network
		return new BayesianNetwork(nodes, data);
	}
	
	
	private static void log(int mode, String str) {
		if (MODE >= mode) {
			System.out.println(str);
		}
	}
}