package algorithms;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

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
    static final Random RAND = new Random();
    List<Integer> output;
    private boolean isConfigured;
    DataSet dataset;

    int maxIterations;
    int updateInterval;
    AtomicBoolean tocontinue;
    AtomicBoolean empty;

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
    public boolean getToContinue() { return tocontinue.get(); }
    @Override
    public void setDataSet(DataSet set) { dataset = set; }

    public AtomicBoolean getEmpty() { return empty;}

    public void setEmpty() { empty = null;}

    public void setEmpty(boolean b) {
        if(empty == null)
            empty = new AtomicBoolean();

        empty.set(b);
    }




    //Return boolean, true to update chart
    protected abstract void runAlgorithm(int i);

    @Override
    public void run(){
        for (int i = 1; i <= maxIterations; i++) {
            synchronized (this) {
                while (!empty.get()) {
                    try { wait(); }
                    catch (InterruptedException e) { if(empty == null) { synchronized (this) {
                            setEmpty(false);
                            notifyAll();
                            return;
                        } } }
                }

                runAlgorithm(i);
                if(i >= maxIterations) break;
                if (i % updateInterval == 0) {
                    //flush(i);
                    setEmpty(false);
                    notifyAll();
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                    //flush(i);
                    break;
                }
            }

            try{ Thread.sleep(500); }
            catch (InterruptedException e){ synchronized (this){
                    setEmpty(false);
                    notifyAll();
                    return;
                } }
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
