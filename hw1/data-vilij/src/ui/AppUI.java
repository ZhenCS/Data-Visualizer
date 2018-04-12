package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.AlgorithmTypes;
import algorithms.RandomClassifier;
import com.sun.prism.paint.Color;
import dataprocessors.AppData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static settings.AppPropertyTypes.*;



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
    private Button                      scrnshotButton; // toolbar button to take a screenshot of the data
    private Button                      runButton;
    private Button                      stopButton;
    private LineChart<Number, Number>   chart;          // the chart where data will be displayed
    private Button                      doneButton;  // workspace button to display data on the chart
    private Button                      editButton;  // workspace button to display data on the chart
    private TextArea                    textArea;       // text area for new data input
    private boolean                     hasNewText;     // whether or not the text area has any new data since last display
    private String                      scrnshotPath;
    private String                      runPath;
    private String                      stopPath;
    private String                      configPath;
    private ComboBox<String>            algorithmTypes;
    private VBox                        algorithmSelection;
    private VBox                        leftPanel;
    private Text                        metaData;


    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);

        PropertyManager manager = applicationTemplate.manager;
        scrnshotPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(SCREENSHOT_ICON.name()));

        runPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(RUN_ICON.name()));

        stopPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(STOP_ICON.name()));

        configPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(CONFIG_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        runButton = setToolbarButton(runPath, manager.getPropertyValue(RUN_TOOLTIP.name()), true);
        stopButton = setToolbarButton(stopPath, manager.getPropertyValue(STOP_TOOLTIP.name()), true);

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
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException e1) {
                ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                dialog.show(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR.name()));
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();

        toggleLeftPane(false);
        disableNewButton(false);
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.setText("");
        applicationTemplate.getDataComponent().clear();
        chart.getData().removeAll(chart.getData());
        disableAppUIButtons(true);
    }

    private void layout() {
        // TODO for homework 1

        PropertyManager manager = applicationTemplate.manager;

        String cssPath = manager.getPropertyValue(CHART_CSS_PATH.name());
        getPrimaryScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

        NumberAxis      xAxis   = new NumberAxis();
        NumberAxis      yAxis   = new NumberAxis();

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setHorizontalZeroLineVisible(false);


        leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight); //0.69
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight);

        Text leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname       = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize       = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();
        textArea.setOnKeyReleased(e -> hasNewText = ((AppActions)applicationTemplate.getActionComponent()).handleTextArea(textArea.getText()));

        HBox processButtonsBox = new HBox();
        doneButton = new Button(manager.getPropertyValue(AppPropertyTypes.DONE_BUTTON.name()));
        editButton = new Button(manager.getPropertyValue(AppPropertyTypes.EDIT_BUTTON.name()));

        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        processButtonsBox.getChildren().addAll(doneButton, editButton);

        HBox metaDataBox = new HBox();
        metaData = new Text();
        metaDataBox.getChildren().add(metaData);

        HBox algorithmBox = new HBox();
        algorithmTypes = new ComboBox<>();
        algorithmTypes.getItems().add("Clustering");
        algorithmTypes.setVisible(false);

        algorithmSelection = new VBox();

        algorithmBox.getChildren().addAll(algorithmTypes/*, algorithm1*/);

        AnchorPane algorithmButtons = new AnchorPane();
        HBox runStop = new HBox();
        runStop.getChildren().addAll(runButton, stopButton);
        algorithmButtons.getChildren().add(runStop);
        AnchorPane.setBottomAnchor(runStop, 70.0);
        AnchorPane.setLeftAnchor(runStop, 0.0);
        VBox.setVgrow(algorithmButtons, Priority.ALWAYS);
        //algorithmButtons.setStyle("-fx-background-color: red");

        leftPanel.setStyle("-fx-background-color: lightgrey");
        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaDataBox, algorithmBox, algorithmSelection, algorithmButtons);

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);


    }

    public void refreshAlgorithms(){
        showAlgorithmsOfType(AlgorithmTypes.valueOf(algorithmTypes.getValue().toUpperCase()));
    }

    private void showAlgorithmsOfType(AlgorithmTypes type){
        algorithmSelection.getChildren().clear();
        ToggleGroup aGroup = new ToggleGroup();

        ArrayList<Algorithm> typeList = ((DataVisualizer) applicationTemplate).getAlgorithmComponent().getAlgorithmOfType(type);
        if(typeList != null && typeList.size() > 0)
            typeList.stream().forEach(algorithm -> {
                HBox hbox = new HBox();
                RadioButton radio = new RadioButton(algorithm.getClass().getSimpleName());
                radio.setToggleGroup(aGroup);
                radio.setStyle("-fx-font-size: 13px");

                PropertyManager manager = applicationTemplate.manager;
                Button config = setToolbarButton(configPath, manager.getPropertyValue(CONFIG_TOOLTIP.name()), false);
                config.setOnAction(e -> ((DataVisualizer) applicationTemplate).getAlgorithmComponent().configAlgorithm(algorithm));
                config.setStyle("-fx-border: none; -fx-background-color: transparent;");
                config.setOnMouseEntered(event -> getPrimaryWindow().getScene().setCursor(Cursor.HAND));
                config.setOnMouseExited(event -> getPrimaryWindow().getScene().setCursor(Cursor.DEFAULT));

                final StackPane space = new StackPane();
                HBox.setHgrow(space, Priority.ALWAYS);

                hbox.getChildren().addAll(radio,space,config);

                hbox.setAlignment(Pos.CENTER_LEFT);
                algorithmSelection.getChildren().add(hbox);
            });
    }

    private void setWorkspaceActions() {
        doneButton.setOnAction(e -> toggleAlgorithm());
        editButton.setOnAction(e -> editUIUpdate());
        algorithmTypes.valueProperty().addListener((observable, oldValue, newValue) -> showAlgorithmsOfType(AlgorithmTypes.valueOf(newValue.toUpperCase())));
    }

    private void toggleAlgorithm(){
        applicationTemplate.getDataComponent().clear();
        chart.getData().removeAll(chart.getData());

        String data = textArea.getText().trim();
        String bufferText = ((AppData) applicationTemplate.getDataComponent()).getBufferTextArea();
        if(bufferText != null)
            data += "\n" + bufferText;

        if(((AppData) applicationTemplate.getDataComponent()).loadData(data)){
            ((AppData) applicationTemplate.getDataComponent()).displayData();

            doneUIUpdate();
        }else{
            setMetaDataText("");
            disableSaveButton(true);
        }
    }

    public void doneUIUpdate(){
        doneButton.setDisable(true);
        editButton.setDisable(false);
        textArea.setDisable(true);
        algorithmTypes.setVisible(true);
        algorithmSelection.setVisible(true);
    }

    public void editUIUpdate(){
        doneButton.setDisable(false);
        editButton.setDisable(true);
        textArea.setDisable(false);
        algorithmTypes.setVisible(false);
        algorithmSelection.setVisible(false);
        metaData.setText("");
    }

    private void display(){
        if(hasNewText){
            applicationTemplate.getDataComponent().clear();
            chart.getData().removeAll(chart.getData());

            String bufferText = ((AppData) applicationTemplate.getDataComponent()).getBufferTextArea();

            if(bufferText == null){
                if(((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText())) {
                    ((AppData) applicationTemplate.getDataComponent()).displayData();

                    if(chart.getData().size() > 0)
                        disableScreenshotButton(false);
                }
                else
                    disableSaveButton(true);
            }else{
                if(((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText().trim() + "\n" + bufferText)) {
                    ((AppData) applicationTemplate.getDataComponent()).displayData();

                    if(chart.getData().size() > 0)
                        disableScreenshotButton(false);
                }
                else
                    disableSaveButton(true);
            }


            hasNewText = false;
        }

    }

    public void toggleTextArea(boolean b){
        textArea.setDisable(b);
    }

    public String getText(){
        return textArea.getText();
    }

    public TextArea getTextArea(){
        return textArea;
    }

    public void setHasNewText(boolean b){
        hasNewText = b;
    }

    public void disableAppUIButtons(boolean b){
        disableSaveButton(b);
        disableNewButton(b);
    }

    public void disableSaveButton(boolean b){
        saveButton.setDisable(b);
    }

    public void disableNewButton(boolean b){
        newButton.setDisable(b);
    }

    public void disableScreenshotButton(boolean b){
        scrnshotButton.setDisable(b);
    }

    public void disableDoneButton(boolean b){
        doneButton.setDisable(b);
    }

    public void disableEditButton(boolean b) {
        editButton.setDisable(b);
    }

    public void toggleLeftPane(boolean b){
        leftPanel.setVisible(b);
    }

    public void setMetaDataText(String text){
        metaData.setText(text);
    }

    public ComboBox<String> getAlgorithmTypes(){
        return algorithmTypes;
    }

    public void toggleAlgorithmTypes(boolean b){
        algorithmTypes.setVisible(b);
    }
}
