import java.util.*;
import java.io.*;

class Event{
  private int event_num;
  private String stroke;
  private String gender;
  private int distance;
  private int lowAge;
  private int highAge;
  private boolean relay;

  public Event(int event, String s, String g, int d, int low, int high, boolean r){
    event_num = event;
    stroke = s;
    gender = g;
    distance = d;
    lowAge = low;
    highAge = high;
    relay = r;
  }

  public String toString(){
    return "#" + event_num + " " + lowAge + "-" + highAge + " " + distance + " " + stroke;
  }

  public int getEventNum(){
    return event_num;
  }

  public String getGender(){
    return gender;
  }

  public int[] getAgeRange(){
    int[] range = new int[2];
    range[0] = lowAge;
    range[1] = highAge;
    return range;
  }

  public boolean getRelay(){
    return relay;
  }

  public String getStroke(){
    return stroke;
  }

  public int getDistance(){
    return distance;
  }

}
