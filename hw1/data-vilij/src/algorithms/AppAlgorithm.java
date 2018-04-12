package algorithms;

import settings.AppPropertyTypes;
import ui.AppUI;
import ui.DataVisualizer;
import vilij.templates.ApplicationTemplate;

import java.util.ArrayList;
import java.util.List;

public class AppAlgorithm implements AlgorithmComponent {

    private ApplicationTemplate applicationTemplate;
    private ArrayList<Algorithm> algorithmList;

    public AppAlgorithm(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        algorithmList = new ArrayList<>();

        algorithmList.add(new RandomClassifier(new DataSet(), 0,0,false));
        algorithmList.add(new RandomCluster(new DataSet(), 0,0,false));
    }

    public void addAlgorithm(Algorithm alg){
        algorithmList.add(alg);
    }

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

            if(alg instanceof Cluster)
                ((Cluster) alg).setClusterNumber(config.getClusterNumber());

            ((AppUI) applicationTemplate.getUIComponent()).refreshAlgorithms();
        }
    }
}
