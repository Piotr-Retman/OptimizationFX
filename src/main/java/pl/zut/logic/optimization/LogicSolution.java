package pl.zut.logic.optimization;

import pl.zut.helpers.StringWorker;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Retman on 2015-09-21.
 */
public class LogicSolution extends Logic {

    private final static Logger LOGGER = Logger.getLogger(LogicSolution.class.getName());
    /**
     * Metoda tworzy listę na podstawie ciągu znaków podanych w strumieniu danych.
     *
     * @param listAsString - lista jako String
     * @return lista fizyczna ze Stringa
     */
    public List<Long> prepareListBasedOnString(String listAsString) {
        return LogicHelper.createListOfValuesBasedOnString(listAsString);
    }


    /**
     * Metoda główna - rozwiązuje główny algorytm (ale nie rozpoczyna go)
     *
     * @param makeOrderTimes - czasy obróbek T0
     * @param deadlineTimes  - terminy wykonania Tt
     */
    public void solveThePoblem(List<Long> makeOrderTimes,
                               List<Long> deadlineTimes,
                               long sumOfTheMakeOrderTimesWhenNotFlag) {
        if (flag) {
            flag = false;
            long sumOfTheMakeOrderTimes = countSumOfMakeOrderTimes(makeOrderTimes);
            List<Long> countedBasePi = countPi(sumOfTheMakeOrderTimes, deadlineTimes);
            staticBasePi = new ArrayList<>(countedBasePi);
            staticListMakeOrderTimes = new ArrayList<>(makeOrderTimes);
            staticListDeadlineTimes = new ArrayList<>(deadlineTimes);
            makeMapBasePiToOrderShortName(countedBasePi);
            lastUsedBasedPiRoads.add(findSmallestValue(countedBasePi));
            startSolution(countedBasePi, sumOfTheMakeOrderTimes);
        } else {
            List<Long> countedPi = countPi(sumOfTheMakeOrderTimesWhenNotFlag, deadlineTimes);
            startSolution(countedPi, sumOfTheMakeOrderTimesWhenNotFlag);
        }
    }

    /**
     * Metoda rozpoczyna rozwiązanie algorytmu
     *
     * @param countedBasePi          - jest to bazowe p(i)
     * @param sumOfTheMakeOrderTimes - suma czasów obróbek (T0)
     */
    private void startSolution(List<Long> countedBasePi, long sumOfTheMakeOrderTimes) {
        if (sumOfTheMakeOrderTimes > 0) {
            long smallestValue = 0;
            if (!flagAfterFirstSolution) {
                smallestValue = findSmallestValue(countedBasePi);
            } else {
                flagAfterFirstSolution = false;
                smallestValue = findSmallestValueWhenNextTurn(countedBasePi);
                finalDelay = delay;
                delay = 0;
            }
            int indexOfElementInOrderOfOrders = countedBasePi.indexOf(smallestValue);
            countedBasePi.remove(indexOfElementInOrderOfOrders);
            Object[] objects = mapOrderShortNameToCurrentPiValue.keySet().toArray();

            order.add(objects[indexOfElementInOrderOfOrders].toString());
            mapOrderShortNameToCurrentPiValue.remove(objects[indexOfElementInOrderOfOrders].toString());
            updateDelay(smallestValue);
            Long valueToMinusSumOfMakeOrdrTimes = listMakeOrderTimes.get(indexOfElementInOrderOfOrders);
            listMakeOrderTimes.remove(indexOfElementInOrderOfOrders);
            listDeadLineTimes.remove(indexOfElementInOrderOfOrders);
            long currentSumOfTheMakeOrderTimes = updateSumOfTheSmallestMakeOfOrderTimes(sumOfTheMakeOrderTimes, valueToMinusSumOfMakeOrdrTimes);
            solveThePoblem(listMakeOrderTimes, listDeadLineTimes, currentSumOfTheMakeOrderTimes);
        } else {
            finalisationOfAlgorithm();
        }
    }

    private void finalisationOfAlgorithm() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Rozpoczynam finalizację algorytmu metodą MOpt...");
        orderToChart = new ArrayList<>(order);
        boolean forwardTheLogic = checkIfHaveSmallerValueThanCurrentDelay(delay);
        generateOrderString();
        // TODO: 2016-04-14  zmieniono przez dodanie delay >= staticBaseDelay
        LOGGER.info("Opóźnienie: "+delay + " Końcowe opóźnienie: "+finalDelay + " Bazowe opóźnienie: " + staticBaseDelay);
        if (delay >= finalDelay && finalDelay != 0 && delay >= staticBaseDelay) {
            flag = true;
//            LOGGER.info("Finalne opóźnienie: "+ String.valueOf(finalDelay));
        } else {
            prepareTheRerun(forwardTheLogic);
        }
    }

    /**
     * Metoda na podstawie wartości logicznej uruchamia dalsze obliczenia algorytmu lub kończy logikę wykonywania kodu
     *
     * @param forwardTheLogic - flaga określająca dalsze czynności w algorytmie
     */
    private void prepareTheRerun(boolean forwardTheLogic) {
        LOGGER.setLevel(Level.ALL);
        if (forwardTheLogic) {
            flagAfterFirstSolution = true;
            List<Long> restartedBaseCountedPi = new ArrayList<>(staticBasePi);
            long restartSumOfMakeOrderTimes = getStaticSumOfMakeOrderTimes();
            mapOrderShortNameToCurrentPiValue.putAll(staticMapOrderShortNameToCurrentPiValue);
            mapToChart = new HashMap<>(staticMapOrderShortNameToCurrentPiValue);
            listMakeOrderTimes = new ArrayList<>(staticListMakeOrderTimes);
            listDeadLineTimes = new ArrayList<>(staticListDeadlineTimes);
            order.clear();
            startSolution(restartedBaseCountedPi, restartSumOfMakeOrderTimes);
        } else {
            flag = true;
            finalDelay = delay;
            LOGGER.info(String.valueOf(finalDelay));
        }
    }

    /**
     * Metoda znajduje najmniejszą wartość z listy kiedy następuje inna niż pierwsza tura obliczeń (1 < blok)
     *
     * @param countedBasePi - lista, bazowe p(i)
     * @return najmniejsza wartość w liście zgodnie z warunkami
     */
    private long findSmallestValueWhenNextTurn(List<Long> countedBasePi) {
        Object[] objects = lastUsedBasedPiRoads.toArray();
        Long lastUsedValueFromBasedPi = (Long) objects[0];

        List<Long> collect = countedBasePi.stream().filter(aLong -> aLong < delay && !findOutIfWasUsed(aLong, lastUsedBasedPiRoads)).collect(Collectors.toList());
        Long min = Collections.min(collect);
        updateListOfUsedPiData(min);
        return min;
    }

    private void updateListOfUsedPiData(Long min) {
        lastUsedBasedPiRoads.add(min);
    }

    /**
     * Określa wartość logiczną czy element został już użyty
     *
     * @param basePiElem           - element do sprawdzenia
     * @param lastUsedBasedPiRoads - lista wartości które zostały już użyte w algorytmie
     * @return wartosć logiczna czy dana wartość została użyta
     */
    private boolean findOutIfWasUsed(Long basePiElem, Set<Long> lastUsedBasedPiRoads) {
        return lastUsedBasedPiRoads.contains(basePiElem);
    }

    /**
     * Metoda przygotowuje do wyświetlenia kolejność zleceń jako String
     */
    private void generateOrderString() {
        LOGGER.setLevel(Level.ALL);
        if (flagCreateBaseOrderString) {
            IntStream.range(0, order.size())
                    .forEach(value -> {
                        int index = order.size() - 1 - value;
                        baseOrder = baseOrder + " " + order.get(index);
                    });
            baseOrder = "U(" + baseOrder + ")";
            flagCreateBaseOrderString = false;
            staticBaseDelay = delay;
            LOGGER.info(baseOrder);
        } else {
            finalOrder = "";
            for (int i = order.size() - 1; i >= 0; i--) {
                finalOrder = finalOrder + " " + order.get(i);
            }
            finalOrder = "U(" + finalOrder + ")";
            LOGGER.info(finalOrder);
        }

    }

    /**
     * Sprawdza czy lista ostatnio użytych wartości ma wartości mniejsze od podanego opóźnienia
     *
     * @param delay - aktualne opóźnienie
     * @return wartość logiczna określająca czy istnieją wartości mniejsze od opóźnienia
     */
    private boolean checkIfHaveSmallerValueThanCurrentDelay(long delay) {
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
     * Aktualizacja sumy czasów obróbki
     *
     * @param sumOfTheMakeOrderTimes - aktualna suma czasów obróbki
     * @param smallestValue          - wartośc do odjęcia od sumy czasów obróbki
     * @return
     */
    private long updateSumOfTheSmallestMakeOfOrderTimes(long sumOfTheMakeOrderTimes, long smallestValue) {
        return sumOfTheMakeOrderTimes - smallestValue;
    }


    /**
     * Aktualizacja opóźnienia
     *
     * @param smallestValue - najmniejsza wartość określająca zgodnie z algorytmem element listy p(i)
     */
    private void updateDelay(long smallestValue) {
        if (smallestValue < 0) {
            smallestValue = 0;
        }
        delay = delay + smallestValue;
    }

    /**
     * Generuje mapę p(i) bazowego z nazwami zleceń (np z1 -> -20)
     *
     * @param countedBasePi - obliczone bazowe p(i)
     */
    private void makeMapBasePiToOrderShortName(List<Long> countedBasePi) {
        String orderShortName = "z";
        for (int i = 0; i < countedBasePi.size(); i++) {
            i++;
            int partOfName = i;
            i--;
            mapOrderShortNameToCurrentPiValue.put(orderShortName + partOfName, countedBasePi.get(i));
        }
        staticMapOrderShortNameToCurrentPiValue.putAll(mapOrderShortNameToCurrentPiValue);
        Map<String, Long> collect = staticMapOrderShortNameToCurrentPiValue.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        staticMapOrderShortNameToCurrentPiValue = collect;
    }

    /**
     * Znajduje najmniejszą wartość kolekcji
     *
     * @param countedBasePi - lista z której ma zostać znaleziona najmniejsza wartość
     * @return zwraca typ long określający najmniejszą wartość listy
     */
    private long findSmallestValue(List<Long> countedBasePi) {
        return Collections.min(countedBasePi);
    }

    /**
     * Liczy sumę czasów obróbek
     *
     * @param makeOrderTimes - lista czasów obróbek
     * @return zwraca typ long określający wartość sumy czasów obróbek
     */
    public long countSumOfMakeOrderTimes(List<Long> makeOrderTimes) {
        final long sumOfMakeOrderTimes[] = {0L};

        makeOrderTimes.stream().forEach(makeOrderTime ->
                sumOfMakeOrderTimes[0] = sumOfMakeOrderTimes[0] + makeOrderTime

        );
        setStaticSumOfMakeOrderTimes(sumOfMakeOrderTimes[0]);
        return sumOfMakeOrderTimes[0];
    }


    /**
     * @param sumOfTheMakeTime - suma czasów obróbek T0
     * @param deadlineTimes    - terminy wykonania Tt
     * @return countedPi - lista obliczonych p(i)
     */
    private List<Long> countPi(Long sumOfTheMakeTime, List<Long> deadlineTimes) {
        List countedPi = new ArrayList<>();
        if (flagAfterFirstSolution) {

        } else {
            deadlineTimes.stream().forEach(deadLineTime -> {
                long piElement = sumOfTheMakeTime - deadLineTime;
                countedPi.add(piElement);
            });
        }
        return countedPi;
    }

    /**
     * Metoda przygotowująca wartości statyczne oraz listy do stanu sprzed uruchomienia algorytmu
     */
    public void clearStatics() {
        try {
            staticSumOfMakeOrderTimes = 0;
            staticListMakeOrderTimes.clear();
            staticBasePi.clear();
            staticListDeadlineTimes.clear();
            mapOrderShortNameToCurrentPiValue.clear();
            staticMapOrderShortNameToCurrentPiValue.clear();
            lastUsedBasedPiRoads = new HashSet<>();
            finalDelay = 0;
            delay = 0;
            finalOrder = "";
            baseOrder = "";
            order.clear();
            listMakeOrderTimes.clear();
            listDeadLineTimes.clear();
            flag = true;
            flagAfterFirstSolution = false;
            flagCreateBaseOrderString = true;
//            staticBaseDelay = 0;
        } catch (NullPointerException ex) {
            //no need to handle this
        }
    }

}
