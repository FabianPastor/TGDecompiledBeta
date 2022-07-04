package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class NativeByteBuffer extends AbstractSerializedData {
    private static final ThreadLocal<LinkedList<NativeByteBuffer>> addressWrappers = new ThreadLocal<LinkedList<NativeByteBuffer>>() {
        /* access modifiers changed from: protected */
        public LinkedList<NativeByteBuffer> initialValue() {
            return new LinkedList<>();
        }
    };
    protected long address;
    public ByteBuffer buffer;
    private boolean justCalc;
    private int len;
    public boolean reused = true;

    public static native long native_getFreeBuffer(int i);

    public static native ByteBuffer native_getJavaByteBuffer(long j);

    public static native int native_limit(long j);

    public static native int native_position(long j);

    public static native void native_reuse(long j);

    public static NativeByteBuffer wrap(long address2) {
        if (address2 == 0) {
            return null;
        }
        NativeByteBuffer result = addressWrappers.get().poll();
        if (result == null) {
            result = new NativeByteBuffer(0, true);
        }
        result.address = address2;
        result.reused = false;
        ByteBuffer native_getJavaByteBuffer = native_getJavaByteBuffer(address2);
        result.buffer = native_getJavaByteBuffer;
        native_getJavaByteBuffer.limit(native_limit(address2));
        int position = native_position(address2);
        if (position <= result.buffer.limit()) {
            result.buffer.position(position);
        }
        result.buffer.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }

    private NativeByteBuffer(int address2, boolean wrap) {
    }

    public NativeByteBuffer(int size) throws Exception {
        if (size >= 0) {
            long native_getFreeBuffer = native_getFreeBuffer(size);
            this.address = native_getFreeBuffer;
            if (native_getFreeBuffer != 0) {
                ByteBuffer native_getJavaByteBuffer = native_getJavaByteBuffer(native_getFreeBuffer);
                this.buffer = native_getJavaByteBuffer;
                native_getJavaByteBuffer.position(0);
                this.buffer.limit(size);
                this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                return;
            }
            return;
        }
        throw new Exception("invalid NativeByteBuffer size");
    }

    public NativeByteBuffer(boolean calculate) {
        this.justCalc = calculate;
    }

    public int position() {
        return this.buffer.position();
    }

    public void position(int position) {
        this.buffer.position(position);
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public int limit() {
        return this.buffer.limit();
    }

    public void limit(int limit) {
        this.buffer.limit(limit);
    }

    public void put(ByteBuffer buff) {
        this.buffer.put(buff);
    }

    public void rewind() {
        if (this.justCalc) {
            this.len = 0;
        } else {
            this.buffer.rewind();
        }
    }

    public void compact() {
        this.buffer.compact();
    }

    public boolean hasRemaining() {
        return this.buffer.hasRemaining();
    }

    public void writeInt32(int x) {
        try {
            if (!this.justCalc) {
                this.buffer.putInt(x);
            } else {
                this.len += 4;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write int32 error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeInt64(long x) {
        try {
            if (!this.justCalc) {
                this.buffer.putLong(x);
            } else {
                this.len += 8;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write int64 error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeBool(boolean value) {
        if (this.justCalc) {
            this.len += 4;
        } else if (value) {
            writeInt32(-NUM);
        } else {
            writeInt32(-NUM);
        }
    }

    public void writeBytes(byte[] b) {
        try {
            if (!this.justCalc) {
                this.buffer.put(b);
            } else {
                this.len += b.length;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeBytes(byte[] b, int offset, int count) {
        try {
            if (!this.justCalc) {
                this.buffer.put(b, offset, count);
            } else {
                this.len += count;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeByte(int i) {
        writeByte((byte) i);
    }

    public void writeByte(byte b) {
        try {
            if (!this.justCalc) {
                this.buffer.put(b);
            } else {
                this.len++;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeString(String s) {
        try {
            writeByteArray(s.getBytes("UTF-8"));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write string error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeByteArray(byte[] b, int offset, int count) {
        if (count <= 253) {
            try {
                if (!this.justCalc) {
                    this.buffer.put((byte) count);
                } else {
                    this.len++;
                }
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write byte array error");
                    FileLog.e((Throwable) e);
                    return;
                }
                return;
            }
        } else if (!this.justCalc) {
            this.buffer.put((byte) -2);
            this.buffer.put((byte) count);
            this.buffer.put((byte) (count >> 8));
            this.buffer.put((byte) (count >> 16));
        } else {
            this.len += 4;
        }
        if (!this.justCalc) {
            this.buffer.put(b, offset, count);
        } else {
            this.len += count;
        }
        for (int i = count <= 253 ? 1 : 4; (count + i) % 4 != 0; i++) {
            if (!this.justCalc) {
                this.buffer.put((byte) 0);
            } else {
                this.len++;
            }
        }
    }

    public void writeByteArray(byte[] b) {
        try {
            if (b.length <= 253) {
                if (!this.justCalc) {
                    this.buffer.put((byte) b.length);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) b.length);
                this.buffer.put((byte) (b.length >> 8));
                this.buffer.put((byte) (b.length >> 16));
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                this.buffer.put(b);
            } else {
                this.len += b.length;
            }
            for (int i = b.length <= 253 ? 1 : 4; (b.length + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.buffer.put((byte) 0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte array error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeDouble(double d) {
        try {
            writeInt64(Double.doubleToRawLongBits(d));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write double error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeByteBuffer(NativeByteBuffer b) {
        try {
            int l = b.limit();
            if (l <= 253) {
                if (!this.justCalc) {
                    this.buffer.put((byte) l);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) l);
                this.buffer.put((byte) (l >> 8));
                this.buffer.put((byte) (l >> 16));
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                b.rewind();
                this.buffer.put(b.buffer);
            } else {
                this.len += l;
            }
            for (int i = l <= 253 ? 1 : 4; (l + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.buffer.put((byte) 0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void writeBytes(NativeByteBuffer b) {
        if (this.justCalc) {
            this.len += b.limit();
            return;
        }
        b.rewind();
        this.buffer.put(b.buffer);
    }

    public int getIntFromByte(byte b) {
        return b >= 0 ? b : b + 256;
    }

    public int length() {
        if (!this.justCalc) {
            return this.buffer.position();
        }
        return this.len;
    }

    public void skip(int count) {
        if (count != 0) {
            if (!this.justCalc) {
                ByteBuffer byteBuffer = this.buffer;
                byteBuffer.position(byteBuffer.position() + count);
                return;
            }
            this.len += count;
        }
    }

    public int getPosition() {
        return this.buffer.position();
    }

    public int readInt32(boolean exception) {
        try {
            return this.buffer.getInt();
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read int32 error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return 0;
            } else {
                FileLog.e("read int32 error");
                FileLog.e((Throwable) e);
                return 0;
            }
        }
    }

    public boolean readBool(boolean exception) {
        int consructor = readInt32(exception);
        if (consructor == -NUM) {
            return true;
        }
        if (consructor == -NUM) {
            return false;
        }
        if (!exception) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Not bool value!");
            }
            return false;
        }
        throw new RuntimeException("Not bool value!");
    }

    public long readInt64(boolean exception) {
        try {
            return this.buffer.getLong();
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read int64 error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return 0;
            } else {
                FileLog.e("read int64 error");
                FileLog.e((Throwable) e);
                return 0;
            }
        }
    }

    public void readBytes(byte[] b, boolean exception) {
        try {
            this.buffer.get(b);
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read raw error", e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read raw error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void readBytes(byte[] b, int offset, int count, boolean exception) {
        try {
            this.buffer.get(b, offset, count);
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read raw error", e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read raw error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public byte[] readData(int count, boolean exception) {
        byte[] arr = new byte[count];
        readBytes(arr, exception);
        return arr;
    }

    public String readString(boolean exception) {
        int startReadPosition = getPosition();
        int sl = 1;
        try {
            int l = getIntFromByte(this.buffer.get());
            if (l >= 254) {
                l = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            byte[] b = new byte[l];
            this.buffer.get(b);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return new String(b, "UTF-8");
        } catch (Exception e) {
            if (!exception) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("read string error");
                    FileLog.e((Throwable) e);
                }
                position(startReadPosition);
                return "";
            }
            throw new RuntimeException("read string error", e);
        }
    }

    public byte[] readByteArray(boolean exception) {
        int sl = 1;
        try {
            int l = getIntFromByte(this.buffer.get());
            if (l >= 254) {
                l = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            byte[] b = new byte[l];
            this.buffer.get(b);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return b;
        } catch (Exception e) {
            if (!exception) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("read byte array error");
                    FileLog.e((Throwable) e);
                }
                return new byte[0];
            }
            throw new RuntimeException("read byte array error", e);
        }
    }

    public NativeByteBuffer readByteBuffer(boolean exception) {
        int sl = 1;
        try {
            int l = getIntFromByte(this.buffer.get());
            if (l >= 254) {
                l = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            NativeByteBuffer b = new NativeByteBuffer(l);
            int old = this.buffer.limit();
            ByteBuffer byteBuffer = this.buffer;
            byteBuffer.limit(byteBuffer.position() + l);
            b.buffer.put(this.buffer);
            this.buffer.limit(old);
            b.buffer.position(0);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return b;
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read byte array error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return null;
            } else {
                FileLog.e("read byte array error");
                FileLog.e((Throwable) e);
                return null;
            }
        }
    }

    public double readDouble(boolean exception) {
        try {
            return Double.longBitsToDouble(readInt64(exception));
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read double error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return 0.0d;
            } else {
                FileLog.e("read double error");
                FileLog.e((Throwable) e);
                return 0.0d;
            }
        }
    }

    public void reuse() {
        if (this.address != 0) {
            addressWrappers.get().add(this);
            this.reused = true;
            native_reuse(this.address);
        }
    }

    public int remaining() {
        return this.buffer.remaining();
    }
}
