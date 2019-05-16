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

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0061 in {3, 4, 9, 10, 16, 18, 20} preds:[]
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
    public byte[] readByteArray(boolean r6) {
        /*
        r5 = this;
        r0 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r0 = r0.get();	 Catch:{ Exception -> 0x004b }
        r0 = r5.getIntFromByte(r0);	 Catch:{ Exception -> 0x004b }
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x004b }
        r2 = 4;	 Catch:{ Exception -> 0x004b }
        if (r0 < r1) goto L_0x0035;	 Catch:{ Exception -> 0x004b }
        r0 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r0 = r0.get();	 Catch:{ Exception -> 0x004b }
        r0 = r5.getIntFromByte(r0);	 Catch:{ Exception -> 0x004b }
        r1 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r1 = r1.get();	 Catch:{ Exception -> 0x004b }
        r1 = r5.getIntFromByte(r1);	 Catch:{ Exception -> 0x004b }
        r1 = r1 << 8;	 Catch:{ Exception -> 0x004b }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x004b }
        r1 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r1 = r1.get();	 Catch:{ Exception -> 0x004b }
        r1 = r5.getIntFromByte(r1);	 Catch:{ Exception -> 0x004b }
        r1 = r1 << 16;	 Catch:{ Exception -> 0x004b }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x004b }
        r1 = 4;	 Catch:{ Exception -> 0x004b }
        goto L_0x0036;	 Catch:{ Exception -> 0x004b }
        r1 = 1;	 Catch:{ Exception -> 0x004b }
        r3 = new byte[r0];	 Catch:{ Exception -> 0x004b }
        r4 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r4.get(r3);	 Catch:{ Exception -> 0x004b }
        r4 = r0 + r1;	 Catch:{ Exception -> 0x004b }
        r4 = r4 % r2;	 Catch:{ Exception -> 0x004b }
        if (r4 == 0) goto L_0x004a;	 Catch:{ Exception -> 0x004b }
        r4 = r5.buffer;	 Catch:{ Exception -> 0x004b }
        r4.get();	 Catch:{ Exception -> 0x004b }
        r1 = r1 + 1;
        goto L_0x003d;
        return r3;
        r0 = move-exception;
        r1 = "read byte array error";
        if (r6 != 0) goto L_0x005b;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0057;
        org.telegram.messenger.FileLog.e(r1);
        r6 = 0;
        r6 = new byte[r6];
        return r6;
        r6 = new java.lang.RuntimeException;
        r6.<init>(r1, r0);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.readByteArray(boolean):byte[]");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0081 in {3, 4, 9, 10, 16, 18, 20} preds:[]
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
    public org.telegram.tgnet.NativeByteBuffer readByteBuffer(boolean r8) {
        /*
        r7 = this;
        r0 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r0 = r0.get();	 Catch:{ Exception -> 0x006d }
        r0 = r7.getIntFromByte(r0);	 Catch:{ Exception -> 0x006d }
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x006d }
        r2 = 4;	 Catch:{ Exception -> 0x006d }
        if (r0 < r1) goto L_0x0035;	 Catch:{ Exception -> 0x006d }
        r0 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r0 = r0.get();	 Catch:{ Exception -> 0x006d }
        r0 = r7.getIntFromByte(r0);	 Catch:{ Exception -> 0x006d }
        r1 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r1 = r1.get();	 Catch:{ Exception -> 0x006d }
        r1 = r7.getIntFromByte(r1);	 Catch:{ Exception -> 0x006d }
        r1 = r1 << 8;	 Catch:{ Exception -> 0x006d }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x006d }
        r1 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r1 = r1.get();	 Catch:{ Exception -> 0x006d }
        r1 = r7.getIntFromByte(r1);	 Catch:{ Exception -> 0x006d }
        r1 = r1 << 16;	 Catch:{ Exception -> 0x006d }
        r0 = r0 | r1;	 Catch:{ Exception -> 0x006d }
        r1 = 4;	 Catch:{ Exception -> 0x006d }
        goto L_0x0036;	 Catch:{ Exception -> 0x006d }
        r1 = 1;	 Catch:{ Exception -> 0x006d }
        r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x006d }
        r3.<init>(r0);	 Catch:{ Exception -> 0x006d }
        r4 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r4 = r4.limit();	 Catch:{ Exception -> 0x006d }
        r5 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r6 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r6 = r6.position();	 Catch:{ Exception -> 0x006d }
        r6 = r6 + r0;	 Catch:{ Exception -> 0x006d }
        r5.limit(r6);	 Catch:{ Exception -> 0x006d }
        r5 = r3.buffer;	 Catch:{ Exception -> 0x006d }
        r6 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r5.put(r6);	 Catch:{ Exception -> 0x006d }
        r5 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r5.limit(r4);	 Catch:{ Exception -> 0x006d }
        r4 = r3.buffer;	 Catch:{ Exception -> 0x006d }
        r5 = 0;	 Catch:{ Exception -> 0x006d }
        r4.position(r5);	 Catch:{ Exception -> 0x006d }
        r4 = r0 + r1;	 Catch:{ Exception -> 0x006d }
        r4 = r4 % r2;	 Catch:{ Exception -> 0x006d }
        if (r4 == 0) goto L_0x006c;	 Catch:{ Exception -> 0x006d }
        r4 = r7.buffer;	 Catch:{ Exception -> 0x006d }
        r4.get();	 Catch:{ Exception -> 0x006d }
        r1 = r1 + 1;
        goto L_0x005f;
        return r3;
        r0 = move-exception;
        r1 = "read byte array error";
        if (r8 != 0) goto L_0x007b;
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r8 == 0) goto L_0x0079;
        org.telegram.messenger.FileLog.e(r1);
        r8 = 0;
        return r8;
        r8 = new java.lang.RuntimeException;
        r8.<init>(r1, r0);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.readByteBuffer(boolean):org.telegram.tgnet.NativeByteBuffer");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x006e in {4, 5, 9, 11, 17, 19, 21} preds:[]
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
        r0 = r6.getPosition();
        r1 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r1 = r1.get();	 Catch:{ Exception -> 0x0056 }
        r1 = r6.getIntFromByte(r1);	 Catch:{ Exception -> 0x0056 }
        r2 = 254; // 0xfe float:3.56E-43 double:1.255E-321;	 Catch:{ Exception -> 0x0056 }
        r3 = 4;	 Catch:{ Exception -> 0x0056 }
        if (r1 < r2) goto L_0x0039;	 Catch:{ Exception -> 0x0056 }
        r1 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r1 = r1.get();	 Catch:{ Exception -> 0x0056 }
        r1 = r6.getIntFromByte(r1);	 Catch:{ Exception -> 0x0056 }
        r2 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r2 = r2.get();	 Catch:{ Exception -> 0x0056 }
        r2 = r6.getIntFromByte(r2);	 Catch:{ Exception -> 0x0056 }
        r2 = r2 << 8;	 Catch:{ Exception -> 0x0056 }
        r1 = r1 | r2;	 Catch:{ Exception -> 0x0056 }
        r2 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r2 = r2.get();	 Catch:{ Exception -> 0x0056 }
        r2 = r6.getIntFromByte(r2);	 Catch:{ Exception -> 0x0056 }
        r2 = r2 << 16;	 Catch:{ Exception -> 0x0056 }
        r1 = r1 | r2;	 Catch:{ Exception -> 0x0056 }
        r2 = 4;	 Catch:{ Exception -> 0x0056 }
        goto L_0x003a;	 Catch:{ Exception -> 0x0056 }
        r2 = 1;	 Catch:{ Exception -> 0x0056 }
        r4 = new byte[r1];	 Catch:{ Exception -> 0x0056 }
        r5 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r5.get(r4);	 Catch:{ Exception -> 0x0056 }
        r5 = r1 + r2;	 Catch:{ Exception -> 0x0056 }
        r5 = r5 % r3;	 Catch:{ Exception -> 0x0056 }
        if (r5 == 0) goto L_0x004e;	 Catch:{ Exception -> 0x0056 }
        r5 = r6.buffer;	 Catch:{ Exception -> 0x0056 }
        r5.get();	 Catch:{ Exception -> 0x0056 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0056 }
        goto L_0x0041;	 Catch:{ Exception -> 0x0056 }
        r1 = new java.lang.String;	 Catch:{ Exception -> 0x0056 }
        r2 = "UTF-8";	 Catch:{ Exception -> 0x0056 }
        r1.<init>(r4, r2);	 Catch:{ Exception -> 0x0056 }
        return r1;
        r1 = move-exception;
        r2 = "read string error";
        if (r7 != 0) goto L_0x0068;
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r7 == 0) goto L_0x0062;
        org.telegram.messenger.FileLog.e(r2);
        r6.position(r0);
        r7 = "";
        return r7;
        r7 = new java.lang.RuntimeException;
        r7.<init>(r2, r1);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.NativeByteBuffer.readString(boolean):java.lang.String");
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
