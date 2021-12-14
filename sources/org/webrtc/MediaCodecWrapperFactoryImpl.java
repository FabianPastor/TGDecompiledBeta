package org.webrtc;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;

class MediaCodecWrapperFactoryImpl implements MediaCodecWrapperFactory {
    MediaCodecWrapperFactoryImpl() {
    }

    private static class MediaCodecWrapperImpl implements MediaCodecWrapper {
        private final MediaCodec mediaCodec;

        public MediaCodecWrapperImpl(MediaCodec mediaCodec2) {
            this.mediaCodec = mediaCodec2;
        }

        public void configure(MediaFormat mediaFormat, Surface surface, MediaCrypto mediaCrypto, int i) {
            this.mediaCodec.configure(mediaFormat, surface, mediaCrypto, i);
        }

        public void start() {
            this.mediaCodec.start();
        }

        public void flush() {
            this.mediaCodec.flush();
        }

        public void stop() {
            this.mediaCodec.stop();
        }

        public void release() {
            this.mediaCodec.release();
        }

        public int dequeueInputBuffer(long j) {
            return this.mediaCodec.dequeueInputBuffer(j);
        }

        public void queueInputBuffer(int i, int i2, int i3, long j, int i4) {
            this.mediaCodec.queueInputBuffer(i, i2, i3, j, i4);
        }

        public int dequeueOutputBuffer(MediaCodec.BufferInfo bufferInfo, long j) {
            return this.mediaCodec.dequeueOutputBuffer(bufferInfo, j);
        }

        public void releaseOutputBuffer(int i, boolean z) {
            this.mediaCodec.releaseOutputBuffer(i, z);
        }

        public MediaFormat getOutputFormat() {
            return this.mediaCodec.getOutputFormat();
        }

        public ByteBuffer[] getInputBuffers() {
            return this.mediaCodec.getInputBuffers();
        }

        public ByteBuffer[] getOutputBuffers() {
            return this.mediaCodec.getOutputBuffers();
        }

        @TargetApi(18)
        public Surface createInputSurface() {
            return this.mediaCodec.createInputSurface();
        }

        @TargetApi(19)
        public void setParameters(Bundle bundle) {
            this.mediaCodec.setParameters(bundle);
        }
    }

    public MediaCodecWrapper createByCodecName(String str) throws IOException {
        return new MediaCodecWrapperImpl(MediaCodec.createByCodecName(str));
    }
}
