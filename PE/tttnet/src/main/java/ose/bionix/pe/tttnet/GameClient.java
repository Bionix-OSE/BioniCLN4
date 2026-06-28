package ose.bionix.pe.tttnet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

		try (Socket socket = new Socket(host, port);
			InputStream serverIn = socket.getInputStream();
			PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

			Thread reader = new Thread(() -> {
				try {
					int b;
					while ((b = serverIn.read()) != -1) {
						System.out.print((char) b);
					}
				} catch (Exception e) {}
			});
			reader.setDaemon(true);
			reader.start();

			String line;
			while ((line = console.readLine()) != null) {
				serverOut.println(line);
				if ("q".equals(line)) break;
			}

		} catch (Exception e) {
			System.err.println("Client error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
