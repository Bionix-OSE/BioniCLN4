package ose.bionix.cn2.skullftp_gui;

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
	// This simple command is actually a MESS to handle however...
	//
	// LIST (from the original RFC 959) does ls/dir as a SHELL COMMAND on the server end.
	// For CLI use, fine (and you can see that it works fine over the CLI version),
	// but if we need to feed data to a GUI (say JTable), it's gonna be a headache.
	// We can just make it return a bare list of files instead with NLST,
	// but we are gonna need more than just file names, which only LIST provides...
	//
	// Fortunately, the newer RFC 3659's has MLSD, designed to solve this exact problem.
	// It returns a ;-separated list, which we can then delimit and populate to FileList.
	// Perfect, let's use that!
	// ...Not quite.
	public List<FileList> ls() throws IOException {
		String msgFailed = "Failed to list directory, server responded with: ";
		boolean isRFC3659 = false;

		// We need PASV first to create a data transfer tunnel
		Socket socket = net.requestPasv();
		// It is here that our second problem arises...
		int respCode = net.sendCmd("MLSD");
		// Not all servers supports MLSD by default, since it's part of a newer standard.
		// There's no telling it to enable the newer protocols, so if it throws a 500 at us
		// we have no choice but to resort back to NLST (which sucks because it does not
		// tell us whether an entry is a directory or not).
		isRFC3659 = respCode != 500;
		if (!isRFC3659) {respCode = net.sendCmd("NLST");}
		else if (respCode == 501) {
			// MLSD returns a 501 if the directory is empty, instead of 150 and a null response
			// We don't need to handle that here so we just return null as what LIST/NLST would normally do
			socket.close();
			return null;
		}
		// If all goes according to plan...
		if (respCode != 150 && respCode != 125) {
			socket.close();
			throw new IOException(msgFailed + respCode);
		}
		// ...then we read received data from the socket.
		List<String> rraw = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String rln; while ((rln = reader.readLine()) != null) {rraw.add(rln);}
		} finally {socket.close();}
		// Finally acknowledge the completion response (226).
		respCode = net.getResponse();
		if (respCode != 226) {throw new IOException(msgFailed + respCode);}

		// Now, time to conform the data to the format we wanted.
		List<FileList> filelist = new ArrayList<>();
		// Whether the list was from MLSD or not is defined by isRFC3659 above. If it wasn't, just populate
		// the names to fileList.name and leave everything else as is.
		// Otherwise, here comes the parsing...
		for (String line : rraw) {
			if (line.trim().isEmpty()) continue;
			String name, size, date;
			name = size = date = null;
			boolean isdir = false;
			if (isRFC3659) {
				// MLSD lists in this format:
				/// type=dir;modify=20260529181145.149;perms=cplem; Columbina
				/// type=file;size=97426;modify=20260529174432.136;perms=awr; Sandrone.txt
				// The name and the metadata are separated by a single space, that's our first thing to look for
				int nmsep = line.indexOf(' ');
				name = line.substring(nmsep + 1);
				String mtd = line.substring(0, nmsep);
				// With just the metadata, we can start break out fields from it
				String[] mtds = mtd.split(";");
				for (String mtdfield : mtds) {
					String[] mtdkv = mtdfield.split("=", 2);
					String mtdkey = mtdkv[0].toLowerCase().trim();
					String mtdval = mtdkv[1].trim();
					switch (mtdkey) {
						// Populate the values based on its key
						case "size":
							size = mtdval + " B";
							break;
						case "date":
							date = parseDate(mtdval);
							break;
						case "type":
							isdir = mtdval == "dir";
							break;
					}
				}
			} else {name = line.trim();}
			filelist.add(new FileList(name, size, date, isdir));
		}
		return filelist;
	}
	private String parseDate(String dateString) {
		if (dateString != null && dateString.length() >= 12) {
			// 20260529181145.149
			String year = dateString.substring(0, 4); // 2026
			String month = dateString.substring(4, 6); // 05
			String day = dateString.substring(6, 8); // 29
			String hour = dateString.substring(8, 10); // 18
			String minute = dateString.substring(10, 12); // 11
			// We ignore the ss.ms for now...
			return year + "-" + month + "-" + day + " " + hour + ":" + minute;
		}
		return dateString;
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
