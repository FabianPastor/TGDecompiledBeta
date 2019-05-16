package org.telegram.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;

public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
    private int bufferSize;
    private long nativeInst;
    private boolean needResampling;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioTrack.getMinBufferSize(i2, 4, 2), i);
    }

    public void init(int i, int i2, int i3, int i4) {
        if (this.audioTrack == null) {
            int bufferSize = getBufferSize(i4, 48000);
            this.bufferSize = i4;
            this.audioTrack = new AudioTrack(0, 48000, i3 == 1 ? 4 : 12, 2, bufferSize, 1);
            if (this.audioTrack.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable unused) {
                }
                bufferSize = getBufferSize(i4 * 6, 44100);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("buffer size: ");
                stringBuilder.append(bufferSize);
                VLog.d(stringBuilder.toString());
                this.audioTrack = new AudioTrack(0, 44100, i3 == 1 ? 4 : 12, 2, bufferSize, 1);
                this.needResampling = true;
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    public void stop() {
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            try {
                audioTrack.stop();
            } catch (Exception unused) {
            }
        }
    }

    public void release() {
        this.running = false;
        Thread thread = this.thread;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                VLog.e(e);
            }
            this.thread = null;
        }
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            audioTrack.release();
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
        if (this.thread == null) {
            this.running = true;
            this.thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        AudioTrackJNI.this.audioTrack.play();
                        ByteBuffer byteBuffer = null;
                        ByteBuffer allocateDirect = AudioTrackJNI.this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
                        if (AudioTrackJNI.this.needResampling) {
                            byteBuffer = ByteBuffer.allocateDirect(1764);
                        }
                        while (AudioTrackJNI.this.running) {
                            try {
                                if (AudioTrackJNI.this.needResampling) {
                                    AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                                    allocateDirect.rewind();
                                    allocateDirect.put(AudioTrackJNI.this.buffer);
                                    Resampler.convert48to44(allocateDirect, byteBuffer);
                                    byteBuffer.rewind();
                                    byteBuffer.get(AudioTrackJNI.this.buffer, 0, 1764);
                                    AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1764);
                                } else {
                                    AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                                    AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1920);
                                }
                                if (!AudioTrackJNI.this.running) {
                                    AudioTrackJNI.this.audioTrack.stop();
                                    break;
                                }
                            } catch (Exception e) {
                                VLog.e(e);
                            }
                        }
                        VLog.i("audiotrack thread exits");
                    } catch (Exception e2) {
                        VLog.e("error starting AudioTrack", e2);
                    }
                }
            });
            this.thread.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }
}
