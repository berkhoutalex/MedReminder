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
import javafx.concurrent.Task;

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

  static final int MAIN_LOOP_FREQUENCY = 750; // How frequent the main loop will loop in milliseconds.
  static Controller control = null; // We can use this to access all of the ui elements
  static ArrayList<Reminder> reminders = new ArrayList<Reminder>();
  static ArrayList<Boolean> dialog_box_already_shown = new ArrayList<Boolean>();

  public static void main_loop() {
    for (int i = 0; i < reminders.size(); i++) {
      while (i >= dialog_box_already_shown.size()) dialog_box_already_shown.add(false);
      Reminder r = reminders.get(i);
      Calendar cal = Calendar.getInstance();
      if (r.start_time_hours == cal.get(Calendar.HOUR_OF_DAY) &&
          r.start_time_minutes == cal.get(Calendar.MINUTE)) {
        if (!dialog_box_already_shown.get(i)) {
          //@NOTE: All of the code below in this block is just creating a dialog box. It's not really anything fancy:
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
          dialog_box_already_shown.set(i, true);
        }
      } else {
        dialog_box_already_shown.set(i, false);
      }
    }
  }
  
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

    //@NOTE: All this thread junk below is just a hack so that I could get a loop that would continuously run alongside the application (since we need it for checking the time, etc.):
    Task main_loop_task = new Task<Void>() {
        @Override
        public Void call() throws Exception {
          for (;;) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {main_loop();}
              });
            Thread.sleep(MAIN_LOOP_FREQUENCY);
          }
        }
      };
    Thread main_loop_thread = new Thread(main_loop_task);
    main_loop_thread.setDaemon(true); // Makes the thread close when the program gets closed
    main_loop_thread.start();
  }
    
  public static void main(String[] args) {
    launch(args);
  }
}
