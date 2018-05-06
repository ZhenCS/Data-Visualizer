package algorithms;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * uses output to update the visualizer
 *
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
    static final Random RAND = new Random();
    List<Integer> output;
    DataSet dataset;

    int maxIterations;
    int updateInterval;
    private int iteration;
    AtomicBoolean continuous;
    private AtomicBoolean empty;

    public Classifier(){
        empty = new AtomicBoolean(true);
    }

    public int getIteration() { return iteration; }
    public List<Integer> getOutput() { return output; }
    @Override
    public int getMaxIterations() { return maxIterations; }
    @Override
    public int getUpdateInterval() { return updateInterval;}
    @Override
    public boolean continuous() {return continuous.get(); }
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


    //Return boolean, true to update chart
    protected abstract void runAlgorithm(int i);
    protected  abstract void initialize();
    @Override
    public void run(){
        initialize();
        for (iteration = 1; iteration <= maxIterations;) {
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

            try{ Thread.sleep(500); }
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

    /*private void flush(int i) {
        System.out.printf("Iteration number %d: ", i);
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }*/
}
