package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
/* loaded from: classes.dex */
public class NativeByteBuffer extends AbstractSerializedData {
    private static final ThreadLocal<LinkedList<NativeByteBuffer>> addressWrappers = new ThreadLocal<LinkedList<NativeByteBuffer>>() { // from class: org.telegram.tgnet.NativeByteBuffer.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
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

    public int getIntFromByte(byte b) {
        return b >= 0 ? b : b + 256;
    }

    public static NativeByteBuffer wrap(long j) {
        if (j != 0) {
            NativeByteBuffer poll = addressWrappers.get().poll();
            if (poll == null) {
                poll = new NativeByteBuffer(0, true);
            }
            poll.address = j;
            poll.reused = false;
            ByteBuffer native_getJavaByteBuffer = native_getJavaByteBuffer(j);
            poll.buffer = native_getJavaByteBuffer;
            native_getJavaByteBuffer.limit(native_limit(j));
            int native_position = native_position(j);
            if (native_position <= poll.buffer.limit()) {
                poll.buffer.position(native_position);
            }
            poll.buffer.order(ByteOrder.LITTLE_ENDIAN);
            return poll;
        }
        return null;
    }

    private NativeByteBuffer(int i, boolean z) {
    }

    public NativeByteBuffer(int i) throws Exception {
        if (i >= 0) {
            long native_getFreeBuffer = native_getFreeBuffer(i);
            this.address = native_getFreeBuffer;
            if (native_getFreeBuffer == 0) {
                return;
            }
            ByteBuffer native_getJavaByteBuffer = native_getJavaByteBuffer(native_getFreeBuffer);
            this.buffer = native_getJavaByteBuffer;
            native_getJavaByteBuffer.position(0);
            this.buffer.limit(i);
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
            return;
        }
        throw new Exception("invalid NativeByteBuffer size");
    }

    public NativeByteBuffer(boolean z) {
        this.justCalc = z;
    }

    public int position() {
        return this.buffer.position();
    }

    public void position(int i) {
        this.buffer.position(i);
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public int limit() {
        return this.buffer.limit();
    }

    public void limit(int i) {
        this.buffer.limit(i);
    }

    public void put(ByteBuffer byteBuffer) {
        this.buffer.put(byteBuffer);
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

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeInt32(int i) {
        try {
            if (!this.justCalc) {
                this.buffer.putInt(i);
            } else {
                this.len += 4;
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write int32 error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeInt64(long j) {
        try {
            if (!this.justCalc) {
                this.buffer.putLong(j);
            } else {
                this.len += 8;
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write int64 error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeBool(boolean z) {
        if (this.justCalc) {
            this.len += 4;
        } else if (z) {
            writeInt32(-NUM);
        } else {
            writeInt32(-NUM);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeBytes(byte[] bArr) {
        try {
            if (!this.justCalc) {
                this.buffer.put(bArr);
            } else {
                this.len += bArr.length;
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write raw error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeBytes(byte[] bArr, int i, int i2) {
        try {
            if (!this.justCalc) {
                this.buffer.put(bArr, i, i2);
            } else {
                this.len += i2;
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write raw error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeByte(int i) {
        writeByte((byte) i);
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeByte(byte b) {
        try {
            if (!this.justCalc) {
                this.buffer.put(b);
            } else {
                this.len++;
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write byte error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeString(String str) {
        if (str == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write string null");
                FileLog.e(new Throwable());
            }
            str = "";
        }
        try {
            writeByteArray(str.getBytes("UTF-8"));
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write string error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeByteArray(byte[] bArr, int i, int i2) {
        try {
            if (i2 <= 253) {
                if (!this.justCalc) {
                    this.buffer.put((byte) i2);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) i2);
                this.buffer.put((byte) (i2 >> 8));
                this.buffer.put((byte) (i2 >> 16));
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                this.buffer.put(bArr, i, i2);
            } else {
                this.len += i2;
            }
            for (int i3 = i2 <= 253 ? 1 : 4; (i2 + i3) % 4 != 0; i3++) {
                if (!this.justCalc) {
                    this.buffer.put((byte) 0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write byte array error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeByteArray(byte[] bArr) {
        try {
            if (bArr.length <= 253) {
                if (!this.justCalc) {
                    this.buffer.put((byte) bArr.length);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) bArr.length);
                this.buffer.put((byte) (bArr.length >> 8));
                this.buffer.put((byte) (bArr.length >> 16));
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                this.buffer.put(bArr);
            } else {
                this.len += bArr.length;
            }
            for (int i = bArr.length <= 253 ? 1 : 4; (bArr.length + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.buffer.put((byte) 0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write byte array error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeDouble(double d) {
        try {
            writeInt64(Double.doubleToRawLongBits(d));
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("write double error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void writeByteBuffer(NativeByteBuffer nativeByteBuffer) {
        try {
            int limit = nativeByteBuffer.limit();
            if (limit <= 253) {
                if (!this.justCalc) {
                    this.buffer.put((byte) limit);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) limit);
                this.buffer.put((byte) (limit >> 8));
                this.buffer.put((byte) (limit >> 16));
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                nativeByteBuffer.rewind();
                this.buffer.put(nativeByteBuffer.buffer);
            } else {
                this.len += limit;
            }
            for (int i = limit <= 253 ? 1 : 4; (limit + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.buffer.put((byte) 0);
                } else {
                    this.len++;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void writeBytes(NativeByteBuffer nativeByteBuffer) {
        if (this.justCalc) {
            this.len += nativeByteBuffer.limit();
            return;
        }
        nativeByteBuffer.rewind();
        this.buffer.put(nativeByteBuffer.buffer);
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public int length() {
        if (!this.justCalc) {
            return this.buffer.position();
        }
        return this.len;
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void skip(int i) {
        if (i == 0) {
            return;
        }
        if (!this.justCalc) {
            ByteBuffer byteBuffer = this.buffer;
            byteBuffer.position(byteBuffer.position() + i);
            return;
        }
        this.len += i;
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public int getPosition() {
        return this.buffer.position();
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public int readInt32(boolean z) {
        try {
            return this.buffer.getInt();
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read int32 error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return 0;
            }
            FileLog.e("read int32 error");
            FileLog.e(e);
            return 0;
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public boolean readBool(boolean z) {
        int readInt32 = readInt32(z);
        if (readInt32 == -NUM) {
            return true;
        }
        if (readInt32 == -NUM) {
            return false;
        }
        if (z) {
            throw new RuntimeException("Not bool value!");
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Not bool value!");
        }
        return false;
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public long readInt64(boolean z) {
        try {
            return this.buffer.getLong();
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read int64 error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return 0L;
            }
            FileLog.e("read int64 error");
            FileLog.e(e);
            return 0L;
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.buffer.get(bArr);
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read raw error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("read raw error");
            FileLog.e(e);
        }
    }

    public void readBytes(byte[] bArr, int i, int i2, boolean z) {
        try {
            this.buffer.get(bArr, i, i2);
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read raw error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("read raw error");
            FileLog.e(e);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public byte[] readData(int i, boolean z) {
        byte[] bArr = new byte[i];
        readBytes(bArr, z);
        return bArr;
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public String readString(boolean z) {
        int i;
        int position = getPosition();
        try {
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[intFromByte];
            this.buffer.get(bArr);
            while ((intFromByte + i) % 4 != 0) {
                this.buffer.get();
                i++;
            }
            return new String(bArr, "UTF-8");
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read string error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read string error");
                FileLog.e(e);
            }
            position(position);
            return "";
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public byte[] readByteArray(boolean z) {
        int i;
        try {
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[intFromByte];
            this.buffer.get(bArr);
            while ((intFromByte + i) % 4 != 0) {
                this.buffer.get();
                i++;
            }
            return bArr;
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read byte array error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read byte array error");
                FileLog.e(e);
            }
            return new byte[0];
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public NativeByteBuffer readByteBuffer(boolean z) {
        int i;
        try {
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(intFromByte);
            int limit = this.buffer.limit();
            ByteBuffer byteBuffer = this.buffer;
            byteBuffer.limit(byteBuffer.position() + intFromByte);
            nativeByteBuffer.buffer.put(this.buffer);
            this.buffer.limit(limit);
            nativeByteBuffer.buffer.position(0);
            while ((intFromByte + i) % 4 != 0) {
                this.buffer.get();
                i++;
            }
            return nativeByteBuffer;
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read byte array error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return null;
            }
            FileLog.e("read byte array error");
            FileLog.e(e);
            return null;
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public double readDouble(boolean z) {
        try {
            return Double.longBitsToDouble(readInt64(z));
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read double error", e);
            }
            if (!BuildVars.LOGS_ENABLED) {
                return 0.0d;
            }
            FileLog.e("read double error");
            FileLog.e(e);
            return 0.0d;
        }
    }

    public void reuse() {
        if (this.address != 0) {
            addressWrappers.get().add(this);
            this.reused = true;
            native_reuse(this.address);
        }
    }

    @Override // org.telegram.tgnet.AbstractSerializedData
    public int remaining() {
        return this.buffer.remaining();
    }
}
