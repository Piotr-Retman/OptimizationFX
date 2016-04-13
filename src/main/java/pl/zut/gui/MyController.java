package pl.zut.gui;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import pl.zut.chart.GanttJavaFX;
import pl.zut.helpers.StringWorker;
import pl.zut.logic.optimization.LogicHelper;
import pl.zut.logic.optimization.LogicSolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static pl.zut.logic.optimization.Logic.*;

public class MyController {

    List<String> order;

    Map<String, Long> mapMakeTimeOrder;

    Stage primaryStage;

    @FXML
    private TableView<TableObject> tableData = new TableView<TableObject>();

    @FXML
    private TableColumn<TableObject, String> orderColumn = new TableColumn<TableObject, String>("Zlecenie");

    @FXML
    private TableColumn<TableObject, String> makeOrderTimeColumn = new TableColumn<TableObject, String>("Czas obróbki T0[j]");

    @FXML
    private TableColumn<TableObject, String> deadlineTimeColumn = new TableColumn<TableObject, String>("Termin Tt[j]");


    @FXML
    private Button runAlgorithmButton = new Button();

    @FXML
    private Button loadData = new Button();

    @FXML
    private Button help = new Button();

    @FXML
    private Button testButtonSetData = new Button();

    @FXML
    private CheckBox singleCountsCheckBox = new CheckBox();

    @FXML
    private CheckBox multipleCountsCheckBox = new CheckBox();

    @FXML
    private TextField makeTimeArray = new TextField();

    @FXML
    private TextField timeOfOrderArray = new TextField();

    @FXML
    private TextField supply = new TextField();

    @FXML
    private TextField sumOfMakeTimes = new TextField();

    @FXML
    private TextField solutionFirst = new TextField();

    @FXML
    private TextField solutionFirstDelay = new TextField();

    @FXML
    private TextField solutionAfterOptimizationDelay = new TextField();

    @FXML
    private TextField solutionAfterOptimization = new TextField();

    @FXML
    private TextField pathToFile = new TextField();


    @FXML
    private Button ganttChart = new Button();

    // TODO: 2016-04-09 Implementacja algorytmów EDD,FIFO,LIFO

    @FXML
    private void handleLoadData() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        pathToFile.setText(file.getAbsolutePath());
    }


    @FXML
    private void handleSubmitButtonAction(ActionEvent event) throws IOException {

        if (singleCountsCheckBox.isSelected()) {
            validateSingleCountPossible();
        } else if (multipleCountsCheckBox.isSelected()) {
            validateMultipleCountPossible();
        }
    }


    @FXML
    private void handleGanttBtnAction(ActionEvent event) {
        try {

            Stage stage = new Stage();

            String[] machines = new String[]{"M1"};

            final NumberAxis xAxis = new NumberAxis();
            final CategoryAxis yAxis = new CategoryAxis();

            final GanttJavaFX<Number, String> chart = new GanttJavaFX<Number, String>(xAxis, yAxis);
            xAxis.setLabel(solutionAfterOptimization.getText());
            xAxis.setTickLabelFill(Color.CHOCOLATE);
            xAxis.setMinorTickCount(4);


            yAxis.setLabel("");
            yAxis.setTickLabelFill(Color.CHOCOLATE);
            yAxis.setTickLabelGap(10);
            yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

            chart.setTitle("Machine Monitoring");
            chart.setLegendVisible(true);
            chart.setBlockHeight(50);
            String machine;

            machine = machines[0];
            XYChart.Series series = new XYChart.Series();

            fillDataSeries(series, machine);

            chart.getData().addAll(series);

            chart.getStylesheets().add(getClass().getResource("/css/ganttchart.css").toExternalForm());

            Scene scene = new Scene(chart, 620, 350);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDataSeries(XYChart.Series series, String machine) {
        long startPoint = 0;
        long endPoint = 0;
        for (int i = order.size() - 1; i >= 0; i--) {
            String orderName = order.get(i);
            Long aLong = mapMakeTimeOrder.get(orderName);
            endPoint = startPoint + aLong;
            String status = checkStatus(i);
            series.getData().add(new XYChart.Data(startPoint, machine, new GanttJavaFX.ExtraData(aLong, status)));
            System.out.println(startPoint + " - " + endPoint + "-" + aLong);
            startPoint = endPoint;
        }
    }

    private String checkStatus(int index) {
        String status = "";
        if ((index % 2) == 0) {
            status = "status-red";
        } else {
            status = "status-green";
        }
        System.out.println(status);
        return status;
    }


    private void validateMultipleCountPossible() throws IOException {
        boolean hasData = true;

        if (pathToFile.getText().isEmpty()) {
            generateAlert("Załaduj plik!", Alert.AlertType.ERROR);
            hasData = false;
        }

        if (hasData) {
            validateFileDataAndRunMultipleCounts();
        }
    }

    private void validateFileDataAndRunMultipleCounts() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(pathToFile.getText()));
        Map<Integer, Pair<String, String>> mapCountNumAndPairTimes = new HashMap<>();
        final String[] makeOrderTime = {null};
        final String[] deadlineTimes = {null};
        final int[] id = {-1};
        final int[] lineNum = {1};
        lines.forEach(s -> {
            try {
                validateCurrentStringFromFile(s, makeOrderTime, deadlineTimes, id, lineNum[0]);
                updateMap(makeOrderTime, deadlineTimes, id, mapCountNumAndPairTimes);
                lineNum[0] = lineNum[0] + 1;
            } catch (Exception ex) {
                //no need to do
                mapCountNumAndPairTimes.clear();
            }
        });

        runMultipleAlgorithmSolutions(mapCountNumAndPairTimes);
    }

    private void runMultipleAlgorithmSolutions(Map<Integer, Pair<String, String>> mapCountNumAndPairTimes) {
        // TODO: 2016-04-02 Add 1) make solutions 2) save solutions to new file
        Collection<Pair<String, String>> values = mapCountNumAndPairTimes.values();
        List<SolutionObject> listOfSolutions = new ArrayList<>();
        for (Pair<String, String> value : values) {
            LogicSolution logicSolution = new LogicSolution();
            SolutionObject so = new SolutionObject();
            logicSolution.clearStatics();
            String currentMakeTimes = value.getKey();
            String currentDeadlineTimes = value.getValue();
            List<Long> currentMakeTimesAsList = logicSolution.prepareListBasedOnString(currentMakeTimes);
            List<Long> currentDeadlineTimesAsList = logicSolution.prepareListBasedOnString(currentDeadlineTimes);
            logicSolution.setListMakeOrderTimes(currentMakeTimesAsList);
            logicSolution.setListDeadLineTimes(currentDeadlineTimesAsList);
            logicSolution.solveThePoblem(currentMakeTimesAsList, currentDeadlineTimesAsList, logicSolution.countSumOfMakeOrderTimes(currentMakeTimesAsList));

            so.setCurrentMakeTimes(currentMakeTimes);
            so.setCurrentDeadlineTimes(currentDeadlineTimes);
            so.setFinalDelay(logicSolution.getFinalDelay());
            so.setFinalOrder(logicSolution.getFinalOrder());
            so.setBaseOrder(logicSolution.getBaseOrder());
            so.setStaticBaseDelay(LogicSolution.getStaticBaseDelay());

            listOfSolutions.add(so);
        }
        saveToFile(listOfSolutions);
    }

    private void saveToFile(List<SolutionObject> listOfSolutions) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.setTitle("Save Resource File");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            createFileAndSave(listOfSolutions, file);
        }
    }

    private void createFileAndSave(List<SolutionObject> listOfSolutions, File file) {
        try {
            FileWriter fileWriter = null;
            int i = 1;
            fileWriter = new FileWriter(file);
            for (SolutionObject so : listOfSolutions) {
                String content = StringWorker.generateRetrieveString(
                        String.valueOf(i),
                        ".",
                        " \n ",
                        "a:",
                        so.getCurrentMakeTimes(), " \n ",
                        "b:",
                        so.getCurrentDeadlineTimes(), " \n ",
                        "Bazowe: \n ",String.valueOf(so.getStaticBaseDelay()),"[j] \n ",
                        so.getBaseOrder(), " \n ",
                        "Zoptymalizowane: \n ",String.valueOf(so.getFinalDelay()), "[j] \n ",
                        so.getFinalOrder(),
                        " \n ======= \n");
                System.out.println(content);
                fileWriter.write(content);
                i++;
            }
            fileWriter.close();
        } catch (IOException ex) {
            Logger.logMsg(Logger.ERROR, ex.getMessage());
        }

    }

    private void updateMap(String[] makeOrderTime, String[] deadlineTimes, int[] id, Map<Integer, Pair<String, String>> mapCountNumAndPairTimes) {
        if (makeOrderTime[0] != null && deadlineTimes[0] != null && id[0] != -1) {
            mapCountNumAndPairTimes.put(id[0], new Pair<>(makeOrderTime[0], deadlineTimes[0]));
            makeOrderTime[0] = null;
            deadlineTimes[0] = null;
            id[0] = -1;
        }
    }

    private void validateCurrentStringFromFile(String s, String[] makeOrderTime, String[] deadlineTimes, int[] id, int lineNum) throws Exception {
        // TODO: 2016-04-09 Obsługa błędów jest niepoprawna!
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

    private void validateSingleCountPossible() {
        boolean hasData = true;

        if (makeTimeArray.getText().isEmpty()) {
            hasData = false;
            generateAlert("Proszę uzupełnić czasy obróbek!", Alert.AlertType.ERROR);
        } else if (timeOfOrderArray.getText().isEmpty()) {
            hasData = false;
            generateAlert("Proszę uzupełnić terminy zleceń", Alert.AlertType.ERROR);
        }

        if (hasData) {
            validateInsideDataForSingleCounts();
        }
    }

    private void validateInsideDataForSingleCounts() {
        LogicSolution ls = new LogicSolution();
        List<Long> timeOfOrderArrayAsList = ls.prepareListBasedOnString(timeOfOrderArray.getText());
        List<Long> makeTimeArrayAsList = ls.prepareListBasedOnString(makeTimeArray.getText());

        int sizeTimeOfOrderArrayAsList = timeOfOrderArrayAsList.size();
        int sizeMakeTimeArrayAsList = makeTimeArrayAsList.size();

        boolean equal = sizeTimeOfOrderArrayAsList == sizeMakeTimeArrayAsList && sizeMakeTimeArrayAsList != 0;
        if (equal) {
            runAlgorithm(ls, makeTimeArrayAsList, timeOfOrderArrayAsList);
        } else {
            generateAlert(StringWorker.generateRetrieveString("Nieodpowiednia ilość danych w listach. Lista czasów obróbek: ",
                    String.valueOf(sizeMakeTimeArrayAsList),
                    " Lista terminów: ", String.valueOf(sizeTimeOfOrderArrayAsList)), Alert.AlertType.ERROR);
        }
    }


    private void runAlgorithm(LogicSolution ls, List<Long> makeTimeArrayAsList, List<Long> timeOfOrderArrayAsList) {

        mapMakeTimeOrder = LogicHelper.createMapOrderAndOrderTime(makeTimeArrayAsList);
        Map<String, Long> mapTimeDeadlineOrder = LogicHelper.createMapOrderAndOrderTime(timeOfOrderArrayAsList);

        setMapDeadlineToOrder(mapTimeDeadlineOrder);
        setMapMakeTimeToOrder(mapMakeTimeOrder);
        updateSupplyData(makeTimeArrayAsList, timeOfOrderArrayAsList);

        ls.clearStatics();
        ls.setListMakeOrderTimes(makeTimeArrayAsList);
        ls.setListDeadLineTimes(timeOfOrderArrayAsList);
        ls.solveThePoblem(makeTimeArrayAsList, timeOfOrderArrayAsList, 0);
        order = ls.getOrder();
        updateVisualData(ls);
        updateTable(ls);
    }

    private void updateSupplyData(List<Long> listMakeTimeOrder, List<Long> listTimeDeadlineOrder) {
        List<Long> supplyLongs = new ArrayList<>(listMakeTimeOrder.size());
        IntStream.range(0, listMakeTimeOrder.size()).forEach(value -> {
            long makeTimeOrdrValue = listMakeTimeOrder.get(value);
            long timeDeadlineOrdrValue = listTimeDeadlineOrder.get(value);
            long supplyData = makeTimeOrdrValue - timeDeadlineOrdrValue;
            long abs = Math.abs(supplyData);
            supplyLongs.add(abs);
        });

        String s = StringWorker.generateRetrieveStringWithDelimitter(",", supplyLongs);
        supply.setText(s);
    }


    private void updateTable(LogicSolution ls) {
        tableData.getItems().clear();
        tableData.getColumns().clear();
        List<String> order = ls.getOrder();
        Map<String, Long> mapDeadlineToOrder = getMapDeadlineToOrder();
        Map<String, Long> mapMakeTimeToOrder = getMapMakeTimeToOrder();
        List<TableObject> tableObjects = generateTableObjectsList(order, mapMakeTimeToOrder, mapDeadlineToOrder);

        ObservableList<TableObject> data = FXCollections.observableArrayList(tableObjects);


        tableData.getColumns().addAll(orderColumn, makeOrderTimeColumn, deadlineTimeColumn);
        orderColumn.setCellValueFactory(
                new PropertyValueFactory<TableObject, String>("order")
        );

        makeOrderTimeColumn.setCellValueFactory(
                new PropertyValueFactory<TableObject, String>("makeOrderTime")
        );

        deadlineTimeColumn.setCellValueFactory(
                new PropertyValueFactory<TableObject, String>("deadlineOrderTime")
        );
        tableData.setItems(data);

    }

    private List<TableObject> generateTableObjectsList(List<String> order, Map<String, Long> mapOrderMakeOrderTime, Map<String, Long> mapOrderDeadlineTime) {
        List<TableObject> tableObjects = new ArrayList<>();
        order.stream().forEach(orderData ->
                tableObjects.add(new TableObject(orderData, mapOrderMakeOrderTime.get(orderData).toString(), mapOrderDeadlineTime.get(orderData).toString())));
        Collections.reverse(tableObjects);
        return tableObjects;
    }

    private void updateVisualData(LogicSolution ls) {
        sumOfMakeTimes.setText(String.valueOf(LogicSolution.getStaticSumOfMakeOrderTimes()));
        solutionFirst.setText(ls.getBaseOrder());
        solutionFirstDelay.setText(String.valueOf(LogicSolution.getStaticBaseDelay()));
        solutionAfterOptimizationDelay.setText(String.valueOf(ls.getFinalDelay()));
        solutionAfterOptimization.setText(ls.getFinalOrder());
    }

    private void generateAlert(String msg, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, msg, ButtonType.OK);
        alert.showAndWait();
    }


    @FXML
    private void handleTestButtonSetTestData(ActionEvent event) {
        makeTimeArray.setText("10,20,100,50,100");
        timeOfOrderArray.setText("150,30,110,60,10");

    }

    @FXML
    private void handleSingleCountsCheckBox(ActionEvent event) {
        if (singleCountsCheckBox.isSelected()) {
            multipleCountsCheckBox.setSelected(false);
            singleCountsCheckBox.setSelected(true);
            makeTimeArray.setDisable(false);
            timeOfOrderArray.setDisable(false);
            testButtonSetData.setDisable(false);
            loadData.setDisable(true);
        }
    }

    @FXML
    private void handleMultipleCountsCheckBox(ActionEvent event) {
        if (multipleCountsCheckBox.isSelected()) {
            singleCountsCheckBox.setSelected(false);
            multipleCountsCheckBox.setSelected(true);
            makeTimeArray.setDisable(true);
            timeOfOrderArray.setDisable(true);
            testButtonSetData.setDisable(true);
            loadData.setDisable(false);
        }
    }
}
