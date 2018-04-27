package algorithms;

import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import settings.AppPropertyTypes;
import ui.AppUI;
import ui.DataVisualizer;
import vilij.templates.ApplicationTemplate;

import java.util.ArrayList;

public class AppAlgorithm implements AlgorithmComponent {

    private  Algorithm selectedAlgorithm;
    private final ApplicationTemplate applicationTemplate;
    private final ArrayList<Algorithm> algorithmList;
    private Thread algorithmThread;

    public AppAlgorithm(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        algorithmList = new ArrayList<>();

        algorithmList.add(new RandomClassifier(new DataSet(), 1,1,true));
        algorithmList.add(new RandomClassifier(new DataSet(), 1,1,true));
        algorithmList.add(new RandomCluster(new DataSet(), 1,1,false));
        algorithmList.add(new RandomCluster(new DataSet(), 1,1,true));
    }

    //public void addAlgorithm(Algorithm alg){algorithmList.add(alg);}

    @Override
    public ArrayList<Algorithm> getAlgorithmOfType(AlgorithmTypes type) {
        ArrayList<Algorithm> typeList = new ArrayList<>();
        for(Algorithm alg : algorithmList){
            if(type.equals(AlgorithmTypes.CLASSIFICATION) && alg instanceof Classifier)
                typeList.add(alg);
            if(type.equals(AlgorithmTypes.CLUSTERING) && alg instanceof Cluster)
                typeList.add(alg);
        }

        return typeList;
    }

    @Override
    public void configAlgorithm(Algorithm alg) {
        //init window
        ConfigDialog config = ((DataVisualizer)applicationTemplate).getConfigDialog();
        config.show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONFIG_TITLE.name())
                                                                    + " " + alg.getClass().getSimpleName(), alg);
        if(config.getSave().equals(ConfigDialog.Option.YES)){
            alg.setMaxIterations(config.getMaxIterations());
            alg.setUpdateInterval(config.getUpdateInterval());
            alg.setToContinue(config.getToContinue());
            alg.setIsConfigured();
            if(alg instanceof Cluster)
                ((Cluster) alg).setClusterNumber(config.getClusterNumber());

            ((AppUI) applicationTemplate.getUIComponent()).refreshAlgorithms();
        }
    }

    public void run(Algorithm alg){
        algorithmThread = new Thread(alg);
        Service<Void> displayThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        //System.out.println("service start");
                        while(algorithmThread.isAlive()){
                            synchronized ((RandomClassifier) alg) {
                                while(((RandomClassifier)alg).getEmpty().get()){
                                    try{
                                        alg.wait();
                                    }catch (InterruptedException e) {
                                        if(((RandomClassifier)alg).getEmpty() == null)
                                            return null;
                                    }
                                }

                                Platform.runLater(() -> {
                                    if (((AppData) applicationTemplate.getDataComponent()).hasNoErrors()) {
                                        ((AppData) applicationTemplate.getDataComponent()).classify(((Classifier) alg).getOutput());
                                        ((AppData) applicationTemplate.getDataComponent()).displayData();
                                    }
                                });

                                if(alg.tocontinue()){
                                    ((RandomClassifier)alg).setEmpty(true);
                                    alg.notifyAll();
                                }else{
                                    Platform.runLater(() ->{
                                        ((AppUI) applicationTemplate.getUIComponent()).disableRunButton(false);
                                        ((AppUI) applicationTemplate.getUIComponent()).disableScreenshotButton(false);
                                    });
                                }
                            }
                            try{
                                Thread.sleep(500);
                            }catch (InterruptedException e){
                                    return null;
                            }
                        }
                        //System.out.println("ENDED");
                        Platform.runLater(() -> {
                            ((AppUI)applicationTemplate.getUIComponent()).getEditButton().setVisible(true);
                            ((AppUI) applicationTemplate.getUIComponent()).disableScreenshotButton(false);
                            ((AppUI)applicationTemplate.getUIComponent()).displayRunButton();
                        });

                        return null;
                    }
                };
            }
        };
        algorithmThread.start();
        displayThread.restart();

    }

    public void setSelectedAlgorithm(Algorithm selectedAlgorithm) { this.selectedAlgorithm = selectedAlgorithm; }

    public Algorithm getSelectedAlgorithm() { return selectedAlgorithm; }

    public Thread getAlgorithmThread() { return algorithmThread; }

    public void endThreads(){
        ((RandomClassifier)selectedAlgorithm).setEmpty();
        algorithmThread.interrupt();
    }

}
