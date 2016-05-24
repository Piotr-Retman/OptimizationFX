package pl.zut.logic.optimization;

import javafx.scene.control.Alert;
import pl.zut.gui.Validator;
import pl.zut.logic.optimization.helpers.LogicHelper;
import pl.zut.logic.optimization.helpers.Permutation;
import pl.zut.logic.optimization.helpers.StringWorker;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Klasa rozwiązująca problem Mopt
 */
public class LogicSolution {

    private final static Logger LOGGER = Logger.getLogger(LogicSolution.class.getName());
    private static List<Long> finalMakeTimeOrderData;
    private long finalDelay;
    private List<String> order;
    private List<String> orderNames;
    private long sumOfMakeTimes;
    private static long staticSumOfMakeTimes;
    private String finalOrder;
    private static Map<String, Long> mapOrderToDeadline;
    private static Map<String, Long> mapOrderToMakeTime;
    private static List<Long> finalDeadlineOrderTimesData;
    String helperString = "abcdefgh";
    Map<String, String> mapLetterOnShortName;

    public static Map<String, Long> getMapOrderToDeadline() {
        return mapOrderToDeadline;
    }

    public static Map<String, Long> getMapOrderToMakeTime() {
        return mapOrderToMakeTime;
    }

    public void solveThePoblem(List<Long> makeTimeArrayAsList, List<Long> deadlineOrders) {
        try {
            mapLetterOnShortName = generateMapLetterOnOrderShortName();
            SimpleProcedure sp = new SimpleProcedure();
            List<String> orderNamesGenerated = sp.generateListOfShortNames(makeTimeArrayAsList.size());
            orderNames = new ArrayList<>(orderNamesGenerated);
            sumOfMakeTimes = sp.countSumOfMakeOrderTimes(makeTimeArrayAsList);
            staticSumOfMakeTimes = sumOfMakeTimes;
            int numberOfOrders = makeTimeArrayAsList.size();
            finalDeadlineOrderTimesData = new ArrayList<>(deadlineOrders);
            finalMakeTimeOrderData = new ArrayList<>(makeTimeArrayAsList);
            mapOrderToMakeTime = (Map<String, Long>) LogicHelper.createMapOrderAndTime(new ArrayList<>(finalMakeTimeOrderData), TypeMap.STRING_ON_LONG);
            mapOrderToDeadline = (Map<String, Long>) LogicHelper.createMapOrderAndTime(new ArrayList<>(finalDeadlineOrderTimesData), TypeMap.STRING_ON_LONG);

            Set<String> permutations = Permutation.generatePerm(helperString.substring(0, numberOfOrders));
            List<List<String>> updatedPermutationsToCorrectDataAsList = createCorrectPermutations(permutations);
            Map<Long, List<String>> mapDelayOnSolution = new HashMap<>();
            updatedPermutationsToCorrectDataAsList.stream().forEach(order -> {
                sumOfMakeTimes = staticSumOfMakeTimes;
                long delay = finalizeAlgorithm(order, new ArrayList<>(makeTimeArrayAsList), new ArrayList<>(deadlineOrders), sumOfMakeTimes, new ArrayList<>(orderNames));
                mapDelayOnSolution.put(delay, order);
                System.out.println(Arrays.toString(order.toArray()) + " " + delay);
            });

            findSmallestAndSetData(mapDelayOnSolution);
        }catch(StringIndexOutOfBoundsException ex){
            Validator validator = new Validator();
            validator.generateAlert("Zbyt dużo zleceń. Jeżeli posiadasz dobry komputer. Spróbuj w trybie multi.", Alert.AlertType.ERROR);
            return;
        }
    }

    private void findSmallestAndSetData(Map<Long,List<String>> mapDelayOnSolution) {
        Set<Long> longs = mapDelayOnSolution.keySet();
        Long min = Collections.min(longs);
        finalDelay = min;
        List<String> strings = mapDelayOnSolution.get(min);
        Collections.reverse(strings);
        order = new ArrayList<>(strings);
        finalOrder = StringWorker.generateResultBasedOnList(strings);
    }

    private Map<String, String> generateMapLetterOnOrderShortName() {
        List<String> alphabet = Arrays.asList(helperString.split(""));
        Map<String, String> letterOnOrderNumber = new HashMap<>();
        int[] value = {1};
        alphabet.stream().forEach(letter -> {
            letterOnOrderNumber.put(letter, StringWorker.generateRetrieveString("z", String.valueOf(value[0])));
            value[0]++;
        });
        return letterOnOrderNumber;
    }

    private List<List<String>> createCorrectPermutations(Set<String> permutations) {
        List<List<String>> orders = new ArrayList<>();
        while (permutations.iterator().hasNext()) {
            String next = permutations.iterator().next();
            List<String> ordersAsAlphabet = Arrays.asList(next.split(""));
            List<String> permutationOrder = new ArrayList<>();
            ordersAsAlphabet.stream().forEach(letter -> {
                permutationOrder.add(mapLetterOnShortName.get(letter));
            });
            orders.add(permutationOrder);
            permutations.remove(next);
        }
        return orders;
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
    public long finalizeAlgorithm(List<String> order, List<Long> makeOrderData, List<Long> deadLineData, Long sumOfMakeOrderTimes, List<String> orderNames) {
        SimpleProcedure sp = new SimpleProcedure();

        long delay = 0;
        for (int i = order.size()-1; i >= 0; i--) {
            List<Long> countedPi = sp.countPi(sumOfMakeOrderTimes, deadLineData);
            int index = orderNames.indexOf(order.get(i));
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
        return delay;
    }

    public void clearStatics() {
        try {
            finalMakeTimeOrderData.clear();
            finalDelay = 0;
            order.clear();
            orderNames.clear();
            sumOfMakeTimes = 0;
            finalOrder = "";
            finalDeadlineOrderTimesData.clear();
        } catch (NullPointerException ex) {
            //no need to handle this
        }
    }

    /*Gettery i Settery*/

    public List<String> getOrder() {
        return order;
    }

    public static List<Long> getFinalMakeTimeOrderData() {
        return finalMakeTimeOrderData;
    }

    public long getSumOfMakeTimes() {
        return sumOfMakeTimes;
    }

    public long getFinalDelay() {
        return finalDelay;
    }

    public String getFinalOrder() {
        return finalOrder;
    }

    public static List<Long> getFinalDeadlineOrderTimesData() {
        return finalDeadlineOrderTimesData;
    }
}
