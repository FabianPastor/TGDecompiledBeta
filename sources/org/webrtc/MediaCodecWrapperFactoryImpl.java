package org.webrtc;

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

        public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
            this.mediaCodec.configure(format, surface, crypto, flags);
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

        public int dequeueInputBuffer(long timeoutUs) {
            return this.mediaCodec.dequeueInputBuffer(timeoutUs);
        }

        public void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
            this.mediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
        }

        public int dequeueOutputBuffer(MediaCodec.BufferInfo info, long timeoutUs) {
            return this.mediaCodec.dequeueOutputBuffer(info, timeoutUs);
        }

        public void releaseOutputBuffer(int index, boolean render) {
            this.mediaCodec.releaseOutputBuffer(index, render);
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

        public Surface createInputSurface() {
            return this.mediaCodec.createInputSurface();
        }

        public void setParameters(Bundle params) {
            this.mediaCodec.setParameters(params);
        }
    }

    public MediaCodecWrapper createByCodecName(String name) throws IOException {
        return new MediaCodecWrapperImpl(MediaCodec.createByCodecName(name));
    }
}
