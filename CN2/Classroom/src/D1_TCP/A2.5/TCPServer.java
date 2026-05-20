import java.io.BufferedInputStream;
import java.net.Socket;

public class TCPServer {
	public static void main(String[] args) {
		try {
			ServerSocker ss = new ServerSocket(1234);
			Socket con = ss.accept();

			BufferInputStream in = new BufferedInputStream(con.getInputStream());

			byte[] buffer = new byte[4];
			in.read(buff_1);
			int length_a = ByteBuffer.wrap(buff_1).getInt();

			byte[] buffer = new byte[length_a];
			in.read(buff_2);;
			String name = new String(buff_2);
			
			byte[] buffer = new byte[4];
			in.read(buff_3);
			int quantity = ByteBuffer.wrap(buff_3).getInt();

			byte[] buffer = new byte[8];
			in.read(buff_4);
			int price = ByteBuffer.wrap(buff_4).getInt();

			System.out.println("Product name: " + name + ", Quantity: " + quantity + ", Unit price: " + price);
			con.close();
		}
	}
}
