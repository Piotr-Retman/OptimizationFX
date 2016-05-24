package pl.zut.logic.optimization.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Klasa zawiera metody wspierające podstawowe działania w algorytmie
 */
public class SimpleProcedureHelper {

    public List<String> generateListOfShortNames(int size) {
        List<String> basedOrder = new ArrayList<>();
        IntStream.range(1,size+1).forEach(iteration -> {
            basedOrder.add(StringWorker.generateRetrieveString("z",String.valueOf(iteration)));
        });
        return basedOrder;
    }
}
