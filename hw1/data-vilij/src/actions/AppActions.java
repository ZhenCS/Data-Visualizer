package actions;

import javafx.application.Platform;
import javafx.stage.FileChooser;

import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.CLOSE_LABEL;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
       if(promptToSave())
           applicationTemplate.getUIComponent().clear();

    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        File file = initSaveWindow();
        if(file != null){
            saveFile(file);
        }
    }

    private void saveFile(File file){
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(((AppUI)applicationTemplate.getUIComponent()).getText());
            writer.close();
        } catch (IOException e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name()));
        }
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1

        if(((AppUI)applicationTemplate.getUIComponent()).getHasNewText()){
            ConfirmationDialog dialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            dialog.show(applicationTemplate.manager.getPropertyValue(CLOSE_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));

            if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)){
                Platform.exit();
            }
        }else{
            Platform.exit();
        }

    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

    public boolean handleTextArea(String text){
        if(text.equals("")){
            ((AppUI)applicationTemplate.getUIComponent()).disableAppUIButtons(true);
            return false;
        }else{
            ((AppUI)applicationTemplate.getUIComponent()).disableAppUIButtons(false);
            return true;
        }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() /*throws IOException*/ {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method

        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        dialog.show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)){
            handleSaveRequest();
            return true;
        }
        else return dialog.getSelectedOption().equals(ConfirmationDialog.Option.NO);

    }

    private File initSaveWindow(){
        PropertyManager manager = applicationTemplate.manager;

        dataFilePath = Paths.get(manager.getPropertyValue(CURRENT_PATH.name()));
        String dataResourcePath = String.join("/",dataFilePath.toString(), manager.getPropertyValue(DATA_RESOURCE_PATH.name()));


        String dataExt = manager.getPropertyValue(DATA_FILE_EXT.name());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_WORK_TITLE.name()));
        fileChooser.setInitialDirectory(new File(dataResourcePath));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()) + " ("+dataExt+")", dataExt));

        return fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
    }
}
