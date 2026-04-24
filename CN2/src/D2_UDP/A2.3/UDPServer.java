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
			int count = 0;
			int int1 = 0, int2 = 0;
			while (true) {
				// Receive packet
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				count++;
				// Get the sequence number (first 4 bytes of the packet)
				byte[] packetData = receivePacket.getData();
				int seq = ByteBuffer.wrap(packetData, 0, 4).getInt(); // Extract sequence number
				// Extract the data from the packet (after the sequence number and data type)
				byte[] data = new byte[receivePacket.getLength() - 5]; // Length of data
				System.arraycopy(packetData, 5, data, 0, data.length); // only copy data
				// Process based on data type
				if (seq == 1) {
					int1 = Integer.parseInt(new String(data));
				} else if (seq == 2) {
					int2 = Integer.parseInt(new String(data));
				}
				if (count == 2) {
					break;
				}
			}
			int diff = int1 - int2;
			DatagramPacket sendPacket = new DatagramPacket(receiveData, receiveData.length);
			byte buff[] = new byte[256];
			buff = String.valueOf(diff).getBytes();
			InetAddress addcl = sendPacket.getAddress();
			int portcl = sendPacket.getPort();
			DatagramPacket k = new DatagramPacket(buff,buff.length,addcl,portcl);
			serverSocket.send(k);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}
}
