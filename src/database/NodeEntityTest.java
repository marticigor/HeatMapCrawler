package database;

import javax.persistence.Entity;

@Entity
public class NodeEntityTest {

	/*
	CREATE TABLE `test` (`id` int(11) NOT NULL AUTO_INCREMENT,
			`name` varchar(255) DEFAULT NULL,
			`serial` bigint(10) UNSIGNED ZEROFILL DEFAULT NULL,
			PRIMARY KEY (`id`)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    */
	
	public NodeEntityTest(){}
	
	public NodeEntityTest(String n, long s ){
		this.setName(n);
		this.setSerial(s);
	}
	
	public long getSerial() {
		return serial;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;
	private String name;
	private long serial;
	
}
