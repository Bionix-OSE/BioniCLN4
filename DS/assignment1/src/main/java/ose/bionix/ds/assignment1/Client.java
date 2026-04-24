package ose.bionix.ds.assignment1;

import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.time.ZonedDateTime; 
import ose.bionix.ds.assignment1.Service;

public class Client {
	private Client() {}
	
	public static void main(String[] args) {
		try {
			// Get the registry
			Registry registry = LocateRegistry.getRegistry(null);
			// Look up the remote object
			Service stub = (Service) registry.lookup("Service");
			// Call the remote methods
			String response1 = stub.getTime();
			System.out.println(response1);
			
			int response2 = stub.countString("Hello, World!");
			System.out.println(String.format("Message length: %d", response2));
			
			String zone = "America/New_York";
			ZonedDateTime response3 = stub.getZonedDateTime(zone);
			System.out.println(String.format("Current time of %s is: %s", zone, response3));
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
