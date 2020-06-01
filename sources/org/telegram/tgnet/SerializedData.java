package org.telegram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class SerializedData extends AbstractSerializedData {
    private DataInputStream in;
    private ByteArrayInputStream inbuf;
    protected boolean isOut;
    private boolean justCalc;
    private int len;
    private DataOutputStream out;
    private ByteArrayOutputStream outbuf;

    public NativeByteBuffer readByteBuffer(boolean z) {
        return null;
    }

    public void writeByteBuffer(NativeByteBuffer nativeByteBuffer) {
    }

    public SerializedData() {
        this.isOut = true;
        this.justCalc = false;
        this.outbuf = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.outbuf);
    }

    public SerializedData(boolean z) {
        this.isOut = true;
        this.justCalc = false;
        if (!z) {
            this.outbuf = new ByteArrayOutputStream();
            this.out = new DataOutputStream(this.outbuf);
        }
        this.justCalc = z;
        this.len = 0;
    }

    public SerializedData(int i) {
        this.isOut = true;
        this.justCalc = false;
        this.outbuf = new ByteArrayOutputStream(i);
        this.out = new DataOutputStream(this.outbuf);
    }

    public SerializedData(byte[] bArr) {
        this.isOut = true;
        this.justCalc = false;
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(bArr);
        this.in = new DataInputStream(this.inbuf);
        this.len = 0;
    }

    public void cleanup() {
        try {
            if (this.inbuf != null) {
                this.inbuf.close();
                this.inbuf = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            if (this.outbuf != null) {
                this.outbuf.close();
                this.outbuf = null;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        try {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
    }

    public void writeInt32(int i) {
        if (!this.justCalc) {
            writeInt32(i, this.out);
        } else {
            this.len += 4;
        }
    }

    private void writeInt32(int i, DataOutputStream dataOutputStream) {
        int i2 = 0;
        while (i2 < 4) {
            try {
                dataOutputStream.write(i >> (i2 * 8));
                i2++;
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int32 error");
                    FileLog.e((Throwable) e);
                    return;
                }
                return;
            }
        }
    }

    public void writeInt64(long j) {
        if (!this.justCalc) {
            writeInt64(j, this.out);
        } else {
            this.len += 8;
        }
    }

    private void writeInt64(long j, DataOutputStream dataOutputStream) {
        int i = 0;
        while (i < 8) {
            try {
                dataOutputStream.write((int) (j >> (i * 8)));
                i++;
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int64 error");
                    FileLog.e((Throwable) e);
                    return;
                }
                return;
            }
        }
    }

    public void writeBool(boolean z) {
        if (this.justCalc) {
            this.len += 4;
        } else if (z) {
            writeInt32(-NUM);
        } else {
            writeInt32(-NUM);
        }
    }

    public void writeBytes(byte[] bArr) {
        try {
            if (!this.justCalc) {
                this.out.write(bArr);
            } else {
                this.len += bArr.length;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeBytes(byte[] bArr, int i, int i2) {
        try {
            if (!this.justCalc) {
                this.out.write(bArr, i, i2);
            } else {
                this.len += i2;
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write bytes error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeByte(int i) {
        try {
            if (!this.justCalc) {
                this.out.writeByte((byte) i);
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

    public void writeByte(byte b) {
        try {
            if (!this.justCalc) {
                this.out.writeByte(b);
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

    public void writeByteArray(byte[] bArr) {
        try {
            if (bArr.length <= 253) {
                if (!this.justCalc) {
                    this.out.write(bArr.length);
                } else {
                    this.len++;
                }
            } else if (!this.justCalc) {
                this.out.write(254);
                this.out.write(bArr.length);
                this.out.write(bArr.length >> 8);
                this.out.write(bArr.length >> 16);
            } else {
                this.len += 4;
            }
            if (!this.justCalc) {
                this.out.write(bArr);
            } else {
                this.len += bArr.length;
            }
            for (int i = bArr.length <= 253 ? 1 : 4; (bArr.length + i) % 4 != 0; i++) {
                if (!this.justCalc) {
                    this.out.write(0);
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

    public void writeString(String str) {
        try {
            writeByteArray(str.getBytes("UTF-8"));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write string error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public void writeByteArray(byte[] bArr, int i, int i2) {
        if (i2 <= 253) {
            try {
                if (!this.justCalc) {
                    this.out.write(i2);
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
            this.out.write(254);
            this.out.write(i2);
            this.out.write(i2 >> 8);
            this.out.write(i2 >> 16);
        } else {
            this.len += 4;
        }
        if (!this.justCalc) {
            this.out.write(bArr, i, i2);
        } else {
            this.len += i2;
        }
        for (int i3 = i2 <= 253 ? 1 : 4; (i2 + i3) % 4 != 0; i3++) {
            if (!this.justCalc) {
                this.out.write(0);
            } else {
                this.len++;
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

    public void writeFloat(float f) {
        try {
            writeInt32(Float.floatToIntBits(f));
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write float error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public int length() {
        if (!this.justCalc) {
            return this.isOut ? this.outbuf.size() : this.inbuf.available();
        }
        return this.len;
    }

    public byte[] toByteArray() {
        return this.outbuf.toByteArray();
    }

    public void skip(int i) {
        if (i != 0) {
            if (!this.justCalc) {
                DataInputStream dataInputStream = this.in;
                if (dataInputStream != null) {
                    try {
                        dataInputStream.skipBytes(i);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else {
                this.len += i;
            }
        }
    }

    public int getPosition() {
        return this.len;
    }

    public boolean readBool(boolean z) {
        int readInt32 = readInt32(z);
        if (readInt32 == -NUM) {
            return true;
        }
        if (readInt32 == -NUM) {
            return false;
        }
        if (!z) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Not bool value!");
            }
            return false;
        }
        throw new RuntimeException("Not bool value!");
    }

    public byte readByte(boolean z) {
        try {
            byte readByte = this.in.readByte();
            this.len++;
            return readByte;
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read byte error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return 0;
            } else {
                FileLog.e("read byte error");
                FileLog.e((Throwable) e);
                return 0;
            }
        }
    }

    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.in.read(bArr);
            this.len += bArr.length;
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read bytes error", e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("read bytes error");
                FileLog.e((Throwable) e);
            }
        }
    }

    public byte[] readData(int i, boolean z) {
        byte[] bArr = new byte[i];
        readBytes(bArr, z);
        return bArr;
    }

    public String readString(boolean z) {
        int i;
        try {
            int read = this.in.read();
            this.len++;
            if (read >= 254) {
                read = this.in.read() | (this.in.read() << 8) | (this.in.read() << 16);
                this.len += 3;
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[read];
            this.in.read(bArr);
            this.len++;
            while ((read + i) % 4 != 0) {
                this.in.read();
                this.len++;
                i++;
            }
            return new String(bArr, "UTF-8");
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read string error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return null;
            } else {
                FileLog.e("read string error");
                FileLog.e((Throwable) e);
                return null;
            }
        }
    }

    public byte[] readByteArray(boolean z) {
        int i;
        try {
            int read = this.in.read();
            this.len++;
            if (read >= 254) {
                read = this.in.read() | (this.in.read() << 8) | (this.in.read() << 16);
                this.len += 3;
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[read];
            this.in.read(bArr);
            this.len++;
            while ((read + i) % 4 != 0) {
                this.in.read();
                this.len++;
                i++;
            }
            return bArr;
        } catch (Exception e) {
            if (z) {
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

    public double readDouble(boolean z) {
        try {
            return Double.longBitsToDouble(readInt64(z));
        } catch (Exception e) {
            if (z) {
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

    public float readFloat(boolean z) {
        try {
            return Float.intBitsToFloat(readInt32(z));
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException("read float error", e);
            } else if (!BuildVars.LOGS_ENABLED) {
                return 0.0f;
            } else {
                FileLog.e("read float error");
                FileLog.e((Throwable) e);
                return 0.0f;
            }
        }
    }

    public int readInt32(boolean z) {
        int i = 0;
        int i2 = 0;
        while (i < 4) {
            try {
                i2 |= this.in.read() << (i * 8);
                this.len++;
                i++;
            } catch (Exception e) {
                if (!z) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("read int32 error");
                        FileLog.e((Throwable) e);
                    }
                    return 0;
                }
                throw new RuntimeException("read int32 error", e);
            }
        }
        return i2;
    }

    public long readInt64(boolean z) {
        int i = 0;
        long j = 0;
        while (i < 8) {
            try {
                j |= ((long) this.in.read()) << (i * 8);
                this.len++;
                i++;
            } catch (Exception e) {
                if (!z) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("read int64 error");
                        FileLog.e((Throwable) e);
                    }
                    return 0;
                }
                throw new RuntimeException("read int64 error", e);
            }
        }
        return j;
    }

    public int remaining() {
        try {
            return this.in.available();
        } catch (Exception unused) {
            return Integer.MAX_VALUE;
        }
    }
}
