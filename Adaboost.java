
import java.util.ArrayList;

public class Adaboost {
	private int num_classes;
	private ArrayList<C45Tree> learners;
	private double[] learner_weights;

	// Zhu, J., Zou, H., Rosset, S., & Hastie, T. (2009). Multi-class adaboost. Statistics and its Interface, 2(3), 349-360.

	// Alvarez, S. (n.d.). Decision Tree Pruning based on Confidence Intervals (as in C4.5). Retrieved May 31, 2017, from http://www.cs.bc.edu/~alvarez/ML/statPruning.html

	/**
	 * @param inputs 2d array of observation's values so that inputs[X][Y] refers to dimension Y of person X
	 * @param classes array of classes (categories) so that inputs[X] refers to person X
	 * @param num_learners number of weak learners (C4.5 trees)
	 * @param leaf_accuracy the accuracy limit that, if a C4.5 tree get's above it, it stops recursively creating itself
	 * @param confidence_threshold the p-value used for pruning
	 * @param max_depth the maximum depth for the C4.5 trees
	 */
	public Adaboost(int[][] inputs, int[] classes, int num_learners, double leaf_accuracy, double confidence_threshold, int max_depth) {
		int max_class = -1;
		for (int i = 0; i < classes.length; ++i) {
			if (classes[i] > max_class) max_class = classes[i];
		}
		num_classes = max_class + 1;

		double[] weights = new double[inputs.length];
		for (int i = 0; i < weights.length; ++i) weights[i] = (double) 1.0;

		learners = new ArrayList<C45Tree>(num_learners);
		learner_weights = new double[num_learners];
		for (int index = 0; index < num_learners; ++index) {
			// train weak learner
			C45Tree new_learner = new C45Tree(inputs, classes, weights, leaf_accuracy, max_depth);
			new_learner.prune(confidence_threshold, inputs, classes, weights);

			// compute error
			double error = 0;
			for (int i = 0; i < inputs.length; ++i) {
				int guess = new_learner.classify(inputs[i]);
				if (guess != classes[i]) error += weights[i];
			}
			error /= inputs.length;

			// compute alpha
			double alpha = 0.5 * Math.log((1-error)/error) + Math.log(num_classes - 1);

			// update weights
			for (int i = 0; i < weights.length; ++i) {
				int guess = new_learner.classify(inputs[i]);
				if (guess != classes[i])
					weights[i] *= Math.exp(alpha);
			}

			// normalize weights
			double sum = 0;
			for (int i = 0; i < weights.length; ++i) sum += weights[i];
			for (int i = 0; i < weights.length; ++i)
				weights[i] *= weights.length / sum;

			// add tree
			learners.add(new_learner);
			learner_weights[index] = alpha;
			if (error == 0) {
				// If perfect, just stop
				return;
			}
		}
	}

	public String toString() {
		String rtn = "[";
		for (int i = 0; i < learners.size(); ++i) {
			rtn += '(';
			rtn += Double.toString(learner_weights[i]);
			rtn += ", ";
			rtn += learners.get(i).toString();
			rtn += ")\n";
		}
		rtn += ']';
		return rtn;
	}

	/*
	 * when given an observation (array of values), this classifies it according to AdaBoost
	 */
	public int classify(int[] input) {
		double sum = 0;
		double[] guesses = new double[num_classes];
		for (int i = 0; i < learners.size(); ++i) {
			int guess = learners.get(i).classify(input);
			guesses[guess] += learner_weights[i];
		}

		int best_guess = -1;
		double best_guess_value = -1000.0;
		for (int i = 0; i < guesses.length; ++i) {
			if (guesses[i] > best_guess_value) {
				best_guess = i;
				best_guess_value = guesses[i];
			}
		}
		
		return best_guess;
	}
}
