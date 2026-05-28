package ose.bionix.cn2.skullftp;

import java.io.*;
import java.net.*;
import java.util.*;

public class FileHandler {
	private final NetHandler net;
	public FileHandler(NetHandler net) {this.net = net;}

	// Directory operations
	public String pwd() throws IOException {
		int respCode = net.sendCmd("PWD");
		if (respCode != 257) {
			throw new IOException("Failed to get current working directory");
		}
		int rsta = net.responseBuffer.indexOf('"');
		int rend = net.responseBuffer.lastIndexOf('"');
		if (rsta != -1 && rend > rsta) {
			return net.responseBuffer.substring(rsta + 1, rend);
		}
		return null;
	}
	public List<String> ls() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		String msgFailed = "Failed to list directory, server responded with: ";

		// Request PASV to get information from
		Socket socket = net.requestPasv();
		// Send LIST
		int respCode = net.sendCmd("LIST");
		if (respCode != 150 && respCode != 125) {
			socket.close();
			throw new IOException(msgFailed + respCode);
		}
		// Read received data from the socket
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String rln; while ((rln = reader.readLine()) != null) {files.add(rln);}
		} finally {socket.close();}

		// Finally read the completion response (226) from and return the files list
		net.getResponse();
		return files;
	}
	public boolean cd(String dir) throws IOException {
		int respCode = net.sendCmd("CWD " + dir);
		return respCode == 250;
	}
	public boolean md(String dir) throws IOException { // mkdir
		int respCode = net.sendCmd("MKD " + dir);
		return respCode == 257; // 257 "dir" created
	}
	public boolean rd(String dir) throws IOException { // rm -r
		int respCode = net.sendCmd("RMD " + dir);
		return respCode == 250;
	}

	// File operations
	public boolean get(String fsource, String fdest) throws IOException {
		Socket socket = null;
		try {
			// Normally FTP communicates over text-based tunnel mode. If we try to send any raw binary streams
			// over this our data might get screwed over by text-handling shenanigans (like LE and such).
			// Thankfully we can tell the server to switch to binary-based tunnel mode instead.
			net.sendCmd("TYPE I"); // TODO: See what response code we're gonna get from this mode-switch and handle them accordingly.
			// We also need a PASV socket to send data over
			socket = net.requestPasv();
			// Now we initiate file retrival with RETR
			int respCode = net.sendCmd("RETR " + fsource);
			if (respCode != 150 && respCode != 125) {
				if (socket != null); socket.close();
				return false;
			}
			// Once server's OK, begin streaming data to fdest
			try (InputStream strmIn = socket.getInputStream()) {
				OutputStream strmOut = new FileOutputStream(fdest);
				byte[] fbuff = new byte[4096];
				int fbytesRead;
				while ((fbytesRead = strmIn.read(fbuff)) != -1) {strmOut.write(fbuff, 0, fbytesRead);}
				strmOut.flush(); strmOut.close();
			}
			// Finally, like above, read the completion response
			net.getResponse();
			return net.responseBuffer.startsWith("226") || net.responseBuffer.startsWith("250");
		} catch (IOException e) {return false;}
		finally {
			if (socket != null && !socket.isClosed()) {try {socket.close();} catch (Exception e) {}}
		}
	}
	public boolean put(String fsource, String fdest) throws IOException {
		// It's get() but reversed
		Socket socket = null;
		try {
			net.sendCmd("TYPE I"); // TODO: See what response code we're gonna get from this mode-switch and handle them accordingly.
			socket = net.requestPasv();
			int respCode = net.sendCmd("STOR " + fdest); // We store with STOR instead of RETR
			if (respCode != 150 && respCode != 125) {
				if (socket != null); socket.close();
				return false;
			}
			try (InputStream strmIn = new FileInputStream(fsource)) {
				OutputStream strmOut =socket.getOutputStream();
				byte[] fbuff = new byte[4096];
				int fbytesRead;
				while ((fbytesRead = strmIn.read(fbuff)) != -1) {strmOut.write(fbuff, 0, fbytesRead);}
				strmOut.flush(); strmOut.close();
			}
			net.getResponse();
			return net.responseBuffer.startsWith("226") || net.responseBuffer.startsWith("250");
		} catch (IOException e) {return false;}
		finally {
			if (socket != null && !socket.isClosed()) {try {socket.close();} catch (Exception e) {}}
		}
	}
	public boolean del(String file) throws IOException {
		int respCode = net.sendCmd("DELE " + file);
		return respCode == 250;
	}
}
