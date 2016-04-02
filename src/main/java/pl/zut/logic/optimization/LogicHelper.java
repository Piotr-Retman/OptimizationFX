package pl.zut.logic.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Long> createMapOrderAndOrderTime(List<Long> makeOrderTimes) {
        Map<String, Long> mapOrderTime = new HashMap<>();
        for (int i = 0; i < makeOrderTimes.size(); i++) {
            int keyPart = i +1;
            mapOrderTime.put("z" + keyPart,makeOrderTimes.get(i));
        }
        return mapOrderTime;
    }
}
