package pl.zut.logic.optimization.helpers;

import java.util.Collections;
import java.util.List;

/**
 * Klasa w, której metody przetwarzają ciągi znaków
 */
public class StringWorker {
    /**
     * Generuje jeden ciąg znaków bazując na podanej liście parametrów
     *
     * @param values wartości parametrów
     * @return ciąg znaków wszystkich parametrów
     */
    public static String generateRetrieveString(String... values) {
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Generuje ciąg znaków w oparciu o znak oddzielający
     *
     * @param delimitter np "," lub "."
     * @param values     wartości parametrów
     * @return ciąg znaków
     */
    public static String generateRetrieveStringWithDelimitter(String delimitter, Object... values) {
        StringBuilder sb = new StringBuilder();

        for (Object s : values) {
            sb.append(s).append(delimitter);
        }
        return sb.toString().substring(1, sb.length() - 2).replace(" ", "");
    }

    /**
     * Tworzy listę w oparciu o ciąg znaków
     *
     * @param listAsString lista jako ciąg znaków
     */
    public static List<Long> prepareListBasedOnString(String listAsString) {
        return LogicHelper.createListOfValuesBasedOnString(listAsString);
    }

    public static String generateResultBasedOnList(List<String> strings) {
        Collections.reverse(strings);
        String result = "U(";
        String[] data = {""};
        strings.stream().forEach(s -> {
            data[0] = data[0] + " " + s;
        });
        return generateRetrieveString(result, data[0], ")");
    }
}
