package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }
    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.setText("");
        applicationTemplate.getDataComponent().clear();
        chart.getData().removeAll(chart.getData());
        ((AppUI) applicationTemplate.getUIComponent()).newButton.setDisable(true);
        ((AppUI) applicationTemplate.getUIComponent()).saveButton.setDisable(true);
    }

    private void layout() {
        // TODO for homework 1
        VBox textBox = new VBox();
        HBox hBox = new HBox();

        textBox.setMaxWidth(200);

        textArea = new TextArea();
        textArea.setPrefHeight(100.0);

        displayButton = new Button();
        displayButton.setText("Display");

        double tickCount = 1;
        double lowerBound = 0;
        double upperBoundX = 10;
        double upperBoundY = 10;

        NumberAxis xAxis = new NumberAxis(lowerBound, upperBoundX, tickCount);
        NumberAxis yAxis = new NumberAxis(lowerBound, upperBoundY, tickCount);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        chart = new ScatterChart(xAxis, yAxis);
        chart.setPrefWidth(800);

        textBox.getChildren().addAll(textArea, displayButton);
        hBox.getChildren().addAll(textBox, chart);
        appPane.getChildren().add(hBox);
    }

    private void setWorkspaceActions() {
        displayButton.setOnAction(e -> display());
    }

    private void display(){
        if(!textArea.getText().equals("")){
            applicationTemplate.getDataComponent().clear();
            chart.getData().removeAll(chart.getData());

            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
            ((AppData) applicationTemplate.getDataComponent()).displayData();

            ((AppUI) applicationTemplate.getUIComponent()).newButton.setDisable(false);
            ((AppUI) applicationTemplate.getUIComponent()).saveButton.setDisable(false);
        }

    }
}
