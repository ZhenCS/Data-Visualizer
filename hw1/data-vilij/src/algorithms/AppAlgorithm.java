package algorithms;

import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import settings.AppPropertyTypes;
import ui.AppUI;
import ui.DataVisualizer;
import vilij.templates.ApplicationTemplate;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class AppAlgorithm implements AlgorithmComponent {

    private  Algorithm selectedAlgorithm;
    private final ApplicationTemplate applicationTemplate;
    private final ArrayList<Algorithm> algorithmList;
    private Thread algorithmThread;

    public AppAlgorithm(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        algorithmList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("hw1/data-vilij/resources/properties/algorithms.tsd")));
            String line;
            while((line = reader.readLine()) != null){
                Class klass = Class.forName("algorithms." + line);
                Constructor konstructor = klass.getConstructors()[0];

                if(klass.getSuperclass().equals(Classifier.class))
                    algorithmList.add((Algorithm) konstructor.newInstance(null, 0, 0, false));
                if(klass.getSuperclass().equals(Clusterer.class))
                    algorithmList.add((Algorithm) konstructor.newInstance(null, 0, 0, 0, false));
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException ignored) {
        }
    }

    @Override
    public ArrayList<Algorithm> getAlgorithmOfType(AlgorithmTypes type) {
        ArrayList<Algorithm> typeList = new ArrayList<>();
        for(Algorithm alg : algorithmList){
            if(type.equals(AlgorithmTypes.CLASSIFICATION) && alg instanceof Classifier)
                typeList.add(alg);
            if(type.equals(AlgorithmTypes.CLUSTERING) && alg instanceof Clusterer)
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
            String data = ((AppData)applicationTemplate.getDataComponent()).getAllDataText();
            try {
                Class klass = Class.forName("algorithms." + alg.getClass().getSimpleName());
                Constructor konstructor = klass.getConstructors()[0];

                Algorithm algorithm = null;
                if(alg instanceof Classifier)
                    algorithm = (Algorithm) konstructor.newInstance(DataSet.fromTSDString(data), config.getMaxIterations(), config.getUpdateInterval(), config.getToContinue());
                if(alg instanceof Clusterer)
                    algorithm = (Algorithm) konstructor.newInstance(DataSet.fromTSDString(data), config.getMaxIterations(), config.getUpdateInterval(), config.getClusterNumber(), config.getToContinue());

                algorithmList.set(algorithmList.indexOf(alg), algorithm);
                setSelectedAlgorithm(algorithm);

                ((AppUI) applicationTemplate.getUIComponent()).refreshAlgorithms();

            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
            }
        }
    }

    public void updateAlgorithmData(){
        String data = ((AppData)applicationTemplate.getDataComponent()).getAllDataText();
        algorithmList.forEach(alg -> {
            Constructor konstructor = alg.getClass().getConstructors()[0];
            Algorithm algorithm = null;
            try {
                if (alg instanceof Classifier)
                    algorithm = (Algorithm) konstructor.newInstance(DataSet.fromTSDString(data), alg.getMaxIterations(), alg.getUpdateInterval(), alg.continuous());
                if (alg instanceof Clusterer)
                    algorithm = (Algorithm) konstructor.newInstance(DataSet.fromTSDString(data), alg.getMaxIterations(), alg.getUpdateInterval(), ((Clusterer) alg).getClusterNumber(), alg.continuous());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                return;
            }
            algorithmList.set(algorithmList.indexOf(alg), algorithm);
        });
    }

    public void run(Algorithm alg){
        algorithmThread = new Thread(alg);
        Service<Void> displayThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        while(algorithmThread.isAlive()){
                            synchronized (alg) {
                                while(alg.getEmpty().get()){
                                    try{
                                        alg.wait();
                                    }catch (InterruptedException e) {
                                        if(alg.getEmpty() == null)
                                            return null;
                                    }
                                }

                                if(alg instanceof Classifier)
                                    classificationUpdate((Classifier) alg);
                                else if(alg instanceof Clusterer)
                                    clusterUpdate((Clusterer) alg);

                                Platform.runLater(() -> ((AppUI) applicationTemplate.getUIComponent()).updateIterationLabel("Running: " + alg.getClass().getSimpleName()
                                        + "\nIteration: " + alg.getIteration()));

                                if(alg.continuous()){
                                    alg.setEmpty(true);
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
                        Platform.runLater(() -> {
                            ((AppUI)applicationTemplate.getUIComponent()).getEditButton().setVisible(true);
                            ((AppUI) applicationTemplate.getUIComponent()).disableScreenshotButton(false);
                            ((AppUI)applicationTemplate.getUIComponent()).displayRunButton();
                            ((AppUI)applicationTemplate.getUIComponent()).refreshAlgorithms();
                            ((AppUI) applicationTemplate.getUIComponent()).updateIterationLabel("Ended: " + alg.getClass().getSimpleName()
                                    + "\nIteration: " + alg.getIteration());
                        });

                        return null;
                    }
                };
            }
        };
        algorithmThread.start();
        displayThread.restart();

    }

    public void setSelectedAlgorithm(Algorithm selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }

    public Algorithm getSelectedAlgorithm() { return selectedAlgorithm; }

    public Thread getAlgorithmThread() { return algorithmThread; }

    public void endThreads(){
        selectedAlgorithm.setEmptyNull();
        algorithmThread.interrupt();
    }

    private void classificationUpdate(Classifier alg){
        Platform.runLater(() -> {
            if (((AppData) applicationTemplate.getDataComponent()).hasNoErrors()) {
                ((AppData) applicationTemplate.getDataComponent()).classify(alg.getOutput());
                ((AppData) applicationTemplate.getDataComponent()).displayData();
            }
        });
    }

    private void clusterUpdate(Clusterer alg){
        Platform.runLater(() -> ((AppData) applicationTemplate.getDataComponent()).clusterize(alg.getDataset()));
    }

}
