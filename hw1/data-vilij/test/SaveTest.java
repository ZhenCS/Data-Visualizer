
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import dataprocessors.TSDProcessor.TSDException;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;

import java.nio.file.Paths;

public class SaveTest {

/*  Saving data from the text-area in the UI to a .tsd file.
        -data is not separated by tabs
        -instance name does not start with @
        -coordinates are not separated by a comma
        -coordinates are not numerical
        -coordinates are max doubles
        -coordinates are greater than max of doubles
        -coordinates are negative
        -label is null

 */

    @Test(expected = TSDException.class)
    public void noTabs() throws TSDException, IOException {
        String data = "@instance1 label 1,2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }

    @Test(expected = TSDException.class)
    public void nameCheck() throws TSDException, IOException {
        String data = "instance1\tlabel\t1,2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }

    @Test(expected = TSDException.class)
    public void coordinatesNotCommaSeparated() throws TSDException, IOException {
        String data = "@instance1\tlabel\t1 2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }

    @Test(expected = TSDException.class)
    public void coordinatesNotNumerical() throws TSDException, IOException {
        String data = "@instance1\tlabel\ta,b";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }

    @Test
    public void coordinatesMaxBoundaryValues() throws IOException, TSDException {
        String data = "@instance\tlabel\t" + Double.MAX_VALUE + "," + Double.MAX_VALUE;
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals(data, reader.readLine());
    }

    @Test
    public void coordinatesGreaterBoundaryValues() throws TSDException, IOException {
        String data = "@instance\tlabel\t" + (Double.MAX_VALUE + 10)+ "," + (Double.MAX_VALUE + 10);
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals(data, reader.readLine());
    }

    @Test
    public void coordinatesNegative() throws TSDException, IOException {
        String data = "@instance1\tlabel\t-10,-5";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals(data, reader.readLine());
    }

    @Test
    public void nullLabel() throws TSDException, IOException {
        String data = "@instance1\tlabel\t1,2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
        File file = Paths.get("savetests.txt").toFile();
        FileWriter writer = new FileWriter(file);
        writer.write(data);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals(data, reader.readLine());
    }
}