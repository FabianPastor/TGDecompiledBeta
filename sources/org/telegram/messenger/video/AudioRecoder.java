package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;

public class AudioRecoder {
    private final int TIMEOUT_USEC = 2500;
    private final MediaCodec decoder;
    private boolean decoderDone = false;
    private ByteBuffer[] decoderInputBuffers;
    private final MediaCodec.BufferInfo decoderOutputBufferInfo = new MediaCodec.BufferInfo();
    private ByteBuffer[] decoderOutputBuffers;
    private final MediaCodec encoder;
    private boolean encoderDone = false;
    private ByteBuffer[] encoderInputBuffers;
    private final MediaCodec.BufferInfo encoderOutputBufferInfo = new MediaCodec.BufferInfo();
    private ByteBuffer[] encoderOutputBuffers;
    public long endTime = 0;
    private final MediaExtractor extractor;
    private boolean extractorDone = false;
    public final MediaFormat format;
    private int pendingAudioDecoderOutputBufferIndex = -1;
    public long startTime = 0;
    private final int trackIndex;

    public AudioRecoder(MediaFormat inputAudioFormat, MediaExtractor extractor2, int trackIndex2) throws IOException {
        this.extractor = extractor2;
        this.trackIndex = trackIndex2;
        MediaCodec createDecoderByType = MediaCodec.createDecoderByType(inputAudioFormat.getString("mime"));
        this.decoder = createDecoderByType;
        createDecoderByType.configure(inputAudioFormat, (Surface) null, (MediaCrypto) null, 0);
        createDecoderByType.start();
        MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
        this.encoder = createEncoderByType;
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", inputAudioFormat.getInteger("sample-rate"), inputAudioFormat.getInteger("channel-count"));
        this.format = createAudioFormat;
        createAudioFormat.setInteger("bitrate", 65536);
        createEncoderByType.configure(createAudioFormat, (Surface) null, (MediaCrypto) null, 1);
        createEncoderByType.start();
        this.decoderInputBuffers = createDecoderByType.getInputBuffers();
        this.decoderOutputBuffers = createDecoderByType.getOutputBuffers();
        this.encoderInputBuffers = createEncoderByType.getInputBuffers();
        this.encoderOutputBuffers = createEncoderByType.getOutputBuffers();
    }

    public void release() {
        try {
            this.encoder.stop();
            this.decoder.stop();
            this.extractor.unselectTrack(this.trackIndex);
            this.extractor.release();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean step(MP4Builder muxer, int audioTrackIndex) throws Exception {
        int encoderInputBufferIndex;
        int decoderOutputBufferIndex;
        int decoderInputBufferIndex;
        ByteBuffer decoderInputBuffer;
        if (!this.extractorDone && (decoderInputBufferIndex = this.decoder.dequeueInputBuffer(2500)) != -1) {
            if (Build.VERSION.SDK_INT >= 21) {
                decoderInputBuffer = this.decoder.getInputBuffer(decoderInputBufferIndex);
            } else {
                decoderInputBuffer = this.decoderInputBuffers[decoderInputBufferIndex];
            }
            int size = this.extractor.readSampleData(decoderInputBuffer, 0);
            long presentationTime = this.extractor.getSampleTime();
            long j = this.endTime;
            if (j > 0 && presentationTime >= j) {
                this.encoderDone = true;
                this.decoderOutputBufferInfo.flags |= 4;
            }
            if (size >= 0) {
                this.decoder.queueInputBuffer(decoderInputBufferIndex, 0, size, this.extractor.getSampleTime(), this.extractor.getSampleFlags());
            }
            boolean z = !this.extractor.advance();
            this.extractorDone = z;
            if (z) {
                this.decoder.queueInputBuffer(this.decoder.dequeueInputBuffer(2500), 0, 0, 0, 4);
            }
        }
        if (this.decoderDone == 0 && this.pendingAudioDecoderOutputBufferIndex == -1 && (decoderOutputBufferIndex = this.decoder.dequeueOutputBuffer(this.decoderOutputBufferInfo, 2500)) != -1) {
            if (decoderOutputBufferIndex == -3) {
                this.decoderOutputBuffers = this.decoder.getOutputBuffers();
            } else if (decoderOutputBufferIndex != -2) {
                if ((this.decoderOutputBufferInfo.flags & 2) != 0) {
                    this.decoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                } else {
                    this.pendingAudioDecoderOutputBufferIndex = decoderOutputBufferIndex;
                }
            }
        }
        if (!(this.pendingAudioDecoderOutputBufferIndex == -1 || (encoderInputBufferIndex = this.encoder.dequeueInputBuffer(2500)) == -1)) {
            ByteBuffer encoderInputBuffer = this.encoderInputBuffers[encoderInputBufferIndex];
            int size2 = this.decoderOutputBufferInfo.size;
            long presentationTime2 = this.decoderOutputBufferInfo.presentationTimeUs;
            if (size2 >= 0) {
                ByteBuffer decoderOutputBuffer = this.decoderOutputBuffers[this.pendingAudioDecoderOutputBufferIndex].duplicate();
                decoderOutputBuffer.position(this.decoderOutputBufferInfo.offset);
                decoderOutputBuffer.limit(this.decoderOutputBufferInfo.offset + size2);
                encoderInputBuffer.position(0);
                encoderInputBuffer.put(decoderOutputBuffer);
                int i = size2;
                ByteBuffer byteBuffer = decoderOutputBuffer;
                ByteBuffer byteBuffer2 = encoderInputBuffer;
                this.encoder.queueInputBuffer(encoderInputBufferIndex, 0, size2, presentationTime2, this.decoderOutputBufferInfo.flags);
            } else {
                int i2 = size2;
                ByteBuffer byteBuffer3 = encoderInputBuffer;
            }
            this.decoder.releaseOutputBuffer(this.pendingAudioDecoderOutputBufferIndex, false);
            this.pendingAudioDecoderOutputBufferIndex = -1;
            if ((this.decoderOutputBufferInfo.flags & 4) != 0) {
                this.decoderDone = true;
            }
        }
        if (this.encoderDone == 0) {
            int encoderOutputBufferIndex = this.encoder.dequeueOutputBuffer(this.encoderOutputBufferInfo, 2500);
            if (encoderOutputBufferIndex == -1) {
                MP4Builder mP4Builder = muxer;
                int i3 = audioTrackIndex;
            } else if (encoderOutputBufferIndex == -3) {
                this.encoderOutputBuffers = this.encoder.getOutputBuffers();
                MP4Builder mP4Builder2 = muxer;
                int i4 = audioTrackIndex;
            } else if (encoderOutputBufferIndex == -2) {
                MP4Builder mP4Builder3 = muxer;
                int i5 = audioTrackIndex;
            } else {
                ByteBuffer encoderOutputBuffer = this.encoderOutputBuffers[encoderOutputBufferIndex];
                if ((this.encoderOutputBufferInfo.flags & 2) != 0) {
                    this.encoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                    MP4Builder mP4Builder4 = muxer;
                    int i6 = audioTrackIndex;
                } else {
                    if (this.encoderOutputBufferInfo.size != 0) {
                        muxer.writeSampleData(audioTrackIndex, encoderOutputBuffer, this.encoderOutputBufferInfo, false);
                    } else {
                        MP4Builder mP4Builder5 = muxer;
                        int i7 = audioTrackIndex;
                    }
                    if ((this.encoderOutputBufferInfo.flags & 4) != 0) {
                        this.encoderDone = true;
                    }
                    this.encoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                }
            }
        } else {
            MP4Builder mP4Builder6 = muxer;
            int i8 = audioTrackIndex;
        }
        return this.encoderDone;
    }
}
