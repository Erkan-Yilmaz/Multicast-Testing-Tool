package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

/**
 * @author konne
 * 
 * Represents a packet of the binary Spam format as specified in the SAS.
 */
public final class SpamPacket implements Packet {
    /**
     * @see com.spam.mctool.model.packet.Packet#fromByteArray(ByteBuffer)
     */
    public void fromByteArray(ByteBuffer data) throws DataFormatException 
    {
        // set default values
        minSize = 0;
        senderID = 0;
        configuredPacketsPerSeconds = 0;
        senderMeasuredPacketRate = 0;
        sequenceNumber = 0;
        dispatchTime = 0;
        payload = new byte[0];
        
        data.order(ByteOrder.BIG_ENDIAN);
        
        int lastId = 0;
        
        try {
            // process all tlv tuples
            outer: while(data.hasRemaining()) {
                // read the header
                int id = data.getShort() & SHORT_MASK;
                long length = data.getInt() & INT_MASK;
                
                // find the correct tuple
                // this over-complicated loop is simply iterating over all
                // available tuples in TlvTuple. The only reason it is not 
                // a for-each loop is that this is faster if the tuples are 
                // coming in in order (which they should be if generated
                // by our tool)
                int i = lastId;
                do {
                    TlvTuple tuple = TlvTuple.values()[i];
                    if(id == tuple.getId()) {
                        if(tuple.hasFixedSize() && length != tuple.calcSize(this)) {
                        	throw new DataFormatException("Tuple with Id "+id+
                                    " has illegal data size");
                        }
                        
                        // parse the tuple
                        tuple.readTupleData(data, length, this);
                        continue outer;
                    }
                    
                    i = ++i % TlvTuple.values().length;
                } while(i != lastId);
                lastId = i;
                
                // no tuple matched
                // advance by the size specified in the tlv header
                long pos = (long)data.position() + length;
                // if the position overflows 2GB
                if((int)pos != pos){
                    // we know for a fact that a buffer class does not support
                    // being larger 2GB thus if the position overflows, we know
                    // we are out of the range of the buffer
                    throw new DataFormatException("Illegal TLV Tuple length");
                }
                
                data.position((int)pos);
            }
        } catch(BufferUnderflowException e) {
        	throw new DataFormatException("Illegal TLV Tuple format");
        }
        catch(IllegalArgumentException e) {
            // thrown if no tuple matches and the length
            // was too big and lead to buffer underflow
            throw new DataFormatException("Illegal TLV Tuple length");
        }
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#toByteArray()
     */
    public ByteBuffer toByteArray() 
    {
        long size = getSize();
        
        // even though, not illegal according to specification,
        // a packet may not be larger 2GB
        if(size > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    "Generating packets larger 2GB is not supported");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate((int)size);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        // needed in order to set the position to the beginning again
        buffer.mark();
        
        // write the tuples
        for(TlvTuple tuple : TlvTuple.values()){
            // writes the header, rest needs to be 
            // written by Tuple Implementation
            buffer.putShort((short)tuple.getId());
            buffer.putInt((int)tuple.calcSize(this));
            // which is done here
            tuple.writeTupleData(buffer, this);
        }
        
        buffer.reset();
        
        return buffer;
    }
    
    
    /**
     * Checks if the passed data buffer contains the correct 
     * header for this type of package.
     * 
     * @param data   The ByteBuffer to be checked
     * @return       Return whether data has a correct header or not.
     */
    public static boolean isCorrectHeader(ByteBuffer data) {
        data.order(ByteOrder.BIG_ENDIAN);
        int pos = data.position();
        
        return (
           data.remaining() >= TLV_HEADER_SIZE &&
           data.getShort(pos) == TlvTuple.HEADER.getId() &&
           data.getInt(pos + Short.SIZE/Byte.SIZE) == 
           TlvTuple.HEADER.calcSize(null)); // size is fixed, thus no package 
                                            // needed and null is ok
    }
    
    private long getSizeWithoutPadding() {
        long size = TLV_HEADER_SIZE; // Size of the padding header is included
        for(TlvTuple tuple : TlvTuple.values()){
            if(tuple != TlvTuple.PADDING){
                size += TLV_HEADER_SIZE + tuple.calcSize(this);
            }
        }
        
        return size;
    }
    
    private static final int TLV_HEADER_SIZE = Integer.SIZE/Byte.SIZE + 
                                               Short.SIZE/Byte.SIZE;
    
    private static final int SHORT_MASK = 0xFFFF;
    private static final long INT_MASK = 0xFFFFFFFFL;
    
    private long minSize;
    private long senderID;
    private long configuredPacketsPerSeconds;
    private long senderMeasuredPacketRate;
    private long sequenceNumber;
    private long dispatchTime;
    private byte[] payload = new byte[0];
    
    private enum TlvTuple {
        HEADER(1337, 0) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                // no data to read here
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                // no data to write here
            }
        },
        
        SENDER_ID(1, Integer.SIZE/Byte.SIZE) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                packet.setSenderId(buffer.getInt() & INT_MASK);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.putInt((int)packet.getSenderId());
            }
        },
        
        CONFIGURED_PACKETS_PER_SECOND(2, Integer.SIZE/Byte.SIZE) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                packet.setConfiguredPacketsPerSecond(buffer.getInt() & INT_MASK);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.putInt((int)packet.getConfiguredPacketsPerSecond());
            }
        },
        
        SENDER_MEASURED_PACKET_RATE(3, Integer.SIZE/Byte.SIZE) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                packet.setSenderMeasuredPacketRate(buffer.getInt() & INT_MASK);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.putInt((int)packet.getSenderMeasuredPacketRate());
            }
        },
        
        SEQUENCE_NUMBER(4, Integer.SIZE/Byte.SIZE) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                packet.setSequenceNumber(buffer.getInt() & INT_MASK);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.putInt((int)packet.getSequenceNumber());
            }
        },
        
        DISPATCH_TIME(5, Long.SIZE/Byte.SIZE) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                packet.setDispatchTime(buffer.getLong());
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.putLong(packet.getDispatchTime());
            }
        },
        
        PAYLOAD(6, -1 /* size is variable */) {
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) throws DataFormatException {
                if(length > Integer.MAX_VALUE || length > buffer.capacity()) {
                    // well java only supports arrays up to 2GB, if the payload is larger 2GB
                    // we have a problem. Luckily it is not really our problem, because the 
                    // ByteBuffer that was passed into this function does not support data
                    // larger 2GB either. So if the length is larger 2GB we can throw an
                    // BufferUnderflowException.
                    throw new BufferUnderflowException();
                }
                
                byte[] load = new byte[(int)length];
                try {
                    buffer.get(load);
                } catch(BufferUnderflowException e) {
                    // this is thrown if the get operation tries to
                    // read data outside of the buffers limits.
                    throw new DataFormatException("Payload size does not match real payload's size");
                }
                packet.setPayload(load);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                buffer.put(packet.payload);
            }
            
            @Override
            public long calcSize(SpamPacket packet){
                return packet.payload.length;
            }
        },
        PADDING(7) {
            private void advanceBuffer(ByteBuffer buffer, long length){
                long pos = (long)buffer.position()+length;
                
                if((int)pos != pos) {
                    // well java only supports arrays up to 2GB, if the payload is larger 2GB
                    // we have a problem. Luckily it is not really our problem, because the 
                    // ByteBuffer that was passed into this function does not support data
                    // larger 2GB either. So if the length is larger 2GB we can throw an
                    // BufferUnderflowException.
                    throw new BufferUnderflowException();
                }
                // ignore data, but advance position
                buffer.position((int)pos);
            }
            
            @Override
            public void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) {
                advanceBuffer(buffer,length);
            }
            
            @Override
            public void writeTupleData(ByteBuffer buffer, SpamPacket packet) {
                // we don't need to write data but if you would like to do
                // it anyway, comment the next line and uncomment the loop
                
                advanceBuffer(buffer,calcSize(packet));
                
                // long size = calcSize(packet);
                // for(long i=0 ; i<size ; ++i){
                //     buffer.put((byte) -1);
                // }
            }
            
            @Override
            public long calcSize(SpamPacket packet){
                // calculate how much padding is needed
                long noPadSize = packet.getSizeWithoutPadding(); // this does include the
                
                if(packet.minSize > noPadSize) {
                    return packet.minSize - noPadSize;
                } else {
                    return 0;
                }
            }
        };
        
        TlvTuple(int id) {
            this.id = id;
            this.size = -1;  // size is variable
        }
        
        TlvTuple(int id, long size) {
            this.id = id;
            this.size = size;
        }
        
        public abstract void readTupleData(ByteBuffer buffer, long length, SpamPacket packet) throws DataFormatException;
        
        public abstract void writeTupleData(ByteBuffer buffer, SpamPacket packet);
        
        private final int id;
        private final long size;
        
        public int getId() {
            return id;
        }
        
        public boolean hasFixedSize() {
            return size >= 0;
        }
        
        public long calcSize(SpamPacket packet) {
            // this function needs to be overridden if the 
            // size passed is negative
            if(size < 0) {
                throw new UnsupportedOperationException();
            }
            return size;
        }
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderId()
     */
    public long getSenderId() {
        return senderID;
    }
    
    /**
     * @exception IllegalArgumentException thrown if senderId is negative or 
     *                                     greater 0xFFFFFFFF (max of unsigned int).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setSenderId(long)
     */
    public void setSenderId(long senderId) {
        if((senderId & INT_MASK) != senderId){
            throw new IllegalArgumentException();
        }
        this.senderID = senderId;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getConfiguredPacketsPerSecond()
     */
    public long getConfiguredPacketsPerSecond() {
        return configuredPacketsPerSeconds;
    }
    
    /**
     * @exception IllegalArgumentException thrown if configuredPacketsPerSeconds is negative or 
     *                                     greater 0xFFFFFFFF (max of unsigned int).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setConfiguredPacketsPerSecond(long)
     */
    public void setConfiguredPacketsPerSecond(long configuredPacketsPerSeconds) {
        if((configuredPacketsPerSeconds & INT_MASK) != configuredPacketsPerSeconds){
            throw new IllegalArgumentException();
        }
    
        this.configuredPacketsPerSeconds = configuredPacketsPerSeconds;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSenderMeasuredPacketRate()
     */
    public long getSenderMeasuredPacketRate() {
        return senderMeasuredPacketRate;
    }
    
    /**
     * @exception IllegalArgumentException thrown if senderMeasuredPacketRate is negative or 
     *                                     greater 0xFFFFFFFF (max of unsigned int).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setSenderMeasuredPacketRate(long)
     */
    public void setSenderMeasuredPacketRate(long senderMeasuredPacketRate) {
        if((senderMeasuredPacketRate & INT_MASK) != senderMeasuredPacketRate){
            throw new IllegalArgumentException();
        }
        this.senderMeasuredPacketRate = senderMeasuredPacketRate;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    /**
     * @exception IllegalArgumentException thrown if sequenceNumber is negative or 
     *                                     greater 0xFFFFFFFF (max of unsigned int).
     *                                     
     * @see com.spam.mctool.model.packet.Packet#setSequenceNumber(long)
     */
    public void setSequenceNumber(long sequenceNumber) {
        if((sequenceNumber & INT_MASK) != sequenceNumber){
            throw new IllegalArgumentException();
        }
        this.sequenceNumber = sequenceNumber;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getDispatchTime()
     */
    public long getDispatchTime() {
        return dispatchTime;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setDispatchTime(long)
     */
    public void setDispatchTime(long dispatchTime) {
        this.dispatchTime = dispatchTime;
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setPayload(byte[] date)
     */
    public void setPayload(byte[] data) {
        payload = data.clone();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getPayload()
     */
    public byte[] getPayload() {
        return payload.clone();
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#getSize()
     */
    public long getSize() {
        return getSizeWithoutPadding() + TlvTuple.PADDING.calcSize(this);
    }
    
    /**
     * @see com.spam.mctool.model.packet.Packet#setMinimumSize(long)
     */
    public void setMinimumSize(long size) {
        minSize = size;
    }
}
