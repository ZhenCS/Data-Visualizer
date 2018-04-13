package ui;

import actions.AppActions;
import algorithms.AppAlgorithm;
import algorithms.AlgorithmComponent;
import algorithms.ConfigDialog;
import dataprocessors.AppData;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;

import static vilij.settings.InitializationParams.*;


/**
 * The main class from which the application is run. The various components used here must be concrete implementations
 * of types defined in {@link vilij.components}.
 *
 * @author Ritwik Banerjee
 */
public final class DataVisualizer extends ApplicationTemplate {

    private AlgorithmComponent algorithmComponent;
    private final ConfigDialog configDialog = ConfigDialog.getDialog();

    @Override
    public void start(Stage primaryStage) {
        dialogsAudit(primaryStage);
        if (propertyAudit()){
            userInterfaceAudit(primaryStage);
            configDialog.init(primaryStage);
        }

    }

    @Override
    protected boolean propertyAudit() {
        boolean failed = manager == null || !(loadProperties(PROPERTIES_XML) && loadProperties(WORKSPACE_PROPERTIES_XML));
        if (failed)
            errorDialog.show(LOAD_ERROR_TITLE.getParameterName(), PROPERTIES_LOAD_ERROR_MESSAGE.getParameterName());
        return !failed;
    }

    @Override
    protected void userInterfaceAudit(Stage primaryStage) {
        setUIComponent(new AppUI(primaryStage, this));
        setActionComponent(new AppActions(this));
        setDataComponent(new AppData(this));
        setAlgorithmComponent(new AppAlgorithm(this));
        uiComponent.initialize();
    }

    private void setAlgorithmComponent(AlgorithmComponent component){
        algorithmComponent = component;
    }

    public AlgorithmComponent getAlgorithmComponent() {return algorithmComponent;}

    public ConfigDialog getConfigDialog() {return configDialog;}

}
