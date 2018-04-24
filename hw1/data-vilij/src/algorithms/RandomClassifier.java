package algorithms;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();
    private AtomicBoolean empty;

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will

    // currently, this value does not change after instantiation


    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        empty = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations; i++) {
            synchronized (this) {
                while (!empty.get()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if(empty == null) {
                            synchronized (this) {
                                setEmpty(false);
                                notifyAll();
                                return;
                            }
                        }
                    }
                }

                int xCoefficient = new Double(RAND.nextDouble() * 10).intValue();
                int yCoefficient = new Double(RAND.nextDouble() * 10).intValue();
                int constant = new Double(RAND.nextDouble() * 10).intValue();

                output = Arrays.asList(xCoefficient, yCoefficient, constant);

                if (i % updateInterval == 0) {
                    System.out.printf("Iteration number %d: ", i); //
                    flush();
                    setEmpty(false);
                    notifyAll();
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                    System.out.printf("Iteration number %d: ", i);
                    flush();
                    break;
                }
            }

            try{
                Thread.sleep(500);
            }catch (InterruptedException e){
                synchronized (this){
                    setEmpty(false);
                    notifyAll();
                    return;
                }
            }
        }

        synchronized (this){
            setEmpty(false);
            notifyAll();
        }

    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    public AtomicBoolean getEmpty() { return empty;}

    public void setEmpty(boolean b) {
        if(empty == null)
            empty = new AtomicBoolean();

        empty.set(b);
    }
    public void setEmpty() { empty = null;}

    /** A placeholder main method to just make sure this code runs smoothly
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }*/
}
