package org.telegram.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;

public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
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
            AudioTrack audioTrack2 = new AudioTrack(0, 48000, i3 == 1 ? 4 : 12, 2, getBufferSize(i4, 48000), 1);
            this.audioTrack = audioTrack2;
            if (audioTrack2.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable unused) {
                }
                int bufferSize = getBufferSize(i4 * 6, 44100);
                VLog.d("buffer size: " + bufferSize);
                this.audioTrack = new AudioTrack(0, 44100, i3 == 1 ? 4 : 12, 2, bufferSize, 1);
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
            Thread thread2 = new Thread(new Runnable() {
                public final void run() {
                    AudioTrackJNI.this.lambda$startThread$0$AudioTrackJNI();
                }
            });
            this.thread = thread2;
            thread2.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startThread$0 */
    public /* synthetic */ void lambda$startThread$0$AudioTrackJNI() {
        try {
            this.audioTrack.play();
            ByteBuffer byteBuffer = null;
            ByteBuffer allocateDirect = this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
            if (this.needResampling) {
                byteBuffer = ByteBuffer.allocateDirect(1764);
            }
            while (true) {
                if (!this.running) {
                    break;
                }
                try {
                    if (this.needResampling) {
                        nativeCallback(this.buffer);
                        allocateDirect.rewind();
                        allocateDirect.put(this.buffer);
                        Resampler.convert48to44(allocateDirect, byteBuffer);
                        byteBuffer.rewind();
                        byteBuffer.get(this.buffer, 0, 1764);
                        this.audioTrack.write(this.buffer, 0, 1764);
                    } else {
                        nativeCallback(this.buffer);
                        this.audioTrack.write(this.buffer, 0, 1920);
                    }
                    if (!this.running) {
                        this.audioTrack.stop();
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
}
