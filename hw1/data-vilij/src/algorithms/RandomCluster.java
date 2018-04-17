package algorithms;

import java.util.concurrent.atomic.AtomicBoolean;

public class RandomCluster extends Cluster {

    private int maxIterations;
    private int updateInterval;

    // currently, this value does not change after instantiation
    private AtomicBoolean tocontinue;

    public RandomCluster(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean toContinue) {
        DataSet dataset1 = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        tocontinue = new AtomicBoolean(toContinue);
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    @Override
    public void setMaxIterations(int iterations) {
        maxIterations = iterations;
    }

    @Override
    public void setUpdateInterval(int interval) {
        updateInterval = interval;
    }

    @Override
    public void setToContinue(boolean toContinue) {
        tocontinue = new AtomicBoolean(toContinue);
    }

    @Override
    public void setDataSet(DataSet set) {

    }

    @Override
    public DataSet getDataSet() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void setIsRunning(boolean isRunning) {

    }

    @Override
    public void run() {

    }
}
