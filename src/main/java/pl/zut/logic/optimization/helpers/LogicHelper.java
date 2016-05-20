package pl.zut.logic.optimization.helpers;

import javafx.scene.control.Alert;
import pl.zut.gui.Validator;
import pl.zut.logic.optimization.TypeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Klasa wspierająca logikę algorytmów
 */
public class LogicHelper {


    /**
     * Metoda tworzy listę w oparciu o ciąg znaków
     *
     * @param arrayAsString ciąg znaków do generowania listy
     * @return lista wartości
     */
    public static List<Long> createListOfValuesBasedOnString(String arrayAsString) {
        List<Long> arrayOfValues = new ArrayList<>();
        String[] split = arrayAsString.split(",");
        int lineNum = 1;
        String task = "";
        try {
            for (String splittedArrayValue : split) {
                task = arrayAsString;
                arrayOfValues.add(Long.valueOf(splittedArrayValue));
                lineNum++;
            }
            return arrayOfValues;
        } catch (NumberFormatException ex) {
            Validator validator = new Validator();
            validator.generateAlert("Sprawdź liczbę: " + lineNum + "w zbiorze: " + task, Alert.AlertType.ERROR);
        }
        return null;
    }

    /**
     * Tworzy mapę w oparciu o wybierany typ oraz poukładaną listę czasów
     *
     * @param orderTimesToCreateMap poukładana lista czasów
     * @param typeMap               typ mapy
     * @return mapę określoną przez podany typ jeśli typ nie będzie prawidłowy TypeMap.LONG_ON_STRING lub TypeMap.STRING_ON_LONG zwróci null
     */
    public static Map<?, ?> createMapOrderAndTime(List<Long> orderTimesToCreateMap, TypeMap typeMap) {
        Map<String, Long> mapOrderTime = new HashMap<>();
        Map<Long, String> mapTimeOrder = new HashMap<>();
        for (int i = 0; i < orderTimesToCreateMap.size(); i++) {
            int keyPart = i + 1;
            mapOrderTime.put("z" + keyPart, orderTimesToCreateMap.get(i));
            mapTimeOrder.put(orderTimesToCreateMap.get(i), "z" + keyPart);
        }
        if (TypeMap.LONG_ON_STRING.equals(typeMap)) {
            return mapTimeOrder;
        } else if (TypeMap.STRING_ON_LONG.equals(typeMap)) {
            return mapOrderTime;
        }
        return null;
    }


    public static List<Long> createSupplyLongs(List<Long> listMakeTimeOrder, List<Long> listTimeDeadlineOrder) {
        List<Long> supplyLongs = new ArrayList<>(listMakeTimeOrder.size());
        IntStream.range(0, listMakeTimeOrder.size()).forEach(value -> {
            long makeTimeOrdrValue = listMakeTimeOrder.get(value);
            long timeDeadlineOrdrValue = listTimeDeadlineOrder.get(value);
            long supplyData = makeTimeOrdrValue - timeDeadlineOrdrValue;
            long abs = Math.abs(supplyData);
            supplyLongs.add(abs);
        });
        return supplyLongs;
    }
}
