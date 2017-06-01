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

	/**
	 * computes the entropy remaining in one variable conditioned on another
	 * the parameter "counts" takes teh form
	 * counts[variable_conditioned_on][variable_whose_entropy_we_are_looking at]
	 */
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

	/**
	 * @param inputs 2d array of observation's values so that inputs[X][Y] refers to dimension Y of person X
	 * @param output array of classes (categories) so that inputs[X] refers to person X
	 * @param weights array of weight to give to each observation
	 * @param leaf_accuracy the accuracy limit that, if a C4.5 tree get's above it, it stops recursively creating itself
	 * @param depth the depth left before max_depth is reached
	 */
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
				int[][] subset = subset_input(input, best_dim, val);
				children.put(val, new C45Tree(subset, output, weights, leaf_accuracy, depth - 1));
			}
		}
	}

	/**
	 * takes the given "input" array and returns a new 2d-array
	 * where each row's column "dim" has value "val"
	 */
	public int[][] subset_input(int[][] inputs, int dim, int val) {
		int subset_length = 0;
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i][dim] == val) {
				++subset_length;
			}
		}
		int[][] rtn = new int[subset_length][];
		int j = 0;
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i][dim] == val) {
				rtn[j] = inputs[i];
				++j;
			}
		}
		return rtn;
	}

	/**
	 * recurseively prunes using confidence_threshold as a p-value
	 */
	public void prune(double confidence_threshold, int[][] inputs, int[] classes, double[] weights) {
		if (isLeaf) return;

		boolean all_children_leaves = true;
		for (int val : children.keySet()) {
			if (!children.get(val).isLeaf) {
				all_children_leaves = false;
				break;
			}
		}

		// recursively prune children and (if all children are leaves) compute accuracy
		double[] error = new double[children.size()];
		double[] upper_bound = new double[children.size()];
		double[] weight_sum = new double[children.size()];
		double z = qnorm(confidence_threshold);
		int index = 0;
		for (int val : children.keySet()) {
			int subset_length = 0;
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i][this.my_attribute] == val) {
					++subset_length;
				}
			}
			int[][] sub_inputs = new int[subset_length][];
			int[] sub_classes = new int[subset_length];
			double[] sub_weights = new double[subset_length];
			int j = 0;
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i][this.my_attribute] == val) {
					sub_inputs[j] = inputs[i];
					sub_classes[j] = classes[i];
					sub_weights[j] = weights[i];
					weight_sum[index] += weights[i];
					++j;
				}
			}
			children.get(val).prune(confidence_threshold, sub_inputs, sub_classes, sub_weights);

			if (all_children_leaves) {
				error[index] = 1.0 - children.get(val).compute_accuracy(sub_inputs, sub_classes, sub_weights);
				upper_bound[index] = error[index] + z * Math.sqrt(error[index] * (1 - error[index]) / weight_sum[index]);
			}
			++index;
		}


		if (all_children_leaves) {
			// if I only have leaves for children, then try to prune self
			double[] counter = new double[3];
			for (int i = 0; i < classes.length; ++i)
				counter[classes[i]] += weights[i];

			double sum = 0;
			int max_class = -1;
			double maximum = -1;
			for (int i = 0; i < counter.length; ++i) {
				if (counter[i] > maximum) {
					max_class = i;
					maximum = counter[i];
				}
				sum += counter[i];
			}
			double error_sum = 1.0 - maximum / sum;

			double upper_bound_sum = 0;
			double weight_total = 0;
			for (int i = 0; i < error.length; ++i) {
				upper_bound_sum += weight_sum[i] * upper_bound[i];
				weight_total += weight_sum[i];
			}
			if (upper_bound_sum / weight_total >= error_sum) {
				// decrease in accuracy isn't statistically significant
				// I should purne myself
				this.children = null;
				this.isLeaf = true;
				this.category = max_class;
			}
		}
	}

	/*
	 * computes the accuracy of a node in a C4.5 Tree
	 */
	public double compute_accuracy(int[][] inputs, int[] classes, double[] weights) {
		double correct = 0.0;
		double weight_sum = 0.0;
		for (int i = 0; i < inputs.length; ++i) {
			int guess = classify(inputs[i]);
			if (guess == classes[i]) correct += weights[i];
			weight_sum += weights[i];
		}
		return correct / weight_sum;
	}

	// computed via R
	private static double[] qnorm_table = {-2.326348, -2.053749, -1.880794, -1.750686, -1.644854, -1.554774, -1.475791, -1.405072, -1.340755, -1.281552, -1.226528, -1.174987, -1.126391, -1.080319, -1.036433, -0.9944579, -0.9541653, -0.9153651, -0.8778963, -0.8416212, -0.8064212, -0.7721932, -0.7388468, -0.7063026, -0.6744898, -0.6433454, -0.612813, -0.5828415, -0.5533847, -0.5244005, -0.4958503, -0.4676988, -0.4399132, -0.4124631, -0.3853205, -0.3584588, -0.3318533, -0.3054808, -0.279319, -0.2533471, -0.227545, -0.2018935, -0.1763742, -0.1509692, -0.1256613, -0.1004337, -0.07526986, -0.05015358, -0.02506891, 0, 0.02506891, 0.05015358, 0.07526986, 0.1004337, 0.1256613, 0.1509692, 0.1763742, 0.2018935, 0.227545, 0.2533471, 0.279319, 0.3054808, 0.3318533, 0.3584588, 0.3853205, 0.4124631, 0.4399132, 0.4676988, 0.4958503, 0.5244005, 0.5533847, 0.5828415, 0.612813, 0.6433454, 0.6744898, 0.7063026, 0.7388468, 0.7721932, 0.8064212, 0.8416212, 0.8778963, 0.9153651, 0.9541653, 0.9944579, 1.036433, 1.080319, 1.126391, 1.174987, 1.226528, 1.281552, 1.340755, 1.405072, 1.475791, 1.554774, 1.644854, 1.750686, 1.880794, 2.053749, 2.326348};

	/*
	 * @param p - probability between 0.01 and 0.99
	 * @return - the z-score associated with the given probability
	 */
	private static double qnorm(double p) {
		if (p < 0.01) return Double.NEGATIVE_INFINITY;
		else if (p > 0.99) return Double.POSITIVE_INFINITY;
		int index = (int) (100.0 * p);
		double t = 100.0 * p - index;
		return (1-t) * qnorm_table[index-1] + t * qnorm_table[index];
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

	/*
	 constructor for the leaf node of a C4.5 tree
	 */
	public C45Tree(int category) {
		this.isLeaf = true;
		this.category = category;
	}

	public int classify(int[] arr) {
		if (this.isLeaf) return this.category;
		else return this.children.get(arr[this.my_attribute]).classify(arr);
	}

	public void printArray(double[] arr) {
		for (int a = 0; a < arr.length; a++) {
			System.out.println(arr[a]);
		}
	}
}