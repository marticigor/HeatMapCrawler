package core;

import java.util.*;
public class BsearchNodes {

 private ArrayList < Node > nodes;
 private int lowLimit;
 private int highLimit;
 private int middle;
 private int currentY;

 int counterUpDownSearch = 1;
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
   counterUpDownSearch = 1;

   if (returning.getX() == x) return returning;
   else {

    while (middle - counterUpDownSearch >= 0 && middle + counterUpDownSearch < nodes.size()) {

     iterated = nodes.get(middle - counterUpDownSearch);
     if (iterated.getY() == y && iterated.getX() == x) return iterated;

     iterated = nodes.get(middle + counterUpDownSearch);
     if (iterated.getY() == y && iterated.getX() == x) return iterated;

     counterUpDownSearch++;
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
