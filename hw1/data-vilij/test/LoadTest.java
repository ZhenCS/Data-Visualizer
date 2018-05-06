
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import dataprocessors.TSDProcessor.TSDException;
import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LoadTest {

/*  Parsing a single line of data in the TSD format to create an instance object. This must include tests for suitable boundary values.
        -data is empty
        -instance name does not start with @
        -file is not separated by tabs
        -coordinates not separated by a comma
        -coordinates are not numerical
        -coordinates are max values of doubles
        -coordinates are min values of doubles
        -coordinates are greater than max value of doubles
        -coordinates are lower than min value of doubles
        -coordinates are negative
        -label is null
 */

    @Test
    public void emptyData() throws Exception {
        String data = "";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
    }

    @Test(expected = TSDException.class)
    public void nameCheck() throws TSDException {
        String data = "instance1\tlabel\t1,2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
    }

    @Test(expected = TSDException.class)
    public void noTabs() throws TSDException {
        String data = "@instance label 1,2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
    }

    @Test(expected = TSDException.class)
    public void coordinatesNotCommaSeparated() throws TSDException {
        String data = "@instance\tlabel\t1 2";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
    }

    @Test(expected = TSDException.class)
    public void coordinatesNotNumerical() throws TSDException {
        String data = "@instance\tlabel\ta,b";
        TSDProcessor processor = new AppData(null).getProcessor();

        processor.processString(data);
    }

    @Test
    public void coordinateMaxBoundaryValues() throws TSDException {
        String data = "@instance\tlabel\t" + Double.MAX_VALUE + "," + Double.MAX_VALUE;
        TSDProcessor processor = new AppData(null).getProcessor();

        Point2D  point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        Map<String, Point2D> dataPoints = new HashMap<>();
        dataPoints.put("@instance", point);

        processor.processString(data);
        assertEquals(dataPoints, processor.getDataPoints());
    }

    @Test
    public void coordinateMinBoundaryValues() throws TSDException {
        String data = "@instance\tlabel\t" + Double.MIN_VALUE + "," + Double.MIN_VALUE;
        TSDProcessor processor = new AppData(null).getProcessor();

        Point2D  point = new Point2D(Double.MIN_VALUE, Double.MIN_VALUE);
        Map<String, Point2D> dataPoints = new HashMap<>();
        dataPoints.put("@instance", point);

        processor.processString(data);
        assertEquals(dataPoints, processor.getDataPoints());
    }

    @Test
    public void coordinateGreaterBoundaryValues() throws TSDException {
        String data = "@instance\tlabel\t" + (Double.MAX_VALUE + 10)+ "," + (Double.MAX_VALUE + 10);
        TSDProcessor processor = new AppData(null).getProcessor();

        Point2D  point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        Map<String, Point2D> dataPoints = new HashMap<>();
        dataPoints.put("@instance", point);

        processor.processString(data);
        assertEquals(dataPoints, processor.getDataPoints());
    }

    @Test
    public void coordinatesNegative() throws TSDException {
        String data = "@instance\tlabel\t-10,-5";
        TSDProcessor processor = new AppData(null).getProcessor();

        Point2D  point = new Point2D(Double.parseDouble("" + -10), Double.parseDouble("" + -5));
        Map<String, Point2D> dataPoints = new HashMap<>();
        dataPoints.put("@instance", point);

        processor.processString(data);
        assertEquals(dataPoints, processor.getDataPoints());
    }

    @Test
    public void nullLabel() throws TSDException {
        String data = "@instance\tnull\t10,5";
        TSDProcessor processor = new AppData(null).getProcessor();
        Map<String, String> dataLabels = new HashMap<>();
        dataLabels.put("@instance", "null");

        processor.processString(data);
        assertEquals(dataLabels, processor.getDataLabels());
    }

}