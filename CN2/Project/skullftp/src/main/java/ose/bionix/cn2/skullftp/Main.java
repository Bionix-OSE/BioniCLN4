package ose.bionix.cn2.skullftp;

import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		PrintStream output = System.out;
		NetHandler net = new NetHandler();
		FileHandler file = new FileHandler(net);

		output.println("Project SkullFTP - (c) Bionix Butter");
		output.println("For testing purposes only. Version 0.1.3.0\n");

		// Phase 1: Connect
		output.print("Enter server IP address: ");
		String ip = input.nextLine().trim();
		output.print("Enter port (Default: 21): ");
		String porti = input.nextLine().trim();
		int port = porti.isEmpty() ? 21 : Integer.parseInt(porti);
		try {
			output.println("Connecting to " + ip + ":" + port + "...");
			net.connect(ip, port);
			output.println("Connected! Server Response: " + net.responseBuffer);
		} catch (IOException e) {
			System.err.println("Connection failed: " + e.getMessage());
			input.close();
			return;
		}
		// Phase 2: Auth
		try {
			output.print("Login anonymously? (y/n): ");
			String anon = input.nextLine().trim().toLowerCase();
			boolean login;

			if (anon.equals("y") || anon.equals("yes")) {
				login = net.login();
			} else {
				output.print("Username: ");
				String user = input.nextLine().trim();
				output.print("Password: ");
				String pass = input.nextLine().trim();
				login = net.login(user, pass);
			}

			if (!login) {
				System.err.println("Authetication failure.");
				net.disconnect();
				input.close();
				return;
			}
			output.println("Ready.");
		} catch (IOException e) {
			System.err.println("Authentication failure: " + e.getMessage());
			net.disconnect();
			input.close();
			return;
		}

		// Ready! Main menu loop
		while (true) {
			String cdir = "";
			try {
				cdir = file.pwd();
			} catch (IOException e) {
				cdir = "?";
			}
			// Command parser
			output.print("ftp:" + cdir + "> ");
			String cmd = input.nextLine().trim();
			if (cmd.isEmpty()) continue;
			String[] tokens = cmd.split("\\s+", 3);
			String command = tokens[0].toLowerCase();
			// Command -> action
			try {
				switch (command) {
					case "pwd":
						output.println(file.pwd());
						break;

					case "ls":
						List<String> flist = file.ls();
						for (String f : flist) {
							output.println(f);
						}
						break;

					case "cd":
						if (tokens.length < 2) {
							output.println("Usage: cd <directory>");
						} else {
							if (!file.cd(tokens[1])) {
								output.println("Failed to change directory.");
							}
						}
						break;

					case "mkdir":
						if (tokens.length < 2) {
							output.println("Usage: md <directory_name>");
						} else {
							if (!file.md(tokens[1])) {
								output.println("Failed to create directory.");
							}
						}
						break;

					case "rmdir":
						if (tokens.length < 2) {
							output.println("Usage: rd <directory_name>");
						} else {
							if (!file.rd(tokens[1])) {
								output.println("Failed to remove directory.");
							}
						}
						break;

					case "delete":
						if (tokens.length < 2) {
							output.println("Usage: del <file_name>");
						} else {
							if (!file.del(tokens[1])) {
								output.println("Failed to delete file.");
							}
						}
						break;

					case "get":
						if (tokens.length < 3) {
							output.println("Usage: get <remote_source> <local_destination>");
						} else {
							output.println("Downloading...");
							if (file.get(tokens[1], tokens[2])) {
								output.println("Download complete.");
							} else {
								output.println("Download failed.");
							}
						}
						break;

					case "put":
						if (tokens.length < 3) {
							output.println("Usage: put <local_source> <remote_destination>");
						} else {
							output.println("Uploading...");
							if (file.put(tokens[1], tokens[2])) {
								output.println("Upload complete.");
							} else {
								output.println("Upload failed.");
							}
						}
						break;

					case "quit":
					case "exit":
						net.disconnect();
						output.println("Goodbye!");
						input.close();
						return;
					default:
						output.println("Available commands: pwd, cd <dir>, ls, get <remote> <local>, put <local> <remote>, md <dir>, rmrddir <dir>, del <file>, quit");
						break;
				}
			} catch (IOException e) {
				System.err.println("Command execution error: " + e.getMessage());
				System.err.println("Server connection status might be unstable.");
			}
		}
	}
}
