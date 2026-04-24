import java.net.*;
import java.io.*;
import java.util.Scanner;

public class UDPClient {
	public static void main(String[] args) {
		try{
			DatagramSocket cl = new DatagramSocket();

			Scanner keyboard = new Scanner(System.in);
			System.out.println("Please input a string:");
			String st= keyboard.nextLine();
			byte buff[] = st.getBytes();
			InetAddress addsv = InetAddress.getByName("localhost");
			DatagramPacket p = new DatagramPacket(buff,buff.length,addsv,1234);
			cl.send(p);
			byte buff2[] = new byte[256];
			DatagramPacket l = new DatagramPacket(buff2,buff2.length);
			cl.receive(l);
			String data = new String(l.getData(), 0, l.getLength());

			System.out.println("Data from Server:"+data);
			cl.close();
		} catch(IOException e) {e.printStackTrace();}
	}
}
