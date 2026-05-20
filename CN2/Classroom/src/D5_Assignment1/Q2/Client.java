import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Client {
	private int sequenceNumber = 0;

	// Datagram handling and Server communication functions
	private byte[] input2datagram(int dataType, String input) {
		Packet packetGen = new Packet(sequenceNumber, dataType, input);
		return packetGen.construct();
	}
	private void send(String host, int port, byte[] datagram) throws Exception {
		DatagramSocket cl = new DatagramSocket();
		InetAddress addsv = InetAddress.getByName(host);
		DatagramPacket packet = new DatagramPacket(datagram, datagram.length, addsv, port);
		cl.send(packet);
		cl.close();
		sequenceNumber++;
	}
	// Display functions
	private String dataType2action(int dataType) {
		switch (dataType) {
			case 1: return "Room temperature";
			case 2: return "Humidity";
			case 3: return "Door status";
			case 4: return "Exit";
		}
		return "Invalid data type";
	}
	private void displayMenu() {
		System.out.println("Select an action:");
		for (int i = 1; i <= 4; i++) {
			System.out.println(i + ". " + dataType2action(i));
		}
	}

	public static void main(String[] args) throws Exception {
		int dataType = -1;
		while (dataType != 4) {
			Client client = new Client();
			Scanner keyboard = new Scanner(System.in);
			client.displayMenu();
			System.out.print("> ");
			dataType = keyboard.nextInt();
			if (dataType < 1 || dataType > 4) {
				System.out.println("Invalid choice. Please try again.");
				continue;
			}
			String input = "";
			if (dataType != 4) {
				System.out.print("Enter " + client.dataType2action(dataType) + ": ");
				input = keyboard.next();
			}
			byte[] datagram = client.input2datagram(dataType, input);
			client.send("localhost", 14393, datagram);
		}
	}
}
