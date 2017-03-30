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

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioRecord != null) {
            throw new IllegalStateException("already inited");
        }
        int size = getBufferSize(bufferSize);
        this.bufferSize = bufferSize;
        try {
            this.audioRecord = new AudioRecord(7, sampleRate, channels == 1 ? 16 : 12, 2, size);
        } catch (Exception x) {
            FileLog.e("AudioRecord init failed!", x);
        }
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
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
                        } else {
                            FileLog.w("AutomaticGainControl is not available on this device :(");
                        }
                    } catch (Throwable x) {
                        FileLog.e("error creating AutomaticGainControl", x);
                    }
                    try {
                        if (NoiseSuppressor.isAvailable()) {
                            this.ns = NoiseSuppressor.create(this.audioRecord.getAudioSessionId());
                            if (this.ns != null) {
                                this.ns.setEnabled(VoIPServerConfig.getBoolean("user_system_ns", true));
                            }
                        } else {
                            FileLog.w("NoiseSuppressor is not available on this device :(");
                        }
                    } catch (Throwable x2) {
                        FileLog.e("error creating NoiseSuppressor", x2);
                    }
                    try {
                        if (AcousticEchoCanceler.isAvailable()) {
                            this.aec = AcousticEchoCanceler.create(this.audioRecord.getAudioSessionId());
                            if (this.aec != null) {
                                this.aec.setEnabled(VoIPServerConfig.getBoolean("use_system_aec", true));
                            }
                        } else {
                            FileLog.w("AcousticEchoCanceler is not available on this device");
                        }
                    } catch (Throwable x22) {
                        FileLog.e("error creating AcousticEchoCanceler", x22);
                    }
                }
                startThread();
            }
            return true;
        } catch (Exception x3) {
            FileLog.e("Error initializing AudioRecord", x3);
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
