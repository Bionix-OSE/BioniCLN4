package ose.bionix.pe.tttpds;

public class ClientTest {
	public static void main(String[] args) {
		String host = args.length > 1 ? args[1] : "localhost";
		int port = args.length > 2 ? Integer.parseInt(args[2]) : 8080;
		Client client = new Client(host, port);
		client.play();
	}
}
