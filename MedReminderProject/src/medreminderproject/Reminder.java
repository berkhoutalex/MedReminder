package medreminderproject;
import java.io.*;

public class Reminder implements Serializable {
  public String medicine_name = "";
  public int interval_hours = 0;
  public int interval_minutes = 0;
  public int start_time_hours = 0;
  public int start_time_minutes = 0;
  public int num_times_a_day = 0;
  public boolean temporary = false;
}
