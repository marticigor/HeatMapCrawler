package core;

import java.util.*;

import lib_duke.Pixel;
public class Node implements Comparable < Node > {

    private final int x;
    private final int y;
    private double lon;
    private double lat;
    private final long id;
    private final long shotId;
    private double distToCenter = Double.MAX_VALUE;
    private boolean isBottleneck = false;
    private HashSet < Node > adjacentNodes;
    private ArrayList < Pixel > mask; //these are copies of Pixels from noded;
    private static final double EPSILON = 0.0000001;

    // finaly I will want this graph format
    // https://www.dropbox.com/s/8et183ufeskkibi/IMG_20171019_194557.jpg?dl=0
    public Node(int x, int y, double lon, double lat,  long id, long shotId) {

        this.x = x;
        this.y = y;
        this.lon = lon;
        this.lat = lat;
        this.distToCenter = 0;
        adjacentNodes = new HashSet < Node > ();
        mask = new ArrayList < Pixel > ();
        this.id = id;
        this.shotId = shotId;

    }

    /** 
     * Sorted by Y axis vertical
     */
    public int compareTo(Node another) {

        //if (this.getY() < another.getY()) return -1;
        //if (this.getY() > another.getY()) return 1;
        //return 0;
        return ((Integer)(this.getY())).compareTo(((Integer)(another.getY())));
    }
    
    /**
     * 
     * @param theOther
     * @return
     */
    public boolean equalsLonLat(Node theOther){
    	
    	boolean lonB = (Math.abs(this.lon - theOther.getLon()) < EPSILON);
    	boolean latB = (Math.abs(this.lat - theOther.getLat()) < EPSILON);
    	
    	return lonB && latB;
    	
    }
    
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public long getId(){
    	return id;
    }
    public long getShotId(){
    	return shotId;
    }
	public double getLon() {
		return lon;
	}
	public double getLat() {
		return lat;
	}
    public boolean getBottleneck() {
        return isBottleneck;
    }
    public void setBottleneck(boolean b) {
        isBottleneck = b;
    }
    public double getDstToCenter() {
        return distToCenter;
    }
    public void setDstToCenter(double d) {
        distToCenter = d;
    }
    public int hashCode() {
        return new Long(id).hashCode();
    }
    public String toString() {
        return System.identityHashCode(this) + "| ID " + id + " | X = " + x +
        		" | Y = " + y + "----- |LON " + lon + " |LAT " + lat;
    }

    public void addAdjacentNode(Node n) {
        adjacentNodes.add(n);
    }

    public HashSet < Node > getAdjacentNodes() {
        return adjacentNodes;
    }

    public void addPixelToMask(Pixel pKey) {
        mask.add(pKey);
    }

    public ArrayList < Pixel > getMask() {
        return mask;
    }

}