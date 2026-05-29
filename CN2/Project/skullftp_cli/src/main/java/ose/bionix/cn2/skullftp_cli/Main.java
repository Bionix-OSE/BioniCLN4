package ose.bionix.cn2.skullftp_cli;

import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		Console console = System.console();
		PrintStream output = System.out;
		PrintStream err = System.err;
		NetHandler net = new NetHandler();
		FileHandler file = new FileHandler(net);

		output.println("Project SkullFTP - (c) Bionix Butter");
		output.println("For testing purposes only. Version 0.1.4\n");

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
			err.println("Connection failed: " + e.getMessage());
			input.close();
			return;
		}
		// Phase 2: Auth
		try {
			output.print("Login anonymously? (yN): ");
			String anon = input.nextLine().trim().toLowerCase();
			boolean login;

			if (anon.equals("y") || anon.equals("yes")) {
				login = net.login();
			} else {
				output.print("Username: ");
				String user = input.nextLine().trim();
				String pass = null;
				if (console != null) {
					output.print("Password: ");
					char[] passarr = console.readPassword();
					pass = String.valueOf(passarr);
				} else {
					output.print("Password (WILL BE ECHOED!): ");
					pass = input.nextLine();
				}
				login = net.login(user, pass);
			}

			if (!login) {
				err.println("Authetication failure.");
				net.disconnect();
				input.close();
				return;
			}
			output.println("Ready.");
		} catch (IOException e) {
			err.println("Authentication failure: " + e.getMessage());
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
				err.println(e.getMessage());
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
					case "mkdir":
					case "rmdir":
					case "del":
						if (tokens.length < 2) {
							String param = "directory";
							if (command.equals("del")) param = "filename";
							output.println("Usage: " + command + " <" + param + ">");
						} else {
							file.sendFileOp(command, tokens[1]);
						}
						break;

					case "get":
					case "put":
						if (tokens.length < 3) {
							String usageHint = "Usage: put <local_source> <remote_destination>";
							if (command.equals("get")) usageHint = "Usage: get <remote_source> <local_destination>";
							output.println(usageHint);
						} else {
							String actionHint = "Upload";
							int param1 = 1;
							int param2 = 2;
							if (command.equals("get")) {
								actionHint = "Download";
								param1 = 2;
								param2 = 1;
							}
							output.println(actionHint + "ing...");
							file.transfer(command, tokens[param1], tokens[param2]);
							output.println(actionHint + " complete.");
						}
						break;

					case "quit":
					case "exit":
						net.disconnect();
						output.println("Goodbye!");
						input.close();
						return;
					default:
						output.println("Available commands: pwd, cd <dir>, ls, get <remote> <local>, put <local> <remote>, mkdir <dir>, rmdir <dir>, del <file>, quit");
						break;
				}
			} catch (IOException e) {err.println(e.getMessage());}
		}
	}
}
