package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.text.TextUtils;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

public class AudioRecordJNI {
    private AcousticEchoCanceler aec;
    private AutomaticGainControl agc;
    /* access modifiers changed from: private */
    public AudioRecord audioRecord;
    /* access modifiers changed from: private */
    public ByteBuffer buffer;
    private int bufferSize;
    private long nativeInst;
    /* access modifiers changed from: private */
    public boolean needResampling = false;
    private NoiseSuppressor ns;
    /* access modifiers changed from: private */
    public boolean running;
    private Thread thread;

    /* access modifiers changed from: private */
    public native void nativeCallback(ByteBuffer byteBuffer);

    public AudioRecordJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioRecord.getMinBufferSize(i2, 16, 2), i);
    }

    public void init(int i, int i2, int i3, int i4) {
        if (this.audioRecord == null) {
            this.bufferSize = i4;
            boolean tryInit = tryInit(7, 48000);
            if (!tryInit) {
                tryInit = tryInit(1, 48000);
            }
            if (!tryInit) {
                tryInit = tryInit(7, 44100);
            }
            if (!tryInit) {
                tryInit = tryInit(1, 44100);
            }
            if (tryInit) {
                if (Build.VERSION.SDK_INT >= 16) {
                    boolean z = false;
                    try {
                        if (AutomaticGainControl.isAvailable()) {
                            this.agc = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                            if (this.agc != null) {
                                this.agc.setEnabled(false);
                            }
                        } else {
                            VLog.w("AutomaticGainControl is not available on this device :(");
                        }
                    } catch (Throwable th) {
                        VLog.e("error creating AutomaticGainControl", th);
                    }
                    try {
                        if (NoiseSuppressor.isAvailable()) {
                            this.ns = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                            if (this.ns != null) {
                                this.ns.setEnabled(VoIPServerConfig.getBoolean("use_system_ns", true) && isGoodAudioEffect(this.ns));
                            }
                        } else {
                            VLog.w("NoiseSuppressor is not available on this device :(");
                        }
                    } catch (Throwable th2) {
                        VLog.e("error creating NoiseSuppressor", th2);
                    }
                    try {
                        if (AcousticEchoCanceler.isAvailable()) {
                            this.aec = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                            if (this.aec != null) {
                                AcousticEchoCanceler acousticEchoCanceler = this.aec;
                                if (VoIPServerConfig.getBoolean("use_system_aec", true) && isGoodAudioEffect(this.aec)) {
                                    z = true;
                                }
                                acousticEchoCanceler.setEnabled(z);
                            }
                        } else {
                            VLog.w("AcousticEchoCanceler is not available on this device");
                        }
                    } catch (Throwable th3) {
                        VLog.e("error creating AcousticEchoCanceler", th3);
                    }
                }
                this.buffer = ByteBuffer.allocateDirect(i4);
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    private boolean tryInit(int i, int i2) {
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            try {
                audioRecord2.release();
            } catch (Exception unused) {
            }
        }
        VLog.i("Trying to initialize AudioRecord with source=" + i + " and sample rate=" + i2);
        try {
            this.audioRecord = new AudioRecord(i, i2, 16, 2, getBufferSize(this.bufferSize, 48000));
        } catch (Exception e) {
            VLog.e("AudioRecord init failed!", e);
        }
        this.needResampling = i2 != 48000;
        AudioRecord audioRecord3 = this.audioRecord;
        if (audioRecord3 == null || audioRecord3.getState() != 1) {
            return false;
        }
        return true;
    }

    public void stop() {
        try {
            if (this.audioRecord != null) {
                this.audioRecord.stop();
            }
        } catch (Exception unused) {
        }
    }

    public void release() {
        this.running = false;
        Thread thread2 = this.thread;
        if (thread2 != null) {
            try {
                thread2.join();
            } catch (InterruptedException e) {
                VLog.e((Throwable) e);
            }
            this.thread = null;
        }
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            audioRecord2.release();
            this.audioRecord = null;
        }
        AutomaticGainControl automaticGainControl = this.agc;
        if (automaticGainControl != null) {
            automaticGainControl.release();
            this.agc = null;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor != null) {
            noiseSuppressor.release();
            this.ns = null;
        }
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler.release();
            this.aec = null;
        }
    }

    public boolean start() {
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null && audioRecord2.getState() == 1) {
            try {
                if (this.thread != null) {
                    this.audioRecord.startRecording();
                } else if (this.audioRecord == null) {
                    return false;
                } else {
                    this.audioRecord.startRecording();
                    startThread();
                }
                return true;
            } catch (Exception e) {
                VLog.e("Error initializing AudioRecord", e);
            }
        }
        return false;
    }

    private void startThread() {
        if (this.thread == null) {
            this.running = true;
            final ByteBuffer allocateDirect = this.needResampling ? ByteBuffer.allocateDirect(1764) : null;
            this.thread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (!AudioRecordJNI.this.running) {
                            break;
                        }
                        try {
                            if (!AudioRecordJNI.this.needResampling) {
                                AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
                            } else {
                                AudioRecordJNI.this.audioRecord.read(allocateDirect, 1764);
                                Resampler.convert44to48(allocateDirect, AudioRecordJNI.this.buffer);
                            }
                            if (!AudioRecordJNI.this.running) {
                                AudioRecordJNI.this.audioRecord.stop();
                                break;
                            }
                            AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
                        } catch (Exception e) {
                            VLog.e((Throwable) e);
                        }
                    }
                    VLog.i("audiorecord thread exits");
                }
            });
            this.thread.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }

    public int getEnabledEffectsMask() {
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        int i = (acousticEchoCanceler == null || !acousticEchoCanceler.getEnabled()) ? 0 : 1;
        NoiseSuppressor noiseSuppressor = this.ns;
        return (noiseSuppressor == null || !noiseSuppressor.getEnabled()) ? i : i | 2;
    }

    private static Pattern makeNonEmptyRegex(String str) {
        String string = VoIPServerConfig.getString(str, "");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            return Pattern.compile(string);
        } catch (Exception e) {
            VLog.e((Throwable) e);
            return null;
        }
    }

    private static boolean isGoodAudioEffect(AudioEffect audioEffect) {
        Pattern makeNonEmptyRegex = makeNonEmptyRegex("adsp_good_impls");
        Pattern makeNonEmptyRegex2 = makeNonEmptyRegex("adsp_good_names");
        AudioEffect.Descriptor descriptor = audioEffect.getDescriptor();
        VLog.d(audioEffect.getClass().getSimpleName() + ": implementor=" + descriptor.implementor + ", name=" + descriptor.name);
        if (makeNonEmptyRegex != null && makeNonEmptyRegex.matcher(descriptor.implementor).find()) {
            return true;
        }
        if (makeNonEmptyRegex2 != null && makeNonEmptyRegex2.matcher(descriptor.name).find()) {
            return true;
        }
        if (audioEffect instanceof AcousticEchoCanceler) {
            Pattern makeNonEmptyRegex3 = makeNonEmptyRegex("aaec_good_impls");
            Pattern makeNonEmptyRegex4 = makeNonEmptyRegex("aaec_good_names");
            if (makeNonEmptyRegex3 != null && makeNonEmptyRegex3.matcher(descriptor.implementor).find()) {
                return true;
            }
            if (makeNonEmptyRegex4 != null && makeNonEmptyRegex4.matcher(descriptor.name).find()) {
                return true;
            }
        }
        if (!(audioEffect instanceof NoiseSuppressor)) {
            return false;
        }
        Pattern makeNonEmptyRegex5 = makeNonEmptyRegex("ans_good_impls");
        Pattern makeNonEmptyRegex6 = makeNonEmptyRegex("ans_good_names");
        if (makeNonEmptyRegex5 != null && makeNonEmptyRegex5.matcher(descriptor.implementor).find()) {
            return true;
        }
        if (makeNonEmptyRegex6 == null || !makeNonEmptyRegex6.matcher(descriptor.name).find()) {
            return false;
        }
        return true;
    }
}
