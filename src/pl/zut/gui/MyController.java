package pl.zut.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.zut.logic.optimization.LogicSolution;

import java.util.List;

public class MyController {

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
    private void handleSubmitButtonAction(ActionEvent event) {

        if (singleCountsCheckBox.isSelected()) {
            validateSingleCountPossible();
        } else if (multipleCountsCheckBox.isSelected()) {
            validateMultipleCountPossible();
        }
    }

    private void validateMultipleCountPossible() {

    }

    private void validateSingleCountPossible() {
        boolean hasData = true;

        if (makeTimeArray.getText().isEmpty()) {
            hasData = false;
            generateError("Proszę uzupełnić czasy obróbek!");
        } else if (timeOfOrderArray.getText().isEmpty()) {
            hasData = false;
            generateError("Proszę uzupełnić terminy zleceń");
        }

        if (hasData) {
            validateInsideDataForSingleCounts();
        }
    }

    private void validateInsideDataForSingleCounts() {
        LogicSolution ls = new LogicSolution();
        List<Long> timeOfOrderArrayAsList = ls.prepareListBasedOnString(timeOfOrderArray.getText());
        List<Long> makeTimeArrayAsList = ls.prepareListBasedOnString(makeTimeArray.getText());


        boolean equal = timeOfOrderArrayAsList.size() == makeTimeArrayAsList.size();
        if (equal) {
            runAlgorithm(ls,makeTimeArrayAsList,timeOfOrderArrayAsList);
        } else {
            generateError("Nieodpowiednia ilość danych w listach. Lista czasów obróbek:" + makeTimeArrayAsList.size() + "Lista terminów:" + timeOfOrderArrayAsList.size());
        }
    }

    private void runAlgorithm(LogicSolution ls, List<Long> makeTimeArrayAsList, List<Long> timeOfOrderArrayAsList) {
        ls.clearStatics();
        ls.setListMakeOrderTimes(makeTimeArrayAsList);
        ls.setListDeadLineTimes(timeOfOrderArrayAsList);
        ls.solveThePoblem(makeTimeArrayAsList,timeOfOrderArrayAsList,0);
        updateVisualData(ls);
    }

    private void updateVisualData(LogicSolution ls) {
        sumOfMakeTimes.setText(String.valueOf(LogicSolution.getStaticSumOfMakeOrderTimes()));
        solutionFirst.setText(ls.getBaseOrder());
        solutionFirstDelay.setText(String.valueOf(LogicSolution.getStaticBaseDelay()));
        solutionAfterOptimizationDelay.setText(String.valueOf(ls.getFinalDelay()));
        solutionAfterOptimization.setText(ls.getFinalOrder());
    }

    private void generateError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
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
