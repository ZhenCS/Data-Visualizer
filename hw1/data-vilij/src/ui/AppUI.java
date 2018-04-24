package ui;

import actions.AppActions;
import algorithms.*;
import dataprocessors.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

import static settings.AppPropertyTypes.*;



/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    private final ApplicationTemplate applicationTemplate;
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
    private HBox                        runStop;
    private Text                        metaData;

    public LineChart<Number, Number> getChart() { return chart; }

    AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
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
        NumberAxis      yAxis   = new NumberAxis(0, 60, 10);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setHorizontalZeroLineVisible(false);

        chart.setAnimated(false);

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
        runStop = new HBox();

        setAlgorithmButtons(runButton, false);
        setAlgorithmButtons(stopButton, false);

        runStop.getChildren().add(runButton);
        algorithmButtons.getChildren().add(runStop);
        AnchorPane.setBottomAnchor(runStop, 70.0);
        AnchorPane.setLeftAnchor(runStop, 0.0);
        VBox.setVgrow(algorithmButtons, Priority.ALWAYS);

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

    private void setAlgorithmButtons(Button button, boolean visible){
        button.setStyle("-fx-border: none; -fx-background-color: transparent;");
        setPointerCursor(button);
        button.setVisible(visible);
    }

    private void setPointerCursor(Button button){
        button.setOnMouseEntered(event -> getPrimaryWindow().getScene().setCursor(Cursor.HAND));
        button.setOnMouseExited(event -> getPrimaryWindow().getScene().setCursor(Cursor.DEFAULT));
    }

    public void refreshAlgorithms(){
        showAlgorithmsOfType(AlgorithmTypes.valueOf(algorithmTypes.getValue().toUpperCase()));
    }

    private void showAlgorithmsOfType(AlgorithmTypes type){
        algorithmSelection.getChildren().clear();
        ToggleGroup aGroup = new ToggleGroup();
        runButton.setDisable(true);
        runButton.setVisible(false);
        ArrayList<Algorithm> typeList = ((DataVisualizer) applicationTemplate).getAlgorithmComponent().getAlgorithmOfType(type);
        if(typeList != null && typeList.size() > 0)
            typeList.forEach(algorithm -> {
                HBox hbox = new HBox();
                RadioButton radio = new RadioButton(algorithm.getClass().getSimpleName());
                radio.setToggleGroup(aGroup);
                radio.setOnAction(event -> {
                    if(algorithm.isConfigured()){
                        runButton.setDisable(false);
                        ((AppAlgorithm)((DataVisualizer)applicationTemplate).getAlgorithmComponent()).setSelectedAlgorithm(algorithm);
                    }else{
                        runButton.setDisable(true);
                        ((AppAlgorithm)((DataVisualizer)applicationTemplate).getAlgorithmComponent()).setSelectedAlgorithm(null);
                    }

                    runButton.setVisible(true);
                });
                radio.setStyle("-fx-font-size: 13px");


                PropertyManager manager = applicationTemplate.manager;
                Button config = setToolbarButton(configPath, manager.getPropertyValue(CONFIG_TOOLTIP.name()), false);
                config.setOnAction(e -> ((DataVisualizer) applicationTemplate).getAlgorithmComponent().configAlgorithm(algorithm));
                setAlgorithmButtons(config, true);

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
        runButton.setOnAction(event -> {
            Algorithm selectedAlgorithm = ((AppAlgorithm)((DataVisualizer)applicationTemplate).getAlgorithmComponent()).getSelectedAlgorithm();

            selectedAlgorithm.setDataSet(DataSet.fromTSDString(textArea.getText()));

            Thread algorithmThread = ((AppAlgorithm)((DataVisualizer) applicationTemplate).getAlgorithmComponent()).getAlgorithmThread();

            ((RandomClassifier) selectedAlgorithm).setEmpty(true);
            if( algorithmThread == null || !algorithmThread.isAlive()){
                ((DataVisualizer) applicationTemplate).getAlgorithmComponent().run(selectedAlgorithm);
            }
            /*TODO show screenshot button after each iteration + end of algorithm
                show confirmation dialog when user exits but theres new data*/
            synchronized (selectedAlgorithm){
                selectedAlgorithm.notifyAll();
            }

            editButton.setVisible(false);
            stopButton.setDisable(false);
            stopButton.setVisible(true);
            if(!selectedAlgorithm.getToContinue()){
                displayNonContinuousButtons();
                runButton.setDisable(true);
            }
            else
                displayStopButton();
        });

        stopButton.setOnAction(event -> {
            ((AppAlgorithm)((DataVisualizer)applicationTemplate).getAlgorithmComponent()).endThreads();

            editButton.setVisible(true);
            displayRunButton();
        });
    }

    public void displayNonContinuousButtons(){
        runStop.getChildren().clear();
        runStop.getChildren().add(runButton);
        runStop.getChildren().add(stopButton);
    }

    public void displayRunButton(){
        runStop.getChildren().clear();
        runStop.getChildren().add(runButton);
    }

    public void displayStopButton(){
        runStop.getChildren().clear();
        runStop.getChildren().add(stopButton);
    }

    public boolean isDisplayable(){
        applicationTemplate.getDataComponent().clear();
        chart.getData().removeAll(chart.getData());

        String data = textArea.getText().trim();
        String bufferText = ((AppData) applicationTemplate.getDataComponent()).getBufferTextArea();
        if (bufferText != null)
            data += "\n" + bufferText;

        return ((AppData) applicationTemplate.getDataComponent()).loadData(data);
    }

    private void toggleAlgorithm(){
        if(hasNewText) {
            if (isDisplayable()) {
                ((AppData) applicationTemplate.getDataComponent()).createAverageLine();
                ((AppData) applicationTemplate.getDataComponent()).displayData();
                ((AppData) applicationTemplate.getDataComponent()).addClassification();
                doneUIUpdate();
            } else {
                setMetaDataText("");
                disableSaveButton(true);
                hasNewText = false;
            }
        }
    }

    public void doneUIUpdate(){
        doneButton.setDisable(true);
        editButton.setDisable(false);
        textArea.setDisable(true);
        scrnshotButton.setDisable(false);

        algorithmTypes.setVisible(true);
        algorithmSelection.setVisible(true);
    }

    public void editUIUpdate(){
        doneButton.setDisable(false);
        editButton.setDisable(true);
        textArea.setDisable(false);

        runButton.setVisible(false);
        stopButton.setVisible(false);
        algorithmTypes.setVisible(false);
        algorithmSelection.setVisible(false);

        metaData.setText("");
        hasNewText = true;
    }

    public void toggleTextArea(boolean b){ textArea.setDisable(b); }

    public String getText(){ return textArea.getText(); }

    public TextArea getTextArea(){ return textArea; }

    public void setHasNewText(boolean b){ hasNewText = b; }

    public void disableAppUIButtons(boolean b){
        disableSaveButton(b);
        disableNewButton(b);
    }
    public void disableRunButton(boolean b) { runButton.setDisable(b); }

    public void disableSaveButton(boolean b){ saveButton.setDisable(b); }

    public void disableNewButton(boolean b){ newButton.setDisable(b); }

    public void disableScreenshotButton(boolean b){ scrnshotButton.setDisable(b); }

    public void toggleLeftPane(boolean b){ leftPanel.setVisible(b); }

    public void setMetaDataText(String text){ metaData.setText(text); }

    public ComboBox<String> getAlgorithmTypes(){ return algorithmTypes; }

    public Button getEditButton() { return editButton; }
    /*public void disableDoneButton(boolean b){
        doneButton.setDisable(b);
    }

    public void disableEditButton(boolean b) {
        editButton.setDisable(b);
    }*/


    /*public void toggleAlgorithmTypes(boolean b){
        algorithmTypes.setVisible(b);
    }*/
}
