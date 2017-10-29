package core;

import java.util.*;

import core.image_filters.JustCopy;
import ifaces.I_ColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class NodeFinder implements I_ColorScheme {

    private ImageResource thresholded;
    private ImageResource skeletonized;
    private ArrayList < Node > nodes = new ArrayList < Node > ();
    private int width;
    private int height;
    private ImageResource noded;
    private int nmbOfNodes = 0;
    
    private int thresholded_lookAheadAndBack = 2;
    private int thresholded_surfaceConstantInterval1_MinExcl = 0;
    private int thresholded_surfaceConstantInterval1_MaxIncl = 1000;
    private int thresholded_surfaceConstantInterval2_MinExcl = 1000;
    private int thresholded_surfaceConstantInterval2_MaxIncl = 1000;
	private int thresholded_routableNeighbourghsConstant = 1;
    
    private int lookAheadAndBack;
    private int surfaceConstantInterval1_MinExcl;
    private int surfaceConstantInterval1_MaxIncl;
    private int surfaceConstantInterval2_MinExcl;
    private int surfaceConstantInterval2_MaxIncl;
	private int routableNeighbourghsConstant = 1;
	
	private int surfaceConstant1_1;
	private int surfaceConstant1_2;
	private int surfaceConstant2_1;
	private int surfaceConstant2_2;
	private int neighbourghsConstant;
    
	private final long shotId;

    private RoundIteratorOfPixels iteratorRound = new RoundIteratorOfPixels();
    private RecursiveClusterFinder rcf;

    //represents white pixels clustered around future node
    private HashSet < Pixel > allClusterAroundNode = new HashSet < Pixel > ();
    private final Runner myHandler;
    private double [] bounds;
    private boolean debug;
    private boolean visual;

    private static double spanLon = -1.0;
    private static double spanLat = -1.0;

    /**
     *
     */
    public NodeFinder(
    		ImageResource thresholded,
    		ImageResource skeletonized,
    		int look,
    		int surface1,
    		int surface2,
    		int surface3,
    		int surface4,
    		Runner myHandler,
    		double [] bounds,
    		long shotId,
    		boolean debug,
    		boolean visual) {

        this.lookAheadAndBack = look;
        this.surfaceConstantInterval1_MinExcl = surface1;
        this.surfaceConstantInterval1_MaxIncl = surface2;
        this.surfaceConstantInterval2_MinExcl = surface3;
        this.surfaceConstantInterval2_MaxIncl = surface4;
        this.skeletonized = skeletonized;
        this.thresholded = thresholded;
        width = skeletonized.getWidth();
        height = skeletonized.getHeight();
        noded = new ImageResource(width, height);
        this.myHandler = myHandler;
        this.bounds = bounds;
        this.shotId = shotId;
        this.debug = debug;
        this.visual = visual;

        //0 lon east
        //1 lat north
        //2 lat south
        //3 lon west
        //4 lat center
        //5 lon center

        //TODO code duplicity
        double spanLonNow = bounds[0] - bounds [3];
        if(spanLon == -1.0) spanLon = spanLonNow;
        else if(spanLon != spanLonNow){
        	System.err.println("------------------------------span discrepancy Lon");
        	System.err.println("spanLon " + spanLon + " now " + spanLonNow);
        	System.err.println("dif " + (spanLon - spanLonNow));
        	spanLon = spanLonNow;
        }
        double spanLatNow = bounds[1] - bounds [2];
        if(spanLat == -1.0) spanLat = spanLatNow;
        else if(spanLat != spanLatNow){
        	System.err.println("------------------------------span discrepancy Lat");
        	System.err.println("spanLat " + spanLat + " now " + spanLatNow);
        	System.err.println("dif " + (spanLat - spanLatNow));
        	spanLat = spanLatNow;
        }

        if(bounds.length != 6) throw new RuntimeException("Node finder bounds length.");
      
        JustCopy copy = new JustCopy(skeletonized, noded,
        		myHandler.getBorderInSharpenStage(),
        		true, debug, -1,-1,-1,-1, myHandler.getBorderInSharpenStage());
        copy.doYourThing();

        if(debug){
            System.out.println("NodeFinder");
            System.out.println("______________________________");
            for (double d : this.bounds) System.out.println(d);
            System.out.println("______________________________");
        }
    }

    /**
     *
     */
    public void findNodes() {

	    surfaceConstant1_1 = thresholded_surfaceConstantInterval1_MinExcl;
	    surfaceConstant1_2 = thresholded_surfaceConstantInterval1_MaxIncl;
	    surfaceConstant2_1 = thresholded_surfaceConstantInterval2_MinExcl; 
	    surfaceConstant2_2 = thresholded_surfaceConstantInterval2_MaxIncl;
	    neighbourghsConstant = thresholded_routableNeighbourghsConstant;
    	
    	detectSalientAreas(thresholded, thresholded_lookAheadAndBack, false);
	    
	    surfaceConstant1_1 = surfaceConstantInterval1_MinExcl;
	    surfaceConstant1_2 = surfaceConstantInterval1_MaxIncl;
	    surfaceConstant2_1 = surfaceConstantInterval2_MinExcl; 
	    surfaceConstant2_2 = surfaceConstantInterval2_MaxIncl;
	    neighbourghsConstant = routableNeighbourghsConstant;
	    
	    detectSalientAreas(noded, lookAheadAndBack, false);
	    
        if(visual) {
        	noded.draw();
        	thresholded.draw();
        	Pause.pause(5000);
            //SALIENT SALIENT
        	//System.out.println("DRAWING SALIENT AREA");
            //salientArea.draw();
        }
        

        //
        rcf = new RecursiveClusterFinder(noded, whiteScheme[0], whiteScheme[1], whiteScheme[2]);
        //

        int x, y;
        double lon, lat;

        double dLon = Math.abs(bounds [0] - bounds [3]);
        double dLat = Math.abs(bounds [1] - bounds [2]);

        if(debug){
        	System.out.println("dLon " + dLon);
        	System.out.println("dLat " + dLat);
        }

        for (Pixel pOfNoded: noded.pixels()) { //1

            if (isSetToClusterAround(pOfNoded)) { //2 white check only

                allClusterAroundNode.clear();
                rcf.resetAllCluster();

                //now define recursively a node object as a small bitmap

                setClustered(pOfNoded); //as this emerges from recursion we have filled allClusterAroundNode

                if (nmbOfNodes % 200 == 0) printAllClustered(allClusterAroundNode); //size of print REDUCED

                int neighboursToFindMax;
                int maxNeighbours = 0;

                int sumX = 0;
                int sumY = 0;
                int centerGravityX = 0;
                int centerGravityY = 0;

                ArrayList < Node > maximusNodes = new ArrayList < Node > ();

                for (Pixel pChecked: allClusterAroundNode) { //find int max

                    neighboursToFindMax = getNumberOfSurrWhites(pChecked, noded);

                    if (neighboursToFindMax > maxNeighbours) maxNeighbours = neighboursToFindMax;

                }

                for (Pixel maybeMaximus: allClusterAroundNode) {

                    if (getNumberOfSurrWhites(maybeMaximus, noded) == maxNeighbours) {

                    	//bounds

                        //0 lon east
                        //1 lat north
                        //2 lat south
                        //3 lon west
                        //4 lat center
                        //5 lon center

                    	x = maybeMaximus.getX();
                    	y = maybeMaximus.getY();

                    	lon = bounds [3] + (   ( (double) x / ((double) (width - 1)) ) * dLon   );
                    	lat = bounds [1] - (   ( (double) y / ((double) (height - 1)) ) * dLat   );

                    	//the only instantiation of node in the project
                        Node node = new Node(
                        		x,
                        		y,
                        		lon,
                        		lat,
                        		myHandler.incrAndGetId(),
                        		shotId
                        		);

                        maximusNodes.add(node);

                        //find average X Y for this cluster of maximuses
                        sumX += node.getX();
                        sumY += node.getY();

                    }
                }

                out: {
                    @SuppressWarnings("unused")
                    int closestXsoFar = Integer.MAX_VALUE;
                    @SuppressWarnings("unused")
                    int closestYsoFar = Integer.MAX_VALUE;
                    @SuppressWarnings("unused")
                    int xToOut = 0;
                    @SuppressWarnings("unused")
                    int yToOut = 0;

                    centerGravityX = sumX / maximusNodes.size();
                    centerGravityY = sumY / maximusNodes.size();

                    double minDist = Double.MAX_VALUE;

                    //select one of maximusNodes and add it to nodes;

                    for (Node maybeClosest: maximusNodes) {

                        //we do have (among maximusNodes) a node with exactly avg x y values
                        if (maybeClosest.getX() == centerGravityX && maybeClosest.getY() == centerGravityY) {
                            nodes.add(maybeClosest);
                            nmbOfNodes++;
                            if(debug) System.out.println(maybeClosest.toString());
                            break out;
                        }

                        //calculate dist to center of gravity and set it to node POJO

                        int deltaX = Math.abs(maybeClosest.getX() - centerGravityX);
                        int deltaY = Math.abs(maybeClosest.getY() - centerGravityY);
                        double dist = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

                        maybeClosest.setDstToCenter(dist);
                        if (dist < minDist) minDist = dist;

                    }

                    for (Node possiblyClosestNode: maximusNodes) {

                        if (possiblyClosestNode.getDstToCenter() == minDist) {
                            nodes.add(possiblyClosestNode);
                            xToOut = possiblyClosestNode.getX();
                            yToOut = possiblyClosestNode.getY();
                            if(debug) System.out.println(possiblyClosestNode.toString());
                            break;
                        }
                    }

                    nmbOfNodes++;
                } //out

                for (Pixel toRed: allClusterAroundNode) {
                    setRed(toRed);
                }
            } //2
        } //1

        //now set pixels white
        Pixel px;
        for (Node n: nodes) {
            px = noded.getPixel(n.getX(), n.getY());
            setWhite(px);
        }
    }

    /**
     * 
     * @param image
     * @param lookBothDir
     */
    private void detectSalientAreas(ImageResource image,int lookBothDir, boolean testAgainstThresholded){
    	
        Pixel p = null;
        Pixel pIn = null;
        
        //SALIENT SALIENT
        //ImageResource salientArea = new ImageResource(sharpened.getWidth(),sharpened.getHeight());
        
        int surfaceArea = 0;
        int routableNeighbours = 0;

        iteratorRound.setImageResource(skeletonized);
        iteratorRound.resetCount();
        int minSurface = Integer.MAX_VALUE;
        int maxSurface = Integer.MIN_VALUE;
        //MapNmbToColor<Integer> mapColor =  new MapNmbToColor<Integer>();
        //
        //finds salient pixels that probably belong to a region of
        //interest in terms of future node.
        //
        for (int x = lookBothDir + 1; x < width - (lookBothDir + 1); x++) {
            for (int y = lookBothDir + 1; y < height - (lookBothDir + 1); y++) {

            	if(testAgainstThresholded && !isSalientPixInThresholded(x,y)) continue;
            	
                p = image.getPixel(x, y);

                if (p.getRed() == redScheme[0] && p.getGreen() == redScheme[1] && p.getBlue() == redScheme[2]) {

                //System.out.println("RED PIXEL------------------------------------");
                //System.out.println("X = " + x + " Y = " + y);
                //System.out.println("ITERATING THIS RED PIXEL---------------------");

                    for (int xIn = x - lookBothDir; xIn < x + lookBothDir + 1; xIn++) {
                        for (int yIn = y - lookBothDir; yIn < y + lookBothDir + 1; yIn++) {

                        	//System.out.println("xIn = " + xIn + " yIn = "+yIn);

                            pIn = image.getPixel(xIn, yIn);

                            if (pIn.getRed() == redScheme[0] && pIn.getGreen() == redScheme[1] && pIn.getBlue() == redScheme[2]) {

                            	routableNeighbours ++;
                                iteratorRound.setPixelToCheckAround(pIn);
                                iteratorRound.resetCount();

                                for (Pixel p1: iteratorRound) {
                                    if (p1.getRed() != redScheme[0] ||
                                    		p1.getGreen() != redScheme[1] ||
                                    		p1.getBlue() != redScheme[2]) surfaceArea ++;
                                }
                            }
                        }
                    }
                    
                    //SALIENT SALIENT
                    //Pixel inSalient = salientArea.getPixel(x, y);
                    //RGB color = mapColor.getRGB(surfaceArea * 10);
                    //inSalient.setRed(color.red);
                    //inSalient.setGreen(color.green);
                    //inSalient.setBlue(color.blue);
                    
                    if ( evaluateAgainstConstants(surfaceArea, routableNeighbours)){
                    	
                        Pixel pToWhite = image.getPixel(x, y);
                        setWhite(pToWhite);
                        
                    }
                    
                    if(surfaceArea > maxSurface) maxSurface = surfaceArea;
                    if(surfaceArea < minSurface) minSurface = surfaceArea;
                    
                    surfaceArea = 0;
                    routableNeighbours = 0;
                }
            }
        }
        if(visual || debug) System.out.println("Surface min max " + minSurface + " " + maxSurface);
    }
    
    /**
     * 
     */
    private boolean evaluateAgainstConstants(int surface, int routableNeighbours ){
    	
        boolean firstInterval = (surface > surfaceConstant1_1 &&
                  surface <= surfaceConstant1_2);

        boolean secondInterval = (surface > surfaceConstant2_1 &&
                  surface <= surfaceConstant2_2);
        
        boolean neighbours = routableNeighbours > neighbourghsConstant ;
    	
    	return (firstInterval || secondInterval) && neighbours;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean isSalientPixInThresholded(int x, int y){
    	Pixel salientInTh = thresholded.getPixel(x, y);
    	if(isSetToClusterAround(salientInTh)) return true; else return false;
    }
    
    /**
     * loads recursively set of white pixels into neighbours and then allClusterAroundNode
     */
    private void setClustered(Pixel p) {

        rcf.setCluster(p);
        this.allClusterAroundNode = rcf.getAllCluster();

    }

    /**
     * checks if p is set to white scheme
     */
    private boolean isSetToClusterAround(Pixel p) {

        if (p.getRed() == whiteScheme[0] && p.getGreen() == whiteScheme[1] && p.getBlue() == whiteScheme[2]) return true;
        return false;
    }

    /**
     *
     */
    private int getNumberOfSurrWhites(Pixel p, ImageResource ir) {

        int neighbours = 0;

        iteratorRound.resetCount();
        iteratorRound.setImageResource(ir);
        iteratorRound.setPixelToCheckAround(p);

        for (Pixel myPx: iteratorRound) {

            if (myPx.getRed() == whiteScheme[0] && myPx.getGreen() == whiteScheme[1] && myPx.getBlue() == whiteScheme[2]) neighbours++;

        }

        return neighbours;
    }
    
    /**
     * prints simplistic visualisation of clusters
     */
    private void printAllClustered(HashSet < Pixel > cluster) {

        System.out.println("--------------------------");

        boolean[][] clusterA = new boolean[(lookAheadAndBack * 2 + 1)][(lookAheadAndBack * 2 + 1)];

        StringBuilder sb = new StringBuilder();

        int minX = 100000;
        int minY = 100000;

        for (Pixel p: cluster) {

            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();

        }

        int xToVis = 0;
        int yToVis = 0;

        for (Pixel p: cluster) {

            xToVis = p.getX() - minX;
            xToVis = xToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : xToVis;
            yToVis = p.getY() - minY;
            yToVis = yToVis >= (lookAheadAndBack * 2 + 1) ? (lookAheadAndBack * 2) : yToVis;

            clusterA[xToVis][yToVis] = true;

        }

        for (int x = 0; x < clusterA.length; x++) {
            for (int y = 0; y < clusterA.length; y++) {

                if (clusterA[x][y]) sb.append("X");
                else sb.append("_");

            }
            System.out.println(sb.toString());
            sb = new StringBuilder();
        }
    }

    public ImageResource getNodedImage() {
        return noded;
    }
    public ArrayList < Node > getNodes() {
        return nodes;
    }
    private void setRed(Pixel p) {
        p.setRed(redScheme[0]);
        p.setGreen(redScheme[1]);
        p.setBlue(redScheme[2]);
    }
    private void setWhite(Pixel p) {
        p.setRed(whiteScheme[0]);
        p.setGreen(whiteScheme[1]);
        p.setBlue(whiteScheme[2]);
    }
    @SuppressWarnings("unused")
    private void setYellow(Pixel p) {
        p.setRed(yellowScheme[0]);
        p.setGreen(yellowScheme[1]);
        p.setBlue(yellowScheme[2]);
    }
}
