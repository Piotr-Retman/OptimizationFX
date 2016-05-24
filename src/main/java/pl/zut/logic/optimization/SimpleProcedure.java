package pl.zut.logic.optimization;

import pl.zut.logic.optimization.helpers.SimpleProcedureHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Klasa rozszerzająca SimpleProcedureHelpera
 */
class SimpleProcedure extends SimpleProcedureHelper {

    public List<Long> countPi(Long sumOfMakeOrderTimes, List<Long> deadLineData) {
        List<Long> pi = new ArrayList<>();
        deadLineData.stream().forEach(deadLineTime -> {
            long piVal = sumOfMakeOrderTimes - deadLineTime;
            pi.add(piVal);
        });
        return pi;
    }

    public long countSumOfMakeOrderTimes(List<Long> makeTimeArrayAsList) {
        long[] sum = {0L};
        makeTimeArrayAsList.stream().forEach(orderMakeTimeValue -> {
            sum[0] += orderMakeTimeValue;
        });
        return sum[0];
    }
}
