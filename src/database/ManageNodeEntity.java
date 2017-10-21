package database;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class ManageNodeEntity {

	private ManageNodeEntity() {
	}

	private static ManageNodeEntity myInstance = new ManageNodeEntity();

	public static ManageNodeEntity getInstance() {
		return myInstance;
	}

	private SessionFactory sf = null;
	private Session session = null;

	// map one to many
	// https://stackoverflow.com/questions/2441598/detached-entity-passed-to-persist-error-with-jpa-ejb-code

	// https://www.tutorialspoint.com/hibernate/hibernate_examples.htm
	
	// batch processing: (should not at all be needed here)
	// https://www.tutorialspoint.com/hibernate/hibernate_batch_processing.htm
	
	public void persist(List<NodeEntity> nodesPojos) {

		//hibernate-commons-annotations.jar
		//ejb3-persistence.jar
		//hibernate-annotations.jar
		
		if ( sf == null) {
			
			/*
			try {
				//factory = new AnnotationConfiguration().configure()
						//.buildSessionFactory();
				factory = new Configuration().configure().buildSessionFactory();
			} catch (Throwable ex) {
				System.err.println("Failed to create sessionFactory object." + ex);
				throw new ExceptionInInitializerError(ex);
			}
			*/

	        Configuration cf = new Configuration().configure("hibernate.cfg.xml");
	        cf.addAnnotatedClass(database.NodeEntity.class);

	        StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder();
	        srb.applySettings(cf.getProperties());
	        ServiceRegistry sr = srb.build();
	        sf = cf.buildSessionFactory(sr);

		}

		System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		session = sf.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			System.err.println(nodesPojos.size() + "\npersisting");
			for (NodeEntity n : nodesPojos){
				System.out.println(n);
			    session.persist(n);
			}
			System.err.println("commiting");
			tx.commit();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
			System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
	}
}
