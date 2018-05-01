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

        public ChunkIterator(ParsableByteArray parsableByteArray, ParsableByteArray parsableByteArray2, boolean z) {
            this.stsc = parsableByteArray;
            this.chunkOffsets = parsableByteArray2;
            this.chunkOffsetsAreLongs = z;
            parsableByteArray2.setPosition(12);
            this.length = parsableByteArray2.readUnsignedIntToInt();
            parsableByteArray.setPosition(12);
            this.remainingSamplesPerChunkChanges = parsableByteArray.readUnsignedIntToInt();
            parsableByteArray2 = true;
            if (parsableByteArray.readInt() != 1) {
                parsableByteArray2 = null;
            }
            Assertions.checkState(parsableByteArray2, "first_chunk must be 1");
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

        public StsdData(int i) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[i];
        }
    }

    private static final class TkhdData {
        private final long duration;
        private final int id;
        private final int rotationDegrees;

        public TkhdData(int i, long j, int i2) {
            this.id = i;
            this.duration = j;
            this.rotationDegrees = i2;
        }
    }

    static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize = this.data.readUnsignedIntToInt();
        private final int sampleCount = this.data.readUnsignedIntToInt();

        public StszSampleSizeBox(LeafAtom leafAtom) {
            this.data = leafAtom.data;
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

        public boolean isFixedSampleSize() {
            return false;
        }

        public Stz2SampleSizeBox(LeafAtom leafAtom) {
            this.data = leafAtom.data;
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
    }

    public static org.telegram.messenger.exoplayer2.extractor.mp4.Track parseTrak(org.telegram.messenger.exoplayer2.extractor.mp4.Atom.ContainerAtom r22, org.telegram.messenger.exoplayer2.extractor.mp4.Atom.LeafAtom r23, long r24, org.telegram.messenger.exoplayer2.drm.DrmInitData r26, boolean r27, boolean r28) throws org.telegram.messenger.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r0_7 org.telegram.messenger.exoplayer2.extractor.mp4.Track) in PHI: PHI: (r0_8 org.telegram.messenger.exoplayer2.extractor.mp4.Track) = (r0_6 org.telegram.messenger.exoplayer2.extractor.mp4.Track), (r0_7 org.telegram.messenger.exoplayer2.extractor.mp4.Track) binds: {(r0_6 org.telegram.messenger.exoplayer2.extractor.mp4.Track)=B:17:0x00a7, (r0_7 org.telegram.messenger.exoplayer2.extractor.mp4.Track)=B:18:0x00a9}
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
        r0 = r22;
        r1 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_mdia;
        r1 = r0.getContainerAtomOfType(r1);
        r2 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_hdlr;
        r2 = r1.getLeafAtomOfType(r2);
        r2 = r2.data;
        r5 = parseHdlr(r2);
        r2 = 0;
        r3 = -1;
        if (r5 != r3) goto L_0x0019;
    L_0x0018:
        return r2;
    L_0x0019:
        r3 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_tkhd;
        r3 = r0.getLeafAtomOfType(r3);
        r3 = r3.data;
        r3 = parseTkhd(r3);
        r6 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r4 = (r24 > r6 ? 1 : (r24 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0035;
    L_0x002e:
        r8 = r3.duration;
        r4 = r23;
        goto L_0x0039;
    L_0x0035:
        r4 = r23;
        r8 = r24;
    L_0x0039:
        r4 = r4.data;
        r14 = parseMvhd(r4);
        r4 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0045;
    L_0x0043:
        r10 = r6;
        goto L_0x004e;
    L_0x0045:
        r10 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r12 = r14;
        r6 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r8, r10, r12);
        goto L_0x0043;
    L_0x004e:
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_minf;
        r4 = r1.getContainerAtomOfType(r4);
        r6 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stbl;
        r4 = r4.getContainerAtomOfType(r6);
        r6 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_mdhd;
        r1 = r1.getLeafAtomOfType(r6);
        r1 = r1.data;
        r1 = parseMdhd(r1);
        r6 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsd;
        r4 = r4.getLeafAtomOfType(r6);
        r4 = r4.data;
        r17 = r3.id;
        r18 = r3.rotationDegrees;
        r6 = r1.second;
        r19 = r6;
        r19 = (java.lang.String) r19;
        r16 = r4;
        r20 = r26;
        r21 = r28;
        r4 = parseStsd(r16, r17, r18, r19, r20, r21);
        if (r27 != 0) goto L_0x009f;
    L_0x0088:
        r6 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_edts;
        r0 = r0.getContainerAtomOfType(r6);
        r0 = parseEdts(r0);
        r6 = r0.first;
        r6 = (long[]) r6;
        r0 = r0.second;
        r0 = (long[]) r0;
        r17 = r0;
        r16 = r6;
        goto L_0x00a3;
    L_0x009f:
        r16 = r2;
        r17 = r16;
    L_0x00a3:
        r0 = r4.format;
        if (r0 != 0) goto L_0x00a9;
    L_0x00a7:
        r0 = r2;
        goto L_0x00c8;
    L_0x00a9:
        r0 = new org.telegram.messenger.exoplayer2.extractor.mp4.Track;
        r2 = r3.id;
        r1 = r1.first;
        r1 = (java.lang.Long) r1;
        r6 = r1.longValue();
        r12 = r4.format;
        r13 = r4.requiredSampleTransformation;
        r1 = r4.trackEncryptionBoxes;
        r8 = r4.nalUnitLengthFieldLength;
        r3 = r0;
        r4 = r2;
        r2 = r8;
        r8 = r14;
        r14 = r1;
        r15 = r2;
        r3.<init>(r4, r5, r6, r8, r10, r12, r13, r14, r15, r16, r17);
    L_0x00c8:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers.parseTrak(org.telegram.messenger.exoplayer2.extractor.mp4.Atom$ContainerAtom, org.telegram.messenger.exoplayer2.extractor.mp4.Atom$LeafAtom, long, org.telegram.messenger.exoplayer2.drm.DrmInitData, boolean, boolean):org.telegram.messenger.exoplayer2.extractor.mp4.Track");
    }

    public static TrackSampleTable parseStbl(Track track, ContainerAtom containerAtom, GaplessInfoHolder gaplessInfoHolder) throws ParserException {
        SampleSizeBox stszSampleSizeBox;
        Track track2 = track;
        ContainerAtom containerAtom2 = containerAtom;
        GaplessInfoHolder gaplessInfoHolder2 = gaplessInfoHolder;
        LeafAtom leafAtomOfType = containerAtom2.getLeafAtomOfType(Atom.TYPE_stsz);
        if (leafAtomOfType != null) {
            stszSampleSizeBox = new StszSampleSizeBox(leafAtomOfType);
        } else {
            leafAtomOfType = containerAtom2.getLeafAtomOfType(Atom.TYPE_stz2);
            if (leafAtomOfType == null) {
                throw new ParserException("Track has no sample table size information");
            }
            stszSampleSizeBox = new Stz2SampleSizeBox(leafAtomOfType);
        }
        int sampleCount = stszSampleSizeBox.getSampleCount();
        if (sampleCount == 0) {
            return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
        }
        boolean z;
        int readUnsignedIntToInt;
        int readUnsignedIntToInt2;
        Object obj;
        Object obj2;
        long[] jArr;
        Object obj3;
        int i;
        int i2;
        int i3;
        int i4;
        long j;
        int i5;
        Object obj4;
        Object obj5;
        Object obj6;
        LeafAtom leafAtomOfType2 = containerAtom2.getLeafAtomOfType(Atom.TYPE_stco);
        if (leafAtomOfType2 == null) {
            leafAtomOfType2 = containerAtom2.getLeafAtomOfType(Atom.TYPE_co64);
            z = true;
        } else {
            z = false;
        }
        ParsableByteArray parsableByteArray = leafAtomOfType2.data;
        ParsableByteArray parsableByteArray2 = containerAtom2.getLeafAtomOfType(Atom.TYPE_stsc).data;
        ParsableByteArray parsableByteArray3 = containerAtom2.getLeafAtomOfType(Atom.TYPE_stts).data;
        LeafAtom leafAtomOfType3 = containerAtom2.getLeafAtomOfType(Atom.TYPE_stss);
        ParsableByteArray parsableByteArray4 = leafAtomOfType3 != null ? leafAtomOfType3.data : null;
        LeafAtom leafAtomOfType4 = containerAtom2.getLeafAtomOfType(Atom.TYPE_ctts);
        ParsableByteArray parsableByteArray5 = leafAtomOfType4 != null ? leafAtomOfType4.data : null;
        ChunkIterator chunkIterator = new ChunkIterator(parsableByteArray2, parsableByteArray, z);
        parsableByteArray3.setPosition(12);
        int readUnsignedIntToInt3 = parsableByteArray3.readUnsignedIntToInt() - 1;
        int readUnsignedIntToInt4 = parsableByteArray3.readUnsignedIntToInt();
        int readUnsignedIntToInt5 = parsableByteArray3.readUnsignedIntToInt();
        if (parsableByteArray5 != null) {
            parsableByteArray5.setPosition(12);
            readUnsignedIntToInt = parsableByteArray5.readUnsignedIntToInt();
        } else {
            readUnsignedIntToInt = 0;
        }
        int i6 = -1;
        if (parsableByteArray4 != null) {
            parsableByteArray4.setPosition(12);
            readUnsignedIntToInt2 = parsableByteArray4.readUnsignedIntToInt();
            if (readUnsignedIntToInt2 > 0) {
                i6 = parsableByteArray4.readUnsignedIntToInt() - 1;
            } else {
                parsableByteArray4 = null;
            }
        } else {
            readUnsignedIntToInt2 = 0;
        }
        int i7 = (stszSampleSizeBox.isFixedSampleSize() && MimeTypes.AUDIO_RAW.equals(track2.format.sampleMimeType) && readUnsignedIntToInt3 == 0 && readUnsignedIntToInt == 0 && readUnsignedIntToInt2 == 0) ? 1 : 0;
        long j2 = 0;
        if (i7 == 0) {
            int i8;
            obj = new long[sampleCount];
            obj2 = new int[sampleCount];
            jArr = new long[sampleCount];
            int i9 = readUnsignedIntToInt2;
            obj3 = new int[sampleCount];
            ParsableByteArray parsableByteArray6 = parsableByteArray3;
            i = i6;
            long j3 = 0;
            long j4 = j3;
            int i10 = i9;
            i6 = 0;
            i2 = 0;
            int i11 = 0;
            i3 = readUnsignedIntToInt3;
            i9 = readUnsignedIntToInt;
            readUnsignedIntToInt3 = 0;
            readUnsignedIntToInt = 0;
            int i12 = readUnsignedIntToInt5;
            readUnsignedIntToInt5 = readUnsignedIntToInt4;
            readUnsignedIntToInt4 = i12;
            while (readUnsignedIntToInt3 < sampleCount) {
                int i13;
                ParsableByteArray parsableByteArray7;
                while (i2 == 0) {
                    i4 = sampleCount;
                    Assertions.checkState(chunkIterator.moveNext());
                    i13 = readUnsignedIntToInt4;
                    i8 = i3;
                    long j5 = chunkIterator.offset;
                    i2 = chunkIterator.numSamples;
                    j3 = j5;
                    sampleCount = i4;
                    readUnsignedIntToInt4 = i13;
                    i3 = i8;
                }
                i4 = sampleCount;
                i13 = readUnsignedIntToInt4;
                i8 = i3;
                if (parsableByteArray5 != null) {
                    while (i11 == 0 && i9 > 0) {
                        i11 = parsableByteArray5.readUnsignedIntToInt();
                        i6 = parsableByteArray5.readInt();
                        i9--;
                    }
                    i11--;
                }
                sampleCount = i6;
                obj[readUnsignedIntToInt3] = j3;
                obj2[readUnsignedIntToInt3] = stszSampleSizeBox.readNextSampleSize();
                if (obj2[readUnsignedIntToInt3] > readUnsignedIntToInt) {
                    readUnsignedIntToInt = obj2[readUnsignedIntToInt3];
                }
                jArr[readUnsignedIntToInt3] = j4 + ((long) sampleCount);
                obj3[readUnsignedIntToInt3] = parsableByteArray4 == null ? 1 : 0;
                if (readUnsignedIntToInt3 == i) {
                    obj3[readUnsignedIntToInt3] = 1;
                    i10--;
                    if (i10 > 0) {
                        i = parsableByteArray4.readUnsignedIntToInt() - 1;
                    }
                }
                readUnsignedIntToInt4 = i10;
                int i14 = i;
                int i15 = sampleCount;
                i10 = i13;
                long j6 = j4 + ((long) i10);
                readUnsignedIntToInt5--;
                if (readUnsignedIntToInt5 != 0 || i8 <= 0) {
                    parsableByteArray7 = parsableByteArray6;
                    i3 = i8;
                } else {
                    parsableByteArray7 = parsableByteArray6;
                    i3 = i8 - 1;
                    readUnsignedIntToInt5 = parsableByteArray7.readUnsignedIntToInt();
                    i10 = parsableByteArray7.readInt();
                }
                ParsableByteArray parsableByteArray8 = parsableByteArray7;
                i2--;
                readUnsignedIntToInt3++;
                j3 += (long) obj2[readUnsignedIntToInt3];
                sampleCount = i4;
                j4 = j6;
                i6 = i15;
                i = i14;
                parsableByteArray6 = parsableByteArray8;
                i12 = readUnsignedIntToInt4;
                readUnsignedIntToInt4 = i10;
                i10 = i12;
            }
            i4 = sampleCount;
            i8 = i3;
            Assertions.checkArgument(i11 == 0);
            while (i9 > 0) {
                Assertions.checkArgument(parsableByteArray5.readUnsignedIntToInt() == 0);
                parsableByteArray5.readInt();
                i9--;
            }
            if (i10 == 0 && readUnsignedIntToInt5 == 0 && i2 == 0) {
                if (i8 == 0) {
                    track2 = track;
                    j = j4;
                    i5 = readUnsignedIntToInt;
                }
            }
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Inconsistent stbl box for track ");
            readUnsignedIntToInt4 = i10;
            track2 = track;
            stringBuilder.append(track2.id);
            stringBuilder.append(": remainingSynchronizationSamples ");
            stringBuilder.append(readUnsignedIntToInt4);
            stringBuilder.append(", remainingSamplesAtTimestampDelta ");
            stringBuilder.append(readUnsignedIntToInt5);
            stringBuilder.append(", remainingSamplesInChunk ");
            stringBuilder.append(i2);
            stringBuilder.append(", remainingTimestampDeltaChanges ");
            stringBuilder.append(i8);
            Log.w(str, stringBuilder.toString());
            j = j4;
            i5 = readUnsignedIntToInt;
        } else {
            i4 = sampleCount;
            long[] jArr2 = new long[chunkIterator.length];
            int[] iArr = new int[chunkIterator.length];
            while (chunkIterator.moveNext()) {
                jArr2[chunkIterator.index] = chunkIterator.offset;
                iArr[chunkIterator.index] = chunkIterator.numSamples;
            }
            Results rechunk = FixedSampleSizeRechunker.rechunk(stszSampleSizeBox.readNextSampleSize(), jArr2, iArr, (long) readUnsignedIntToInt5);
            obj = rechunk.offsets;
            obj2 = rechunk.sizes;
            i = rechunk.maximumSize;
            jArr = rechunk.timestamps;
            obj3 = rechunk.flags;
            i5 = i;
            j = 0;
        }
        if (track2.editListDurations != null) {
            GaplessInfoHolder gaplessInfoHolder3 = gaplessInfoHolder;
            if (!gaplessInfoHolder.hasGaplessInfo()) {
                long j7;
                long scaleLargeTimestamp;
                Object obj7;
                Object obj8;
                Object obj9;
                long[] jArr3;
                Object obj10;
                if (track2.editListDurations.length == 1 && track2.type == 1 && jArr.length >= 2) {
                    j7 = track2.editListMediaTimes[0];
                    scaleLargeTimestamp = j7 + Util.scaleLargeTimestamp(track2.editListDurations[0], track2.timescale, track2.movieTimescale);
                    if (jArr[0] <= j7 && j7 < jArr[1] && jArr[jArr.length - 1] < scaleLargeTimestamp && scaleLargeTimestamp <= j) {
                        long j8 = j - scaleLargeTimestamp;
                        j = Util.scaleLargeTimestamp(j7 - jArr[0], (long) track2.format.sampleRate, track2.timescale);
                        long scaleLargeTimestamp2 = Util.scaleLargeTimestamp(j8, (long) track2.format.sampleRate, track2.timescale);
                        if (!(j == 0 && scaleLargeTimestamp2 == 0) && j <= 2147483647L && scaleLargeTimestamp2 <= 2147483647L) {
                            gaplessInfoHolder3.encoderDelay = (int) j;
                            gaplessInfoHolder3.encoderPadding = (int) scaleLargeTimestamp2;
                            Util.scaleLargeTimestampsInPlace(jArr, C0542C.MICROS_PER_SECOND, track2.timescale);
                            return new TrackSampleTable(obj, obj2, i5, jArr, obj3);
                        }
                    }
                }
                if (track2.editListDurations.length == 1) {
                    int i16 = 0;
                    if (track2.editListDurations[0] == 0) {
                        int i17 = 0;
                        while (i17 < jArr.length) {
                            jArr[i17] = Util.scaleLargeTimestamp(jArr[i17] - track2.editListMediaTimes[i16], C0542C.MICROS_PER_SECOND, track2.timescale);
                            i17++;
                            i16 = 0;
                        }
                        return new TrackSampleTable(obj, obj2, i5, jArr, obj3);
                    }
                }
                boolean z2 = track2.type == 1;
                i = 0;
                sampleCount = 0;
                int i18 = 0;
                readUnsignedIntToInt3 = 0;
                while (true) {
                    scaleLargeTimestamp = -1;
                    if (i >= track2.editListDurations.length) {
                        break;
                    }
                    j7 = track2.editListMediaTimes[i];
                    if (j7 != -1) {
                        obj7 = obj2;
                        j5 = Util.scaleLargeTimestamp(track2.editListDurations[i], track2.timescale, track2.movieTimescale);
                        int binarySearchCeil = Util.binarySearchCeil(jArr, j7, true, true);
                        obj8 = obj;
                        obj9 = obj3;
                        i7 = Util.binarySearchCeil(jArr, j7 + j5, z2, false);
                        i18 += i7 - binarySearchCeil;
                        sampleCount |= readUnsignedIntToInt3 != binarySearchCeil ? 1 : 0;
                        readUnsignedIntToInt3 = i7;
                    } else {
                        obj8 = obj;
                        obj9 = obj3;
                        obj7 = obj2;
                    }
                    i++;
                    obj2 = obj7;
                    obj = obj8;
                    obj3 = obj9;
                }
                obj8 = obj;
                obj9 = obj3;
                obj7 = obj2;
                i = (i18 != i4 ? 1 : 0) | sampleCount;
                obj = i != 0 ? new long[i18] : obj8;
                obj2 = i != 0 ? new int[i18] : obj7;
                sampleCount = i != 0 ? 0 : i5;
                obj3 = i != 0 ? new int[i18] : obj9;
                long[] jArr4 = new long[i18];
                i2 = sampleCount;
                sampleCount = 0;
                readUnsignedIntToInt3 = 0;
                while (sampleCount < track2.editListDurations.length) {
                    int i19;
                    boolean z3;
                    Object obj11;
                    j7 = track2.editListMediaTimes[sampleCount];
                    long j9 = track2.editListDurations[sampleCount];
                    if (j7 != scaleLargeTimestamp) {
                        i19 = sampleCount;
                        jArr3 = jArr4;
                        j5 = j7 + Util.scaleLargeTimestamp(j9, track2.timescale, track2.movieTimescale);
                        i18 = Util.binarySearchCeil(jArr, j7, true, true);
                        readUnsignedIntToInt4 = Util.binarySearchCeil(jArr, j5, z2, false);
                        if (i != 0) {
                            i3 = readUnsignedIntToInt4 - i18;
                            obj4 = obj8;
                            System.arraycopy(obj4, i18, obj, readUnsignedIntToInt3, i3);
                            obj5 = obj7;
                            System.arraycopy(obj5, i18, obj2, readUnsignedIntToInt3, i3);
                            z3 = z2;
                            obj6 = obj9;
                            System.arraycopy(obj6, i18, obj3, readUnsignedIntToInt3, i3);
                        } else {
                            z3 = z2;
                            obj5 = obj7;
                            obj4 = obj8;
                            obj6 = obj9;
                        }
                        i3 = i2;
                        while (i18 < readUnsignedIntToInt4) {
                            obj10 = obj;
                            obj11 = obj3;
                            long j10 = j7;
                            jArr3[readUnsignedIntToInt3] = Util.scaleLargeTimestamp(j2, C0542C.MICROS_PER_SECOND, track2.movieTimescale) + Util.scaleLargeTimestamp(jArr[i18] - j7, C0542C.MICROS_PER_SECOND, track2.timescale);
                            if (i != 0 && obj2[readUnsignedIntToInt3] > i3) {
                                i3 = obj5[i18];
                            }
                            readUnsignedIntToInt3++;
                            i18++;
                            obj = obj10;
                            obj3 = obj11;
                            j7 = j10;
                        }
                        obj10 = obj;
                        obj11 = obj3;
                        i2 = i3;
                    } else {
                        z3 = z2;
                        i19 = sampleCount;
                        jArr3 = jArr4;
                        obj10 = obj;
                        obj11 = obj3;
                        obj5 = obj7;
                        obj4 = obj8;
                        obj6 = obj9;
                    }
                    obj9 = obj6;
                    obj7 = obj5;
                    j2 += j9;
                    sampleCount = i19 + 1;
                    obj8 = obj4;
                    jArr4 = jArr3;
                    z2 = z3;
                    obj = obj10;
                    obj3 = obj11;
                    scaleLargeTimestamp = -1;
                }
                jArr3 = jArr4;
                obj10 = obj;
                obj5 = obj7;
                obj4 = obj8;
                obj6 = obj9;
                i18 = 0;
                for (i = 0; i < obj3.length && i18 == 0; i++) {
                    i18 |= (obj3[i] & 1) != 0 ? 1 : 0;
                }
                if (i18 != 0) {
                    return new TrackSampleTable(obj10, obj2, i2, jArr3, obj3);
                }
                Log.w(TAG, "Ignoring edit list: Edited sample sequence does not contain a sync sample.");
                Util.scaleLargeTimestampsInPlace(jArr, C0542C.MICROS_PER_SECOND, track2.timescale);
                return new TrackSampleTable(obj4, obj5, i5, jArr, obj6);
            }
        }
        obj4 = obj;
        obj6 = obj3;
        obj5 = obj2;
        Util.scaleLargeTimestampsInPlace(jArr, C0542C.MICROS_PER_SECOND, track2.timescale);
        return new TrackSampleTable(obj4, obj5, i5, jArr, obj6);
    }

    public static Metadata parseUdta(LeafAtom leafAtom, boolean z) {
        if (z) {
            return null;
        }
        leafAtom = leafAtom.data;
        leafAtom.setPosition(8);
        while (leafAtom.bytesLeft() >= 8) {
            int position = leafAtom.getPosition();
            int readInt = leafAtom.readInt();
            if (leafAtom.readInt() == Atom.TYPE_meta) {
                leafAtom.setPosition(position);
                return parseMetaAtom(leafAtom, position + readInt);
            }
            leafAtom.skipBytes(readInt - 8);
        }
        return null;
    }

    private static Metadata parseMetaAtom(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.skipBytes(12);
        while (parsableByteArray.getPosition() < i) {
            int position = parsableByteArray.getPosition();
            int readInt = parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_ilst) {
                parsableByteArray.setPosition(position);
                return parseIlst(parsableByteArray, position + readInt);
            }
            parsableByteArray.skipBytes(readInt - 8);
        }
        return null;
    }

    private static Metadata parseIlst(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.skipBytes(8);
        List arrayList = new ArrayList();
        while (parsableByteArray.getPosition() < i) {
            Entry parseIlstElement = MetadataUtil.parseIlstElement(parsableByteArray);
            if (parseIlstElement != null) {
                arrayList.add(parseIlstElement);
            }
        }
        return arrayList.isEmpty() != null ? null : new Metadata(arrayList);
    }

    private static long parseMvhd(ParsableByteArray parsableByteArray) {
        int i = 8;
        parsableByteArray.setPosition(8);
        if (Atom.parseFullAtomVersion(parsableByteArray.readInt()) != 0) {
            i = 16;
        }
        parsableByteArray.skipBytes(i);
        return parsableByteArray.readUnsignedInt();
    }

    private static TkhdData parseTkhd(ParsableByteArray parsableByteArray) {
        int i = 8;
        parsableByteArray.setPosition(8);
        int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(parseFullAtomVersion == 0 ? 8 : 16);
        int readInt = parsableByteArray.readInt();
        parsableByteArray.skipBytes(4);
        int position = parsableByteArray.getPosition();
        if (parseFullAtomVersion == 0) {
            i = 4;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            if (parsableByteArray.data[position + i3] != (byte) -1) {
                position = 0;
                break;
            }
        }
        position = 1;
        long j = C0542C.TIME_UNSET;
        if (position != 0) {
            parsableByteArray.skipBytes(i);
        } else {
            long readUnsignedInt = parseFullAtomVersion == 0 ? parsableByteArray.readUnsignedInt() : parsableByteArray.readUnsignedLongToLong();
            if (readUnsignedInt != 0) {
                j = readUnsignedInt;
            }
        }
        parsableByteArray.skipBytes(16);
        i = parsableByteArray.readInt();
        parseFullAtomVersion = parsableByteArray.readInt();
        parsableByteArray.skipBytes(4);
        int readInt2 = parsableByteArray.readInt();
        parsableByteArray = parsableByteArray.readInt();
        if (i == 0 && parseFullAtomVersion == C0542C.DEFAULT_BUFFER_SEGMENT_SIZE && readInt2 == -65536 && parsableByteArray == null) {
            i2 = 90;
        } else if (i == 0 && parseFullAtomVersion == -65536 && readInt2 == C0542C.DEFAULT_BUFFER_SEGMENT_SIZE && parsableByteArray == null) {
            i2 = 270;
        } else if (i == -65536 && parseFullAtomVersion == 0 && readInt2 == 0 && parsableByteArray == -65536) {
            i2 = 180;
        }
        return new TkhdData(readInt, j, i2);
    }

    private static int parseHdlr(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(16);
        parsableByteArray = parsableByteArray.readInt();
        if (parsableByteArray == TYPE_soun) {
            return 1;
        }
        if (parsableByteArray == TYPE_vide) {
            return 2;
        }
        if (!(parsableByteArray == TYPE_text || parsableByteArray == TYPE_sbtl || parsableByteArray == TYPE_subt)) {
            if (parsableByteArray != TYPE_clcp) {
                return parsableByteArray == TYPE_meta ? 4 : -1;
            }
        }
        return 3;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray parsableByteArray) {
        int i = 8;
        parsableByteArray.setPosition(8);
        int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(parseFullAtomVersion == 0 ? 8 : 16);
        long readUnsignedInt = parsableByteArray.readUnsignedInt();
        if (parseFullAtomVersion == 0) {
            i = 4;
        }
        parsableByteArray.skipBytes(i);
        parsableByteArray = parsableByteArray.readUnsignedShort();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append((char) (((parsableByteArray >> 10) & 31) + 96));
        stringBuilder.append((char) (((parsableByteArray >> 5) & 31) + 96));
        stringBuilder.append((char) ((parsableByteArray & 31) + 96));
        return Pair.create(Long.valueOf(readUnsignedInt), stringBuilder.toString());
    }

    private static StsdData parseStsd(ParsableByteArray parsableByteArray, int i, int i2, String str, DrmInitData drmInitData, boolean z) throws ParserException {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        parsableByteArray2.setPosition(12);
        int readInt = parsableByteArray.readInt();
        StsdData stsdData = new StsdData(readInt);
        for (int i3 = 0; i3 < readInt; i3++) {
            int position = parsableByteArray.getPosition();
            int readInt2 = parsableByteArray.readInt();
            Assertions.checkArgument(readInt2 > 0, "childAtomSize should be positive");
            int readInt3 = parsableByteArray.readInt();
            if (!(readInt3 == Atom.TYPE_avc1 || readInt3 == Atom.TYPE_avc3 || readInt3 == Atom.TYPE_encv || readInt3 == Atom.TYPE_mp4v || readInt3 == Atom.TYPE_hvc1 || readInt3 == Atom.TYPE_hev1 || readInt3 == Atom.TYPE_s263 || readInt3 == Atom.TYPE_vp08)) {
                if (readInt3 != Atom.TYPE_vp09) {
                    if (!(readInt3 == Atom.TYPE_mp4a || readInt3 == Atom.TYPE_enca || readInt3 == Atom.TYPE_ac_3 || readInt3 == Atom.TYPE_ec_3 || readInt3 == Atom.TYPE_dtsc || readInt3 == Atom.TYPE_dtse || readInt3 == Atom.TYPE_dtsh || readInt3 == Atom.TYPE_dtsl || readInt3 == Atom.TYPE_samr || readInt3 == Atom.TYPE_sawb || readInt3 == Atom.TYPE_lpcm || readInt3 == Atom.TYPE_sowt || readInt3 == Atom.TYPE__mp3)) {
                        if (readInt3 != Atom.TYPE_alac) {
                            if (!(readInt3 == Atom.TYPE_TTML || readInt3 == Atom.TYPE_tx3g || readInt3 == Atom.TYPE_wvtt || readInt3 == Atom.TYPE_stpp)) {
                                if (readInt3 != Atom.TYPE_c608) {
                                    if (readInt3 == Atom.TYPE_camm) {
                                        stsdData.format = Format.createSampleFormat(Integer.toString(i), MimeTypes.APPLICATION_CAMERA_MOTION, null, -1, null);
                                    }
                                    parsableByteArray2.setPosition(position + readInt2);
                                }
                            }
                            parseTextSampleEntry(parsableByteArray2, readInt3, position, readInt2, i, str, stsdData);
                            parsableByteArray2.setPosition(position + readInt2);
                        }
                    }
                    parseAudioSampleEntry(parsableByteArray2, readInt3, position, readInt2, i, str, z, drmInitData, stsdData, i3);
                    parsableByteArray2.setPosition(position + readInt2);
                }
            }
            parseVideoSampleEntry(parsableByteArray2, readInt3, position, readInt2, i, i2, drmInitData, stsdData, i3);
            parsableByteArray2.setPosition(position + readInt2);
        }
        return stsdData;
    }

    private static void parseTextSampleEntry(ParsableByteArray parsableByteArray, int i, int i2, int i3, int i4, String str, StsdData stsdData) throws ParserException {
        String str2;
        String str3;
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        int i5 = i;
        StsdData stsdData2 = stsdData;
        parsableByteArray2.setPosition((i2 + 8) + 8);
        List list = null;
        long j = Long.MAX_VALUE;
        if (i5 == Atom.TYPE_TTML) {
            str2 = MimeTypes.APPLICATION_TTML;
        } else if (i5 == Atom.TYPE_tx3g) {
            String str4 = MimeTypes.APPLICATION_TX3G;
            int i6 = (i3 - 8) - 8;
            Object obj = new byte[i6];
            parsableByteArray2.readBytes(obj, 0, i6);
            list = Collections.singletonList(obj);
            str3 = str4;
            stsdData2.format = Format.createTextSampleFormat(Integer.toString(i4), str3, null, -1, 0, str, -1, null, j, list);
        } else if (i5 == Atom.TYPE_wvtt) {
            str2 = MimeTypes.APPLICATION_MP4VTT;
        } else if (i5 == Atom.TYPE_stpp) {
            str2 = MimeTypes.APPLICATION_TTML;
            j = 0;
        } else if (i5 == Atom.TYPE_c608) {
            str2 = MimeTypes.APPLICATION_MP4CEA608;
            stsdData2.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        str3 = str2;
        stsdData2.format = Format.createTextSampleFormat(Integer.toString(i4), str3, null, -1, 0, str, -1, null, j, list);
    }

    private static void parseVideoSampleEntry(ParsableByteArray parsableByteArray, int i, int i2, int i3, int i4, int i5, DrmInitData drmInitData, StsdData stsdData, int i6) throws ParserException {
        Pair parseSampleEntryEncryptionData;
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        int i7 = i2;
        int i8 = i3;
        DrmInitData drmInitData2 = drmInitData;
        StsdData stsdData2 = stsdData;
        parsableByteArray2.setPosition((i7 + 8) + 8);
        parsableByteArray2.skipBytes(16);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
        parsableByteArray2.skipBytes(50);
        int position = parsableByteArray.getPosition();
        String str = null;
        int i9 = i;
        if (i9 == Atom.TYPE_encv) {
            parseSampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray2, i7, i8);
            if (parseSampleEntryEncryptionData != null) {
                i9 = ((Integer) parseSampleEntryEncryptionData.first).intValue();
                if (drmInitData2 == null) {
                    drmInitData2 = null;
                } else {
                    drmInitData2 = drmInitData2.copyWithSchemeType(((TrackEncryptionBox) parseSampleEntryEncryptionData.second).schemeType);
                }
                stsdData2.trackEncryptionBoxes[i6] = (TrackEncryptionBox) parseSampleEntryEncryptionData.second;
            }
            parsableByteArray2.setPosition(position);
        }
        DrmInitData drmInitData3 = drmInitData2;
        int i10 = -1;
        List list = null;
        byte[] bArr = list;
        float f = 1.0f;
        Object obj = null;
        while (position - i7 < i8) {
            parsableByteArray2.setPosition(position);
            int position2 = parsableByteArray.getPosition();
            int readInt = parsableByteArray.readInt();
            if (readInt != 0 || parsableByteArray.getPosition() - i7 != i8) {
                Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
                int readInt2 = parsableByteArray.readInt();
                if (readInt2 == Atom.TYPE_avcC) {
                    Assertions.checkState(str == null);
                    str = "video/avc";
                    parsableByteArray2.setPosition(position2 + 8);
                    AvcConfig parse = AvcConfig.parse(parsableByteArray);
                    list = parse.initializationData;
                    stsdData2.nalUnitLengthFieldLength = parse.nalUnitLengthFieldLength;
                    if (obj == null) {
                        f = parse.pixelWidthAspectRatio;
                    }
                } else if (readInt2 == Atom.TYPE_hvcC) {
                    Assertions.checkState(str == null);
                    str = MimeTypes.VIDEO_H265;
                    parsableByteArray2.setPosition(position2 + 8);
                    HevcConfig parse2 = HevcConfig.parse(parsableByteArray);
                    list = parse2.initializationData;
                    stsdData2.nalUnitLengthFieldLength = parse2.nalUnitLengthFieldLength;
                } else if (readInt2 == Atom.TYPE_vpcC) {
                    Assertions.checkState(str == null);
                    str = i9 == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
                } else if (readInt2 == Atom.TYPE_d263) {
                    Assertions.checkState(str == null);
                    str = MimeTypes.VIDEO_H263;
                } else if (readInt2 == Atom.TYPE_esds) {
                    Assertions.checkState(str == null);
                    parseSampleEntryEncryptionData = parseEsdsFromParent(parsableByteArray2, position2);
                    str = (String) parseSampleEntryEncryptionData.first;
                    list = Collections.singletonList(parseSampleEntryEncryptionData.second);
                } else if (readInt2 == Atom.TYPE_pasp) {
                    f = parsePaspFromParent(parsableByteArray2, position2);
                    obj = 1;
                } else if (readInt2 == Atom.TYPE_sv3d) {
                    bArr = parseProjFromParent(parsableByteArray2, position2, readInt);
                } else if (readInt2 == Atom.TYPE_st3d) {
                    readInt2 = parsableByteArray.readUnsignedByte();
                    parsableByteArray2.skipBytes(3);
                    if (readInt2 == 0) {
                        switch (parsableByteArray.readUnsignedByte()) {
                            case 0:
                                i10 = 0;
                                break;
                            case 1:
                                i10 = 1;
                                break;
                            case 2:
                                i10 = 2;
                                break;
                            case 3:
                                i10 = 3;
                                break;
                            default:
                                break;
                        }
                    }
                }
                position += readInt;
            } else if (str == null) {
                stsdData2.format = Format.createVideoSampleFormat(Integer.toString(i4), str, null, -1, -1, readUnsignedShort, readUnsignedShort2, -1.0f, list, i5, f, bArr, i10, null, drmInitData3);
            }
        }
        if (str == null) {
            stsdData2.format = Format.createVideoSampleFormat(Integer.toString(i4), str, null, -1, -1, readUnsignedShort, readUnsignedShort2, -1.0f, list, i5, f, bArr, i10, null, drmInitData3);
        }
    }

    private static Pair<long[], long[]> parseEdts(ContainerAtom containerAtom) {
        if (containerAtom != null) {
            containerAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_elst);
            if (containerAtom != null) {
                containerAtom = containerAtom.data;
                containerAtom.setPosition(8);
                int parseFullAtomVersion = Atom.parseFullAtomVersion(containerAtom.readInt());
                int readUnsignedIntToInt = containerAtom.readUnsignedIntToInt();
                Object obj = new long[readUnsignedIntToInt];
                Object obj2 = new long[readUnsignedIntToInt];
                for (int i = 0; i < readUnsignedIntToInt; i++) {
                    obj[i] = parseFullAtomVersion == 1 ? containerAtom.readUnsignedLongToLong() : containerAtom.readUnsignedInt();
                    obj2[i] = parseFullAtomVersion == 1 ? containerAtom.readLong() : (long) containerAtom.readInt();
                    if (containerAtom.readShort() != (short) 1) {
                        throw new IllegalArgumentException("Unsupported media rate.");
                    }
                    containerAtom.skipBytes(2);
                }
                return Pair.create(obj, obj2);
            }
        }
        return Pair.create(null, null);
    }

    private static float parsePaspFromParent(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.setPosition(i + 8);
        return ((float) parsableByteArray.readUnsignedIntToInt()) / ((float) parsableByteArray.readUnsignedIntToInt());
    }

    private static void parseAudioSampleEntry(ParsableByteArray parsableByteArray, int i, int i2, int i3, int i4, String str, boolean z, DrmInitData drmInitData, StsdData stsdData, int i5) throws ParserException {
        int readUnsignedShort;
        int round;
        int position;
        int i6;
        Pair parseSampleEntryEncryptionData;
        DrmInitData drmInitData2;
        String str2;
        String str3;
        int i7;
        int i8;
        Object obj;
        int readInt;
        int i9;
        String str4;
        int i10;
        DrmInitData drmInitData3;
        Object obj2;
        int i11;
        int i12;
        StsdData stsdData2;
        Object obj3;
        boolean z2;
        String str5;
        int i13;
        Object obj4;
        List list;
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        int i14 = i2;
        int i15 = i3;
        String str6 = str;
        DrmInitData drmInitData4 = drmInitData;
        StsdData stsdData3 = stsdData;
        int i16 = 8;
        parsableByteArray2.setPosition((i14 + 8) + 8);
        boolean z3 = false;
        if (z) {
            readUnsignedShort = parsableByteArray.readUnsignedShort();
            parsableByteArray2.skipBytes(6);
        } else {
            parsableByteArray2.skipBytes(8);
            readUnsignedShort = 0;
        }
        int i17 = 2;
        if (readUnsignedShort != 0) {
            if (readUnsignedShort != 1) {
                if (readUnsignedShort == 2) {
                    parsableByteArray2.skipBytes(16);
                    round = (int) Math.round(parsableByteArray.readDouble());
                    readUnsignedShort = parsableByteArray.readUnsignedIntToInt();
                    parsableByteArray2.skipBytes(20);
                    position = parsableByteArray.getPosition();
                    i6 = i;
                    if (i6 == Atom.TYPE_enca) {
                        parseSampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray2, i14, i15);
                        if (parseSampleEntryEncryptionData != null) {
                            i6 = ((Integer) parseSampleEntryEncryptionData.first).intValue();
                            if (drmInitData4 != null) {
                                drmInitData4 = null;
                            } else {
                                drmInitData4 = drmInitData4.copyWithSchemeType(((TrackEncryptionBox) parseSampleEntryEncryptionData.second).schemeType);
                            }
                            stsdData3.trackEncryptionBoxes[i5] = (TrackEncryptionBox) parseSampleEntryEncryptionData.second;
                        }
                        parsableByteArray2.setPosition(position);
                    }
                    drmInitData2 = drmInitData4;
                    if (i6 == Atom.TYPE_ac_3) {
                        str2 = MimeTypes.AUDIO_AC3;
                    } else if (i6 == Atom.TYPE_ec_3) {
                        str2 = MimeTypes.AUDIO_E_AC3;
                    } else if (i6 != Atom.TYPE_dtsc) {
                        str2 = MimeTypes.AUDIO_DTS;
                    } else {
                        if (i6 != Atom.TYPE_dtsh) {
                            if (i6 == Atom.TYPE_dtsl) {
                                if (i6 == Atom.TYPE_dtse) {
                                    str2 = MimeTypes.AUDIO_DTS_EXPRESS;
                                } else if (i6 == Atom.TYPE_samr) {
                                    str2 = MimeTypes.AUDIO_AMR_NB;
                                } else if (i6 != Atom.TYPE_sawb) {
                                    str2 = MimeTypes.AUDIO_AMR_WB;
                                } else {
                                    if (i6 != Atom.TYPE_lpcm) {
                                        if (i6 == Atom.TYPE_sowt) {
                                            str2 = i6 != Atom.TYPE__mp3 ? MimeTypes.AUDIO_MPEG : i6 != Atom.TYPE_alac ? MimeTypes.AUDIO_ALAC : null;
                                        }
                                    }
                                    str2 = MimeTypes.AUDIO_RAW;
                                }
                            }
                        }
                        str2 = MimeTypes.AUDIO_DTS_HD;
                    }
                    str3 = str2;
                    i7 = round;
                    i8 = readUnsignedShort;
                    i6 = position;
                    obj = null;
                    while (i6 - i14 < i15) {
                        parsableByteArray2.setPosition(i6);
                        readUnsignedShort = parsableByteArray.readInt();
                        Assertions.checkArgument(readUnsignedShort <= 0 ? true : z3, "childAtomSize should be positive");
                        readInt = parsableByteArray.readInt();
                        if (readInt != Atom.TYPE_esds) {
                            if (z || readInt != Atom.TYPE_wave) {
                                if (readInt == Atom.TYPE_dac3) {
                                    parsableByteArray2.setPosition(i16 + i6);
                                    stsdData3.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray2, Integer.toString(i4), str6, drmInitData2);
                                } else if (readInt != Atom.TYPE_dec3) {
                                    parsableByteArray2.setPosition(i16 + i6);
                                    stsdData3.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray2, Integer.toString(i4), str6, drmInitData2);
                                } else {
                                    if (readInt != Atom.TYPE_ddts) {
                                        i9 = readUnsignedShort;
                                        str4 = str3;
                                        i10 = i6;
                                        drmInitData3 = drmInitData2;
                                        obj2 = obj;
                                        i11 = 2;
                                        i12 = i16;
                                        stsdData2 = stsdData3;
                                        stsdData2.format = Format.createAudioSampleFormat(Integer.toString(i4), str3, null, -1, -1, i8, i7, null, drmInitData3, 0, str6);
                                    } else {
                                        i9 = readUnsignedShort;
                                        obj2 = obj;
                                        str4 = str3;
                                        i10 = i6;
                                        drmInitData3 = drmInitData2;
                                        i12 = i16;
                                        stsdData2 = stsdData3;
                                        i11 = 2;
                                        if (readInt == Atom.TYPE_alac) {
                                            readUnsignedShort = i9;
                                            obj3 = new byte[readUnsignedShort];
                                            position = i10;
                                            parsableByteArray2.setPosition(position);
                                            z2 = false;
                                            parsableByteArray2.readBytes(obj3, 0, readUnsignedShort);
                                            i6 = position + readUnsignedShort;
                                            stsdData3 = stsdData2;
                                            obj = obj3;
                                            z3 = z2;
                                            drmInitData2 = drmInitData3;
                                            i17 = i11;
                                            i16 = i12;
                                            str3 = str4;
                                            i15 = i3;
                                        }
                                    }
                                    readUnsignedShort = i9;
                                    position = i10;
                                    z2 = false;
                                    obj3 = obj2;
                                    i6 = position + readUnsignedShort;
                                    stsdData3 = stsdData2;
                                    obj = obj3;
                                    z3 = z2;
                                    drmInitData2 = drmInitData3;
                                    i17 = i11;
                                    i16 = i12;
                                    str3 = str4;
                                    i15 = i3;
                                }
                                obj2 = obj;
                                str4 = str3;
                                position = i6;
                                drmInitData3 = drmInitData2;
                                z2 = z3;
                                i12 = i16;
                                stsdData2 = stsdData3;
                                i11 = 2;
                                obj3 = obj2;
                                i6 = position + readUnsignedShort;
                                stsdData3 = stsdData2;
                                obj = obj3;
                                z3 = z2;
                                drmInitData2 = drmInitData3;
                                i17 = i11;
                                i16 = i12;
                                str3 = str4;
                                i15 = i3;
                            }
                        }
                        obj2 = obj;
                        str4 = str3;
                        position = i6;
                        drmInitData3 = drmInitData2;
                        z2 = z3;
                        i12 = i16;
                        stsdData2 = stsdData3;
                        i11 = 2;
                        if (readInt != Atom.TYPE_esds) {
                            i6 = position;
                        } else {
                            i6 = findEsdsPosition(parsableByteArray2, position, readUnsignedShort);
                        }
                        if (i6 == -1) {
                            Pair parseEsdsFromParent = parseEsdsFromParent(parsableByteArray2, i6);
                            str5 = (String) parseEsdsFromParent.first;
                            obj3 = (byte[]) parseEsdsFromParent.second;
                            if (MimeTypes.AUDIO_AAC.equals(str5)) {
                                Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(obj3);
                                i7 = ((Integer) parseAacAudioSpecificConfig.first).intValue();
                                i8 = ((Integer) parseAacAudioSpecificConfig.second).intValue();
                            }
                        } else {
                            str5 = str4;
                            obj3 = obj2;
                        }
                        str4 = str5;
                        i6 = position + readUnsignedShort;
                        stsdData3 = stsdData2;
                        obj = obj3;
                        z3 = z2;
                        drmInitData2 = drmInitData3;
                        i17 = i11;
                        i16 = i12;
                        str3 = str4;
                        i15 = i3;
                    }
                    obj2 = obj;
                    str4 = str3;
                    drmInitData3 = drmInitData2;
                    i11 = i17;
                    stsdData2 = stsdData3;
                    if (stsdData2.format == null) {
                        str2 = str4;
                        if (str2 != null) {
                            i13 = MimeTypes.AUDIO_RAW.equals(str2) ? i11 : -1;
                            String num = Integer.toString(i4);
                            obj4 = obj2;
                            if (obj4 != null) {
                                list = null;
                            } else {
                                list = Collections.singletonList(obj4);
                            }
                            stsdData2.format = Format.createAudioSampleFormat(num, str2, null, -1, -1, i8, i7, i13, list, drmInitData3, 0, str6);
                        }
                    }
                }
                return;
            }
        }
        i13 = parsableByteArray.readUnsignedShort();
        parsableByteArray2.skipBytes(6);
        round = parsableByteArray.readUnsignedFixedPoint1616();
        if (readUnsignedShort == 1) {
            parsableByteArray2.skipBytes(16);
        }
        readUnsignedShort = i13;
        position = parsableByteArray.getPosition();
        i6 = i;
        if (i6 == Atom.TYPE_enca) {
            parseSampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray2, i14, i15);
            if (parseSampleEntryEncryptionData != null) {
                i6 = ((Integer) parseSampleEntryEncryptionData.first).intValue();
                if (drmInitData4 != null) {
                    drmInitData4 = drmInitData4.copyWithSchemeType(((TrackEncryptionBox) parseSampleEntryEncryptionData.second).schemeType);
                } else {
                    drmInitData4 = null;
                }
                stsdData3.trackEncryptionBoxes[i5] = (TrackEncryptionBox) parseSampleEntryEncryptionData.second;
            }
            parsableByteArray2.setPosition(position);
        }
        drmInitData2 = drmInitData4;
        if (i6 == Atom.TYPE_ac_3) {
            str2 = MimeTypes.AUDIO_AC3;
        } else if (i6 == Atom.TYPE_ec_3) {
            str2 = MimeTypes.AUDIO_E_AC3;
        } else if (i6 != Atom.TYPE_dtsc) {
            if (i6 != Atom.TYPE_dtsh) {
                if (i6 == Atom.TYPE_dtsl) {
                    if (i6 == Atom.TYPE_dtse) {
                        str2 = MimeTypes.AUDIO_DTS_EXPRESS;
                    } else if (i6 == Atom.TYPE_samr) {
                        str2 = MimeTypes.AUDIO_AMR_NB;
                    } else if (i6 != Atom.TYPE_sawb) {
                        if (i6 != Atom.TYPE_lpcm) {
                            if (i6 == Atom.TYPE_sowt) {
                                if (i6 != Atom.TYPE__mp3) {
                                    if (i6 != Atom.TYPE_alac) {
                                    }
                                }
                            }
                        }
                        str2 = MimeTypes.AUDIO_RAW;
                    } else {
                        str2 = MimeTypes.AUDIO_AMR_WB;
                    }
                }
            }
            str2 = MimeTypes.AUDIO_DTS_HD;
        } else {
            str2 = MimeTypes.AUDIO_DTS;
        }
        str3 = str2;
        i7 = round;
        i8 = readUnsignedShort;
        i6 = position;
        obj = null;
        while (i6 - i14 < i15) {
            parsableByteArray2.setPosition(i6);
            readUnsignedShort = parsableByteArray.readInt();
            if (readUnsignedShort <= 0) {
            }
            Assertions.checkArgument(readUnsignedShort <= 0 ? true : z3, "childAtomSize should be positive");
            readInt = parsableByteArray.readInt();
            if (readInt != Atom.TYPE_esds) {
                if (z) {
                }
                if (readInt == Atom.TYPE_dac3) {
                    parsableByteArray2.setPosition(i16 + i6);
                    stsdData3.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray2, Integer.toString(i4), str6, drmInitData2);
                } else if (readInt != Atom.TYPE_dec3) {
                    if (readInt != Atom.TYPE_ddts) {
                        i9 = readUnsignedShort;
                        obj2 = obj;
                        str4 = str3;
                        i10 = i6;
                        drmInitData3 = drmInitData2;
                        i12 = i16;
                        stsdData2 = stsdData3;
                        i11 = 2;
                        if (readInt == Atom.TYPE_alac) {
                            readUnsignedShort = i9;
                            obj3 = new byte[readUnsignedShort];
                            position = i10;
                            parsableByteArray2.setPosition(position);
                            z2 = false;
                            parsableByteArray2.readBytes(obj3, 0, readUnsignedShort);
                            i6 = position + readUnsignedShort;
                            stsdData3 = stsdData2;
                            obj = obj3;
                            z3 = z2;
                            drmInitData2 = drmInitData3;
                            i17 = i11;
                            i16 = i12;
                            str3 = str4;
                            i15 = i3;
                        }
                    } else {
                        i9 = readUnsignedShort;
                        str4 = str3;
                        i10 = i6;
                        drmInitData3 = drmInitData2;
                        obj2 = obj;
                        i11 = 2;
                        i12 = i16;
                        stsdData2 = stsdData3;
                        stsdData2.format = Format.createAudioSampleFormat(Integer.toString(i4), str3, null, -1, -1, i8, i7, null, drmInitData3, 0, str6);
                    }
                    readUnsignedShort = i9;
                    position = i10;
                    z2 = false;
                    obj3 = obj2;
                    i6 = position + readUnsignedShort;
                    stsdData3 = stsdData2;
                    obj = obj3;
                    z3 = z2;
                    drmInitData2 = drmInitData3;
                    i17 = i11;
                    i16 = i12;
                    str3 = str4;
                    i15 = i3;
                } else {
                    parsableByteArray2.setPosition(i16 + i6);
                    stsdData3.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray2, Integer.toString(i4), str6, drmInitData2);
                }
                obj2 = obj;
                str4 = str3;
                position = i6;
                drmInitData3 = drmInitData2;
                z2 = z3;
                i12 = i16;
                stsdData2 = stsdData3;
                i11 = 2;
                obj3 = obj2;
                i6 = position + readUnsignedShort;
                stsdData3 = stsdData2;
                obj = obj3;
                z3 = z2;
                drmInitData2 = drmInitData3;
                i17 = i11;
                i16 = i12;
                str3 = str4;
                i15 = i3;
            }
            obj2 = obj;
            str4 = str3;
            position = i6;
            drmInitData3 = drmInitData2;
            z2 = z3;
            i12 = i16;
            stsdData2 = stsdData3;
            i11 = 2;
            if (readInt != Atom.TYPE_esds) {
                i6 = findEsdsPosition(parsableByteArray2, position, readUnsignedShort);
            } else {
                i6 = position;
            }
            if (i6 == -1) {
                str5 = str4;
                obj3 = obj2;
            } else {
                Pair parseEsdsFromParent2 = parseEsdsFromParent(parsableByteArray2, i6);
                str5 = (String) parseEsdsFromParent2.first;
                obj3 = (byte[]) parseEsdsFromParent2.second;
                if (MimeTypes.AUDIO_AAC.equals(str5)) {
                    Pair parseAacAudioSpecificConfig2 = CodecSpecificDataUtil.parseAacAudioSpecificConfig(obj3);
                    i7 = ((Integer) parseAacAudioSpecificConfig2.first).intValue();
                    i8 = ((Integer) parseAacAudioSpecificConfig2.second).intValue();
                }
            }
            str4 = str5;
            i6 = position + readUnsignedShort;
            stsdData3 = stsdData2;
            obj = obj3;
            z3 = z2;
            drmInitData2 = drmInitData3;
            i17 = i11;
            i16 = i12;
            str3 = str4;
            i15 = i3;
        }
        obj2 = obj;
        str4 = str3;
        drmInitData3 = drmInitData2;
        i11 = i17;
        stsdData2 = stsdData3;
        if (stsdData2.format == null) {
            str2 = str4;
            if (str2 != null) {
                if (MimeTypes.AUDIO_RAW.equals(str2)) {
                }
                String num2 = Integer.toString(i4);
                obj4 = obj2;
                if (obj4 != null) {
                    list = Collections.singletonList(obj4);
                } else {
                    list = null;
                }
                stsdData2.format = Format.createAudioSampleFormat(num2, str2, null, -1, -1, i8, i7, i13, list, drmInitData3, 0, str6);
            }
        }
    }

    private static int findEsdsPosition(ParsableByteArray parsableByteArray, int i, int i2) {
        int position = parsableByteArray.getPosition();
        while (position - i < i2) {
            parsableByteArray.setPosition(position);
            int readInt = parsableByteArray.readInt();
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            if (parsableByteArray.readInt() == Atom.TYPE_esds) {
                return position;
            }
            position += readInt;
        }
        return -1;
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.setPosition((i + 8) + 4);
        parsableByteArray.skipBytes(1);
        parseExpandableClassSize(parsableByteArray);
        parsableByteArray.skipBytes(2);
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        if ((readUnsignedByte & 128) != 0) {
            parsableByteArray.skipBytes(2);
        }
        if ((readUnsignedByte & 64) != 0) {
            parsableByteArray.skipBytes(parsableByteArray.readUnsignedShort());
        }
        if ((readUnsignedByte & 32) != 0) {
            parsableByteArray.skipBytes(2);
        }
        parsableByteArray.skipBytes(1);
        parseExpandableClassSize(parsableByteArray);
        Object obj = null;
        switch (parsableByteArray.readUnsignedByte()) {
            case 32:
                obj = MimeTypes.VIDEO_MP4V;
                break;
            case 33:
                obj = "video/avc";
                break;
            case 35:
                obj = MimeTypes.VIDEO_H265;
                break;
            case 64:
            case 102:
            case 103:
            case 104:
                obj = MimeTypes.AUDIO_AAC;
                break;
            case 96:
            case 97:
                obj = MimeTypes.VIDEO_MPEG2;
                break;
            case 107:
                return Pair.create(MimeTypes.AUDIO_MPEG, null);
            case 165:
                obj = MimeTypes.AUDIO_AC3;
                break;
            case 166:
                obj = MimeTypes.AUDIO_E_AC3;
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
        parsableByteArray.skipBytes(12);
        parsableByteArray.skipBytes(1);
        i = parseExpandableClassSize(parsableByteArray);
        Object obj2 = new byte[i];
        parsableByteArray.readBytes(obj2, 0, i);
        return Pair.create(obj, obj2);
    }

    private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray parsableByteArray, int i, int i2) {
        int position = parsableByteArray.getPosition();
        while (position - i < i2) {
            parsableByteArray.setPosition(position);
            int readInt = parsableByteArray.readInt();
            Assertions.checkArgument(readInt > 0, "childAtomSize should be positive");
            if (parsableByteArray.readInt() == Atom.TYPE_sinf) {
                Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent = parseCommonEncryptionSinfFromParent(parsableByteArray, position, readInt);
                if (parseCommonEncryptionSinfFromParent != null) {
                    return parseCommonEncryptionSinfFromParent;
                }
            }
            position += readInt;
        }
        return null;
    }

    static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray parsableByteArray, int i, int i2) {
        int i3 = i + 8;
        int i4 = -1;
        String str = null;
        Object obj = str;
        int i5 = 0;
        while (i3 - i < i2) {
            parsableByteArray.setPosition(i3);
            int readInt = parsableByteArray.readInt();
            int readInt2 = parsableByteArray.readInt();
            if (readInt2 == Atom.TYPE_frma) {
                obj = Integer.valueOf(parsableByteArray.readInt());
            } else if (readInt2 == Atom.TYPE_schm) {
                parsableByteArray.skipBytes(4);
                str = parsableByteArray.readString(4);
            } else if (readInt2 == Atom.TYPE_schi) {
                i4 = i3;
                i5 = readInt;
            }
            i3 += readInt;
        }
        if (C0542C.CENC_TYPE_cenc.equals(str) == 0 && C0542C.CENC_TYPE_cbc1.equals(str) == 0 && C0542C.CENC_TYPE_cens.equals(str) == 0) {
            if (C0542C.CENC_TYPE_cbcs.equals(str) == 0) {
                return null;
            }
        }
        i = 1;
        Assertions.checkArgument(obj != null ? 1 : 0, "frma atom is mandatory");
        Assertions.checkArgument(i4 != -1 ? 1 : 0, "schi atom is mandatory");
        parsableByteArray = parseSchiFromParent(parsableByteArray, i4, i5, str);
        if (parsableByteArray == null) {
            i = 0;
        }
        Assertions.checkArgument(i, "tenc atom is mandatory");
        return Pair.create(obj, parsableByteArray);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parsableByteArray, int i, int i2, String str) {
        TrackEncryptionBox trackEncryptionBox;
        int i3;
        int i4;
        int i5 = i + 8;
        while (true) {
            trackEncryptionBox = null;
            if (i5 - i >= i2) {
                return null;
            }
            parsableByteArray.setPosition(i5);
            int readInt = parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_tenc) {
                break;
            }
            i5 += readInt;
        }
        i = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(1);
        if (i == 0) {
            parsableByteArray.skipBytes(1);
            i3 = 0;
            i4 = i3;
        } else {
            i = parsableByteArray.readUnsignedByte();
            i4 = i & 15;
            i3 = (i & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        }
        boolean z = parsableByteArray.readUnsignedByte() == 1;
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        byte[] bArr = new byte[16];
        parsableByteArray.readBytes(bArr, 0, bArr.length);
        if (z && readUnsignedByte == 0) {
            i = parsableByteArray.readUnsignedByte();
            trackEncryptionBox = new byte[i];
            parsableByteArray.readBytes(trackEncryptionBox, 0, i);
        }
        return new TrackEncryptionBox(z, str, readUnsignedByte, bArr, i3, i4, trackEncryptionBox);
    }

    private static byte[] parseProjFromParent(ParsableByteArray parsableByteArray, int i, int i2) {
        int i3 = i + 8;
        while (i3 - i < i2) {
            parsableByteArray.setPosition(i3);
            int readInt = parsableByteArray.readInt();
            if (parsableByteArray.readInt() == Atom.TYPE_proj) {
                return Arrays.copyOfRange(parsableByteArray.data, i3, readInt + i3);
            }
            i3 += readInt;
        }
        return null;
    }

    private static int parseExpandableClassSize(ParsableByteArray parsableByteArray) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int i = readUnsignedByte & 127;
        while ((readUnsignedByte & 128) == 128) {
            readUnsignedByte = parsableByteArray.readUnsignedByte();
            i = (i << 7) | (readUnsignedByte & 127);
        }
        return i;
    }

    private AtomParsers() {
    }
}
