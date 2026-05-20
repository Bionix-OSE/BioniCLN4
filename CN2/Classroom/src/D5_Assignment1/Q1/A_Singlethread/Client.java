import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost",14393);

			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			Scanner keyboard = new Scanner(System.in);

			while (true) {
				System.out.print("Please input an integer: ");
				int input = keyboard.nextInt();
				out.write(input + "\r\n"); out.flush();
				String result = in.readLine();
				System.out.println("[Server] " + result);
				String signal = in.readLine();
				if (signal.equals("end")) {break;}
			}

			s.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
