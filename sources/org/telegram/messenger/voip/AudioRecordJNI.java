package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.Descriptor;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;
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

    public AudioRecordJNI(long nativeInst) {
        this.nativeInst = nativeInst;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioRecord.getMinBufferSize(sampleRate, 16, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        boolean z = true;
        if (this.audioRecord != null) {
            throw new IllegalStateException("already inited");
        }
        this.bufferSize = bufferSize;
        boolean res = tryInit(7, 48000);
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
            if (VERSION.SDK_INT >= 16) {
                try {
                    if (AutomaticGainControl.isAvailable()) {
                        this.agc = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                        if (this.agc != null) {
                            this.agc.setEnabled(false);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("AutomaticGainControl is not available on this device :(");
                    }
                } catch (Throwable x) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error creating AutomaticGainControl", x);
                    }
                }
                try {
                    if (NoiseSuppressor.isAvailable()) {
                        this.ns = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                        if (this.ns != null) {
                            boolean z2;
                            NoiseSuppressor noiseSuppressor = this.ns;
                            if (VoIPServerConfig.getBoolean("use_system_ns", true) && isGoodAudioEffect(this.ns)) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            noiseSuppressor.setEnabled(z2);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("NoiseSuppressor is not available on this device :(");
                    }
                } catch (Throwable x2) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error creating NoiseSuppressor", x2);
                    }
                }
                try {
                    if (AcousticEchoCanceler.isAvailable()) {
                        this.aec = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                        if (this.aec != null) {
                            AcousticEchoCanceler acousticEchoCanceler = this.aec;
                            if (!(VoIPServerConfig.getBoolean("use_system_aec", true) && isGoodAudioEffect(this.aec))) {
                                z = false;
                            }
                            acousticEchoCanceler.setEnabled(z);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("AcousticEchoCanceler is not available on this device");
                    }
                } catch (Throwable x22) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error creating AcousticEchoCanceler", x22);
                    }
                }
            }
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
        }
    }

    private boolean tryInit(int source, int sampleRate) {
        boolean z;
        if (this.audioRecord != null) {
            try {
                this.audioRecord.release();
            } catch (Exception e) {
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Trying to initialize AudioRecord with source=" + source + " and sample rate=" + sampleRate);
        }
        try {
            this.audioRecord = new AudioRecord(source, sampleRate, 16, 2, getBufferSize(this.bufferSize, 48000));
        } catch (Exception x) {
            FileLog.e("AudioRecord init failed!", x);
        }
        if (sampleRate != 48000) {
            z = true;
        } else {
            z = false;
        }
        this.needResampling = z;
        if (this.audioRecord == null || this.audioRecord.getState() != 1) {
            return false;
        }
        return true;
    }

    public void stop() {
        try {
            if (this.audioRecord != null) {
                this.audioRecord.stop();
            }
        } catch (Exception e) {
        }
    }

    public void release() {
        this.running = false;
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                FileLog.e(e);
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
        if (this.audioRecord == null || this.audioRecord.getState() != 1) {
            return false;
        }
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
        } catch (Exception x) {
            if (!BuildVars.LOGS_ENABLED) {
                return false;
            }
            FileLog.e("Error initializing AudioRecord", x);
            return false;
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        final ByteBuffer tmpBuf = this.needResampling ? ByteBuffer.allocateDirect(1764) : null;
        this.thread = new Thread(new Runnable() {
            public void run() {
                while (AudioRecordJNI.this.running) {
                    try {
                        if (AudioRecordJNI.this.needResampling) {
                            AudioRecordJNI.this.audioRecord.read(tmpBuf, 1764);
                            Resampler.convert44to48(tmpBuf, AudioRecordJNI.this.buffer);
                        } else {
                            AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
                        }
                        if (!AudioRecordJNI.this.running) {
                            AudioRecordJNI.this.audioRecord.stop();
                            break;
                        }
                        AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("audiorecord thread exits");
                }
            }
        });
        this.thread.start();
    }

    public int getEnabledEffectsMask() {
        int r = 0;
        if (this.aec != null && this.aec.getEnabled()) {
            r = 0 | 1;
        }
        if (this.ns == null || !this.ns.getEnabled()) {
            return r;
        }
        return r | 2;
    }

    private static Pattern makeNonEmptyRegex(String configKey) {
        Pattern pattern = null;
        String r = VoIPServerConfig.getString(configKey, "");
        if (TextUtils.isEmpty(r)) {
            return pattern;
        }
        try {
            return Pattern.compile(r);
        } catch (Exception x) {
            FileLog.e(x);
            return pattern;
        }
    }

    private static boolean isGoodAudioEffect(AudioEffect effect) {
        Pattern globalImpl = makeNonEmptyRegex("adsp_good_impls");
        Pattern globalName = makeNonEmptyRegex("adsp_good_names");
        Descriptor desc = effect.getDescriptor();
        FileLog.d(effect.getClass().getSimpleName() + ": implementor=" + desc.implementor + ", name=" + desc.name);
        if (globalImpl != null && globalImpl.matcher(desc.implementor).find()) {
            return true;
        }
        if (globalName != null && globalName.matcher(desc.name).find()) {
            return true;
        }
        Pattern impl;
        Pattern name;
        if (effect instanceof AcousticEchoCanceler) {
            impl = makeNonEmptyRegex("aaec_good_impls");
            name = makeNonEmptyRegex("aaec_good_names");
            if (impl != null && impl.matcher(desc.implementor).find()) {
                return true;
            }
            if (name != null && name.matcher(desc.name).find()) {
                return true;
            }
        }
        if (effect instanceof NoiseSuppressor) {
            impl = makeNonEmptyRegex("ans_good_impls");
            name = makeNonEmptyRegex("ans_good_names");
            if (impl != null && impl.matcher(desc.implementor).find()) {
                return true;
            }
            if (name != null && name.matcher(desc.name).find()) {
                return true;
            }
        }
        return false;
    }
}
