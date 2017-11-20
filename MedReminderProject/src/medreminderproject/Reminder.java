package medreminderproject;
import java.io.*;

public class Reminder implements Serializable {
  public String medicine_name;
  public int interval_hours;
  public int interval_minutes;
  public int start_time_hours;
  public int start_time_minutes;
  public int num_times_a_day;
}
