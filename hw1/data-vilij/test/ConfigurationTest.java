
import algorithms.KMeansClusterer;
import algorithms.RandomClassifier;
import algorithms.RandomClusterer;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurationTest {

/*  Run configuration values for classification and clustering. Again, the tests must include boundary value analyses.
        -max iterations is lower than 1
        -max iterations is 1
        -max iterations is max value of integers
        -max iterations is greater than max value of integers

        -update interval is lower than 1
        -update interval is 1
        -update interval is max value of integers
        -update interval is greater than max value of integers

        -cluster number is lower than 2
        -cluster number is 2
        -cluster number is 4
        -cluster number is greater than 4 and lower or equal to max value of integers
        -cluster number is greater than max value of integers


 */

    @Test
    public void maxIterationsNegative() {
        int updateInterval = -5;

        if(updateInterval < 1)
            updateInterval = 1;

        RandomClassifier classifier = new RandomClassifier(null, updateInterval, 2, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, updateInterval, 2, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, updateInterval, 2, 2,false);

        assertEquals(1, classifier.getMaxIterations());
        assertEquals(1, kClusterer.getMaxIterations());
        assertEquals(1, clusterer.getMaxIterations());
    }

    @Test
    public void maxIterationLowerBound() {
        RandomClassifier classifier = new RandomClassifier(null, 1, 2, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, 1, 2, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 1, 2, 2,false);

        assertEquals(1, classifier.getMaxIterations());
        assertEquals(1, kClusterer.getMaxIterations());
        assertEquals(1, clusterer.getMaxIterations());

    }

    @Test
    public void maxIterationUpperBound() {
        RandomClassifier classifier = new RandomClassifier(null, Integer.MAX_VALUE, 2, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, Integer.MAX_VALUE, 2, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, Integer.MAX_VALUE, 2, 2,false);

        assertEquals(Integer.MAX_VALUE, classifier.getMaxIterations());
        assertEquals(Integer.MAX_VALUE, kClusterer.getMaxIterations());
        assertEquals(Integer.MAX_VALUE, clusterer.getMaxIterations());
    }

    @Test
    public void maxIterationGreaterUpperBound() {
        int updateInterval = Integer.MAX_VALUE + 1;

        if(updateInterval < 1)
            updateInterval = 1;


        RandomClassifier classifier = new RandomClassifier(null, updateInterval, 2, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, updateInterval, 2, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, updateInterval, 2, 2,false);

        assertEquals(1, classifier.getMaxIterations());
        assertEquals(1, kClusterer.getMaxIterations());
        assertEquals(1, clusterer.getMaxIterations());
    }





    @Test
    public void updateIntervalNegative() {
        int updateInterval = -2;

        if(updateInterval < 1)
            updateInterval = 1;


        RandomClassifier classifier = new RandomClassifier(null, 2, updateInterval, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, updateInterval, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, updateInterval, 2,false);

        assertEquals(1, classifier.getUpdateInterval());
        assertEquals(1, kClusterer.getUpdateInterval());
        assertEquals(1, clusterer.getUpdateInterval());
    }

    @Test
    public void updateIntervalLowerBound() {
        RandomClassifier classifier = new RandomClassifier(null, 2, 1, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 1, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 1, 2,false);

        assertEquals(1, classifier.getUpdateInterval());
        assertEquals(1, kClusterer.getUpdateInterval());
        assertEquals(1, clusterer.getUpdateInterval());
    }

    @Test
    public void updateIntervalUpperBound() {
        RandomClassifier classifier = new RandomClassifier(null, 2, Integer.MAX_VALUE, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, Integer.MAX_VALUE, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, Integer.MAX_VALUE, 2,false);

        assertEquals(Integer.MAX_VALUE, classifier.getUpdateInterval());
        assertEquals(Integer.MAX_VALUE, kClusterer.getUpdateInterval());
        assertEquals(Integer.MAX_VALUE, clusterer.getUpdateInterval());
    }

    @Test
    public void updateIntervalGreaterUpperBound() {
        int updateInterval = Integer.MAX_VALUE + 1;

        if(updateInterval < 1)
            updateInterval = 1;


        RandomClassifier classifier = new RandomClassifier(null, 2, updateInterval, false);
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, updateInterval, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, updateInterval, 2,false);

        assertEquals(1, classifier.getUpdateInterval());
        assertEquals(1, kClusterer.getUpdateInterval());
        assertEquals(1, clusterer.getUpdateInterval());
    }




    @Test
    public void clusterNumberNegative() {
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 2, -2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 2, -2,false);

        assertEquals(2, kClusterer.getClusterNumber());
        assertEquals(2, clusterer.getClusterNumber());
    }

    @Test
    public void clusterNumberLowerBound() {
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 2, 2,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 2, 2,false);

        assertEquals(2, kClusterer.getClusterNumber());
        assertEquals(2, clusterer.getClusterNumber());
    }

    @Test
    public void clusterNumberUpperBound() {
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 2, 4,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 2, 4,false);

        assertEquals(4, kClusterer.getClusterNumber());
        assertEquals(4, clusterer.getClusterNumber());
    }

    @Test
    public void clusterNumberGreaterUpperBound() {
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 2, 5,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 2, 5,false);

        assertEquals(4, kClusterer.getClusterNumber());
        assertEquals(4, clusterer.getClusterNumber());
    }

    @Test
    public void clusterNumberGreaterMaxValue() {
        KMeansClusterer kClusterer = new KMeansClusterer(null, 2, 2, Integer.MAX_VALUE + 1,false);
        RandomClusterer clusterer = new RandomClusterer(null, 2, 2, Integer.MAX_VALUE + 1,false);

        assertEquals(2, kClusterer.getClusterNumber());
        assertEquals(2, clusterer.getClusterNumber());
    }



}