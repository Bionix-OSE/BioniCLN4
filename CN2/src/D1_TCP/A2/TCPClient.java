import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost",1234);

			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			Scanner keyboard = new Scanner(System.in);

			while (true) {
				System.out.print("Product name, quantity, unit price: ");
				String input = keyboard.nextLine();
				out.write(input + "\r\n"); out.flush();
				String result = in.readLine();
				System.out.println("[Server] " + result);
				if (input.equalsIgnoreCase("end")) {break;}
			}

			s.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
