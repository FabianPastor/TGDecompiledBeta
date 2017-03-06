package org.telegram.messenger.voip;

import android.media.AudioTrack;
import android.util.Log;
import org.telegram.messenger.FileLog;

public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
    private int bufferSize;
    private long nativeInst;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long nativeInst) {
        this.nativeInst = nativeInst;
    }

    private int getBufferSize(int min) {
        return Math.max(AudioTrack.getMinBufferSize(48000, 4, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        if (this.audioTrack != null) {
            throw new IllegalStateException("already inited");
        }
        int size = getBufferSize(bufferSize);
        this.bufferSize = bufferSize;
        this.audioTrack = new AudioTrack(0, sampleRate, channels == 1 ? 4 : 12, 2, size, 1);
    }

    public void stop() {
        this.audioTrack.stop();
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
        this.thread = new Thread(new Runnable() {
            public void run() {
                AudioTrackJNI.this.audioTrack.play();
                while (AudioTrackJNI.this.running) {
                    try {
                        AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                        AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1920);
                        if (!AudioTrackJNI.this.running) {
                            AudioTrackJNI.this.audioTrack.stop();
                            break;
                        }
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
