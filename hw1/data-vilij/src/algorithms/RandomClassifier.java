package algorithms;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

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
        isRunning = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        System.out.println("2");
        isRunning.set(true);
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            int xCoefficient = new Double(RAND.nextDouble() * 10).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 10).intValue();
            int constant = new Double(RAND.nextDouble() * 10).intValue();

            if(yCoefficient == 0)
                yCoefficient = 1;

            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
                //notifyRunning();
                try{
                    Thread.sleep(1000/updateInterval);
                }catch (InterruptedException e){
                    return;
                }
            }
        }

        isRunning.set(false);
    }

    private synchronized void notifyRunning(){
        try{
            wait();
        }catch (InterruptedException e){

        }
        isRunning.set(false);
        notifyAll();
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }*/
}
