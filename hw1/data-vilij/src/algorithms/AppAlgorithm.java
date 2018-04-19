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

    private ApplicationTemplate applicationTemplate;
    private ArrayList<Algorithm> algorithmList;
    private Thread algorithmThread;

    public AppAlgorithm(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        algorithmList = new ArrayList<>();

        algorithmList.add(new RandomClassifier(new DataSet(), 40,2,true));
        algorithmList.add(new RandomClassifier(new DataSet(), 1,7,true));
        algorithmList.add(new RandomClassifier(new DataSet(), 6,2,false));
        algorithmList.add(new RandomCluster(new DataSet(), 0,0,false));
        algorithmList.add(new RandomCluster(new DataSet(), 2,5,true));
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
                    protected Void call() throws Exception {
                        System.out.println("3");
                        while(alg.isRunning()){
                            Platform.runLater(() -> {
                                if(((AppUI) applicationTemplate.getUIComponent()).isDisplayable()){
                                    ((AppData)applicationTemplate.getDataComponent()).classify(((Classifier)alg).getOutput());
                                    ((AppData) applicationTemplate.getDataComponent()).displayData();
                                }

                            });
                            try{
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                return null;
                            }
                        }
                        return null;
                    }
                };
            }
        };

        algorithmThread.start();
        displayThread.restart();
        //TODO show that algorithm is running

    }

    public Thread getAlgorithmThread() { return algorithmThread; }
}
