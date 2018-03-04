package dataprocessors;


import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {


    static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    static class DuplicateNameException extends Exception {

        DuplicateNameException(String name) {
            super(String.format("Duplicate name '%s'.", name));
        }
    }

    static class FormatException extends Exception {

        FormatException(int line) {
            super(String.format("Invalid format at line: '%s'.", line));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;

    TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();


        try{
            hadAnError.set(checkFormat(tsdString));
        } catch(Exception e){
            errorMessage.setLength(0);
            errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
            hadAnError.set(true);
        }


        if(!hadAnError.get()){
            Stream.of(tsdString.split("\n"))
                    .map(line -> Arrays.asList(line.split("\t")))
                    .forEach(list -> {
                        try {
                            String   name  = checkedname(list.get(0));
                            String   label = list.get(1);

                            String[] pair  = list.get(2).split(",");
                            Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));

                            if(dataLabels.containsKey(name)) throw new DuplicateNameException(name);

                            dataLabels.put(name, label);
                            dataPoints.put(name, point);
                        } catch (Exception e) {
                            errorMessage.setLength(0);

                            errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                            hadAnError.set(true);
                        }
                    });
        }

        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data<Number, Number> data = new XYChart.Data<>(point.getX(), point.getY());
                series.getData().add(data);
            });
            chart.getData().add(series);
        }
    }

    public void createAverageLine(XYChart<Number, Number> chart, String averageName) {

        double sum;
        int count = dataPoints.size();


        //sum = Stream.of(chart.getData()).flatMap(List::stream).map(series -> series.getData()).flatMap(List::stream).mapToDouble(data -> data.getYValue().doubleValue()).sum();
        //count = Stream.of(chart.getData()).flatMap(List::stream).map(series -> series.getData()).flatMap(List::stream).count();

        sum = dataPoints.values().stream().mapToDouble(Point2D::getY).sum();

        double average = sum/count;

        double lowerBound = dataPoints.values().stream().mapToDouble(Point2D::getX).min().getAsDouble();
        double upperBound = dataPoints.values().stream().mapToDouble(Point2D::getX).max().getAsDouble();

       if(lowerBound == upperBound){
           lowerBound -= 10;
           upperBound += 10;
       }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(averageName);
        series.getData().add(new XYChart.Data<>(lowerBound, average));
        series.getData().add(new XYChart.Data<>(upperBound, average));
        chart.getData().add(series);

    }

    public void createTooltips(LineChart<Number, Number> chart) {

        chart.getData().stream().map(XYChart.Series::getData).flatMap(List::stream).forEach(data -> {
            Point2D  point = new Point2D(data.getXValue().doubleValue(), data.getYValue().doubleValue());
            dataPoints.entrySet().stream().filter(entry -> entry.getValue().equals(point)).forEach(entry -> Tooltip.install(data.getNode(), new Tooltip(entry.getKey())));
            data.getNode().setOnMouseEntered(event -> chart.setCursor(Cursor.HAND));
            data.getNode().setOnMouseExited(event -> chart.setCursor(Cursor.DEFAULT));
        });
    }



    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }

    private boolean checkFormat(String tsdString) throws FormatException{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        String[] list = tsdString.split("\n");
        for(int i = 0; i < list.length; i++){

            String[] line = list[i].split("\t");

            if(line.length != 3){
                hadAnError.set(true);
                throw new FormatException(i + 1);
            }else{
                if(line[1].equals("")){
                    hadAnError.set(true);
                    throw new FormatException(i + 1);
                }

                String[] pair = line[2].split(",");
                if(pair.length != 2 || pair[1].equals("")){
                    hadAnError.set(true);
                    throw new FormatException(i + 1);
                }

            }
        }

        return hadAnError.get();
    }

    public void checkForErrors(String tsdString) throws Exception{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        try{
            hadAnError.set(checkFormat(tsdString));
        } catch(Exception e){
            errorMessage.setLength(0);
            errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
            hadAnError.set(true);
        }

        Map<String, String>  dataLabels2 = new HashMap<>();


        if(!hadAnError.get()) {
            Stream.of(tsdString.split("\n"))
                    .map(line -> Arrays.asList(line.split("\t")))
                    .forEach(list -> {
                        try {
                            String name = checkedname(list.get(0));
                            String label = list.get(1);

                            if (dataLabels2.containsKey(name)) throw new DuplicateNameException(name);

                            dataLabels2.put(name, label);
                        } catch (Exception e) {
                            errorMessage.setLength(0);
                            errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                            hadAnError.set(true);
                        }
                    });
        }

        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }
}
