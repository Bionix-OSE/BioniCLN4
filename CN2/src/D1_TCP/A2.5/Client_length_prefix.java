public class Client_length_prefix {
	public static void main(String[] args) {
		try{
			Socket s = new Socket("localhost",1236);
			
			BufferOutputStream out = new BufferOutputStream(s.getOutputStream())
	
			Scanner keyboard = new Scanner(System.in); 
			System.out.println("Type in the a product name: ");
			String a = keyboard.nextLine();
			System.out.println("Type in the a product quantity: ");
			int b = keyboard.nextInt();
			System.out.println("Type in the a product price: ");
			float c = keyboard.nextFloat();
			
			int length_a = a.length();
			byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length_a).array();
			byte[] stringBytes = a.getByte();
			byte[] intBytes = ByteBuffer.allocate(4).putInt(b).array();
			byte[] floatBytes = ByteBuffer.allocate(4).putFloat(c).array();
	
			out.write(lengthBytes);
			out.write(stringBytes);
			out.write(intBytes);
			out.write(floatBytes);
			out.flush();
		}
		catch (IOException e){}
	}
}
