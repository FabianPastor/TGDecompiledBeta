package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.upstream.Allocation;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultTrackOutput implements TrackOutput {
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private static final int STATE_DISABLED = 2;
    private static final int STATE_ENABLED = 0;
    private static final int STATE_ENABLED_WRITING = 1;
    private final int allocationLength;
    private final Allocator allocator;
    private final LinkedBlockingDeque<Allocation> dataQueue = new LinkedBlockingDeque();
    private Format downstreamFormat;
    private final BufferExtrasHolder extrasHolder = new BufferExtrasHolder();
    private final InfoQueue infoQueue = new InfoQueue();
    private Allocation lastAllocation;
    private int lastAllocationOffset = this.allocationLength;
    private Format lastUnadjustedFormat;
    private boolean needKeyframe = true;
    private boolean pendingFormatAdjustment;
    private boolean pendingSplice;
    private long sampleOffsetUs;
    private final ParsableByteArray scratch = new ParsableByteArray(32);
    private final AtomicInteger state = new AtomicInteger();
    private long totalBytesDropped;
    private long totalBytesWritten;
    private UpstreamFormatChangedListener upstreamFormatChangeListener;

    private static final class BufferExtrasHolder {
        public byte[] encryptionKeyId;
        public long nextOffset;
        public long offset;
        public int size;

        private BufferExtrasHolder() {
        }
    }

    private static final class InfoQueue {
        private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
        private int absoluteReadIndex;
        private int capacity = 1000;
        private byte[][] encryptionKeys = new byte[this.capacity][];
        private int[] flags = new int[this.capacity];
        private Format[] formats = new Format[this.capacity];
        private long largestDequeuedTimestampUs = Long.MIN_VALUE;
        private long largestQueuedTimestampUs = Long.MIN_VALUE;
        private long[] offsets = new long[this.capacity];
        private int queueSize;
        private int relativeReadIndex;
        private int relativeWriteIndex;
        private int[] sizes = new int[this.capacity];
        private int[] sourceIds = new int[this.capacity];
        private long[] timesUs = new long[this.capacity];
        private Format upstreamFormat;
        private boolean upstreamFormatRequired = true;
        private int upstreamSourceId;

        public void clearSampleData() {
            this.absoluteReadIndex = 0;
            this.relativeReadIndex = 0;
            this.relativeWriteIndex = 0;
            this.queueSize = 0;
        }

        public void resetLargestParsedTimestamps() {
            this.largestDequeuedTimestampUs = Long.MIN_VALUE;
            this.largestQueuedTimestampUs = Long.MIN_VALUE;
        }

        public int getWriteIndex() {
            return this.absoluteReadIndex + this.queueSize;
        }

        public long discardUpstreamSamples(int discardFromIndex) {
            int discardCount = getWriteIndex() - discardFromIndex;
            boolean z = discardCount >= 0 && discardCount <= this.queueSize;
            Assertions.checkArgument(z);
            if (discardCount != 0) {
                this.queueSize -= discardCount;
                this.relativeWriteIndex = ((this.relativeWriteIndex + this.capacity) - discardCount) % this.capacity;
                this.largestQueuedTimestampUs = Long.MIN_VALUE;
                for (int i = this.queueSize - 1; i >= 0; i--) {
                    int sampleIndex = (this.relativeReadIndex + i) % this.capacity;
                    this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, this.timesUs[sampleIndex]);
                    if ((this.flags[sampleIndex] & 1) != 0) {
                        break;
                    }
                }
                return this.offsets[this.relativeWriteIndex];
            } else if (this.absoluteReadIndex == 0) {
                return 0;
            } else {
                int lastWriteIndex = (this.relativeWriteIndex == 0 ? this.capacity : this.relativeWriteIndex) - 1;
                return this.offsets[lastWriteIndex] + ((long) this.sizes[lastWriteIndex]);
            }
        }

        public void sourceId(int sourceId) {
            this.upstreamSourceId = sourceId;
        }

        public int getReadIndex() {
            return this.absoluteReadIndex;
        }

        public int peekSourceId() {
            return this.queueSize == 0 ? this.upstreamSourceId : this.sourceIds[this.relativeReadIndex];
        }

        public synchronized boolean isEmpty() {
            return this.queueSize == 0;
        }

        public synchronized Format getUpstreamFormat() {
            return this.upstreamFormatRequired ? null : this.upstreamFormat;
        }

        public synchronized long getLargestQueuedTimestampUs() {
            return Math.max(this.largestDequeuedTimestampUs, this.largestQueuedTimestampUs);
        }

        public synchronized int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, Format downstreamFormat, BufferExtrasHolder extrasHolder) {
            int i = -4;
            synchronized (this) {
                if (this.queueSize == 0) {
                    if (loadingFinished) {
                        buffer.setFlags(4);
                    } else if (this.upstreamFormat == null || (!formatRequired && this.upstreamFormat == downstreamFormat)) {
                        i = -3;
                    } else {
                        formatHolder.format = this.upstreamFormat;
                        i = -5;
                    }
                } else if (formatRequired || this.formats[this.relativeReadIndex] != downstreamFormat) {
                    formatHolder.format = this.formats[this.relativeReadIndex];
                    i = -5;
                } else {
                    long j;
                    buffer.timeUs = this.timesUs[this.relativeReadIndex];
                    buffer.setFlags(this.flags[this.relativeReadIndex]);
                    extrasHolder.size = this.sizes[this.relativeReadIndex];
                    extrasHolder.offset = this.offsets[this.relativeReadIndex];
                    extrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
                    this.largestDequeuedTimestampUs = Math.max(this.largestDequeuedTimestampUs, buffer.timeUs);
                    this.queueSize--;
                    this.relativeReadIndex++;
                    this.absoluteReadIndex++;
                    if (this.relativeReadIndex == this.capacity) {
                        this.relativeReadIndex = 0;
                    }
                    if (this.queueSize > 0) {
                        j = this.offsets[this.relativeReadIndex];
                    } else {
                        j = extrasHolder.offset + ((long) extrasHolder.size);
                    }
                    extrasHolder.nextOffset = j;
                }
            }
            return i;
        }

        public synchronized long skipToKeyframeBefore(long timeUs, boolean allowTimeBeyondBuffer) {
            long j = -1;
            synchronized (this) {
                if (this.queueSize != 0 && timeUs >= this.timesUs[this.relativeReadIndex]) {
                    if (timeUs <= this.largestQueuedTimestampUs || allowTimeBeyondBuffer) {
                        int sampleCount = 0;
                        int sampleCountToKeyframe = -1;
                        int searchIndex = this.relativeReadIndex;
                        while (searchIndex != this.relativeWriteIndex && this.timesUs[searchIndex] <= timeUs) {
                            if ((this.flags[searchIndex] & 1) != 0) {
                                sampleCountToKeyframe = sampleCount;
                            }
                            searchIndex = (searchIndex + 1) % this.capacity;
                            sampleCount++;
                        }
                        if (sampleCountToKeyframe != -1) {
                            this.queueSize -= sampleCountToKeyframe;
                            this.relativeReadIndex = (this.relativeReadIndex + sampleCountToKeyframe) % this.capacity;
                            this.absoluteReadIndex += sampleCountToKeyframe;
                            j = this.offsets[this.relativeReadIndex];
                        }
                    }
                }
            }
            return j;
        }

        public synchronized boolean format(Format format) {
            boolean z = false;
            synchronized (this) {
                if (format == null) {
                    this.upstreamFormatRequired = true;
                } else {
                    this.upstreamFormatRequired = false;
                    if (!Util.areEqual(format, this.upstreamFormat)) {
                        this.upstreamFormat = format;
                        z = true;
                    }
                }
            }
            return z;
        }

        public synchronized void commitSample(long timeUs, int sampleFlags, long offset, int size, byte[] encryptionKey) {
            Assertions.checkState(!this.upstreamFormatRequired);
            commitSampleTimestamp(timeUs);
            this.timesUs[this.relativeWriteIndex] = timeUs;
            this.offsets[this.relativeWriteIndex] = offset;
            this.sizes[this.relativeWriteIndex] = size;
            this.flags[this.relativeWriteIndex] = sampleFlags;
            this.encryptionKeys[this.relativeWriteIndex] = encryptionKey;
            this.formats[this.relativeWriteIndex] = this.upstreamFormat;
            this.sourceIds[this.relativeWriteIndex] = this.upstreamSourceId;
            this.queueSize++;
            if (this.queueSize == this.capacity) {
                int newCapacity = this.capacity + 1000;
                int[] newSourceIds = new int[newCapacity];
                long[] newOffsets = new long[newCapacity];
                long[] newTimesUs = new long[newCapacity];
                int[] newFlags = new int[newCapacity];
                int[] newSizes = new int[newCapacity];
                byte[][] newEncryptionKeys = new byte[newCapacity][];
                Format[] newFormats = new Format[newCapacity];
                int beforeWrap = this.capacity - this.relativeReadIndex;
                System.arraycopy(this.offsets, this.relativeReadIndex, newOffsets, 0, beforeWrap);
                System.arraycopy(this.timesUs, this.relativeReadIndex, newTimesUs, 0, beforeWrap);
                System.arraycopy(this.flags, this.relativeReadIndex, newFlags, 0, beforeWrap);
                System.arraycopy(this.sizes, this.relativeReadIndex, newSizes, 0, beforeWrap);
                System.arraycopy(this.encryptionKeys, this.relativeReadIndex, newEncryptionKeys, 0, beforeWrap);
                System.arraycopy(this.formats, this.relativeReadIndex, newFormats, 0, beforeWrap);
                System.arraycopy(this.sourceIds, this.relativeReadIndex, newSourceIds, 0, beforeWrap);
                int afterWrap = this.relativeReadIndex;
                System.arraycopy(this.offsets, 0, newOffsets, beforeWrap, afterWrap);
                System.arraycopy(this.timesUs, 0, newTimesUs, beforeWrap, afterWrap);
                System.arraycopy(this.flags, 0, newFlags, beforeWrap, afterWrap);
                System.arraycopy(this.sizes, 0, newSizes, beforeWrap, afterWrap);
                System.arraycopy(this.encryptionKeys, 0, newEncryptionKeys, beforeWrap, afterWrap);
                System.arraycopy(this.formats, 0, newFormats, beforeWrap, afterWrap);
                System.arraycopy(this.sourceIds, 0, newSourceIds, beforeWrap, afterWrap);
                this.offsets = newOffsets;
                this.timesUs = newTimesUs;
                this.flags = newFlags;
                this.sizes = newSizes;
                this.encryptionKeys = newEncryptionKeys;
                this.formats = newFormats;
                this.sourceIds = newSourceIds;
                this.relativeReadIndex = 0;
                this.relativeWriteIndex = this.capacity;
                this.queueSize = this.capacity;
                this.capacity = newCapacity;
            } else {
                this.relativeWriteIndex++;
                if (this.relativeWriteIndex == this.capacity) {
                    this.relativeWriteIndex = 0;
                }
            }
        }

        public synchronized void commitSampleTimestamp(long timeUs) {
            this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, timeUs);
        }

        public synchronized boolean attemptSplice(long timeUs) {
            boolean z;
            if (this.largestDequeuedTimestampUs >= timeUs) {
                z = false;
            } else {
                int retainCount = this.queueSize;
                while (retainCount > 0 && this.timesUs[((this.relativeReadIndex + retainCount) - 1) % this.capacity] >= timeUs) {
                    retainCount--;
                }
                discardUpstreamSamples(this.absoluteReadIndex + retainCount);
                z = true;
            }
            return z;
        }
    }

    public interface UpstreamFormatChangedListener {
        void onUpstreamFormatChanged(Format format);
    }

    public DefaultTrackOutput(Allocator allocator) {
        this.allocator = allocator;
        this.allocationLength = allocator.getIndividualAllocationLength();
    }

    public void reset(boolean enable) {
        int i;
        AtomicInteger atomicInteger = this.state;
        if (enable) {
            i = 0;
        } else {
            i = 2;
        }
        int previousState = atomicInteger.getAndSet(i);
        clearSampleData();
        this.infoQueue.resetLargestParsedTimestamps();
        if (previousState == 2) {
            this.downstreamFormat = null;
        }
    }

    public void sourceId(int sourceId) {
        this.infoQueue.sourceId(sourceId);
    }

    public void splice() {
        this.pendingSplice = true;
    }

    public int getWriteIndex() {
        return this.infoQueue.getWriteIndex();
    }

    public void discardUpstreamSamples(int discardFromIndex) {
        this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(discardFromIndex);
        dropUpstreamFrom(this.totalBytesWritten);
    }

    private void dropUpstreamFrom(long absolutePosition) {
        int relativePosition = (int) (absolutePosition - this.totalBytesDropped);
        int allocationOffset = relativePosition % this.allocationLength;
        int allocationDiscardCount = (this.dataQueue.size() - (relativePosition / this.allocationLength)) - 1;
        if (allocationOffset == 0) {
            allocationDiscardCount++;
        }
        for (int i = 0; i < allocationDiscardCount; i++) {
            this.allocator.release((Allocation) this.dataQueue.removeLast());
        }
        this.lastAllocation = (Allocation) this.dataQueue.peekLast();
        if (allocationOffset == 0) {
            allocationOffset = this.allocationLength;
        }
        this.lastAllocationOffset = allocationOffset;
    }

    public void disable() {
        if (this.state.getAndSet(2) == 0) {
            clearSampleData();
        }
    }

    public boolean isEmpty() {
        return this.infoQueue.isEmpty();
    }

    public int getReadIndex() {
        return this.infoQueue.getReadIndex();
    }

    public int peekSourceId() {
        return this.infoQueue.peekSourceId();
    }

    public Format getUpstreamFormat() {
        return this.infoQueue.getUpstreamFormat();
    }

    public long getLargestQueuedTimestampUs() {
        return this.infoQueue.getLargestQueuedTimestampUs();
    }

    public boolean skipToKeyframeBefore(long timeUs) {
        return skipToKeyframeBefore(timeUs, false);
    }

    public boolean skipToKeyframeBefore(long timeUs, boolean allowTimeBeyondBuffer) {
        long nextOffset = this.infoQueue.skipToKeyframeBefore(timeUs, allowTimeBeyondBuffer);
        if (nextOffset == -1) {
            return false;
        }
        dropDownstreamTo(nextOffset);
        return true;
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, long decodeOnlyUntilUs) {
        switch (this.infoQueue.readData(formatHolder, buffer, formatRequired, loadingFinished, this.downstreamFormat, this.extrasHolder)) {
            case C.RESULT_FORMAT_READ /*-5*/:
                this.downstreamFormat = formatHolder.format;
                return -5;
            case C.RESULT_BUFFER_READ /*-4*/:
                if (!buffer.isEndOfStream()) {
                    if (buffer.timeUs < decodeOnlyUntilUs) {
                        buffer.addFlag(Integer.MIN_VALUE);
                    }
                    if (buffer.isEncrypted()) {
                        readEncryptionData(buffer, this.extrasHolder);
                    }
                    buffer.ensureSpaceForWrite(this.extrasHolder.size);
                    readData(this.extrasHolder.offset, buffer.data, this.extrasHolder.size);
                    dropDownstreamTo(this.extrasHolder.nextOffset);
                }
                return -4;
            case -3:
                return -3;
            default:
                throw new IllegalStateException();
        }
    }

    private void readEncryptionData(DecoderInputBuffer buffer, BufferExtrasHolder extrasHolder) {
        int subsampleCount;
        long offset = extrasHolder.offset;
        this.scratch.reset(1);
        readData(offset, this.scratch.data, 1);
        offset++;
        byte signalByte = this.scratch.data[0];
        boolean subsampleEncryption = (signalByte & 128) != 0;
        int ivSize = signalByte & 127;
        if (buffer.cryptoInfo.iv == null) {
            buffer.cryptoInfo.iv = new byte[16];
        }
        readData(offset, buffer.cryptoInfo.iv, ivSize);
        offset += (long) ivSize;
        if (subsampleEncryption) {
            this.scratch.reset(2);
            readData(offset, this.scratch.data, 2);
            offset += 2;
            subsampleCount = this.scratch.readUnsignedShort();
        } else {
            subsampleCount = 1;
        }
        int[] clearDataSizes = buffer.cryptoInfo.numBytesOfClearData;
        if (clearDataSizes == null || clearDataSizes.length < subsampleCount) {
            clearDataSizes = new int[subsampleCount];
        }
        int[] encryptedDataSizes = buffer.cryptoInfo.numBytesOfEncryptedData;
        if (encryptedDataSizes == null || encryptedDataSizes.length < subsampleCount) {
            encryptedDataSizes = new int[subsampleCount];
        }
        if (subsampleEncryption) {
            int subsampleDataLength = subsampleCount * 6;
            this.scratch.reset(subsampleDataLength);
            readData(offset, this.scratch.data, subsampleDataLength);
            offset += (long) subsampleDataLength;
            this.scratch.setPosition(0);
            for (int i = 0; i < subsampleCount; i++) {
                clearDataSizes[i] = this.scratch.readUnsignedShort();
                encryptedDataSizes[i] = this.scratch.readUnsignedIntToInt();
            }
        } else {
            clearDataSizes[0] = 0;
            encryptedDataSizes[0] = extrasHolder.size - ((int) (offset - extrasHolder.offset));
        }
        buffer.cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, extrasHolder.encryptionKeyId, buffer.cryptoInfo.iv, 1);
        int bytesRead = (int) (offset - extrasHolder.offset);
        extrasHolder.offset += (long) bytesRead;
        extrasHolder.size -= bytesRead;
    }

    private void readData(long absolutePosition, ByteBuffer target, int length) {
        int remaining = length;
        while (remaining > 0) {
            dropDownstreamTo(absolutePosition);
            int positionInAllocation = (int) (absolutePosition - this.totalBytesDropped);
            int toCopy = Math.min(remaining, this.allocationLength - positionInAllocation);
            Allocation allocation = (Allocation) this.dataQueue.peek();
            target.put(allocation.data, allocation.translateOffset(positionInAllocation), toCopy);
            absolutePosition += (long) toCopy;
            remaining -= toCopy;
        }
    }

    private void readData(long absolutePosition, byte[] target, int length) {
        int bytesRead = 0;
        while (bytesRead < length) {
            dropDownstreamTo(absolutePosition);
            int positionInAllocation = (int) (absolutePosition - this.totalBytesDropped);
            int toCopy = Math.min(length - bytesRead, this.allocationLength - positionInAllocation);
            Allocation allocation = (Allocation) this.dataQueue.peek();
            System.arraycopy(allocation.data, allocation.translateOffset(positionInAllocation), target, bytesRead, toCopy);
            absolutePosition += (long) toCopy;
            bytesRead += toCopy;
        }
    }

    private void dropDownstreamTo(long absolutePosition) {
        int allocationIndex = ((int) (absolutePosition - this.totalBytesDropped)) / this.allocationLength;
        for (int i = 0; i < allocationIndex; i++) {
            this.allocator.release((Allocation) this.dataQueue.remove());
            this.totalBytesDropped += (long) this.allocationLength;
        }
    }

    public void setUpstreamFormatChangeListener(UpstreamFormatChangedListener listener) {
        this.upstreamFormatChangeListener = listener;
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        if (this.sampleOffsetUs != sampleOffsetUs) {
            this.sampleOffsetUs = sampleOffsetUs;
            this.pendingFormatAdjustment = true;
        }
    }

    public void format(Format format) {
        Format adjustedFormat = getAdjustedSampleFormat(format, this.sampleOffsetUs);
        boolean formatChanged = this.infoQueue.format(adjustedFormat);
        this.lastUnadjustedFormat = format;
        this.pendingFormatAdjustment = false;
        if (this.upstreamFormatChangeListener != null && formatChanged) {
            this.upstreamFormatChangeListener.onUpstreamFormatChanged(adjustedFormat);
        }
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        if (startWriteOperation()) {
            try {
                int bytesAppended = input.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend(length));
                if (bytesAppended != -1) {
                    this.lastAllocationOffset += bytesAppended;
                    this.totalBytesWritten += (long) bytesAppended;
                    endWriteOperation();
                    return bytesAppended;
                } else if (allowEndOfInput) {
                    return -1;
                } else {
                    throw new EOFException();
                }
            } finally {
                endWriteOperation();
            }
        } else {
            int bytesSkipped = input.skip(length);
            if (bytesSkipped != -1) {
                return bytesSkipped;
            }
            if (allowEndOfInput) {
                return -1;
            }
            throw new EOFException();
        }
    }

    public void sampleData(ParsableByteArray buffer, int length) {
        if (startWriteOperation()) {
            while (length > 0) {
                int thisAppendLength = prepareForAppend(length);
                buffer.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), thisAppendLength);
                this.lastAllocationOffset += thisAppendLength;
                this.totalBytesWritten += (long) thisAppendLength;
                length -= thisAppendLength;
            }
            endWriteOperation();
            return;
        }
        buffer.skipBytes(length);
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, byte[] encryptionKey) {
        if (this.pendingFormatAdjustment) {
            format(this.lastUnadjustedFormat);
        }
        if (startWriteOperation()) {
            try {
                if (this.pendingSplice) {
                    if ((flags & 1) == 0 || !this.infoQueue.attemptSplice(timeUs)) {
                        endWriteOperation();
                        return;
                    }
                    this.pendingSplice = false;
                }
                if (this.needKeyframe) {
                    if ((flags & 1) == 0) {
                        endWriteOperation();
                        return;
                    }
                    this.needKeyframe = false;
                }
                this.infoQueue.commitSample(timeUs + this.sampleOffsetUs, flags, (this.totalBytesWritten - ((long) size)) - ((long) offset), size, encryptionKey);
                endWriteOperation();
            } catch (Throwable th) {
                endWriteOperation();
            }
        } else {
            this.infoQueue.commitSampleTimestamp(timeUs);
        }
    }

    private boolean startWriteOperation() {
        return this.state.compareAndSet(0, 1);
    }

    private void endWriteOperation() {
        if (!this.state.compareAndSet(1, 0)) {
            clearSampleData();
        }
    }

    private void clearSampleData() {
        this.infoQueue.clearSampleData();
        this.allocator.release((Allocation[]) this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
        this.dataQueue.clear();
        this.allocator.trim();
        this.totalBytesDropped = 0;
        this.totalBytesWritten = 0;
        this.lastAllocation = null;
        this.lastAllocationOffset = this.allocationLength;
        this.needKeyframe = true;
    }

    private int prepareForAppend(int length) {
        if (this.lastAllocationOffset == this.allocationLength) {
            this.lastAllocationOffset = 0;
            this.lastAllocation = this.allocator.allocate();
            this.dataQueue.add(this.lastAllocation);
        }
        return Math.min(length, this.allocationLength - this.lastAllocationOffset);
    }

    private static Format getAdjustedSampleFormat(Format format, long sampleOffsetUs) {
        if (format == null) {
            return null;
        }
        if (sampleOffsetUs == 0 || format.subsampleOffsetUs == Long.MAX_VALUE) {
            return format;
        }
        return format.copyWithSubsampleOffsetUs(format.subsampleOffsetUs + sampleOffsetUs);
    }
}
