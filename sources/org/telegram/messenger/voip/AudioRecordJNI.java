package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build.VERSION;
import android.util.Log;
import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class AudioRecordJNI {
    private AcousticEchoCanceler aec;
    private AutomaticGainControl agc;
    private AudioRecord audioRecord;
    private ByteBuffer buffer;
    private int bufferSize;
    private long nativeInst;
    private boolean needResampling = false;
    private NoiseSuppressor ns;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(ByteBuffer byteBuffer);

    public AudioRecordJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioRecord.getMinBufferSize(i2, 16, 2), i);
    }

    public void init(int i, int i2, int i3, int i4) {
        if (this.audioRecord != 0) {
            throw new IllegalStateException("already inited");
        }
        this.bufferSize = i4;
        i3 = tryInit(7, 48000);
        if (i3 == 0) {
            tryInit(1, 48000);
        }
        if (i3 == 0) {
            tryInit(7, 44100);
        }
        if (i3 == 0) {
            tryInit(1, 44100);
        }
        this.buffer = ByteBuffer.allocateDirect(i4);
    }

    private boolean tryInit(int r9, int r10) {
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
        r8 = this;
        r0 = r8.audioRecord;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = r8.audioRecord;	 Catch:{ Exception -> 0x0009 }
        r0.release();	 Catch:{ Exception -> 0x0009 }
    L_0x0009:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0029;
    L_0x000d:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Trying to initialize AudioRecord with source=";
        r0.append(r1);
        r0.append(r9);
        r1 = " and sample rate=";
        r0.append(r1);
        r0.append(r10);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.m0d(r0);
    L_0x0029:
        r0 = r8.bufferSize;
        r1 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;
        r7 = r8.getBufferSize(r0, r1);
        r0 = new android.media.AudioRecord;	 Catch:{ Exception -> 0x0040 }
        r5 = 16;	 Catch:{ Exception -> 0x0040 }
        r6 = 2;	 Catch:{ Exception -> 0x0040 }
        r2 = r0;	 Catch:{ Exception -> 0x0040 }
        r3 = r9;	 Catch:{ Exception -> 0x0040 }
        r4 = r10;	 Catch:{ Exception -> 0x0040 }
        r2.<init>(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0040 }
        r8.audioRecord = r0;	 Catch:{ Exception -> 0x0040 }
        goto L_0x0046;
    L_0x0040:
        r9 = move-exception;
        r0 = "AudioRecord init failed!";
        org.telegram.messenger.FileLog.m2e(r0, r9);
    L_0x0046:
        r9 = 0;
        r0 = 1;
        if (r10 == r1) goto L_0x004c;
    L_0x004a:
        r10 = r0;
        goto L_0x004d;
    L_0x004c:
        r10 = r9;
    L_0x004d:
        r8.needResampling = r10;
        r10 = r8.audioRecord;
        if (r10 == 0) goto L_0x005c;
    L_0x0053:
        r10 = r8.audioRecord;
        r10 = r10.getState();
        if (r10 != r0) goto L_0x005c;
    L_0x005b:
        r9 = r0;
    L_0x005c:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.AudioRecordJNI.tryInit(int, int):boolean");
    }

    public void stop() {
        if (this.audioRecord != null) {
            this.audioRecord.stop();
        }
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
        if (this.audioRecord != null) {
            this.audioRecord.release();
            this.audioRecord = null;
        }
        if (this.agc != null) {
            this.agc.release();
            this.agc = null;
        }
        if (this.ns != null) {
            this.ns.release();
            this.ns = null;
        }
        if (this.aec != null) {
            this.aec.release();
            this.aec = null;
        }
    }

    public boolean start() {
        try {
            if (this.thread != null) {
                this.audioRecord.startRecording();
            } else if (this.audioRecord == null) {
                return false;
            } else {
                this.audioRecord.startRecording();
                if (VERSION.SDK_INT >= 16) {
                    try {
                        if (AutomaticGainControl.isAvailable()) {
                            this.agc = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                            if (this.agc != null) {
                                this.agc.setEnabled(false);
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.m4w("AutomaticGainControl is not available on this device :(");
                        }
                    } catch (Throwable th) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m2e("error creating AutomaticGainControl", th);
                        }
                    }
                    try {
                        if (NoiseSuppressor.isAvailable()) {
                            this.ns = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                            if (this.ns != null) {
                                this.ns.setEnabled(VoIPServerConfig.getBoolean("user_system_ns", true));
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.m4w("NoiseSuppressor is not available on this device :(");
                        }
                    } catch (Throwable th2) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m2e("error creating NoiseSuppressor", th2);
                        }
                    }
                    try {
                        if (AcousticEchoCanceler.isAvailable()) {
                            this.aec = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                            if (this.aec != null) {
                                this.aec.setEnabled(VoIPServerConfig.getBoolean("use_system_aec", true));
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.m4w("AcousticEchoCanceler is not available on this device");
                        }
                    } catch (Throwable th22) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m2e("error creating AcousticEchoCanceler", th22);
                        }
                    }
                }
                startThread();
            }
            return true;
        } catch (Throwable th222) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error initializing AudioRecord", th222);
            }
            return false;
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        final ByteBuffer allocateDirect = this.needResampling ? ByteBuffer.allocateDirect(1764) : null;
        this.thread = new Thread(new Runnable() {
            public void run() {
                while (AudioRecordJNI.this.running) {
                    try {
                        if (AudioRecordJNI.this.needResampling) {
                            AudioRecordJNI.this.audioRecord.read(allocateDirect, 1764);
                            Resampler.convert44to48(allocateDirect, AudioRecordJNI.this.buffer);
                        } else {
                            AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
                        }
                        if (!AudioRecordJNI.this.running) {
                            AudioRecordJNI.this.audioRecord.stop();
                            break;
                        }
                        AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                Log.i("tg-voip", "audiotrack thread exits");
            }
        });
        this.thread.start();
    }
}
