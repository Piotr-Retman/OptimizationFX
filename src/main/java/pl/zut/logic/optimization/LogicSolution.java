package pl.zut.logic.optimization;

import pl.zut.logic.optimization.helpers.HelperObject;
import pl.zut.logic.optimization.helpers.LogicHelper;
import pl.zut.logic.optimization.helpers.StringWorker;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa rozwiązująca problem Mopt
 */
public class LogicSolution {

    private final static Logger LOGGER = Logger.getLogger(LogicSolution.class.getName());
    String finalOrder;
    long finalDelay;
    List<String> order = new ArrayList<>();
    static long staticSumOfMakeOrderTimes;
    static List<Long> staticListMakeOrderTimes = new ArrayList<>();
    static List<Long> staticBasePi = new ArrayList<>();
    static List<Long> staticListDeadlineTimes = new ArrayList<>();
    static Map<String, Long> staticMapOrderShortNameToCurrentPiValue = new HashMap<>();
    static Map<Long, String> staticMapDeadlineToOrder = new HashMap<>();
    static Map<Long, String> staticMapMakeTimeToOrder = new HashMap<>();
    static Map<String, Long> staticMapOrderToDeadline = new HashMap<>();
    static Map<String, Long> staticMapOrderToMakeTime = new HashMap<>();
    static List<String> staticOrderNames = new ArrayList<>();
    static Set<Long> staticLastUsedBasedPiRoads = new HashSet<>();


    static List<HelperObject> listOfSolutionObjects = new ArrayList<>();
    long baseDelay = 0;
    long currentDelay = 0;
    List<String> finalOrderOfOrders = new ArrayList<>();

    public static List<Long> finalDeadLineData = new ArrayList<>();
    public static List<String> finalStaticOrderNames = new ArrayList<>();
    public static List<Long> finalMakeOrderData = new ArrayList<>();
    public static Long finalSumOfMakeOrderData = 0L;
    private boolean flag = true;
    private int size = 0;

    /**
     * Metoda to punkt startowy rozwiązania problemu Mopt
     *
     * @param makeOrderTimes czasy obróbek T0
     * @param deadlineTimes  czasy terminów Tt
     */
    public void solveThePoblem(List<Long> makeOrderTimes,
                               List<Long> deadlineTimes) {
        try {
            SimpleProcedure sp = new SimpleProcedure();
            long sumOfTheMakeOrderTimes = sp.countSumOfMakeOrderTimes(makeOrderTimes);
            staticSumOfMakeOrderTimes = sp.countSumOfMakeOrderTimes(makeOrderTimes);

            List<Long> countedBasePi = sp.countPi(sumOfTheMakeOrderTimes, deadlineTimes);
            staticBasePi = new ArrayList<>(countedBasePi);
            staticListMakeOrderTimes = new ArrayList<>(makeOrderTimes);
            staticListDeadlineTimes = new ArrayList<>(deadlineTimes);
            if (flag) {
                staticOrderNames = sp.createStaticOrderNames(makeOrderTimes.size());
                finalDeadLineData = new ArrayList<>(staticListDeadlineTimes);
                finalMakeOrderData = new ArrayList<>(staticListMakeOrderTimes);
                finalStaticOrderNames = new ArrayList<>(staticOrderNames);
                finalSumOfMakeOrderData = new Long(staticSumOfMakeOrderTimes);
                staticMapOrderToDeadline = (Map<String, Long>) LogicHelper.createMapOrderAndTime(finalDeadLineData, TypeMap.STRING_ON_LONG);
                staticMapOrderToMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(finalMakeOrderData, TypeMap.STRING_ON_LONG);
                staticMapDeadlineToOrder = (Map<Long, String>) LogicHelper.createMapOrderAndTime(finalDeadLineData, TypeMap.LONG_ON_STRING);
                staticMapMakeTimeToOrder = (Map<Long, String>) LogicHelper.createMapOrderAndTime(finalMakeOrderData, TypeMap.LONG_ON_STRING);
                size = makeOrderTimes.size() - 1;
                flag = false;
            }
            fullAlgorithmJob(countedBasePi, new Long(staticSumOfMakeOrderTimes));
        } catch (NullPointerException ex) {
            return;
        }

    }

    /**
     * Pełna logika wykonania algorytmu
     *
     * @param countedBasePi          obliczone p(i) bazowe
     * @param sumOfTheMakeOrderTimes suma czasów obróbek
     */
    private void fullAlgorithmJob(List<Long> countedBasePi, Long sumOfTheMakeOrderTimes) {
        if (countedBasePi.size() != 0) {
            SimpleProcedure sp = new SimpleProcedure();
            for (int i = 0; i < countedBasePi.size(); i++) {
                if (i == 0) {
                    long smallestValue = sp.findSmallestValue(countedBasePi);
                    solveSolution(countedBasePi, smallestValue, 0, new ArrayList<>(staticListMakeOrderTimes), new ArrayList<>(staticListDeadlineTimes), sumOfTheMakeOrderTimes, new ArrayList<>(staticOrderNames), currentDelay);
                    staticLastUsedBasedPiRoads.add(smallestValue);
                } else {
                    long smallestValue = sp.findSmallestValueOfPi(countedBasePi, staticLastUsedBasedPiRoads, baseDelay);
                    if (sp.isHavingSmallerValueThanCurrentDelay(staticLastUsedBasedPiRoads, baseDelay, countedBasePi)) {
                        solveSolution(countedBasePi, smallestValue, 0, new ArrayList<>(staticListMakeOrderTimes), new ArrayList<>(staticListDeadlineTimes), sumOfTheMakeOrderTimes, new ArrayList<>(staticOrderNames), currentDelay);
                        staticLastUsedBasedPiRoads.add(smallestValue);
                    }
                }
            }
            //Dodanie do listy kolejnego zlecenia uszeregowanego
            HelperObject lastOfCurrentQueueHelperObj = listOfSolutionObjects.get(listOfSolutionObjects.size() - 1);
            List<String> orderOfCurrentQueueHelperObj = lastOfCurrentQueueHelperObj.getOrder();
            String orderElemToSave = orderOfCurrentQueueHelperObj.get(0);
            finalOrderOfOrders.add(orderElemToSave);
            //Przygotowanie do dalszych obliczeń.
            if (finalOrderOfOrders.size() - 1 != size) {
                int indexOfReducing = staticOrderNames.indexOf(orderElemToSave);
                staticSumOfMakeOrderTimes = staticSumOfMakeOrderTimes - staticListMakeOrderTimes.get(indexOfReducing);
                staticOrderNames.remove(indexOfReducing);
                staticListDeadlineTimes.remove(indexOfReducing);
                staticListMakeOrderTimes.remove(indexOfReducing);
                staticLastUsedBasedPiRoads.clear();
                solveThePoblem(new ArrayList<>(staticListMakeOrderTimes), new ArrayList<>(staticListDeadlineTimes));
            } else {
                order = new ArrayList<>(finalOrderOfOrders);
                setFinalOrder(sp.generateOrderString(order));
                finalizeAlgorithm(order, new ArrayList<>(finalMakeOrderData), new ArrayList<>(finalDeadLineData), new Long(finalSumOfMakeOrderData), new ArrayList<>(finalStaticOrderNames));
            }
        }
    }

    /**
     * Finalizacja algorytmu
     *
     * @param order               układ
     * @param makeOrderData       lista czasów obróbek
     * @param deadLineData        lista terminów
     * @param sumOfMakeOrderTimes suma czasów obróbek
     * @param orderNames          lista nazw zleceń np [z1,z2,z3]
     */
    private void finalizeAlgorithm(List<String> order, ArrayList<Long> makeOrderData, ArrayList<Long> deadLineData, Long sumOfMakeOrderTimes, ArrayList<String> orderNames) {
        SimpleProcedure sp = new SimpleProcedure();
        long delay = 0;
        for (String orderName : order) {
            List<Long> countedPi = sp.countPi(sumOfMakeOrderTimes, deadLineData);
            int index = orderNames.indexOf(orderName);
            Long aLong = countedPi.get(index);
            if (aLong < 0) {
                aLong = 0L;
            }
            delay += aLong;
            sumOfMakeOrderTimes -= makeOrderData.get(index);
            makeOrderData.remove(index);
            deadLineData.remove(index);
            orderNames.remove(index);
        }
        finalDelay = delay;
    }

    /**
     * Rozwiązuje aktualny punkt algorytmu
     *
     * @param countedBasePi          bazowe p(i)
     * @param basePiElem             element listy p(i)
     * @param baseDelay              opóźnienie
     * @param makeOrderTimes         lista czasów obróbek
     * @param deadlineTimes          lista terminów
     * @param sumOfTheMakeOrderTimes suma czasów obróbek
     * @param ordersList             lista zleceń
     * @param currentDelay           akutalne opóźnienie
     */
    private void solveSolution(List<Long> countedBasePi,
                               Long basePiElem,
                               long baseDelay,
                               List<Long> makeOrderTimes,
                               List<Long> deadlineTimes,
                               long sumOfTheMakeOrderTimes,
                               List<String> ordersList,
                               long currentDelay) {
        SimpleProcedure sp = new SimpleProcedure();
        try {
            if (sumOfTheMakeOrderTimes != 0) {
                int indexOfEverything = countedBasePi.indexOf(basePiElem);
                long updatedSumOfMakeTimes = sp.updateSumOfMakeOfOrderTimes(sumOfTheMakeOrderTimes, makeOrderTimes.get(indexOfEverything));
                long delay = sp.updateDelay(basePiElem, currentDelay);
                baseDelay += delay;
                order.add(ordersList.get(indexOfEverything));
                deadlineTimes.remove(indexOfEverything);
                makeOrderTimes.remove(indexOfEverything);
                ordersList.remove(indexOfEverything);
                List<Long> newPi = sp.countPi(updatedSumOfMakeTimes, deadlineTimes);
                long smallestValue = sp.findSmallestValue(newPi);
                solveSolution(newPi, smallestValue, baseDelay, makeOrderTimes, deadlineTimes, updatedSumOfMakeTimes, ordersList, currentDelay);
            } else {
                if (this.baseDelay == 0) {
                    this.baseDelay = new Long(baseDelay);
                    listOfSolutionObjects.add(new HelperObject(TypeSolution.STEP_SOLUTION, new ArrayList<String>(order), sp.generateOrderString(order), this.baseDelay));
                } else if (this.baseDelay >= baseDelay) {
                    this.baseDelay = new Long(baseDelay);
                    listOfSolutionObjects.add(new HelperObject(TypeSolution.STEP_SOLUTION, new ArrayList<String>(order), sp.generateOrderString(new ArrayList<>(order)), this.baseDelay));
                }
                order.clear();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void clearStatics() {
        try {
            staticSumOfMakeOrderTimes = 0;
            staticListMakeOrderTimes.clear();
            staticBasePi.clear();
            staticListDeadlineTimes.clear();
            staticMapOrderShortNameToCurrentPiValue.clear();
            staticLastUsedBasedPiRoads = new HashSet<>();
        } catch (NullPointerException ex) {
            //no need to handle this
        }
    }


    /*Gettery i Settery*/

    public String getFinalOrder() {
        return finalOrder;
    }

    public void setFinalOrder(String finalOrder) {
        this.finalOrder = finalOrder;
    }

    public long getFinalDelay() {
        return finalDelay;
    }

    public List<String> getOrder() {
        return order;
    }

    public static Map<String, Long> getStaticMapOrderToDeadline() {
        return staticMapOrderToDeadline;
    }

    public static Map<String, Long> getStaticMapOrderToMakeTime() {
        return staticMapOrderToMakeTime;
    }

    public static Long getFinalSumOfMakeOrderData() {
        return finalSumOfMakeOrderData;
    }
}
