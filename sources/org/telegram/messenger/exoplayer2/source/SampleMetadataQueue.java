package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SampleMetadataQueue {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteFirstIndex;
    private int capacity = SAMPLE_CAPACITY_INCREMENT;
    private CryptoData[] cryptoDatas = new CryptoData[this.capacity];
    private int[] flags = new int[this.capacity];
    private Format[] formats = new Format[this.capacity];
    private long largestDiscardedTimestampUs = Long.MIN_VALUE;
    private long largestQueuedTimestampUs = Long.MIN_VALUE;
    private int length;
    private long[] offsets = new long[this.capacity];
    private int readPosition;
    private int relativeFirstIndex;
    private int[] sizes = new int[this.capacity];
    private int[] sourceIds = new int[this.capacity];
    private long[] timesUs = new long[this.capacity];
    private Format upstreamFormat;
    private boolean upstreamFormatRequired = true;
    private boolean upstreamKeyframeRequired = true;
    private int upstreamSourceId;

    public static final class SampleExtrasHolder {
        public CryptoData cryptoData;
        public long offset;
        public int size;
    }

    public void reset(boolean z) {
        this.length = 0;
        this.absoluteFirstIndex = 0;
        this.relativeFirstIndex = 0;
        this.readPosition = 0;
        this.upstreamKeyframeRequired = true;
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        if (z) {
            this.upstreamFormat = false;
            this.upstreamFormatRequired = true;
        }
    }

    public int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public long discardUpstreamSamples(int i) {
        int writeIndex = getWriteIndex() - i;
        boolean z = writeIndex >= 0 && writeIndex <= this.length - this.readPosition;
        Assertions.checkArgument(z);
        this.length -= writeIndex;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.length));
        if (this.length == 0) {
            return 0;
        }
        i = getRelativeIndex(this.length - 1);
        return this.offsets[i] + ((long) this.sizes[i]);
    }

    public void sourceId(int i) {
        this.upstreamSourceId = i;
    }

    public int getFirstIndex() {
        return this.absoluteFirstIndex;
    }

    public int getReadIndex() {
        return this.absoluteFirstIndex + this.readPosition;
    }

    public int peekSourceId() {
        return hasNextSample() ? this.sourceIds[getRelativeIndex(this.readPosition)] : this.upstreamSourceId;
    }

    public synchronized boolean hasNextSample() {
        return this.readPosition != this.length;
    }

    public synchronized Format getUpstreamFormat() {
        return this.upstreamFormatRequired ? null : this.upstreamFormat;
    }

    public synchronized long getLargestQueuedTimestampUs() {
        return this.largestQueuedTimestampUs;
    }

    public synchronized long getFirstTimestampUs() {
        return this.length == 0 ? Long.MIN_VALUE : this.timesUs[this.relativeFirstIndex];
    }

    public synchronized void rewind() {
        this.readPosition = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z, boolean z2, Format format, SampleExtrasHolder sampleExtrasHolder) {
        if (hasNextSample()) {
            z2 = getRelativeIndex(this.readPosition);
            if (!z) {
                if (this.formats[z2] == format) {
                    if (decoderInputBuffer.isFlagsOnly() != null) {
                        return -3;
                    }
                    decoderInputBuffer.timeUs = this.timesUs[z2];
                    decoderInputBuffer.setFlags(this.flags[z2]);
                    sampleExtrasHolder.size = this.sizes[z2];
                    sampleExtrasHolder.offset = this.offsets[z2];
                    sampleExtrasHolder.cryptoData = this.cryptoDatas[z2];
                    this.readPosition++;
                    return -4;
                }
            }
            formatHolder.format = this.formats[z2];
            return -5;
        } else if (z2) {
            decoderInputBuffer.setFlags(4);
            return -4;
        } else if (this.upstreamFormat != null && (z || this.upstreamFormat != format)) {
            formatHolder.format = this.upstreamFormat;
            return -5;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int advanceTo(long j, boolean z, boolean z2) {
        int relativeIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && j >= this.timesUs[relativeIndex]) {
            if (j <= this.largestQueuedTimestampUs || z2) {
                j = findSampleBefore(relativeIndex, this.length - this.readPosition, j, z);
                if (j == -1) {
                    return -1;
                }
                this.readPosition += j;
                return j;
            }
        }
    }

    public synchronized int advanceToEnd() {
        int i;
        i = this.length - this.readPosition;
        this.readPosition = this.length;
        return i;
    }

    public synchronized boolean setReadPosition(int i) {
        if (this.absoluteFirstIndex > i || i > this.absoluteFirstIndex + this.length) {
            return false;
        }
        this.readPosition = i - this.absoluteFirstIndex;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long discardTo(long j, boolean z, boolean z2) {
        if (this.length != 0) {
            if (j >= this.timesUs[this.relativeFirstIndex]) {
                z2 = (!z2 || this.readPosition == this.length) ? this.length : this.readPosition + 1;
                j = findSampleBefore(this.relativeFirstIndex, z2, j, z);
                if (j == -1) {
                    return -1;
                }
                return discardSamples(j);
            }
        }
    }

    public synchronized long discardToRead() {
        if (this.readPosition == 0) {
            return -1;
        }
        return discardSamples(this.readPosition);
    }

    public synchronized long discardToEnd() {
        if (this.length == 0) {
            return -1;
        }
        return discardSamples(this.length);
    }

    public synchronized boolean format(Format format) {
        if (format == null) {
            this.upstreamFormatRequired = true;
            return false;
        }
        this.upstreamFormatRequired = false;
        if (Util.areEqual(format, this.upstreamFormat)) {
            return false;
        }
        this.upstreamFormat = format;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void commitSample(long j, int i, long j2, int i2, CryptoData cryptoData) {
        if (this.upstreamKeyframeRequired) {
            if ((i & 1) != 0) {
                this.upstreamKeyframeRequired = false;
            } else {
                return;
            }
        }
        Assertions.checkState(this.upstreamFormatRequired ^ 1);
        commitSampleTimestamp(j);
        int relativeIndex = getRelativeIndex(this.length);
        this.timesUs[relativeIndex] = j;
        this.offsets[relativeIndex] = j2;
        this.sizes[relativeIndex] = i2;
        this.flags[relativeIndex] = i;
        this.cryptoDatas[relativeIndex] = cryptoData;
        this.formats[relativeIndex] = this.upstreamFormat;
        this.sourceIds[relativeIndex] = this.upstreamSourceId;
        this.length++;
        if (this.length == this.capacity) {
            j = this.capacity + SAMPLE_CAPACITY_INCREMENT;
            Object obj = new int[j];
            i = new long[j];
            j2 = new long[j];
            Object obj2 = new int[j];
            i2 = new int[j];
            cryptoData = new CryptoData[j];
            Object obj3 = new Format[j];
            int i3 = this.capacity - this.relativeFirstIndex;
            System.arraycopy(this.offsets, this.relativeFirstIndex, i, 0, i3);
            System.arraycopy(this.timesUs, this.relativeFirstIndex, j2, 0, i3);
            System.arraycopy(this.flags, this.relativeFirstIndex, obj2, 0, i3);
            System.arraycopy(this.sizes, this.relativeFirstIndex, i2, 0, i3);
            System.arraycopy(this.cryptoDatas, this.relativeFirstIndex, cryptoData, 0, i3);
            System.arraycopy(this.formats, this.relativeFirstIndex, obj3, 0, i3);
            System.arraycopy(this.sourceIds, this.relativeFirstIndex, obj, 0, i3);
            int i4 = this.relativeFirstIndex;
            System.arraycopy(this.offsets, 0, i, i3, i4);
            System.arraycopy(this.timesUs, 0, j2, i3, i4);
            System.arraycopy(this.flags, 0, obj2, i3, i4);
            System.arraycopy(this.sizes, 0, i2, i3, i4);
            System.arraycopy(this.cryptoDatas, 0, cryptoData, i3, i4);
            System.arraycopy(this.formats, 0, obj3, i3, i4);
            System.arraycopy(this.sourceIds, 0, obj, i3, i4);
            this.offsets = i;
            this.timesUs = j2;
            this.flags = obj2;
            this.sizes = i2;
            this.cryptoDatas = cryptoData;
            this.formats = obj3;
            this.sourceIds = obj;
            this.relativeFirstIndex = 0;
            this.length = this.capacity;
            this.capacity = j;
        }
    }

    public synchronized void commitSampleTimestamp(long j) {
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, j);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean attemptSplice(long j) {
        boolean z = false;
        if (this.length == 0) {
            if (j > this.largestDiscardedTimestampUs) {
                z = true;
            }
        } else if (Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.readPosition)) >= j) {
            return false;
        } else {
            int i = this.length;
            int relativeIndex = getRelativeIndex(this.length - 1);
            while (i > this.readPosition && this.timesUs[relativeIndex] >= j) {
                i--;
                relativeIndex--;
                if (relativeIndex == -1) {
                    relativeIndex = this.capacity - 1;
                }
            }
            discardUpstreamSamples(this.absoluteFirstIndex + i);
            return true;
        }
    }

    private int findSampleBefore(int i, int i2, long j, boolean z) {
        int i3 = -1;
        int i4 = i;
        for (i = 0; i < i2 && this.timesUs[i4] <= j; i++) {
            if (!(z && (this.flags[i4] & 1) == 0)) {
                i3 = i;
            }
            i4++;
            if (i4 == this.capacity) {
                i4 = 0;
            }
        }
        return i3;
    }

    private long discardSamples(int i) {
        this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(i));
        this.length -= i;
        this.absoluteFirstIndex += i;
        this.relativeFirstIndex += i;
        if (this.relativeFirstIndex >= this.capacity) {
            this.relativeFirstIndex -= this.capacity;
        }
        this.readPosition -= i;
        if (this.readPosition < 0) {
            this.readPosition = 0;
        }
        if (this.length != 0) {
            return this.offsets[this.relativeFirstIndex];
        }
        i = (this.relativeFirstIndex == 0 ? this.capacity : this.relativeFirstIndex) - 1;
        return this.offsets[i] + ((long) this.sizes[i]);
    }

    private long getLargestTimestamp(int i) {
        long j = Long.MIN_VALUE;
        if (i == 0) {
            return Long.MIN_VALUE;
        }
        int relativeIndex = getRelativeIndex(i - 1);
        for (int i2 = 0; i2 < i; i2++) {
            j = Math.max(j, this.timesUs[relativeIndex]);
            if ((this.flags[relativeIndex] & 1) != 0) {
                break;
            }
            relativeIndex--;
            if (relativeIndex == -1) {
                relativeIndex = this.capacity - 1;
            }
        }
        return j;
    }

    private int getRelativeIndex(int i) {
        int i2 = this.relativeFirstIndex + i;
        return i2 < this.capacity ? i2 : i2 - this.capacity;
    }
}
