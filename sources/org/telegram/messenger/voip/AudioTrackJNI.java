package org.telegram.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;

public class AudioTrackJNI {
    /* access modifiers changed from: private */
    public AudioTrack audioTrack;
    /* access modifiers changed from: private */
    public byte[] buffer = new byte[1920];
    private int bufferSize;
    private long nativeInst;
    /* access modifiers changed from: private */
    public boolean needResampling;
    /* access modifiers changed from: private */
    public boolean running;
    private Thread thread;

    /* access modifiers changed from: private */
    public native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioTrack.getMinBufferSize(i2, 4, 2), i);
    }

    public void init(int i, int i2, int i3, int i4) {
        if (this.audioTrack == null) {
            int bufferSize2 = getBufferSize(i4, 48000);
            this.bufferSize = i4;
            this.audioTrack = new AudioTrack(0, 48000, i3 == 1 ? 4 : 12, 2, bufferSize2, 1);
            if (this.audioTrack.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable unused) {
                }
                int bufferSize3 = getBufferSize(i4 * 6, 44100);
                VLog.d("buffer size: " + bufferSize3);
                this.audioTrack = new AudioTrack(0, 44100, i3 == 1 ? 4 : 12, 2, bufferSize3, 1);
                this.needResampling = true;
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    public void stop() {
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            try {
                audioTrack2.stop();
            } catch (Exception unused) {
            }
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
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            audioTrack2.release();
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
                        while (true) {
                            if (!AudioTrackJNI.this.running) {
                                break;
                            }
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
                                VLog.e((Throwable) e);
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
