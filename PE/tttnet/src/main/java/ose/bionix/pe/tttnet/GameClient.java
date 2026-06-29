package ose.bionix.pe.tttnet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

		try (Socket socket = new Socket(host, port);
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

			Thread reader = new Thread(() -> {
				try {
					StringBuilder lineBuffer = new StringBuilder();
					int ch;
					while ((ch = serverIn.read()) != -1) {
						char c = (char) ch;
						lineBuffer.append(c);
						System.out.print(c);
						if (c == '\n') {
							String line = lineBuffer.toString().trim();
							if ("QUIT".equalsIgnoreCase(line)) {
								break;
							}
							lineBuffer.setLength(0);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			reader.setDaemon(true);
			reader.start();

			String line;
			while (reader.isAlive()) {
				if ((line = console.readLine()) != null) {
					out.println(line);
					if ("q".equals(line)) {
						reader.interrupt();
						break;
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Client error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
