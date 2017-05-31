
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataAnalyzer {
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

	public static void main(String[] args) {
		ArrayList<String> lines = readLinesFromFile("tea.csv");
		String[][] cells = new String[lines.size()][];
		for (int i = 0; i < lines.size(); ++i) {
			cells[i] = lines.get(i).split(",");
		}

		Hashtable<String, Integer> foo = new Hashtable<String, Integer>();

		// load input data from file data
		int[] input_columns = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36};
		// int[] input_columns = {1, 15, 19, 26};
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


		double[] weights = new double[outputs.length];
		for (int w = 0; w < weights.length; w++) {
			weights[w] = 1;
		}

		Adaboost forest = new Adaboost(inputs, outputs, 10);
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
