package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build.VERSION;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;

public class AudioRecordJNI {
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
    }

    public void start() {
        if (this.thread == null) {
            startThread();
        } else {
            this.audioRecord.startRecording();
        }
    }

    private void startThread() {
        if (this.thread != null) {
            throw new IllegalStateException("thread already started");
        }
        this.running = true;
        this.thread = new Thread(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (AudioRecordJNI.this) {
                    if (AudioRecordJNI.this.audioRecord == null) {
                        return;
                    }
                    AudioRecordJNI.this.audioRecord.startRecording();
                    if (VERSION.SDK_INT >= 16) {
                        if (AutomaticGainControl.isAvailable()) {
                            AudioRecordJNI.this.agc = AutomaticGainControl.create(AudioRecordJNI.this.audioRecord.getAudioSessionId());
                            if (AudioRecordJNI.this.agc != null) {
                                AudioRecordJNI.this.agc.setEnabled(false);
                            }
                        } else {
                            FileLog.w("AutomaticGainControl is not available on this device :(");
                        }
                        if (NoiseSuppressor.isAvailable()) {
                            AudioRecordJNI.this.ns = NoiseSuppressor.create(AudioRecordJNI.this.audioRecord.getAudioSessionId());
                            if (AudioRecordJNI.this.ns != null) {
                                AudioRecordJNI.this.ns.setEnabled(true);
                            }
                        } else {
                            FileLog.w("NoiseSuppressor is not available on this device :(");
                        }
                    }
                }
            }
        });
        this.thread.start();
    }
}
