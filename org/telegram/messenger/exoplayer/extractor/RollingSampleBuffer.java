package org.telegram.messenger.exoplayer.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.upstream.Allocation;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class RollingSampleBuffer {
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private final LinkedBlockingDeque<Allocation> dataQueue = new LinkedBlockingDeque();
    private final SampleExtrasHolder extrasHolder = new SampleExtrasHolder();
    private final InfoQueue infoQueue = new InfoQueue();
    private Allocation lastAllocation;
    private int lastAllocationOffset = this.allocationLength;
    private final ParsableByteArray scratch = new ParsableByteArray(32);
    private long totalBytesDropped;
    private long totalBytesWritten;

    private static final class InfoQueue {
        private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
        private int absoluteReadIndex;
        private int capacity = 1000;
        private byte[][] encryptionKeys = new byte[this.capacity][];
        private int[] flags = new int[this.capacity];
        private long[] offsets = new long[this.capacity];
        private int queueSize;
        private int relativeReadIndex;
        private int relativeWriteIndex;
        private int[] sizes = new int[this.capacity];
        private long[] timesUs = new long[this.capacity];

        public void clear() {
            this.absoluteReadIndex = 0;
            this.relativeReadIndex = 0;
            this.relativeWriteIndex = 0;
            this.queueSize = 0;
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
                return this.offsets[this.relativeWriteIndex];
            } else if (this.absoluteReadIndex == 0) {
                return 0;
            } else {
                int lastWriteIndex = (this.relativeWriteIndex == 0 ? this.capacity : this.relativeWriteIndex) - 1;
                return this.offsets[lastWriteIndex] + ((long) this.sizes[lastWriteIndex]);
            }
        }

        public int getReadIndex() {
            return this.absoluteReadIndex;
        }

        public synchronized boolean peekSample(SampleHolder holder, SampleExtrasHolder extrasHolder) {
            boolean z;
            if (this.queueSize == 0) {
                z = false;
            } else {
                holder.timeUs = this.timesUs[this.relativeReadIndex];
                holder.size = this.sizes[this.relativeReadIndex];
                holder.flags = this.flags[this.relativeReadIndex];
                extrasHolder.offset = this.offsets[this.relativeReadIndex];
                extrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
                z = true;
            }
            return z;
        }

        public synchronized long moveToNextSample() {
            int lastReadIndex;
            this.queueSize--;
            lastReadIndex = this.relativeReadIndex;
            this.relativeReadIndex = lastReadIndex + 1;
            this.absoluteReadIndex++;
            if (this.relativeReadIndex == this.capacity) {
                this.relativeReadIndex = 0;
            }
            return this.queueSize > 0 ? this.offsets[this.relativeReadIndex] : ((long) this.sizes[lastReadIndex]) + this.offsets[lastReadIndex];
        }

        public synchronized long skipToKeyframeBefore(long timeUs) {
            long j = -1;
            synchronized (this) {
                if (this.queueSize != 0 && timeUs >= this.timesUs[this.relativeReadIndex]) {
                    if (timeUs <= this.timesUs[(this.relativeWriteIndex == 0 ? this.capacity : this.relativeWriteIndex) - 1]) {
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

        public synchronized void commitSample(long timeUs, int sampleFlags, long offset, int size, byte[] encryptionKey) {
            this.timesUs[this.relativeWriteIndex] = timeUs;
            this.offsets[this.relativeWriteIndex] = offset;
            this.sizes[this.relativeWriteIndex] = size;
            this.flags[this.relativeWriteIndex] = sampleFlags;
            this.encryptionKeys[this.relativeWriteIndex] = encryptionKey;
            this.queueSize++;
            if (this.queueSize == this.capacity) {
                int newCapacity = this.capacity + 1000;
                long[] newOffsets = new long[newCapacity];
                long[] newTimesUs = new long[newCapacity];
                int[] newFlags = new int[newCapacity];
                int[] newSizes = new int[newCapacity];
                byte[][] newEncryptionKeys = new byte[newCapacity][];
                int beforeWrap = this.capacity - this.relativeReadIndex;
                System.arraycopy(this.offsets, this.relativeReadIndex, newOffsets, 0, beforeWrap);
                System.arraycopy(this.timesUs, this.relativeReadIndex, newTimesUs, 0, beforeWrap);
                System.arraycopy(this.flags, this.relativeReadIndex, newFlags, 0, beforeWrap);
                System.arraycopy(this.sizes, this.relativeReadIndex, newSizes, 0, beforeWrap);
                System.arraycopy(this.encryptionKeys, this.relativeReadIndex, newEncryptionKeys, 0, beforeWrap);
                int afterWrap = this.relativeReadIndex;
                System.arraycopy(this.offsets, 0, newOffsets, beforeWrap, afterWrap);
                System.arraycopy(this.timesUs, 0, newTimesUs, beforeWrap, afterWrap);
                System.arraycopy(this.flags, 0, newFlags, beforeWrap, afterWrap);
                System.arraycopy(this.sizes, 0, newSizes, beforeWrap, afterWrap);
                System.arraycopy(this.encryptionKeys, 0, newEncryptionKeys, beforeWrap, afterWrap);
                this.offsets = newOffsets;
                this.timesUs = newTimesUs;
                this.flags = newFlags;
                this.sizes = newSizes;
                this.encryptionKeys = newEncryptionKeys;
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
    }

    private static final class SampleExtrasHolder {
        public byte[] encryptionKeyId;
        public long offset;

        private SampleExtrasHolder() {
        }
    }

    public RollingSampleBuffer(Allocator allocator) {
        this.allocator = allocator;
        this.allocationLength = allocator.getIndividualAllocationLength();
    }

    public void clear() {
        this.infoQueue.clear();
        this.allocator.release((Allocation[]) this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
        this.dataQueue.clear();
        this.totalBytesDropped = 0;
        this.totalBytesWritten = 0;
        this.lastAllocation = null;
        this.lastAllocationOffset = this.allocationLength;
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

    public int getReadIndex() {
        return this.infoQueue.getReadIndex();
    }

    public boolean peekSample(SampleHolder holder) {
        return this.infoQueue.peekSample(holder, this.extrasHolder);
    }

    public void skipSample() {
        dropDownstreamTo(this.infoQueue.moveToNextSample());
    }

    public boolean skipToKeyframeBefore(long timeUs) {
        long nextOffset = this.infoQueue.skipToKeyframeBefore(timeUs);
        if (nextOffset == -1) {
            return false;
        }
        dropDownstreamTo(nextOffset);
        return true;
    }

    public boolean readSample(SampleHolder sampleHolder) {
        if (!this.infoQueue.peekSample(sampleHolder, this.extrasHolder)) {
            return false;
        }
        if (sampleHolder.isEncrypted()) {
            readEncryptionData(sampleHolder, this.extrasHolder);
        }
        sampleHolder.ensureSpaceForWrite(sampleHolder.size);
        readData(this.extrasHolder.offset, sampleHolder.data, sampleHolder.size);
        dropDownstreamTo(this.infoQueue.moveToNextSample());
        return true;
    }

    private void readEncryptionData(SampleHolder sampleHolder, SampleExtrasHolder extrasHolder) {
        int subsampleCount;
        long offset = extrasHolder.offset;
        readData(offset, this.scratch.data, 1);
        offset++;
        byte signalByte = this.scratch.data[0];
        boolean subsampleEncryption = (signalByte & 128) != 0;
        int ivSize = signalByte & 127;
        if (sampleHolder.cryptoInfo.iv == null) {
            sampleHolder.cryptoInfo.iv = new byte[16];
        }
        readData(offset, sampleHolder.cryptoInfo.iv, ivSize);
        offset += (long) ivSize;
        if (subsampleEncryption) {
            readData(offset, this.scratch.data, 2);
            offset += 2;
            this.scratch.setPosition(0);
            subsampleCount = this.scratch.readUnsignedShort();
        } else {
            subsampleCount = 1;
        }
        int[] clearDataSizes = sampleHolder.cryptoInfo.numBytesOfClearData;
        if (clearDataSizes == null || clearDataSizes.length < subsampleCount) {
            clearDataSizes = new int[subsampleCount];
        }
        int[] encryptedDataSizes = sampleHolder.cryptoInfo.numBytesOfEncryptedData;
        if (encryptedDataSizes == null || encryptedDataSizes.length < subsampleCount) {
            encryptedDataSizes = new int[subsampleCount];
        }
        if (subsampleEncryption) {
            int subsampleDataLength = subsampleCount * 6;
            ensureCapacity(this.scratch, subsampleDataLength);
            readData(offset, this.scratch.data, subsampleDataLength);
            offset += (long) subsampleDataLength;
            this.scratch.setPosition(0);
            for (int i = 0; i < subsampleCount; i++) {
                clearDataSizes[i] = this.scratch.readUnsignedShort();
                encryptedDataSizes[i] = this.scratch.readUnsignedIntToInt();
            }
        } else {
            clearDataSizes[0] = 0;
            encryptedDataSizes[0] = sampleHolder.size - ((int) (offset - extrasHolder.offset));
        }
        sampleHolder.cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, extrasHolder.encryptionKeyId, sampleHolder.cryptoInfo.iv, 1);
        int bytesRead = (int) (offset - extrasHolder.offset);
        extrasHolder.offset += (long) bytesRead;
        sampleHolder.size -= bytesRead;
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

    private static void ensureCapacity(ParsableByteArray byteArray, int limit) {
        if (byteArray.limit() < limit) {
            byteArray.reset(new byte[limit], limit);
        }
    }

    public long getWritePosition() {
        return this.totalBytesWritten;
    }

    public int appendData(DataSource dataSource, int length, boolean allowEndOfInput) throws IOException {
        int bytesAppended = dataSource.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend(length));
        if (bytesAppended != -1) {
            this.lastAllocationOffset += bytesAppended;
            this.totalBytesWritten += (long) bytesAppended;
            return bytesAppended;
        } else if (allowEndOfInput) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public int appendData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesAppended = input.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), prepareForAppend(length));
        if (bytesAppended != -1) {
            this.lastAllocationOffset += bytesAppended;
            this.totalBytesWritten += (long) bytesAppended;
            return bytesAppended;
        } else if (allowEndOfInput) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public void appendData(ParsableByteArray buffer, int length) {
        while (length > 0) {
            int thisAppendLength = prepareForAppend(length);
            buffer.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), thisAppendLength);
            this.lastAllocationOffset += thisAppendLength;
            this.totalBytesWritten += (long) thisAppendLength;
            length -= thisAppendLength;
        }
    }

    public void commitSample(long sampleTimeUs, int flags, long position, int size, byte[] encryptionKey) {
        this.infoQueue.commitSample(sampleTimeUs, flags, position, size, encryptionKey);
    }

    private int prepareForAppend(int length) {
        if (this.lastAllocationOffset == this.allocationLength) {
            this.lastAllocationOffset = 0;
            this.lastAllocation = this.allocator.allocate();
            this.dataQueue.add(this.lastAllocation);
        }
        return Math.min(length, this.allocationLength - this.lastAllocationOffset);
    }
}
