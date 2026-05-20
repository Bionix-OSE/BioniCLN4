
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class UDPServer {
	public static void main(String[] args) {
		// Define socket and buffer
		DatagramSocket serverSocket = null;
		byte[] receiveData = new byte[1024];
		try {
			serverSocket = new DatagramSocket(9876);
			while (true) {
				// Receive packet
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				// Get the sequence number (first 4 bytes of the packet)
				byte[] packetData = receivePacket.getData();
				int seq = ByteBuffer.wrap(packetData, 0, 4).getInt(); // Extract sequence number
				// Get the data type (the 5th byte)
				byte dataType = packetData[4]; // Data type: 0 = String, 1 = Integer
				// Extract the data from the packet (after the sequence number and data type)
				byte[] data = new byte[receivePacket.getLength() - 5]; // Length of data
				System.arraycopy(packetData, 5, data, 0, data.length); // only copy data
				// Process based on data type
				if (dataType == 0) {
					// Data is a String
					String receivedMessage = new String(data);
					System.out.println("Received sequence number: " + seq);
					System.out.println("Received message (String): " + receivedMessage);
				} else if (dataType == 1) {
					// Data is an Integer
					int receivedInt = Integer.parseInt(new String(data));
					System.out.println("Received sequence number: " + seq);
					System.out.println("Received message (Integer): " + receivedInt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}
}
