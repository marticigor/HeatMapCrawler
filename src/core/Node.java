package core;

import java.util.*;

import database.NodeEntity;
import lib_duke.Pixel;
public class Node implements Comparable < Node > {

    private final int x;
    private final int y;
    private double lon;
    private double lat;
    //
    private final long idHash;
    private final long shotId;
    //
    private double distToCenter = Double.MAX_VALUE;
    private boolean isBottleneck = false;
    private Set < Node > adjacentNodes;
    private ArrayList < Pixel > mask; //these are copies of Pixels from noded ImageResource;
    private NodeEntity entity = null;

    // finaly I will want this graph format
    // https://www.dropbox.com/s/8et183ufeskkibi/IMG_20171019_194557.jpg?dl=0
    
    // https://stackoverflow.com/questions/35958335/jpa-onetomany-on-same-entity
    /**
     * 
     * @param x
     * @param y
     * @param lon
     * @param lat
     * @param id
     * @param shotId
     */
    public Node(int x, int y, double lon, double lat,  long id, long shotId) {

        this.x = x;
        this.y = y;
        this.lon = lon;
        this.lat = lat;
        this.distToCenter = 0;
        adjacentNodes = new HashSet < Node > ();
        mask = new ArrayList < Pixel > ();
        this.idHash = id;
        this.shotId = shotId;
        
        this.entity = new NodeEntity(shotId, lon, lat, new HashSet<NodeEntity>());

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
     * @return NodeEntity pojo
     */
    public NodeEntity getEntity (){
    	
    	if(entity == null || entity.getAdjacents() == null){
            throw new RuntimeException("Entity not fully constructed");
    	}
    	return entity;
    }
    
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public long getIdHash(){
    	return idHash;
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
    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
    //OBJECT!!!
    @Override
    public boolean equals(Object theOther){
        // self check
        if (this == theOther)
            return true;
        // null check
        if (theOther == null)
            return false;
        // type check
        if (getClass() != theOther.getClass())
            return false;
        Node n = (Node) theOther;
        if(x == n.getX() && y == n.getY()) return true; else return false;
    	
    }
    @Override
    public String toString() {
        return hashCode() + "| ID " + idHash + " | X = " + x +
        		" | Y = " + y + "----- |LON " + lon + " |LAT " + lat;
    }

    public void addAdjacentNode(Node n) {
        adjacentNodes.add(n);
        entity.addToAdj(n.getEntity());
        //.getEntity().setParent(entity);
    }

    //TODO debug and remove
    public void removeAdjacentNode(Node n){
    	adjacentNodes.remove(n);
    	entity.removeFromAdj(n.getEntity());
    }
    
    public Set < Node > getAdjacentNodes() {
        return adjacentNodes;
    }

    public void addPixelToMask(Pixel pKey) {
        mask.add(pKey);
    }

    public ArrayList < Pixel > getMask() {
        return mask;
    }

}