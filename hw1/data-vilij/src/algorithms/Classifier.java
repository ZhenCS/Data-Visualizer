package algorithms;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An abstract class for classification algorithms. The output
 * for these algorithms is a straight line, as described in
 * Appendix C of the software requirements specification
 * (SRS). The {@link #output} is defined with extensibility
 * in mind.
 *
 * @author Ritwik Banerjee
 */
public abstract class Classifier implements Algorithm {

    /**
     * See Appendix C of the SRS. Defining the output as a
     * list instead of a triple allows for future extension
     * into polynomial curves instead of just straight lines.
     * See 3.4.4 of the SRS.
     */
    List<Integer> output;
    boolean isConfigured;
    DataSet dataset;

    int maxIterations;
    int updateInterval;
    AtomicBoolean tocontinue;
    AtomicBoolean isRunning;

    public List<Integer> getOutput() { return output; }
    @Override
    public boolean isConfigured() { return isConfigured;}
    @Override
    public void setIsConfigured() { isConfigured = true;}
    @Override
    public int getMaxIterations() { return maxIterations; }
    @Override
    public int getUpdateInterval() { return updateInterval;}
    @Override
    public boolean tocontinue() { return tocontinue.get(); }
    @Override
    public void setMaxIterations(int iterations) { maxIterations = iterations; }
    @Override
    public void setUpdateInterval(int interval) { updateInterval = interval; }
    @Override
    public void setToContinue(boolean toContinue) { this.tocontinue = new AtomicBoolean(toContinue); }
    @Override
    public void setDataSet(DataSet set) { dataset = set; }
    @Override
    public DataSet getDataSet() { return dataset; }
    @Override
    public boolean isRunning() { return isRunning.get(); }

    public void setIsRunning(boolean isRunning) { this.isRunning.set(isRunning); }

}
