package name.yumao.ffxiv.chn.model;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import name.yumao.ffxiv.chn.util.ArrayUtil;

public class EXDFEntry {
	private byte[] chunk;
	
	private byte[] string;
	
	private byte[] data;
	
	public EXDFEntry(byte[] data, int datasetChunkSize) {
		Logger log = Logger.getLogger("GPLogger");
		ByteBuffer buffer = ByteBuffer.wrap(data);
		if (buffer.remaining() < datasetChunkSize) {
			log.severe("Buffer position: " + buffer.position() + ", limit: " + buffer.limit() + ", remaining: " + buffer.remaining() + ", dataChunkSize: " + datasetChunkSize);
	        this.chunk = new byte[0];  // empty array for chunk
	        this.string = new byte[0]; // empty array for string
	        this.data = new byte[0];   // empty array for combined data
	        return;
	    }
		this.chunk = new byte[datasetChunkSize];
		buffer.get(this.chunk);
		this.string = new byte[data.length - datasetChunkSize];
		buffer.get(this.string);
		this.data = ArrayUtil.append(this.chunk, this.string);
	}
	
	public byte getByte(int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		return buffer.get();
	}
	
	public short getShort(int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		return buffer.getShort();
	}
	
	public int[] getQuad(short offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		int[] quad = new int[4];
		quad[0] = buffer.getShort();
		quad[1] = buffer.getShort();
		quad[2] = buffer.getShort();
		quad[3] = buffer.getShort();
		return quad;
	}
	
	public boolean getByteBool(int datatype, int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		int val = buffer.get();
		int shift = datatype - 25;
		int i = 1 << shift;
		val &= i;
		return ((val & i) == i);
	}
	
	public int getInt(int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		return buffer.getInt();
	}
	
	public float getFloat(short offset) {
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		return buffer.getFloat();
	}
	
	public byte[] getString(short offset) {
		byte in;
		int datasetChunkSize = this.chunk.length;
		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(offset);
		int stringOffset = buffer.getInt();
		if (datasetChunkSize + stringOffset >= buffer.limit())
			return new byte[0]; 
		buffer.position(datasetChunkSize + stringOffset);
		int nullTermPos = -1;
		do {
			in = buffer.get();
		} while (in != 0);
		// nullTermPos = buffer.position() - datasetChunkSize + stringOffset;
		nullTermPos = buffer.position() - (datasetChunkSize + stringOffset);
		byte[] stringBytes = new byte[nullTermPos - 1];
		buffer.position(datasetChunkSize + stringOffset);
		buffer.get(stringBytes);
		return stringBytes;
	}
	
	public boolean getBoolean(short offset) {
		byte b = getByte(offset);
		return (b == 1);
	}
	
	public boolean getByteBool(short offset2) {
		return false;
	}
	
	public byte[] getChunk() {
		return this.chunk;
	}
	
	public byte[] getString() {
		return this.string;
	}
	
	public byte[] getData() {
		return this.data;
	}
}
