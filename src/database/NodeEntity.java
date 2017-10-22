package database;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "test_node_entity")
public class NodeEntity {

    // finaly I will want this graph format
    // https://www.dropbox.com/s/8et183ufeskkibi/IMG_20171019_194557.jpg?dl=0
	
	// map one to many same entity
	// https://stackoverflow.com/questions/3393515/jpa-how-to-have-one-to-many-relation-of-the-same-entity-type

	// CREATE TABLE `test_node_entity` (`id` bigint(10) UNSIGNED NOT NULL auto_increment, `parent_id` bigint(10) UNSIGNED, `shotId` bigint(10) UNSIGNED NOT NULL,`lon` double (22,18) NOT NULL,`lat` double (22,18) NOT NULL, PRIMARY KEY (`id`)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
	private long id;

	@Column(name = "shotId")
	private long shotId;
    @Column(name = "lon")
	private double lon;
    @Column(name = "lat")
	private double lat;

    @ManyToOne
    private NodeEntity parent;
    
    @OneToMany(mappedBy="parent")
	private Set<NodeEntity> adjacents;
	
    private static final transient double EPSILON = 0.0000001;
	
	public NodeEntity (){}
	
	public NodeEntity(long shotId, double lon, double lat, Set <NodeEntity> adjacents ){
		
		this.shotId = shotId;
		this.lon = lon;
		this.lat = lat;
		this.adjacents = adjacents;
		
	}
	
	//--------------------------------------
	public void addToAdj(NodeEntity adj){
		adjacents.add(adj);
	}
	//--------------------------------------
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getShotId() {
		return shotId;
	}
	public void setShotId(long shotId) {
		this.shotId = shotId;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double l){
		this.lon = l;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double l){
		this.lat = l;
	}
	public Set<NodeEntity> getAdjacents() {
		return adjacents;
	}
    public void setAdjacents(Set<NodeEntity> adj){
    	this.adjacents = adj;
    }
    public NodeEntity getParent() {
		return parent;
	}
	public void setParent(NodeEntity parent) {
		this.parent = parent;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(lon, lat);
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
	    
	    NodeEntity theOtherNe = (NodeEntity) theOther;
	    
	    return (this.lon == theOtherNe.getLon() && this.lat == theOtherNe.getLat());
	}
	
    /**
     * 
     * @param theOther
     * @return
     */
    public boolean equalsLonLat(NodeEntity theOther){
    	
    	boolean lonB = (Math.abs(this.lon - theOther.getLon()) < EPSILON);
    	boolean latB = (Math.abs(this.lat - theOther.getLat()) < EPSILON);
    	
    	return lonB && latB;
    	
    }
	
	@Override
	public String toString(){
		String value = "|id " + id + " |shotId " + shotId +  " |lon " + lon + " |lat " + lat + "\n";
		value += "adjacents:\n"+ adjacents.size() + "\n";
	    for(NodeEntity n : adjacents){
	    	if(n == this){
	    		System.err.println("reference to this in adjacents in NodeEntity.toString()");
	    		continue;
	    	}
	    	value += ("---------- some NodeEntity - stackOverflowError we do not want here\n");
	    }

		return value;
	}
}
