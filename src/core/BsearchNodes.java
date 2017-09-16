package core;

import java.util.*;
public class BsearchNodes {

 private ArrayList < Node > nodes;
 private int lowLimit;
 private int highLimit;
 private int middle;
 private int currentY;

 int counterForUpDownSearch = 1;
 Node returning;
 Node iterated;
 
 /**
  *
  */
 public BsearchNodes(ArrayList < Node > inpNodes) {
   this.nodes = inpNodes;
   setLimits();
  }
 
 /**
  *  
  */
 private void setLimits() {
   lowLimit = 0;
   highLimit = nodes.size();
  }
  
 /**
  *  
  */
 public Node getNode(int x, int y) {

   setLimits();
   findFirstAndSingleByY(y);

   //now it is sure that nodes.get(middle).getY() = y

   returning = nodes.get(middle);
   counterForUpDownSearch = 1;

   if (returning.getX() == x) return returning;
   else {

    while (middle - counterForUpDownSearch >= 0 && middle + counterForUpDownSearch < nodes.size()) {

     iterated = nodes.get(middle - counterForUpDownSearch);
     if (iterated.getY() == y && iterated.getX() == x) return iterated;

     iterated = nodes.get(middle + counterForUpDownSearch);
     if (iterated.getY() == y && iterated.getX() == x) return iterated;

     counterForUpDownSearch++;
    }

   }

   return null;
  }
  
 /**
  *  
  */
 private void findFirstAndSingleByY(int y) {

  middle = lowLimit + ((highLimit - lowLimit) / 2);
  //System.out.println("low: "+lowLimit + " middle: "+middle+" high: "+highLimit);
  currentY = nodes.get(middle).getY();

  if (currentY == y) {
   //System.out.println("returning from top");
   return;
  }
  if (y < currentY) {
   //narrow search limits
   highLimit = middle;
  } else {
   lowLimit = middle;
  }

  findFirstAndSingleByY(y);
  //System.out.println("returning from bottom");
  return;
 }
}
