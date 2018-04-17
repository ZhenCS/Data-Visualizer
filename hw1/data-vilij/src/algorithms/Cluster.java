package algorithms;

import java.util.List;

public abstract class Cluster implements Algorithm{
    private List<Integer> output;
    private int clusterNumber;
    private boolean isConfigured;


    public List<Integer> getOutput() { return output; }

    public void setClusterNumber(int num) {clusterNumber = num;}

    public int getClusterNumber() {return clusterNumber;}

    @Override
    public boolean isConfigured() { return isConfigured;}
    @Override
    public void setIsConfigured() { isConfigured = true;}
}
