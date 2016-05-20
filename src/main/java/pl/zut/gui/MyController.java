package pl.zut.gui;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import pl.zut.logic.optimization.LogicSolution;
import pl.zut.logic.optimization.TypeMap;
import pl.zut.logic.optimization.helpers.LogicHelper;
import pl.zut.logic.optimization.helpers.StringWorker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class MyController {
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MyController.class.getName());
    List<String> order;

    Map<String, Long> mapMakeTimeOrder;
    Map<String, Long> mapDeadLineTimeOrder;

    Stage primaryStage;

    @FXML
    private TextField pathToFileToCreateCollectionOfExamples = new TextField();

    @FXML
    private TextField pathToSavedFile = new TextField();

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
    Validator validator = new Validator();

    @FXML
    private void handleAddToSolutionPackageBtn(ActionEvent event) {
        String makeTimeArrayText = makeTimeArray.getText();
        String deadLineTimes = timeOfOrderArray.getText();
        String path = pathToFileToCreateCollectionOfExamples.getText();
        if (path.isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(primaryStage);
            String absolutePath = "";
            try {
                absolutePath = file.getAbsolutePath();
                path = absolutePath;
                loadFileAndAddNewExample(path, makeTimeArrayText, deadLineTimes);
            } catch (NullPointerException ex) {
                absolutePath = "Nie została wybrana ścieżka";
            }
            pathToFileToCreateCollectionOfExamples.setText(absolutePath);
        } else {
            loadFileAndAddNewExample(path, makeTimeArrayText, deadLineTimes);
        }
    }

    private void loadFileAndAddNewExample(String path, String makeTimeArrayText, String deadLineTimes) {
        File file = new File(path);
        try {
            Stream<String> lines = Files.lines(Paths.get(path));
            String[] fullDataFromFile = {""};
            int num[] = {1};
                lines.forEach(s -> {
                    if (!(s.startsWith("b") || s.startsWith("a"))) {
                        System.out.println(s);
                        num[0]++;
                    }
                    fullDataFromFile[0] = StringWorker.generateRetrieveString(fullDataFromFile[0], s, "\n");
                });

            FileWriter fw = new FileWriter(file);
            String dataToSave = StringWorker.generateRetrieveString(fullDataFromFile[0], String.valueOf(num[0]), "\n", "a:", makeTimeArrayText, "\n", "b:", deadLineTimes, "\n");

            fw.append(dataToSave);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Obsługa przycisku ładowania danych
     *
     * @throws IOException
     */
    @FXML
    private void handleLoadData() throws IOException {
        resetWhenMultiple();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        String absolutePath = "";
        try {
            absolutePath = file.getAbsolutePath();
        } catch (NullPointerException ex) {
            absolutePath = "Nie została wybrana ścieżka";
        }
        pathToFile.setText(absolutePath);
        String lines = validator.validateMultipleCountsAndReturnFileAsString(pathToFile.getText());
        finalDataAfterLoad.setText(lines);
    }


    /**
     * Obsługa przycisku START
     *
     * @param event parametr określający działanie użytkownika (w tym przypadku obsługiwany jest onSubmit)
     * @throws IOException
     */
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) throws IOException {
        LOGGER.setLevel(Level.ALL);
//        LOGGER.info("Rozpoczynam procedurę obliczeń...");
        if (!makeTimeArray.getText().isEmpty() && !timeOfOrderArray.getText().isEmpty()) {
            resetWhenSingle();
            LogicSolution ls = new LogicSolution();
            List<Long> timeOfOrderArrayAsList = StringWorker.prepareListBasedOnString(timeOfOrderArray.getText());
            List<Long> makeTimeArrayAsList = StringWorker.prepareListBasedOnString(makeTimeArray.getText());

            int sizeTimeOfOrderArrayAsList = timeOfOrderArrayAsList.size();
            int sizeMakeTimeArrayAsList = makeTimeArrayAsList.size();
            boolean isOk = validator.validateSingleCountPossible(makeTimeArray.getText(), timeOfOrderArray.getText(), sizeMakeTimeArrayAsList, sizeTimeOfOrderArrayAsList);

            if (isOk) {
                runAlgorithm(ls, makeTimeArrayAsList, timeOfOrderArrayAsList);
                runDifferentSolutions(ls);
            }
        } else if (makeTimeArray.getText().isEmpty() && timeOfOrderArray.getText().isEmpty()) {

            runMultipleAlgorithmSolutions(validator.getMapCountNumAndPairTimes());
        } else {
            validator.generateAlert("Jeśli chcesz pracować w trybie pracy z plikiem wczytaj dane!", Alert.AlertType.ERROR);
        }
    }

    private void resetWhenSingle() {
        ganttChart.setDisable(false);
        finalDataAfterLoad.setText("");
        pathToFile.setText("");
        finalDataToSave.setText("");
        pathToSavedFile.setText("");
    }

    private void resetWhenMultiple() {
        makeTimeArray.setText("");
        timeOfOrderArray.setText("");
        supply.setText("");
        mtOrder.setText("");
        mtDelay.setText("");
        moDelay.setText("");
        moOrder.setText("");
        mzaOrder.setText("");
        mzaDelay.setText("");
        solutionAfterOptimization.setText("");
        solutionAfterOptimizationDelay.setText("");
        sumOfMakeTimes.setText("");
        tableData.getItems().clear();
        ganttChart.setDisable(true);

    }

    @FXML
    private void handleHelpWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Stage stage = new Stage();
        fxmlLoader.setController(new HelperController());
        TabPane p = fxmlLoader.load(getClass().getResource("/fxml/help.fxml").openStream());
        ObservableList<Tab> tabs = p.getTabs();
        Scene scene = new Scene(p, 600, 500);
        stage.setTitle("Pomoc");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Obsługa przycisku generowania diagramów Gantta
     *
     * @param event onSubmit na przycisku RYSUJ WYKRES
     */
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
            yAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(machines)));

            chart.setTitle("Diagram Gantta");
            chart.setLegendVisible(true);
            chart.setBlockHeight(50);
            String machine;

            //M1
            machine = machines[0];
            XYChart.Series series = new XYChart.Series();
            fillDataSeries(series, machine, TypeMap.WITH_MAKE_ORDER_TIMES);

            chart.getData().addAll(series);

            chart.getStylesheets().add(getClass().getResource("/css/ganttchart.css").toExternalForm());

            Scene scene = new Scene(chart, 620, 350);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uzupełnia dane na wykresoe
     *
     * @param machineOneSeries seria danych
     * @param machine          nazwa maszyny
     * @param typeMap          typ mapy z danymi które mają zostać przypisane do danego diagramu
     */
    private void fillDataSeries(XYChart.Series machineOneSeries, String machine, TypeMap typeMap) {
        long startPoint = 0;
        long endPoint = 0;
        LOGGER.setLevel(Level.ALL);
        for (int i = order.size() - 1; i >= 0; i--) {
            String orderName = order.get(i);
            Long aLong = 0L;
            if (typeMap.equals(TypeMap.WITH_MAKE_ORDER_TIMES)) {
                aLong = mapMakeTimeOrder.get(orderName);
            } else if (typeMap.equals(TypeMap.WITH_DEADLINE_TIMES)) {
                aLong = mapDeadLineTimeOrder.get(orderName);
            }
            endPoint = startPoint + aLong;
            String status = checkStatus(i);
            machineOneSeries.getData().add(new XYChart.Data(startPoint, machine, new GanttJavaFX.ExtraData(aLong, status)));
//            LOGGER.info("Dane do diagramu Gantta: " + StringWorker.generateRetrieveString(startPoint + " - " + endPoint + "-" + aLong));
            startPoint = endPoint;
        }
    }

    /**
     * Metoda sprawdza czy dany index jest parzysty lub nie i na tej podstawie ustala styl w CSS
     *
     * @param index indeks danych
     * @return nazwę klasy CSS
     */
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

    /**
     * Metoda działająca w trybie wielu obliczeń
     *
     * @param mapCountNumAndPairTimes
     */
    private void runMultipleAlgorithmSolutions(Map<Integer, Pair<String, String>> mapCountNumAndPairTimes) {
        Collection<Pair<String, String>> values = mapCountNumAndPairTimes.values();
        List<SolutionObject> listOfSolutions = new ArrayList<>();
        for (Pair<String, String> value : values) {
            LogicSolution logicSolution = new LogicSolution();
            DifferentMethodologies df = new DifferentMethodologies();
            SolutionObject so = new SolutionObject();
            logicSolution.clearStatics();
            String currentMakeTimes = value.getKey();
            String currentDeadlineTimes = value.getValue();
            List<Long> currentMakeTimesAsList = StringWorker.prepareListBasedOnString(currentMakeTimes);
            List<Long> currentDeadlineTimesAsList = StringWorker.prepareListBasedOnString(currentDeadlineTimes);
            logicSolution.solveThePoblem(currentMakeTimesAsList, currentDeadlineTimesAsList);

            df.countIncreasingDeadLineTimesOrder(logicSolution);
            df.countIncreasingMakeTimesOrder(logicSolution);
            df.countSupplyIncreaseOrder(logicSolution);

            so.setMakeTimes(currentMakeTimes);
            so.setDeadLineTimes(currentDeadlineTimes);

            //Mopt
            so.setmOptDelay(logicSolution.getFinalDelay());
            so.setmOptOrder(logicSolution.getFinalOrder());

            //Mza - od najmniejszego do największego zapasu czasu
            so.setmZaOrder(df.getSupplyIncreaseOrder());
            so.setmZaDelay(df.getSupplyIncreaseTimeDelay());

            //MO - rosnący czas obróbki
            so.setmOOrder(df.getIncreaseMakeTimeOrder());
            so.setmODelay(df.getIncreateMakeTimeDelay());

            //MT - rosnący termin realizacji
            so.setMtOrder(df.getIncreaseDeadLineTimeOrder());
            so.setMtDelay(df.getIncreaseDeadLineTimeDelay());

            listOfSolutions.add(so);
        }
        saveToFile(listOfSolutions);
    }

    private void saveToFile(List<SolutionObject> listOfSolutions) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.setTitle("Zapisywanie");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            pathToSavedFile.setText(file.getAbsolutePath());
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
                        so.getMakeTimes(), " \n ",
                        "b:",
                        so.getDeadLineTimes(), " \n ",
                        "Mopt", " -",
                        so.getmOptOrder(), " - ",
                        String.valueOf(so.getmOptDelay()), "\n",
                        "MO", " -",
                        so.getmOOrder(), " - ",
                        String.valueOf(so.getmODelay()), "\n",
                        "MT", " -",
                        so.getMtOrder(), " - ",
                        String.valueOf(so.getMtDelay()), "\n",
                        "MZa", " -",
                        so.getmZaOrder(), " - ",
                        String.valueOf(so.getmZaDelay()),
                        "\n ======= \n");
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
//        makeTimeArray.setText("10,20,100,50,100");
        makeTimeArray.setText("10,20,100,50,100,23,50,55,77,222,444");
//        timeOfOrderArray.setText("150,30,110,60,10");
        timeOfOrderArray.setText("150,30,110,60,120,100,150,40,100,333,111");

    }

//    @FXML
//    private void handleSingleCountsCheckBox(ActionEvent event) {
//        if (singleCountsCheckBox.isSelected()) {
//            multipleCountsCheckBox.setSelected(false);
//            singleCountsCheckBox.setSelected(true);
//            makeTimeArray.setDisable(false);
//            timeOfOrderArray.setDisable(false);
//            testButtonSetData.setDisable(false);
//            loadData.setDisable(true);
//        }
//    }
//
//    @FXML
//    private void handleMultipleCountsCheckBox(ActionEvent event) {
//        if (multipleCountsCheckBox.isSelected()) {
//            singleCountsCheckBox.setSelected(false);
//            multipleCountsCheckBox.setSelected(true);
//            makeTimeArray.setDisable(true);
//            timeOfOrderArray.setDisable(true);
//            testButtonSetData.setDisable(true);
//            loadData.setDisable(false);
//        }
//    }
}
