package medreminderproject;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.beans.value.*;
import java.util.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.concurrent.Task;
import java.nio.file.*;
import java.io.*;

import java.nio.charset.StandardCharsets;
/**
 *
 * @author berkh
 */
public class MedReminderProject extends Application {
  static Alarm alarm = null;
  static final int MAIN_LOOP_FREQUENCY = 750; // How frequent the main loop will loop in milliseconds.
  static final int DELAY_TIME = 5; // How many minutes the reminder should be delayed if the user hits the delay button.
  static final String REMINDERS_SAVE_FILENAME = "reminders.bin";
  static final String MEDICATION_LOG_SAVE_FILENAME = "medication_log.bin";
  static Controller control = null; // We can use this to access all of the ui elements
  static ArrayList<Reminder> reminders = new ArrayList<Reminder>();
  static ArrayList<Medication_Log_Entry> medication_log = new ArrayList<Medication_Log_Entry>();
  static ArrayList<Boolean> dialog_box_already_shown = new ArrayList<Boolean>();
  public static void save_all_data() {
    try {
      ArrayList<Reminder> reminders_to_serialize = new ArrayList<Reminder>();
      for (int i = 0; i < reminders.size(); i++) {
        if (!reminders.get(i).temporary) reminders_to_serialize.add(reminders.get(i));
      }
      
      FileOutputStream file_output_stream = new FileOutputStream(REMINDERS_SAVE_FILENAME);
      ObjectOutputStream output_stream = new ObjectOutputStream(file_output_stream);
      output_stream.writeObject(reminders_to_serialize);
      output_stream.flush();
      output_stream.close();
      file_output_stream.close();
      file_output_stream = new FileOutputStream(MEDICATION_LOG_SAVE_FILENAME);
      output_stream = new ObjectOutputStream(file_output_stream);
      output_stream.writeObject(medication_log);
      output_stream.flush();
      output_stream.close();
      file_output_stream.close();
    } catch (Exception e) {e.printStackTrace();}
  }
  @SuppressWarnings("unchecked") // This is just to allow me to cast the object to an ArrayList<Reminder>
  public static boolean load_all_data() {
    File file = new File(REMINDERS_SAVE_FILENAME);
    if (!file.exists() || file.isDirectory()) return false;
    try {
      FileInputStream file_input_stream = new FileInputStream(REMINDERS_SAVE_FILENAME);
      ObjectInputStream object_input_stream = new ObjectInputStream(file_input_stream);
      reminders = (ArrayList<Reminder>)object_input_stream.readObject();
      object_input_stream.close();
      file_input_stream.close();
      file_input_stream = new FileInputStream(MEDICATION_LOG_SAVE_FILENAME);
      object_input_stream = new ObjectInputStream(file_input_stream);
      medication_log = (ArrayList<Medication_Log_Entry>)object_input_stream.readObject();
      object_input_stream.close();
      file_input_stream.close();
    } catch (Exception e) {}
    return true;
  }

  public static void main_loop() {
    for (int i = 0; i < reminders.size(); i++) {
      while (i >= dialog_box_already_shown.size()) dialog_box_already_shown.add(false);
      Reminder r = reminders.get(i);
      Calendar cal = Calendar.getInstance();
      boolean reminder_went_off = false;
      for (int time = 0; time < r.num_times_a_day; time++) {
        int time_hours = r.start_time_hours + r.interval_hours*time;
        int time_minutes = r.start_time_minutes + r.interval_minutes*time;
        while (time_minutes >= 60) {
          time_minutes -= 60;
          time_hours++;
        }
        if (time_hours == cal.get(Calendar.HOUR_OF_DAY) &&
            time_minutes == cal.get(Calendar.MINUTE)) {
          reminder_went_off = true;
          if (!dialog_box_already_shown.get(i)) {
            alarm = new Alarm();
            //@NOTE: All of the code below in this block is just creating a dialog box. It's not really anything fancy:
            Stage dialog_stage = new Stage();
            dialog_stage.initModality(Modality.WINDOW_MODAL);

            Label dialog_box_note = new Label(String.format("Please Take Medicine '%s'", r.medicine_name));
            dialog_box_note.setAlignment(Pos.BASELINE_CENTER);

            Button done_button = new Button("Done");
            int reminder_index = i;
            done_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  dialog_stage.close();
                  if (alarm != null) alarm.alarmStop();
                  alarm = null;
                  Medication_Log_Entry entry = new Medication_Log_Entry();
                  entry.medicine_name = r.medicine_name;
                  entry.hour = cal.get(Calendar.HOUR_OF_DAY);
                  entry.minute = cal.get(Calendar.MINUTE);
                  entry.year = cal.get(Calendar.YEAR);
                  entry.month = cal.get(Calendar.MONTH);
                  entry.day = cal.get(Calendar.DAY_OF_MONTH);
                  medication_log.add(entry);
                  update_medication_log_label();
                  if (r.temporary) {
                    dialog_box_already_shown.remove(reminder_index);
                    reminders.remove(reminder_index);
                  }
                  save_all_data();
                }
              });
            Button delay_button = new Button("Delay");
            delay_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {  
                  dialog_stage.close();
                  if (alarm != null) alarm.alarmStop();
                  alarm = null;
                  Reminder new_reminder = new Reminder();
                  new_reminder.medicine_name = r.medicine_name;
                  new_reminder.start_time_hours = r.start_time_hours;
                  new_reminder.start_time_minutes = r.start_time_minutes + DELAY_TIME;
                  if (new_reminder.start_time_minutes >= 60) {
                    new_reminder.start_time_minutes -= 60;
                    new_reminder.start_time_hours++;
                  }
                  new_reminder.num_times_a_day = 1;
                  new_reminder.temporary = true;
                  reminders.add(new_reminder);

                  if (r.temporary) {
                    dialog_box_already_shown.remove(reminder_index);
                    reminders.remove(reminder_index);
                  }
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
        }
      }
      if (!reminder_went_off) dialog_box_already_shown.set(i, false);
    }
  }

  @SuppressWarnings("unchecked")
  public static void update_medication_log_label() {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < medication_log.size(); i++) {
      Medication_Log_Entry e = medication_log.get(i);
      String text = String.format("'%s' was taken at %02d:%02d on %02d-%02d-%d\n", e.medicine_name, e.hour, e.minute, e.month, e.day, e.year);
      list.add(text);
    }
    control.medication_log_observable_list.setAll(list);
    control.medication_log_list.setItems(control.medication_log_observable_list);
  }

  /*
<Label fx:id="medication_log_label" alignment="TOP_CENTER" layoutY="8.0" prefHeight="718.0" prefWidth="596.0" textAlignment="CENTER">
                     <font>
                        <Font size="18.0" />
                     </font>
                  </Label>
   */

  /*
            <ListView fx:id="medication_info_label" alignment="TOP_CENTER" layoutX="-3.0" prefHeight="718.0" prefWidth="582.0" text="Sample Medication at Sample Time" textAlignment="CENTER">
                     <font>
                        <Font size="18.0" />
                     </font></ListView>
   */
  @SuppressWarnings("unchecked")
  public static void update_medication_info_label() {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < reminders.size(); i++) {
      Reminder r = reminders.get(i);
      if (r.temporary) continue;
      String text = String.format("%s to be taken every %d hours and %d minutes starting at %02d:%02d %s, %d times daily\n", r.medicine_name, r.interval_hours, r.interval_minutes, r.start_time_hours > 12 ? r.start_time_hours - 12 : r.start_time_hours, r.start_time_minutes, r.start_time_hours >= 12 ? "PM" : "AM", r.num_times_a_day);
      list.add(text);
    }
    
    control.medication_reminders_observable_list.setAll(list);
    control.medication_reminder_list.setItems(control.medication_reminders_observable_list);
  }

  public static void only_allow_numberic_input_for_text_field(TextField text_field) {
    //Source: https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx#30796829
    text_field.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String old_value, String new_value) {
          if (!new_value.matches("\\d*")) text_field.setText(new_value.replaceAll("[^\\d]", ""));
        }
      });
  }

  public static boolean set_reminder_by_ui(Reminder r) {
    if (control.num_times_a_day_field.getText().equals("") ||
        control.start_time_hours_field.getText().equals("") ||
        control.start_time_minutes_field.getText().equals("") ||
        control.interval_hours_field.getText().equals("") ||
        control.interval_minutes_field.getText().equals("")) {
      //@TODO: Maybe an error sound here or something
      return false;
    }
    r.medicine_name = control.medication_name_field.getText();
    r.num_times_a_day = Integer.parseInt(control.num_times_a_day_field.getText());
    r.start_time_hours = Integer.parseInt(control.start_time_hours_field.getText());
    r.start_time_minutes = Integer.parseInt(control.start_time_minutes_field.getText());
    while (r.start_time_minutes >= 60) {
      r.start_time_minutes -= 60;
      r.start_time_hours++;
    }
    if (control.start_time_pm_checkbox.isSelected()) r.start_time_hours += 12;

    r.interval_hours = Integer.parseInt(control.interval_hours_field.getText());
    r.interval_minutes = Integer.parseInt(control.interval_minutes_field.getText());
    while (r.interval_minutes >= 60) {
      r.interval_minutes -= 60;
      r.interval_hours++;
    }
    return true;
  }
  
  @SuppressWarnings("unchecked")
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
          if (!set_reminder_by_ui(r)) return;
          reminders.add(r);
          update_medication_info_label();
          save_all_data();
        }
        });

    control.remove_selected_reminder_button.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          int index = control.medication_reminder_list.getSelectionModel().getSelectedIndex();
          if (index < 0 || index >= reminders.size()) return;
          dialog_box_already_shown.remove(index);
          reminders.remove(index);
          update_medication_info_label();
          save_all_data();
        }
      });
    control.edit_selected_reminder_button.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          int index = control.medication_reminder_list.getSelectionModel().getSelectedIndex();
          if (index < 0 || index >= reminders.size()) return;
          Reminder r = reminders.get(index);
          if (!set_reminder_by_ui(r)) return;
          dialog_box_already_shown.set(index, false);
          update_medication_info_label();
          save_all_data();
        }
      });

    control.medication_reminder_list.getSelectionModel().selectedItemProperty()
      .addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observable,
                              String oldValue, String newValue) {
            int index = control.medication_reminder_list.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= reminders.size()) return;
            Reminder r = reminders.get(index);
            control.interval_hours_field.setText("" + r.interval_hours);
            control.interval_minutes_field.setText("" + r.interval_minutes);
            control.start_time_hours_field.setText("" + r.start_time_hours);
            control.start_time_minutes_field.setText("" + r.start_time_minutes);
            control.num_times_a_day_field.setText("" + r.num_times_a_day);
            control.medication_name_field.setText("" + r.medicine_name);
          }
        });

    only_allow_numberic_input_for_text_field(control.interval_hours_field);
    only_allow_numberic_input_for_text_field(control.interval_minutes_field);
    only_allow_numberic_input_for_text_field(control.start_time_hours_field);
    only_allow_numberic_input_for_text_field(control.start_time_minutes_field);
    only_allow_numberic_input_for_text_field(control.num_times_a_day_field);

    load_all_data();
    update_medication_info_label();
    update_medication_log_label();
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
