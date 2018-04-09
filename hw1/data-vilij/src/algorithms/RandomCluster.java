package algorithms;

public class RandomCluster extends Cluster {
    @Override
    public int getMaxIterations() {
        return 0;
    }

    @Override
    public int getUpdateInterval() {
        return 0;
    }

    @Override
    public boolean tocontinue() {
        return false;
    }

    @Override
    public void run() {

    }
}
