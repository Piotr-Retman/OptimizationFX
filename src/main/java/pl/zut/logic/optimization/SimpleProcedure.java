package pl.zut.logic.optimization;

import pl.zut.logic.optimization.helpers.SimpleProcedureHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Retman on 2016-04-15.
 */
class SimpleProcedure extends SimpleProcedureHelper {

    /**
     * Oblicza listę p(i)
     * @param sumOfTheMakeTime - suma czasów obróbek T0
     * @param deadlineTimes - lista czasów terminów Tt
     * @return lista p(i)
     */
    List<Long> countPi(Long sumOfTheMakeTime, List<Long> deadlineTimes) {
        List countedPi = new ArrayList<>();
        deadlineTimes.stream().forEach(deadLineTime -> {
            long piElement = sumOfTheMakeTime - deadLineTime;
            countedPi.add(piElement);
        });
        return countedPi;
    }

    /**
     * Metoda oblicza sumę czasów obróbek T0
     * @param makeOrderTimes - lista czasów obróbek T0
     * @return suma czasów obróbek T0
     */
    long countSumOfMakeOrderTimes(List<Long> makeOrderTimes) {
        final long sumOfMakeOrderTimes[] = {0L};

        makeOrderTimes.stream().forEach(makeOrderTime ->
                sumOfMakeOrderTimes[0] = sumOfMakeOrderTimes[0] + makeOrderTime

        );
        return sumOfMakeOrderTimes[0];
    }

    public List<String> createStaticOrderNames(int numberOfOrders) {
        List<String> orders = new ArrayList<>();
        IntStream.range(0,numberOfOrders).forEach(value -> {
            value++;
            orders.add("z"+value);
            value--;
        });
        return orders;
    }
}
