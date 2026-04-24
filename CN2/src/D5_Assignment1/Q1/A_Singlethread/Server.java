import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
	public static void main(String[] args) {
		Random rand = new Random();
		int random = rand.nextInt(99);
		System.out.println("Random number: "+random);
		try {
			ServerSocket ss = new ServerSocket(14393);
			Socket con = ss.accept();
			BufferedReader in= new BufferedReader(new InputStreamReader (con.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter (con.getOutputStream()));
			
			boolean guessed = false;
			while (!guessed) {
				int rdata = Integer.parseInt(in.readLine());
				guessed = (rdata == random);
				String verdict = switch (Integer.compare(rdata, random)) {
					case 1 -> "Your number is bigger. Please predict again.";
					case -1 -> "Your number is smaller. Please predict again.";
					default -> "Congratulations! You predicted correctly!";
				};
				out.write(verdict+"\r\n"); out.write(guessed ? "end\r\n" : "continue\r\n"); out.flush();
			};
			out.write("end"); out.flush();
			con.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
