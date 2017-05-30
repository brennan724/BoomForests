
import java.util.ArrayList;

public class Adaboost {
	private int num_classes;
	private ArrayList<C45Tree> learners;
	private double[] learner_weights;

	// http://ww.web.stanford.edu/~hastie/Papers/SII-2-3-A8-Zhu.pdf
	public Adaboost(int[][] inputs, int[] classes, int num_learners) {
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
			C45Tree new_learner = new C45Tree(inputs, classes, weights, 1.0, 0);

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
