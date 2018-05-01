package org.telegram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        try {
            if (this.outbuf != null) {
                this.outbuf.close();
                this.outbuf = null;
            }
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
        try {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (Throwable e3) {
            FileLog.m3e(e3);
        }
    }

    public SerializedData(File file) throws Exception {
        this.isOut = true;
        this.justCalc = false;
        InputStream fileInputStream = new FileInputStream(file);
        file = new byte[((int) file.length())];
        new DataInputStream(fileInputStream).readFully(file);
        fileInputStream.close();
        this.isOut = false;
        this.inbuf = new ByteArrayInputStream(file);
        this.in = new DataInputStream(this.inbuf);
    }

    public void writeInt32(int i) {
        if (this.justCalc) {
            this.len += 4;
        } else {
            writeInt32(i, this.out);
        }
    }

    private void writeInt32(int r3, java.io.DataOutputStream r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = 0;
    L_0x0001:
        r1 = 4;
        if (r0 >= r1) goto L_0x0017;
    L_0x0004:
        r1 = r0 * 8;
        r1 = r3 >> r1;
        r4.write(r1);	 Catch:{ Exception -> 0x000e }
        r0 = r0 + 1;
        goto L_0x0001;
    L_0x000e:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0017;
    L_0x0012:
        r3 = "write int32 error";
        org.telegram.messenger.FileLog.m1e(r3);
    L_0x0017:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeInt32(int, java.io.DataOutputStream):void");
    }

    public void writeInt64(long j) {
        if (this.justCalc) {
            this.len += 8;
        } else {
            writeInt64(j, this.out);
        }
    }

    private void writeInt64(long r4, java.io.DataOutputStream r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r0 = 0;
    L_0x0001:
        r1 = 8;
        if (r0 >= r1) goto L_0x0019;
    L_0x0005:
        r1 = r0 * 8;
        r1 = r4 >> r1;
        r1 = (int) r1;
        r6.write(r1);	 Catch:{ Exception -> 0x0010 }
        r0 = r0 + 1;
        goto L_0x0001;
    L_0x0010:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0019;
    L_0x0014:
        r4 = "write int64 error";
        org.telegram.messenger.FileLog.m1e(r4);
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeInt64(long, java.io.DataOutputStream):void");
    }

    public void writeBool(boolean z) {
        if (this.justCalc) {
            this.len += 4;
        } else if (z) {
            writeInt32(true);
        } else {
            writeInt32(true);
        }
    }

    public void writeBytes(byte[] r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.justCalc;	 Catch:{ Exception -> 0x0011 }
        if (r0 != 0) goto L_0x000a;	 Catch:{ Exception -> 0x0011 }
    L_0x0004:
        r0 = r1.out;	 Catch:{ Exception -> 0x0011 }
        r0.write(r2);	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;	 Catch:{ Exception -> 0x0011 }
    L_0x000a:
        r0 = r1.len;	 Catch:{ Exception -> 0x0011 }
        r2 = r2.length;	 Catch:{ Exception -> 0x0011 }
        r0 = r0 + r2;	 Catch:{ Exception -> 0x0011 }
        r1.len = r0;	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;
    L_0x0011:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x001a;
    L_0x0015:
        r2 = "write raw error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x001a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeBytes(byte[]):void");
    }

    public void writeBytes(byte[] r2, int r3, int r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.justCalc;	 Catch:{ Exception -> 0x0010 }
        if (r0 != 0) goto L_0x000a;	 Catch:{ Exception -> 0x0010 }
    L_0x0004:
        r0 = r1.out;	 Catch:{ Exception -> 0x0010 }
        r0.write(r2, r3, r4);	 Catch:{ Exception -> 0x0010 }
        goto L_0x0019;	 Catch:{ Exception -> 0x0010 }
    L_0x000a:
        r2 = r1.len;	 Catch:{ Exception -> 0x0010 }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x0010 }
        r1.len = r2;	 Catch:{ Exception -> 0x0010 }
        goto L_0x0019;
    L_0x0010:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0019;
    L_0x0014:
        r2 = "write bytes error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeBytes(byte[], int, int):void");
    }

    public void writeByte(int r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.justCalc;	 Catch:{ Exception -> 0x0012 }
        if (r0 != 0) goto L_0x000b;	 Catch:{ Exception -> 0x0012 }
    L_0x0004:
        r0 = r1.out;	 Catch:{ Exception -> 0x0012 }
        r2 = (byte) r2;	 Catch:{ Exception -> 0x0012 }
        r0.writeByte(r2);	 Catch:{ Exception -> 0x0012 }
        goto L_0x001b;	 Catch:{ Exception -> 0x0012 }
    L_0x000b:
        r2 = r1.len;	 Catch:{ Exception -> 0x0012 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0012 }
        r1.len = r2;	 Catch:{ Exception -> 0x0012 }
        goto L_0x001b;
    L_0x0012:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x001b;
    L_0x0016:
        r2 = "write byte error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x001b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeByte(int):void");
    }

    public void writeByte(byte r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.justCalc;	 Catch:{ Exception -> 0x0011 }
        if (r0 != 0) goto L_0x000a;	 Catch:{ Exception -> 0x0011 }
    L_0x0004:
        r0 = r1.out;	 Catch:{ Exception -> 0x0011 }
        r0.writeByte(r2);	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;	 Catch:{ Exception -> 0x0011 }
    L_0x000a:
        r2 = r1.len;	 Catch:{ Exception -> 0x0011 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0011 }
        r1.len = r2;	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;
    L_0x0011:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x001a;
    L_0x0015:
        r2 = "write byte error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x001a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeByte(byte):void");
    }

    public void writeByteArray(byte[] r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r5 = this;
        r0 = r6.length;	 Catch:{ Exception -> 0x006d }
        r1 = 253; // 0xfd float:3.55E-43 double:1.25E-321;	 Catch:{ Exception -> 0x006d }
        r2 = 4;	 Catch:{ Exception -> 0x006d }
        r3 = 1;	 Catch:{ Exception -> 0x006d }
        if (r0 > r1) goto L_0x0018;	 Catch:{ Exception -> 0x006d }
    L_0x0007:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x006d }
        if (r0 != 0) goto L_0x0012;	 Catch:{ Exception -> 0x006d }
    L_0x000b:
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = r6.length;	 Catch:{ Exception -> 0x006d }
        r0.write(r4);	 Catch:{ Exception -> 0x006d }
        goto L_0x003f;	 Catch:{ Exception -> 0x006d }
    L_0x0012:
        r0 = r5.len;	 Catch:{ Exception -> 0x006d }
        r0 = r0 + r3;	 Catch:{ Exception -> 0x006d }
        r5.len = r0;	 Catch:{ Exception -> 0x006d }
        goto L_0x003f;	 Catch:{ Exception -> 0x006d }
    L_0x0018:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x006d }
        if (r0 != 0) goto L_0x003a;	 Catch:{ Exception -> 0x006d }
    L_0x001c:
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x006d }
        r0.write(r4);	 Catch:{ Exception -> 0x006d }
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = r6.length;	 Catch:{ Exception -> 0x006d }
        r0.write(r4);	 Catch:{ Exception -> 0x006d }
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = r6.length;	 Catch:{ Exception -> 0x006d }
        r4 = r4 >> 8;	 Catch:{ Exception -> 0x006d }
        r0.write(r4);	 Catch:{ Exception -> 0x006d }
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = r6.length;	 Catch:{ Exception -> 0x006d }
        r4 = r4 >> 16;	 Catch:{ Exception -> 0x006d }
        r0.write(r4);	 Catch:{ Exception -> 0x006d }
        goto L_0x003f;	 Catch:{ Exception -> 0x006d }
    L_0x003a:
        r0 = r5.len;	 Catch:{ Exception -> 0x006d }
        r0 = r0 + r2;	 Catch:{ Exception -> 0x006d }
        r5.len = r0;	 Catch:{ Exception -> 0x006d }
    L_0x003f:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x006d }
        if (r0 != 0) goto L_0x0049;	 Catch:{ Exception -> 0x006d }
    L_0x0043:
        r0 = r5.out;	 Catch:{ Exception -> 0x006d }
        r0.write(r6);	 Catch:{ Exception -> 0x006d }
        goto L_0x004f;	 Catch:{ Exception -> 0x006d }
    L_0x0049:
        r0 = r5.len;	 Catch:{ Exception -> 0x006d }
        r4 = r6.length;	 Catch:{ Exception -> 0x006d }
        r0 = r0 + r4;	 Catch:{ Exception -> 0x006d }
        r5.len = r0;	 Catch:{ Exception -> 0x006d }
    L_0x004f:
        r0 = r6.length;	 Catch:{ Exception -> 0x006d }
        if (r0 > r1) goto L_0x0054;	 Catch:{ Exception -> 0x006d }
    L_0x0052:
        r0 = r3;	 Catch:{ Exception -> 0x006d }
        goto L_0x0055;	 Catch:{ Exception -> 0x006d }
    L_0x0054:
        r0 = r2;	 Catch:{ Exception -> 0x006d }
    L_0x0055:
        r1 = r6.length;	 Catch:{ Exception -> 0x006d }
        r1 = r1 + r0;	 Catch:{ Exception -> 0x006d }
        r1 = r1 % r2;	 Catch:{ Exception -> 0x006d }
        if (r1 == 0) goto L_0x0076;	 Catch:{ Exception -> 0x006d }
    L_0x005a:
        r1 = r5.justCalc;	 Catch:{ Exception -> 0x006d }
        if (r1 != 0) goto L_0x0065;	 Catch:{ Exception -> 0x006d }
    L_0x005e:
        r1 = r5.out;	 Catch:{ Exception -> 0x006d }
        r4 = 0;	 Catch:{ Exception -> 0x006d }
        r1.write(r4);	 Catch:{ Exception -> 0x006d }
        goto L_0x006a;	 Catch:{ Exception -> 0x006d }
    L_0x0065:
        r1 = r5.len;	 Catch:{ Exception -> 0x006d }
        r1 = r1 + r3;	 Catch:{ Exception -> 0x006d }
        r5.len = r1;	 Catch:{ Exception -> 0x006d }
    L_0x006a:
        r0 = r0 + 1;
        goto L_0x0055;
    L_0x006d:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0076;
    L_0x0071:
        r6 = "write byte array error";
        org.telegram.messenger.FileLog.m1e(r6);
    L_0x0076:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeByteArray(byte[]):void");
    }

    public void writeString(java.lang.String r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = "UTF-8";	 Catch:{ Exception -> 0x000a }
        r2 = r2.getBytes(r0);	 Catch:{ Exception -> 0x000a }
        r1.writeByteArray(r2);	 Catch:{ Exception -> 0x000a }
        goto L_0x0013;
    L_0x000a:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0013;
    L_0x000e:
        r2 = "write string error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x0013:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeString(java.lang.String):void");
    }

    public void writeByteArray(byte[] r6, int r7, int r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r5 = this;
        r0 = 253; // 0xfd float:3.55E-43 double:1.25E-321;
        r1 = 4;
        r2 = 1;
        if (r8 > r0) goto L_0x0016;
    L_0x0006:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0066 }
        if (r3 != 0) goto L_0x0010;	 Catch:{ Exception -> 0x0066 }
    L_0x000a:
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r3.write(r8);	 Catch:{ Exception -> 0x0066 }
        goto L_0x003a;	 Catch:{ Exception -> 0x0066 }
    L_0x0010:
        r3 = r5.len;	 Catch:{ Exception -> 0x0066 }
        r3 = r3 + r2;	 Catch:{ Exception -> 0x0066 }
        r5.len = r3;	 Catch:{ Exception -> 0x0066 }
        goto L_0x003a;	 Catch:{ Exception -> 0x0066 }
    L_0x0016:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0066 }
        if (r3 != 0) goto L_0x0035;	 Catch:{ Exception -> 0x0066 }
    L_0x001a:
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r4 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x0066 }
        r3.write(r4);	 Catch:{ Exception -> 0x0066 }
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r3.write(r8);	 Catch:{ Exception -> 0x0066 }
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r4 = r8 >> 8;	 Catch:{ Exception -> 0x0066 }
        r3.write(r4);	 Catch:{ Exception -> 0x0066 }
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r4 = r8 >> 16;	 Catch:{ Exception -> 0x0066 }
        r3.write(r4);	 Catch:{ Exception -> 0x0066 }
        goto L_0x003a;	 Catch:{ Exception -> 0x0066 }
    L_0x0035:
        r3 = r5.len;	 Catch:{ Exception -> 0x0066 }
        r3 = r3 + r1;	 Catch:{ Exception -> 0x0066 }
        r5.len = r3;	 Catch:{ Exception -> 0x0066 }
    L_0x003a:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0066 }
        if (r3 != 0) goto L_0x0044;	 Catch:{ Exception -> 0x0066 }
    L_0x003e:
        r3 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r3.write(r6, r7, r8);	 Catch:{ Exception -> 0x0066 }
        goto L_0x0049;	 Catch:{ Exception -> 0x0066 }
    L_0x0044:
        r6 = r5.len;	 Catch:{ Exception -> 0x0066 }
        r6 = r6 + r8;	 Catch:{ Exception -> 0x0066 }
        r5.len = r6;	 Catch:{ Exception -> 0x0066 }
    L_0x0049:
        if (r8 > r0) goto L_0x004d;	 Catch:{ Exception -> 0x0066 }
    L_0x004b:
        r6 = r2;	 Catch:{ Exception -> 0x0066 }
        goto L_0x004e;	 Catch:{ Exception -> 0x0066 }
    L_0x004d:
        r6 = r1;	 Catch:{ Exception -> 0x0066 }
    L_0x004e:
        r7 = r8 + r6;	 Catch:{ Exception -> 0x0066 }
        r7 = r7 % r1;	 Catch:{ Exception -> 0x0066 }
        if (r7 == 0) goto L_0x006f;	 Catch:{ Exception -> 0x0066 }
    L_0x0053:
        r7 = r5.justCalc;	 Catch:{ Exception -> 0x0066 }
        if (r7 != 0) goto L_0x005e;	 Catch:{ Exception -> 0x0066 }
    L_0x0057:
        r7 = r5.out;	 Catch:{ Exception -> 0x0066 }
        r0 = 0;	 Catch:{ Exception -> 0x0066 }
        r7.write(r0);	 Catch:{ Exception -> 0x0066 }
        goto L_0x0063;	 Catch:{ Exception -> 0x0066 }
    L_0x005e:
        r7 = r5.len;	 Catch:{ Exception -> 0x0066 }
        r7 = r7 + r2;	 Catch:{ Exception -> 0x0066 }
        r5.len = r7;	 Catch:{ Exception -> 0x0066 }
    L_0x0063:
        r6 = r6 + 1;
        goto L_0x004e;
    L_0x0066:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x006f;
    L_0x006a:
        r6 = "write byte array error";
        org.telegram.messenger.FileLog.m1e(r6);
    L_0x006f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeByteArray(byte[], int, int):void");
    }

    public void writeDouble(double r1) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = this;
        r1 = java.lang.Double.doubleToRawLongBits(r1);	 Catch:{ Exception -> 0x0008 }
        r0.writeInt64(r1);	 Catch:{ Exception -> 0x0008 }
        goto L_0x0011;
    L_0x0008:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0011;
    L_0x000c:
        r1 = "write double error";
        org.telegram.messenger.FileLog.m1e(r1);
    L_0x0011:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.writeDouble(double):void");
    }

    public int length() {
        if (this.justCalc) {
            return this.len;
        }
        return this.isOut ? this.outbuf.size() : this.inbuf.available();
    }

    protected void set(byte[] bArr) {
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
            } else if (this.in != null) {
                try {
                    this.in.skipBytes(i);
                } catch (Throwable e) {
                    FileLog.m3e(e);
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
        if (z) {
            throw new RuntimeException("Not bool value!");
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m1e("Not bool value!");
        }
        return false;
    }

    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.in.read(bArr);
            this.len += bArr.length;
        } catch (byte[] bArr2) {
            if (z) {
                throw new RuntimeException("read bytes error", bArr2);
            } else if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("read bytes error");
            }
        }
    }

    public byte[] readData(int i, boolean z) {
        i = new byte[i];
        readBytes(i, z);
        return i;
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
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
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
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 20 more

*/

                    public double readDouble(boolean z) {
                        try {
                            return Double.longBitsToDouble(readInt64(z));
                        } catch (Throwable e) {
                            if (z) {
                                throw new RuntimeException("read double error", e);
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m1e("read double error");
                            }
                            return 0.0d;
                        }
                    }

                    public int readInt32(boolean z) {
                        int i = 0;
                        int i2 = i;
                        while (i < 4) {
                            try {
                                i2 |= this.in.read() << (i * 8);
                                this.len++;
                                i++;
                            } catch (Throwable e) {
                                if (z) {
                                    throw new RuntimeException("read int32 error", e);
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m1e("read int32 error");
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
                                long read = j | (((long) this.in.read()) << (i * 8));
                                this.len++;
                                i++;
                                j = read;
                            } catch (Throwable e) {
                                if (z) {
                                    throw new RuntimeException("read int64 error", e);
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m1e("read int64 error");
                                }
                                return 0;
                            }
                        }
                        return j;
                    }

                    public int remaining() {
                        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                        /*
                        r1 = this;
                        r0 = r1.in;	 Catch:{ Exception -> 0x0007 }
                        r0 = r0.available();	 Catch:{ Exception -> 0x0007 }
                        return r0;
                    L_0x0007:
                        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
                        return r0;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.SerializedData.remaining():int");
                    }
                }
