package com.spam.mctool.model.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

enum TlvTuple {
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
            if(length > Integer.MAX_VALUE) {
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
            long size = packet.getSizeWithoutPadding(); // this does include the
            
            long minSize = packet.getMinimumSize();
            
            if(minSize > size) {
                return minSize - size;
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
    
    final int id;
    final long size;
    private static final long INT_MASK = 0xFFFFFFFFL;
    
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