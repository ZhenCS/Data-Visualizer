package algorithms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {


    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean continuous) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(true);
        this.continuous = new AtomicBoolean(continuous);
    }


    @Override
    protected void runAlgorithm(int i) {
        dataset.getLocations().forEach((instanceName, location) -> {
            int randomLabel = new Random().nextInt(numberOfClusters);
            dataset.getLabels().put(instanceName, Integer.toString(randomLabel));
        });

    }

    @Override
    protected void initialize() {

    }
}
