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
	float[] weights;
	float leaf_accuracy;
	List<C45Tree> children;
	boolean isLeaf;
	int category;

	// meta-data
	int my_attribute;
	Map<Integer, Integer> value_to_category;

	public C45Tree(int[][] input, int[] output, float[] weights, float leaf_accuracy, int depth) {
		this.isLeaf = false;
		this.input = input;
		this.output = output;
		this.weights = weights;
		this.children = new ArrayList<C45Tree>();
		C45Tree root = makeTree_Recursive(depth);
	}
	public C45Tree(int category) {
		this.isLeaf = true;
		this.category = category;
	}

	public C45Tree makeTree_Recursive(int depth) {
		int dimNum = input[0].length;
		System.out.println(dimNum);
		float[] goodness = new float[dimNum];
		// iterate through each of the categories, to see which one has the best goodness
		for (int dim = 0; dim < dimNum; dim++) {
			System.out.println("----------");
			// calculate the goodness, need to loop through every row
			// create a dictonary to put everything into
			// counts[value][category] = count
			Map<Integer, Map<Integer, Float>> counts = new HashMap<Integer, Map<Integer, Float>>();
			// this iterates through the rows
			for (int i = 0; i < input.length; i++) {
				// increment the appropriate value
				if (counts.containsKey(input[i][dim])) {
					float current_count = counts.get(input[i][dim]).get(output[i]);
					counts.get(input[i][dim]).put(output[i], current_count + weights[i]);
				}
				else {
					counts.put(input[i][dim], new HashMap<Integer, Float>());
					counts.get(input[i][dim]).put(0, (float)0);
					counts.get(input[i][dim]).put(1, (float)0);
					counts.get(input[i][dim]).put(2, (float)0);
					counts.get(input[i][dim]).put(output[i], weights[i]);
				}
			}
			for (int key : counts.keySet()) {
				System.out.println(key + " : " + counts.get(key));
			}
			// // counts[value][category] = count
			System.out.println("^^^^^^^^^^");
			// now that we've gotten the counts of everything, we can calculate goodness
			Set<Integer> valuesSet = counts.keySet();
			int[] values = new int[valuesSet.size()];
			int counter = 0;
			for (Integer v : valuesSet) {
				values[counter++] = v;
			}
			float total_branches = 0;
			float correct_classifications = 0;
			// look at each branch, and figure out where it is supposed to be classified
			for (int val = 0; val < values.length; val++) {
				Map<Integer, Float> category_counts = counts.get(values[val]);
				int max_index = -1;
				float max_value = -1;
				for (int cat = 0; cat < 3; cat++) {
					// if there is a tie, then it will give us the first thing
					System.out.println(dim + ", " + val + ", " + values[val] + " : " + cat + ", " + category_counts.get(cat));
					total_branches += category_counts.get(cat);
					if (max_value == -1 || category_counts.get(cat) > max_value) {
						max_index = cat;
						max_value = category_counts.get(cat);
					}
				}
				correct_classifications += max_value;
			}
			// System.out.println(correct_classifications);
			float individual_goodness = (float) correct_classifications / total_branches / values.length;
			System.out.println(individual_goodness);
			goodness[dim] = individual_goodness;
		}
		// now that I have the entire list of goodnesses, we need to figure out what to divide along
		int max_index = -1;
		float max_value = -1;
		for (int i = 0; i < goodness.length; i++) {
			if (max_value == -1 || goodness[i] > max_value) {
				max_index = i;
				max_value = goodness[i];
			}
		}
		printArray(goodness);
		// This is the index that I'm going to want to split on
		System.out.println(max_index + " : " + max_value);

		my_attribute = max_index;
		// create a hashmap of value to child, based on what we split on
		// if it passes some kind of threshold, make the dummy tree
		// else make the proper tree
		// we'll just make it a dummy tree and assign it a category


		return null;
		// now that I know the best index to split on, I can split on it
		// sort into categories based on the best category, and make a new tree
	}

	// public int classify(int[] arr) {
	// 	return value_to_category[arr[my_attribute]];
	// }

	public void printArray(float[] arr) {
		for (int a = 0; a < arr.length; a++) {
			System.out.println(arr[a]);
		}
	}
}