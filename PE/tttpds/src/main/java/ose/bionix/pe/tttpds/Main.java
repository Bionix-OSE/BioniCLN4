package ose.bionix.pe.tttpds;

public class Main {
	public static void main(String[] args) {
		args[0] = args.length > 0 ? args[0] : "server";
		switch (args[0]) {
			case "server": {
				Server.main();
				break;
			}
			case "client": {
				
			}
			default: System.exit(1);
		}
	}
}
