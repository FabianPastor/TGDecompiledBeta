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
    class C06711 implements Runnable {
        C06711() {
        }

        public void run() {
            try {
                AudioTrackJNI.this.audioTrack.play();
                ByteBuffer tmp44 = null;
                ByteBuffer tmp48 = AudioTrackJNI.this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
                if (AudioTrackJNI.this.needResampling) {
                    tmp44 = ByteBuffer.allocateDirect(1764);
                }
                while (AudioTrackJNI.this.running) {
                    try {
                        if (AudioTrackJNI.this.needResampling) {
                            AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                            tmp48.rewind();
                            tmp48.put(AudioTrackJNI.this.buffer);
                            Resampler.convert48to44(tmp48, tmp44);
                            tmp44.rewind();
                            tmp44.get(AudioTrackJNI.this.buffer, 0, 1764);
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
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e("error starting AudioTrack", x);
                }
            }
        }
    }

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long nativeInst) {
        this.nativeInst = nativeInst;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioTrack.getMinBufferSize(sampleRate, 4, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        int i = channels;
        int i2 = bufferSize;
        if (this.audioTrack != null) {
            throw new IllegalStateException("already inited");
        }
        int size = getBufferSize(i2, 48000);
        r1.bufferSize = i2;
        r1.audioTrack = new AudioTrack(0, 48000, i == 1 ? 4 : 12, 2, size, 1);
        if (r1.audioTrack.getState() != 1) {
            try {
                r1.audioTrack.release();
            } catch (Throwable th) {
            }
            size = getBufferSize(i2 * 6, 44100);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("buffer size: ");
                stringBuilder.append(size);
                FileLog.m0d(stringBuilder.toString());
            }
            r1.audioTrack = new AudioTrack(0, 44100, i == 1 ? 4 : 12, 2, size, 1);
            r1.needResampling = true;
        }
    }

    public void stop() {
        if (this.audioTrack != null) {
            try {
                this.audioTrack.stop();
            } catch (Exception e) {
            }
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
        this.thread = new Thread(new C06711());
        this.thread.start();
    }
}
