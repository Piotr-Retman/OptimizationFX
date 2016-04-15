package pl.zut.logic.optimization.helpers;

import pl.zut.logic.optimization.TypeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by Retman on 2016-01-14.
 */
public class LogicHelper {


    public static List<Long> createListOfValuesBasedOnString(String arrayAsString) {
        List<Long> arrayOfValues = new ArrayList<>();
        String[] split = arrayAsString.split(",");
        for (String splittedArrayValue : split) {
            arrayOfValues.add(Long.valueOf(splittedArrayValue));
        }
        return arrayOfValues;
    }

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
