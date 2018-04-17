package algorithms;

import java.util.List;

/**
 * This interface provides a way to run an algorithm
 * on a thread as a {@link java.lang.Runnable} object.
 *
 * @author Ritwik Banerjee
 */
public interface Algorithm extends Runnable {

    int getMaxIterations();

    int getUpdateInterval();

    boolean tocontinue();

    boolean isConfigured();

    void setIsConfigured();

    void setMaxIterations(int iterations);

    void setUpdateInterval(int interval);

    void setToContinue(boolean toContinue);

    void setDataSet(DataSet set);

    DataSet getDataSet();

    boolean isRunning();

    void setIsRunning(boolean isRunning);

}