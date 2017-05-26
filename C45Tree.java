import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

class C45Tree {

	// two-dimensional array of inputs
	// 1-d array signifying the Life Insurance Promo data points (yes, no)
	// 1-d array of weights for data points
	int[][] input;
	int[] output;

	double[] weights;
	HashMap<Integer, C45Tree> children;
	boolean isLeaf;
	int category;

	// meta-data
	int my_attribute;

	public C45Tree(int[][] input, int[] output, double[] weights, double leaf_accuracy, int depth) {
		this.isLeaf = false;
		int dimNum = input[0].length;
		double[] goodness = new double[dimNum];
		children = new HashMap<Integer, C45Tree>();
		// iterate through each of the categories, to see which one has the best goodness
		for (int dim = 0; dim < dimNum; dim++) {
			// calculate the goodness, need to loop through every row
			// create a dictonary to put everything into
			// counts[value][category] = count
			Map<Integer, Map<Integer, Double>> counts = new HashMap<Integer, Map<Integer, Double>>();
			// this iterates through the rows
			for (int i = 0; i < input.length; i++) {
				// increment the appropriate value
				if (counts.containsKey(input[i][dim])) {
					double current_count = counts.get(input[i][dim]).get(output[i]);
					counts.get(input[i][dim]).put(output[i], current_count + weights[i]);
				}
				else {
					counts.put(input[i][dim], new HashMap<Integer, Double>());
					counts.get(input[i][dim]).put(0, (double)0);
					counts.get(input[i][dim]).put(1, (double)0);
					counts.get(input[i][dim]).put(2, (double)0);
					counts.get(input[i][dim]).put(output[i], weights[i]);
				}
			}
			// counts[value][category] = count
			// now that we've gotten the counts of everything, we can calculate goodness
			Set<Integer> valuesSet = counts.keySet();
			int[] values = new int[valuesSet.size()];
			int counter = 0;
			for (Integer v : valuesSet) {
				values[counter++] = v;
			}
			double total_branches = 0;
			double correct_classifications = 0;
			// look at each branch, and figure out where it is supposed to be classified
			for (int val = 0; val < values.length; val++) {
				Map<Integer, Double> category_counts = counts.get(values[val]);
				int max_index = -1;
				double max_value = -1;
				for (int cat = 0; cat < 3; cat++) {
					// if there is a tie, then it will give us the first thing
					total_branches += category_counts.get(cat);
					if (max_value == -1 || category_counts.get(cat) > max_value) {
						max_index = cat;
						max_value = category_counts.get(cat);
					}
				}
				correct_classifications += max_value;
			}
			double individual_goodness = (double) correct_classifications / total_branches / values.length;
			goodness[dim] = individual_goodness;
		}

		// now that I have the entire list of goodnesses, we need to figure out what to divide along
		int best_dim = -1;
		double best_dim_value = -1;
		for (int i = 0; i < goodness.length; i++) {
			if (best_dim_value == -1 || goodness[i] > best_dim_value) {
				best_dim = i;
				best_dim_value = goodness[i];
			}
		}
		// This is the index that I'm going to want to split on

		my_attribute = best_dim;

		// counts[value][category] = count
		Map<Integer, Map<Integer, Double>> counts = new HashMap<Integer, Map<Integer, Double>>();
		for (int i = 0; i < input.length; i++) {
			// increment the appropriate value
			if (counts.containsKey(input[i][best_dim])) {
				double current_count = counts.get(input[i][best_dim]).get(output[i]);
				counts.get(input[i][best_dim]).put(output[i], current_count + weights[i]);
			}
			else {
				counts.put(input[i][best_dim], new HashMap<Integer, Double>());
				counts.get(input[i][best_dim]).put(0, (double)0);
				counts.get(input[i][best_dim]).put(1, (double)0);
				counts.get(input[i][best_dim]).put(2, (double)0);
				counts.get(input[i][best_dim]).put(output[i], weights[i]);
			}
		}

		for (int val : counts.keySet()) {
			int max_index = -1;
			double max_value = -1;
			for (int cat = 0; cat < 3; cat++) {
				if (counts.get(val).get(cat) > max_value) {
					max_index = cat;
					max_value = counts.get(val).get(cat);
				}
			}
			children.put(val, new C45Tree(max_index));
		}
	}

	public String toString() {
		if (this.isLeaf) return Integer.toString(this.category);
		
		String rtn = "{(";
		rtn += Integer.toString(my_attribute);
		rtn += ") ";
		for (int val : children.keySet()) {
			rtn += Integer.toString(val);
			rtn += ": ";
			rtn += children.get(val).toString();
			rtn += "; ";
		}
		rtn += "}";
		return rtn;
	}

	public C45Tree(int category) {
		this.isLeaf = true;
		this.category = category;
	}

	public int classify(int[] arr) {
		if (isLeaf) return category;
		else return children.get(arr[my_attribute]).classify(arr);
	}

	public void printArray(double[] arr) {
		for (int a = 0; a < arr.length; a++) {
			System.out.println(arr[a]);
		}
	}
}