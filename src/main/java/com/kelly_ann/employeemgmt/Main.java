package com.kelly_ann.employeemgmt;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.kelly_ann.employeemgmt.service.EmployeeMgmtService;
import com.kelly_ann.employeemgmt.domain.Employee;
import com.kelly_ann.employeemgmt.exception.ServiceUnavailableException;


public class Main {
	
	// this remotely invokes the server's EmployeeMgmtService.getAllEmployees() method.
	public static void main(String[] args) {
		
		// To remotely invoke the EmployeeMgmtService.getAllEmployees() method:
		// 1.  create properties obj to hold key/value pairs in it's HashTable
		// 2.  add connection context info
		// 3.  create JNDI(Java Naming and Directory Interface) context
		// 4.  Find external service JNDI "friendly name" via:
		//	localhost:9990 -> Runtime -> Subsystems -> JNDI View -> View -> "java:jboss/exported" -> appName -> click className
		//  From "Selected URI" above copy/use "friendly!name" from "java:jboss/exported/friendly!name"
		// 5.  Create service obj using JNDI context obj's lookup("friendly!name") method.
		
		try {
			Properties jndiProperties = new Properties();
			jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
			jndiProperties.put("jboss.naming.client.ejb.context", true);
			Context jndi = new InitialContext(jndiProperties);
			
			// lookup() format is: jarName/implClassName!interfaceFullyQualifiedClassName
			EmployeeMgmtService service = (EmployeeMgmtService)jndi.lookup("emsa/EmployeeMgmtImpl!com.kelly_ann.employeemgmt.service.EmployeeMgmtService");
			
			// add new employees
			Employee e1 = new Employee("Hillary", "Clinton", "Politician", 500);
			Employee e2 = new Employee("Donald", "Trump", "Businessman", 150);
			Employee e3 = new Employee("Kelly", "Ann", "Software Engineer", 88);
			try {
				service.registerEmployee(e1);
				service.registerEmployee(e2);
				service.registerEmployee(e3);
			} catch (ServiceUnavailableException e) {
				System.out.println("ServiceUnavailableException thrown: ........ Something went wrong: ........");
				//e.printStackTrace();
			}
			
			
			// search for employee(s)
			List<Employee> employees = service.searchByLastName("Ann");
			for(Employee employee : employees) {
				System.out.println(employee);
			}
			
			// call the method that doesn't use any of the Java EE transactional api
			System.out.println("\nThis call to dummyInMemoryNoTransactionNeededMethod() with return of '" 
					+ service.dummyInMemoryNoTransactionNeededMethod() + "' did not use Java EE transactions." );
			
		}
		catch (NamingException ne) {
			ne.printStackTrace();
		}
	}
	
}
