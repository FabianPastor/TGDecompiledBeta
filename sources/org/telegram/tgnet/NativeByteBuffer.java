package org.telegram.tgnet;

import com.google.android.exoplayer2.C0016C;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class NativeByteBuffer extends AbstractSerializedData {
    private static final ThreadLocal<NativeByteBuffer> addressWrapper = new C06411();
    protected long address;
    public ByteBuffer buffer;
    private boolean justCalc;
    private int len;
    public boolean reused;

    /* renamed from: org.telegram.tgnet.NativeByteBuffer$1 */
    static class C06411 extends ThreadLocal<NativeByteBuffer> {
        C06411() {
        }

        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(0, true, null);
        }
    }

    public static native long native_getFreeBuffer(int i);

    public static native ByteBuffer native_getJavaByteBuffer(long j);

    public static native int native_limit(long j);

    public static native int native_position(long j);

    public static native void native_reuse(long j);

    /* synthetic */ NativeByteBuffer(int x0, boolean x1, C06411 x2) {
        this(x0, x1);
    }

    public static NativeByteBuffer wrap(long address) {
        NativeByteBuffer result = (NativeByteBuffer) addressWrapper.get();
        if (address != 0) {
            if (!result.reused && BuildVars.LOGS_ENABLED) {
                FileLog.m12e("forgot to reuse?");
            }
            result.address = address;
            result.reused = false;
            result.buffer = native_getJavaByteBuffer(address);
            result.buffer.limit(native_limit(address));
            int position = native_position(address);
            if (position <= result.buffer.limit()) {
                result.buffer.position(position);
            }
            result.buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return result;
    }

    private NativeByteBuffer(int address, boolean wrap) {
        this.reused = true;
    }

    public NativeByteBuffer(int size) throws Exception {
        this.reused = true;
        if (size >= 0) {
            this.address = native_getFreeBuffer(size);
            if (this.address != 0) {
                this.buffer = native_getJavaByteBuffer(this.address);
                this.buffer.position(0);
                this.buffer.limit(size);
                this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                return;
            }
            return;
        }
        throw new Exception("invalid NativeByteBuffer size");
    }

    public NativeByteBuffer(boolean calculate) {
        this.reused = true;
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
            if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.putInt(x);
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write int32 error");
            }
        }
    }

    public void writeInt64(long x) {
        try {
            if (this.justCalc) {
                this.len += 8;
            } else {
                this.buffer.putLong(x);
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write int64 error");
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
            if (this.justCalc) {
                this.len += b.length;
            } else {
                this.buffer.put(b);
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write raw error");
            }
        }
    }

    public void writeBytes(byte[] b, int offset, int count) {
        try {
            if (this.justCalc) {
                this.len += count;
            } else {
                this.buffer.put(b, offset, count);
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write raw error");
            }
        }
    }

    public void writeByte(int i) {
        writeByte((byte) i);
    }

    public void writeByte(byte b) {
        try {
            if (this.justCalc) {
                this.len++;
            } else {
                this.buffer.put(b);
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write byte error");
            }
        }
    }

    public void writeString(String s) {
        try {
            writeByteArray(s.getBytes(C0016C.UTF8_NAME));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write string error");
            }
        }
    }

    public void writeByteArray(byte[] b, int offset, int count) {
        if (count <= 253) {
            try {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) count);
                }
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m12e("write byte array error");
                    return;
                }
                return;
            }
        } else if (this.justCalc) {
            this.len += 4;
        } else {
            this.buffer.put((byte) -2);
            this.buffer.put((byte) count);
            this.buffer.put((byte) (count >> 8));
            this.buffer.put((byte) (count >> 16));
        }
        if (this.justCalc) {
            this.len += count;
        } else {
            this.buffer.put(b, offset, count);
        }
        int i = count <= 253 ? 1 : 4;
        while ((count + i) % 4 != 0) {
            if (this.justCalc) {
                this.len++;
            } else {
                this.buffer.put((byte) 0);
            }
            i++;
        }
    }

    public void writeByteArray(byte[] b) {
        try {
            if (b.length <= 253) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) b.length);
                }
            } else if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) b.length);
                this.buffer.put((byte) (b.length >> 8));
                this.buffer.put((byte) (b.length >> 16));
            }
            if (this.justCalc) {
                this.len += b.length;
            } else {
                this.buffer.put(b);
            }
            int i = b.length <= 253 ? 1 : 4;
            while ((b.length + i) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) 0);
                }
                i++;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write byte array error");
            }
        }
    }

    public void writeDouble(double d) {
        try {
            writeInt64(Double.doubleToRawLongBits(d));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("write double error");
            }
        }
    }

    public void writeByteBuffer(NativeByteBuffer b) {
        try {
            int l = b.limit();
            if (l <= 253) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) l);
                }
            } else if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) l);
                this.buffer.put((byte) (l >> 8));
                this.buffer.put((byte) (l >> 16));
            }
            if (this.justCalc) {
                this.len += l;
            } else {
                b.rewind();
                this.buffer.put(b.buffer);
            }
            int i = l <= 253 ? 1 : 4;
            while ((l + i) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) 0);
                }
                i++;
            }
        } catch (Throwable e) {
            FileLog.m14e(e);
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
        return b >= (byte) 0 ? b : b + 256;
    }

    public int length() {
        if (this.justCalc) {
            return this.len;
        }
        return this.buffer.position();
    }

    public void skip(int count) {
        if (count != 0) {
            if (this.justCalc) {
                this.len += count;
            } else {
                this.buffer.position(this.buffer.position() + count);
            }
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
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read int32 error");
            }
            return 0;
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
        if (exception) {
            throw new RuntimeException("Not bool value!");
        } else if (!BuildVars.LOGS_ENABLED) {
            return false;
        } else {
            FileLog.m12e("Not bool value!");
            return false;
        }
    }

    public long readInt64(boolean exception) {
        try {
            return this.buffer.getLong();
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read int64 error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read int64 error");
            }
            return 0;
        }
    }

    public void readBytes(byte[] b, boolean exception) {
        try {
            this.buffer.get(b);
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read raw error", e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read raw error");
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
                FileLog.m12e("read raw error");
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
                l = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            byte[] b = new byte[l];
            this.buffer.get(b);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return new String(b, C0016C.UTF8_NAME);
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read string error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read string error");
            }
            position(startReadPosition);
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public byte[] readByteArray(boolean exception) {
        int sl = 1;
        try {
            int l = getIntFromByte(this.buffer.get());
            if (l >= 254) {
                l = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            byte[] bArr = new byte[l];
            this.buffer.get(bArr);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return bArr;
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read byte array error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read byte array error");
            }
            return new byte[0];
        }
    }

    public NativeByteBuffer readByteBuffer(boolean exception) {
        int sl = 1;
        try {
            int l = getIntFromByte(this.buffer.get());
            if (l >= 254) {
                l = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                sl = 4;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(l);
            int old = this.buffer.limit();
            this.buffer.limit(this.buffer.position() + l);
            nativeByteBuffer.buffer.put(this.buffer);
            this.buffer.limit(old);
            nativeByteBuffer.buffer.position(0);
            for (int i = sl; (l + i) % 4 != 0; i++) {
                this.buffer.get();
            }
            return nativeByteBuffer;
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read byte array error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read byte array error");
            }
            return null;
        }
    }

    public double readDouble(boolean exception) {
        try {
            return Double.longBitsToDouble(readInt64(exception));
        } catch (Exception e) {
            if (exception) {
                throw new RuntimeException("read double error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m12e("read double error");
            }
            return 0.0d;
        }
    }

    public void reuse() {
        if (this.address != 0) {
            this.reused = true;
            native_reuse(this.address);
        }
    }

    public int remaining() {
        return this.buffer.remaining();
    }
}
