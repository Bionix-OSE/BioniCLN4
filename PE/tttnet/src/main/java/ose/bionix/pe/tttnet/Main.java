package ose.bionix.pe.tttnet;

public class Main {
	public static void main(String[] args) {
		args[0] = args.length > 0 ? args[0] : "server";
		switch (args[0]) {
			case "server": {
				args[1] = args.length > 1 ? args[1] : "stmc";
				switch (args[1]) {
					case "stsc": {
						GameServer_STSC server = new GameServer_STSC();
						server.run();
						break;
					}
					case "mtmc": {
						GameServer_MTMC server = new GameServer_MTMC();
						server.run();
						break;
					}
					case "stmc": {
						try {
							GameServer_STMC server = new GameServer_STMC();
							server.run();
						} catch (Exception e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						break;
					}
					default: System.exit(1);
				}
				break;
			}
			case "client": {
				String[] argv = {"localhost","8080"};
				GameClient.main(argv);
			}
			default: System.exit(1);
		}
	}
}
