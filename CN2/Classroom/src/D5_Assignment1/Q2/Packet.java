import java.nio.ByteBuffer;

public class Packet {
	private int sequenceNumber;
	private int dataType;
	private String data;
	

	public Packet(int sequenceNumber, int dataType, String data) {
		this.sequenceNumber = sequenceNumber;
		this.dataType = dataType;
		this.data = data;
	}

	public byte[] construct() {
		byte[] dataBytes = String.valueOf(data).getBytes();
		byte[] datatypeBytes = String.valueOf(dataType).getBytes();
		// DATAGRAM FORMAT: [4 bytes for sequence number] [1 byte for data type] [data bytes]
		// The data type will be hanlded by the server. On this end all we need to care about are the bytes, and thus we only concern strings here.
		byte[] datagram = new byte[4 + 1 + dataBytes.length];
		// 4 first bytes of sequence number
		System.arraycopy(ByteBuffer.allocate(4).putInt(sequenceNumber).array(), 0, datagram, 0, 4);
		// 5th byte for data type
		datagram[4] = datatypeBytes[0];
		// The rest for the data itself
		System.arraycopy(dataBytes, 0, datagram, 5, dataBytes.length); // Copy the data bytes
		return datagram;
	}
}