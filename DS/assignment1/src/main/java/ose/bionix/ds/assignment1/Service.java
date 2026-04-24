package ose.bionix.ds.assignment1;

import java.rmi.Remote;
import java.rmi.RemoteException; 
import java.time.ZonedDateTime;

public interface Service extends Remote {
	String getTime() throws RemoteException;
	int countString(String msg) throws RemoteException;
	ZonedDateTime getZonedDateTime(String time) throws RemoteException;
}
