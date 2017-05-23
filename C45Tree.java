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
		int categoryNum = input[0].length;
		float[] goodness = new float[categoryNum];
		// iterate through each of the categories, to see which one has the best goodness
		for (int i = 0; i < categoryNum; i++) {
			// calculate the goodness, need to loop through every row
			//create a dictonary to put everything into
			Map<Integer, Map<Integer, Float>> attributes = new HashMap<Integer, Map<Integer, Float>>();
			// this iterates through the rows
			for (int j = 0; j < input.length; j++) {
				// increment the appropriate value
				if (attributes.containsKey(input[j][i])) {
					float current_count = attributes.get(input[j][i]).get(output[j]);
					attributes.get(input[j][i]).put(output[j], current_count + weights[j]);
				}
				else {
					attributes.put(input[j][i], new HashMap<Integer, Float>());
					// this relies on Thomas adding tea flavors as numbers, not strings
					attributes.get(input[j][i]).put(0, (float)0);
					attributes.get(input[j][i]).put(1, (float)0);
					attributes.get(input[j][i]).put(2, (float)0);
					attributes.get(input[j][i]).put(output[j], weights[j]);
				}
				// System.out.println(input[j][i] + ", " + output[j] + ", " + attributes.get(input[j][i]).get(output[j]));
			}
			// now that we've gotten the counts of everything, we can calculate goodness
			Set<Integer> branchSet = attributes.keySet();
			int[] branches = new int[branchSet.size()];
			int counter = 0;
			for (Integer b : branchSet) {
				branches[counter++] = b;
			}
			float total_branches = 0;
			float correct_classifications = 0;
			// look at each branch, and figure out where it is supposed to be classified
			for (int k = 0; k < branches.length; k++) {
				Map<Integer, Float> branch = attributes.get(branches[k]);
				int max_index = -1;
				float max_value = -1;
				for (int l = 0; l < 3; l++) {
					// if there is a tie, then it will give us the first thing
					total_branches += branch.get(i);
					if (max_value == -1 || branch.get(l) > max_value) {
						max_index = l;
						// System.out.println("l " + branch.get(l));
						max_value = branch.get(l);
					}
				}
				correct_classifications += max_value;
			}
			// System.out.println(correct_classifications);
			float individual_goodness = (float) correct_classifications / total_branches / branches.length;
			goodness[i] = individual_goodness;
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
		System.out.println(max_index + " : " + max_value);

		my_attribute = max_index;
		// create a hashmap of value to child, based on what we split on
		// if it passes some kind of threshold, make the dummy tree
		// else make the proper tree


		// todo: make

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