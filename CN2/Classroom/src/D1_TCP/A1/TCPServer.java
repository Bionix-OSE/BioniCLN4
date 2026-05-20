import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(1234);
			Socket con = ss.accept();
			BufferedReader in= new BufferedReader(new InputStreamReader (con.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter (con.getOutputStream()));
			
			String rdata = in.readLine();
			System.out.println(rdata);
			out.write(rdata.toUpperCase()+"\r\n");
			
			out.flush();
			con.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
