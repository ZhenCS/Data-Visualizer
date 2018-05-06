package dataprocessors;

import actions.AppActions;
import algorithms.AppAlgorithm;
import algorithms.DataSet;
import javafx.scene.chart.LineChart;
import settings.AppPropertyTypes;
import ui.AppUI;
import ui.DataVisualizer;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


import static settings.AppPropertyTypes.AVERAGE_LINE;
import static settings.AppPropertyTypes.CLASSIFIER_LINE;
import static settings.AppPropertyTypes.TEXT_AREA;
import static vilij.settings.PropertyTypes.LOAD_ERROR_MSG;
import static vilij.settings.PropertyTypes.LOAD_ERROR_TITLE;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private final TSDProcessor        processor;
    private final ApplicationTemplate applicationTemplate;
    private String bufferTextArea;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    public String getBufferTextArea(){ return bufferTextArea; }

    public void setBufferTextArea(String text){ bufferTextArea = text; }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataFilePath.toFile()));
            applicationTemplate.getUIComponent().clear();
            String line;
            String displayTextArea;
            bufferTextArea = "";
            int lineNum = 0;
            StringBuilder displayTextAreaBuilder = new StringBuilder();
            StringBuilder bufferTextAreaBuilder = new StringBuilder();
            while((line = reader.readLine()) != null){
                if(lineNum < 10)
                    displayTextAreaBuilder.append(line).append("\n");
                else
                    bufferTextAreaBuilder.append(line).append("\n");

                lineNum++;
            }
            displayTextArea = displayTextAreaBuilder.toString();
            bufferTextArea = bufferTextAreaBuilder.toString();

            reader.close();
            String allInstances = displayTextArea.trim() + "\n" + bufferTextArea;

            processor.checkForErrors(allInstances);
            clear();

            AppUI ui = ((AppUI)applicationTemplate.getUIComponent());

            ui.getTextArea().setText(displayTextArea.trim());
            ui.disableNewButton(false);
            ui.toggleLeftPane(true);
            ui.setHasNewText(false);
            loadData(allInstances);
            addClassification();
            ui.doneUIUpdate();

            createAverageLine();
            displayData();

        } catch (IOException e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name() + AppPropertyTypes.SPECIFIED_FILE.name()));
        } catch (Exception e){
            showCorrectDialog(e);
            ((AppUI)applicationTemplate.getUIComponent()).toggleTextArea(false);
            ((AppUI)applicationTemplate.getUIComponent()).disableNewButton(false);
            ((AppActions)applicationTemplate.getActionComponent()).setDataFilePath(null);
        }
    }

    private boolean loadData(String dataString) {
        // TODO for homework 1
        try {
            processor.processString(dataString);
            Path dataFilePath = ((AppActions)applicationTemplate.getActionComponent()).getDataFilePath();
            processor.setMetaData(dataString);
            if(dataFilePath != null){
                Path p = Paths.get(System.getProperty("user.dir"));
                TSDProcessor.MetaDataBuilder.getMetaDataBuilder().setSource(p.relativize(dataFilePath).toString());
            }
            ((AppUI)applicationTemplate.getUIComponent()).setMetaDataText(TSDProcessor.MetaDataBuilder.getMetaDataBuilder().build());
            ((AppAlgorithm)((DataVisualizer) applicationTemplate).getAlgorithmComponent()).updateAlgorithmData();
            ((AppUI)applicationTemplate.getUIComponent()).refreshAlgorithms();
            return true;

        } catch (Exception e) {
            showCorrectDialog(e);
        }
        return false;

    }

    public void addClassification(){
        if(TSDProcessor.MetaDataBuilder.getMetaDataBuilder().getLabelNum() == 2){
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().remove("Classification");
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().add("Classification");
        }
        else
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().remove("Classification");

    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO NOT A PART OF HW 1
        String text = getAllDataText();

        try {
            processor.checkForErrors(text);
            File file = dataFilePath.toFile();
            FileWriter writer = new FileWriter(file);
            writer.write(text);
            writer.close();

        } catch (IOException e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name()));
        }catch (Exception e) {
            showCorrectDialog(e);
        }

        ((AppUI)applicationTemplate.getUIComponent()).disableSaveButton(true);
    }

    private void showCorrectDialog(Exception e){
        String error = e.getMessage().split(":")[0];

        if (error.equals(TSDProcessor.InvalidDataNameException.class.getSimpleName()) ||
                error.equals(TSDProcessor.DuplicateNameException.class.getSimpleName()) ||
                error.equals(TSDProcessor.FormatException.class.getSimpleName())) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(LOAD_ERROR_TITLE.name()), e.getMessage());
        }
        if(error.equals(ArrayIndexOutOfBoundsException.class.getSimpleName())){
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(LOAD_ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(LOAD_ERROR_MSG.name()) +
                            applicationTemplate.manager.getPropertyValue(TEXT_AREA.name()));
        }
    }

    public boolean hasNoErrors(){
        clear();
        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        chart.getData().removeAll(chart.getData());

        String data = getAllDataText();

        return ((AppData) applicationTemplate.getDataComponent()).loadData(data);
    }

    public String getAllDataText(){
        String data = ((AppUI)applicationTemplate.getUIComponent()).getText().trim();
        if (bufferTextArea != null)
            data += "\n" + bufferTextArea;

        return data;
    }

    @Override
    public void clear() {
        processor.clear();
        ((AppUI)applicationTemplate.getUIComponent()).disableScreenshotButton(true);
    }

    public void createAverageLine(){
        processor.createAverageLine(((AppUI) applicationTemplate.getUIComponent()).getChart(), applicationTemplate.manager.getPropertyValue(AVERAGE_LINE.name()));
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        processor.createTooltips(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    public void classify(List<Integer> output){
        processor.createLine(((AppUI) applicationTemplate.getUIComponent()).getChart(), applicationTemplate.manager.getPropertyValue(CLASSIFIER_LINE.name()), output);
    }

    public void clusterize(DataSet set){
        clear();
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        processor.setDataSet(set);
        createAverageLine();
        displayData();
    }
}
