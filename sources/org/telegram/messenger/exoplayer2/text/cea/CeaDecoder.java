package org.telegram.messenger.exoplayer2.text.cea;

import java.util.LinkedList;
import java.util.PriorityQueue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.telegram.messenger.exoplayer2.text.SubtitleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.Assertions;

abstract class CeaDecoder implements SubtitleDecoder {
    private static final int NUM_INPUT_BUFFERS = 10;
    private static final int NUM_OUTPUT_BUFFERS = 2;
    private final LinkedList<CeaInputBuffer> availableInputBuffers = new LinkedList();
    private final LinkedList<SubtitleOutputBuffer> availableOutputBuffers;
    private CeaInputBuffer dequeuedInputBuffer;
    private long playbackPositionUs;
    private long queuedInputBufferCount;
    private final PriorityQueue<CeaInputBuffer> queuedInputBuffers;

    private static final class CeaInputBuffer extends SubtitleInputBuffer implements Comparable<CeaInputBuffer> {
        private long queuedInputBufferCount;

        private CeaInputBuffer() {
        }

        public int compareTo(CeaInputBuffer other) {
            if (isEndOfStream() == other.isEndOfStream()) {
                long delta = this.timeUs - other.timeUs;
                if (delta == 0) {
                    delta = this.queuedInputBufferCount - other.queuedInputBufferCount;
                    if (delta == 0) {
                        return 0;
                    }
                }
                if (delta <= 0) {
                    return -1;
                }
                return 1;
            } else if (isEndOfStream()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private final class CeaOutputBuffer extends SubtitleOutputBuffer {
        private CeaOutputBuffer() {
        }

        public final void release() {
            CeaDecoder.this.releaseOutputBuffer(this);
        }
    }

    protected abstract Subtitle createSubtitle();

    protected abstract void decode(SubtitleInputBuffer subtitleInputBuffer);

    public abstract String getName();

    protected abstract boolean isNewSubtitleDataAvailable();

    public CeaDecoder() {
        int i;
        for (i = 0; i < 10; i++) {
            this.availableInputBuffers.add(new CeaInputBuffer());
        }
        this.availableOutputBuffers = new LinkedList();
        for (i = 0; i < 2; i++) {
            this.availableOutputBuffers.add(new CeaOutputBuffer());
        }
        this.queuedInputBuffers = new PriorityQueue();
    }

    public void setPositionUs(long positionUs) {
        this.playbackPositionUs = positionUs;
    }

    public SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        Assertions.checkState(this.dequeuedInputBuffer == null);
        if (this.availableInputBuffers.isEmpty()) {
            return null;
        }
        this.dequeuedInputBuffer = (CeaInputBuffer) this.availableInputBuffers.pollFirst();
        return this.dequeuedInputBuffer;
    }

    public void queueInputBuffer(SubtitleInputBuffer inputBuffer) throws SubtitleDecoderException {
        Assertions.checkArgument(inputBuffer == this.dequeuedInputBuffer);
        if (inputBuffer.isDecodeOnly()) {
            releaseInputBuffer(this.dequeuedInputBuffer);
        } else {
            CeaInputBuffer ceaInputBuffer = this.dequeuedInputBuffer;
            long j = this.queuedInputBufferCount;
            this.queuedInputBufferCount = 1 + j;
            ceaInputBuffer.queuedInputBufferCount = j;
            this.queuedInputBuffers.add(this.dequeuedInputBuffer);
        }
        this.dequeuedInputBuffer = null;
    }

    public SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        if (this.availableOutputBuffers.isEmpty()) {
            return null;
        }
        while (!this.queuedInputBuffers.isEmpty() && ((CeaInputBuffer) this.queuedInputBuffers.peek()).timeUs <= this.playbackPositionUs) {
            CeaInputBuffer inputBuffer = (CeaInputBuffer) this.queuedInputBuffers.poll();
            if (inputBuffer.isEndOfStream()) {
                SubtitleOutputBuffer outputBuffer = (SubtitleOutputBuffer) this.availableOutputBuffers.pollFirst();
                outputBuffer.addFlag(4);
                releaseInputBuffer(inputBuffer);
                return outputBuffer;
            }
            decode(inputBuffer);
            if (isNewSubtitleDataAvailable()) {
                Subtitle subtitle = createSubtitle();
                if (!inputBuffer.isDecodeOnly()) {
                    outputBuffer = (SubtitleOutputBuffer) this.availableOutputBuffers.pollFirst();
                    outputBuffer.setContent(inputBuffer.timeUs, subtitle, Long.MAX_VALUE);
                    releaseInputBuffer(inputBuffer);
                    return outputBuffer;
                }
            }
            releaseInputBuffer(inputBuffer);
        }
        return null;
    }

    private void releaseInputBuffer(CeaInputBuffer inputBuffer) {
        inputBuffer.clear();
        this.availableInputBuffers.add(inputBuffer);
    }

    protected void releaseOutputBuffer(SubtitleOutputBuffer outputBuffer) {
        outputBuffer.clear();
        this.availableOutputBuffers.add(outputBuffer);
    }

    public void flush() {
        this.queuedInputBufferCount = 0;
        this.playbackPositionUs = 0;
        while (!this.queuedInputBuffers.isEmpty()) {
            releaseInputBuffer((CeaInputBuffer) this.queuedInputBuffers.poll());
        }
        if (this.dequeuedInputBuffer != null) {
            releaseInputBuffer(this.dequeuedInputBuffer);
            this.dequeuedInputBuffer = null;
        }
    }

    public void release() {
    }
}
