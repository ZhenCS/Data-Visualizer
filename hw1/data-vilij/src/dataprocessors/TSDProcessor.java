package dataprocessors;


import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final Map<String, String> dataLabels;
    private final Map<String, Point2D> dataPoints;

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

    public void createLine(XYChart<Number, Number> chart, String name, List<Integer> output){
        double lowerBoundX = dataPoints.values().stream().mapToDouble(Point2D::getX).min().getAsDouble();
        double upperBoundX = dataPoints.values().stream().mapToDouble(Point2D::getX).max().getAsDouble();


        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);

        double lowerBoundY = output.get(0) * lowerBoundX + output.get(2);
        double upperBoundY = output.get(0) * upperBoundX + output.get(2);

       // double lowerBoundY = (0 - (output.get(0) * lowerBoundX + output.get(2)))/output.get(1);
        //double upperBoundY = (0 - (output.get(0) * upperBoundX + output.get(2)))/output.get(1);

        XYChart.Data<Number, Number> lower = new XYChart.Data<>(lowerBoundX, lowerBoundY);
        XYChart.Data<Number, Number> upper = new XYChart.Data<>(upperBoundX, upperBoundY);

        series.getData().add(lower);
        series.getData().add(upper);
        chart.getData().add(series);

        //lower.getNode().setVisible(false);
        //upper.getNode().setVisible(false);
    }

    public void createAverageLine(XYChart<Number, Number> chart, String averageName) {
        int count = dataPoints.size();
        double sum = dataPoints.values().stream().mapToDouble(Point2D::getY).sum();

        double average = sum/count;
        double lowerBound = dataPoints.values().stream().mapToDouble(Point2D::getX).min().getAsDouble();
        double upperBound = dataPoints.values().stream().mapToDouble(Point2D::getX).max().getAsDouble();

       if(lowerBound == upperBound){
           lowerBound -= 10;
           upperBound += 10;
       }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(averageName);

        XYChart.Data<Number, Number> lower = new XYChart.Data<>(lowerBound, average);
        XYChart.Data<Number, Number> upper = new XYChart.Data<>(upperBound, average);

        series.getData().add(lower);
        series.getData().add(upper);
        chart.getData().add(series);

        lower.getNode().setVisible(false);
        upper.getNode().setVisible(false);

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
                }else{
                    try{
                        Double.parseDouble(pair[0]) ;
                        Double.parseDouble(pair[1]) ;
                    }catch(NumberFormatException e){
                        hadAnError.set(true);
                        throw new FormatException(i + 1);
                    }
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



    public void setMetaData(String tsdString){
        MetaDataBuilder builder = MetaDataBuilder.getMetaDataBuilder();
        Map<String, String> labels = new HashMap<>();
        AtomicInteger count = new AtomicInteger(0);
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String name = checkedname(list.get(0));
                        String label = list.get(1);

                        count.incrementAndGet();
                        if(!labels.containsValue(label) && !label.equals("null"))
                            labels.put(name, label);

                    } catch (InvalidDataNameException ignored) {}
                });

       builder.setInstanceNum(count.get()).setLabelName(labels).setSource(null);
    }

    public static class MetaDataBuilder{

        private int instanceNum;
        private int labelNum;
        private Map<String, String> labelNames;
        private String source;

        private static MetaDataBuilder builder;

        private MetaDataBuilder(){ }

        public static MetaDataBuilder getMetaDataBuilder(){
            if(builder == null)
                builder = new MetaDataBuilder();
            return builder;
        }

        public int getLabelNum(){
            return labelNum;
        }

        MetaDataBuilder setInstanceNum(int num){
            instanceNum = num;
            return this;
        }

        MetaDataBuilder setLabelName(Map<String, String> labels){
            labelNames = labels;
            labelNum = labelNames.size();
            return this;
        }

        MetaDataBuilder setSource(String src){
            source = src;
            return this;
        }

        public String build(){
            StringBuilder out = new StringBuilder(instanceNum + " instances with " + labelNum + " label(s).");

            if(source != null)
                out.append(" Located from \n ").append(source).append(".\n");

            out.append(" The labels are:\n");
            for(String name : labelNames.keySet())
                out.append("- ").append(labelNames.get(name)).append("\n");

            return out.toString();
        }
    }
}
