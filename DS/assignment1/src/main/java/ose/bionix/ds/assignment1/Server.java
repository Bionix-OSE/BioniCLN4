package ose.bionix.ds.assignment1;

import java.rmi.registry.Registry; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.Remote;
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject; 
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.time.Duration;
import java.time.Instant;
import ose.bionix.ds.assignment1.Service;

public class Server implements Service {
	public Server() {};

	public String getTime() throws RemoteException {
		Instant start = ProcessHandle.current().info().startInstant().orElse(Instant.now());
		Duration uptime = Duration.between(start, Instant.now());
		// Format uptime as HH:mm:ss
		return String.format("%02d:%02d:%02d", uptime.toHoursPart(), uptime.toMinutesPart(), uptime.toSecondsPart());
	}
	public int countString(String msg) throws RemoteException {
		return msg.length();
	}
	public ZonedDateTime getZonedDateTime(String time) throws RemoteException {
		return ZonedDateTime.now(TimeZone.getTimeZone(time).toZoneId());
	}

	public static void main(String[] args) {
		try {
			// Create and export a remote object
			Server server = new Server();
			// Export the object to the RMI runtime
			Service stub = (Service) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.getRegistry();
			// Bind the remote object's stub in the registry
			registry.bind("Service", stub);
			System.out.println("Server is ready.");
			Thread.sleep(Long.MAX_VALUE);
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
