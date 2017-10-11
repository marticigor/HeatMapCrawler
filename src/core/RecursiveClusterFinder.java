 package core;

 import java.util.*;

 import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;
 public class RecursiveClusterFinder {

     private HashSet < Pixel > allClusterAroundNode;
     private RoundIteratorOfPixels iteratorRound;
     private ImageResource ir;
     private int red, green, blue;
     private int redAllBottleneck, greenAllBottleneck, blueAllBottleneck;
     private boolean redClusterSearch = false;
     private boolean visualize;
     //private List < Node > nodes;
     private Visualizer vis;

     private HashMap < Pixel, Node > mapPixToNode;
     private HashSet < Node > adjacents;

     /**
      *
      */
     public RecursiveClusterFinder(ImageResource ir, int red, int green, int blue) {

         this.ir = ir;
         this.red = red;
         this.green = green;
         this.blue = blue;

         iteratorRound = new RoundIteratorOfPixels();
         iteratorRound.setImageResource(ir);

         this.redClusterSearch = false;

         allClusterAroundNode = new HashSet < Pixel > ();

     }

     /**
      *  
      */
     public RecursiveClusterFinder(ImageResource ir, List < Node > nodes, HashMap < Pixel, Node > mapPixToNode,
         int red, int green, int blue, int redAllBottleneck,
         int greenAllBottleneck, int blueAllBottleneck, boolean visualize) {

         this.ir = ir;
         this.red = red;
         this.green = green;
         this.blue = blue;

         this.redAllBottleneck = redAllBottleneck;
         this.greenAllBottleneck = greenAllBottleneck;
         this.blueAllBottleneck = blueAllBottleneck;

         this.visualize = visualize;

         if (visualize) vis = new Visualizer();

         this.redClusterSearch = true;

         iteratorRound = new RoundIteratorOfPixels();
         iteratorRound.setImageResource(ir);

         //this.nodes = nodes;
         this.mapPixToNode = mapPixToNode;

         allClusterAroundNode = new HashSet < Pixel > ();
         adjacents = new HashSet < Node > ();

     }

     /**
      * loads recursively set of (colorScheme) pixels into neighbours and then allClusterAroundNode 
      */
     public void setCluster(Pixel p) {

         //currentNodeX
         //currentNodeY

         if (allClusterAroundNode.size() > 3000) {
             return;
         }

         if (!redClusterSearch) allClusterAroundNode.add(p);

         if (redClusterSearch && p.getRed() == red &&
             p.getGreen() == green && p.getBlue() == blue) allClusterAroundNode.add(p);

         if (visualize && allClusterAroundNode.size() == 1) vis.displayInitial();
         if (visualize && allClusterAroundNode.size() % 50 == 0) vis.displayProgres();

         iteratorRound.resetCount();
         iteratorRound.setPixelToCheckAround(p);

         if (redClusterSearch) {
             for (Pixel pixelStoper: iteratorRound) {
                 if (pixelStoper.getRed() == redAllBottleneck && //check for allBottlenecks
                     pixelStoper.getGreen() == greenAllBottleneck &&
                     pixelStoper.getBlue() == blueAllBottleneck) {

                     Node mappedToThisPix = mapPixToNode.get(pixelStoper);
                     adjacents.add(mappedToThisPix);

                     return;
                 }
             }
             iteratorRound.resetCount();
         }

         HashSet < Pixel > neighbours = new HashSet < Pixel > ();

         for (Pixel pRecycled: iteratorRound) {
             if (pRecycled.getRed() == red && pRecycled.getGreen() == green && pRecycled.getBlue() == blue) {
                 neighbours.add(pRecycled); //color scheme check
             }
         }

         for (Pixel pCopied: neighbours) {
             if (allClusterAroundNode.contains(pCopied)) continue; //to skip recursion in many cases
             allClusterAroundNode.add(pCopied);
             setCluster(pCopied); //now immerse into recursion
         }

     }

     /**
      * 
      */
     public HashSet < Pixel > getAllCluster() {
         return allClusterAroundNode;
     }

     /**
      *  
      */
     public void resetAllCluster() {
         allClusterAroundNode.clear();
     }

     /**
      *  
      */
     public void resetToNewAdjacents() {
         adjacents = new HashSet < Node > ();
     }

     /**
      *  
      */
     public HashSet < Node > getAdjacents() {
         return adjacents;
     }

     /**
      *  
      */
     class Visualizer implements IColorScheme {

         ImageResource visIr;
         RoundIteratorOfPixels riop;

         Visualizer() {

             visIr = new ImageResource(ir.getWidth(), ir.getHeight());
             riop = new RoundIteratorOfPixels();
             riop.setImageResource(visIr);

         }

         /**
          *  
          */
         public void displayInitial() {
             for (Pixel p: visIr.pixels()) {
                 p.setRed(0);
                 p.setGreen(0);
                 p.setBlue(0);
             }

             for (Pixel p: allClusterAroundNode) {

                 Pixel pVis = visIr.getPixel(p.getX(), p.getY());

                 riop.resetCount();
                 riop.setPixelToCheckAround(pVis);

                 for (Pixel pHighlight: riop) {

                     pHighlight.setRed(whiteScheme[0]);
                     pHighlight.setGreen(whiteScheme[1]);
                     pHighlight.setBlue(whiteScheme[2]);

                 }

             }
             //visIr.draw();
         }

         /**
          *  
          */
         public void displayProgres() {

             for (Pixel p: allClusterAroundNode) {

                 Pixel pVis = visIr.getPixel(p.getX(), p.getY());

                 pVis.setRed(blueischScheme[0]);
                 pVis.setGreen(blueischScheme[1]);
                 pVis.setBlue(blueischScheme[2]);

             }
             //visIr.draw();
         }
     }
 }