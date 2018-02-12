package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;


/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;
    private static final String SEPARATOR = "/";
    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshotPath;

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);

        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

        scrnshotPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
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
        VBox inputBox = new VBox();
        HBox displayBox = new HBox();

        inputBox.setMaxWidth(200);


        initInput();
        initChart();

        inputBox.getChildren().addAll(textArea, displayButton);
        displayBox.getChildren().addAll(inputBox, chart);
        appPane.getChildren().add(displayBox);
    }

    private void initInput(){
        hasNewText = false;

        textArea = new TextArea();
        textArea.setPrefHeight(100.0);
        textArea.setOnKeyReleased(e -> hasNewText = ((AppActions)applicationTemplate.getActionComponent()).handleTextArea(textArea.getText()));

        displayButton = new Button();
        displayButton.setText(applicationTemplate.manager.getPropertyValue(DISPLAY_BUTTON.name()));
    }

    private void initChart(){
        double tickCount = 1;
        double lowerBound = 0;
        double upperBoundX = 10;
        double upperBoundY = 10;

        NumberAxis xAxis = new NumberAxis(lowerBound, upperBoundX, tickCount);
        NumberAxis yAxis = new NumberAxis(lowerBound, upperBoundY, tickCount);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        chart = new ScatterChart<>(xAxis, yAxis);
        chart.setPrefWidth(800);
    }

    private void setWorkspaceActions() {
        displayButton.setOnAction(e -> display());
    }

    private void display(){
        if(hasNewText){
            applicationTemplate.getDataComponent().clear();
            chart.getData().removeAll(chart.getData());

            if(((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText()))
                ((AppData) applicationTemplate.getDataComponent()).displayData();

            hasNewText = false;
        }

    }

    public String getText(){
        return textArea.getText();
    }

    public void disableAppUIButtons(boolean bool){
        ((AppUI) applicationTemplate.getUIComponent()).newButton.setDisable(bool);
        ((AppUI) applicationTemplate.getUIComponent()).saveButton.setDisable(bool);
    }
}
