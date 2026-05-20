import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(1234);
			Socket con = ss.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader (con.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter (con.getOutputStream()));
			int totalPrice = 0;

			while (true) {
				String rdata = in.readLine();
				if (rdata.equalsIgnoreCase("end")) {
					out.write("Total price: " + totalPrice + "\r\n");
					out.flush();
					break;
				}
				String[] array = rdata.split(",");
				for (int i=0; i<array.length; i++) {array[i] = array[i].trim();}
				out.write("Product name: " + array[0] + ", Quantity: " + array[1] + ", Unit price: " + array[2] + "\r\n");
				out.flush();
				int quantity = Integer.parseInt(array[1]);
				int unit_price = Integer.parseInt(array[2]);
				totalPrice += quantity * unit_price;
			}
			
			con.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
