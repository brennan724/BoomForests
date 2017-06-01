
Use the makefile to (um) make the program DataAnalyzer. DataAnalyzer uses AdaBoost to construct a "forest" of C4.5 trees to attempt to guess what kind of tea people prefer given other known properties.

Running the program looks like

java DataAnalyzer tea.csv 10 1 0.5 2

more generally the parameters are (in order)

tea.csv - the relative path to a csv file with the data
10      - number of trees to add to the forest
1       - the threshold, which, if the leaf accuracy reaches, we stop adding branches
0.5     - the p-value we require to not prune a branch [0.5 or lower implies no pruning; a value above 0.5 implies possible pruning]
2       - the maximum depth a tree can have
