package ose.bionix.cn2.skullftp_gui;

import java.io.*;
import java.net.*;

public class NetHandler {
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	public String responseBuffer;
	private static final int timeout = 5000;

	// Connection handling
	public void connect(String ip, int port) throws IOException {
		// Create connection socket to the server
		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, port), timeout);
		socket.setSoTimeout(timeout);
		// Initialize IO (reader/writer)
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		// Wait for server hello (220)
		int respCode = getResponse();
		if (respCode != 220) {
			disconnect();
			throw new IOException("Connection refused: " + respCode);
		}
	}
	public void disconnect() {
		if (socket != null && !socket.isClosed()) {
			try {
				// First try to send "QUIT" signal if socket is still healthy, in raw
				writer.write("QUIT\r\n"); writer.flush();
			} catch (Exception e) {} // Ignore failures since we don't need to handle them in this context
		}
		// Free up any related resources (using the helper below also to supress failures)
		silentClose(reader);
		silentClose(writer);
		silentClose(socket);
	}
	private void silentClose(AutoCloseable obj) {
		if (obj != null) {
			try {obj.close();} catch (Exception e) {}
		}
	}

	// Authentication
	// This will NOT work with RFC 4217's FTP over TLS, as that requires complexed auth
	// mechanism that it out of scope both for this project and my knowledge
	public void login(String user, String password) throws IOException {
		// Send username first
		int respCode = sendCmd("USER " + user);
		// If we get 331, means username is OK. Now we send password (in plaintext :], this is why they want TLS btw)
		if (respCode == 331) {respCode = sendCmd("PASS " + password);}
		// Whether we are in is whether we get 230 or not.
		if (respCode != 230) {throw new IOException("Authentication failure: " + respCode);}
	}
	public void login() throws IOException {
		login("anonymous", "anon@ftp.net");
	}

	// Communication functions
	public Socket requestPasv() throws IOException {
		// This requests passive mode from the server - Which is a new IP socket from the one we are using.
		// Obviously we need to send the request first.
		String msgFailed = "Passive mode request failed.";
		int respCode = sendCmd("PASV");
		if (respCode != 227) {throw new IOException(msgFailed);}
		// Once we got the response back, parse the respone body which looks like this
		// "227 Entering Passive Mode (10,0,2,32,50,195,14)"
		// First 4 entries forms the IPv4 address, while the last 3 forms the port number
		// (due to these numbers only going up to 255 and we have well... 65536 possible ports).
		int rsta = responseBuffer.indexOf("(");
		int rend = responseBuffer.indexOf(")");
		if (rsta == -1 || rend == -1) {throw new IOException(msgFailed);}
		String[] rraw = responseBuffer.substring(rsta + 1, rend).split(",");
		String ip = rraw[0] + "." + rraw[1] + "." + rraw[2] + "." + rraw[3];
		int port = (Integer.parseInt(rraw[4]) * 256) + Integer.parseInt(rraw[5]); // Port = [4] * 256 + [5]
		return new Socket(ip, port);
	}
	public int getResponse() throws IOException {
		String resp = reader.readLine();
		if (resp == null) {
			throw new IOException("Connection dropped.");
		}
		String respCode = resp.substring(0, 3);
		StringBuilder respFull = new StringBuilder(resp).append("\n");
		// Multi-response check (220-Message...)
		if (resp.length() >= 4 && resp.charAt(3) == '-') {
			respCode = resp.substring(0, 3); // Keep this as string...
			while ((resp = reader.readLine()) != null) {
				respFull.append(resp).append("\n");
				// Break on " " terminator
				if (resp.startsWith(respCode + " ")) break; // ...so we can do our check here...
			}
		}
		this.responseBuffer = respFull.toString().trim();
		return Integer.parseInt(respCode); // ...and THEN we convert to int.
	}
	public int sendCmd(String cmd) throws IOException {
		if (writer == null) throw new IOException("Not connected to a server.");
		// We just send the command as raw text
		writer.write(cmd + "\r\n"); writer.flush();
		return getResponse();
	}

}
