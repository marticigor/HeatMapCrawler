package core;

import java.util.*;

import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;
public class AdjacencyFinder implements IColorScheme {

 private ImageResource noded;
 private List < Node > nodes;
 private RoundIteratorOfPixels riop;
 private HashSet < Pixel > redCluster;
 private RecursiveClusterFinder rcf;
 private ImageResource visualizeIR;
 private boolean visual;
 private boolean debug;

 private HashMap < Pixel, Node > mapPixToNode;

 private int bottleneckSize, passableSize;

 public AdjacencyFinder(ImageResource noded, List < Node > nodes, boolean visual, boolean debug, int bottleneckSize, int passableSize) {

  this.noded = noded;
  this.nodes = nodes;
  this.visual = visual;
  this.debug = debug;
  this.bottleneckSize = bottleneckSize;
  this.passableSize = passableSize;

  riop = new RoundIteratorOfPixels();
  riop.setImageResource(noded);

  mapPixToNode = new HashMap < Pixel, Node > ();

  rcf = new RecursiveClusterFinder(noded, nodes, mapPixToNode, redScheme[0], redScheme[1], redScheme[2],
   greenScheme[0], greenScheme[1], greenScheme[2],
   this.debug); //visualize "debuger" 

  if (this.visual) visualizeIR = new ImageResource(noded.getWidth(), noded.getHeight());

 }

 /**
  * wrap method to build all list into nodes
  */
 public void buildAdjacencyLists() {

   int nmbOfBottlenecks = 0;
   Pixel p;

   for (Node n: nodes) {
    if (isBottleNeck(n)) {
     n.setBottleneck(true);
     nmbOfBottlenecks++;
    }
   }
   System.out.println("Number of bottleneck nodes: " + nmbOfBottlenecks);

   for (Node n: nodes) {

    if (n.getBottleneck()) {
     p = noded.getPixel(n.getX(), n.getY());
     p.setRed(yellowScheme[0]);
     p.setGreen(yellowScheme[1]);
     p.setBlue(yellowScheme[2]);

    }
   }

   noded.draw();
   
   //Pause.pause(2000);

   Collections.sort(nodes);

   { //scope for adjacency lists builder

    /**
    //testing code
    //----------------------------------------------------------------------------
    BsearchNodes bSearch = new BsearchNodes(nodes);
    System.out.println("LIST OF NODES COORDINATES sorted by Y coordinate (vertical)");
   
    for(Node n : nodes){
        System.out.println(n.toString());
    }
   
    for(Node tester : nodes){
        System.out.println("tester node we are searching for " + tester.toString());
        Node returned = bSearch.getNode(tester.getX(), tester.getY());
        System.out.println("tester node returned " + returned.toString());
        //Pause.pause(3000);
    }
    //testing code
    //----------------------------------------------------------------------------
    */

    redCluster = new HashSet < Pixel > ();

    Pixel currP;
    int currX, currY;

    maskAllNodes(true); //this is first run so map of pixels to recreate "green squares" gets created;
    noded.draw();
    
    //Pause.pause(3000);

    for (Node buildForThis: nodes) {

     maskOrDemaskNode(buildForThis, false, false); //demask this node, not first run

     redCluster.clear();

     currX = buildForThis.getX();
     currY = buildForThis.getY();
     currP = noded.getPixel(currX, currY);

     riop.resetCount();
     riop.setPixelToCheckAround(currP);
     int count = 0;

     //-------------------------------------------------------------------------------------------------------------------------
     for (Pixel iterP: riop) {
      if (count == 0 || count == 3 || count == 5) putAllSurrReds(iterP);
      count++;
     }
     //-------------------------------------------------------------------------------------------------------------------------  
     //now redCluster is build for node buildForThis
     HashSet < Node > adjacents = rcf.getAdjacents();
     for(Node singleAdjacent : adjacents) {
         buildForThis.addAdjacentNode(singleAdjacent);
     }
     rcf.resetToNewAdjacents();

     if (visual) { //visual
      for (Pixel pDebug: redCluster) {

       Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());

       pIr.setRed(blueischScheme[0]);
       pIr.setGreen(blueischScheme[1]);
       pIr.setBlue(blueischScheme[2]);

      }

      visualizeIR.draw();
      //Pause.pause(1000);

      for (Pixel pDebug: redCluster) {

       Pixel pIr = visualizeIR.getPixel(pDebug.getX(), pDebug.getY());

       pIr.setRed(redischScheme[0]);
       pIr.setGreen(redischScheme[1]);
       pIr.setBlue(redischScheme[2]);

      }
     } //visual
     maskOrDemaskNode(buildForThis, true, false); //doMask
    }

    demaskAllNodes();
    noded.draw();

    System.out.println("actualy highlighting nodes");

    RoundIteratorOfPixels makeYellow = new RoundIteratorOfPixels();
    makeYellow.setImageResource(noded);

    for (Node n: nodes) {
     boolean bottleneck = n.getBottleneck();
     makeYellow.resetCount();
     Pixel pY = noded.getPixel(n.getX(), n.getY());
     makeYellow.setPixelToCheckAround(pY);
     for (Pixel pix: makeYellow) {
      if (bottleneck) {
       pix.setRed(yellowScheme[0]);
       pix.setGreen(yellowScheme[1]);
       pix.setBlue(yellowScheme[2]);
      } else {
       pix.setRed(whiteScheme[0]);
       pix.setGreen(whiteScheme[1]);
       pix.setBlue(whiteScheme[2]);
      }
     }
    }
   } //scope for adjacency lists builder
   noded.draw();
  }
  
 /**
  * 
  */
 private void putAllSurrReds(Pixel currP) {

  rcf.resetAllCluster();
  rcf.setCluster(currP);

  HashSet < Pixel > branch = rcf.getAllCluster();
  copyBranchIntoRedCluster(branch);

 }

 /**
  *  
  */
 private void copyBranchIntoRedCluster(HashSet < Pixel > branch) {

  for (Pixel p: branch) {

   redCluster.add(p);

  }
 }

 /**
  * 
  */
 private void maskAllNodes(boolean firstRun) {
   for (Node node: nodes) {
    maskOrDemaskNode(node, true, firstRun);
   }
  }
  
  /**
   *  
   */
 private void demaskAllNodes() {
   for (Node node: nodes) {
    maskOrDemaskNode(node, false, false);
   }
  }
  
  /**    
      * Creates a Pixel from RGB values and an Alpha value (for transparency).
      * 
      * @param r the red value
      * @param g the green value
      * @param b the blue value
      * @param a the Alpha value
      * @param x the x-coordinate of this pixel in the image
      * @param y the y-coordinate of this pixel in the image
      *
     Pixel (int r, int g, int b, int a, int x, int y) {
         red = r;
         green = g;
         blue = b;
         alpha = a;
         myX = x;
         myY = y;
     }
     
      * Creates a new Pixel from with the same values as the other pixel passed
      * as a parameter.
      * 
      * @param other another pixel
      *
     public Pixel (Pixel other) {
         this(other.getRed(), other.getGreen(), other.getBlue(), 
              other.getAlpha(), other.getX(), other.getY());
     }
   * 
   */
   
 /**
  *
  */
 private void maskOrDemaskNode(Node n, boolean doMask, boolean firstRun) {

  //int xToDeb = 1323;
  //int yToDeb = 6;
  //boolean deb = false;

  //if(xToDeb == n.getX() && yToDeb == n.getY()) deb = true;

  //String pre;
  //if(!doMask) pre = "\t\t\t\t demask ";else pre = "mask ";
  //System.out.println(pre + "x y " + n.getX() + " " + n.getY());

  int maskSize = 0;
  int toSizes = 0;
  int counter = 0;
  ArrayList < Pixel > retrievedMask = null;

  if (n.getBottleneck()) maskSize = bottleneckSize;
  else maskSize = passableSize;

  toSizes = (maskSize - 1) / 2;

  if (!doMask && (maskSize != (int) Math.sqrt(n.getMask().size()))) {
   throw new RuntimeException("Mask size problem." + n.getMask().size());
  }
  if (!doMask) retrievedMask = n.getMask();

  for (int x = n.getX() - toSizes; x < n.getX() + toSizes + 1; x++) {
   for (int y = n.getY() - toSizes; y < n.getY() + toSizes + 1; y++) {

    //if(deb)System.out.println("x y " + x + "  " + y);

    if (doMask) { //mask
     Pixel original = noded.getPixel(x, y);
     Pixel copy = new Pixel(original);
     original.setRed(greenScheme[0]);
     original.setGreen(greenScheme[1]);
     original.setBlue(greenScheme[2]);
     
     if (firstRun) {
     
         n.addPixelToMap(copy);
         mapPixToNode.put( original, n);
         
     }
     
    } else { //demask
     Pixel original = noded.getPixel(x, y);
     Pixel copy = retrievedMask.get(counter);
     original.setRed(copy.getRed());
     original.setGreen(copy.getGreen());
     original.setBlue(copy.getBlue());
     counter++;
     //if(deb) System.out.println("counter " + counter);
    }
   }
  }
 }

 /**
  *  
  */
 private boolean isBottleNeck(Node n) {

   byte counter = 0;
   boolean[] takenSeat = new boolean[8];
   Pixel p;
   riop.resetCount();

   p = noded.getPixel(n.getX(), n.getY());

   riop.setPixelToCheckAround(p);

   for (Pixel pRecycled: riop) {
    if (isWhite(pRecycled) || isRed(pRecycled)) takenSeat[counter] = true;
    else takenSeat[counter] = false;
    counter++;
   }
   for (byte i = 0; i < 4; i++) {
    if (!takenSeat[i] && !takenSeat[i + 4]) return true; //rotation of xox mask
   }

   //now rotation of permutation matrix
   //xoo
   //oox
   //oxo
   int firstPoint, secondPoint;
   for (byte i = 0; i < 8; i++) {

    firstPoint = i + 3;
    if (firstPoint > 7) firstPoint -= 8;
    secondPoint = i + 5;
    if (secondPoint > 7) secondPoint -= 8;

    if (!takenSeat[i] && !takenSeat[firstPoint] && !takenSeat[secondPoint]) return true;

   }
   return false;
  }
 
 private boolean isWhite(Pixel p) {
   if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]) return true;
   return false;
  }
 private boolean isRed(Pixel p) {
   if (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]) return true;
   return false;
  }
 @SuppressWarnings("unused")
 private boolean isYellow(Pixel p) {
  if (p.getRed() == yellowScheme[0] && p.getGreen() == yellowScheme[1] && p.getBlue() == yellowScheme[2]) return true;
  return false;
 }
}
