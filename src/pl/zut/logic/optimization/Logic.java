package pl.zut.logic.optimization;

import java.util.*;

/**
 * Created by Retman on 2016-02-27.
 */
public class Logic{
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
    static Map<String, Long> mapDeadlineToOrder= new HashMap<>();

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

    void setFinalOrder(String finalOrder) {
        this.finalOrder = finalOrder;
    }

    public long getFinalDelay() {
        return finalDelay;
    }

    public void setFinalDelay(long finalDelay) {
        this.finalDelay = finalDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getFinalOrder() {
        return finalOrder;
    }

    public String getBaseOrder() {
        return baseOrder;
    }

    public void setBaseOrder(String baseOrder) {
        this.baseOrder = baseOrder;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public static List<Long> getStaticListMakeOrderTimes() {
        return staticListMakeOrderTimes;
    }

    public static void setStaticListMakeOrderTimes(List<Long> staticListMakeOrderTimes) {
        Logic.staticListMakeOrderTimes = staticListMakeOrderTimes;
    }

    public List<Long> getListMakeOrderTimes() {
        return listMakeOrderTimes;
    }

    public void setListMakeOrderTimes(List<Long> listMakeOrderTimes) {
        this.listMakeOrderTimes = listMakeOrderTimes;
    }

    public static List<Long> getStaticBasePi() {
        return staticBasePi;
    }

    public static void setStaticBasePi(List<Long> staticBasePi) {
        Logic.staticBasePi = staticBasePi;
    }

    public List<Long> getListDeadLineTimes() {
        return listDeadLineTimes;
    }

    public void setListDeadLineTimes(List<Long> listDeadLineTimes) {
        this.listDeadLineTimes = listDeadLineTimes;
    }

    public static List<Long> getStaticListDeadlineTimes() {
        return staticListDeadlineTimes;
    }

    public static void setStaticListDeadlineTimes(List<Long> staticListDeadlineTimes) {
        Logic.staticListDeadlineTimes = staticListDeadlineTimes;
    }

    public static Map<String, Long> getMapOrderShortNameToCurrentPiValue() {
        return mapOrderShortNameToCurrentPiValue;
    }

    public static void setMapOrderShortNameToCurrentPiValue(Map<String, Long> mapOrderShortNameToCurrentPiValue) {
        Logic.mapOrderShortNameToCurrentPiValue = mapOrderShortNameToCurrentPiValue;
    }

    public static Map<String, Long> getStaticMapOrderShortNameToCurrentPiValue() {
        return staticMapOrderShortNameToCurrentPiValue;
    }

    public static void setStaticMapOrderShortNameToCurrentPiValue(Map<String, Long> staticMapOrderShortNameToCurrentPiValue) {
        Logic.staticMapOrderShortNameToCurrentPiValue = staticMapOrderShortNameToCurrentPiValue;
    }

    public static Map<String, Long> getMapToChart() {
        return mapToChart;
    }

    public static void setMapToChart(Map<String, Long> mapToChart) {
        Logic.mapToChart = mapToChart;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlagAfterFirstSolution() {
        return flagAfterFirstSolution;
    }

    public void setFlagAfterFirstSolution(boolean flagAfterFirstSolution) {
        this.flagAfterFirstSolution = flagAfterFirstSolution;
    }

    public boolean isFlagCreateBaseOrderString() {
        return flagCreateBaseOrderString;
    }

    public void setFlagCreateBaseOrderString(boolean flagCreateBaseOrderString) {
        this.flagCreateBaseOrderString = flagCreateBaseOrderString;
    }

    public static Set<Long> getLastUsedBasedPiRoads() {
        return lastUsedBasedPiRoads;
    }

    public static void setLastUsedBasedPiRoads(Set<Long> lastUsedBasedPiRoads) {
        Logic.lastUsedBasedPiRoads = lastUsedBasedPiRoads;
    }

    public List<String> getOrderToChart() {
        return orderToChart;
    }

    public void setOrderToChart(List<String> orderToChart) {
        this.orderToChart = orderToChart;
    }

    public static long getStaticBaseDelay() {
        return staticBaseDelay;
    }


    public static void setStaticBaseDelay(long staticBaseDelay) {
        Logic.staticBaseDelay = staticBaseDelay;
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
