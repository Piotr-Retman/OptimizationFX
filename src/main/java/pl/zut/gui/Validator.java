package pl.zut.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;
import pl.zut.logic.optimization.helpers.StringWorker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Klasa Validator służy walidacji danych w trybie pojedynczym i multi
 */
public class Validator {

    private Map<Integer, Pair<String, String>> mapCountNumAndPairTimes = new HashMap<>();

    /**
     * Walidacja ścieżki do pliku
     * @param textOfPath ścieżka
     * @return dane
     * @throws IOException
     */
    public String validateMultipleCountsAndReturnFileAsString(String textOfPath) throws IOException {
        boolean hasData = true;

        if (textOfPath.isEmpty()) {
            generateAlert("Załaduj plik!", Alert.AlertType.ERROR);
            hasData = false;
        }
        String lines = "";
        if (hasData) {
            lines = validateFileDataAndRunMultipleCounts(textOfPath);
        }
        return lines;
    }

    /**
     * Walidacja pojedynczego obliczenia
     * @param makeTimeText lista czasów obróbek jako ciag znaków
     * @param timeOfOrderArrayText lista terminów jako ciag znaków
     * @param sizeMakeTimeArrayAsList rozmiar listy czasów obróbek
     * @param sizeTimeOfOrderArrayAsList rozmiar listy terminów
     * @return wartość logiczną okreslającą pozytywnie lub negatywnie zwalidowane dane
     */
    public boolean validateSingleCountPossible(String makeTimeText, String timeOfOrderArrayText, int sizeMakeTimeArrayAsList, int sizeTimeOfOrderArrayAsList) {
        boolean hasData = true;

        if (makeTimeText.isEmpty()) {
            hasData = false;
            generateAlert("Proszę uzupełnić czasy obróbek!", Alert.AlertType.ERROR);
        } else if (timeOfOrderArrayText.isEmpty()) {
            hasData = false;
            generateAlert("Proszę uzupełnić terminy zleceń", Alert.AlertType.ERROR);
        }
        boolean isOk = false;
        if (hasData) {
          isOk = validateInsideDataForSingleCounts(sizeMakeTimeArrayAsList,sizeTimeOfOrderArrayAsList);
        }
        return isOk;
    }

    /**
     * Walidacja danych z pojedynczego obliczenia
     * @param sizeMakeTimeArrayAsList rozmiar listy czasów obróbek
     * @param sizeTimeOfOrderArrayAsList rozmiar listy terminów
     * @return
     */
    private boolean validateInsideDataForSingleCounts(int sizeMakeTimeArrayAsList, int sizeTimeOfOrderArrayAsList) {

        boolean equal = (sizeTimeOfOrderArrayAsList == sizeMakeTimeArrayAsList && sizeMakeTimeArrayAsList != 0);
        if (equal) {
            return equal;
        } else {
            generateAlert(StringWorker.generateRetrieveString("Nieodpowiednia ilość danych w listach. Lista czasów obróbek: ",
                    String.valueOf(sizeMakeTimeArrayAsList),
                    " Lista terminów: ", String.valueOf(sizeTimeOfOrderArrayAsList)), Alert.AlertType.ERROR);
        }
        return false;
    }

    /**
     * Metoda generuje okno z informacją o błędzie
     * @param msg informacja
     * @param alertType typ błędu np ERROR, INFO itp
     */
    private void generateAlert(String msg, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Walidacja danych z pliku i uruchomienie metody multi obliczeń
     * @param path ścieżka do pliku
     * @return dane jako ciąg znaków
     * @throws IOException
     */
    private String validateFileDataAndRunMultipleCounts(String path) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(path));
        final String[] makeOrderTime = {null};
        final String[] deadlineTimes = {null};
        final int[] id = {-1};
        final int[] lineNum = {1};
        final String[] linesAsString = {""};

        lines.forEach(s -> {
            try {
                linesAsString[0] = linesAsString[0] + s + "\n";
                validateCurrentStringFromFile(s, makeOrderTime, deadlineTimes, id, lineNum[0]);
                updateMap(makeOrderTime, deadlineTimes, id, mapCountNumAndPairTimes);
                lineNum[0] = lineNum[0] + 1;
            } catch (Exception ex) {
                //no need to do
                mapCountNumAndPairTimes.clear();
            }
        });
        return linesAsString[0];
    }

    /**
     * Walidacja aktualnej linii z pliku
     *
     * @param s linia z pliku
     * @param makeOrderTime czasy obróbek w tablicy asocjacyjnej
     * @param deadlineTimes terminy w tablicy asocjacyjnej
     * @param id tablica identyfikatorów
     * @param lineNum numer linii z pliku
     * @throws Exception wyjątek generuje informację i błąd
     */
    private void validateCurrentStringFromFile(String s, String[] makeOrderTime, String[] deadlineTimes, int[] id, int lineNum) throws Exception {
        if (s.startsWith("a") && Objects.equals(validateData(s), Boolean.TRUE)) {
            makeOrderTime[0] = s.replace("a:", "");
        } else if (s.startsWith("b") && Objects.equals(validateData(s), Boolean.TRUE)) {
            deadlineTimes[0] = s.replace("b:", "");
        } else {
            try {
                id[0] = Integer.valueOf(s);
            } catch (Exception ex) {
                generateAlert("Numer zadania w jednej z linii jest zły! Sprawdź to! Linia:" + lineNum, Alert.AlertType.ERROR);
                throw new Exception("Błąd zabezpieczony!");
            }
        }
    }

    /**
     * Aktualizacja mapy numer na parę czasów
     * @param makeOrderTime tablica czasów obróbki
     * @param deadlineTimes tablica terminów
     * @param id tablica identyfikatorów indeksu
     * @param mapCountNumAndPairTimes mapa do aktualizacji
     */
    private void updateMap(String[] makeOrderTime, String[] deadlineTimes, int[] id, Map<Integer, Pair<String, String>> mapCountNumAndPairTimes) {
        if (makeOrderTime[0] != null && deadlineTimes[0] != null && id[0] != -1) {
            mapCountNumAndPairTimes.put(id[0], new Pair<>(makeOrderTime[0], deadlineTimes[0]));
            makeOrderTime[0] = null;
            deadlineTimes[0] = null;
            id[0] = -1;
        }
    }

    /**
     * Zwraca mapę numer na parę
     */
    public Map<Integer, Pair<String, String>> getMapCountNumAndPairTimes() {
        return mapCountNumAndPairTimes;
    }

    /**
     * Metoda waliduje dane na podstawie typu generycznego
     * @param s typ generyczny
     * @return wartość logiczną czy dane są wpisane poprawnie
     * @throws Exception wyjątek generuje okno błędu
     */
    private <T> T validateData(T s) throws Exception {
        T ok = (T) Boolean.FALSE;
        if (s instanceof Integer) {
            ok = (T) Boolean.TRUE;
        } else if (s instanceof String) {
            try {
                ((String) s).split(",");
                String regex = ".+\\d*[,]\\d*";
                if (!((String) s).matches(regex)) {
                    throw new Exception();
                } else {
                    ok = (T) Boolean.TRUE;
                }
            } catch (Exception ex) {
                generateAlert("Można używać tylko znaku ',' między wartościami poszczególnych czasów!", Alert.AlertType.ERROR);
                throw new Exception("Błąd zabezpieczony!");
            }
        }
        return ok;
    }
}
