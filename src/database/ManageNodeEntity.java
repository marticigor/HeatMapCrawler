package database;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageNodeEntity {

	private SessionFactory factory;
	
	//https://www.tutorialspoint.com/hibernate/hibernate_examples.htm
	public void store(List <NodeEntityTest> nodes){
		
	      try {
	          factory = new Configuration().configure().buildSessionFactory();
	       } catch (Throwable ex) { 
	          System.err.println("Failed to create sessionFactory object." + ex);
	          throw new ExceptionInInitializerError(ex); 
	       }
		
	      Session session = factory.openSession();
	      Transaction tx = null;
	      
	      try {
	          tx = session.beginTransaction();
	          
	          for(NodeEntityTest n : nodes) session.persist(n);
	          
	          tx.commit();
	          
	       } catch (HibernateException e) {
	          if (tx!=null) tx.rollback();
	          e.printStackTrace(); 
	       } finally {
	          session.close(); 
	       }
	}
}
