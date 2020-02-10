import java.util.*;
import java.io.*;

class Meet{
  private ArrayList<Event> events = new ArrayList<Event>();
  private ArrayList<MeetLineUp> lineUp = new ArrayList<MeetLineUp>();
  private int day;
  private int month;
  private int year;
  private String location;
  private String course;
  private String team;
  private String opponent;
  private boolean home = false;
  private boolean initialized = false;

  public Meet(){
    Scanner sc = new Scanner(System.in);
    System.out.println("What is the start date of the meet MM/DD/YYYY");
    String date = sc.next();
    System.out.println("Where is the meet located?");
    location = sc.next();
    System.out.println("What course is the meet in? (SCM, SCY, LCM)");
    course = sc.next();
    System.out.println("What team are your desired athletes on?");
    team = sc.next();
    System.out.println("Who is the meet against?");
    opponent = sc.next();
    System.out.println("Are you home or away?");
    String hA = sc.next();
    home = false;
    if (hA == "home" || hA == "Home")
      home = true;
    initialized = true;
  }

  public boolean getInitialized(){
    return initialized;
  }

  public void createEvents(String fname){
    //inputs a file name and iterates through the data to add events to the ArrayList
    boolean error = false;
    //if there is an error in the data, set error to true so it is not added to the ArrayList
  }

  public void createEvents(int num){
    for (int i = 1; i <= num; i++){
      inputEvent(i);
    }
  }

  public void inputEvent(int event_num){
    Scanner sc = new Scanner(System.in);
    System.out.println("What stroke is this event?");
    String stroke = sc.next();
    System.out.println("What gender is this race?");
    String gender = sc.next();
    System.out.println("What distance is this race? (Integer)");
    int distance;
    while(true){
      String d = sc.next();
      try{
        distance = Integer.parseInt(d);
        break;
      } catch (InputMismatchException ime){
        System.out.println("Your input must be an Integer! Try again.");
      }
    }
    System.out.println("What is the low of the age range? (Integer)");
    int lowAge;
    while (true){
      String low = sc.next();
      try{
        lowAge = Integer.parseInt(low);
        break;
      } catch (InputMismatchException ime){
        System.out.println("Your input must be an Integer! Try again.");
      }
    }
    System.out.println("What is the high of the range range? (Integer)");
    int highAge;
    while (true){
      String high = sc.next();
      try{
        highAge = Integer.parseInt(high);
        break;
      } catch (InputMismatchException ime){
        System.out.println("Your input must be an Integer! Try again.");
      }
    }
    System.out.println("Is this event a relay?");
    String r = sc.next();
    boolean relay = false;
    if (r == "Yes" || r == "yes" || r == "y" || r == "Y")
      relay = true;
    Event n = new Event(event_num, stroke, gender, distance, lowAge, highAge, relay);
    events.add(n);
  }
}
