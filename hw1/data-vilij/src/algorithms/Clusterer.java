package algorithms;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses dataset to update visualizer
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm {

    DataSet dataset;
    final int numberOfClusters;
    int maxIterations;
    int updateInterval;
    int iteration;
    AtomicBoolean tocontinue;
    AtomicBoolean continuous;
    private AtomicBoolean empty;

    public int getNumberOfClusters() { return numberOfClusters; }

    public Clusterer(int k) {
        if (k < 2)
            k = 2;
        else if (k > 4)
            k = 4;
        numberOfClusters = k;
        empty = new AtomicBoolean(true);
    }
    protected abstract void runAlgorithm(int i);
    protected abstract void initialize();

    @Override
    public void run(){
        initialize();
        iteration = 0;
        while (iteration < maxIterations & tocontinue.get()) {
            synchronized (this) {
                while (!empty.get()) {
                    try { wait(); }
                    catch (InterruptedException e) { if(empty == null) { synchronized (this) {
                        setEmpty(false);
                        notifyAll();
                        return;
                    } } }
                }

                runAlgorithm(iteration);

                if(iteration >= maxIterations) break;
                if (iteration % updateInterval == 0) {
                    setEmpty(false);
                    notifyAll();
                }
            }

            try{ Thread.sleep(1000); }
            catch (InterruptedException e){ synchronized (this){
                setEmpty(false);
                notifyAll();
                return;
            } }

            iteration++;
        }
        synchronized (this){
            setEmpty(false);
            notifyAll();
        }
    }

    public DataSet getDataset() { return dataset;}
    public int getClusterNumber() { return numberOfClusters;}
    public int getIteration() { return iteration;}
    @Override
    public int getMaxIterations() {return maxIterations;}
    @Override
    public int getUpdateInterval() {return updateInterval;}
    @Override
    public boolean continuous() {return continuous.get();}
    @Override
    public AtomicBoolean getEmpty() { return empty;}
    @Override
    public void setEmptyNull() { empty = null;}
    @Override
    public void setEmpty(boolean b) {
        if(empty == null)
            empty = new AtomicBoolean();

        empty.set(b);
    }
}