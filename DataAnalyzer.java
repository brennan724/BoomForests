
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

		// load input data from file data
		int[] input_columns = {3, 4, 5};
		ArrayList<Hashtable<String, Integer>> string_to_int = new ArrayList<Hashtable<String, Integer>>();
		ArrayList<Hashtable<Integer, String>> int_to_string = new ArrayList<Hashtable<Integer, String>>();
		int[][] inputs = new int[cells.length][];
		for (int i = 0; i < inputs.length; ++i)
			inputs[i] = new int[input_columns.length];
		for (int i = 0; i < input_columns.length; ++i) {
			int col = input_columns[i];
			string_to_int.add(new Hashtable<String, Integer>());
			int_to_string.add(new Hashtable<Integer, String>());
			for (int j = 1; j < cells.length; ++j) {
				if (!string_to_int.get(i).contains(cells[j][col])) {
					int n = string_to_int.get(i).size();
					string_to_int.get(i).put(cells[j][col], n);
					int_to_string.get(i).put(n, cells[j][col]);
				}
				inputs[j][i] = string_to_int.get(i).get(cells[j][col]);
			}
		}

		// load output (category) data from file data
		int output_column = 10;
		int[] outputs = new int[inputs.length];
		Hashtable<String, Integer> output_string_to_int = new Hashtable<String, Integer>();
		Hashtable<Integer, String> output_int_to_string = new Hashtable<Integer, String>();
		for (int i = 0; i < outputs.length; ++i) {
			if (!output_string_to_int.contains(cells[i][output_column])) {
				int n = output_string_to_int.size();
				output_string_to_int.put(cells[i][output_column], n);
				output_int_to_string.put(n, cells[i][output_column]);
			}
			outputs[i] = output_string_to_int.get(cells[i][output_column]);
		}

		// todo: analyze it
	}
}
