import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) throws Exception {
        Scanner keyboard = new Scanner(System.in);
        DatagramSocket cl = new DatagramSocket();
        int seq = 1;
        byte[] intToBytes = ByteBuffer.allocate(4).putInt(seq).array();
        // Send integer with sequence number
        System.out.print("Please input first integer: ");
        int x = keyboard.nextInt();
        byte[] buffInt = String.valueOf(x).getBytes();
        // Create a message with sequence number + data type + data
        byte[] dataInt = new byte[4 + 1 + buffInt.length]; // 4 bytes for the sequence number, 1 byte for data type
        System.arraycopy(intToBytes, 0, dataInt, 0, 4); // Copy sequence number
        dataInt[4] = 1; // Indicate that this is an Integer
        System.arraycopy(buffInt, 0, dataInt, 5, buffInt.length); // Copy the integer bytes
        InetAddress addsv = InetAddress.getByName("localhost");
        DatagramPacket pInt = new DatagramPacket(dataInt, dataInt.length, addsv, 9876);
        cl.send(pInt);
        // Increment sequence number
        seq++;
        intToBytes = ByteBuffer.allocate(4).putInt(seq).array();
        // Send string with sequence number
        System.out.print("Please input second integer: ");
        int y = keyboard.nextInt();
        byte[] buffStr = String.valueOf(y).getBytes();
        // Create a message with sequence number + data type + data
        byte[] dataStr = new byte[4 + 1 + buffStr.length]; // 4 bytes for the sequence number, 1 byte for data type
        System.arraycopy(intToBytes, 0, dataStr, 0, 4); // Copy sequence number
        dataStr[4] = 1; // Indicate that this is an Integer
        System.arraycopy(buffStr, 0, dataStr, 5, buffStr.length); // Copy the integer bytes
        DatagramPacket pStr = new DatagramPacket(dataStr, dataStr.length, addsv, 9876);
        cl.send(pStr);
        keyboard.close();
        // Receive the result from the server
        byte buff[] = new byte[256];
        DatagramPacket l = new DatagramPacket(buff,buff.length);
        cl.receive(l);
        String data = new String(l.getData(), 0, l.getLength());
        System.out.println("Server responds: Difference is " + data);
        cl.close();
    }
}
