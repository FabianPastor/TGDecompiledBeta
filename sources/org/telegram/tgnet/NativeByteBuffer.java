package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class NativeByteBuffer extends AbstractSerializedData {
    private static final ThreadLocal<NativeByteBuffer> addressWrapper = new C07061();
    protected long address;
    public ByteBuffer buffer;
    private boolean justCalc;
    private int len;
    public boolean reused;

    /* renamed from: org.telegram.tgnet.NativeByteBuffer$1 */
    static class C07061 extends ThreadLocal<NativeByteBuffer> {
        C07061() {
        }

        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(0, true);
        }
    }

    public static native long native_getFreeBuffer(int i);

    public static native ByteBuffer native_getJavaByteBuffer(long j);

    public static native int native_limit(long j);

    public static native int native_position(long j);

    public static native void native_reuse(long j);

    public int getIntFromByte(byte b) {
        return b >= (byte) 0 ? b : b + 256;
    }

    public static NativeByteBuffer wrap(long j) {
        NativeByteBuffer nativeByteBuffer = (NativeByteBuffer) addressWrapper.get();
        if (j != 0) {
            if (!nativeByteBuffer.reused && BuildVars.LOGS_ENABLED) {
                FileLog.m1e("forgot to reuse?");
            }
            nativeByteBuffer.address = j;
            nativeByteBuffer.reused = false;
            nativeByteBuffer.buffer = native_getJavaByteBuffer(j);
            nativeByteBuffer.buffer.limit(native_limit(j));
            j = native_position(j);
            if (j <= nativeByteBuffer.buffer.limit()) {
                nativeByteBuffer.buffer.position(j);
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
            if (this.address != 0) {
                this.buffer = native_getJavaByteBuffer(this.address);
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

    public void writeInt32(int r2) {
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
        r0 = r1.buffer;	 Catch:{ Exception -> 0x0011 }
        r0.putInt(r2);	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;	 Catch:{ Exception -> 0x0011 }
    L_0x000a:
        r2 = r1.len;	 Catch:{ Exception -> 0x0011 }
        r2 = r2 + 4;	 Catch:{ Exception -> 0x0011 }
        r1.len = r2;	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;
    L_0x0011:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x001a;
    L_0x0015:
        r2 = "write int32 error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x001a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeInt32(int):void");
    }

    public void writeInt64(long r2) {
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
        r0 = r1.buffer;	 Catch:{ Exception -> 0x0011 }
        r0.putLong(r2);	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;	 Catch:{ Exception -> 0x0011 }
    L_0x000a:
        r2 = r1.len;	 Catch:{ Exception -> 0x0011 }
        r2 = r2 + 8;	 Catch:{ Exception -> 0x0011 }
        r1.len = r2;	 Catch:{ Exception -> 0x0011 }
        goto L_0x001a;
    L_0x0011:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x001a;
    L_0x0015:
        r2 = "write int64 error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x001a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeInt64(long):void");
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
        r0 = r1.buffer;	 Catch:{ Exception -> 0x0011 }
        r0.put(r2);	 Catch:{ Exception -> 0x0011 }
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeBytes(byte[]):void");
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
        r0 = r1.buffer;	 Catch:{ Exception -> 0x0010 }
        r0.put(r2, r3, r4);	 Catch:{ Exception -> 0x0010 }
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
        r2 = "write raw error";
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeBytes(byte[], int, int):void");
    }

    public void writeByte(int i) {
        writeByte((byte) i);
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
        r0 = r1.buffer;	 Catch:{ Exception -> 0x0011 }
        r0.put(r2);	 Catch:{ Exception -> 0x0011 }
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeByte(byte):void");
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeString(java.lang.String):void");
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
        if (r8 > r0) goto L_0x0017;
    L_0x0006:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0069 }
        if (r3 != 0) goto L_0x0011;	 Catch:{ Exception -> 0x0069 }
    L_0x000a:
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r4 = (byte) r8;	 Catch:{ Exception -> 0x0069 }
        r3.put(r4);	 Catch:{ Exception -> 0x0069 }
        goto L_0x003d;	 Catch:{ Exception -> 0x0069 }
    L_0x0011:
        r3 = r5.len;	 Catch:{ Exception -> 0x0069 }
        r3 = r3 + r2;	 Catch:{ Exception -> 0x0069 }
        r5.len = r3;	 Catch:{ Exception -> 0x0069 }
        goto L_0x003d;	 Catch:{ Exception -> 0x0069 }
    L_0x0017:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0069 }
        if (r3 != 0) goto L_0x0038;	 Catch:{ Exception -> 0x0069 }
    L_0x001b:
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r4 = -2;	 Catch:{ Exception -> 0x0069 }
        r3.put(r4);	 Catch:{ Exception -> 0x0069 }
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r4 = (byte) r8;	 Catch:{ Exception -> 0x0069 }
        r3.put(r4);	 Catch:{ Exception -> 0x0069 }
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r4 = r8 >> 8;	 Catch:{ Exception -> 0x0069 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0069 }
        r3.put(r4);	 Catch:{ Exception -> 0x0069 }
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r4 = r8 >> 16;	 Catch:{ Exception -> 0x0069 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0069 }
        r3.put(r4);	 Catch:{ Exception -> 0x0069 }
        goto L_0x003d;	 Catch:{ Exception -> 0x0069 }
    L_0x0038:
        r3 = r5.len;	 Catch:{ Exception -> 0x0069 }
        r3 = r3 + r1;	 Catch:{ Exception -> 0x0069 }
        r5.len = r3;	 Catch:{ Exception -> 0x0069 }
    L_0x003d:
        r3 = r5.justCalc;	 Catch:{ Exception -> 0x0069 }
        if (r3 != 0) goto L_0x0047;	 Catch:{ Exception -> 0x0069 }
    L_0x0041:
        r3 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r3.put(r6, r7, r8);	 Catch:{ Exception -> 0x0069 }
        goto L_0x004c;	 Catch:{ Exception -> 0x0069 }
    L_0x0047:
        r6 = r5.len;	 Catch:{ Exception -> 0x0069 }
        r6 = r6 + r8;	 Catch:{ Exception -> 0x0069 }
        r5.len = r6;	 Catch:{ Exception -> 0x0069 }
    L_0x004c:
        if (r8 > r0) goto L_0x0050;	 Catch:{ Exception -> 0x0069 }
    L_0x004e:
        r6 = r2;	 Catch:{ Exception -> 0x0069 }
        goto L_0x0051;	 Catch:{ Exception -> 0x0069 }
    L_0x0050:
        r6 = r1;	 Catch:{ Exception -> 0x0069 }
    L_0x0051:
        r7 = r8 + r6;	 Catch:{ Exception -> 0x0069 }
        r7 = r7 % r1;	 Catch:{ Exception -> 0x0069 }
        if (r7 == 0) goto L_0x0072;	 Catch:{ Exception -> 0x0069 }
    L_0x0056:
        r7 = r5.justCalc;	 Catch:{ Exception -> 0x0069 }
        if (r7 != 0) goto L_0x0061;	 Catch:{ Exception -> 0x0069 }
    L_0x005a:
        r7 = r5.buffer;	 Catch:{ Exception -> 0x0069 }
        r0 = 0;	 Catch:{ Exception -> 0x0069 }
        r7.put(r0);	 Catch:{ Exception -> 0x0069 }
        goto L_0x0066;	 Catch:{ Exception -> 0x0069 }
    L_0x0061:
        r7 = r5.len;	 Catch:{ Exception -> 0x0069 }
        r7 = r7 + r2;	 Catch:{ Exception -> 0x0069 }
        r5.len = r7;	 Catch:{ Exception -> 0x0069 }
    L_0x0066:
        r6 = r6 + 1;
        goto L_0x0051;
    L_0x0069:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0072;
    L_0x006d:
        r6 = "write byte array error";
        org.telegram.messenger.FileLog.m1e(r6);
    L_0x0072:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeByteArray(byte[], int, int):void");
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
        r0 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r1 = 253; // 0xfd float:3.55E-43 double:1.25E-321;	 Catch:{ Exception -> 0x0070 }
        r2 = 4;	 Catch:{ Exception -> 0x0070 }
        r3 = 1;	 Catch:{ Exception -> 0x0070 }
        if (r0 > r1) goto L_0x0019;	 Catch:{ Exception -> 0x0070 }
    L_0x0007:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x0070 }
        if (r0 != 0) goto L_0x0013;	 Catch:{ Exception -> 0x0070 }
    L_0x000b:
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0070 }
        r0.put(r4);	 Catch:{ Exception -> 0x0070 }
        goto L_0x0042;	 Catch:{ Exception -> 0x0070 }
    L_0x0013:
        r0 = r5.len;	 Catch:{ Exception -> 0x0070 }
        r0 = r0 + r3;	 Catch:{ Exception -> 0x0070 }
        r5.len = r0;	 Catch:{ Exception -> 0x0070 }
        goto L_0x0042;	 Catch:{ Exception -> 0x0070 }
    L_0x0019:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x0070 }
        if (r0 != 0) goto L_0x003d;	 Catch:{ Exception -> 0x0070 }
    L_0x001d:
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = -2;	 Catch:{ Exception -> 0x0070 }
        r0.put(r4);	 Catch:{ Exception -> 0x0070 }
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0070 }
        r0.put(r4);	 Catch:{ Exception -> 0x0070 }
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r4 = r4 >> 8;	 Catch:{ Exception -> 0x0070 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0070 }
        r0.put(r4);	 Catch:{ Exception -> 0x0070 }
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r4 = r4 >> 16;	 Catch:{ Exception -> 0x0070 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0070 }
        r0.put(r4);	 Catch:{ Exception -> 0x0070 }
        goto L_0x0042;	 Catch:{ Exception -> 0x0070 }
    L_0x003d:
        r0 = r5.len;	 Catch:{ Exception -> 0x0070 }
        r0 = r0 + r2;	 Catch:{ Exception -> 0x0070 }
        r5.len = r0;	 Catch:{ Exception -> 0x0070 }
    L_0x0042:
        r0 = r5.justCalc;	 Catch:{ Exception -> 0x0070 }
        if (r0 != 0) goto L_0x004c;	 Catch:{ Exception -> 0x0070 }
    L_0x0046:
        r0 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r0.put(r6);	 Catch:{ Exception -> 0x0070 }
        goto L_0x0052;	 Catch:{ Exception -> 0x0070 }
    L_0x004c:
        r0 = r5.len;	 Catch:{ Exception -> 0x0070 }
        r4 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r0 = r0 + r4;	 Catch:{ Exception -> 0x0070 }
        r5.len = r0;	 Catch:{ Exception -> 0x0070 }
    L_0x0052:
        r0 = r6.length;	 Catch:{ Exception -> 0x0070 }
        if (r0 > r1) goto L_0x0057;	 Catch:{ Exception -> 0x0070 }
    L_0x0055:
        r0 = r3;	 Catch:{ Exception -> 0x0070 }
        goto L_0x0058;	 Catch:{ Exception -> 0x0070 }
    L_0x0057:
        r0 = r2;	 Catch:{ Exception -> 0x0070 }
    L_0x0058:
        r1 = r6.length;	 Catch:{ Exception -> 0x0070 }
        r1 = r1 + r0;	 Catch:{ Exception -> 0x0070 }
        r1 = r1 % r2;	 Catch:{ Exception -> 0x0070 }
        if (r1 == 0) goto L_0x0079;	 Catch:{ Exception -> 0x0070 }
    L_0x005d:
        r1 = r5.justCalc;	 Catch:{ Exception -> 0x0070 }
        if (r1 != 0) goto L_0x0068;	 Catch:{ Exception -> 0x0070 }
    L_0x0061:
        r1 = r5.buffer;	 Catch:{ Exception -> 0x0070 }
        r4 = 0;	 Catch:{ Exception -> 0x0070 }
        r1.put(r4);	 Catch:{ Exception -> 0x0070 }
        goto L_0x006d;	 Catch:{ Exception -> 0x0070 }
    L_0x0068:
        r1 = r5.len;	 Catch:{ Exception -> 0x0070 }
        r1 = r1 + r3;	 Catch:{ Exception -> 0x0070 }
        r5.len = r1;	 Catch:{ Exception -> 0x0070 }
    L_0x006d:
        r0 = r0 + 1;
        goto L_0x0058;
    L_0x0070:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0079;
    L_0x0074:
        r6 = "write byte array error";
        org.telegram.messenger.FileLog.m1e(r6);
    L_0x0079:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeByteArray(byte[]):void");
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.writeDouble(double):void");
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
            nativeByteBuffer = limit <= 253 ? 1 : 4;
            while ((limit + nativeByteBuffer) % 4 != 0) {
                if (this.justCalc) {
                    this.len++;
                } else {
                    this.buffer.put((byte) 0);
                }
                nativeByteBuffer++;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
                this.buffer.position(this.buffer.position() + i);
            }
        }
    }

    public int getPosition() {
        return this.buffer.position();
    }

    public int readInt32(boolean z) {
        try {
            return this.buffer.getInt();
        } catch (Throwable e) {
            if (z) {
                throw new RuntimeException("read int32 error", e);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m1e("read int32 error");
            }
            return false;
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
        if (z) {
            throw new RuntimeException("Not bool value!");
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m1e("Not bool value!");
        }
        return false;
    }

    public long readInt64(boolean z) {
        try {
            return this.buffer.getLong();
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

    public void readBytes(byte[] bArr, boolean z) {
        try {
            this.buffer.get(bArr);
        } catch (byte[] bArr2) {
            if (z) {
                throw new RuntimeException("read raw error", bArr2);
            } else if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("read raw error");
            }
        }
    }

    public void readBytes(byte[] bArr, int i, int i2, boolean z) {
        try {
            this.buffer.get(bArr, i, i2);
        } catch (byte[] bArr2) {
            if (z) {
                throw new RuntimeException("read raw error", bArr2);
            } else if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("read raw error");
            }
        }
    }

    public byte[] readData(int i, boolean z) {
        i = new byte[i];
        readBytes(i, z);
        return i;
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
