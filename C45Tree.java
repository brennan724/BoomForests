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

	private double entropy(Map<Integer, Map<Integer, Double>> counts) {
		// counts[value][category]
		double weight_sum = 0;
		for (int val : counts.keySet()) {
			for (int cat : counts.get(val).keySet())
				weight_sum += counts.get(val).get(cat);
		}

		double rtn = 0;
		for (int val : counts.keySet()) {
			double val_sum = 0;
			for (int cat : counts.get(val).keySet())
				val_sum += counts.get(val).get(cat);

			double ent = 0;
			for (int cat : counts.get(val).keySet()) {
				double p = counts.get(val).get(cat);
				if (p == 0 || p == 1) ent += 0;
				else ent += (p / val_sum) * Math.log(p / val_sum);
			}
			rtn += ent * val_sum / weight_sum;
		}

		return -1*rtn;
	}

	public C45Tree(int[][] input, int[] output, double[] weights, double leaf_accuracy, int depth) {
		double[] output_counts = new double[3];
		for (int i = 0; i < output.length; i++) {
			++output_counts[output[i]];
		}
		int maxIndex = 0;
		double maxValue = 0;
		for (int i = 0; i < output_counts.length; i++) {
			if (output_counts[i] > maxValue) {
				maxValue = output_counts[i];
				maxIndex = i;
			}
		}
		if ((double) maxValue / output.length >= leaf_accuracy) {
			this.isLeaf = true;
			this.category = output[maxIndex];
			return;
		}
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
			goodness[dim] = -1*entropy(counts);
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
			if (depth == 0) {
				children.put(val, new C45Tree(max_index));
			}
			else {
				// change inputs, create the subset
				int subset_length = 0;
				for (int j = 0; j < input.length; j++) {
					if (input[j][best_dim] == val) {
						++subset_length;
					}
				}
				int[][] input_subset = new int[subset_length][];
				int k = 0;
				for (int j = 0; j < input.length; j++) {
					if (input[j][best_dim] == val) {
						input_subset[k] = input[j];
						++k;
					}
				}
				children.put(val, new C45Tree(input_subset, output, weights, leaf_accuracy, depth - 1));
			}
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