package core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ifaces.spring_beans.I_KickStart;

public class App {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("aplicationContext.xml");
		I_KickStart start = context.getBean("kickStart", I_KickStart.class);
		start.kickStart();
		context.close();

	}
}
