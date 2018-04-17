package algorithms;

import java.util.ArrayList;

public interface AlgorithmComponent {

    ArrayList<Algorithm> getAlgorithmOfType(AlgorithmTypes type);

    void configAlgorithm(Algorithm alg);

    void run(Algorithm alg);

}
