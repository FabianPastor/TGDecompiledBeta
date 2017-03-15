package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build.VERSION;
import android.util.Log;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;

public class AudioRecordJNI {
    private AcousticEchoCanceler aec;
    private AutomaticGainControl agc;
    private AudioRecord audioRecord;
    private ByteBuffer buffer;
    private int bufferSize;
    private long nativeInst;
    private NoiseSuppressor ns;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(ByteBuffer byteBuffer);

    public AudioRecordJNI(long nativeInst) {
        this.nativeInst = nativeInst;
    }

    private int getBufferSize(int min) {
        return Math.max(AudioRecord.getMinBufferSize(48000, 16, 2), min);
    }

    public synchronized void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioRecord != null) {
            throw new IllegalStateException("already inited");
        }
        int size = getBufferSize(bufferSize);
        this.bufferSize = bufferSize;
        this.audioRecord = new AudioRecord(7, sampleRate, channels == 1 ? 16 : 12, 2, size);
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
    }

    public synchronized void stop() {
        if (this.audioRecord != null) {
            this.audioRecord.stop();
        }
    }

    public synchronized void release() {
        this.running = false;
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (Throwable e) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start() {
        try {
            if (this.thread == null) {
                synchronized (this) {
                    if (this.audioRecord == null) {
                        return false;
                    }
                    this.audioRecord.startRecording();
                    if (VERSION.SDK_INT >= 16) {
                        if (AutomaticGainControl.isAvailable()) {
                            this.agc = AutomaticGainControl.create(this.audioRecord.getAudioSessionId());
                            if (this.agc != null) {
                                this.agc.setEnabled(false);
                            }
                        } else {
                            FileLog.w("AutomaticGainControl is not available on this device :(");
                        }
                        if (NoiseSuppressor.isAvailable()) {
                            this.ns = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                            if (this.ns != null) {
                                this.ns.setEnabled(VoIPServerConfig.getBoolean("user_system_ns", true));
                            }
                        } else {
                            FileLog.w("NoiseSuppressor is not available on this device :(");
                        }
                        if (AcousticEchoCanceler.isAvailable()) {
                            this.aec = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                            if (this.aec != null) {
                                this.aec.setEnabled(VoIPServerConfig.getBoolean("use_system_aec", true));
                            }
                        } else {
                            FileLog.w("AcousticEchoCanceler is not available on this device");
                        }
                    }
                }
            } else {
                this.audioRecord.startRecording();
            }
            return true;
        } catch (Exception x) {
            FileLog.e("Error initializing AudioRecord", x);
            return false;
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        this.thread = new Thread(new Runnable() {
            public void run() {
                while (AudioRecordJNI.this.running) {
                    try {
                        AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
                        if (!AudioRecordJNI.this.running) {
                            AudioRecordJNI.this.audioRecord.stop();
                            break;
                        }
                        AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                Log.i("tg-voip", "audiotrack thread exits");
            }
        });
        this.thread.start();
    }
}
