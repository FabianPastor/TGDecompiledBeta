package org.telegram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
            FileLog.e(e);
        }
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            if (this.outbuf != null) {
                this.outbuf.close();
                this.outbuf = null;
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
        try {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public SerializedData(File file) throws Exception {
        this.isOut = true;
        this.justCalc = false;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[((int) file.length())];
        new DataInputStream(fileInputStream).readFully(bArr);
        fileInputStream.close();
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(bArr);
        this.in = new DataInputStream(this.inbuf);
    }

    public void writeInt32(int i) {
        if (this.justCalc) {
            this.len += 4;
        } else {
            writeInt32(i, this.out);
        }
    }

    private void writeInt32(int i, DataOutputStream dataOutputStream) {
        int i2 = 0;
        while (i2 < 4) {
            try {
                dataOutputStream.write(i >> (i2 * 8));
                i2++;
            } catch (Exception unused) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int32 error");
                    return;
                }
                return;
            }
        }
    }

    public void writeInt64(long j) {
        if (this.justCalc) {
            this.len += 8;
        } else {
            writeInt64(j, this.out);
        }
    }

    private void writeInt64(long j, DataOutputStream dataOutputStream) {
        int i = 0;
        while (i < 8) {
            try {
                dataOutputStream.write((int) (j >> (i * 8)));
                i++;
            } catch (Exception unused) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("write int64 error");
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
            if (this.justCalc) {
                this.len += bArr.length;
            } else {
                this.out.write(bArr);
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
                this.out.write(bArr, i, i2);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write bytes error");
            }
        }
    }

    public void writeByte(int i) {
        try {
            if (this.justCalc) {
                this.len++;
            } else {
                this.out.writeByte((byte) i);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
            }
        }
    }

    public void writeByte(byte b) {
        try {
            if (this.justCalc) {
                this.len++;
            } else {
                this.out.writeByte(b);
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte error");
            }
        }
    }

    public void writeByteArray(byte[] bArr) {
        try {
            if (bArr.length <= 253) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.out.write(bArr.length);
                }
            } else if (this.justCalc) {
                this.len += 4;
            } else {
                this.out.write(254);
                this.out.write(bArr.length);
                this.out.write(bArr.length >> 8);
                this.out.write(bArr.length >> 16);
            }
            if (this.justCalc) {
                this.len += bArr.length;
            } else {
                this.out.write(bArr);
            }
            int i = bArr.length <= 253 ? 1 : 4;
            while ((bArr.length + i) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.out.write(0);
                }
                i++;
            }
        } catch (Exception unused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("write byte array error");
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
                    this.out.write(i2);
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
            this.out.write(254);
            this.out.write(i2);
            this.out.write(i2 >> 8);
            this.out.write(i2 >> 16);
        }
        if (this.justCalc) {
            this.len += i2;
        } else {
            this.out.write(bArr, i, i2);
        }
        int i3 = i2 <= 253 ? 1 : 4;
        while ((i2 + i3) % 4 != 0) {
            if (this.justCalc) {
                this.len++;
            } else {
                this.out.write(0);
            }
            i3++;
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

    public int length() {
        if (this.justCalc) {
            return this.len;
        }
        return this.isOut ? this.outbuf.size() : this.inbuf.available();
    }

    /* Access modifiers changed, original: protected */
    public void set(byte[] bArr) {
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(bArr);
        this.in = new DataInputStream(this.inbuf);
    }

    public byte[] toByteArray() {
        return this.outbuf.toByteArray();
    }

    public void skip(int i) {
        if (i != 0) {
            if (this.justCalc) {
                this.len += i;
            } else {
                DataInputStream dataInputStream = this.in;
                if (dataInputStream != null) {
                    try {
                        dataInputStream.skipBytes(i);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
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
        String str = "Not bool value!";
        if (z) {
            throw new RuntimeException(str);
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(str);
        }
        return false;
    }

    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.in.read(bArr);
            this.len += bArr.length;
        } catch (Exception e) {
            String str = "read bytes error";
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
        try {
            int i;
            int read = this.in.read();
            this.len++;
            if (read >= 254) {
                read = (this.in.read() | (this.in.read() << 8)) | (this.in.read() << 16);
                this.len += 3;
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[read];
            this.in.read(bArr);
            this.len++;
            for (i = 
/*
Method generation error in method: org.telegram.tgnet.SerializedData.readString(boolean):java.lang.String, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r1_13 'i' int) = (r1_11 'i' int), (r1_12 'i' int) binds: {(r1_11 'i' int)=B:3:0x0011, (r1_12 'i' int)=B:4:0x0031} in method: org.telegram.tgnet.SerializedData.readString(boolean):java.lang.String, dex: classes.dex
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
            int read = this.in.read();
            this.len++;
            if (read >= 254) {
                read = (this.in.read() | (this.in.read() << 8)) | (this.in.read() << 16);
                this.len += 3;
                i = 4;
            } else {
                i = 1;
            }
            byte[] bArr = new byte[read];
            this.in.read(bArr);
            this.len++;
            for (i = 
/*
Method generation error in method: org.telegram.tgnet.SerializedData.readByteArray(boolean):byte[], dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r1_13 'i' int) = (r1_11 'i' int), (r1_12 'i' int) binds: {(r1_11 'i' int)=B:3:0x0011, (r1_12 'i' int)=B:4:0x0031} in method: org.telegram.tgnet.SerializedData.readByteArray(boolean):byte[], dex: classes.dex
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

    public int readInt32(boolean z) {
        int i = 0;
        int i2 = 0;
        while (i < 4) {
            try {
                i2 |= this.in.read() << (i * 8);
                this.len++;
                i++;
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
