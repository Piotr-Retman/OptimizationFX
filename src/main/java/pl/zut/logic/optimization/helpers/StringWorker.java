package pl.zut.logic.optimization.helpers;

import java.util.List;

/**
 * Created by Retman on 2016-03-31.
 */
public class StringWorker {
    public static String generateRetrieveString(String... values) {
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String generateRetrieveStringWithDelimitter(String delimitter, Object... values) {
        StringBuilder sb = new StringBuilder();

        for (Object s : values) {
            sb.append(s).append(delimitter);
        }
        return sb.toString().substring(1,sb.length()-2).replace(" ","");
    }

    public static List<Long> prepareListBasedOnString(String listAsString) {
        return LogicHelper.createListOfValuesBasedOnString(listAsString);
    }
}
