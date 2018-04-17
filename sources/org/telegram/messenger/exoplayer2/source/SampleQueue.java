package org.telegram.messenger.exoplayer2.source;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.source.SampleMetadataQueue.SampleExtrasHolder;
import org.telegram.messenger.exoplayer2.upstream.Allocation;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SampleQueue implements TrackOutput {
    public static final int ADVANCE_FAILED = -1;
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private Format downstreamFormat;
    private final SampleExtrasHolder extrasHolder = new SampleExtrasHolder();
    private AllocationNode firstAllocationNode = new AllocationNode(0, this.allocationLength);
    private Format lastUnadjustedFormat;
    private final SampleMetadataQueue metadataQueue = new SampleMetadataQueue();
    private boolean pendingFormatAdjustment;
    private boolean pendingSplice;
    private AllocationNode readAllocationNode = this.firstAllocationNode;
    private long sampleOffsetUs;
    private final ParsableByteArray scratch = new ParsableByteArray(32);
    private long totalBytesWritten;
    private UpstreamFormatChangedListener upstreamFormatChangeListener;
    private AllocationNode writeAllocationNode = this.firstAllocationNode;

    private static final class AllocationNode {
        public Allocation allocation;
        public final long endPosition;
        public AllocationNode next;
        public final long startPosition;
        public boolean wasInitialized;

        public AllocationNode(long startPosition, int allocationLength) {
            this.startPosition = startPosition;
            this.endPosition = startPosition + ((long) allocationLength);
        }

        public void initialize(Allocation allocation, AllocationNode next) {
            this.allocation = allocation;
            this.next = next;
            this.wasInitialized = true;
        }

        public int translateOffset(long absolutePosition) {
            return ((int) (absolutePosition - this.startPosition)) + this.allocation.offset;
        }

        public AllocationNode clear() {
            this.allocation = null;
            AllocationNode temp = this.next;
            this.next = null;
            return temp;
        }
    }

    public interface UpstreamFormatChangedListener {
        void onUpstreamFormatChanged(Format format);
    }

    public SampleQueue(Allocator allocator) {
        this.allocator = allocator;
        this.allocationLength = allocator.getIndividualAllocationLength();
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean resetUpstreamFormat) {
        this.metadataQueue.reset(resetUpstreamFormat);
        clearAllocationNodes(this.firstAllocationNode);
        this.firstAllocationNode = new AllocationNode(0, this.allocationLength);
        this.readAllocationNode = this.firstAllocationNode;
        this.writeAllocationNode = this.firstAllocationNode;
        this.totalBytesWritten = 0;
        this.allocator.trim();
    }

    public void sourceId(int sourceId) {
        this.metadataQueue.sourceId(sourceId);
    }

    public void splice() {
        this.pendingSplice = true;
    }

    public int getWriteIndex() {
        return this.metadataQueue.getWriteIndex();
    }

    public void discardUpstreamSamples(int discardFromIndex) {
        this.totalBytesWritten = this.metadataQueue.discardUpstreamSamples(discardFromIndex);
        if (this.totalBytesWritten != 0) {
            if (this.totalBytesWritten != this.firstAllocationNode.startPosition) {
                AllocationNode lastNodeToKeep = this.firstAllocationNode;
                while (this.totalBytesWritten > lastNodeToKeep.endPosition) {
                    lastNodeToKeep = lastNodeToKeep.next;
                }
                AllocationNode firstNodeToDiscard = lastNodeToKeep.next;
                clearAllocationNodes(firstNodeToDiscard);
                lastNodeToKeep.next = new AllocationNode(lastNodeToKeep.endPosition, this.allocationLength);
                this.writeAllocationNode = this.totalBytesWritten == lastNodeToKeep.endPosition ? lastNodeToKeep.next : lastNodeToKeep;
                if (this.readAllocationNode == firstNodeToDiscard) {
                    this.readAllocationNode = lastNodeToKeep.next;
                    return;
                }
                return;
            }
        }
        clearAllocationNodes(this.firstAllocationNode);
        this.firstAllocationNode = new AllocationNode(this.totalBytesWritten, this.allocationLength);
        this.readAllocationNode = this.firstAllocationNode;
        this.writeAllocationNode = this.firstAllocationNode;
    }

    public boolean hasNextSample() {
        return this.metadataQueue.hasNextSample();
    }

    public int getFirstIndex() {
        return this.metadataQueue.getFirstIndex();
    }

    public int getReadIndex() {
        return this.metadataQueue.getReadIndex();
    }

    public int peekSourceId() {
        return this.metadataQueue.peekSourceId();
    }

    public Format getUpstreamFormat() {
        return this.metadataQueue.getUpstreamFormat();
    }

    public long getLargestQueuedTimestampUs() {
        return this.metadataQueue.getLargestQueuedTimestampUs();
    }

    public long getFirstTimestampUs() {
        return this.metadataQueue.getFirstTimestampUs();
    }

    public void rewind() {
        this.metadataQueue.rewind();
        this.readAllocationNode = this.firstAllocationNode;
    }

    public void discardTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        discardDownstreamTo(this.metadataQueue.discardTo(timeUs, toKeyframe, stopAtReadPosition));
    }

    public void discardToRead() {
        discardDownstreamTo(this.metadataQueue.discardToRead());
    }

    public void discardToEnd() {
        discardDownstreamTo(this.metadataQueue.discardToEnd());
    }

    public int advanceToEnd() {
        return this.metadataQueue.advanceToEnd();
    }

    public int advanceTo(long timeUs, boolean toKeyframe, boolean allowTimeBeyondBuffer) {
        return this.metadataQueue.advanceTo(timeUs, toKeyframe, allowTimeBeyondBuffer);
    }

    public boolean setReadPosition(int sampleIndex) {
        return this.metadataQueue.setReadPosition(sampleIndex);
    }

    public int read(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, long decodeOnlyUntilUs) {
        switch (this.metadataQueue.read(formatHolder, buffer, formatRequired, loadingFinished, this.downstreamFormat, this.extrasHolder)) {
            case -5:
                this.downstreamFormat = formatHolder.format;
                return -5;
            case -4:
                if (!buffer.isEndOfStream()) {
                    if (buffer.timeUs < decodeOnlyUntilUs) {
                        buffer.addFlag(Integer.MIN_VALUE);
                    }
                    if (buffer.isEncrypted()) {
                        readEncryptionData(buffer, this.extrasHolder);
                    }
                    buffer.ensureSpaceForWrite(this.extrasHolder.size);
                    readData(this.extrasHolder.offset, buffer.data, this.extrasHolder.size);
                }
                return -4;
            case -3:
                return -3;
            default:
                throw new IllegalStateException();
        }
    }

    private void readEncryptionData(DecoderInputBuffer buffer, SampleExtrasHolder extrasHolder) {
        DecoderInputBuffer decoderInputBuffer = buffer;
        SampleExtrasHolder sampleExtrasHolder = extrasHolder;
        long offset = sampleExtrasHolder.offset;
        int subsampleCount = 1;
        this.scratch.reset(1);
        readData(offset, this.scratch.data, 1);
        long offset2 = offset + 1;
        int i = 0;
        byte signalByte = this.scratch.data[0];
        boolean subsampleEncryption = (signalByte & 128) != 0;
        int ivSize = signalByte & 127;
        if (decoderInputBuffer.cryptoInfo.iv == null) {
            decoderInputBuffer.cryptoInfo.iv = new byte[16];
        }
        readData(offset2, decoderInputBuffer.cryptoInfo.iv, ivSize);
        long offset3 = offset2 + ((long) ivSize);
        if (subsampleEncryption) {
            r0.scratch.reset(2);
            readData(offset3, r0.scratch.data, 2);
            long offset4 = offset3 + 2;
            subsampleCount = r0.scratch.readUnsignedShort();
            offset3 = offset4;
        }
        int[] clearDataSizes = decoderInputBuffer.cryptoInfo.numBytesOfClearData;
        if (clearDataSizes == null || clearDataSizes.length < subsampleCount) {
            clearDataSizes = new int[subsampleCount];
        }
        int[] encryptedDataSizes = decoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
        if (encryptedDataSizes == null || encryptedDataSizes.length < subsampleCount) {
            encryptedDataSizes = new int[subsampleCount];
        }
        if (subsampleEncryption) {
            int subsampleDataLength = 6 * subsampleCount;
            r0.scratch.reset(subsampleDataLength);
            readData(offset3, r0.scratch.data, subsampleDataLength);
            long offset5 = offset3 + ((long) subsampleDataLength);
            r0.scratch.setPosition(0);
            while (i < subsampleCount) {
                clearDataSizes[i] = r0.scratch.readUnsignedShort();
                encryptedDataSizes[i] = r0.scratch.readUnsignedIntToInt();
                i++;
            }
            boolean z = subsampleEncryption;
            offset3 = offset5;
        } else {
            clearDataSizes[0] = 0;
            encryptedDataSizes[0] = sampleExtrasHolder.size - ((int) (offset3 - sampleExtrasHolder.offset));
        }
        CryptoData cryptoData = sampleExtrasHolder.cryptoData;
        decoderInputBuffer.cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, cryptoData.encryptionKey, decoderInputBuffer.cryptoInfo.iv, cryptoData.cryptoMode, cryptoData.encryptedBlocks, cryptoData.clearBlocks);
        int bytesRead = (int) (offset3 - sampleExtrasHolder.offset);
        sampleExtrasHolder.offset += (long) bytesRead;
        sampleExtrasHolder.size -= bytesRead;
    }

    private void readData(long absolutePosition, ByteBuffer target, int length) {
        advanceReadTo(absolutePosition);
        long absolutePosition2 = absolutePosition;
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition2));
            target.put(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(absolutePosition2), toCopy);
            remaining -= toCopy;
            long absolutePosition3 = absolutePosition2 + ((long) toCopy);
            if (absolutePosition3 == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
            absolutePosition2 = absolutePosition3;
        }
    }

    private void readData(long absolutePosition, byte[] target, int length) {
        advanceReadTo(absolutePosition);
        long absolutePosition2 = absolutePosition;
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition2));
            System.arraycopy(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(absolutePosition2), target, length - remaining, toCopy);
            remaining -= toCopy;
            long absolutePosition3 = absolutePosition2 + ((long) toCopy);
            if (absolutePosition3 == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
            absolutePosition2 = absolutePosition3;
        }
    }

    private void advanceReadTo(long absolutePosition) {
        while (absolutePosition >= this.readAllocationNode.endPosition) {
            this.readAllocationNode = this.readAllocationNode.next;
        }
    }

    private void discardDownstreamTo(long absolutePosition) {
        if (absolutePosition != -1) {
            while (absolutePosition >= this.firstAllocationNode.endPosition) {
                this.allocator.release(this.firstAllocationNode.allocation);
                this.firstAllocationNode = this.firstAllocationNode.clear();
            }
            if (this.readAllocationNode.startPosition < this.firstAllocationNode.startPosition) {
                this.readAllocationNode = this.firstAllocationNode;
            }
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
        boolean formatChanged = this.metadataQueue.format(adjustedFormat);
        this.lastUnadjustedFormat = format;
        this.pendingFormatAdjustment = false;
        if (this.upstreamFormatChangeListener != null && formatChanged) {
            this.upstreamFormatChangeListener.onUpstreamFormatChanged(adjustedFormat);
        }
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesAppended = input.read(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), preAppend(length));
        if (bytesAppended != -1) {
            postAppend(bytesAppended);
            return bytesAppended;
        } else if (allowEndOfInput) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public void sampleData(ParsableByteArray buffer, int length) {
        while (length > 0) {
            int bytesAppended = preAppend(length);
            buffer.readBytes(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), bytesAppended);
            length -= bytesAppended;
            postAppend(bytesAppended);
        }
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, CryptoData cryptoData) {
        long j = timeUs;
        if (this.pendingFormatAdjustment) {
            format(r0.lastUnadjustedFormat);
        }
        if (r0.pendingSplice) {
            if ((flags & 1) != 0) {
                if (r0.metadataQueue.attemptSplice(j)) {
                    r0.pendingSplice = false;
                }
            }
            return;
        }
        int i = size;
        r0.metadataQueue.commitSample(j + r0.sampleOffsetUs, flags, (r0.totalBytesWritten - ((long) i)) - ((long) offset), i, cryptoData);
    }

    private void clearAllocationNodes(AllocationNode fromNode) {
        if (fromNode.wasInitialized) {
            Allocation[] allocationsToRelease = new Allocation[(this.writeAllocationNode.wasInitialized + (((int) (this.writeAllocationNode.startPosition - fromNode.startPosition)) / this.allocationLength))];
            AllocationNode currentNode = fromNode;
            for (int i = 0; i < allocationsToRelease.length; i++) {
                allocationsToRelease[i] = currentNode.allocation;
                currentNode = currentNode.clear();
            }
            this.allocator.release(allocationsToRelease);
        }
    }

    private int preAppend(int length) {
        if (!this.writeAllocationNode.wasInitialized) {
            this.writeAllocationNode.initialize(this.allocator.allocate(), new AllocationNode(this.writeAllocationNode.endPosition, this.allocationLength));
        }
        return Math.min(length, (int) (this.writeAllocationNode.endPosition - this.totalBytesWritten));
    }

    private void postAppend(int length) {
        this.totalBytesWritten += (long) length;
        if (this.totalBytesWritten == this.writeAllocationNode.endPosition) {
            this.writeAllocationNode = this.writeAllocationNode.next;
        }
    }

    private static Format getAdjustedSampleFormat(Format format, long sampleOffsetUs) {
        if (format == null) {
            return null;
        }
        if (!(sampleOffsetUs == 0 || format.subsampleOffsetUs == Long.MAX_VALUE)) {
            format = format.copyWithSubsampleOffsetUs(format.subsampleOffsetUs + sampleOffsetUs);
        }
        return format;
    }
}
