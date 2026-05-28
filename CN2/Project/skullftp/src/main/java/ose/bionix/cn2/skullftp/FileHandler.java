package ose.bionix.cn2.skullftp;

import java.io.*;
import java.net.*;
import java.util.*;

public class FileHandler {
	private final NetHandler net;
	public FileHandler(NetHandler net) {this.net = net;}

	// PWD: Print current working directory
	public String pwd() throws IOException {
		int respCode = net.sendCmd("PWD");
		if (respCode != 257) {
			throw new IOException("Failed to get current working directory: " + respCode);
		}
		int rsta = net.responseBuffer.indexOf('"');
		int rend = net.responseBuffer.lastIndexOf('"');
		if (rsta != -1 && rend > rsta) {
			return net.responseBuffer.substring(rsta + 1, rend);
		}
		return null;
	}

	// LIST is... obviously for listing a directory
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
		// Finally acknowledge the completion response (226) from and return the files list.
		net.getResponse();
		return files;
	}

	// RETR and STOR, for downloading and uploading files
	public void transfer(String mode, String flocal, String fremote) throws IOException {
		Socket socket = null;
		String msgFailed = "Failed to download file: ";
		boolean up = mode.equalsIgnoreCase("put"); // Boolean mode indicator, in favor of upload
		boolean ok = false;
		Exception streamException = null;

		// Normally FTP communicates over text-based tunnel mode. If we try to send any raw binary streams
		// over this our data might get screwed over by text-handling shenanigans (like LE and such).
		// Thankfully we can tell the server to switch to binary-based tunnel mode instead.
		int respCode = net.sendCmd("TYPE I");
		if (respCode != 200) {throw new IOException(msgFailed + respCode);}
		try {
			// We also need a PASV socket to send data over
			socket = net.requestPasv();
			// Now we initiate file retrival with RETR
			String cmd = (up ? "STOR " : "RETR ") + fremote;
			respCode = net.sendCmd(cmd);
			ok = respCode == 150 || respCode == 125;
			if (!ok) {throw new IOException(msgFailed + respCode);}
			// Once server's OK, begin streaming data to fdest
			// We use the mode indicator to decide which stream to use for both ends
			try (InputStream strmIn = up ? new FileInputStream(flocal) : socket.getInputStream();
				OutputStream strmOut = up ? socket.getOutputStream() : new FileOutputStream(flocal)) {
				byte[] fbuff = new byte[4096];
				int fbytesRead;
				while ((fbytesRead = strmIn.read(fbuff)) != -1) {strmOut.write(fbuff, 0, fbytesRead);}
				strmOut.flush();
			} catch (Exception e) {streamException = e;}
		} finally {
			if (socket != null && !socket.isClosed()) {try {socket.close();} catch (Exception i) {}}
			// Finally, like above, acknowledge the completion response (if the transfer did happen, indicated by "ok")
			// We do this down here to make sure that we don't desync the control channel if "ok" was to be false and an exception is thrown
			// (Basically leaving the server hanging with a 226/250 and confuse whatever requests that might come next)
			if (ok) {
				try { // Handle this too in case if things goes really wrong
					respCode = net.getResponse();
					if (streamException == null && respCode != 226 && respCode != 250) {throw new Exception(msgFailed + respCode);}
				} catch (Exception e) {if (streamException == null) {streamException = e;}}
			}
		}
		if (streamException != null) {throw new IOException(msgFailed + streamException.getMessage());}
	}

	// CWD, MKD, RMD for manipulating directories, and DELE for deleting files (NOT directories)
	// These are simpler as all they need to do is send commands with some args.
	public void sendFileOp(String op, String dir) throws IOException {
		String cmd;
		int respCodeExpected;
		String msgFailed;

		switch (op) {
			case "cd":
				cmd = "CWD";
				respCodeExpected = 250;
				msgFailed = "Failed to change working directory: ";
				break;
			case "mkdir":
				cmd = "MKD";
				respCodeExpected = 257;
				msgFailed = "Failed to create directory: ";
				break;
			case "rmdir":
				cmd = "RMD";
				respCodeExpected = 250;
				msgFailed = "Failed to remove directory: ";
				break;
			case "del":
				cmd = "DELE";
				respCodeExpected = 250;
				msgFailed = "Failed to delete file: ";
				break;
			default:
				throw new IllegalArgumentException("Unknown file operation specified: " + op);
		}

		int respCode = net.sendCmd(cmd + " " + dir);
		if (respCode != respCodeExpected) {
			throw new IOException(msgFailed + respCode);
		}
	}
}
