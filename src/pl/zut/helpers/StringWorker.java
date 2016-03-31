package pl.zut.helpers;

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
}
