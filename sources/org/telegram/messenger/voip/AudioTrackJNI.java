package org.telegram.messenger.voip;

import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
    private int bufferSize;
    private long nativeInst;
    private boolean needResampling;
    private boolean running;
    private Thread thread;

    /* renamed from: org.telegram.messenger.voip.AudioTrackJNI$1 */
    class C06741 implements Runnable {
        C06741() {
        }

        public void run() {
            try {
                AudioTrackJNI.this.audioTrack.play();
                ByteBuffer byteBuffer = null;
                ByteBuffer allocateDirect = AudioTrackJNI.this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
                if (AudioTrackJNI.this.needResampling) {
                    byteBuffer = ByteBuffer.allocateDirect(1764);
                }
                while (AudioTrackJNI.this.running) {
                    try {
                        if (AudioTrackJNI.this.needResampling) {
                            AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                            allocateDirect.rewind();
                            allocateDirect.put(AudioTrackJNI.this.buffer);
                            Resampler.convert48to44(allocateDirect, byteBuffer);
                            byteBuffer.rewind();
                            byteBuffer.get(AudioTrackJNI.this.buffer, 0, 1764);
                            AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1764);
                        } else {
                            AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                            AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1920);
                        }
                        if (!AudioTrackJNI.this.running) {
                            AudioTrackJNI.this.audioTrack.stop();
                            break;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                Log.i("tg-voip", "audiotrack thread exits");
            } catch (Throwable e2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e("error starting AudioTrack", e2);
                }
            }
        }
    }

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioTrack.getMinBufferSize(i2, 4, 2), i);
    }

    public void init(int r10, int r11, int r12, int r13) {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r10 = r9.audioTrack;
        if (r10 == 0) goto L_0x000c;
    L_0x0004:
        r10 = new java.lang.IllegalStateException;
        r11 = "already inited";
        r10.<init>(r11);
        throw r10;
    L_0x000c:
        r10 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;
        r5 = r9.getBufferSize(r13, r10);
        r9.bufferSize = r13;
        r10 = new android.media.AudioTrack;
        r1 = 0;
        r2 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;
        r11 = 12;
        r7 = 4;
        r8 = 1;
        if (r12 != r8) goto L_0x0023;
    L_0x0021:
        r3 = r7;
        goto L_0x0024;
    L_0x0023:
        r3 = r11;
    L_0x0024:
        r4 = 2;
        r6 = 1;
        r0 = r10;
        r0.<init>(r1, r2, r3, r4, r5, r6);
        r9.audioTrack = r10;
        r10 = r9.audioTrack;
        r10 = r10.getState();
        if (r10 == r8) goto L_0x006f;
    L_0x0034:
        r10 = r9.audioTrack;	 Catch:{ Throwable -> 0x0039 }
        r10.release();	 Catch:{ Throwable -> 0x0039 }
    L_0x0039:
        r13 = r13 * 6;
        r10 = 44100; // 0xac44 float:6.1797E-41 double:2.17883E-319;
        r5 = r9.getBufferSize(r13, r10);
        r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r10 == 0) goto L_0x005a;
    L_0x0046:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r13 = "buffer size: ";
        r10.append(r13);
        r10.append(r5);
        r10 = r10.toString();
        org.telegram.messenger.FileLog.m0d(r10);
    L_0x005a:
        r10 = new android.media.AudioTrack;
        r1 = 0;
        r2 = 44100; // 0xac44 float:6.1797E-41 double:2.17883E-319;
        if (r12 != r8) goto L_0x0064;
    L_0x0062:
        r3 = r7;
        goto L_0x0065;
    L_0x0064:
        r3 = r11;
    L_0x0065:
        r4 = 2;
        r6 = 1;
        r0 = r10;
        r0.<init>(r1, r2, r3, r4, r5, r6);
        r9.audioTrack = r10;
        r9.needResampling = r8;
    L_0x006f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.AudioTrackJNI.init(int, int, int, int):void");
    }

    public void stop() {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.audioTrack;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = r1.audioTrack;	 Catch:{ Exception -> 0x0009 }
        r0.stop();	 Catch:{ Exception -> 0x0009 }
    L_0x0009:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.AudioTrackJNI.stop():void");
    }

    public void release() {
        this.running = false;
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.thread = null;
        }
        if (this.audioTrack != null) {
            this.audioTrack.release();
            this.audioTrack = null;
        }
    }

    public void start() {
        if (this.thread == null) {
            startThread();
        } else {
            this.audioTrack.play();
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        this.thread = new Thread(new C06741());
        this.thread.start();
    }
}
