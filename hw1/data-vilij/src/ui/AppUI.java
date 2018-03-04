package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
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
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshotPath;
    private CheckBox                     readOnlyCheckBox;

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


        VBox leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight * 0.3);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.3);

        Text leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname       = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize       = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();
        textArea.setOnKeyReleased(e -> hasNewText = ((AppActions)applicationTemplate.getActionComponent()).handleTextArea(textArea.getText()));

        HBox processButtonsBox = new HBox();
        displayButton = new Button(manager.getPropertyValue(AppPropertyTypes.DISPLAY_BUTTON.name()));
        readOnlyCheckBox = new CheckBox(manager.getPropertyValue(AppPropertyTypes.READ_ONLY_CHECKBOX.name()));
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        processButtonsBox.getChildren().addAll(displayButton, readOnlyCheckBox);



        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox);

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);


    }


    private void setWorkspaceActions() {
        displayButton.setOnAction(e -> display());
        readOnlyCheckBox.setOnAction(e -> toggleReadOnly());
    }

    private void toggleReadOnly(){
        if(readOnlyCheckBox.isSelected())
            textArea.setDisable(true);
        else
            textArea.setDisable(false);
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
        ((AppUI) applicationTemplate.getUIComponent()).saveButton.setDisable(b);
    }

    public void disableNewButton(boolean b){
        ((AppUI) applicationTemplate.getUIComponent()).newButton.setDisable(b);
    }

    public void disableScreenshotButton(boolean b){
        ((AppUI) applicationTemplate.getUIComponent()).scrnshotButton.setDisable(b);
    }

    public void checkReadOnlyBox(){
        readOnlyCheckBox.setSelected(true);
        toggleReadOnly();
    }
}
