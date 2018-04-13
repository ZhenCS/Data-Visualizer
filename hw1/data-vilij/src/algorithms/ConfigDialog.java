package algorithms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;

public class ConfigDialog extends Stage {

    public enum Option{
        YES,NO
    }


    private static ConfigDialog dialog;
    private final TextField maxIterationField;
    private final TextField updateIntervalField;
    private final TextField clusterNumberField;
    private final CheckBox toContinueBox;
    private final HBox clusterNumberHBox;
    private Option saveOption;

    private ConfigDialog() {
        maxIterationField = new TextField();
        updateIntervalField = new TextField();
        clusterNumberField = new TextField();
        clusterNumberHBox = new HBox();
        toContinueBox = new CheckBox("Continuous Run?");
    }

    public static ConfigDialog getDialog() {
        if (dialog == null)
            dialog = new ConfigDialog();
        return dialog;
    }

    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);
        setWidth(400);

        PropertyManager manager     = PropertyManager.getManager();
        Button closeButton = new Button(manager.getPropertyValue(AppPropertyTypes.CONFIG_LABEL.name()));
        VBox messagePane = new VBox();

        closeButton.setOnAction(e -> {
            saveOption = Option.YES;
            this.close();
        });
        messagePane.setAlignment(Pos.CENTER);

        HBox hbox1 = new HBox();
        Text text1 = new Text(manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS_LABEL.name()));
        StackPane space1 = new StackPane();
        space1.setMinWidth(10);
        HBox.setHgrow(space1, Priority.ALWAYS);
        hbox1.setAlignment(Pos.CENTER_LEFT);
        hbox1.getChildren().addAll(text1, space1 ,maxIterationField);

        HBox hbox2 = new HBox();
        Text text2 = new Text(manager.getPropertyValue(AppPropertyTypes.UPDATE_INTERVAL_LABEL.name()));
        StackPane space2 = new StackPane();
        space2.setMinWidth(10);
        HBox.setHgrow(space2, Priority.ALWAYS);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox2.getChildren().addAll(text2, space2, updateIntervalField);


        Text text3 = new Text(manager.getPropertyValue(AppPropertyTypes.CLUSTER_LABEL.name()));
        StackPane space3 = new StackPane();
        space3.setMinWidth(10);
        HBox.setHgrow(space3, Priority.ALWAYS);
        clusterNumberHBox.setAlignment(Pos.CENTER_LEFT);
        clusterNumberHBox.getChildren().addAll(text3, space3, clusterNumberField);


        messagePane.getChildren().addAll(hbox1, hbox2, clusterNumberHBox, toContinueBox);
        messagePane.getChildren().add(closeButton);
        messagePane.setPadding(new Insets(60, 40, 60, 40));
        messagePane.setSpacing(20);

        Scene messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }


    public void show(String configDialogTitle, Algorithm alg) {
        setTitle(configDialogTitle);
        saveOption = Option.NO;

        int text = (alg.getMaxIterations() <= 0) ? 1 : alg.getMaxIterations();
        maxIterationField.setText("" + text);

        text = (alg.getUpdateInterval() <= 0) ? 1 : alg.getUpdateInterval();
        updateIntervalField.setText("" + text);

        if(alg.tocontinue())
            toContinueBox.setSelected(true);
        else
            toContinueBox.setSelected(false);

        if(alg instanceof Cluster){

            text = (((Cluster) alg).getClusterNumber() <= 0) ? 1 : ((Cluster) alg).getClusterNumber();
            clusterNumberHBox.setVisible(true);
            clusterNumberField.setText("" + text);
        }
        else{
            clusterNumberHBox.setVisible(false);
        }

        showAndWait();
    }

    public Option getSave() {return saveOption;}

    public int getMaxIterations() {

        try{
            return Integer.parseInt(maxIterationField.getText());
        }catch (NumberFormatException e){
            return 0;
        }


    }

    public int getUpdateInterval() {
        try{
            return Integer.parseInt(updateIntervalField.getText());
        }catch (NumberFormatException e){
            return 1;
        }
    }

    public int getClusterNumber() {
        try{
            return Integer.parseInt(clusterNumberField.getText());
        }catch (NumberFormatException e){
            return 1;
        }
    }

    public boolean getToContinue() {
        return toContinueBox.isSelected();
    }


}
