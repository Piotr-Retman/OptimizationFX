package pl.zut.logic.optimization;

import pl.zut.logic.optimization.helpers.LogicHelper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Klasa zawiera metody potrzebne do obliczenia MT,MO,MZa
 */
public class DifferentMethodologies extends LogicSolution {

    private final static Logger LOGGER = Logger.getLogger(DifferentMethodologies.class.getName());

    private String increaseMakeTimeOrder = "";
    private long increateMakeTimeDelay = 0;

    private String supplyIncreaseOrder = "";
    private long supplyIncreaseTimeDelay = 0;

    private String increaseDeadLineTimeOrder = "";
    private long increaseDeadLineTimeDelay = 0;


    private List<String> helperKeys = new ArrayList<>();

    /**
     * Oblicza metodę MT
     */
    public void countIncreasingDeadLineTimesOrder() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Rozpoczynam obliczanie metodą MT...");
        List<Long> makeOrderTimes = new ArrayList(LogicSolution.getFinalMakeTimeOrderData());
        List<Long> deadlineOrderTimes = new ArrayList(LogicSolution.getFinalDeadlineOrderTimesData());
        Map<String, Long> mapOrderAndTimeMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(makeOrderTimes,TypeMap.STRING_ON_LONG);
        Map<String, Long> mapOrderAndTimeDeadLine = (Map<String, Long>) LogicHelper.createMapOrderAndTime(deadlineOrderTimes,TypeMap.STRING_ON_LONG);
        Collections.sort(deadlineOrderTimes);
        List<Long> mappedValues = new ArrayList(mapOrderAndTimeDeadLine.values());
        List<String> keys = new ArrayList<>(mapOrderAndTimeMakeTime.keySet());
        increaseDeadLineTimeOrder = prepareOrderForSupplyIncreaseOrder(mappedValues, keys, deadlineOrderTimes);
        increaseDeadLineTimeDelay = delayCount(mapOrderAndTimeDeadLine, mapOrderAndTimeMakeTime);

    }

    /**
     * Oblicza metodę MO
     * @param ls obiekt klasy LogicSolution
     */
    public void countIncreasingMakeTimesOrder() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Rozpoczynam obliczanie metodą M0...");
        List<Long> makeOrderTimes = new ArrayList(LogicSolution.getFinalMakeTimeOrderData());
        List<Long> deadlineOrderTimes = new ArrayList(LogicSolution.getFinalDeadlineOrderTimesData());
        Map<String, Long> mapOrderAndTimeMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(makeOrderTimes,TypeMap.STRING_ON_LONG);
        Map<String, Long> mapOrderAndTimeDeadLine = (Map<String, Long>) LogicHelper.createMapOrderAndTime(deadlineOrderTimes,TypeMap.STRING_ON_LONG);
        Collections.sort(makeOrderTimes);
        List<Long> mappedValues = new ArrayList(mapOrderAndTimeMakeTime.values());
        List<String> keys = new ArrayList<>(mapOrderAndTimeMakeTime.keySet());
        increaseMakeTimeOrder = prepareOrderForSupplyIncreaseOrder(mappedValues, keys, makeOrderTimes);
        increateMakeTimeDelay = delayCount(mapOrderAndTimeDeadLine, mapOrderAndTimeMakeTime);

    }


    /**
     * Oblicza metodę MZa
     * @param ls obiekt klasy LogicSolution
     */
    public void countSupplyIncreaseOrder() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Rozpoczynam obliczanie metodą MZa...");
        List<Long> makeOrderTimes = new ArrayList(LogicSolution.getFinalMakeTimeOrderData());
        List<Long> deadlineOrderTimes = new ArrayList(LogicSolution.getFinalDeadlineOrderTimesData());
        List<Long> supplyLongs = LogicHelper.createSupplyLongs(makeOrderTimes, deadlineOrderTimes);
        Map<String, Long> mapOrderAndTimeSupplies = (Map<String, Long>) LogicHelper.createMapOrderAndTime(supplyLongs,TypeMap.STRING_ON_LONG);
        Map<String, Long> mapOrderAndTimeDeadLine = (Map<String, Long>) LogicHelper.createMapOrderAndTime(deadlineOrderTimes,TypeMap.STRING_ON_LONG);
        Map<String, Long> mapOrderAndTimeMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(makeOrderTimes,TypeMap.STRING_ON_LONG);

        Collections.sort(supplyLongs);
        List<Long> mappedValues = new ArrayList(mapOrderAndTimeSupplies.values());
        List<String> keys = new ArrayList<>(mapOrderAndTimeSupplies.keySet());
        supplyIncreaseOrder = prepareOrderForSupplyIncreaseOrder(mappedValues, keys, supplyLongs);

        supplyIncreaseTimeDelay = delayCount(mapOrderAndTimeDeadLine, mapOrderAndTimeMakeTime);
    }

    /**
     * Oblicza opóźnienie
     * @param mapOrderAndTimeDeadLine mapa zlecenie / czasy terminów
     * @param mapOrderAndTimeMakeTime mapa zlecenie / czasy obróbek
     * @return
     */
    private long delayCount(Map<String, Long> mapOrderAndTimeDeadLine, Map<String, Long> mapOrderAndTimeMakeTime) {
        final long[] timeLaps = {0, 0};
        IntStream.range(0, helperKeys.size()).forEach(value -> {
            String key = helperKeys.get(value);
            Long deadLineTimeCurrentValue = mapOrderAndTimeDeadLine.get(key);
            Long makeOrderTimeCurrentValue = mapOrderAndTimeMakeTime.get(key);
            timeLaps[0] += makeOrderTimeCurrentValue;
            if (timeLaps[0] > deadLineTimeCurrentValue) {
                long abs = Math.abs(timeLaps[0] - deadLineTimeCurrentValue);
                timeLaps[1] += abs;
            }
            LOGGER.info("Zlecenie " + key + " zostanie wykonane po " + timeLaps[0] + ": opóźnienie " + timeLaps[1]);
        });
        return timeLaps[1];
    }


    /**
     * Przygotowuje układ zleceń jako ciąg znaków
     * @param mappedValues zmapowane wartości
     * @param keys klucze
     * @param supplyLongs zapasy czasu
     * @return np U(z1 z2 z3)
     */
    private String prepareOrderForSupplyIncreaseOrder(List<Long> mappedValues, List<String> keys, List<Long> supplyLongs) {
        final String[] ordr = {""};
        helperKeys.clear();
        supplyLongs.stream().forEach(aLong -> {
            int i = mappedValues.indexOf(aLong);
            String s = keys.get(i);
            helperKeys.add(s);
            ordr[0] = ordr[0] + s + " ";
            keys.remove(i);
            mappedValues.remove(i);
        });
        String data = "U( " + ordr[0] + ")";
        return data;
    }


    // getter setter


    public long getSupplyIncreaseTimeDelay() {
        return supplyIncreaseTimeDelay;
    }

    public String getSupplyIncreaseOrder() {
        return supplyIncreaseOrder;
    }

    public long getIncreateMakeTimeDelay() {
        return increateMakeTimeDelay;
    }

    public String getIncreaseMakeTimeOrder() {
        return increaseMakeTimeOrder;
    }

    public long getIncreaseDeadLineTimeDelay() {
        return increaseDeadLineTimeDelay;
    }

    public String getIncreaseDeadLineTimeOrder() {
        return increaseDeadLineTimeOrder;
    }
}
