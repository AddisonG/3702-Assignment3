/**
 * 
 */
package solution;

import java.io.*;
import java.util.*;

/**
 * @author Cameron Darragh, Addison Gourluck
 * 
 * Static class used to read in the files and initialise the appropriate
 * classes with the read data.
 *
 */
public final class Reader {

	//TODO check method works correctly
	/**
	 * Reads the file at the given path.
	 * Creates a new Bayesian Network filled with nodes
	 * from the read file.
	 * 
	 * @param filePath - path to file to read
	 * @return - a new network representation of that file
	 * @throws IOException 
	 */
	public static BayesianNetwork readFile(String filePath) throws IOException {
		System.out.println("Loading file at " + filePath);
		
		BayesianNetwork network;
		
		BufferedReader input = new BufferedReader(new FileReader(filePath));
		String line;
		int lineNo = 0;
		Scanner s;
		try {
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);
			int numNodes = s.nextInt();
			int numData = s.nextInt();
			
			System.out.println("Nodes are " + numNodes + ", data lines are " + numData);
			
			s.close();
			
			ArrayList<Node> nodes = new ArrayList<Node>();
			// Read in and setup each node
			for(int i = 0; i < numNodes; i++) {
				line = input.readLine();
				lineNo++;
				
				String name;
				ArrayList<String> parents = new ArrayList<String>();
				
				s = new Scanner(line);
				name = s.next();
				
				// Add parents
				while(s.hasNext()) {
					parents.add(s.next());
				}
				
				nodes.add(new Node(name, parents));
			}
			
			ArrayList<ArrayList<Boolean>> data = new ArrayList<ArrayList<Boolean>>();
			// Read in each data point
			for(int i = 0; i < numData; i++) {
				line = input.readLine();
				lineNo++;
				
				ArrayList<Boolean> row = new ArrayList<Boolean>();
				for(int j = 0; j < numNodes; j++) {
					s = new Scanner(line);
					int dataPoint = s.nextInt();
					
					// set to true or false in data
					if(dataPoint == 1) {
						row.add(true);
					} else {
						row.add(false);
					}
				}
				data.add(row);
			}
			
			// Create Bayesian Network
			network = new BayesianNetwork(nodes, data);
			
		} catch(Exception e) {
			System.out.println("Something went wrong :(");
			throw new IOException(e.getMessage());
			
		} finally {
			input.close();
		}
		
		System.out.println("Finished reading from file");
		
		return network;
	}

}
