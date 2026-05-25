package ose.bionix.cn2.skullftp;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	private String responseStr;
	private int responseCode;

	// Connection handling
	public boolean clConnect(String ip, int port) {
		return false;
	}
	public boolean clDisconnect() {
		return false;
	}
	public boolean clLogin(String user, String password) {
		return false;
	}

	// Communication functions
	public int clParseResponse() throws IOException {
		return 0;
	}
	public void clSendCmd(String cmd) {
		// ...
	}

	// File operations
	public List<String> fileList() {
		ArrayList<String> files = new ArrayList<String>();
		return files;
	}
	public boolean fileGet(String fsource, String fdest) {
		return false;
	}
}