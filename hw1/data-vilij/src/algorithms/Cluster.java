package algorithms;

import java.util.List;

public abstract class Cluster implements Algorithm{
    protected List<Integer> output;
    protected int clusterNumber;

    public List<Integer> getOutput() { return output; }

    public void setClusterNumber(int num) {clusterNumber = num;}

    public int getClusterNumber() {return clusterNumber;}
}
