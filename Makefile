all:
	javac DataAnalyzer.java; javac C45Tree.java; java DataAnalyzer tea.csv 10 1.0 0.5 1
