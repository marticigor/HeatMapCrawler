package core;

public class Pause {

 /**
  * 
  */
 public static void pause(int pause) {
  //System.out.println("pausa "+pause/1000+" s.");
  try {
   Thread.sleep(pause);
  } catch (Exception e) {}
 }

}
