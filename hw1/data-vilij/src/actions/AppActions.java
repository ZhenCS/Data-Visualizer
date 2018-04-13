package actions;


import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private final ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    private Path dataFilePath;
    public void setDataFilePath(Path p){
        dataFilePath = p;
    }
    public Path getDataFilePath() {return dataFilePath;}

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1

       if(!((AppUI)applicationTemplate.getUIComponent()).getText().equals("")){
           if(promptToSave()){
               dataFilePath = null;
               applicationTemplate.getUIComponent().clear();
               ((AppData) applicationTemplate.getDataComponent()).setBufferTextArea(null);
           }
       }
       AppUI ui = ((AppUI) applicationTemplate.getUIComponent());
       ui.toggleLeftPane(true);
       ui.setHasNewText(false);
       ui.editUIUpdate();

    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        if(dataFilePath == null){
            File file = initSaveWindow();
            if(file != null){
                dataFilePath = file.toPath();
                applicationTemplate.getDataComponent().saveData(dataFilePath);
            }else{
                dataFilePath = null;
            }
        }else{
            applicationTemplate.getDataComponent().saveData(dataFilePath);
        }



    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        File file = initLoadWindow();
        if(file != null){
            dataFilePath = file.toPath();
            applicationTemplate.getDataComponent().loadData(dataFilePath);
        }else{
            dataFilePath = null;
        }
    }


    @Override
    public void handleExitRequest() {
        // TODO for homework 1

        Platform.exit();

        /*if(((AppUI)applicationTemplate.getUIComponent()).getHasNewText()){
            ConfirmationDialog dialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            dialog.show(applicationTemplate.manager.getPropertyValue(CLOSE_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));

            if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)){
                Platform.exit();
            }
        }else{
            Platform.exit();
        }*/


    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        WritableImage screenshot = ((AppUI)applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);

        File image = initSaveImageWindow();

        if(image != null)
            ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", image);

    }

    public boolean handleTextArea(String text){
        String bufferTextArea = ((AppData) applicationTemplate.getDataComponent()).getBufferTextArea();

        if(bufferTextArea != null && !bufferTextArea.equals("")){
            String[] textArray = text.split("\n");

            text = Stream.concat(Stream.of(textArray), Stream.of(bufferTextArea.split("\n"))).limit(10).reduce("", (a,b) -> a.concat(b).concat("\n")).trim();
            bufferTextArea = Stream.of(bufferTextArea.split("\n")).skip(10-textArray.length).reduce("", (a,b) -> a.concat(b).concat("\n")).trim();

            ((AppUI)applicationTemplate.getUIComponent()).getTextArea().setText(text.trim());
            ((AppData) applicationTemplate.getDataComponent()).setBufferTextArea(bufferTextArea);
        }

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
        String dataExt = manager.getPropertyValue(DATA_FILE_EXT.name());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_WORK_TITLE.name()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()) + " ("+dataExt+")", dataExt));


        String dataResourcePath;
        if(dataFilePath == null){
            dataFilePath = Paths.get(manager.getPropertyValue(CURRENT_PATH.name()));
            dataResourcePath = String.join("/",dataFilePath.toString(), manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        }else{
            dataResourcePath = dataFilePath.toString();
        }


        fileChooser.setInitialDirectory(new File(dataResourcePath));

        return fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

    }

    private File initLoadWindow(){
        PropertyManager manager = applicationTemplate.manager;

        dataFilePath = Paths.get(manager.getPropertyValue(CURRENT_PATH.name()));
        String dataResourcePath = String.join("/",dataFilePath.toString(), manager.getPropertyValue(DATA_RESOURCE_PATH.name()));


        String dataExt = manager.getPropertyValue(DATA_FILE_EXT.name());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_TOOLTIP.name()));
        fileChooser.setInitialDirectory(new File(dataResourcePath));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()) + " ("+dataExt+")", dataExt));

        return fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
    }

    private File initSaveImageWindow(){

        PropertyManager manager = applicationTemplate.manager;
        String dataExt = ".png";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(PropertyTypes.SAVE_WORK_TITLE.name()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter( dataExt, dataExt));

        String dataResourcePath = String.join("/",manager.getPropertyValue(CURRENT_PATH.name()), manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        fileChooser.setInitialDirectory(new File(dataResourcePath));

        return fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

    }
}
