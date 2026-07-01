package ose.bionix.pe.tttpds;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage:\n    tttpds.jar server\n    tttpds.jar client <serverIP> <port>");
			System.exit(1);
		};
		switch (args[0]) {
			case "server": {
				Server.main();
				break;
			}
			case "client": {
				String host = args.length > 1 ? args[1] : "localhost";
				int port = args.length > 2 ? Integer.parseInt(args[2]) : 8080;
				Client client = new Client(host, port);
				client.play();
			}
			default: System.exit(1);
		}
	}
}
