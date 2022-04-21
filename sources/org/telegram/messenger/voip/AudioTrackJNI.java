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

    public AudioTrackJNI(long ptr) {
        this.nativeInst = ptr;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioTrack.getMinBufferSize(sampleRate, 4, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize) {
        int i = channels;
        int i2 = bufferSize;
        if (this.audioTrack == null) {
            AudioTrack audioTrack2 = new AudioTrack(0, 48000, i == 1 ? 4 : 12, 2, getBufferSize(i2, 48000), 1);
            this.audioTrack = audioTrack2;
            if (audioTrack2.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable th) {
                }
                int size = getBufferSize(i2 * 6, 44100);
                VLog.d("buffer size: " + size);
                this.audioTrack = new AudioTrack(0, 44100, i == 1 ? 4 : 12, 2, size, 1);
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
            } catch (Exception e) {
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
            Thread thread2 = new Thread(new AudioTrackJNI$$ExternalSyntheticLambda0(this));
            this.thread = thread2;
            thread2.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }

    /* renamed from: lambda$startThread$0$org-telegram-messenger-voip-AudioTrackJNI  reason: not valid java name */
    public /* synthetic */ void m1140lambda$startThread$0$orgtelegrammessengervoipAudioTrackJNI() {
        try {
            this.audioTrack.play();
            ByteBuffer tmp44 = null;
            ByteBuffer tmp48 = this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
            if (this.needResampling) {
                tmp44 = ByteBuffer.allocateDirect(1764);
            }
            while (true) {
                if (!this.running) {
                    break;
                }
                try {
                    if (this.needResampling) {
                        nativeCallback(this.buffer);
                        tmp48.rewind();
                        tmp48.put(this.buffer);
                        Resampler.convert48to44(tmp48, tmp44);
                        tmp44.rewind();
                        tmp44.get(this.buffer, 0, 1764);
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
        } catch (Exception x) {
            VLog.e("error starting AudioTrack", x);
        }
    }
}
