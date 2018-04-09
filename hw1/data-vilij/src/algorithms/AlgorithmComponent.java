package algorithms;

import java.util.ArrayList;

public interface AlgorithmComponent {
    ArrayList<Algorithm> getAlgorithmOfType(AlgorithmTypes type);

}
