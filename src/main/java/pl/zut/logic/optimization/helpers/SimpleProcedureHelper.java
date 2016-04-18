package pl.zut.logic.optimization.helpers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Retman on 2016-04-15.
 */
public class SimpleProcedureHelper {

    /**
     * Metoda znajduje najmniejszą wartość w liście p(i) bazuje na ostatnich używanych wartościach p(i) oraz opóźnieniu
     *
     * @param countedBasePi        - lista p(i)
     * @param lastUsedBasedPiRoads - lista użytych wartości p(i)
     * @param delay                - opóźnienie
     * @return najmniejsza znaleziona wartość z listy p(i)
     */
    public long findSmallestValueOfPi(List<Long> countedBasePi, Set<Long> lastUsedBasedPiRoads, long delay) {
        Object[] objects = lastUsedBasedPiRoads.toArray();
        List<Long> collect = countedBasePi.stream().filter(aLong -> aLong < delay && !findOutIfWasUsed(aLong, lastUsedBasedPiRoads)).collect(Collectors.toList());
        long min = 0;
        if (collect.size() != 0) {
            min = Collections.min(collect);
        }
        return min;
    }


    /**
     * Metoda sprawdza i zwraca warunek logiczny czy dany element został już użyty w ostatnio używanych p(i)
     *
     * @param basePiElem           - element do znalezienia
     * @param lastUsedBasedPiRoads - lista ostatnio używanych p(i)
     * @return warunek logiczny czy lista zawiera element
     */
    public boolean findOutIfWasUsed(Long basePiElem, Set<Long> lastUsedBasedPiRoads) {
        return lastUsedBasedPiRoads.contains(basePiElem);
    }

    /**
     * Metoda aktualizuje listę o podaną wartość
     *
     * @param min                  - wartość minimum do aktualizacji listy
     * @param lastUsedBasedPiRoads - lista do zaktualizowania
     */
    public void updateListOfUsedPiData(Long min, Set<Long> lastUsedBasedPiRoads) {
        lastUsedBasedPiRoads.add(min);
    }

    /**
     * Metoda sprawdza czy lista p(i) posiada wartości mniejsze od aktualnego opoóźnienia.
     *
     * @param lastUsedBasedPiRoads - lista ostatnio używanych elementow p(i)
     * @param delay                - opóźnienie
     * @param staticBasePi         - lista p(i)
     * @return warunek logiczny czy posiada mniejsze wartości opóźnienia lub nie
     */
    public boolean isHavingSmallerValueThanCurrentDelay(Set<Long> lastUsedBasedPiRoads, long delay, List<Long> staticBasePi) {
        Object[] objects = lastUsedBasedPiRoads.toArray();
        Long lastUsedValueFromBasedPi = (Long) objects[0];

        boolean haveSmallerValue = staticBasePi.stream().anyMatch(aLong -> {
            boolean smallerThanDelay = aLong < delay;
            boolean equalToLastUsedValue = aLong.equals(lastUsedValueFromBasedPi);
            boolean used = findOutIfWasUsed(aLong, lastUsedBasedPiRoads);
            return smallerThanDelay && !equalToLastUsedValue && !used;
        });

        return haveSmallerValue;
    }


    /**
     * Metoda aktualizuje sumę czasów obróbek poprzez różnicę aktualnej wartości o najmniejszą wartość
     *
     * @param sumOfTheMakeOrderTimes - suma czasów obróbek T0
     * @param makeTimeData           - wartość o którą suma ma zostać pomniejszona z czasów obróbek T0
     * @return zaktualizowana wartość sumy czasów obróbek T0
     */
    public long updateSumOfMakeOfOrderTimes(long sumOfTheMakeOrderTimes, long makeTimeData) {
        return sumOfTheMakeOrderTimes - makeTimeData;
    }

    /**
     * Metoda aktualizuje opóźnienie o wartość
     *
     * @param smallestValue - wartość o którą opóźnienie ma zostać zaktualizowane
     * @param delay         - wartość opóźnienia
     * @return aktualna wartość opóźnienia
     */
    public long updateDelay(long smallestValue, long delay) {
        if (smallestValue < 0) {
            smallestValue = 0;
        }
        delay = delay + smallestValue;
        return delay;
    }


    /**
     * Metoda tworzy ciąg znaków określający układ zleceń
     *
     * @param order - lista ze zleceniami w formie [z1,z2,z3 itp]
     * @return ciąg znaków np U(z1 z2 z3 z4)
     */
    public String generateOrderString(List<String> order) {
        final String[] orderAsString = {""};
        IntStream.range(0, order.size())
                .forEach(value -> {
                    int index = order.size() - 1 - value;
                    orderAsString[0] = orderAsString[0] + " " + order.get(index);
                });
        return StringWorker.generateRetrieveString("U(", orderAsString[0], ")");
    }


    /**
     * Metoda znajduje najmniejszą wartość listy
     *
     * @param countedBasePi - lista w której ma zostac znaleziona najmniejsza wartość
     * @return najmniejsza wartość z listy
     */
    public long findSmallestValue(List<Long> countedBasePi) {
        if (countedBasePi.size() == 0) {
            return 0;
        } else {
            return Collections.min(countedBasePi);
        }
    }
}
