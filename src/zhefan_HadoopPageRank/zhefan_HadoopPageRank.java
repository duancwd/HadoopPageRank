package zhefan_HadoopPageRank;

import java.io.*;
import java.util.*;

public class zhefan_HadoopPageRank {
    // adjacency matrix read from file
    private HashMap<Integer, ArrayList<Integer>> adjMatrix = new HashMap<Integer, ArrayList<Integer>>();
    // input file name
    private String inputFile = "";
    // output file name
    private String outputFile = "";
    // number of iterations
    private int iterations = 10;
    // damping factor
    private double df = 0.85;
    // number of URLs
    private int size = 0;
    // calculating rank values
    private HashMap<Integer, Double> rankValues = new HashMap<Integer, Double>();

    /**
     * Parse the command line arguments and update the instance variables. Command line arguments are of the form
     * <input_file_name> <output_file_name> <num_iters> <damp_factor>
     *
     * @param args arguments
     */
    public void parseArgs(String[] args) {
    	this.inputFile = args[0];
    	this.outputFile = args[1];
    	this.iterations = Integer.parseInt(args[2]);
    	this.df = Double.parseDouble(args[3]);
    }

    /**
     * Read the input from the file and populate the adjacency matrix
     *
     * The input is of type
     *
     0
     1 2
     2 1
     3 0 1
     4 1 3 5
     5 1 4
     6 1 4
     7 1 4
     8 1 4
     9 4
     10 4
     * The first value in each line is a URL. Each value after the first value is the URLs referred by the first URL.
     * For example the page represented by the 0 URL doesn't refer any other URL. Page
     * represented by 1 refer the URL 2.
     *
     * @throws java.io.IOException if an error occurs
     */
    public void loadInput() throws IOException {
    	FileInputStream in = null;
    	//System.out.println(adjMatrix.toString());
    	
        try {
           in = new FileInputStream(inputFile);

           int c = in.read();
           while (c != -1) {
        	   //System.out.print(c + "  ");
            while (c != 13 && c != 10 && c != -1) {
                  if (c != 13 && c != 10 && c != -1) {
                          int num = 0;
                                   while( c >= 48 && c <= 57) {
                                           num = num * 10 + c - 48;
                                           c = in.read();
                                   }
                          int key = num;
                          ArrayList<Integer> arr = new ArrayList<Integer>();
                          while (c != 13 && c != 10 && c != -1) {
                                  if (c >= 48 && c <= 57) {
                                          int numin = 0;
                                                   while( c >= 48 && c <= 57) {
                                                           numin = numin * 10 + c - 48;
                                                           c = in.read();
                                                   }
                                          arr.add(numin);
                                  } else {
                                	  c = in.read();
                                  }
                          }
                          this.adjMatrix.put(key, arr);
                  } else {
                	  c = in.read();
                  }
              }
              c = in.read();
           }
           //System.out.println(adjMatrix.toString());
        } catch (IOException e) {
	        System.err.println("Problem writing to the file statsTest.txt");
	    }
    }

    /**
     * Do fixed number of iterations and calculate the page rank values. You may keep the
     * intermediate page rank values in a hash table.
     */
    public void calculatePageRank() {
    	this.size = adjMatrix.size();
    	//Initial part (t = 0)
    	Set<Integer> keys = adjMatrix.keySet();
    	double original = ((double)1/(double)(keys.size()));
    	Iterator<Integer> iterator = keys.iterator();
        while(iterator.hasNext()) {
            Integer key = iterator.next();
            rankValues.put(key, original);
        }
        
        //Iteration
        for (int t = 0; t < iterations; t++) {
        	HashMap<Integer, Double> newRankValues = new HashMap<Integer, Double>();
        	Set<Integer> oldKeys = adjMatrix.keySet();
        	Iterator<Integer> itr = oldKeys.iterator();

        	double checkSum = 0.0;
        	
            while(itr.hasNext()) {
                Integer key = itr.next();
                //System.out.print(key + "=");
                Integer size = adjMatrix.size();
                double rankValue = (((double)1-df)/(double)size)+df*(pr(key));
                newRankValues.put(key, rankValue);
                
                checkSum += rankValue;
            }
        	this.rankValues = newRankValues;
        	
        //System.out.println(checkSum);
        }
    }

    private double pr(Integer key) {
    	Set<Integer> keys = adjMatrix.keySet();
    	Iterator<Integer> iterator = keys.iterator();
    	double rankValue = 0.0;
        while(iterator.hasNext()) {
            Integer currentKey = iterator.next();
            ArrayList<Integer> arr = adjMatrix.get(currentKey);
            if(arr.contains(key)) {
            	//System.out.print(key + "=");
            	//System.out.println(this.rankValues.get(currentKey));
            	double value = this.rankValues.get(currentKey);
            	//System.out.println("value = "+value);
            	rankValue += (value/(double) arr.size());
            }
            
        }
		return rankValue;
	}

	/**
     * Print the pagerank values. Before printing you should sort them according to decreasing order.
     * Print all the values to the output file. Print only the first 10 values to console.
     *
     * @throws IOException if an error occurs
     */
    public void printValues() throws IOException {
    	//System.out.println(rankValues.toString());
    	ArrayList<Integer> sorted = sort(rankValues);
    	String list10 = "";
    	String listall = "";
    	for (int i = 0; i < 10; i++) {
    		int key = sorted.get(i);
    		list10 += (key + " = " + rankValues.get(key) + "\r\n");
    	}
    	System.out.println("Iteration number is " + this.iterations);
    	System.out.println("Top 10 urls and their page ranks are:");
    	System.out.print(list10);
    	listall = list10;
    	for (int i = 10; i < this.size; i++) {
    		int key = sorted.get(i);
    		listall += (key + " = " + rankValues.get(key) + "\r\n");
    	}

    	try {
    		        File output = new File(outputFile);
    		        FileOutputStream out = new FileOutputStream(output);
    		        OutputStreamWriter opw = new OutputStreamWriter(out);    
    		        Writer w = new BufferedWriter(opw);
    		        w.write(
                            "*********************************************\r\n"
                    +"* Project#1 P434                            *\r\n"
                    +"* PageRank                                  *\r\n"
                    +"*********************************************\r\n"
                             +"Java PageRank \r\n"
                                 + "["+outputFile+"]\r\n["+size+" urls]\r\n[Group 6 (Zhexu Fan & Chao Duan)] \r\n" 
                                 + "e.g.: "                      
                                 + "Java PageRank "+" "+ inputFile+" "+" "+" " +"iterations = "+iterations+"\r\n "+"df = "+ df+"\r\n"
                                 
                                +"***************\r\n"
                                +"*Top 10 :  *\r\n"
                                +"***************\r\n"
                                + list10
                                +"\r\n "
                                +"********************\r\n"
                                +"*All sorted urls:* \r\n"
                                +"*********************\r\n"
                                
                                +listall
                                +"\r\n"
               );
    		        w.close();
    		    } catch (IOException e) {
    		        System.err.println("Problem writing to the output file");
    		    }
    }

    private ArrayList<Integer> sort(HashMap<Integer, Double> unsorted) {
    	HashMap<Integer, Double> original = (HashMap<Integer, Double>) unsorted.clone();
    	ArrayList<Integer> sorted = new ArrayList<Integer>();
    	while (!original.isEmpty()) {
    		int current = maxValueKey(original);
    		sorted.add(current);
    		//System.out.println(original.get(current));
    		original.remove(current);
    		
    	}
    	//System.out.println(sorted);
		return sorted;
	}

	private int maxValueKey(HashMap<Integer, Double> original) {
		
		Set<Integer> keys = original.keySet();
    	Iterator<Integer> iterator = keys.iterator();
		int maxKey = iterator.next();
    	double maxValue = original.get(maxKey);
        while(iterator.hasNext()) {
            int currentKey = iterator.next();
            double currentValue = original.get(currentKey);
            if (currentValue > maxValue) {
            	maxKey = currentKey;
            	maxValue = currentValue;
            }
        }
		return maxKey;
	}

	public static void main(String[] args) throws IOException {
    	zhefan_HadoopPageRank sequentialPR = new zhefan_HadoopPageRank();

        sequentialPR.parseArgs(args);
        sequentialPR.loadInput();
        sequentialPR.calculatePageRank();
        sequentialPR.printValues();
    }
}
