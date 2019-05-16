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

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0065 in {3, 4, 9, 10, 16, 18, 20} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public byte[] readByteArray(boolean r7) {
        /*
        r6 = this;
        r0 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r0 = r0.read();	 Catch:{ Exception -> 0x0051 }
        r1 = r6.len;	 Catch:{ Exception -> 0x0051 }
        r2 = 1;	 Catch:{ Exception -> 0x0051 }
        r1 = r1 + r2;	 Catch:{ Exception -> 0x0051 }
        r6.len = r1;	 Catch:{ Exception -> 0x0051 }
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x0051 }
        r3 = 4;	 Catch:{ Exception -> 0x0051 }
        if (r0 < r1) goto L_0x0031;	 Catch:{ Exception -> 0x0051 }
        r0 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r0 = r0.read();	 Catch:{ Exception -> 0x0051 }
        r1 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r1 = r1.read();	 Catch:{ Exception -> 0x0051 }
        r1 = r1 << 8;	 Catch:{ Exception -> 0x0051 }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x0051 }
        r1 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r1 = r1.read();	 Catch:{ Exception -> 0x0051 }
        r1 = r1 << 16;	 Catch:{ Exception -> 0x0051 }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x0051 }
        r1 = r6.len;	 Catch:{ Exception -> 0x0051 }
        r1 = r1 + 3;	 Catch:{ Exception -> 0x0051 }
        r6.len = r1;	 Catch:{ Exception -> 0x0051 }
        r1 = 4;	 Catch:{ Exception -> 0x0051 }
        goto L_0x0032;	 Catch:{ Exception -> 0x0051 }
        r1 = 1;	 Catch:{ Exception -> 0x0051 }
        r4 = new byte[r0];	 Catch:{ Exception -> 0x0051 }
        r5 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r5.read(r4);	 Catch:{ Exception -> 0x0051 }
        r5 = r6.len;	 Catch:{ Exception -> 0x0051 }
        r5 = r5 + r2;	 Catch:{ Exception -> 0x0051 }
        r6.len = r5;	 Catch:{ Exception -> 0x0051 }
        r5 = r0 + r1;	 Catch:{ Exception -> 0x0051 }
        r5 = r5 % r3;	 Catch:{ Exception -> 0x0051 }
        if (r5 == 0) goto L_0x0050;	 Catch:{ Exception -> 0x0051 }
        r5 = r6.in;	 Catch:{ Exception -> 0x0051 }
        r5.read();	 Catch:{ Exception -> 0x0051 }
        r5 = r6.len;	 Catch:{ Exception -> 0x0051 }
        r5 = r5 + r2;	 Catch:{ Exception -> 0x0051 }
        r6.len = r5;	 Catch:{ Exception -> 0x0051 }
        r1 = r1 + 1;
        goto L_0x003e;
        return r4;
        r0 = move-exception;
        r1 = "read byte array error";
        if (r7 != 0) goto L_0x005f;
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r7 == 0) goto L_0x005d;
        org.telegram.messenger.FileLog.e(r1);
        r7 = 0;
        return r7;
        r7 = new java.lang.RuntimeException;
        r7.<init>(r1, r0);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.readByteArray(boolean):byte[]");
    }

    public NativeByteBuffer readByteBuffer(boolean z) {
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x006c in {3, 4, 8, 10, 16, 18, 20} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public java.lang.String readString(boolean r7) {
        /*
        r6 = this;
        r0 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r0 = r0.read();	 Catch:{ Exception -> 0x0058 }
        r1 = r6.len;	 Catch:{ Exception -> 0x0058 }
        r2 = 1;	 Catch:{ Exception -> 0x0058 }
        r1 = r1 + r2;	 Catch:{ Exception -> 0x0058 }
        r6.len = r1;	 Catch:{ Exception -> 0x0058 }
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x0058 }
        r3 = 4;	 Catch:{ Exception -> 0x0058 }
        if (r0 < r1) goto L_0x0031;	 Catch:{ Exception -> 0x0058 }
        r0 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r0 = r0.read();	 Catch:{ Exception -> 0x0058 }
        r1 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r1 = r1.read();	 Catch:{ Exception -> 0x0058 }
        r1 = r1 << 8;	 Catch:{ Exception -> 0x0058 }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x0058 }
        r1 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r1 = r1.read();	 Catch:{ Exception -> 0x0058 }
        r1 = r1 << 16;	 Catch:{ Exception -> 0x0058 }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x0058 }
        r1 = r6.len;	 Catch:{ Exception -> 0x0058 }
        r1 = r1 + 3;	 Catch:{ Exception -> 0x0058 }
        r6.len = r1;	 Catch:{ Exception -> 0x0058 }
        r1 = 4;	 Catch:{ Exception -> 0x0058 }
        goto L_0x0032;	 Catch:{ Exception -> 0x0058 }
        r1 = 1;	 Catch:{ Exception -> 0x0058 }
        r4 = new byte[r0];	 Catch:{ Exception -> 0x0058 }
        r5 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r5.read(r4);	 Catch:{ Exception -> 0x0058 }
        r5 = r6.len;	 Catch:{ Exception -> 0x0058 }
        r5 = r5 + r2;	 Catch:{ Exception -> 0x0058 }
        r6.len = r5;	 Catch:{ Exception -> 0x0058 }
        r5 = r0 + r1;	 Catch:{ Exception -> 0x0058 }
        r5 = r5 % r3;	 Catch:{ Exception -> 0x0058 }
        if (r5 == 0) goto L_0x0050;	 Catch:{ Exception -> 0x0058 }
        r5 = r6.in;	 Catch:{ Exception -> 0x0058 }
        r5.read();	 Catch:{ Exception -> 0x0058 }
        r5 = r6.len;	 Catch:{ Exception -> 0x0058 }
        r5 = r5 + r2;	 Catch:{ Exception -> 0x0058 }
        r6.len = r5;	 Catch:{ Exception -> 0x0058 }
        r1 = r1 + 1;	 Catch:{ Exception -> 0x0058 }
        goto L_0x003e;	 Catch:{ Exception -> 0x0058 }
        r0 = new java.lang.String;	 Catch:{ Exception -> 0x0058 }
        r1 = "UTF-8";	 Catch:{ Exception -> 0x0058 }
        r0.<init>(r4, r1);	 Catch:{ Exception -> 0x0058 }
        return r0;
        r0 = move-exception;
        r1 = "read string error";
        if (r7 != 0) goto L_0x0066;
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r7 == 0) goto L_0x0064;
        org.telegram.messenger.FileLog.e(r1);
        r7 = 0;
        return r7;
        r7 = new java.lang.RuntimeException;
        r7.<init>(r1, r0);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.readString(boolean):java.lang.String");
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
