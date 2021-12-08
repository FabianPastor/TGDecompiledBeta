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
    private AudioRecord audioRecord;
    private ByteBuffer buffer;
    private int bufferSize;
    private long nativeInst;
    private boolean needResampling = false;
    private NoiseSuppressor ns;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(ByteBuffer byteBuffer);

    public AudioRecordJNI(long ptr) {
        this.nativeInst = ptr;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioRecord.getMinBufferSize(sampleRate, 16, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize2) {
        if (this.audioRecord == null) {
            this.bufferSize = bufferSize2;
            boolean res = tryInit(7, 48000);
            boolean z = true;
            if (!res) {
                res = tryInit(1, 48000);
            }
            if (!res) {
                res = tryInit(7, 44100);
            }
            if (!res) {
                res = tryInit(1, 44100);
            }
            if (res) {
                if (Build.VERSION.SDK_INT >= 16) {
                    try {
                        if (AutomaticGainControl.isAvailable()) {
                            AutomaticGainControl create = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                            this.agc = create;
                            if (create != null) {
                                create.setEnabled(false);
                            }
                        } else {
                            VLog.w("AutomaticGainControl is not available on this device :(");
                        }
                    } catch (Throwable x) {
                        VLog.e("error creating AutomaticGainControl", x);
                    }
                    try {
                        if (NoiseSuppressor.isAvailable()) {
                            NoiseSuppressor create2 = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                            this.ns = create2;
                            if (create2 != null) {
                                create2.setEnabled(Instance.getGlobalServerConfig().useSystemNs && isGoodAudioEffect(this.ns));
                            }
                        } else {
                            VLog.w("NoiseSuppressor is not available on this device :(");
                        }
                    } catch (Throwable x2) {
                        VLog.e("error creating NoiseSuppressor", x2);
                    }
                    try {
                        if (AcousticEchoCanceler.isAvailable()) {
                            AcousticEchoCanceler create3 = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                            this.aec = create3;
                            if (create3 != null) {
                                if (!Instance.getGlobalServerConfig().useSystemAec || !isGoodAudioEffect(this.aec)) {
                                    z = false;
                                }
                                create3.setEnabled(z);
                            }
                        } else {
                            VLog.w("AcousticEchoCanceler is not available on this device");
                        }
                    } catch (Throwable x3) {
                        VLog.e("error creating AcousticEchoCanceler", x3);
                    }
                }
                this.buffer = ByteBuffer.allocateDirect(bufferSize2);
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    private boolean tryInit(int source, int sampleRate) {
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            try {
                audioRecord2.release();
            } catch (Exception e) {
            }
        }
        VLog.i("Trying to initialize AudioRecord with source=" + source + " and sample rate=" + sampleRate);
        try {
            this.audioRecord = new AudioRecord(source, sampleRate, 16, 2, getBufferSize(this.bufferSize, 48000));
        } catch (Exception x) {
            VLog.e("AudioRecord init failed!", x);
        }
        this.needResampling = sampleRate != 48000;
        AudioRecord audioRecord3 = this.audioRecord;
        if (audioRecord3 == null || audioRecord3.getState() != 1) {
            return false;
        }
        return true;
    }

    public void stop() {
        try {
            AudioRecord audioRecord2 = this.audioRecord;
            if (audioRecord2 != null) {
                audioRecord2.stop();
            }
        } catch (Exception e) {
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
        if (audioRecord2 == null || audioRecord2.getState() != 1) {
            return false;
        }
        try {
            if (this.thread == null) {
                AudioRecord audioRecord3 = this.audioRecord;
                if (audioRecord3 == null) {
                    return false;
                }
                audioRecord3.startRecording();
                startThread();
            } else {
                this.audioRecord.startRecording();
            }
            return true;
        } catch (Exception x) {
            VLog.e("Error initializing AudioRecord", x);
            return false;
        }
    }

    private void startThread() {
        if (this.thread == null) {
            this.running = true;
            Thread thread2 = new Thread(new AudioRecordJNI$$ExternalSyntheticLambda0(this, this.needResampling ? ByteBuffer.allocateDirect(1764) : null));
            this.thread = thread2;
            thread2.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }

    /* renamed from: lambda$startThread$0$org-telegram-messenger-voip-AudioRecordJNI  reason: not valid java name */
    public /* synthetic */ void m1188lambda$startThread$0$orgtelegrammessengervoipAudioRecordJNI(ByteBuffer tmpBuf) {
        while (true) {
            if (!this.running) {
                break;
            }
            try {
                if (!this.needResampling) {
                    this.audioRecord.read(this.buffer, 1920);
                } else {
                    this.audioRecord.read(tmpBuf, 1764);
                    Resampler.convert44to48(tmpBuf, this.buffer);
                }
                if (!this.running) {
                    this.audioRecord.stop();
                    break;
                }
                nativeCallback(this.buffer);
            } catch (Exception e) {
                VLog.e((Throwable) e);
            }
        }
        VLog.i("audiorecord thread exits");
    }

    public int getEnabledEffectsMask() {
        int r = 0;
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        if (acousticEchoCanceler != null && acousticEchoCanceler.getEnabled()) {
            r = 0 | 1;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor == null || !noiseSuppressor.getEnabled()) {
            return r;
        }
        return r | 2;
    }

    private static Pattern makeNonEmptyRegex(String configKey) {
        String r = Instance.getGlobalServerConfig().getString(configKey);
        if (TextUtils.isEmpty(r)) {
            return null;
        }
        try {
            return Pattern.compile(r);
        } catch (Exception x) {
            VLog.e((Throwable) x);
            return null;
        }
    }

    private static boolean isGoodAudioEffect(AudioEffect effect) {
        Pattern globalImpl = makeNonEmptyRegex("adsp_good_impls");
        Pattern globalName = makeNonEmptyRegex("adsp_good_names");
        AudioEffect.Descriptor desc = effect.getDescriptor();
        VLog.d(effect.getClass().getSimpleName() + ": implementor=" + desc.implementor + ", name=" + desc.name);
        if (globalImpl != null && globalImpl.matcher(desc.implementor).find()) {
            return true;
        }
        if (globalName != null && globalName.matcher(desc.name).find()) {
            return true;
        }
        if (effect instanceof AcousticEchoCanceler) {
            Pattern impl = makeNonEmptyRegex("aaec_good_impls");
            Pattern name = makeNonEmptyRegex("aaec_good_names");
            if (impl != null && impl.matcher(desc.implementor).find()) {
                return true;
            }
            if (name != null && name.matcher(desc.name).find()) {
                return true;
            }
        }
        if (!(effect instanceof NoiseSuppressor)) {
            return false;
        }
        Pattern impl2 = makeNonEmptyRegex("ans_good_impls");
        Pattern name2 = makeNonEmptyRegex("ans_good_names");
        if (impl2 != null && impl2.matcher(desc.implementor).find()) {
            return true;
        }
        if (name2 == null || !name2.matcher(desc.name).find()) {
            return false;
        }
        return true;
    }
}
