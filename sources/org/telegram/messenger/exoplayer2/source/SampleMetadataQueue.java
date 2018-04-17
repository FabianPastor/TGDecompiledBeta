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

    public void reset(boolean resetUpstreamFormat) {
        this.length = 0;
        this.absoluteFirstIndex = 0;
        this.relativeFirstIndex = 0;
        this.readPosition = 0;
        this.upstreamKeyframeRequired = true;
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        if (resetUpstreamFormat) {
            this.upstreamFormat = null;
            this.upstreamFormatRequired = true;
        }
    }

    public int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public long discardUpstreamSamples(int discardFromIndex) {
        int discardCount = getWriteIndex() - discardFromIndex;
        boolean z = discardCount >= 0 && discardCount <= this.length - this.readPosition;
        Assertions.checkArgument(z);
        this.length -= discardCount;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.length));
        if (this.length == 0) {
            return 0;
        }
        int relativeLastWriteIndex = getRelativeIndex(this.length - 1);
        return this.offsets[relativeLastWriteIndex] + ((long) this.sizes[relativeLastWriteIndex]);
    }

    public void sourceId(int sourceId) {
        this.upstreamSourceId = sourceId;
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
    public synchronized int read(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, Format downstreamFormat, SampleExtrasHolder extrasHolder) {
        if (hasNextSample()) {
            int relativeReadIndex = getRelativeIndex(this.readPosition);
            if (!formatRequired) {
                if (this.formats[relativeReadIndex] == downstreamFormat) {
                    if (buffer.isFlagsOnly()) {
                        return -3;
                    }
                    buffer.timeUs = this.timesUs[relativeReadIndex];
                    buffer.setFlags(this.flags[relativeReadIndex]);
                    extrasHolder.size = this.sizes[relativeReadIndex];
                    extrasHolder.offset = this.offsets[relativeReadIndex];
                    extrasHolder.cryptoData = this.cryptoDatas[relativeReadIndex];
                    this.readPosition++;
                    return -4;
                }
            }
            formatHolder.format = this.formats[relativeReadIndex];
            return -5;
        } else if (loadingFinished) {
            buffer.setFlags(4);
            return -4;
        } else if (this.upstreamFormat != null && (formatRequired || this.upstreamFormat != downstreamFormat)) {
            formatHolder.format = this.upstreamFormat;
            return -5;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int advanceTo(long timeUs, boolean toKeyframe, boolean allowTimeBeyondBuffer) {
        int relativeReadIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && timeUs >= this.timesUs[relativeReadIndex]) {
            if (timeUs <= this.largestQueuedTimestampUs || allowTimeBeyondBuffer) {
                int offset = findSampleBefore(relativeReadIndex, this.length - this.readPosition, timeUs, toKeyframe);
                if (offset == -1) {
                    return -1;
                }
                this.readPosition += offset;
                return offset;
            }
        }
    }

    public synchronized int advanceToEnd() {
        int skipCount;
        skipCount = this.length - this.readPosition;
        this.readPosition = this.length;
        return skipCount;
    }

    public synchronized boolean setReadPosition(int sampleIndex) {
        if (this.absoluteFirstIndex > sampleIndex || sampleIndex > this.absoluteFirstIndex + this.length) {
            return false;
        }
        this.readPosition = sampleIndex - this.absoluteFirstIndex;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long discardTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        if (this.length != 0) {
            if (timeUs >= this.timesUs[this.relativeFirstIndex]) {
                int i = (!stopAtReadPosition || this.readPosition == this.length) ? this.length : this.readPosition + 1;
                i = findSampleBefore(this.relativeFirstIndex, i, timeUs, toKeyframe);
                if (i == -1) {
                    return -1;
                }
                return discardSamples(i);
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
    public synchronized void commitSample(long timeUs, int sampleFlags, long offset, int size, CryptoData cryptoData) {
        SampleMetadataQueue sampleMetadataQueue = this;
        synchronized (this) {
            int i;
            try {
                if (sampleMetadataQueue.upstreamKeyframeRequired) {
                    i = sampleFlags & 1;
                    if (i == 0) {
                        return;
                    }
                    sampleMetadataQueue.upstreamKeyframeRequired = false;
                }
                Assertions.checkState(sampleMetadataQueue.upstreamFormatRequired ^ 1);
                commitSampleTimestamp(timeUs);
                i = getRelativeIndex(sampleMetadataQueue.length);
                sampleMetadataQueue.timesUs[i] = timeUs;
                sampleMetadataQueue.offsets[i] = offset;
                sampleMetadataQueue.sizes[i] = size;
                sampleMetadataQueue.flags[i] = sampleFlags;
                sampleMetadataQueue.cryptoDatas[i] = cryptoData;
                sampleMetadataQueue.formats[i] = sampleMetadataQueue.upstreamFormat;
                sampleMetadataQueue.sourceIds[i] = sampleMetadataQueue.upstreamSourceId;
                sampleMetadataQueue.length++;
                if (sampleMetadataQueue.length == sampleMetadataQueue.capacity) {
                    int newCapacity = sampleMetadataQueue.capacity + SAMPLE_CAPACITY_INCREMENT;
                    int[] newSourceIds = new int[newCapacity];
                    long[] newOffsets = new long[newCapacity];
                    long[] newTimesUs = new long[newCapacity];
                    int[] newFlags = new int[newCapacity];
                    int[] newSizes = new int[newCapacity];
                    CryptoData[] newCryptoDatas = new CryptoData[newCapacity];
                } else {
                    int relativeEndIndex = i;
                }
            } finally {
                Object obj = 
/*
Method generation error in method: org.telegram.messenger.exoplayer2.source.SampleMetadataQueue.commitSample(long, int, long, int, org.telegram.messenger.exoplayer2.extractor.TrackOutput$CryptoData):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00d7: MERGE  (r2_1 'obj' java.lang.Object) = (r0_1 int), (r3_7 'i' int) in method: org.telegram.messenger.exoplayer2.source.SampleMetadataQueue.commitSample(long, int, long, int, org.telegram.messenger.exoplayer2.extractor.TrackOutput$CryptoData):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:299)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:229)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: MERGE can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 28 more

*/

                public synchronized void commitSampleTimestamp(long timeUs) {
                    this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, timeUs);
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public synchronized boolean attemptSplice(long timeUs) {
                    boolean z = false;
                    if (this.length == 0) {
                        if (timeUs > this.largestDiscardedTimestampUs) {
                            z = true;
                        }
                    } else if (Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.readPosition)) >= timeUs) {
                        return false;
                    } else {
                        int retainCount = this.length;
                        int relativeSampleIndex = getRelativeIndex(this.length - 1);
                        while (retainCount > this.readPosition && this.timesUs[relativeSampleIndex] >= timeUs) {
                            retainCount--;
                            relativeSampleIndex--;
                            if (relativeSampleIndex == -1) {
                                relativeSampleIndex = this.capacity - 1;
                            }
                        }
                        discardUpstreamSamples(this.absoluteFirstIndex + retainCount);
                        return true;
                    }
                }

                private int findSampleBefore(int relativeStartIndex, int length, long timeUs, boolean keyframe) {
                    int sampleCountToTarget = -1;
                    int searchIndex = relativeStartIndex;
                    for (int i = 0; i < length && this.timesUs[searchIndex] <= timeUs; i++) {
                        if (!(keyframe && (this.flags[searchIndex] & 1) == 0)) {
                            sampleCountToTarget = i;
                        }
                        searchIndex++;
                        if (searchIndex == this.capacity) {
                            searchIndex = 0;
                        }
                    }
                    return sampleCountToTarget;
                }

                private long discardSamples(int discardCount) {
                    this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(discardCount));
                    this.length -= discardCount;
                    this.absoluteFirstIndex += discardCount;
                    this.relativeFirstIndex += discardCount;
                    if (this.relativeFirstIndex >= this.capacity) {
                        this.relativeFirstIndex -= this.capacity;
                    }
                    this.readPosition -= discardCount;
                    if (this.readPosition < 0) {
                        this.readPosition = 0;
                    }
                    if (this.length != 0) {
                        return this.offsets[this.relativeFirstIndex];
                    }
                    int relativeLastDiscardIndex = (this.relativeFirstIndex == 0 ? this.capacity : this.relativeFirstIndex) - 1;
                    return this.offsets[relativeLastDiscardIndex] + ((long) this.sizes[relativeLastDiscardIndex]);
                }

                private long getLargestTimestamp(int length) {
                    if (length == 0) {
                        return Long.MIN_VALUE;
                    }
                    long largestTimestampUs = Long.MIN_VALUE;
                    int relativeSampleIndex = getRelativeIndex(length - 1);
                    for (int i = 0; i < length; i++) {
                        largestTimestampUs = Math.max(largestTimestampUs, this.timesUs[relativeSampleIndex]);
                        if ((this.flags[relativeSampleIndex] & 1) != 0) {
                            break;
                        }
                        relativeSampleIndex--;
                        if (relativeSampleIndex == -1) {
                            relativeSampleIndex = this.capacity - 1;
                        }
                    }
                    return largestTimestampUs;
                }

                private int getRelativeIndex(int offset) {
                    int relativeIndex = this.relativeFirstIndex + offset;
                    return relativeIndex < this.capacity ? relativeIndex : relativeIndex - this.capacity;
                }
            }
