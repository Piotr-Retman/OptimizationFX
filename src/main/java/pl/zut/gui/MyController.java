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
import pl.zut.logic.optimization.DifferentMethodologies;
import pl.zut.logic.optimization.TypeMap;
import pl.zut.logic.optimization.helpers.LogicHelper;
import pl.zut.logic.optimization.LogicSolution;
import pl.zut.logic.optimization.helpers.StringWorker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class MyController {
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MyController.class.getName());
    List<String> order;

    Map<String, Long> mapMakeTimeOrder;
    Map<String, Long> mapDeadLineTimeOrder;

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
    private TextField mtOrder = new TextField();

    @FXML
    private TextField mtDelay = new TextField();

    @FXML
    private TextField solutionAfterOptimizationDelay = new TextField();

    @FXML
    private TextField solutionAfterOptimization = new TextField();

    @FXML
    private TextField pathToFile = new TextField();

    @FXML
    private Button ganttChart = new Button();

    @FXML
    private TextArea finalDataAfterLoad = new TextArea();

    @FXML
    private TextArea finalDataToSave = new TextArea();

    @FXML
    private TextField mzaOrder = new TextField();

    @FXML
    private TextField mzaDelay = new TextField();

    @FXML
    private TextField moOrder = new TextField();

    @FXML
    private TextField moDelay = new TextField();

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
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Rozpoczynam procedurę obliczeń...");
        Validator validator = new Validator();
        if (singleCountsCheckBox.isSelected()) {
            LogicSolution ls = new LogicSolution();
            List<Long> timeOfOrderArrayAsList = StringWorker.prepareListBasedOnString(timeOfOrderArray.getText());
            List<Long> makeTimeArrayAsList = StringWorker.prepareListBasedOnString(makeTimeArray.getText());

            int sizeTimeOfOrderArrayAsList = timeOfOrderArrayAsList.size();
            int sizeMakeTimeArrayAsList = makeTimeArrayAsList.size();
            boolean isOk = validator.validateSingleCountPossible(makeTimeArray.getText(), timeOfOrderArray.getText(),sizeMakeTimeArrayAsList,sizeTimeOfOrderArrayAsList);

            if(isOk){
                runAlgorithm(ls, makeTimeArrayAsList, timeOfOrderArrayAsList);
                runDifferentSolutions(ls);
            }
        } else if (multipleCountsCheckBox.isSelected()) {
            String lines = validator.validateMultipleCountsAndReturnFileAsString(pathToFile.getText());
            finalDataAfterLoad.setText(lines);
            runMultipleAlgorithmSolutions(validator.getMapCountNumAndPairTimes());
        }
    }


    @FXML
    private void handleGanttBtnAction(ActionEvent event) {
        try {

            Stage stage = new Stage();

            String[] machines = new String[]{"M1","Terminy"};

            final NumberAxis xAxis = new NumberAxis();
            final CategoryAxis yAxis = new CategoryAxis();

            final GanttJavaFX<Number, String> chart = new GanttJavaFX<Number, String>(xAxis, yAxis);
            xAxis.setLabel(solutionAfterOptimization.getText());
            xAxis.setTickLabelFill(Color.CHOCOLATE);
            xAxis.setMinorTickCount(4);


            yAxis.setLabel("");
            yAxis.setTickLabelFill(Color.CHOCOLATE);
            yAxis.setTickLabelGap(10);
            yAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(machines)));

            chart.setTitle("Diagram Gantta");
            chart.setLegendVisible(true);
            chart.setBlockHeight(50);
            String machine;

            //M1
            machine = machines[0];
            XYChart.Series series = new XYChart.Series();
            fillDataSeries(series, machine, TypeMap.WITH_MAKE_ORDER_TIMES);

            machine = machines[1];
            XYChart.Series seriesDeadlines = new XYChart.Series();
            fillDataSeries(seriesDeadlines, machine, TypeMap.WITH_DEADLINE_TIMES);

            chart.getData().addAll(series,seriesDeadlines);

            chart.getStylesheets().add(getClass().getResource("/css/ganttchart.css").toExternalForm());

            Scene scene = new Scene(chart, 620, 350);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDataSeries(XYChart.Series machineOneSeries, String machine, TypeMap typeMap) {
        long startPoint = 0;
        long endPoint = 0;
        LOGGER.setLevel(Level.ALL);
        for (int i = order.size() - 1; i >= 0; i--) {
            String orderName = order.get(i);
            Long aLong = 0L;
            if(typeMap.equals(TypeMap.WITH_MAKE_ORDER_TIMES)) {
               aLong  = mapMakeTimeOrder.get(orderName);
            }else if(typeMap.equals(TypeMap.WITH_DEADLINE_TIMES)){
                aLong  = mapDeadLineTimeOrder.get(orderName);
            }
            endPoint = startPoint + aLong;
            String status = checkStatus(i);
            machineOneSeries.getData().add(new XYChart.Data(startPoint, machine, new GanttJavaFX.ExtraData(aLong, status)));
            LOGGER.info("Dane do diagramu Gantta: " + StringWorker.generateRetrieveString(startPoint + " - " + endPoint + "-" + aLong));
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
        LOGGER.fine(status);
        return status;
    }

    private void runMultipleAlgorithmSolutions(Map<Integer, Pair<String, String>> mapCountNumAndPairTimes) {
        Collection<Pair<String, String>> values = mapCountNumAndPairTimes.values();
        List<SolutionObject> listOfSolutions = new ArrayList<>();
        for (Pair<String, String> value : values) {
            LogicSolution logicSolution = new LogicSolution();
            SolutionObject so = new SolutionObject();
            logicSolution.clearStatics();
            String currentMakeTimes = value.getKey();
            String currentDeadlineTimes = value.getValue();
            List<Long> currentMakeTimesAsList = StringWorker.prepareListBasedOnString(currentMakeTimes);
            List<Long> currentDeadlineTimesAsList = StringWorker.prepareListBasedOnString(currentDeadlineTimes);
            logicSolution.solveThePoblem(currentMakeTimesAsList, currentDeadlineTimesAsList);

            so.setCurrentMakeTimes(currentMakeTimes);
            so.setCurrentDeadlineTimes(currentDeadlineTimes);
            so.setFinalDelay(logicSolution.getFinalDelay());
            so.setFinalOrder(logicSolution.getFinalOrder());

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
            String contentToShowUser = "";
            for (SolutionObject so : listOfSolutions) {
                String content = StringWorker.generateRetrieveString(
                        String.valueOf(i),
                        ".",
                        " \n ",
                        "a:",
                        so.getCurrentMakeTimes(), " \n ",
                        "b:",
                        so.getCurrentDeadlineTimes(), " \n ",
                        "Bazowe: \n ", String.valueOf(so.getStaticBaseDelay()), "[j] \n ",
                        so.getBaseOrder(), " \n ",
                        "Zoptymalizowane: \n ", String.valueOf(so.getFinalDelay()), "[j] \n ",
                        so.getFinalOrder(),
                        " \n ======= \n");
                contentToShowUser = contentToShowUser + content;
                LOGGER.info(content);
                fileWriter.write(content);
                i++;
            }
            finalDataToSave.setText(contentToShowUser);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.logMsg(Logger.ERROR, ex.getMessage());
        }

    }

    private void runDifferentSolutions(LogicSolution ls) {
        DifferentMethodologies differentMethodologies = new DifferentMethodologies();
        differentMethodologies.countSupplyIncreaseOrder(ls);
        mzaOrder.setText(differentMethodologies.getSupplyIncreaseOrder());
        mzaDelay.setText(String.valueOf(differentMethodologies.getSupplyIncreaseTimeDelay()));

        differentMethodologies.countIncreasingMakeTimesOrder(ls);
        moOrder.setText(differentMethodologies.getIncreaseMakeTimeOrder());
        moDelay.setText(String.valueOf(differentMethodologies.getIncreateMakeTimeDelay()));

        differentMethodologies.countIncreasingDeadLineTimesOrder(ls);
        mtOrder.setText(differentMethodologies.getIncreaseDeadLineTimeOrder());
        mtDelay.setText(String.valueOf(differentMethodologies.getIncreaseDeadLineTimeDelay()));
    }


    private void runAlgorithm(LogicSolution ls, List<Long> makeTimeArrayAsList, List<Long> timeOfOrderArrayAsList) {
        mapMakeTimeOrder = (Map<String, Long>) LogicHelper.createMapOrderAndTime(makeTimeArrayAsList, TypeMap.STRING_ON_LONG);
        mapDeadLineTimeOrder = (Map<String, Long>) LogicHelper.createMapOrderAndTime(timeOfOrderArrayAsList, TypeMap.STRING_ON_LONG);
        updateSupplyData(makeTimeArrayAsList, timeOfOrderArrayAsList);
        ls.clearStatics();
        ls.solveThePoblem(makeTimeArrayAsList, timeOfOrderArrayAsList);
        order = ls.getOrder();
        updateVisualData(ls);
        updateTable(ls);
    }

    private void updateSupplyData(List<Long> listMakeTimeOrder, List<Long> listTimeDeadlineOrder) {
        List<Long> supplyLongs = LogicHelper.createSupplyLongs(listMakeTimeOrder, listTimeDeadlineOrder);
        String s = StringWorker.generateRetrieveStringWithDelimitter(",", supplyLongs);
        supply.setText(s);
    }


    private void updateTable(LogicSolution ls) {
        tableData.getItems().clear();
        tableData.getColumns().clear();
        List<String> order = ls.getOrder();
        Map<String, Long> mapDeadlineToOrder = LogicSolution.getStaticMapOrderToDeadline();
        Map<String, Long> mapMakeTimeToOrder = LogicSolution.getStaticMapOrderToMakeTime();
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
        sumOfMakeTimes.setText(String.valueOf(LogicSolution.getFinalSumOfMakeOrderData()));
        solutionAfterOptimizationDelay.setText(String.valueOf(ls.getFinalDelay()));
        solutionAfterOptimization.setText(ls.getFinalOrder());
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
