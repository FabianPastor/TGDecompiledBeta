package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import com.coremedia.iso.boxes.MetaBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.Results;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.AvcConfig;
import org.telegram.messenger.exoplayer2.video.HevcConfig;

final class AtomParsers {
    private static final String TAG = "AtomParsers";
    private static final int TYPE_cenc = Util.getIntegerCodeForString(C0542C.CENC_TYPE_cenc);
    private static final int TYPE_clcp = Util.getIntegerCodeForString("clcp");
    private static final int TYPE_meta = Util.getIntegerCodeForString(MetaBox.TYPE);
    private static final int TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    private static final int TYPE_soun = Util.getIntegerCodeForString("soun");
    private static final int TYPE_subt = Util.getIntegerCodeForString("subt");
    private static final int TYPE_text = Util.getIntegerCodeForString(MimeTypes.BASE_TYPE_TEXT);
    private static final int TYPE_vide = Util.getIntegerCodeForString("vide");

    private static final class ChunkIterator {
        private final ParsableByteArray chunkOffsets;
        private final boolean chunkOffsetsAreLongs;
        public int index;
        public final int length;
        private int nextSamplesPerChunkChangeIndex;
        public int numSamples;
        public long offset;
        private int remainingSamplesPerChunkChanges;
        private final ParsableByteArray stsc;

        public ChunkIterator(ParsableByteArray stsc, ParsableByteArray chunkOffsets, boolean chunkOffsetsAreLongs) {
            this.stsc = stsc;
            this.chunkOffsets = chunkOffsets;
            this.chunkOffsetsAreLongs = chunkOffsetsAreLongs;
            chunkOffsets.setPosition(12);
            this.length = chunkOffsets.readUnsignedIntToInt();
            stsc.setPosition(12);
            this.remainingSamplesPerChunkChanges = stsc.readUnsignedIntToInt();
            boolean z = true;
            if (stsc.readInt() != 1) {
                z = false;
            }
            Assertions.checkState(z, "first_chunk must be 1");
            this.index = -1;
        }

        public boolean moveNext() {
            int i = this.index + 1;
            this.index = i;
            if (i == this.length) {
                return false;
            }
            long readUnsignedLongToLong;
            if (this.chunkOffsetsAreLongs) {
                readUnsignedLongToLong = this.chunkOffsets.readUnsignedLongToLong();
            } else {
                readUnsignedLongToLong = this.chunkOffsets.readUnsignedInt();
            }
            this.offset = readUnsignedLongToLong;
            if (this.index == this.nextSamplesPerChunkChangeIndex) {
                this.numSamples = this.stsc.readUnsignedIntToInt();
                this.stsc.skipBytes(4);
                i = this.remainingSamplesPerChunkChanges - 1;
                this.remainingSamplesPerChunkChanges = i;
                this.nextSamplesPerChunkChangeIndex = i > 0 ? this.stsc.readUnsignedIntToInt() - 1 : -1;
            }
            return true;
        }
    }

    private interface SampleSizeBox {
        int getSampleCount();

        boolean isFixedSampleSize();

        int readNextSampleSize();
    }

    private static final class StsdData {
        public static final int STSD_HEADER_SIZE = 8;
        public Format format;
        public int nalUnitLengthFieldLength;
        public int requiredSampleTransformation = 0;
        public final TrackEncryptionBox[] trackEncryptionBoxes;

        public StsdData(int numberOfEntries) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[numberOfEntries];
        }
    }

    private static final class TkhdData {
        private final long duration;
        private final int id;
        private final int rotationDegrees;

        public TkhdData(int id, long duration, int rotationDegrees) {
            this.id = id;
            this.duration = duration;
            this.rotationDegrees = rotationDegrees;
        }
    }

    static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize = this.data.readUnsignedIntToInt();
        private final int sampleCount = this.data.readUnsignedIntToInt();

        public StszSampleSizeBox(LeafAtom stszAtom) {
            this.data = stszAtom.data;
            this.data.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            return this.fixedSampleSize == 0 ? this.data.readUnsignedIntToInt() : this.fixedSampleSize;
        }

        public boolean isFixedSampleSize() {
            return this.fixedSampleSize != 0;
        }
    }

    static final class Stz2SampleSizeBox implements SampleSizeBox {
        private int currentByte;
        private final ParsableByteArray data;
        private final int fieldSize = (this.data.readUnsignedIntToInt() & 255);
        private final int sampleCount = this.data.readUnsignedIntToInt();
        private int sampleIndex;

        public Stz2SampleSizeBox(LeafAtom stz2Atom) {
            this.data = stz2Atom.data;
            this.data.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            if (this.fieldSize == 8) {
                return this.data.readUnsignedByte();
            }
            if (this.fieldSize == 16) {
                return this.data.readUnsignedShort();
            }
            int i = this.sampleIndex;
            this.sampleIndex = i + 1;
            if (i % 2 != 0) {
                return this.currentByte & 15;
            }
            this.currentByte = this.data.readUnsignedByte();
            return (this.currentByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        }

        public boolean isFixedSampleSize() {
            return false;
        }
    }

    public static org.telegram.messenger.exoplayer2.extractor.mp4.Track parseTrak(org.telegram.messenger.exoplayer2.extractor.mp4.Atom.ContainerAtom r30, org.telegram.messenger.exoplayer2.extractor.mp4.Atom.LeafAtom r31, long r32, org.telegram.messenger.exoplayer2.drm.DrmInitData r34, boolean r35, boolean r36) throws org.telegram.messenger.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r25_3 org.telegram.messenger.exoplayer2.extractor.mp4.Track) in PHI: PHI: (r25_4 org.telegram.messenger.exoplayer2.extractor.mp4.Track) = (r25_2 org.telegram.messenger.exoplayer2.extractor.mp4.Track), (r25_3 org.telegram.messenger.exoplayer2.extractor.mp4.Track) binds: {(r25_2 org.telegram.messenger.exoplayer2.extractor.mp4.Track)=B:17:0x00ae, (r25_3 org.telegram.messenger.exoplayer2.extractor.mp4.Track)=B:18:0x00b9}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r30;
        r1 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_mdia;
        r1 = r0.getContainerAtomOfType(r1);
        r2 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_hdlr;
        r2 = r1.getLeafAtomOfType(r2);
        r2 = r2.data;
        r2 = parseHdlr(r2);
        r3 = 0;
        r4 = -1;
        if (r2 != r4) goto L_0x0019;
    L_0x0018:
        return r3;
    L_0x0019:
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_tkhd;
        r4 = r0.getLeafAtomOfType(r4);
        r4 = r4.data;
        r15 = parseTkhd(r4);
        r4 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r8 = (r32 > r4 ? 1 : (r32 == r4 ? 0 : -1));
        if (r8 != 0) goto L_0x0035;
    L_0x002e:
        r6 = r15.duration;
        r18 = r6;
        goto L_0x0037;
    L_0x0035:
        r18 = r32;
    L_0x0037:
        r14 = r31;
        r6 = r14.data;
        r20 = parseMvhd(r6);
        r6 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x004a;
    L_0x0043:
        r4 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
    L_0x0048:
        r10 = r4;
        goto L_0x0056;
    L_0x004a:
        r10 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r8 = r18;
        r12 = r20;
        r4 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r8, r10, r12);
        goto L_0x0048;
    L_0x0056:
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_minf;
        r4 = r1.getContainerAtomOfType(r4);
        r5 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stbl;
        r13 = r4.getContainerAtomOfType(r5);
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_mdhd;
        r4 = r1.getLeafAtomOfType(r4);
        r4 = r4.data;
        r12 = parseMdhd(r4);
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsd;
        r4 = r13.getLeafAtomOfType(r4);
        r4 = r4.data;
        r23 = r15.id;
        r24 = r15.rotationDegrees;
        r5 = r12.second;
        r25 = r5;
        r25 = (java.lang.String) r25;
        r22 = r4;
        r26 = r34;
        r27 = r36;
        r8 = parseStsd(r22, r23, r24, r25, r26, r27);
        r4 = 0;
        r5 = 0;
        if (r35 != 0) goto L_0x00a6;
    L_0x0092:
        r6 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_edts;
        r6 = r0.getContainerAtomOfType(r6);
        r6 = parseEdts(r6);
        r7 = r6.first;
        r4 = r7;
        r4 = (long[]) r4;
        r7 = r6.second;
        r5 = r7;
        r5 = (long[]) r5;
    L_0x00a6:
        r23 = r4;
        r24 = r5;
        r4 = r8.format;
        if (r4 != 0) goto L_0x00b9;
    L_0x00ae:
        r25 = r3;
        r26 = r8;
        r28 = r12;
        r27 = r13;
        r29 = r15;
        goto L_0x00f0;
    L_0x00b9:
        r25 = new org.telegram.messenger.exoplayer2.extractor.mp4.Track;
        r4 = r15.id;
        r3 = r12.first;
        r3 = (java.lang.Long) r3;
        r6 = r3.longValue();
        r9 = r8.format;
        r5 = r8.requiredSampleTransformation;
        r3 = r8.trackEncryptionBoxes;
        r0 = r8.nalUnitLengthFieldLength;
        r16 = r3;
        r3 = r25;
        r17 = r5;
        r5 = r2;
        r26 = r8;
        r27 = r9;
        r8 = r20;
        r28 = r12;
        r12 = r27;
        r27 = r13;
        r13 = r17;
        r14 = r16;
        r29 = r15;
        r15 = r0;
        r16 = r23;
        r17 = r24;
        r3.<init>(r4, r5, r6, r8, r10, r12, r13, r14, r15, r16, r17);
    L_0x00f0:
        return r25;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers.parseTrak(org.telegram.messenger.exoplayer2.extractor.mp4.Atom$ContainerAtom, org.telegram.messenger.exoplayer2.extractor.mp4.Atom$LeafAtom, long, org.telegram.messenger.exoplayer2.drm.DrmInitData, boolean, boolean):org.telegram.messenger.exoplayer2.extractor.mp4.Track");
    }

    public static TrackSampleTable parseStbl(Track track, ContainerAtom stblAtom, GaplessInfoHolder gaplessInfoHolder) throws ParserException {
        SampleSizeBox sampleSizeBox;
        Track track2 = track;
        ContainerAtom containerAtom = stblAtom;
        GaplessInfoHolder gaplessInfoHolder2 = gaplessInfoHolder;
        LeafAtom stszAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_stsz);
        if (stszAtom != null) {
            sampleSizeBox = new StszSampleSizeBox(stszAtom);
        } else {
            LeafAtom stz2Atom = containerAtom.getLeafAtomOfType(Atom.TYPE_stz2);
            if (stz2Atom == null) {
                throw new ParserException("Track has no sample table size information");
            }
            sampleSizeBox = new Stz2SampleSizeBox(stz2Atom);
        }
        int sampleCount = sampleSizeBox.getSampleCount();
        if (sampleCount == 0) {
            return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
        }
        int nextSynchronizationSampleIndex;
        boolean isRechunkable;
        long timestampTimeUnits;
        long[] timestamps;
        long[] chunkOffsetsBytes;
        int maximumSize;
        int[] omitClippedSample;
        int[] flags;
        boolean chunkOffsetsAreLongs = false;
        LeafAtom chunkOffsetsAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_stco);
        if (chunkOffsetsAtom == null) {
            chunkOffsetsAreLongs = true;
            chunkOffsetsAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_co64);
        }
        ParsableByteArray chunkOffsets = chunkOffsetsAtom.data;
        ParsableByteArray stsc = containerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
        ParsableByteArray stts = containerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
        LeafAtom stssAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_stss);
        ParsableByteArray ctts = null;
        ParsableByteArray stss = stssAtom != null ? stssAtom.data : null;
        LeafAtom cttsAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
        if (cttsAtom != null) {
            ctts = cttsAtom.data;
        }
        ChunkIterator chunkIterator = new ChunkIterator(stsc, chunkOffsets, chunkOffsetsAreLongs);
        stts.setPosition(12);
        int remainingTimestampDeltaChanges = stts.readUnsignedIntToInt() - 1;
        int remainingSamplesAtTimestampDelta = stts.readUnsignedIntToInt();
        int timestampDeltaInTimeUnits = stts.readUnsignedIntToInt();
        int remainingSamplesAtTimestampOffset = 0;
        int remainingTimestampOffsetChanges = 0;
        int timestampOffset = 0;
        if (ctts != null) {
            ctts.setPosition(12);
            remainingTimestampOffsetChanges = ctts.readUnsignedIntToInt();
        }
        int remainingSynchronizationSamples = 0;
        if (stss != null) {
            nextSynchronizationSampleIndex = -1;
            stss.setPosition(12);
            remainingSynchronizationSamples = stss.readUnsignedIntToInt();
            if (remainingSynchronizationSamples > 0) {
                nextSynchronizationSampleIndex = stss.readUnsignedIntToInt() - 1;
            } else {
                stss = null;
            }
        } else {
            nextSynchronizationSampleIndex = -1;
        }
        int maximumSize2;
        long offset;
        ParsableByteArray stts2;
        int remainingTimestampDeltaChanges2;
        int remainingSamplesInChunk;
        int remainingSamplesAtTimestampDelta2;
        int remainingSynchronizationSamples2;
        int nextSynchronizationSampleIndex2;
        int sampleCount2;
        int timestampDeltaInTimeUnits2;
        int remainingTimestampDeltaChanges3;
        ParsableByteArray stts3;
        Object offsets;
        int i;
        int[] sizes;
        int[] flags2;
        ParsableByteArray parsableByteArray;
        Object offsets2;
        int i2;
        int[] chunkSampleCounts;
        int[] sizes2;
        int maximumSize3;
        GaplessInfoHolder gaplessInfoHolder3;
        long editEndTime;
        long lastSampleEndTime;
        long encoderDelay;
        SampleSizeBox sampleSizeBox2;
        ChunkIterator chunkIterator2;
        int i3;
        ParsableByteArray parsableByteArray2;
        ParsableByteArray parsableByteArray3;
        if (sampleSizeBox.isFixedSampleSize()) {
            if (MimeTypes.AUDIO_RAW.equals(track2.format.sampleMimeType) && remainingTimestampDeltaChanges == 0 && remainingTimestampOffsetChanges == 0 && remainingSynchronizationSamples == 0) {
                isRechunkable = true;
                timestampTimeUnits = 0;
                if (isRechunkable) {
                    isRechunkable = new long[sampleCount];
                    maximumSize2 = new int[sampleCount];
                    chunkOffsetsAtom = new long[sampleCount];
                    chunkOffsets = new int[sampleCount];
                    offset = 0;
                    stts2 = stts;
                    remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges;
                    remainingSamplesInChunk = 0;
                    remainingSamplesAtTimestampDelta2 = remainingSamplesAtTimestampDelta;
                    remainingSynchronizationSamples2 = remainingSynchronizationSamples;
                    nextSynchronizationSampleIndex2 = nextSynchronizationSampleIndex;
                    stssAtom = 0;
                    stsc = timestampDeltaInTimeUnits;
                    timestampDeltaInTimeUnits = 0;
                    while (timestampDeltaInTimeUnits < sampleCount) {
                        while (remainingSamplesInChunk == 0) {
                            sampleCount2 = sampleCount;
                            Assertions.checkState(chunkIterator.moveNext());
                            timestampDeltaInTimeUnits2 = stsc;
                            remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
                            stsc = chunkIterator.offset;
                            remainingSamplesInChunk = chunkIterator.numSamples;
                            offset = stsc;
                            sampleCount = sampleCount2;
                            stsc = timestampDeltaInTimeUnits2;
                            remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges3;
                        }
                        sampleCount2 = sampleCount;
                        timestampDeltaInTimeUnits2 = stsc;
                        remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
                        if (ctts != null) {
                            while (remainingSamplesAtTimestampOffset == 0 && remainingTimestampOffsetChanges > 0) {
                                remainingSamplesAtTimestampOffset = ctts.readUnsignedIntToInt();
                                timestampOffset = ctts.readInt();
                                remainingTimestampOffsetChanges--;
                            }
                            remainingSamplesAtTimestampOffset--;
                        }
                        sampleCount = timestampOffset;
                        isRechunkable[timestampDeltaInTimeUnits] = offset;
                        maximumSize2[timestampDeltaInTimeUnits] = sampleSizeBox.readNextSampleSize();
                        if (maximumSize2[timestampDeltaInTimeUnits] > stssAtom) {
                            stssAtom = maximumSize2[timestampDeltaInTimeUnits];
                        }
                        chunkOffsetsAtom[timestampDeltaInTimeUnits] = timestampTimeUnits + ((long) sampleCount);
                        chunkOffsets[timestampDeltaInTimeUnits] = stss != null ? true : null;
                        if (timestampDeltaInTimeUnits == nextSynchronizationSampleIndex2) {
                            chunkOffsets[timestampDeltaInTimeUnits] = 1;
                            remainingSynchronizationSamples2--;
                            if (remainingSynchronizationSamples2 > 0) {
                                nextSynchronizationSampleIndex2 = stss.readUnsignedIntToInt() - 1;
                            }
                        }
                        int remainingSynchronizationSamples3 = remainingSynchronizationSamples2;
                        long[] offsets3 = isRechunkable;
                        stsc = timestampDeltaInTimeUnits2;
                        long timestampTimeUnits2 = timestampTimeUnits + ((long) stsc);
                        remainingSamplesAtTimestampDelta2--;
                        if (remainingSamplesAtTimestampDelta2 == 0 || remainingTimestampDeltaChanges3 <= 0) {
                            isRechunkable = stts2;
                            remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges3;
                        } else {
                            isRechunkable = stts2;
                            remainingSamplesAtTimestampDelta2 = isRechunkable.readUnsignedIntToInt();
                            stsc = isRechunkable.readInt();
                            remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges3 - 1;
                        }
                        stts3 = isRechunkable;
                        remainingSamplesInChunk--;
                        timestampDeltaInTimeUnits++;
                        timestampOffset = sampleCount;
                        timestampTimeUnits = timestampTimeUnits2;
                        offset += (long) maximumSize2[timestampDeltaInTimeUnits];
                        sampleCount = sampleCount2;
                        isRechunkable = offsets3;
                        remainingSynchronizationSamples2 = remainingSynchronizationSamples3;
                        stts2 = stts3;
                    }
                    offsets = isRechunkable;
                    sampleCount2 = sampleCount;
                    remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
                    stts3 = stts2;
                    Assertions.checkArgument(remainingSamplesAtTimestampOffset != 0);
                    while (remainingTimestampOffsetChanges > 0) {
                        Assertions.checkArgument(ctts.readUnsignedIntToInt() != 0);
                        ctts.readInt();
                        remainingTimestampOffsetChanges--;
                    }
                    if (remainingSynchronizationSamples2 == 0 && remainingSamplesAtTimestampDelta2 == 0 && remainingSamplesInChunk == 0) {
                        if (remainingTimestampDeltaChanges3 != 0) {
                            sampleCount = remainingSamplesAtTimestampDelta2;
                            i = remainingSynchronizationSamples2;
                            remainingSynchronizationSamples2 = remainingTimestampDeltaChanges3;
                            track2 = track;
                            remainingSamplesAtTimestampDelta = sampleCount;
                            sizes = maximumSize2;
                            timestamps = chunkOffsetsAtom;
                            flags2 = chunkOffsets;
                            parsableByteArray = stsc;
                            nextSynchronizationSampleIndex = nextSynchronizationSampleIndex2;
                            offsets2 = offsets;
                            nextSynchronizationSampleIndex2 = stssAtom;
                        }
                    }
                    String str = TAG;
                    isRechunkable = new StringBuilder();
                    isRechunkable.append("Inconsistent stbl box for track ");
                    sampleCount = remainingSamplesAtTimestampDelta2;
                    track2 = track;
                    isRechunkable.append(track2.id);
                    isRechunkable.append(": remainingSynchronizationSamples ");
                    isRechunkable.append(remainingSynchronizationSamples2);
                    isRechunkable.append(", remainingSamplesAtTimestampDelta ");
                    isRechunkable.append(sampleCount);
                    isRechunkable.append(", remainingSamplesInChunk ");
                    isRechunkable.append(remainingSamplesInChunk);
                    isRechunkable.append(", remainingTimestampDeltaChanges ");
                    remainingSynchronizationSamples2 = remainingTimestampDeltaChanges3;
                    isRechunkable.append(remainingSynchronizationSamples2);
                    Log.w(str, isRechunkable.toString());
                    remainingSamplesAtTimestampDelta = sampleCount;
                    sizes = maximumSize2;
                    timestamps = chunkOffsetsAtom;
                    flags2 = chunkOffsets;
                    parsableByteArray = stsc;
                    nextSynchronizationSampleIndex = nextSynchronizationSampleIndex2;
                    offsets2 = offsets;
                    nextSynchronizationSampleIndex2 = stssAtom;
                } else {
                    sampleCount2 = sampleCount;
                    i2 = 0;
                    LeafAtom leafAtom = chunkOffsetsAtom;
                    ParsableByteArray parsableByteArray4 = chunkOffsets;
                    ParsableByteArray parsableByteArray5 = stsc;
                    stts3 = stts;
                    LeafAtom leafAtom2 = stssAtom;
                    LeafAtom leafAtom3 = cttsAtom;
                    chunkOffsetsBytes = new long[chunkIterator.length];
                    chunkSampleCounts = new int[chunkIterator.length];
                    while (chunkIterator.moveNext()) {
                        chunkOffsetsBytes[chunkIterator.index] = chunkIterator.offset;
                        chunkSampleCounts[chunkIterator.index] = chunkIterator.numSamples;
                    }
                    Results rechunkedResults = FixedSampleSizeRechunker.rechunk(sampleSizeBox.readNextSampleSize(), chunkOffsetsBytes, chunkSampleCounts, (long) timestampDeltaInTimeUnits);
                    Object offsets4 = rechunkedResults.offsets;
                    sizes2 = rechunkedResults.sizes;
                    maximumSize3 = rechunkedResults.maximumSize;
                    long[] timestamps2 = rechunkedResults.timestamps;
                    flags2 = rechunkedResults.flags;
                    offsets2 = offsets4;
                    sizes = sizes2;
                    nextSynchronizationSampleIndex2 = maximumSize3;
                    timestamps = timestamps2;
                    i = remainingSynchronizationSamples;
                }
                if (track2.editListDurations == null) {
                    gaplessInfoHolder3 = gaplessInfoHolder;
                    if (gaplessInfoHolder.hasGaplessInfo()) {
                        boolean omitClippedSample2;
                        boolean copyMetadata;
                        int editedSampleCount;
                        int nextSampleIndex;
                        int[] sizes3;
                        long mediaTime;
                        int[] flags3;
                        Object offsets5;
                        int i4;
                        long[] offsets6;
                        Object editedFlags;
                        long[] editedTimestamps;
                        int i5;
                        int nextSampleIndex2;
                        long mediaTime2;
                        long duration;
                        int editedSampleCount2;
                        long[] editedTimestamps2;
                        Object editedFlags2;
                        long endMediaTime;
                        int startIndex;
                        int count;
                        Object flags4;
                        boolean z;
                        long j;
                        int startIndex2;
                        int[] editedFlags3;
                        int endIndex;
                        long mediaTime3;
                        Object editedFlags4;
                        int sampleIndex;
                        if (track2.editListDurations.length == 1 && track2.type == 1 && timestamps.length >= 2) {
                            offset = track2.editListMediaTimes[0];
                            editEndTime = offset + Util.scaleLargeTimestamp(track2.editListDurations[0], track2.timescale, track2.movieTimescale);
                            lastSampleEndTime = timestampTimeUnits;
                            if (timestamps[0] <= offset && offset < timestamps[1] && timestamps[timestamps.length - 1] < editEndTime && editEndTime <= lastSampleEndTime) {
                                long paddingTimeUnits = lastSampleEndTime - editEndTime;
                                encoderDelay = Util.scaleLargeTimestamp(offset - timestamps[0], (long) track2.format.sampleRate, track2.timescale);
                                ctts = Util.scaleLargeTimestamp(paddingTimeUnits, (long) track2.format.sampleRate, track2.timescale);
                                if (!(encoderDelay == 0 && ctts == null) && encoderDelay <= 2147483647L && ctts <= NUM) {
                                    gaplessInfoHolder3.encoderDelay = (int) encoderDelay;
                                    gaplessInfoHolder3.encoderPadding = (int) ctts;
                                    long encoderDelay2 = encoderDelay;
                                    Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                                    return new TrackSampleTable(offsets2, sizes, nextSynchronizationSampleIndex2, timestamps, flags2);
                                }
                                if (track2.editListDurations.length == 1 || track2.editListDurations[0] != 0) {
                                    omitClippedSample2 = track2.type != 1;
                                    copyMetadata = false;
                                    editedSampleCount = 0;
                                    nextSampleIndex = 0;
                                    maximumSize2 = 0;
                                    while (maximumSize2 < track2.editListDurations.length) {
                                        sampleSizeBox2 = sampleSizeBox;
                                        sizes3 = sizes;
                                        mediaTime = track2.editListMediaTimes[maximumSize2];
                                        if (mediaTime == -1) {
                                            flags3 = flags2;
                                            offsets5 = offsets2;
                                            flags2 = Util.scaleLargeTimestamp(track2.editListDurations[maximumSize2], track2.timescale, track2.movieTimescale);
                                            maximumSize3 = Util.binarySearchCeil(timestamps, mediaTime, true, true);
                                            chunkIterator2 = chunkIterator;
                                            i4 = maximumSize2;
                                            chunkIterator = Util.binarySearchCeil(timestamps, mediaTime + flags2, omitClippedSample2, false);
                                            editedSampleCount += chunkIterator - maximumSize3;
                                            maximumSize2 = nextSampleIndex == maximumSize3 ? 1 : 0;
                                            nextSampleIndex = chunkIterator;
                                            copyMetadata = maximumSize2 | copyMetadata;
                                        } else {
                                            flags3 = flags2;
                                            offsets5 = offsets2;
                                            chunkIterator2 = chunkIterator;
                                            i4 = maximumSize2;
                                        }
                                        maximumSize2 = i4 + 1;
                                        sampleSizeBox = sampleSizeBox2;
                                        sizes = sizes3;
                                        offsets2 = offsets5;
                                        flags2 = flags3;
                                        chunkIterator = chunkIterator2;
                                    }
                                    flags3 = flags2;
                                    offsets5 = offsets2;
                                    sampleSizeBox2 = sampleSizeBox;
                                    sizes3 = sizes;
                                    chunkIterator2 = chunkIterator;
                                    flags2 = sampleCount2;
                                    offsets6 = (editedSampleCount == flags2 ? 1 : null) | copyMetadata;
                                    sampleSizeBox = offsets6 == null ? new long[editedSampleCount] : offsets5;
                                    sizes = offsets6 == null ? new int[editedSampleCount] : sizes3;
                                    editedFlags = offsets6 == null ? new int[editedSampleCount] : flags3;
                                    editedTimestamps = new long[editedSampleCount];
                                    offset = 0;
                                    maximumSize2 = 0;
                                    remainingSynchronizationSamples = offsets6 == null ? 0 : nextSynchronizationSampleIndex2;
                                    i5 = 0;
                                    while (i5 < track2.editListDurations.length) {
                                        nextSampleIndex2 = nextSampleIndex;
                                        mediaTime2 = track2.editListMediaTimes[i5];
                                        duration = track2.editListDurations[i5];
                                        if (mediaTime2 == -1) {
                                            editedSampleCount2 = editedSampleCount;
                                            maximumSize = nextSynchronizationSampleIndex2;
                                            editedTimestamps2 = editedTimestamps;
                                            editedFlags2 = editedFlags;
                                            endMediaTime = mediaTime2 + Util.scaleLargeTimestamp(duration, track2.timescale, track2.movieTimescale);
                                            startIndex = Util.binarySearchCeil(timestamps, mediaTime2, true, true);
                                            maximumSize3 = Util.binarySearchCeil(timestamps, endMediaTime, omitClippedSample2, false);
                                            if (offsets6 == null) {
                                                count = maximumSize3 - startIndex;
                                                i3 = flags2;
                                                flags4 = offsets5;
                                                System.arraycopy(flags4, startIndex, sampleSizeBox, maximumSize2, count);
                                                z = omitClippedSample2;
                                                omitClippedSample = sizes3;
                                                System.arraycopy(omitClippedSample, startIndex, sizes, maximumSize2, count);
                                                editedSampleCount = flags3;
                                                nextSynchronizationSampleIndex2 = editedFlags2;
                                                System.arraycopy(editedSampleCount, startIndex, nextSynchronizationSampleIndex2, maximumSize2, count);
                                            } else {
                                                i3 = flags2;
                                                z = omitClippedSample2;
                                                j = endMediaTime;
                                                omitClippedSample = sizes3;
                                                flags4 = offsets5;
                                                editedSampleCount = flags3;
                                                nextSynchronizationSampleIndex2 = editedFlags2;
                                            }
                                            editedTimestamps = maximumSize2;
                                            maximumSize2 = startIndex;
                                            startIndex2 = maximumSize2;
                                            sizes2 = remainingSynchronizationSamples;
                                            while (maximumSize2 < maximumSize3) {
                                                flags = editedSampleCount;
                                                editedFlags3 = nextSynchronizationSampleIndex2;
                                                endIndex = maximumSize3;
                                                mediaTime3 = mediaTime2;
                                                editedTimestamps2[editedTimestamps] = Util.scaleLargeTimestamp(offset, C0542C.MICROS_PER_SECOND, track2.movieTimescale) + Util.scaleLargeTimestamp(timestamps[maximumSize2] - mediaTime2, C0542C.MICROS_PER_SECOND, track2.timescale);
                                                if (offsets6 != null && sizes[editedTimestamps] > sizes2) {
                                                    sizes2 = omitClippedSample[maximumSize2];
                                                }
                                                editedTimestamps++;
                                                maximumSize2++;
                                                editedSampleCount = flags;
                                                nextSynchronizationSampleIndex2 = editedFlags3;
                                                mediaTime2 = mediaTime3;
                                                maximumSize3 = endIndex;
                                            }
                                            flags = editedSampleCount;
                                            editedFlags4 = nextSynchronizationSampleIndex2;
                                            maximumSize2 = editedTimestamps;
                                            remainingSynchronizationSamples = sizes2;
                                        } else {
                                            i3 = flags2;
                                            editedTimestamps2 = editedTimestamps;
                                            editedFlags4 = editedFlags;
                                            mediaTime3 = mediaTime2;
                                            z = omitClippedSample2;
                                            editedSampleCount2 = editedSampleCount;
                                            maximumSize = nextSynchronizationSampleIndex2;
                                            omitClippedSample = sizes3;
                                            flags4 = offsets5;
                                            flags = flags3;
                                        }
                                        i5++;
                                        offsets5 = flags4;
                                        offset += duration;
                                        sizes3 = omitClippedSample;
                                        encoderDelay = -1;
                                        nextSampleIndex = nextSampleIndex2;
                                        nextSynchronizationSampleIndex2 = maximumSize;
                                        editedSampleCount = editedSampleCount2;
                                        editedTimestamps = editedTimestamps2;
                                        flags2 = i3;
                                        omitClippedSample2 = z;
                                        flags3 = flags;
                                        editedFlags = editedFlags4;
                                    }
                                    i3 = flags2;
                                    editedTimestamps2 = editedTimestamps;
                                    editedFlags3 = editedFlags;
                                    nextSampleIndex2 = nextSampleIndex;
                                    z = omitClippedSample2;
                                    editedSampleCount2 = editedSampleCount;
                                    maximumSize = nextSynchronizationSampleIndex2;
                                    ctts = sizes3;
                                    flags4 = offsets5;
                                    flags = flags3;
                                    stss = false;
                                    i5 = 0;
                                    while (true) {
                                        sizes2 = editedFlags3;
                                        if (i5 < sizes2.length && stss == null) {
                                            stss |= (sizes2[i5] & 1) != 0 ? 1 : 0;
                                            i5++;
                                            editedFlags3 = sizes2;
                                        } else if (stss == null) {
                                            Log.w(TAG, "Ignoring edit list: Edited sample sequence does not contain a sync sample.");
                                            sampleIndex = maximumSize2;
                                            Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                                            return new TrackSampleTable(flags4, ctts, maximumSize, timestamps, flags);
                                        } else {
                                            nextSynchronizationSampleIndex2 = maximumSize2;
                                            i2 = nextSampleIndex2;
                                            return new TrackSampleTable(sampleSizeBox, sizes, remainingSynchronizationSamples, editedTimestamps2, sizes2);
                                        }
                                    }
                                    if (stss == null) {
                                        nextSynchronizationSampleIndex2 = maximumSize2;
                                        i2 = nextSampleIndex2;
                                        return new TrackSampleTable(sampleSizeBox, sizes, remainingSynchronizationSamples, editedTimestamps2, sizes2);
                                    }
                                    Log.w(TAG, "Ignoring edit list: Edited sample sequence does not contain a sync sample.");
                                    sampleIndex = maximumSize2;
                                    Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                                    return new TrackSampleTable(flags4, ctts, maximumSize, timestamps, flags);
                                }
                                for (maximumSize2 = 0; maximumSize2 < timestamps.length; maximumSize2++) {
                                    timestamps[maximumSize2] = Util.scaleLargeTimestamp(timestamps[maximumSize2] - track2.editListMediaTimes[0], C0542C.MICROS_PER_SECOND, track2.timescale);
                                }
                                return new TrackSampleTable(offsets2, sizes, nextSynchronizationSampleIndex2, timestamps, flags2);
                            }
                        }
                        parsableByteArray2 = stss;
                        if (track2.editListDurations.length == 1) {
                        }
                        if (track2.type != 1) {
                        }
                        omitClippedSample2 = track2.type != 1;
                        copyMetadata = false;
                        editedSampleCount = 0;
                        nextSampleIndex = 0;
                        maximumSize2 = 0;
                        while (maximumSize2 < track2.editListDurations.length) {
                            sampleSizeBox2 = sampleSizeBox;
                            sizes3 = sizes;
                            mediaTime = track2.editListMediaTimes[maximumSize2];
                            if (mediaTime == -1) {
                                flags3 = flags2;
                                offsets5 = offsets2;
                                chunkIterator2 = chunkIterator;
                                i4 = maximumSize2;
                            } else {
                                flags3 = flags2;
                                offsets5 = offsets2;
                                flags2 = Util.scaleLargeTimestamp(track2.editListDurations[maximumSize2], track2.timescale, track2.movieTimescale);
                                maximumSize3 = Util.binarySearchCeil(timestamps, mediaTime, true, true);
                                chunkIterator2 = chunkIterator;
                                i4 = maximumSize2;
                                chunkIterator = Util.binarySearchCeil(timestamps, mediaTime + flags2, omitClippedSample2, false);
                                editedSampleCount += chunkIterator - maximumSize3;
                                if (nextSampleIndex == maximumSize3) {
                                }
                                nextSampleIndex = chunkIterator;
                                copyMetadata = maximumSize2 | copyMetadata;
                            }
                            maximumSize2 = i4 + 1;
                            sampleSizeBox = sampleSizeBox2;
                            sizes = sizes3;
                            offsets2 = offsets5;
                            flags2 = flags3;
                            chunkIterator = chunkIterator2;
                        }
                        flags3 = flags2;
                        offsets5 = offsets2;
                        sampleSizeBox2 = sampleSizeBox;
                        sizes3 = sizes;
                        chunkIterator2 = chunkIterator;
                        flags2 = sampleCount2;
                        if (editedSampleCount == flags2) {
                        }
                        offsets6 = (editedSampleCount == flags2 ? 1 : null) | copyMetadata;
                        if (offsets6 == null) {
                        }
                        if (offsets6 == null) {
                        }
                        if (offsets6 == null) {
                        }
                        if (offsets6 == null) {
                        }
                        editedFlags = offsets6 == null ? new int[editedSampleCount] : flags3;
                        editedTimestamps = new long[editedSampleCount];
                        offset = 0;
                        maximumSize2 = 0;
                        remainingSynchronizationSamples = offsets6 == null ? 0 : nextSynchronizationSampleIndex2;
                        i5 = 0;
                        while (i5 < track2.editListDurations.length) {
                            nextSampleIndex2 = nextSampleIndex;
                            mediaTime2 = track2.editListMediaTimes[i5];
                            duration = track2.editListDurations[i5];
                            if (mediaTime2 == -1) {
                                i3 = flags2;
                                editedTimestamps2 = editedTimestamps;
                                editedFlags4 = editedFlags;
                                mediaTime3 = mediaTime2;
                                z = omitClippedSample2;
                                editedSampleCount2 = editedSampleCount;
                                maximumSize = nextSynchronizationSampleIndex2;
                                omitClippedSample = sizes3;
                                flags4 = offsets5;
                                flags = flags3;
                            } else {
                                editedSampleCount2 = editedSampleCount;
                                maximumSize = nextSynchronizationSampleIndex2;
                                editedTimestamps2 = editedTimestamps;
                                editedFlags2 = editedFlags;
                                endMediaTime = mediaTime2 + Util.scaleLargeTimestamp(duration, track2.timescale, track2.movieTimescale);
                                startIndex = Util.binarySearchCeil(timestamps, mediaTime2, true, true);
                                maximumSize3 = Util.binarySearchCeil(timestamps, endMediaTime, omitClippedSample2, false);
                                if (offsets6 == null) {
                                    i3 = flags2;
                                    z = omitClippedSample2;
                                    j = endMediaTime;
                                    omitClippedSample = sizes3;
                                    flags4 = offsets5;
                                    editedSampleCount = flags3;
                                    nextSynchronizationSampleIndex2 = editedFlags2;
                                } else {
                                    count = maximumSize3 - startIndex;
                                    i3 = flags2;
                                    flags4 = offsets5;
                                    System.arraycopy(flags4, startIndex, sampleSizeBox, maximumSize2, count);
                                    z = omitClippedSample2;
                                    omitClippedSample = sizes3;
                                    System.arraycopy(omitClippedSample, startIndex, sizes, maximumSize2, count);
                                    editedSampleCount = flags3;
                                    nextSynchronizationSampleIndex2 = editedFlags2;
                                    System.arraycopy(editedSampleCount, startIndex, nextSynchronizationSampleIndex2, maximumSize2, count);
                                }
                                editedTimestamps = maximumSize2;
                                maximumSize2 = startIndex;
                                startIndex2 = maximumSize2;
                                sizes2 = remainingSynchronizationSamples;
                                while (maximumSize2 < maximumSize3) {
                                    flags = editedSampleCount;
                                    editedFlags3 = nextSynchronizationSampleIndex2;
                                    endIndex = maximumSize3;
                                    mediaTime3 = mediaTime2;
                                    editedTimestamps2[editedTimestamps] = Util.scaleLargeTimestamp(offset, C0542C.MICROS_PER_SECOND, track2.movieTimescale) + Util.scaleLargeTimestamp(timestamps[maximumSize2] - mediaTime2, C0542C.MICROS_PER_SECOND, track2.timescale);
                                    sizes2 = omitClippedSample[maximumSize2];
                                    editedTimestamps++;
                                    maximumSize2++;
                                    editedSampleCount = flags;
                                    nextSynchronizationSampleIndex2 = editedFlags3;
                                    mediaTime2 = mediaTime3;
                                    maximumSize3 = endIndex;
                                }
                                flags = editedSampleCount;
                                editedFlags4 = nextSynchronizationSampleIndex2;
                                maximumSize2 = editedTimestamps;
                                remainingSynchronizationSamples = sizes2;
                            }
                            i5++;
                            offsets5 = flags4;
                            offset += duration;
                            sizes3 = omitClippedSample;
                            encoderDelay = -1;
                            nextSampleIndex = nextSampleIndex2;
                            nextSynchronizationSampleIndex2 = maximumSize;
                            editedSampleCount = editedSampleCount2;
                            editedTimestamps = editedTimestamps2;
                            flags2 = i3;
                            omitClippedSample2 = z;
                            flags3 = flags;
                            editedFlags = editedFlags4;
                        }
                        i3 = flags2;
                        editedTimestamps2 = editedTimestamps;
                        editedFlags3 = editedFlags;
                        nextSampleIndex2 = nextSampleIndex;
                        z = omitClippedSample2;
                        editedSampleCount2 = editedSampleCount;
                        maximumSize = nextSynchronizationSampleIndex2;
                        ctts = sizes3;
                        flags4 = offsets5;
                        flags = flags3;
                        stss = false;
                        i5 = 0;
                        while (true) {
                            sizes2 = editedFlags3;
                            if (i5 < sizes2.length) {
                                break;
                            }
                            break;
                            stss |= (sizes2[i5] & 1) != 0 ? 1 : 0;
                            i5++;
                            editedFlags3 = sizes2;
                        }
                        if (stss == null) {
                            Log.w(TAG, "Ignoring edit list: Edited sample sequence does not contain a sync sample.");
                            sampleIndex = maximumSize2;
                            Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                            return new TrackSampleTable(flags4, ctts, maximumSize, timestamps, flags);
                        }
                        nextSynchronizationSampleIndex2 = maximumSize2;
                        i2 = nextSampleIndex2;
                        return new TrackSampleTable(sampleSizeBox, sizes, remainingSynchronizationSamples, editedTimestamps2, sizes2);
                    }
                    flags = flags2;
                    chunkOffsetsBytes = offsets2;
                    sampleSizeBox2 = sampleSizeBox;
                    chunkIterator2 = chunkIterator;
                    parsableByteArray3 = ctts;
                    parsableByteArray2 = stss;
                    maximumSize = nextSynchronizationSampleIndex2;
                    i3 = sampleCount2;
                    omitClippedSample = sizes;
                } else {
                    flags = flags2;
                    chunkOffsetsBytes = offsets2;
                    sampleSizeBox2 = sampleSizeBox;
                    chunkIterator2 = chunkIterator;
                    parsableByteArray3 = ctts;
                    parsableByteArray2 = stss;
                    maximumSize = nextSynchronizationSampleIndex2;
                    i3 = sampleCount2;
                    omitClippedSample = sizes;
                }
                Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                return new TrackSampleTable(chunkOffsetsBytes, omitClippedSample, maximumSize, timestamps, flags);
            }
        }
        isRechunkable = false;
        timestampTimeUnits = 0;
        if (isRechunkable) {
            sampleCount2 = sampleCount;
            i2 = 0;
            LeafAtom leafAtom4 = chunkOffsetsAtom;
            ParsableByteArray parsableByteArray42 = chunkOffsets;
            ParsableByteArray parsableByteArray52 = stsc;
            stts3 = stts;
            LeafAtom leafAtom22 = stssAtom;
            LeafAtom leafAtom32 = cttsAtom;
            chunkOffsetsBytes = new long[chunkIterator.length];
            chunkSampleCounts = new int[chunkIterator.length];
            while (chunkIterator.moveNext()) {
                chunkOffsetsBytes[chunkIterator.index] = chunkIterator.offset;
                chunkSampleCounts[chunkIterator.index] = chunkIterator.numSamples;
            }
            Results rechunkedResults2 = FixedSampleSizeRechunker.rechunk(sampleSizeBox.readNextSampleSize(), chunkOffsetsBytes, chunkSampleCounts, (long) timestampDeltaInTimeUnits);
            Object offsets42 = rechunkedResults2.offsets;
            sizes2 = rechunkedResults2.sizes;
            maximumSize3 = rechunkedResults2.maximumSize;
            long[] timestamps22 = rechunkedResults2.timestamps;
            flags2 = rechunkedResults2.flags;
            offsets2 = offsets42;
            sizes = sizes2;
            nextSynchronizationSampleIndex2 = maximumSize3;
            timestamps = timestamps22;
            i = remainingSynchronizationSamples;
        } else {
            isRechunkable = new long[sampleCount];
            maximumSize2 = new int[sampleCount];
            chunkOffsetsAtom = new long[sampleCount];
            chunkOffsets = new int[sampleCount];
            offset = 0;
            stts2 = stts;
            remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges;
            remainingSamplesInChunk = 0;
            remainingSamplesAtTimestampDelta2 = remainingSamplesAtTimestampDelta;
            remainingSynchronizationSamples2 = remainingSynchronizationSamples;
            nextSynchronizationSampleIndex2 = nextSynchronizationSampleIndex;
            stssAtom = 0;
            stsc = timestampDeltaInTimeUnits;
            timestampDeltaInTimeUnits = 0;
            while (timestampDeltaInTimeUnits < sampleCount) {
                while (remainingSamplesInChunk == 0) {
                    sampleCount2 = sampleCount;
                    Assertions.checkState(chunkIterator.moveNext());
                    timestampDeltaInTimeUnits2 = stsc;
                    remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
                    stsc = chunkIterator.offset;
                    remainingSamplesInChunk = chunkIterator.numSamples;
                    offset = stsc;
                    sampleCount = sampleCount2;
                    stsc = timestampDeltaInTimeUnits2;
                    remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges3;
                }
                sampleCount2 = sampleCount;
                timestampDeltaInTimeUnits2 = stsc;
                remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
                if (ctts != null) {
                    while (remainingSamplesAtTimestampOffset == 0) {
                        remainingSamplesAtTimestampOffset = ctts.readUnsignedIntToInt();
                        timestampOffset = ctts.readInt();
                        remainingTimestampOffsetChanges--;
                    }
                    remainingSamplesAtTimestampOffset--;
                }
                sampleCount = timestampOffset;
                isRechunkable[timestampDeltaInTimeUnits] = offset;
                maximumSize2[timestampDeltaInTimeUnits] = sampleSizeBox.readNextSampleSize();
                if (maximumSize2[timestampDeltaInTimeUnits] > stssAtom) {
                    stssAtom = maximumSize2[timestampDeltaInTimeUnits];
                }
                chunkOffsetsAtom[timestampDeltaInTimeUnits] = timestampTimeUnits + ((long) sampleCount);
                if (stss != null) {
                }
                chunkOffsets[timestampDeltaInTimeUnits] = stss != null ? true : null;
                if (timestampDeltaInTimeUnits == nextSynchronizationSampleIndex2) {
                    chunkOffsets[timestampDeltaInTimeUnits] = 1;
                    remainingSynchronizationSamples2--;
                    if (remainingSynchronizationSamples2 > 0) {
                        nextSynchronizationSampleIndex2 = stss.readUnsignedIntToInt() - 1;
                    }
                }
                int remainingSynchronizationSamples32 = remainingSynchronizationSamples2;
                long[] offsets32 = isRechunkable;
                stsc = timestampDeltaInTimeUnits2;
                long timestampTimeUnits22 = timestampTimeUnits + ((long) stsc);
                remainingSamplesAtTimestampDelta2--;
                if (remainingSamplesAtTimestampDelta2 == 0) {
                }
                isRechunkable = stts2;
                remainingTimestampDeltaChanges2 = remainingTimestampDeltaChanges3;
                stts3 = isRechunkable;
                remainingSamplesInChunk--;
                timestampDeltaInTimeUnits++;
                timestampOffset = sampleCount;
                timestampTimeUnits = timestampTimeUnits22;
                offset += (long) maximumSize2[timestampDeltaInTimeUnits];
                sampleCount = sampleCount2;
                isRechunkable = offsets32;
                remainingSynchronizationSamples2 = remainingSynchronizationSamples32;
                stts2 = stts3;
            }
            offsets = isRechunkable;
            sampleCount2 = sampleCount;
            remainingTimestampDeltaChanges3 = remainingTimestampDeltaChanges2;
            stts3 = stts2;
            if (remainingSamplesAtTimestampOffset != 0) {
            }
            Assertions.checkArgument(remainingSamplesAtTimestampOffset != 0);
            while (remainingTimestampOffsetChanges > 0) {
                if (ctts.readUnsignedIntToInt() != 0) {
                }
                Assertions.checkArgument(ctts.readUnsignedIntToInt() != 0);
                ctts.readInt();
                remainingTimestampOffsetChanges--;
            }
            if (remainingTimestampDeltaChanges3 != 0) {
                sampleCount = remainingSamplesAtTimestampDelta2;
                i = remainingSynchronizationSamples2;
                remainingSynchronizationSamples2 = remainingTimestampDeltaChanges3;
                track2 = track;
                remainingSamplesAtTimestampDelta = sampleCount;
                sizes = maximumSize2;
                timestamps = chunkOffsetsAtom;
                flags2 = chunkOffsets;
                parsableByteArray = stsc;
                nextSynchronizationSampleIndex = nextSynchronizationSampleIndex2;
                offsets2 = offsets;
                nextSynchronizationSampleIndex2 = stssAtom;
            } else {
                String str2 = TAG;
                isRechunkable = new StringBuilder();
                isRechunkable.append("Inconsistent stbl box for track ");
                sampleCount = remainingSamplesAtTimestampDelta2;
                track2 = track;
                isRechunkable.append(track2.id);
                isRechunkable.append(": remainingSynchronizationSamples ");
                isRechunkable.append(remainingSynchronizationSamples2);
                isRechunkable.append(", remainingSamplesAtTimestampDelta ");
                isRechunkable.append(sampleCount);
                isRechunkable.append(", remainingSamplesInChunk ");
                isRechunkable.append(remainingSamplesInChunk);
                isRechunkable.append(", remainingTimestampDeltaChanges ");
                remainingSynchronizationSamples2 = remainingTimestampDeltaChanges3;
                isRechunkable.append(remainingSynchronizationSamples2);
                Log.w(str2, isRechunkable.toString());
                remainingSamplesAtTimestampDelta = sampleCount;
                sizes = maximumSize2;
                timestamps = chunkOffsetsAtom;
                flags2 = chunkOffsets;
                parsableByteArray = stsc;
                nextSynchronizationSampleIndex = nextSynchronizationSampleIndex2;
                offsets2 = offsets;
                nextSynchronizationSampleIndex2 = stssAtom;
            }
        }
        if (track2.editListDurations == null) {
            flags = flags2;
            chunkOffsetsBytes = offsets2;
            sampleSizeBox2 = sampleSizeBox;
            chunkIterator2 = chunkIterator;
            parsableByteArray3 = ctts;
            parsableByteArray2 = stss;
            maximumSize = nextSynchronizationSampleIndex2;
            i3 = sampleCount2;
            omitClippedSample = sizes;
        } else {
            gaplessInfoHolder3 = gaplessInfoHolder;
            if (gaplessInfoHolder.hasGaplessInfo()) {
                offset = track2.editListMediaTimes[0];
                editEndTime = offset + Util.scaleLargeTimestamp(track2.editListDurations[0], track2.timescale, track2.movieTimescale);
                lastSampleEndTime = timestampTimeUnits;
                long paddingTimeUnits2 = lastSampleEndTime - editEndTime;
                encoderDelay = Util.scaleLargeTimestamp(offset - timestamps[0], (long) track2.format.sampleRate, track2.timescale);
                ctts = Util.scaleLargeTimestamp(paddingTimeUnits2, (long) track2.format.sampleRate, track2.timescale);
                gaplessInfoHolder3.encoderDelay = (int) encoderDelay;
                gaplessInfoHolder3.encoderPadding = (int) ctts;
                long encoderDelay22 = encoderDelay;
                Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
                return new TrackSampleTable(offsets2, sizes, nextSynchronizationSampleIndex2, timestamps, flags2);
            }
            flags = flags2;
            chunkOffsetsBytes = offsets2;
            sampleSizeBox2 = sampleSizeBox;
            chunkIterator2 = chunkIterator;
            parsableByteArray3 = ctts;
            parsableByteArray2 = stss;
            maximumSize = nextSynchronizationSampleIndex2;
            i3 = sampleCount2;
            omitClippedSample = sizes;
        }
        Util.scaleLargeTimestampsInPlace(timestamps, C0542C.MICROS_PER_SECOND, track2.timescale);
        return new TrackSampleTable(chunkOffsetsBytes, omitClippedSample, maximumSize, timestamps, flags);
    }

    public static Metadata parseUdta(LeafAtom udtaAtom, boolean isQuickTime) {
        if (isQuickTime) {
            return null;
        }
        ParsableByteArray udtaData = udtaAtom.data;
        udtaData.setPosition(8);
        while (udtaData.bytesLeft() >= 8) {
            int atomPosition = udtaData.getPosition();
            int atomSize = udtaData.readInt();
            if (udtaData.readInt() == Atom.TYPE_meta) {
                udtaData.setPosition(atomPosition);
                return parseMetaAtom(udtaData, atomPosition + atomSize);
            }
            udtaData.skipBytes(atomSize - 8);
        }
        return null;
    }

    private static Metadata parseMetaAtom(ParsableByteArray meta, int limit) {
        meta.skipBytes(12);
        while (meta.getPosition() < limit) {
            int atomPosition = meta.getPosition();
            int atomSize = meta.readInt();
            if (meta.readInt() == Atom.TYPE_ilst) {
                meta.setPosition(atomPosition);
                return parseIlst(meta, atomPosition + atomSize);
            }
            meta.skipBytes(atomSize - 8);
        }
        return null;
    }

    private static Metadata parseIlst(ParsableByteArray ilst, int limit) {
        ilst.skipBytes(8);
        List entries = new ArrayList();
        while (ilst.getPosition() < limit) {
            Entry entry = MetadataUtil.parseIlstElement(ilst);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries.isEmpty() ? null : new Metadata(entries);
    }

    private static long parseMvhd(ParsableByteArray mvhd) {
        int i = 8;
        mvhd.setPosition(8);
        if (Atom.parseFullAtomVersion(mvhd.readInt()) != 0) {
            i = 16;
        }
        mvhd.skipBytes(i);
        return mvhd.readUnsignedInt();
    }

    private static TkhdData parseTkhd(ParsableByteArray tkhd) {
        long duration;
        int durationByteCount = 8;
        tkhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(tkhd.readInt());
        tkhd.skipBytes(version == 0 ? 8 : 16);
        int trackId = tkhd.readInt();
        tkhd.skipBytes(4);
        boolean durationUnknown = true;
        int durationPosition = tkhd.getPosition();
        if (version == 0) {
            durationByteCount = 4;
        }
        int rotationDegrees = 0;
        for (int i = 0; i < durationByteCount; i++) {
            if (tkhd.data[durationPosition + i] != (byte) -1) {
                durationUnknown = false;
                break;
            }
        }
        if (durationUnknown) {
            tkhd.skipBytes(durationByteCount);
            duration = C0542C.TIME_UNSET;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = C0542C.TIME_UNSET;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == C0542C.DEFAULT_BUFFER_SEGMENT_SIZE && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == C0542C.DEFAULT_BUFFER_SEGMENT_SIZE && a11 == 0) {
            rotationDegrees = 270;
        } else if (a00 != (-65536) || a01 != 0 || a10 != 0 || a11 != (-65536)) {
            return new TkhdData(trackId, duration, rotationDegrees);
        } else {
            rotationDegrees = 180;
        }
        return new TkhdData(trackId, duration, rotationDegrees);
    }

    private static int parseHdlr(ParsableByteArray hdlr) {
        hdlr.setPosition(16);
        int trackType = hdlr.readInt();
        if (trackType == TYPE_soun) {
            return 1;
        }
        if (trackType == TYPE_vide) {
            return 2;
        }
        if (!(trackType == TYPE_text || trackType == TYPE_sbtl || trackType == TYPE_subt)) {
            if (trackType != TYPE_clcp) {
                if (trackType == TYPE_meta) {
                    return 4;
                }
                return -1;
            }
        }
        return 3;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray mdhd) {
        int i = 8;
        mdhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(mdhd.readInt());
        mdhd.skipBytes(version == 0 ? 8 : 16);
        long timescale = mdhd.readUnsignedInt();
        if (version == 0) {
            i = 4;
        }
        mdhd.skipBytes(i);
        i = mdhd.readUnsignedShort();
        String language = new StringBuilder();
        language.append(TtmlNode.ANONYMOUS_REGION_ID);
        language.append((char) (((i >> 10) & 31) + 96));
        language.append((char) (((i >> 5) & 31) + 96));
        language.append((char) ((i & 31) + 96));
        return Pair.create(Long.valueOf(timescale), language.toString());
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, int rotationDegrees, String language, DrmInitData drmInitData, boolean isQuickTime) throws ParserException {
        ParsableByteArray parsableByteArray = stsd;
        parsableByteArray.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= numberOfEntries) {
                return out;
            }
            int childAtomType;
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType2 = stsd.readInt();
            if (childAtomType2 == Atom.TYPE_avc1 || childAtomType2 == Atom.TYPE_avc3 || childAtomType2 == Atom.TYPE_encv || childAtomType2 == Atom.TYPE_mp4v || childAtomType2 == Atom.TYPE_hvc1 || childAtomType2 == Atom.TYPE_hev1 || childAtomType2 == Atom.TYPE_s263 || childAtomType2 == Atom.TYPE_vp08) {
                childAtomType = childAtomType2;
            } else if (childAtomType2 == Atom.TYPE_vp09) {
                childAtomType = childAtomType2;
            } else {
                if (!(childAtomType2 == Atom.TYPE_mp4a || childAtomType2 == Atom.TYPE_enca || childAtomType2 == Atom.TYPE_ac_3 || childAtomType2 == Atom.TYPE_ec_3 || childAtomType2 == Atom.TYPE_dtsc || childAtomType2 == Atom.TYPE_dtse || childAtomType2 == Atom.TYPE_dtsh || childAtomType2 == Atom.TYPE_dtsl || childAtomType2 == Atom.TYPE_samr || childAtomType2 == Atom.TYPE_sawb || childAtomType2 == Atom.TYPE_lpcm || childAtomType2 == Atom.TYPE_sowt || childAtomType2 == Atom.TYPE__mp3)) {
                    if (childAtomType2 != Atom.TYPE_alac) {
                        if (!(childAtomType2 == Atom.TYPE_TTML || childAtomType2 == Atom.TYPE_tx3g || childAtomType2 == Atom.TYPE_wvtt || childAtomType2 == Atom.TYPE_stpp)) {
                            if (childAtomType2 != Atom.TYPE_c608) {
                                if (childAtomType2 == Atom.TYPE_camm) {
                                    out.format = Format.createSampleFormat(Integer.toString(trackId), MimeTypes.APPLICATION_CAMERA_MOTION, null, -1, null);
                                }
                                parsableByteArray.setPosition(childStartPosition + childAtomSize);
                                i = i2 + 1;
                            }
                        }
                        parseTextSampleEntry(parsableByteArray, childAtomType2, childStartPosition, childAtomSize, trackId, language, out);
                        parsableByteArray.setPosition(childStartPosition + childAtomSize);
                        i = i2 + 1;
                    }
                }
                childAtomType = childAtomType2;
                parseAudioSampleEntry(parsableByteArray, childAtomType2, childStartPosition, childAtomSize, trackId, language, isQuickTime, drmInitData, out, i2);
                parsableByteArray.setPosition(childStartPosition + childAtomSize);
                i = i2 + 1;
            }
            parseVideoSampleEntry(parsableByteArray, childAtomType, childStartPosition, childAtomSize, trackId, rotationDegrees, drmInitData, out, i2);
            parsableByteArray.setPosition(childStartPosition + childAtomSize);
            i = i2 + 1;
        }
    }

    private static void parseTextSampleEntry(ParsableByteArray parent, int atomType, int position, int atomSize, int trackId, String language, StsdData out) throws ParserException {
        String str;
        ParsableByteArray parsableByteArray = parent;
        int i = atomType;
        StsdData stsdData = out;
        parsableByteArray.setPosition((position + 8) + 8);
        List<byte[]> initializationData = null;
        long subsampleOffsetUs = Long.MAX_VALUE;
        if (i == Atom.TYPE_TTML) {
            str = MimeTypes.APPLICATION_TTML;
        } else if (i == Atom.TYPE_tx3g) {
            str = MimeTypes.APPLICATION_TX3G;
            int sampleDescriptionLength = (atomSize - 8) - 8;
            byte[] sampleDescriptionData = new byte[sampleDescriptionLength];
            parsableByteArray.readBytes(sampleDescriptionData, 0, sampleDescriptionLength);
            initializationData = Collections.singletonList(sampleDescriptionData);
        } else if (i == Atom.TYPE_wvtt) {
            str = MimeTypes.APPLICATION_MP4VTT;
        } else if (i == Atom.TYPE_stpp) {
            str = MimeTypes.APPLICATION_TTML;
            subsampleOffsetUs = 0;
        } else if (i == Atom.TYPE_c608) {
            str = MimeTypes.APPLICATION_MP4CEA608;
            stsdData.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        stsdData.format = Format.createTextSampleFormat(Integer.toString(trackId), str, null, -1, 0, language, -1, null, subsampleOffsetUs, initializationData);
    }

    private static void parseVideoSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, int rotationDegrees, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        ParsableByteArray parsableByteArray = parent;
        int i = position;
        int i2 = size;
        DrmInitData drmInitData2 = drmInitData;
        StsdData stsdData = out;
        parsableByteArray.setPosition((i + 8) + 8);
        parsableByteArray.skipBytes(16);
        int width = parent.readUnsignedShort();
        int height = parent.readUnsignedShort();
        parsableByteArray.skipBytes(50);
        int childPosition = parent.getPosition();
        int atomType2 = atomType;
        if (atomType2 == Atom.TYPE_encv) {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i, i2);
            if (sampleEntryEncryptionData != null) {
                DrmInitData drmInitData3;
                atomType2 = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData2 == null) {
                    drmInitData3 = null;
                } else {
                    drmInitData3 = drmInitData2.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                }
                drmInitData2 = drmInitData3;
                stsdData.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parsableByteArray.setPosition(childPosition);
        }
        int atomType3 = atomType2;
        boolean pixelWidthHeightRatioFromPasp = false;
        float pixelWidthHeightRatio = 1.0f;
        int childPosition2 = childPosition;
        List<byte[]> initializationData = null;
        String mimeType = null;
        byte[] projectionData = null;
        int stereoMode = -1;
        while (childPosition2 - i < i2) {
            parsableByteArray.setPosition(childPosition2);
            int childStartPosition = parent.getPosition();
            int childAtomSize = parent.readInt();
            if (childAtomSize != 0 || parent.getPosition() - i != i2) {
                List<byte[]> initializationData2;
                boolean z = false;
                Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
                atomType2 = parent.readInt();
                if (atomType2 == Atom.TYPE_avcC) {
                    if (mimeType == null) {
                        z = true;
                    }
                    Assertions.checkState(z);
                    mimeType = "video/avc";
                    parsableByteArray.setPosition(childStartPosition + 8);
                    float pixelWidthHeightRatio2 = AvcConfig.parse(parent);
                    initializationData2 = pixelWidthHeightRatio2.initializationData;
                    stsdData.nalUnitLengthFieldLength = pixelWidthHeightRatio2.nalUnitLengthFieldLength;
                    if (!pixelWidthHeightRatioFromPasp) {
                        pixelWidthHeightRatio = pixelWidthHeightRatio2.pixelWidthAspectRatio;
                    }
                } else if (atomType2 == Atom.TYPE_hvcC) {
                    if (mimeType == null) {
                        z = true;
                    }
                    Assertions.checkState(z);
                    mimeType = MimeTypes.VIDEO_H265;
                    parsableByteArray.setPosition(childStartPosition + 8);
                    HevcConfig hevcConfig = HevcConfig.parse(parent);
                    initializationData2 = hevcConfig.initializationData;
                    stsdData.nalUnitLengthFieldLength = hevcConfig.nalUnitLengthFieldLength;
                } else {
                    if (atomType2 == Atom.TYPE_vpcC) {
                        if (mimeType == null) {
                            z = true;
                        }
                        Assertions.checkState(z);
                        mimeType = atomType3 == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
                    } else if (atomType2 == Atom.TYPE_d263) {
                        if (mimeType == null) {
                            z = true;
                        }
                        Assertions.checkState(z);
                        mimeType = MimeTypes.VIDEO_H263;
                    } else if (atomType2 == Atom.TYPE_esds) {
                        if (mimeType == null) {
                            z = true;
                        }
                        Assertions.checkState(z);
                        Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parsableByteArray, childStartPosition);
                        mimeType = mimeTypeAndInitializationData.first;
                        initializationData = Collections.singletonList(mimeTypeAndInitializationData.second);
                    } else if (atomType2 == Atom.TYPE_pasp) {
                        pixelWidthHeightRatio = parsePaspFromParent(parsableByteArray, childStartPosition);
                        pixelWidthHeightRatioFromPasp = true;
                    } else if (atomType2 == Atom.TYPE_sv3d) {
                        projectionData = parseProjFromParent(parsableByteArray, childStartPosition, childAtomSize);
                    } else if (atomType2 == Atom.TYPE_st3d) {
                        childPosition = parent.readUnsignedByte();
                        parsableByteArray.skipBytes(3);
                        if (childPosition == 0) {
                            int stereoMode2;
                            switch (parent.readUnsignedByte()) {
                                case 0:
                                    stereoMode2 = 0;
                                    break;
                                case 1:
                                    stereoMode2 = 1;
                                    break;
                                case 2:
                                    stereoMode2 = 2;
                                    break;
                                case 3:
                                    stereoMode2 = 3;
                                    break;
                                default:
                                    continue;
                            }
                            stereoMode = stereoMode2;
                        }
                    }
                    childPosition2 += childAtomSize;
                }
                initializationData = initializationData2;
                childPosition2 += childAtomSize;
            } else if (mimeType == null) {
                stsdData.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData2);
            }
        }
        if (mimeType == null) {
            stsdData.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData2);
        }
    }

    private static Pair<long[], long[]> parseEdts(ContainerAtom edtsAtom) {
        if (edtsAtom != null) {
            LeafAtom leafAtomOfType = edtsAtom.getLeafAtomOfType(Atom.TYPE_elst);
            LeafAtom elst = leafAtomOfType;
            if (leafAtomOfType != null) {
                ParsableByteArray elstData = elst.data;
                elstData.setPosition(8);
                int version = Atom.parseFullAtomVersion(elstData.readInt());
                int entryCount = elstData.readUnsignedIntToInt();
                long[] editListDurations = new long[entryCount];
                long[] editListMediaTimes = new long[entryCount];
                for (int i = 0; i < entryCount; i++) {
                    editListDurations[i] = version == 1 ? elstData.readUnsignedLongToLong() : elstData.readUnsignedInt();
                    editListMediaTimes[i] = version == 1 ? elstData.readLong() : (long) elstData.readInt();
                    if (elstData.readShort() != 1) {
                        throw new IllegalArgumentException("Unsupported media rate.");
                    }
                    elstData.skipBytes(2);
                }
                return Pair.create(editListDurations, editListMediaTimes);
            }
        }
        return Pair.create(null, null);
    }

    private static float parsePaspFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8);
        return ((float) parent.readUnsignedIntToInt()) / ((float) parent.readUnsignedIntToInt());
    }

    private static void parseAudioSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, String language, boolean isQuickTime, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        int channelCount;
        int childPosition;
        List list;
        int i;
        Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData;
        DrmInitData drmInitData2;
        int atomType2;
        String mimeType;
        String mimeType2;
        int sampleRate;
        int channelCount2;
        byte[] initializationData;
        byte[] initializationData2;
        byte[] initializationData3;
        String mimeType3;
        DrmInitData drmInitData3;
        int atomType3;
        int quickTimeSoundDescriptionVersion;
        int i2;
        int i3;
        int childAtomSize;
        int childPosition2;
        int esdsAtomPosition;
        byte[] initializationData4;
        String mimeType4;
        byte[] initializationData5;
        StsdData stsdData;
        byte[] bArr;
        ParsableByteArray parsableByteArray = parent;
        int i4 = position;
        int i5 = size;
        String str = language;
        DrmInitData drmInitData4 = drmInitData;
        StsdData stsdData2 = out;
        int i6 = 8;
        parsableByteArray.setPosition((i4 + 8) + 8);
        int quickTimeSoundDescriptionVersion2 = 0;
        if (isQuickTime) {
            quickTimeSoundDescriptionVersion2 = parent.readUnsignedShort();
            parsableByteArray.skipBytes(6);
        } else {
            parsableByteArray.skipBytes(8);
        }
        int quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
        int i7 = 2;
        if (quickTimeSoundDescriptionVersion3 != 0) {
            if (quickTimeSoundDescriptionVersion3 != 1) {
                if (quickTimeSoundDescriptionVersion3 == 2) {
                    parsableByteArray.skipBytes(16);
                    quickTimeSoundDescriptionVersion2 = (int) Math.round(parent.readDouble());
                    channelCount = parent.readUnsignedIntToInt();
                    parsableByteArray.skipBytes(20);
                    childPosition = parent.getPosition();
                    list = null;
                    i = atomType;
                    if (i == Atom.TYPE_enca) {
                        sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i4, i5);
                        if (sampleEntryEncryptionData != null) {
                            i = ((Integer) sampleEntryEncryptionData.first).intValue();
                            drmInitData4 = drmInitData4 != null ? null : drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                            stsdData2.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
                        }
                        parsableByteArray.setPosition(childPosition);
                    }
                    drmInitData2 = drmInitData4;
                    atomType2 = i;
                    mimeType = null;
                    if (atomType2 == Atom.TYPE_ac_3) {
                        mimeType = MimeTypes.AUDIO_AC3;
                    } else if (atomType2 == Atom.TYPE_ec_3) {
                        mimeType = MimeTypes.AUDIO_E_AC3;
                    } else if (atomType2 != Atom.TYPE_dtsc) {
                        mimeType = MimeTypes.AUDIO_DTS;
                    } else {
                        if (atomType2 != Atom.TYPE_dtsh) {
                            if (atomType2 == Atom.TYPE_dtsl) {
                                if (atomType2 == Atom.TYPE_dtse) {
                                    mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
                                } else if (atomType2 == Atom.TYPE_samr) {
                                    mimeType = MimeTypes.AUDIO_AMR_NB;
                                } else if (atomType2 != Atom.TYPE_sawb) {
                                    mimeType = MimeTypes.AUDIO_AMR_WB;
                                } else {
                                    if (atomType2 != Atom.TYPE_lpcm) {
                                        if (atomType2 == Atom.TYPE_sowt) {
                                            if (atomType2 == Atom.TYPE__mp3) {
                                                mimeType = MimeTypes.AUDIO_MPEG;
                                            } else if (atomType2 == Atom.TYPE_alac) {
                                                mimeType = MimeTypes.AUDIO_ALAC;
                                            }
                                        }
                                    }
                                    mimeType = MimeTypes.AUDIO_RAW;
                                }
                            }
                        }
                        mimeType = MimeTypes.AUDIO_DTS_HD;
                    }
                    mimeType2 = mimeType;
                    sampleRate = quickTimeSoundDescriptionVersion2;
                    channelCount2 = channelCount;
                    i = childPosition;
                    initializationData = null;
                    while (true) {
                        initializationData2 = initializationData;
                        if (i - i4 < i5) {
                            break;
                        }
                        parsableByteArray.setPosition(i);
                        channelCount = parent.readInt();
                        Assertions.checkArgument(channelCount <= 0, "childAtomSize should be positive");
                        i7 = parent.readInt();
                        if (i7 != Atom.TYPE_esds) {
                            quickTimeSoundDescriptionVersion2 = channelCount;
                            initializationData3 = initializationData2;
                            mimeType3 = mimeType2;
                            drmInitData3 = drmInitData2;
                            atomType3 = atomType2;
                            channelCount = i7;
                            quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                            i2 = i6;
                            i3 = 2;
                            i6 = i;
                        } else if (isQuickTime || i7 != Atom.TYPE_wave) {
                            if (i7 == Atom.TYPE_dac3) {
                                parsableByteArray.setPosition(i6 + i);
                                stsdData2.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                            } else if (i7 != Atom.TYPE_dec3) {
                                parsableByteArray.setPosition(i6 + i);
                                stsdData2.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                            } else {
                                if (i7 != Atom.TYPE_ddts) {
                                    childAtomSize = channelCount;
                                    initializationData3 = initializationData2;
                                    mimeType3 = mimeType2;
                                    childPosition2 = i;
                                    drmInitData3 = drmInitData2;
                                    atomType3 = atomType2;
                                    int childAtomType = i7;
                                    i3 = 2;
                                    quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                                    i2 = i6;
                                    stsdData2.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount2, sampleRate, null, drmInitData3, 0, str);
                                    quickTimeSoundDescriptionVersion2 = childAtomSize;
                                    i6 = childPosition2;
                                    channelCount = childAtomType;
                                } else {
                                    childAtomSize = channelCount;
                                    initializationData3 = initializationData2;
                                    mimeType3 = mimeType2;
                                    childPosition2 = i;
                                    drmInitData3 = drmInitData2;
                                    atomType3 = atomType2;
                                    quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                                    i2 = i6;
                                    i3 = 2;
                                    if (i7 != Atom.TYPE_alac) {
                                        quickTimeSoundDescriptionVersion2 = childAtomSize;
                                        initializationData2 = new byte[quickTimeSoundDescriptionVersion2];
                                        i6 = childPosition2;
                                        parsableByteArray.setPosition(i6);
                                        parsableByteArray.readBytes(initializationData2, 0, quickTimeSoundDescriptionVersion2);
                                        initializationData = initializationData2;
                                        mimeType2 = mimeType3;
                                        i = i6 + quickTimeSoundDescriptionVersion2;
                                        atomType2 = atomType3;
                                        i7 = i3;
                                        drmInitData2 = drmInitData3;
                                        quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                                        i6 = i2;
                                    } else {
                                        quickTimeSoundDescriptionVersion2 = childAtomSize;
                                        i6 = childPosition2;
                                    }
                                }
                                initializationData = initializationData3;
                                mimeType2 = mimeType3;
                                i = i6 + quickTimeSoundDescriptionVersion2;
                                atomType2 = atomType3;
                                i7 = i3;
                                drmInitData2 = drmInitData3;
                                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                                i6 = i2;
                            }
                            quickTimeSoundDescriptionVersion2 = channelCount;
                            initializationData3 = initializationData2;
                            mimeType3 = mimeType2;
                            drmInitData3 = drmInitData2;
                            atomType3 = atomType2;
                            channelCount = i7;
                            quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                            i2 = i6;
                            i3 = 2;
                            i6 = i;
                            initializationData = initializationData3;
                            mimeType2 = mimeType3;
                            i = i6 + quickTimeSoundDescriptionVersion2;
                            atomType2 = atomType3;
                            i7 = i3;
                            drmInitData2 = drmInitData3;
                            quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                            i6 = i2;
                        } else {
                            quickTimeSoundDescriptionVersion2 = channelCount;
                            initializationData3 = initializationData2;
                            mimeType3 = mimeType2;
                            drmInitData3 = drmInitData2;
                            atomType3 = atomType2;
                            channelCount = i7;
                            quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                            i2 = i6;
                            i3 = 2;
                            i6 = i;
                        }
                        esdsAtomPosition = channelCount != Atom.TYPE_esds ? i6 : findEsdsPosition(parsableByteArray, i6, quickTimeSoundDescriptionVersion2);
                        if (esdsAtomPosition == -1) {
                            initializationData2 = parseEsdsFromParent(parsableByteArray, esdsAtomPosition);
                            mimeType2 = (String) initializationData2.first;
                            initializationData4 = initializationData2.second;
                            if (MimeTypes.AUDIO_AAC.equals(mimeType2)) {
                                Pair<Integer, Integer> audioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData4);
                                sampleRate = ((Integer) audioSpecificConfig.first).intValue();
                                channelCount2 = ((Integer) audioSpecificConfig.second).intValue();
                            }
                        } else {
                            initializationData4 = initializationData3;
                            mimeType2 = mimeType3;
                        }
                        initializationData = initializationData4;
                        i = i6 + quickTimeSoundDescriptionVersion2;
                        atomType2 = atomType3;
                        i7 = i3;
                        drmInitData2 = drmInitData3;
                        quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                        i6 = i2;
                    }
                    initializationData3 = initializationData2;
                    mimeType3 = mimeType2;
                    i6 = i;
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    i3 = i7;
                    quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                    if (stsdData2.format != null) {
                        mimeType4 = mimeType3;
                        if (mimeType4 == null) {
                            atomType2 = MimeTypes.AUDIO_RAW.equals(mimeType4) ? i3 : -1;
                            mimeType = Integer.toString(trackId);
                            initializationData5 = initializationData3;
                            if (initializationData5 == null) {
                                list = Collections.singletonList(initializationData5);
                            }
                            List list2 = list;
                            stsdData2.format = Format.createAudioSampleFormat(mimeType, mimeType4, null, -1, -1, channelCount2, sampleRate, atomType2, list2, drmInitData3, 0, str);
                        } else {
                            int i8 = i6;
                            stsdData = stsdData2;
                            bArr = initializationData3;
                        }
                    } else {
                        stsdData = stsdData2;
                        bArr = initializationData3;
                        String str2 = mimeType3;
                    }
                }
                return;
            }
        }
        childPosition = parent.readUnsignedShort();
        parsableByteArray.skipBytes(6);
        channelCount = parent.readUnsignedFixedPoint1616();
        if (quickTimeSoundDescriptionVersion3 == 1) {
            parsableByteArray.skipBytes(16);
        }
        quickTimeSoundDescriptionVersion2 = channelCount;
        channelCount = childPosition;
        childPosition = parent.getPosition();
        list = null;
        i = atomType;
        if (i == Atom.TYPE_enca) {
            sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i4, i5);
            if (sampleEntryEncryptionData != null) {
                i = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData4 != null) {
                }
                drmInitData4 = drmInitData4 != null ? null : drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                stsdData2.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parsableByteArray.setPosition(childPosition);
        }
        drmInitData2 = drmInitData4;
        atomType2 = i;
        mimeType = null;
        if (atomType2 == Atom.TYPE_ac_3) {
            mimeType = MimeTypes.AUDIO_AC3;
        } else if (atomType2 == Atom.TYPE_ec_3) {
            mimeType = MimeTypes.AUDIO_E_AC3;
        } else if (atomType2 != Atom.TYPE_dtsc) {
            if (atomType2 != Atom.TYPE_dtsh) {
                if (atomType2 == Atom.TYPE_dtsl) {
                    if (atomType2 == Atom.TYPE_dtse) {
                        mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
                    } else if (atomType2 == Atom.TYPE_samr) {
                        mimeType = MimeTypes.AUDIO_AMR_NB;
                    } else if (atomType2 != Atom.TYPE_sawb) {
                        if (atomType2 != Atom.TYPE_lpcm) {
                            if (atomType2 == Atom.TYPE_sowt) {
                                if (atomType2 == Atom.TYPE__mp3) {
                                    mimeType = MimeTypes.AUDIO_MPEG;
                                } else if (atomType2 == Atom.TYPE_alac) {
                                    mimeType = MimeTypes.AUDIO_ALAC;
                                }
                            }
                        }
                        mimeType = MimeTypes.AUDIO_RAW;
                    } else {
                        mimeType = MimeTypes.AUDIO_AMR_WB;
                    }
                }
            }
            mimeType = MimeTypes.AUDIO_DTS_HD;
        } else {
            mimeType = MimeTypes.AUDIO_DTS;
        }
        mimeType2 = mimeType;
        sampleRate = quickTimeSoundDescriptionVersion2;
        channelCount2 = channelCount;
        i = childPosition;
        initializationData = null;
        while (true) {
            initializationData2 = initializationData;
            if (i - i4 < i5) {
                break;
            }
            parsableByteArray.setPosition(i);
            channelCount = parent.readInt();
            if (channelCount <= 0) {
            }
            Assertions.checkArgument(channelCount <= 0, "childAtomSize should be positive");
            i7 = parent.readInt();
            if (i7 != Atom.TYPE_esds) {
                quickTimeSoundDescriptionVersion2 = channelCount;
                initializationData3 = initializationData2;
                mimeType3 = mimeType2;
                drmInitData3 = drmInitData2;
                atomType3 = atomType2;
                channelCount = i7;
                quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                i2 = i6;
                i3 = 2;
                i6 = i;
            } else {
                if (isQuickTime) {
                }
                if (i7 == Atom.TYPE_dac3) {
                    parsableByteArray.setPosition(i6 + i);
                    stsdData2.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                } else if (i7 != Atom.TYPE_dec3) {
                    if (i7 != Atom.TYPE_ddts) {
                        childAtomSize = channelCount;
                        initializationData3 = initializationData2;
                        mimeType3 = mimeType2;
                        childPosition2 = i;
                        drmInitData3 = drmInitData2;
                        atomType3 = atomType2;
                        quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                        i2 = i6;
                        i3 = 2;
                        if (i7 != Atom.TYPE_alac) {
                            quickTimeSoundDescriptionVersion2 = childAtomSize;
                            i6 = childPosition2;
                        } else {
                            quickTimeSoundDescriptionVersion2 = childAtomSize;
                            initializationData2 = new byte[quickTimeSoundDescriptionVersion2];
                            i6 = childPosition2;
                            parsableByteArray.setPosition(i6);
                            parsableByteArray.readBytes(initializationData2, 0, quickTimeSoundDescriptionVersion2);
                            initializationData = initializationData2;
                            mimeType2 = mimeType3;
                            i = i6 + quickTimeSoundDescriptionVersion2;
                            atomType2 = atomType3;
                            i7 = i3;
                            drmInitData2 = drmInitData3;
                            quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                            i6 = i2;
                        }
                    } else {
                        childAtomSize = channelCount;
                        initializationData3 = initializationData2;
                        mimeType3 = mimeType2;
                        childPosition2 = i;
                        drmInitData3 = drmInitData2;
                        atomType3 = atomType2;
                        int childAtomType2 = i7;
                        i3 = 2;
                        quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                        i2 = i6;
                        stsdData2.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount2, sampleRate, null, drmInitData3, 0, str);
                        quickTimeSoundDescriptionVersion2 = childAtomSize;
                        i6 = childPosition2;
                        channelCount = childAtomType2;
                    }
                    initializationData = initializationData3;
                    mimeType2 = mimeType3;
                    i = i6 + quickTimeSoundDescriptionVersion2;
                    atomType2 = atomType3;
                    i7 = i3;
                    drmInitData2 = drmInitData3;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                    i6 = i2;
                } else {
                    parsableByteArray.setPosition(i6 + i);
                    stsdData2.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                }
                quickTimeSoundDescriptionVersion2 = channelCount;
                initializationData3 = initializationData2;
                mimeType3 = mimeType2;
                drmInitData3 = drmInitData2;
                atomType3 = atomType2;
                channelCount = i7;
                quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
                i2 = i6;
                i3 = 2;
                i6 = i;
                initializationData = initializationData3;
                mimeType2 = mimeType3;
                i = i6 + quickTimeSoundDescriptionVersion2;
                atomType2 = atomType3;
                i7 = i3;
                drmInitData2 = drmInitData3;
                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
                i6 = i2;
            }
            if (channelCount != Atom.TYPE_esds) {
            }
            esdsAtomPosition = channelCount != Atom.TYPE_esds ? i6 : findEsdsPosition(parsableByteArray, i6, quickTimeSoundDescriptionVersion2);
            if (esdsAtomPosition == -1) {
                initializationData4 = initializationData3;
                mimeType2 = mimeType3;
            } else {
                initializationData2 = parseEsdsFromParent(parsableByteArray, esdsAtomPosition);
                mimeType2 = (String) initializationData2.first;
                initializationData4 = initializationData2.second;
                if (MimeTypes.AUDIO_AAC.equals(mimeType2)) {
                    Pair<Integer, Integer> audioSpecificConfig2 = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData4);
                    sampleRate = ((Integer) audioSpecificConfig2.first).intValue();
                    channelCount2 = ((Integer) audioSpecificConfig2.second).intValue();
                }
            }
            initializationData = initializationData4;
            i = i6 + quickTimeSoundDescriptionVersion2;
            atomType2 = atomType3;
            i7 = i3;
            drmInitData2 = drmInitData3;
            quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion;
            i6 = i2;
        }
        initializationData3 = initializationData2;
        mimeType3 = mimeType2;
        i6 = i;
        drmInitData3 = drmInitData2;
        atomType3 = atomType2;
        i3 = i7;
        quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion3;
        if (stsdData2.format != null) {
            stsdData = stsdData2;
            bArr = initializationData3;
            String str22 = mimeType3;
        } else {
            mimeType4 = mimeType3;
            if (mimeType4 == null) {
                int i82 = i6;
                stsdData = stsdData2;
                bArr = initializationData3;
            } else {
                if (MimeTypes.AUDIO_RAW.equals(mimeType4)) {
                }
                mimeType = Integer.toString(trackId);
                initializationData5 = initializationData3;
                if (initializationData5 == null) {
                    list = Collections.singletonList(initializationData5);
                }
                List list22 = list;
                stsdData2.format = Format.createAudioSampleFormat(mimeType, mimeType4, null, -1, -1, channelCount2, sampleRate, atomType2, list22, drmInitData3, 0, str);
            }
        }
    }

    private static int findEsdsPosition(ParsableByteArray parent, int position, int size) {
        int childAtomPosition = parent.getPosition();
        while (childAtomPosition - position < size) {
            parent.setPosition(childAtomPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_esds) {
                return childAtomPosition;
            }
            childAtomPosition += childAtomSize;
        }
        return -1;
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parent, int position) {
        parent.setPosition((position + 8) + 4);
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        parent.skipBytes(2);
        int flags = parent.readUnsignedByte();
        if ((flags & 128) != 0) {
            parent.skipBytes(2);
        }
        if ((flags & 64) != 0) {
            parent.skipBytes(parent.readUnsignedShort());
        }
        if ((flags & 32) != 0) {
            parent.skipBytes(2);
        }
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        String mimeType = null;
        switch (parent.readUnsignedByte()) {
            case 32:
                mimeType = MimeTypes.VIDEO_MP4V;
                break;
            case 33:
                mimeType = "video/avc";
                break;
            case 35:
                mimeType = MimeTypes.VIDEO_H265;
                break;
            case 64:
            case 102:
            case 103:
            case 104:
                mimeType = MimeTypes.AUDIO_AAC;
                break;
            case 96:
            case 97:
                mimeType = MimeTypes.VIDEO_MPEG2;
                break;
            case 107:
                return Pair.create(MimeTypes.AUDIO_MPEG, null);
            case 165:
                mimeType = MimeTypes.AUDIO_AC3;
                break;
            case 166:
                mimeType = MimeTypes.AUDIO_E_AC3;
                break;
            case 169:
            case 172:
                return Pair.create(MimeTypes.AUDIO_DTS, null);
            case 170:
            case 171:
                return Pair.create(MimeTypes.AUDIO_DTS_HD, null);
            default:
                break;
        }
        parent.skipBytes(12);
        parent.skipBytes(1);
        int initializationDataSize = parseExpandableClassSize(parent);
        byte[] initializationData = new byte[initializationDataSize];
        parent.readBytes(initializationData, 0, initializationDataSize);
        return Pair.create(mimeType, initializationData);
    }

    private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray parent, int position, int size) {
        int childPosition = parent.getPosition();
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_sinf) {
                Pair<Integer, TrackEncryptionBox> result = parseCommonEncryptionSinfFromParent(parent, childPosition, childAtomSize);
                if (result != null) {
                    return result;
                }
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray parent, int position, int size) {
        String schemeType = null;
        int schemeInformationBoxSize = 0;
        int schemeInformationBoxPosition = -1;
        int childPosition = position + 8;
        Integer dataFormat = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_frma) {
                dataFormat = Integer.valueOf(parent.readInt());
            } else if (childAtomType == Atom.TYPE_schm) {
                parent.skipBytes(4);
                schemeType = parent.readString(4);
            } else if (childAtomType == Atom.TYPE_schi) {
                schemeInformationBoxPosition = childPosition;
                schemeInformationBoxSize = childAtomSize;
            }
            childPosition += childAtomSize;
        }
        if (!(C0542C.CENC_TYPE_cenc.equals(schemeType) || C0542C.CENC_TYPE_cbc1.equals(schemeType) || C0542C.CENC_TYPE_cens.equals(schemeType))) {
            if (!C0542C.CENC_TYPE_cbcs.equals(schemeType)) {
                return null;
            }
        }
        boolean z = false;
        Assertions.checkArgument(dataFormat != null, "frma atom is mandatory");
        Assertions.checkArgument(schemeInformationBoxPosition != -1, "schi atom is mandatory");
        TrackEncryptionBox encryptionBox = parseSchiFromParent(parent, schemeInformationBoxPosition, schemeInformationBoxSize, schemeType);
        if (encryptionBox != null) {
            z = true;
        }
        Assertions.checkArgument(z, "tenc atom is mandatory");
        return Pair.create(dataFormat, encryptionBox);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size, String schemeType) {
        ParsableByteArray parsableByteArray = parent;
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parsableByteArray.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_tenc) {
                int version = Atom.parseFullAtomVersion(parent.readInt());
                boolean defaultIsProtected = true;
                parsableByteArray.skipBytes(1);
                int defaultCryptByteBlock = 0;
                int defaultSkipByteBlock = 0;
                if (version == 0) {
                    parsableByteArray.skipBytes(1);
                } else {
                    int patternByte = parent.readUnsignedByte();
                    defaultCryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                    defaultSkipByteBlock = patternByte & 15;
                }
                if (parent.readUnsignedByte() != 1) {
                    defaultIsProtected = false;
                }
                int defaultPerSampleIvSize = parent.readUnsignedByte();
                byte[] bArr = new byte[16];
                parsableByteArray.readBytes(bArr, 0, bArr.length);
                byte[] constantIv = null;
                if (defaultIsProtected && defaultPerSampleIvSize == 0) {
                    int constantIvSize = parent.readUnsignedByte();
                    constantIv = new byte[constantIvSize];
                    parsableByteArray.readBytes(constantIv, 0, constantIvSize);
                }
                byte[] defaultKeyId = bArr;
                return new TrackEncryptionBox(defaultIsProtected, schemeType, defaultPerSampleIvSize, bArr, defaultCryptByteBlock, defaultSkipByteBlock, constantIv);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static byte[] parseProjFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_proj) {
                return Arrays.copyOfRange(parent.data, childPosition, childPosition + childAtomSize);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static int parseExpandableClassSize(ParsableByteArray data) {
        int currentByte = data.readUnsignedByte();
        int size = currentByte & 127;
        while ((currentByte & 128) == 128) {
            currentByte = data.readUnsignedByte();
            size = (size << 7) | (currentByte & 127);
        }
        return size;
    }

    private AtomParsers() {
    }
}
