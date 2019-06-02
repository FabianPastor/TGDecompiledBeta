package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class NativeByteBuffer extends AbstractSerializedData {
    private static final ThreadLocal<NativeByteBuffer> addressWrapper = new ThreadLocal<NativeByteBuffer>() {
        /* Access modifiers changed, original: protected */
        public NativeByteBuffer initialValue() {
            return new NativeByteBuffer(0, true, null);
        }
    };
    protected long address;
    public ByteBuffer buffer;
    private boolean justCalc;
    private int len;
    public boolean reused;

    public static native long native_getFreeBuffer(int i);

    public static native ByteBuffer native_getJavaByteBuffer(long j);

    public static native int native_limit(long j);

    public static native int native_position(long j);

    public static native void native_reuse(long j);

    public int getIntFromByte(byte b) {
        return b >= (byte) 0 ? b : b + 256;
    }

    /* synthetic */ NativeByteBuffer(int i, boolean z, AnonymousClass1 anonymousClass1) {
        this(i, z);
    }

    public static NativeByteBuffer wrap(long j) {
        NativeByteBuffer nativeByteBuffer = (NativeByteBuffer) addressWrapper.get();
        if (j != 0) {
            if (!nativeByteBuffer.reused && BuildVars.LOGS_ENABLED) {
                FileLog.e("forgot to reuse?");
            }
            nativeByteBuffer.address = j;
            nativeByteBuffer.reused = false;
            nativeByteBuffer.buffer = native_getJavaByteBuffer(j);
            nativeByteBuffer.buffer.limit(native_limit(j));
            int native_position = native_position(j);
            if (native_position <= nativeByteBuffer.buffer.limit()) {
                nativeByteBuffer.buffer.position(native_position);
            }
            nativeByteBuffer.buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return nativeByteBuffer;
    }

    private NativeByteBuffer(int i, boolean z) {
        this.reused = true;
    }

    public NativeByteBuffer(int i) throws Exception {
        this.reused = true;
        if (i >= 0) {
            this.address = native_getFreeBuffer(i);
            long j = this.address;
            if (j != 0) {
                this.buffer = native_getJavaByteBuffer(j);
                this.buffer.position(0);
                this.buffer.limit(i);
                this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                return;
            }
            return;
        }
        throw new Exception("invalid NativeByteBuffer size");
    }

    public NativeByteBuffer(boolean z) {
        this.reused = true;
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

    public void writeInt32(int i) {
        try {
            if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.putInt(i);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write int32 error");
            }
        }
    }

    public void writeInt64(long j) {
        try {
            if (this.justCalc) {
                this.len += 8;
            } else {
                this.buffer.putLong(j);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write int64 error");
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
            if (this.justCalc) {
                this.len += bArr.length;
            } else {
                this.buffer.put(bArr);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
            }
        }
    }

    public void writeBytes(byte[] bArr, int i, int i2) {
        try {
            if (this.justCalc) {
                this.len += i2;
            } else {
                this.buffer.put(bArr, i, i2);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write raw error");
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
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
            }
        }
    }

    public void writeString(String str) {
        try {
            writeByteArray(str.getBytes("UTF-8"));
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write string error");
            }
        }
    }

    public void writeByteArray(byte[] bArr, int i, int i2) {
        if (i2 <= 253) {
            try {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) i2);
                }
            } catch (Exception unused) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write byte array error");
                    return;
                }
                return;
            }
        } else if (this.justCalc) {
            this.len += 4;
        } else {
            this.buffer.put((byte) -2);
            this.buffer.put((byte) i2);
            this.buffer.put((byte) (i2 >> 8));
            this.buffer.put((byte) (i2 >> 16));
        }
        if (this.justCalc) {
            this.len += i2;
        } else {
            this.buffer.put(bArr, i, i2);
        }
        int i3 = i2 <= 253 ? 1 : 4;
        while ((i2 + i3) % 4 != 0) {
            if (this.justCalc) {
                this.len++;
            } else {
                this.buffer.put((byte) 0);
            }
            i3++;
        }
    }

    public void writeByteArray(byte[] bArr) {
        try {
            if (bArr.length <= 253) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) bArr.length);
                }
            } else if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) bArr.length);
                this.buffer.put((byte) (bArr.length >> 8));
                this.buffer.put((byte) (bArr.length >> 16));
            }
            if (this.justCalc) {
                this.len += bArr.length;
            } else {
                this.buffer.put(bArr);
            }
            int i = bArr.length <= 253 ? 1 : 4;
            while ((bArr.length + i) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) 0);
                }
                i++;
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte array error");
            }
        }
    }

    public void writeDouble(double d) {
        try {
            writeInt64(Double.doubleToRawLongBits(d));
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write double error");
            }
        }
    }

    public void writeByteBuffer(NativeByteBuffer nativeByteBuffer) {
        try {
            int limit = nativeByteBuffer.limit();
            if (limit <= 253) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) limit);
                }
            } else if (this.justCalc) {
                this.len += 4;
            } else {
                this.buffer.put((byte) -2);
                this.buffer.put((byte) limit);
                this.buffer.put((byte) (limit >> 8));
                this.buffer.put((byte) (limit >> 16));
            }
            if (this.justCalc) {
                this.len += limit;
            } else {
                nativeByteBuffer.rewind();
                this.buffer.put(nativeByteBuffer.buffer);
            }
            int i = limit <= 253 ? 1 : 4;
            while ((limit + i) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) 0);
                }
                i++;
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

    public int length() {
        if (this.justCalc) {
            return this.len;
        }
        return this.buffer.position();
    }

    public void skip(int i) {
        if (i != 0) {
            if (this.justCalc) {
                this.len += i;
            } else {
                ByteBuffer byteBuffer = this.buffer;
                byteBuffer.position(byteBuffer.position() + i);
            }
        }
    }

    public int getPosition() {
        return this.buffer.position();
    }

    public int readInt32(boolean z) {
        try {
            z = this.buffer.getInt();
            return z;
        } catch (Exception e) {
            String str = "read int32 error";
            if (z) {
                throw new RuntimeException(str, e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(str);
            }
            return 0;
        }
    }

    public boolean readBool(boolean z) {
        int readInt32 = readInt32(z);
        if (readInt32 == -NUM) {
            return true;
        }
        if (readInt32 == -NUM) {
            return false;
        }
        String str = "Not bool value!";
        if (z) {
            throw new RuntimeException(str);
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(str);
        }
        return false;
    }

    public long readInt64(boolean z) {
        try {
            return this.buffer.getLong();
        } catch (Exception e) {
            String str = "read int64 error";
            if (z) {
                throw new RuntimeException(str, e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(str);
            }
            return 0;
        }
    }

    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.buffer.get(bArr);
        } catch (Exception e) {
            String str = "read raw error";
            if (z) {
                throw new RuntimeException(str, e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(str);
            }
        }
    }

    public void readBytes(byte[] bArr, int i, int i2, boolean z) {
        try {
            this.buffer.get(bArr, i, i2);
        } catch (Exception e) {
            String str = "read raw error";
            if (z) {
                throw new RuntimeException(str, e);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(str);
            }
        }
    }

    public byte[] readData(int i, boolean z) {
        byte[] bArr = new byte[i];
        readBytes(bArr, z);
        return bArr;
    }

    public String readString(boolean z) {
        int position = getPosition();
        try {
            int i;
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[intFromByte];
            this.buffer.get(bArr);
            for (i = 
/*
Method generation error in method: org.telegram.tgnet.NativeByteBuffer.readString(boolean):java.lang.String, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r2_11 'i' int) = (r2_9 'i' int), (r2_10 'i' int) binds: {(r2_9 'i' int)=B:4:0x0013, (r2_10 'i' int)=B:5:0x0039} in method: org.telegram.tgnet.NativeByteBuffer.readString(boolean):java.lang.String, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 20 more

*/

    public byte[] readByteArray(boolean z) {
        try {
            int i;
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[intFromByte];
            this.buffer.get(bArr);
            for (i = 
/*
Method generation error in method: org.telegram.tgnet.NativeByteBuffer.readByteArray(boolean):byte[], dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r1_11 'i' int) = (r1_9 'i' int), (r1_10 'i' int) binds: {(r1_9 'i' int)=B:3:0x000f, (r1_10 'i' int)=B:4:0x0035} in method: org.telegram.tgnet.NativeByteBuffer.readByteArray(boolean):byte[], dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 20 more

*/

    public NativeByteBuffer readByteBuffer(boolean z) {
        try {
            int i;
            int intFromByte = getIntFromByte(this.buffer.get());
            if (intFromByte >= 254) {
                intFromByte = (getIntFromByte(this.buffer.get()) | (getIntFromByte(this.buffer.get()) << 8)) | (getIntFromByte(this.buffer.get()) << 16);
                i = 4;
            } else {
                i = 1;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(intFromByte);
            int limit = this.buffer.limit();
            this.buffer.limit(this.buffer.position() + intFromByte);
            nativeByteBuffer.buffer.put(this.buffer);
            this.buffer.limit(limit);
            nativeByteBuffer.buffer.position(0);
            for (i = 
/*
Method generation error in method: org.telegram.tgnet.NativeByteBuffer.readByteBuffer(boolean):org.telegram.tgnet.NativeByteBuffer, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r1_11 'i' int) = (r1_9 'i' int), (r1_10 'i' int) binds: {(r1_9 'i' int)=B:3:0x000f, (r1_10 'i' int)=B:4:0x0035} in method: org.telegram.tgnet.NativeByteBuffer.readByteBuffer(boolean):org.telegram.tgnet.NativeByteBuffer, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 20 more

*/

    public double readDouble(boolean z) {
        try {
            return Double.longBitsToDouble(readInt64(z));
        } catch (Exception e) {
            String str = "read double error";
            if (z) {
                throw new RuntimeException(str, e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(str);
            }
            return 0.0d;
        }
    }

    public void reuse() {
        long j = this.address;
        if (j != 0) {
            this.reused = true;
            native_reuse(j);
        }
    }

    public int remaining() {
        return this.buffer.remaining();
    }
}
