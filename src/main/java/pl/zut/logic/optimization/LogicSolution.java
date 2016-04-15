package pl.zut.logic.optimization;

import javafx.util.Pair;
import pl.zut.logic.optimization.helpers.HelperObject;
import pl.zut.logic.optimization.helpers.LogicHelper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Retman on 2015-09-21.
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
    static Set<Long> lastUsedBasedPiRoads = new HashSet<>();

    static Map<Integer,HelperObject> mapSolutionOnHelper = new HashMap<>();

    public void solveThePoblem(List<Long> makeOrderTimes,
                               List<Long> deadlineTimes) {
        SimpleProcedure sp = new SimpleProcedure();
        long sumOfTheMakeOrderTimes = sp.countSumOfMakeOrderTimes(makeOrderTimes);
        staticSumOfMakeOrderTimes = sp.countSumOfMakeOrderTimes(makeOrderTimes);
        List<Long> countedBasePi = sp.countPi(sumOfTheMakeOrderTimes, deadlineTimes);
        staticOrderNames = sp.createStaticOrderNames(makeOrderTimes.size());
        List<String> ordersList = new ArrayList<>(staticOrderNames);
        staticBasePi = new ArrayList<>(countedBasePi);
        staticListMakeOrderTimes = new ArrayList<>(makeOrderTimes);
        staticListDeadlineTimes = new ArrayList<>(deadlineTimes);
        staticMapOrderToDeadline = (Map<String, Long>) LogicHelper.createMapOrderAndTime(staticListDeadlineTimes, TypeMap.STRING_ON_LONG);
        staticMapOrderToMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(staticListMakeOrderTimes, TypeMap.STRING_ON_LONG);
        staticMapDeadlineToOrder = (Map<Long, String>) LogicHelper.createMapOrderAndTime(staticListDeadlineTimes, TypeMap.LONG_ON_STRING);
        staticMapMakeTimeToOrder = (Map<Long, String>) LogicHelper.createMapOrderAndTime(staticListMakeOrderTimes, TypeMap.LONG_ON_STRING);
        solveBaseSolution(countedBasePi, 0L, "", makeOrderTimes, deadlineTimes, sumOfTheMakeOrderTimes, ordersList);
        lastUsedBasedPiRoads.add(sp.findSmallestValue(countedBasePi));
        solveOptimalSolution();
    }

    private void solveOptimalSolution() {
        // TODO: 2016-04-15 Rozwiązanie algorytmu tutaj powinno się znaleźć
    }

    private void solveBaseSolution(List<Long> countedBasePi, Long delay, String orderOfBase, List<Long> makeOrderTimes, List<Long> deadlineTimes, long sumOfTheMakeOrderTimes, List<String> orders) {
        if (sumOfTheMakeOrderTimes != 0) {
            SimpleProcedure sp = new SimpleProcedure();
            long smallestValue = sp.findSmallestValue(countedBasePi);
            int index = countedBasePi.indexOf(smallestValue);
            Long foundMakeTime = makeOrderTimes.get(index);
            String orderName = orders.get(index);
            order.add(orderName);
            delay = sp.updateDelay(smallestValue, delay);
            orderOfBase = sp.generateOrderString(order);
            sumOfTheMakeOrderTimes = sp.updateSumOfTheSmallestMakeOfOrderTimes(sumOfTheMakeOrderTimes, foundMakeTime);
            deadlineTimes.remove(index);
            makeOrderTimes.remove(index);
            orders.remove(index);
            List<Long> newPi = sp.countPi(sumOfTheMakeOrderTimes, deadlineTimes);
            solveBaseSolution(newPi, delay, orderOfBase, makeOrderTimes, deadlineTimes, sumOfTheMakeOrderTimes, orders);
        }else{
            mapSolutionOnHelper.put(1,new HelperObject(TypeSolution.BASE_SOLUTION,new ArrayList<String>(order),orderOfBase,delay));
            order.clear();
        }
    }

    public void clearStatics() {
        try {
            staticSumOfMakeOrderTimes = 0;
            staticListMakeOrderTimes.clear();
            staticBasePi.clear();
            staticListDeadlineTimes.clear();
            staticMapOrderShortNameToCurrentPiValue.clear();
            lastUsedBasedPiRoads = new HashSet<>();
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

    public void setFinalDelay(long finalDelay) {
        this.finalDelay = finalDelay;
    }

    public static long getStaticSumOfMakeOrderTimes() {
        return staticSumOfMakeOrderTimes;
    }

    public static void setStaticSumOfMakeOrderTimes(long staticSumOfMakeOrderTimes) {
        LogicSolution.staticSumOfMakeOrderTimes = staticSumOfMakeOrderTimes;
    }

    public static List<Long> getStaticListMakeOrderTimes() {
        return staticListMakeOrderTimes;
    }

    public static void setStaticListMakeOrderTimes(List<Long> staticListMakeOrderTimes) {
        LogicSolution.staticListMakeOrderTimes = staticListMakeOrderTimes;
    }

    public static List<Long> getStaticBasePi() {
        return staticBasePi;
    }

    public static void setStaticBasePi(List<Long> staticBasePi) {
        LogicSolution.staticBasePi = staticBasePi;
    }

    public static List<Long> getStaticListDeadlineTimes() {
        return staticListDeadlineTimes;
    }

    public static void setStaticListDeadlineTimes(List<Long> staticListDeadlineTimes) {
        LogicSolution.staticListDeadlineTimes = staticListDeadlineTimes;
    }

    public static Map<String, Long> getStaticMapOrderShortNameToCurrentPiValue() {
        return staticMapOrderShortNameToCurrentPiValue;
    }

    public static void setStaticMapOrderShortNameToCurrentPiValue(Map<String, Long> staticMapOrderShortNameToCurrentPiValue) {
        LogicSolution.staticMapOrderShortNameToCurrentPiValue = staticMapOrderShortNameToCurrentPiValue;
    }

    public static Set<Long> getLastUsedBasedPiRoads() {
        return lastUsedBasedPiRoads;
    }

    public static void setLastUsedBasedPiRoads(Set<Long> lastUsedBasedPiRoads) {
        LogicSolution.lastUsedBasedPiRoads = lastUsedBasedPiRoads;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public static Map<String, Long> getStaticMapOrderToDeadline() {
        return staticMapOrderToDeadline;
    }

    public static Map<String, Long> getStaticMapOrderToMakeTime() {
        return staticMapOrderToMakeTime;
    }
}
