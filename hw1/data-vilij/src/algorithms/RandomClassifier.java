package algorithms;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will

    // currently, this value does not change after instantiation


    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        super();
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.continuous = new AtomicBoolean(tocontinue);
    }


    @Override
    public void runAlgorithm(int i) {

        int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
        int yCoefficient = 10;
        int constant     = RAND.nextInt(11);

        output = Arrays.asList(xCoefficient, yCoefficient, constant, i);
    }

    @Override
    protected void initialize() {

    }



}
