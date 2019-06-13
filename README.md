# DataVisualizer

Display data points onto a graph and categorizing them into groups.

To run: run DataVisualizer.java in an IDE

This program uses .tsd files. Each data point in a .tsd file has: @name label x,y. (tab seperated).
Import a sample .tsd file from resources/data or add your own data points.

After adding the data points, click Display Data and choose a grouping method, Clustering or Classification.
Choose an algorithm and set the config.

In the config,
  Max Iterations: how many times the algorithm runs.
  Update Interval: at what iteration does the graph update.
  Number of Clusters: the number of groups only for clustering algorithms.
  Continuous Run: automatically goes to the next iteration. If unchecked, user must manually press go to continue to the next iteration.

Press the green button to run the algorithm.

This program was made for CSE 219: Computer Science III with Professor Ritwik Banerjee
