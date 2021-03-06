
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataAnalyzer {
	/*
	 * Read the given file and print out the lines as an ArrayList.
	 */
	private static ArrayList<String> readLinesFromFile(String path) {
		ArrayList<String> rtn = null;
		try {
			rtn = new ArrayList<String>();
			FileReader file = new FileReader(path);
			BufferedReader buffer = new BufferedReader(file);
			String line;
			while ((line = buffer.readLine()) != null)
				rtn.add(line);
			buffer.close();
			file.close();
		}
		catch (IOException e) {
			System.out.println("Problem reading file 'tea.csv'");
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * Takes five command line arguments:
	 * @param fileName the file of data
	 * @param numTrees number of trees to use for each iteration of Adaboost
	 * @param leaf_accuracy leaf accuracy for C4.5
	 * @param confidence_threshold the confidence threshold for pruning
	 * @param max_depth the maximum depth for the C4.5 trees to obtain
	 */
	public static void main(String[] args) {
		// parse user input
		String fileName = args[0];
		System.out.println(fileName);
		int numTrees = Integer.parseInt(args[1]);
		System.out.println(numTrees);
		double leaf_accuracy = Double.parseDouble(args[2]);
		System.out.println(leaf_accuracy);
		double confidence_threshold = Double.parseDouble(args[3]);
		System.out.println(confidence_threshold);
		int max_depth = Integer.parseInt(args[4]) - 1;
		System.out.println(max_depth + 1);
		if (max_depth < 0) {
			System.out.println("max_depth must be a positive integer");
			return;
		}


		// read file
		ArrayList<String> lines = readLinesFromFile(fileName);
		String[][] cells = new String[lines.size()][];
		for (int i = 0; i < lines.size(); ++i) {
			cells[i] = lines.get(i).split(",");
		}


		// load these columns from the csv fil
		int[] input_columns = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36};
		ArrayList<Hashtable<String, Integer>> string_to_int = new ArrayList<Hashtable<String, Integer>>();
		ArrayList<Hashtable<Integer, String>> int_to_string = new ArrayList<Hashtable<Integer, String>>();
		int[][] inputs = new int[cells.length-1][];
		for (int i = 0; i < inputs.length; ++i)
			inputs[i] = new int[input_columns.length];
		for (int i = 0; i < input_columns.length; ++i) {
			int col = input_columns[i];
			string_to_int.add(new Hashtable<String, Integer>());
			int_to_string.add(new Hashtable<Integer, String>());
			for (int j = 1; j < cells.length; ++j) {
				if (string_to_int.get(i).get(cells[j][col]) == null) {
					int n = string_to_int.get(i).size();
					string_to_int.get(i).put(cells[j][col], n);
					int_to_string.get(i).put(n, cells[j][col]);
				}
				inputs[j-1][i] = string_to_int.get(i).get(cells[j][col]);
			}
		}


		// load output (category) data from file data
		int output_column = 13;
		int[] outputs = new int[inputs.length];
		Hashtable<String, Integer> output_string_to_int = new Hashtable<String, Integer>();
		Hashtable<Integer, String> output_int_to_string = new Hashtable<Integer, String>();
		for (int i = 1; i < outputs.length+1; ++i) {
			if (output_string_to_int.get(cells[i][output_column]) == null) {
				int n = output_string_to_int.size();
				output_string_to_int.put(cells[i][output_column], n);
				output_int_to_string.put(n, cells[i][output_column]);
			}
			outputs[i-1] = output_string_to_int.get(cells[i][output_column]);
		}


		// run AdaBoost
		Adaboost forest = new Adaboost(inputs, outputs, numTrees, leaf_accuracy, confidence_threshold, max_depth);
		int score = 0;
		for (int i = 0; i < inputs.length; ++i) {
			int guess = forest.classify(inputs[i]);
			if (guess == outputs[i]) ++score;
		}
		System.out.println("\nForest:");
		System.out.println(forest.toString());
		System.out.println("\nAccuracy:");
		System.out.println((double) score/inputs.length);
	}
}
