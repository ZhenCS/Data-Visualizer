package algorithms;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This interface provides a way to run an algorithm
 * on a thread as a {@link java.lang.Runnable} object.
 *
 * @author Ritwik Banerjee
 */
public interface Algorithm extends Runnable {

    int getMaxIterations();
    int getUpdateInterval();
    boolean continuous();

    int getIteration();
    AtomicBoolean getEmpty();
    void setEmptyNull();
    void setEmpty(boolean b);
}