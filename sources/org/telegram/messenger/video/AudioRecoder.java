package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;

public class AudioRecoder {
    private final int TIMEOUT_USEC = 2500;
    private final MediaCodec decoder;
    private boolean decoderDone = false;
    private ByteBuffer[] decoderInputBuffers;
    private final BufferInfo decoderOutputBufferInfo = new BufferInfo();
    private ByteBuffer[] decoderOutputBuffers;
    private final MediaCodec encoder;
    private boolean encoderDone = false;
    private ByteBuffer[] encoderInputBuffers;
    private final BufferInfo encoderOutputBufferInfo = new BufferInfo();
    private ByteBuffer[] encoderOutputBuffers;
    public long endTime = 0;
    private final MediaExtractor extractor;
    private boolean extractorDone = false;
    private int pendingAudioDecoderOutputBufferIndex = -1;
    public long startTime = 0;
    private final int trackIndex;

    public AudioRecoder(MediaFormat mediaFormat, MediaExtractor mediaExtractor, int i) throws IOException {
        this.extractor = mediaExtractor;
        this.trackIndex = i;
        this.decoder = MediaCodec.createDecoderByType(mediaFormat.getString("mime"));
        this.decoder.configure(mediaFormat, null, null, 0);
        this.decoder.start();
        String str = "audio/mp4a-latm";
        this.encoder = MediaCodec.createEncoderByType(str);
        mediaFormat = MediaFormat.createAudioFormat(str, mediaFormat.getInteger("sample-rate"), mediaFormat.getInteger("channel-count"));
        mediaFormat.setInteger("bitrate", 65536);
        this.encoder.configure(mediaFormat, null, null, 1);
        this.encoder.start();
        this.decoderInputBuffers = this.decoder.getInputBuffers();
        this.decoderOutputBuffers = this.decoder.getOutputBuffers();
        this.encoderInputBuffers = this.encoder.getInputBuffers();
        this.encoderOutputBuffers = this.encoder.getOutputBuffers();
    }

    public void release() {
        try {
            this.encoder.stop();
            this.decoder.stop();
            this.extractor.unselectTrack(this.trackIndex);
            this.extractor.release();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean step(MP4Builder mP4Builder, int i) throws Exception {
        ByteBuffer inputBuffer;
        int readSampleData;
        long j;
        int dequeueOutputBuffer;
        if (!this.extractorDone) {
            int dequeueInputBuffer = this.decoder.dequeueInputBuffer(2500);
            if (dequeueInputBuffer != -1) {
                if (VERSION.SDK_INT >= 21) {
                    inputBuffer = this.decoder.getInputBuffer(dequeueInputBuffer);
                } else {
                    inputBuffer = this.decoderInputBuffers[dequeueInputBuffer];
                }
                readSampleData = this.extractor.readSampleData(inputBuffer, 0);
                long sampleTime = this.extractor.getSampleTime();
                j = this.endTime;
                if (j > 0 && sampleTime >= j) {
                    this.encoderDone = true;
                    BufferInfo bufferInfo = this.decoderOutputBufferInfo;
                    bufferInfo.flags |= 4;
                }
                if (readSampleData >= 0) {
                    this.decoder.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, this.extractor.getSampleTime(), this.extractor.getSampleFlags());
                }
                this.extractorDone = this.extractor.advance() ^ 1;
                if (this.extractorDone) {
                    this.decoder.queueInputBuffer(this.decoder.dequeueInputBuffer(2500), 0, 0, 0, 4);
                }
            }
        }
        if (!this.decoderDone && this.pendingAudioDecoderOutputBufferIndex == -1) {
            dequeueOutputBuffer = this.decoder.dequeueOutputBuffer(this.decoderOutputBufferInfo, 2500);
            if (dequeueOutputBuffer != -1) {
                if (dequeueOutputBuffer == -3) {
                    this.decoderOutputBuffers = this.decoder.getOutputBuffers();
                } else if (dequeueOutputBuffer != -2) {
                    if ((this.decoderOutputBufferInfo.flags & 2) != 0) {
                        this.decoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                    } else {
                        this.pendingAudioDecoderOutputBufferIndex = dequeueOutputBuffer;
                    }
                }
            }
        }
        if (this.pendingAudioDecoderOutputBufferIndex != -1) {
            readSampleData = this.encoder.dequeueInputBuffer(2500);
            if (readSampleData != -1) {
                inputBuffer = this.encoderInputBuffers[readSampleData];
                BufferInfo bufferInfo2 = this.decoderOutputBufferInfo;
                int i2 = bufferInfo2.size;
                j = bufferInfo2.presentationTimeUs;
                if (i2 >= 0) {
                    ByteBuffer duplicate = this.decoderOutputBuffers[this.pendingAudioDecoderOutputBufferIndex].duplicate();
                    duplicate.position(this.decoderOutputBufferInfo.offset);
                    duplicate.limit(this.decoderOutputBufferInfo.offset + i2);
                    inputBuffer.position(0);
                    inputBuffer.put(duplicate);
                    this.encoder.queueInputBuffer(readSampleData, 0, i2, j, this.decoderOutputBufferInfo.flags);
                }
                this.decoder.releaseOutputBuffer(this.pendingAudioDecoderOutputBufferIndex, false);
                this.pendingAudioDecoderOutputBufferIndex = -1;
                if ((this.decoderOutputBufferInfo.flags & 4) != 0) {
                    this.decoderDone = true;
                }
            }
        }
        if (!this.encoderDone) {
            dequeueOutputBuffer = this.encoder.dequeueOutputBuffer(this.encoderOutputBufferInfo, 2500);
            if (dequeueOutputBuffer != -1) {
                if (dequeueOutputBuffer == -3) {
                    this.encoderOutputBuffers = this.encoder.getOutputBuffers();
                } else if (dequeueOutputBuffer != -2) {
                    ByteBuffer byteBuffer = this.encoderOutputBuffers[dequeueOutputBuffer];
                    BufferInfo bufferInfo3 = this.encoderOutputBufferInfo;
                    if ((bufferInfo3.flags & 2) != 0) {
                        this.encoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                    } else {
                        if (bufferInfo3.size != 0) {
                            mP4Builder.writeSampleData(i, byteBuffer, bufferInfo3, false);
                        }
                        if ((this.encoderOutputBufferInfo.flags & 4) != 0) {
                            this.encoderDone = true;
                        }
                        this.encoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                    }
                }
            }
        }
        return this.encoderDone;
    }
}
