package medreminderproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;

/**
 * FXML Controller class
 *
 * @author berkh
 */
public class Controller implements Initializable {
  @FXML public TextField interval_hours_field;
  @FXML public TextField interval_minutes_field;
  @FXML public TextField start_time_hours_field;
  @FXML public TextField start_time_minutes_field;
  @FXML public TextField num_times_a_day_field;
  @FXML public TextField medication_name_field;
  @FXML public Button add_reminder_button;
  @FXML public Label medication_info_label;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {} 
}
