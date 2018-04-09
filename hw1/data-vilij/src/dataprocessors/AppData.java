package dataprocessors;

import actions.AppActions;
import javafx.scene.control.TextArea;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static settings.AppPropertyTypes.AVERAGE_LINE;
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

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private String bufferTextArea;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    public String getBufferTextArea(){
        return bufferTextArea;
    }

    public void setBufferTextArea(String text){
        bufferTextArea = text;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataFilePath.toFile()));
            applicationTemplate.getUIComponent().clear();
            String line;
            String displayTextArea = "";
            bufferTextArea = "";
            int lineNum = 0;
            while((line = reader.readLine()) != null){
                if(lineNum < 10)
                    displayTextArea += line + "\n";
                else
                     bufferTextArea += line + "\n";

                lineNum++;
            }
            reader.close();
            processor.checkForErrors(displayTextArea.trim() + "\n" + bufferTextArea);

            AppUI ui = ((AppUI)applicationTemplate.getUIComponent());

            ui.getTextArea().setText(displayTextArea.trim());
            ui.setHasNewText(true);
            ui.disableNewButton(false);
            ui.toggleLeftPane(true);
            ui.doneUIUpdate();

            ui.setMetaDataText(processor.getMetaData(displayTextArea.trim() + "\n" + bufferTextArea,
                                dataFilePath.getFileName().toString()).build());
            addClassification();

            if(lineNum > 10){
                ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                dialog.show(applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name()),
                        String.format(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.TOO_MANY_LINES_MSG.name()), lineNum));
            }
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

    public boolean loadData(String dataString) {
        // TODO for homework 1
        try {
            processor.processString(dataString);
            ((AppUI)applicationTemplate.getUIComponent()).setMetaDataText(processor.getMetaData(dataString).build());
            addClassification();

            return true;
        } catch (Exception e) {
            showCorrectDialog(e);
        }
        return false;
    }

    private void addClassification(){
        if(TSDProcessor.MetaDataBuilder.getMetaDataBuilder().getLabelNum() >= 2){
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().remove("Classification");
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().add("Classification");
        }
        else
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorithmTypes().getItems().remove("Classification");
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO NOT A PART OF HW 1
        String text = ((AppUI)applicationTemplate.getUIComponent()).getText();

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

    @Override
    public void clear() {
        processor.clear();
        ((AppUI)applicationTemplate.getUIComponent()).disableScreenshotButton(true);
    }

    public void displayData() {
        processor.createAverageLine(((AppUI) applicationTemplate.getUIComponent()).getChart(), applicationTemplate.manager.getPropertyValue(AVERAGE_LINE.name()));
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        processor.createTooltips(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
