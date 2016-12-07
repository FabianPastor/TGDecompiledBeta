package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build.VERSION;
import android.util.Log;
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

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioRecord != null) {
            throw new IllegalStateException("already inited");
        }
        int size = getBufferSize(bufferSize);
        this.bufferSize = bufferSize;
        this.audioRecord = new AudioRecord(6, sampleRate, channels == 1 ? 16 : 12, 2, size);
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
    }

    public void stop() {
        this.audioRecord.stop();
    }

    public void release() {
        this.running = false;
        try {
            this.thread.join();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        this.thread = null;
        this.audioRecord.release();
        this.audioRecord = null;
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
            public void run() {
                AudioRecordJNI.this.audioRecord.startRecording();
                if (VERSION.SDK_INT >= 16) {
                    if (AutomaticGainControl.isAvailable()) {
                        AudioRecordJNI.this.agc = AutomaticGainControl.create(AudioRecordJNI.this.audioRecord.getAudioSessionId());
                        AudioRecordJNI.this.agc.setEnabled(true);
                    } else {
                        FileLog.w("tg-voip", "AutomaticGainControl is not available on this device :(");
                    }
                    if (NoiseSuppressor.isAvailable()) {
                        AudioRecordJNI.this.ns = NoiseSuppressor.create(AudioRecordJNI.this.audioRecord.getAudioSessionId());
                        AudioRecordJNI.this.ns.setEnabled(true);
                    } else {
                        FileLog.w("tg-voip", "NoiseSuppressor is not available on this device :(");
                    }
                }
                while (AudioRecordJNI.this.running) {
                    try {
                        AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, AudioRecordJNI.this.bufferSize);
                        if (!AudioRecordJNI.this.running) {
                            AudioRecordJNI.this.audioRecord.stop();
                            break;
                        }
                        AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
                Log.i("tg-voip", "audiotrack thread exits");
            }
        });
        this.thread.start();
    }
}
