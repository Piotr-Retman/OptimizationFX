package pl.zut.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Retman on 2016-04-01.
 */
public class GanttController {

    GanttController(List<String> orderToChart){

    }

    @FXML
    private Button closeBtn;

    @FXML
    private BarChart ganttChart;


    @FXML
    private void handleCloseBtn(ActionEvent action) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
