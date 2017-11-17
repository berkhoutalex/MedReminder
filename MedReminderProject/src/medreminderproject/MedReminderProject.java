/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medreminderproject;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;
import java.util.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
/**
 *
 * @author berkh
 */
public class MedReminderProject extends Application {
  class Reminder {
    String medicine_name;
    int interval_hours;
    int interval_minutes;
    int start_time_hours;
    int start_time_minutes;
    int num_times_a_day;
  }
  
  static Controller control = null;
  static ArrayList<Reminder> reminders = new ArrayList<Reminder>();
  
  public static void update_medication_info_label() {
    String text = "Reminders List:\n";
    for (int i = 0; i < reminders.size(); i++) {
      Reminder r = reminders.get(i);
      text += String.format("Name: %s, Interval Hours: %d, Interval Minutes: %d, Start Time Hours: %d, Start Time Minutes: %d, Number Of Times a Day: %d\n", r.medicine_name, r.interval_hours, r.interval_minutes, r.start_time_hours, r.start_time_minutes, r.num_times_a_day);
    }
    control.medication_info_label.setText(text);
  }
  
  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("UILayout.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    control = loader.getController();
    control.add_reminder_button.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          Reminder r = new Reminder();
          r.medicine_name = control.medication_name_field.getText();
          //@BUG: This won't work if the user doesn't type in a valid number into the textbox, we should find a different method of getting numberic input:
          r.num_times_a_day = Integer.parseInt(control.num_times_a_day_field.getText());
          r.start_time_hours = Integer.parseInt(control.start_time_hours_field.getText());
          r.start_time_minutes = Integer.parseInt(control.start_time_minutes_field.getText());

          r.interval_hours = Integer.parseInt(control.interval_hours_field.getText());
          r.interval_minutes = Integer.parseInt(control.interval_minutes_field.getText());
          
          reminders.add(r);
          update_medication_info_label();
        }
        });

    update_medication_info_label();
    stage.setTitle("Medication Reminder");
    stage.setScene(scene);
    stage.show();

    Thread time_loop_thread = new Thread() {
        public void run() {
          try { // Only doing this because java is forcing me to...
            for (;;) {
              //Calendar cal1 = Calendar.getInstance();
              //System.out.printf("%d:%d\n", cal1.get(Calendar.HOUR_OF_DAY), cal1.get(Calendar.MINUTE));
              Thread.sleep(500);
              for (int i = 0; i < reminders.size(); i++) {
                Reminder r = reminders.get(i);
                Calendar cal = Calendar.getInstance();
                if (r.start_time_hours == cal.get(Calendar.HOUR_OF_DAY) &&
                    r.start_time_minutes == cal.get(Calendar.MINUTE)) {
                  //
                  // @BUG: This dialog box code won't run because this happens in a thread
                  // that isn't the main thread. I'm not sure how to get around it...
                  // it's kind of dumb.
                  //
                  Stage dialog_stage = new Stage();
                  dialog_stage.initModality(Modality.WINDOW_MODAL);

                  Label dialog_box_note = new Label(String.format("Please Take Medicine '%s'", r.medicine_name));
                  dialog_box_note.setAlignment(Pos.BASELINE_CENTER);

                  Button done_button = new Button("Done");
                  done_button.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        dialog_stage.close();
                      }
                    });
                  Button delay_button = new Button("Delay");
                  delay_button.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        dialog_stage.close();
                      }
                    });
                  HBox horizontal_box = new HBox();
                  horizontal_box.setAlignment(Pos.BASELINE_CENTER);
                  horizontal_box.setSpacing(50.0);
                  horizontal_box.getChildren().addAll(done_button, delay_button);

                  VBox vertical_box = new VBox();
                  vertical_box.setSpacing(50.0);
                  vertical_box.getChildren().addAll(dialog_box_note, horizontal_box);

                  dialog_stage.setScene(new Scene(vertical_box));
                  dialog_stage.show();
                }
              }
            }
          } catch (Exception e) { System.out.println(e.getMessage()); }
        }
      };
    time_loop_thread.setDaemon(true); // Makes the thread close when the program gets closed
    time_loop_thread.start();
  }
    
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
    
}
