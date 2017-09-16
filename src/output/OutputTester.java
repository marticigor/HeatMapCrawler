package output;

import java.util.*;

public class OutputTester {


 public void test() {

  ArrayList < Trackpoint > track = new ArrayList < Trackpoint > ();

  for (int i = 1; i < 100; i++) {

   Trackpoint t = new Trackpoint(i, i, 10, 10, 10, 10);
   track.add(t);

  }
  
  System.out.println("track.size " + track.size());
  OutputXml out = new OutputXml(track);

  try {

   out.composeOutputDoc();
   out.writeOutputFile();

  } catch (Exception e) {

   e.printStackTrace();

  }
 }
}