import java.net.*;
import java.io.*;

public class UDPServer {
	public static void main(String[] args) {
		try{
			DatagramSocket sv = new DatagramSocket(1234);
			byte buff1[] = new byte[256];
			DatagramPacket q = new DatagramPacket(buff1, buff1.length);
			sv.receive(q);

			String data = new String(q.getData(), 0, q.getLength());
			String kq = (Integer.parseInt(data) % 2 == 0) ? "even" : "odd"; // This is equivalent to if-else statement, where [condition ? value_if_true : value_if_false]
			byte buff2[] = new byte[256];
			buff2 = kq.getBytes();
			InetAddress addcl = q.getAddress();
			int portcl = q.getPort();
			DatagramPacket k = new DatagramPacket(buff2,buff2.length,addcl,portcl);

			sv.send(k);
			sv.close();
		} catch(IOException e) {e.printStackTrace();}
	}
}
