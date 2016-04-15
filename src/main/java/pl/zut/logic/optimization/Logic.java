package pl.zut.logic.optimization;

import java.util.*;

/**
 * Created by Retman on 2016-02-27.
 */
public class Logic {
    long finalDelay;
    long delay;

    String finalOrder;
    String baseOrder = "";
    List<String> order = new ArrayList<>();

    static long staticSumOfMakeOrderTimes;

    static List<Long> staticListMakeOrderTimes = new ArrayList<>();
    List<Long> listMakeOrderTimes = new ArrayList<>();
    static long staticBaseDelay;
    static List<Long> staticBasePi = new ArrayList<>();

    List<Long> listDeadLineTimes = new ArrayList<>();
    static List<Long> staticListDeadlineTimes = new ArrayList<>();

    static Map<String, Long> mapOrderShortNameToCurrentPiValue = new HashMap<>();

    static Map<String, Long> mapMakeTimeToOrder = new HashMap<>();
    static Map<String, Long> mapDeadlineToOrder = new HashMap<>();

    static Map<String, Long> staticMapOrderShortNameToCurrentPiValue = new HashMap<>();
    static Map<String, Long> mapToChart = new HashMap<>();


    boolean flag = true;
    boolean flagAfterFirstSolution = false;
    boolean flagCreateBaseOrderString = true;

    static Set<Long> lastUsedBasedPiRoads = new HashSet<>();
    List<String> orderToChart = new ArrayList<>();

    /*Gettery i Settery*/

    static void setStaticSumOfMakeOrderTimes(long staticSumOfMakeOrderTimes) {
        LogicSolution.staticSumOfMakeOrderTimes = staticSumOfMakeOrderTimes;
    }

    public static long getStaticSumOfMakeOrderTimes() {
        return staticSumOfMakeOrderTimes;
    }

    public long getFinalDelay() {
        return finalDelay;
    }

    public String getFinalOrder() {
        return finalOrder;
    }

    public String getBaseOrder() {
        return baseOrder;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setListMakeOrderTimes(List<Long> listMakeOrderTimes) {
        this.listMakeOrderTimes = listMakeOrderTimes;
    }

    public void setListDeadLineTimes(List<Long> listDeadLineTimes) {
        this.listDeadLineTimes = listDeadLineTimes;
    }

    public static long getStaticBaseDelay() {
        return staticBaseDelay;
    }

    public static Map<String, Long> getMapMakeTimeToOrder() {
        return mapMakeTimeToOrder;
    }

    public static void setMapMakeTimeToOrder(Map<String, Long> mapMakeTimeToOrder) {
        Logic.mapMakeTimeToOrder = mapMakeTimeToOrder;
    }

    public static Map<String, Long> getMapDeadlineToOrder() {
        return mapDeadlineToOrder;
    }

    public static void setMapDeadlineToOrder(Map<String, Long> mapDeadlineToOrder) {
        Logic.mapDeadlineToOrder = mapDeadlineToOrder;
    }
}
