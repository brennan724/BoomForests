Use the makefile to (um) make the program DataAnalyzer. DataAnalyzer uses AdaBoost to construct a "forest" of C4.5 trees to attempt to guess what kind of tea people prefer given other known properties.

Running the program looks like

java DataAnalyzer tea.csv 10 1 0.5 2

more generally the parameters are (in order)

tea.csv - the relative path to a csv file with the data
10      - number of trees to add to the forest
1       - the threshold, which, if the leaf accuracy reaches, we stop adding branches
0.5     - the p-value we require to not prune a branch [0.5 or lower implies no pruning; a value above 0.5 implies possible pruning]
2       - the maximum depth a tree can have

This file prints out the forest in text form. The forest takes the form
[(tree 1), (tree 2), ...]

and each tree takes the form

(AdaBoost-weight, {(dimension) value1: guess1, value2: guess2, ...})

So, for instance, this forest

[(1.0247942892650772, {(20) 0: 1; 1: 1; 2: 1; 3: 1; 4: 0; })
(0.5895574112475471, {(10) 0: 1; 1: 1; })
(0.5948240630612895, 0)
]

represents a "forest" with 3 C4.5 trees. The first tree has AdaBoost weight ("alpha") of about 1.025, and the other two trees have weights of approximately 0.59.

The first tree splits on dimension 20 (age_Q) and guesses "Earl Grey for all of them" except the 4th value in age_Q, where it guesses "black". The second tree does something vaguely  similar. The third tree has been completely pruned away, and so just always guesses "black".
