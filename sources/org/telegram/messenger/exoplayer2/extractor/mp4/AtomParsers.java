package org.telegram.messenger.exoplayer2.extractor.mp4;

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
            boolean z = true;
            this.stsc = stsc;
            this.chunkOffsets = chunkOffsets;
            this.chunkOffsetsAreLongs = chunkOffsetsAreLongs;
            chunkOffsets.setPosition(12);
            this.length = chunkOffsets.readUnsignedIntToInt();
            stsc.setPosition(12);
            this.remainingSamplesPerChunkChanges = stsc.readUnsignedIntToInt();
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

    public static Track parseTrak(ContainerAtom trak, LeafAtom mvhd, long duration, DrmInitData drmInitData, boolean ignoreEditLists, boolean isQuickTime) throws ParserException {
        ContainerAtom mdia = trak.getContainerAtomOfType(Atom.TYPE_mdia);
        int trackType = parseHdlr(mdia.getLeafAtomOfType(Atom.TYPE_hdlr).data);
        if (trackType == -1) {
            return null;
        }
        long durationUs;
        TkhdData tkhdData = parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        if (duration == C0542C.TIME_UNSET) {
            duration = tkhdData.duration;
        }
        long movieTimescale = parseMvhd(mvhd.data);
        if (duration == C0542C.TIME_UNSET) {
            durationUs = C0542C.TIME_UNSET;
        } else {
            durationUs = Util.scaleLargeTimestamp(duration, C0542C.MICROS_PER_SECOND, movieTimescale);
        }
        ContainerAtom stbl = mdia.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        Pair<Long, String> mdhdData = parseMdhd(mdia.getLeafAtomOfType(Atom.TYPE_mdhd).data);
        StsdData stsdData = parseStsd(stbl.getLeafAtomOfType(Atom.TYPE_stsd).data, tkhdData.id, tkhdData.rotationDegrees, (String) mdhdData.second, drmInitData, isQuickTime);
        long[] editListDurations = null;
        long[] editListMediaTimes = null;
        if (!ignoreEditLists) {
            Pair<long[], long[]> edtsData = parseEdts(trak.getContainerAtomOfType(Atom.TYPE_edts));
            editListDurations = (long[]) edtsData.first;
            editListMediaTimes = (long[]) edtsData.second;
        }
        if (stsdData.format == null) {
            return null;
        }
        return new Track(tkhdData.id, trackType, ((Long) mdhdData.first).longValue(), movieTimescale, durationUs, stsdData.format, stsdData.requiredSampleTransformation, stsdData.trackEncryptionBoxes, stsdData.nalUnitLengthFieldLength, editListDurations, editListMediaTimes);
    }

    public static org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable parseStbl(org.telegram.messenger.exoplayer2.extractor.mp4.Track r88, org.telegram.messenger.exoplayer2.extractor.mp4.Atom.ContainerAtom r89, org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder r90) throws org.telegram.messenger.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r74_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) in PHI: PHI: (r74_1 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) = (r74_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox), (r74_2 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) binds: {(r74_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox)=B:2:0x000a, (r74_2 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox)=B:10:0x003f}
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
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsz;
        r0 = r89;
        r79 = r0.getLeafAtomOfType(r4);
        if (r79 == 0) goto L_0x002c;
    L_0x000a:
        r74 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$StszSampleSizeBox;
        r0 = r74;
        r1 = r79;
        r0.<init>(r1);
    L_0x0013:
        r72 = r74.getSampleCount();
        if (r72 != 0) goto L_0x0049;
    L_0x0019:
        r4 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r12 = 0;
        r5 = new long[r12];
        r12 = 0;
        r6 = new int[r12];
        r7 = 0;
        r12 = 0;
        r8 = new long[r12];
        r12 = 0;
        r9 = new int[r12];
        r4.<init>(r5, r6, r7, r8, r9);
    L_0x002b:
        return r4;
    L_0x002c:
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stz2;
        r0 = r89;
        r81 = r0.getLeafAtomOfType(r4);
        if (r81 != 0) goto L_0x003f;
    L_0x0036:
        r4 = new org.telegram.messenger.exoplayer2.ParserException;
        r12 = "Track has no sample table size information";
        r4.<init>(r12);
        throw r4;
    L_0x003f:
        r74 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$Stz2SampleSizeBox;
        r0 = r74;
        r1 = r81;
        r0.<init>(r1);
        goto L_0x0013;
    L_0x0049:
        r20 = 0;
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stco;
        r0 = r89;
        r21 = r0.getLeafAtomOfType(r4);
        if (r21 != 0) goto L_0x005f;
    L_0x0055:
        r20 = 1;
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_co64;
        r0 = r89;
        r21 = r0.getLeafAtomOfType(r4);
    L_0x005f:
        r0 = r21;
        r0 = r0.data;
        r19 = r0;
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsc;
        r0 = r89;
        r4 = r0.getLeafAtomOfType(r4);
        r0 = r4.data;
        r76 = r0;
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stts;
        r0 = r89;
        r4 = r0.getLeafAtomOfType(r4);
        r0 = r4.data;
        r80 = r0;
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stss;
        r0 = r89;
        r78 = r0.getLeafAtomOfType(r4);
        if (r78 == 0) goto L_0x013f;
    L_0x0087:
        r0 = r78;
        r0 = r0.data;
        r77 = r0;
    L_0x008d:
        r4 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_ctts;
        r0 = r89;
        r27 = r0.getLeafAtomOfType(r4);
        if (r27 == 0) goto L_0x0143;
    L_0x0097:
        r0 = r27;
        r0 = r0.data;
        r26 = r0;
    L_0x009d:
        r18 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$ChunkIterator;
        r0 = r18;
        r1 = r76;
        r2 = r19;
        r3 = r20;
        r0.<init>(r1, r2, r3);
        r4 = 12;
        r0 = r80;
        r0.setPosition(r4);
        r4 = r80.readUnsignedIntToInt();
        r70 = r4 + -1;
        r66 = r80.readUnsignedIntToInt();
        r84 = r80.readUnsignedIntToInt();
        r67 = 0;
        r71 = 0;
        r85 = 0;
        if (r26 == 0) goto L_0x00d2;
    L_0x00c7:
        r4 = 12;
        r0 = r26;
        r0.setPosition(r4);
        r71 = r26.readUnsignedIntToInt();
    L_0x00d2:
        r55 = -1;
        r69 = 0;
        if (r77 == 0) goto L_0x00eb;
    L_0x00d8:
        r4 = 12;
        r0 = r77;
        r0.setPosition(r4);
        r69 = r77.readUnsignedIntToInt();
        if (r69 <= 0) goto L_0x0147;
    L_0x00e5:
        r4 = r77.readUnsignedIntToInt();
        r55 = r4 + -1;
    L_0x00eb:
        r4 = r74.isFixedSampleSize();
        if (r4 == 0) goto L_0x014a;
    L_0x00f1:
        r4 = "audio/raw";
        r0 = r88;
        r12 = r0.format;
        r12 = r12.sampleMimeType;
        r4 = r4.equals(r12);
        if (r4 == 0) goto L_0x014a;
    L_0x0100:
        if (r70 != 0) goto L_0x014a;
    L_0x0102:
        if (r71 != 0) goto L_0x014a;
    L_0x0104:
        if (r69 != 0) goto L_0x014a;
    L_0x0106:
        r48 = 1;
    L_0x0108:
        r7 = 0;
        r86 = 0;
        if (r48 != 0) goto L_0x0242;
    L_0x010d:
        r0 = r72;
        r5 = new long[r0];
        r0 = r72;
        r6 = new int[r0];
        r0 = r72;
        r8 = new long[r0];
        r0 = r72;
        r9 = new int[r0];
        r56 = 0;
        r68 = 0;
        r47 = 0;
    L_0x0123:
        r0 = r47;
        r1 = r72;
        if (r0 >= r1) goto L_0x01af;
    L_0x0129:
        if (r68 != 0) goto L_0x014d;
    L_0x012b:
        r4 = r18.moveNext();
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r4);
        r0 = r18;
        r0 = r0.offset;
        r56 = r0;
        r0 = r18;
        r0 = r0.numSamples;
        r68 = r0;
        goto L_0x0129;
    L_0x013f:
        r77 = 0;
        goto L_0x008d;
    L_0x0143:
        r26 = 0;
        goto L_0x009d;
    L_0x0147:
        r77 = 0;
        goto L_0x00eb;
    L_0x014a:
        r48 = 0;
        goto L_0x0108;
    L_0x014d:
        if (r26 == 0) goto L_0x0160;
    L_0x014f:
        if (r67 != 0) goto L_0x015e;
    L_0x0151:
        if (r71 <= 0) goto L_0x015e;
    L_0x0153:
        r67 = r26.readUnsignedIntToInt();
        r85 = r26.readInt();
        r71 = r71 + -1;
        goto L_0x014f;
    L_0x015e:
        r67 = r67 + -1;
    L_0x0160:
        r5[r47] = r56;
        r4 = r74.readNextSampleSize();
        r6[r47] = r4;
        r4 = r6[r47];
        if (r4 <= r7) goto L_0x016e;
    L_0x016c:
        r7 = r6[r47];
    L_0x016e:
        r0 = r85;
        r12 = (long) r0;
        r12 = r12 + r86;
        r8[r47] = r12;
        if (r77 != 0) goto L_0x01ad;
    L_0x0177:
        r4 = 1;
    L_0x0178:
        r9[r47] = r4;
        r0 = r47;
        r1 = r55;
        if (r0 != r1) goto L_0x018d;
    L_0x0180:
        r4 = 1;
        r9[r47] = r4;
        r69 = r69 + -1;
        if (r69 <= 0) goto L_0x018d;
    L_0x0187:
        r4 = r77.readUnsignedIntToInt();
        r55 = r4 + -1;
    L_0x018d:
        r0 = r84;
        r12 = (long) r0;
        r86 = r86 + r12;
        r66 = r66 + -1;
        if (r66 != 0) goto L_0x01a2;
    L_0x0196:
        if (r70 <= 0) goto L_0x01a2;
    L_0x0198:
        r66 = r80.readUnsignedIntToInt();
        r84 = r80.readInt();
        r70 = r70 + -1;
    L_0x01a2:
        r4 = r6[r47];
        r12 = (long) r4;
        r56 = r56 + r12;
        r68 = r68 + -1;
        r47 = r47 + 1;
        goto L_0x0123;
    L_0x01ad:
        r4 = 0;
        goto L_0x0178;
    L_0x01af:
        if (r67 != 0) goto L_0x01c7;
    L_0x01b1:
        r4 = 1;
    L_0x01b2:
        org.telegram.messenger.exoplayer2.util.Assertions.checkArgument(r4);
    L_0x01b5:
        if (r71 <= 0) goto L_0x01cb;
    L_0x01b7:
        r4 = r26.readUnsignedIntToInt();
        if (r4 != 0) goto L_0x01c9;
    L_0x01bd:
        r4 = 1;
    L_0x01be:
        org.telegram.messenger.exoplayer2.util.Assertions.checkArgument(r4);
        r26.readInt();
        r71 = r71 + -1;
        goto L_0x01b5;
    L_0x01c7:
        r4 = 0;
        goto L_0x01b2;
    L_0x01c9:
        r4 = 0;
        goto L_0x01be;
    L_0x01cb:
        if (r69 != 0) goto L_0x01d3;
    L_0x01cd:
        if (r66 != 0) goto L_0x01d3;
    L_0x01cf:
        if (r68 != 0) goto L_0x01d3;
    L_0x01d1:
        if (r70 == 0) goto L_0x0225;
    L_0x01d3:
        r4 = "AtomParsers";
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "Inconsistent stbl box for track ";
        r12 = r12.append(r13);
        r0 = r88;
        r13 = r0.id;
        r12 = r12.append(r13);
        r13 = ": remainingSynchronizationSamples ";
        r12 = r12.append(r13);
        r0 = r69;
        r12 = r12.append(r0);
        r13 = ", remainingSamplesAtTimestampDelta ";
        r12 = r12.append(r13);
        r0 = r66;
        r12 = r12.append(r0);
        r13 = ", remainingSamplesInChunk ";
        r12 = r12.append(r13);
        r0 = r68;
        r12 = r12.append(r0);
        r13 = ", remainingTimestampDeltaChanges ";
        r12 = r12.append(r13);
        r0 = r70;
        r12 = r12.append(r0);
        r12 = r12.toString();
        android.util.Log.w(r4, r12);
    L_0x0225:
        r0 = r88;
        r4 = r0.editListDurations;
        if (r4 == 0) goto L_0x0231;
    L_0x022b:
        r4 = r90.hasGaplessInfo();
        if (r4 == 0) goto L_0x0293;
    L_0x0231:
        r12 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r14 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r8, r12, r14);
        r4 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r4.<init>(r5, r6, r7, r8, r9);
        goto L_0x002b;
    L_0x0242:
        r0 = r18;
        r4 = r0.length;
        r0 = new long[r4];
        r22 = r0;
        r0 = r18;
        r4 = r0.length;
        r0 = new int[r4];
        r23 = r0;
    L_0x0252:
        r4 = r18.moveNext();
        if (r4 == 0) goto L_0x026d;
    L_0x0258:
        r0 = r18;
        r4 = r0.index;
        r0 = r18;
        r12 = r0.offset;
        r22[r4] = r12;
        r0 = r18;
        r4 = r0.index;
        r0 = r18;
        r12 = r0.numSamples;
        r23[r4] = r12;
        goto L_0x0252;
    L_0x026d:
        r43 = r74.readNextSampleSize();
        r0 = r84;
        r12 = (long) r0;
        r0 = r43;
        r1 = r22;
        r2 = r23;
        r59 = org.telegram.messenger.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.rechunk(r0, r1, r2, r12);
        r0 = r59;
        r5 = r0.offsets;
        r0 = r59;
        r6 = r0.sizes;
        r0 = r59;
        r7 = r0.maximumSize;
        r0 = r59;
        r8 = r0.timestamps;
        r0 = r59;
        r9 = r0.flags;
        goto L_0x0225;
    L_0x0293:
        r0 = r88;
        r4 = r0.editListDurations;
        r4 = r4.length;
        r12 = 1;
        if (r4 != r12) goto L_0x033f;
    L_0x029b:
        r0 = r88;
        r4 = r0.type;
        r12 = 1;
        if (r4 != r12) goto L_0x033f;
    L_0x02a2:
        r4 = r8.length;
        r12 = 2;
        if (r4 < r12) goto L_0x033f;
    L_0x02a6:
        r0 = r88;
        r4 = r0.editListMediaTimes;
        r12 = 0;
        r30 = r4[r12];
        r0 = r88;
        r4 = r0.editListDurations;
        r12 = 0;
        r10 = r4[r12];
        r0 = r88;
        r12 = r0.timescale;
        r0 = r88;
        r14 = r0.movieTimescale;
        r12 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r28 = r30 + r12;
        r50 = r86;
        r4 = 0;
        r12 = r8[r4];
        r4 = (r12 > r30 ? 1 : (r12 == r30 ? 0 : -1));
        if (r4 > 0) goto L_0x033f;
    L_0x02cb:
        r4 = 1;
        r12 = r8[r4];
        r4 = (r30 > r12 ? 1 : (r30 == r12 ? 0 : -1));
        if (r4 >= 0) goto L_0x033f;
    L_0x02d2:
        r4 = r8.length;
        r4 = r4 + -1;
        r12 = r8[r4];
        r4 = (r12 > r28 ? 1 : (r12 == r28 ? 0 : -1));
        if (r4 >= 0) goto L_0x033f;
    L_0x02db:
        r4 = (r28 > r50 ? 1 : (r28 == r50 ? 0 : -1));
        if (r4 > 0) goto L_0x033f;
    L_0x02df:
        r60 = r50 - r28;
        r4 = 0;
        r12 = r8[r4];
        r10 = r30 - r12;
        r0 = r88;
        r4 = r0.format;
        r4 = r4.sampleRate;
        r12 = (long) r4;
        r0 = r88;
        r14 = r0.timescale;
        r38 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r0 = r88;
        r4 = r0.format;
        r4 = r4.sampleRate;
        r12 = (long) r4;
        r0 = r88;
        r14 = r0.timescale;
        r10 = r60;
        r40 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r12 = 0;
        r4 = (r38 > r12 ? 1 : (r38 == r12 ? 0 : -1));
        if (r4 != 0) goto L_0x0312;
    L_0x030c:
        r12 = 0;
        r4 = (r40 > r12 ? 1 : (r40 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x033f;
    L_0x0312:
        r12 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = (r38 > r12 ? 1 : (r38 == r12 ? 0 : -1));
        if (r4 > 0) goto L_0x033f;
    L_0x0319:
        r12 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = (r40 > r12 ? 1 : (r40 == r12 ? 0 : -1));
        if (r4 > 0) goto L_0x033f;
    L_0x0320:
        r0 = r38;
        r4 = (int) r0;
        r0 = r90;
        r0.encoderDelay = r4;
        r0 = r40;
        r4 = (int) r0;
        r0 = r90;
        r0.encoderPadding = r4;
        r12 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r14 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r8, r12, r14);
        r4 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r4.<init>(r5, r6, r7, r8, r9);
        goto L_0x002b;
    L_0x033f:
        r0 = r88;
        r4 = r0.editListDurations;
        r4 = r4.length;
        r12 = 1;
        if (r4 != r12) goto L_0x037d;
    L_0x0347:
        r0 = r88;
        r4 = r0.editListDurations;
        r12 = 0;
        r12 = r4[r12];
        r14 = 0;
        r4 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x037d;
    L_0x0354:
        r47 = 0;
    L_0x0356:
        r4 = r8.length;
        r0 = r47;
        if (r0 >= r4) goto L_0x0376;
    L_0x035b:
        r12 = r8[r47];
        r0 = r88;
        r4 = r0.editListMediaTimes;
        r14 = 0;
        r14 = r4[r14];
        r10 = r12 - r14;
        r12 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r14 = r0.timescale;
        r12 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r8[r47] = r12;
        r47 = r47 + 1;
        goto L_0x0356;
    L_0x0376:
        r4 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r4.<init>(r5, r6, r7, r8, r9);
        goto L_0x002b;
    L_0x037d:
        r0 = r88;
        r4 = r0.type;
        r12 = 1;
        if (r4 != r12) goto L_0x03d8;
    L_0x0384:
        r58 = 1;
    L_0x0386:
        r35 = 0;
        r54 = 0;
        r24 = 0;
        r47 = 0;
    L_0x038e:
        r0 = r88;
        r4 = r0.editListDurations;
        r4 = r4.length;
        r0 = r47;
        if (r0 >= r4) goto L_0x03dd;
    L_0x0397:
        r0 = r88;
        r4 = r0.editListMediaTimes;
        r52 = r4[r47];
        r12 = -1;
        r4 = (r52 > r12 ? 1 : (r52 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x03d5;
    L_0x03a3:
        r0 = r88;
        r4 = r0.editListDurations;
        r10 = r4[r47];
        r0 = r88;
        r12 = r0.timescale;
        r0 = r88;
        r14 = r0.movieTimescale;
        r10 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r4 = 1;
        r12 = 1;
        r0 = r52;
        r75 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r8, r0, r4, r12);
        r12 = r52 + r10;
        r4 = 0;
        r0 = r58;
        r42 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r8, r12, r0, r4);
        r4 = r42 - r75;
        r35 = r35 + r4;
        r0 = r54;
        r1 = r75;
        if (r0 == r1) goto L_0x03db;
    L_0x03d0:
        r4 = 1;
    L_0x03d1:
        r24 = r24 | r4;
        r54 = r42;
    L_0x03d5:
        r47 = r47 + 1;
        goto L_0x038e;
    L_0x03d8:
        r58 = 0;
        goto L_0x0386;
    L_0x03db:
        r4 = 0;
        goto L_0x03d1;
    L_0x03dd:
        r0 = r35;
        r1 = r72;
        if (r0 == r1) goto L_0x04a8;
    L_0x03e3:
        r4 = 1;
    L_0x03e4:
        r24 = r24 | r4;
        if (r24 == 0) goto L_0x04ab;
    L_0x03e8:
        r0 = r35;
        r0 = new long[r0];
        r34 = r0;
    L_0x03ee:
        if (r24 == 0) goto L_0x04af;
    L_0x03f0:
        r0 = r35;
        r0 = new int[r0];
        r36 = r0;
    L_0x03f6:
        if (r24 == 0) goto L_0x04b3;
    L_0x03f8:
        r33 = 0;
    L_0x03fa:
        if (r24 == 0) goto L_0x04b7;
    L_0x03fc:
        r0 = r35;
        r0 = new int[r0];
        r32 = r0;
    L_0x0402:
        r0 = r35;
        r0 = new long[r0];
        r37 = r0;
        r62 = 0;
        r73 = 0;
        r47 = 0;
    L_0x040e:
        r0 = r88;
        r4 = r0.editListDurations;
        r4 = r4.length;
        r0 = r47;
        if (r0 >= r4) goto L_0x04c1;
    L_0x0417:
        r0 = r88;
        r4 = r0.editListMediaTimes;
        r52 = r4[r47];
        r0 = r88;
        r4 = r0.editListDurations;
        r10 = r4[r47];
        r12 = -1;
        r4 = (r52 > r12 ? 1 : (r52 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x04bb;
    L_0x0429:
        r0 = r88;
        r12 = r0.timescale;
        r0 = r88;
        r14 = r0.movieTimescale;
        r12 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r10, r12, r14);
        r44 = r52 + r12;
        r4 = 1;
        r12 = 1;
        r0 = r52;
        r75 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r8, r0, r4, r12);
        r4 = 0;
        r0 = r44;
        r2 = r58;
        r42 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r8, r0, r2, r4);
        if (r24 == 0) goto L_0x046d;
    L_0x044a:
        r25 = r42 - r75;
        r0 = r75;
        r1 = r34;
        r2 = r73;
        r3 = r25;
        java.lang.System.arraycopy(r5, r0, r1, r2, r3);
        r0 = r75;
        r1 = r36;
        r2 = r73;
        r3 = r25;
        java.lang.System.arraycopy(r6, r0, r1, r2, r3);
        r0 = r75;
        r1 = r32;
        r2 = r73;
        r3 = r25;
        java.lang.System.arraycopy(r9, r0, r1, r2, r3);
    L_0x046d:
        r49 = r75;
    L_0x046f:
        r0 = r49;
        r1 = r42;
        if (r0 >= r1) goto L_0x04bb;
    L_0x0475:
        r14 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r0 = r0.movieTimescale;
        r16 = r0;
        r12 = r62;
        r64 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r12, r14, r16);
        r12 = r8[r49];
        r12 = r12 - r52;
        r14 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r0 = r0.timescale;
        r16 = r0;
        r82 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r12, r14, r16);
        r12 = r64 + r82;
        r37[r73] = r12;
        if (r24 == 0) goto L_0x04a3;
    L_0x049b:
        r4 = r36[r73];
        r0 = r33;
        if (r4 <= r0) goto L_0x04a3;
    L_0x04a1:
        r33 = r6[r49];
    L_0x04a3:
        r73 = r73 + 1;
        r49 = r49 + 1;
        goto L_0x046f;
    L_0x04a8:
        r4 = 0;
        goto L_0x03e4;
    L_0x04ab:
        r34 = r5;
        goto L_0x03ee;
    L_0x04af:
        r36 = r6;
        goto L_0x03f6;
    L_0x04b3:
        r33 = r7;
        goto L_0x03fa;
    L_0x04b7:
        r32 = r9;
        goto L_0x0402;
    L_0x04bb:
        r62 = r62 + r10;
        r47 = r47 + 1;
        goto L_0x040e;
    L_0x04c1:
        r46 = 0;
        r47 = 0;
    L_0x04c5:
        r0 = r32;
        r4 = r0.length;
        r0 = r47;
        if (r0 >= r4) goto L_0x04dc;
    L_0x04cc:
        if (r46 != 0) goto L_0x04dc;
    L_0x04ce:
        r4 = r32[r47];
        r4 = r4 & 1;
        if (r4 == 0) goto L_0x04da;
    L_0x04d4:
        r4 = 1;
    L_0x04d5:
        r46 = r46 | r4;
        r47 = r47 + 1;
        goto L_0x04c5;
    L_0x04da:
        r4 = 0;
        goto L_0x04d5;
    L_0x04dc:
        if (r46 != 0) goto L_0x04f8;
    L_0x04de:
        r4 = "AtomParsers";
        r12 = "Ignoring edit list: Edited sample sequence does not contain a sync sample.";
        android.util.Log.w(r4, r12);
        r12 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r88;
        r14 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r8, r12, r14);
        r4 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r4.<init>(r5, r6, r7, r8, r9);
        goto L_0x002b;
    L_0x04f8:
        r12 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r13 = r34;
        r14 = r36;
        r15 = r33;
        r16 = r37;
        r17 = r32;
        r12.<init>(r13, r14, r15, r16, r17);
        r4 = r12;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers.parseStbl(org.telegram.messenger.exoplayer2.extractor.mp4.Track, org.telegram.messenger.exoplayer2.extractor.mp4.Atom$ContainerAtom, org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder):org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable");
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
        int rotationDegrees;
        tkhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(tkhd.readInt());
        tkhd.skipBytes(version == 0 ? 8 : 16);
        int trackId = tkhd.readInt();
        tkhd.skipBytes(4);
        boolean durationUnknown = true;
        int durationPosition = tkhd.getPosition();
        int durationByteCount = version == 0 ? 4 : 8;
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
        } else if (a00 == (-65536) && a01 == 0 && a10 == 0 && a11 == (-65536)) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 0;
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
        if (trackType == TYPE_text || trackType == TYPE_sbtl || trackType == TYPE_subt || trackType == TYPE_clcp) {
            return 3;
        }
        if (trackType == TYPE_meta) {
            return 4;
        }
        return -1;
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
        int languageCode = mdhd.readUnsignedShort();
        return Pair.create(Long.valueOf(timescale), TtmlNode.ANONYMOUS_REGION_ID + ((char) (((languageCode >> 10) & 31) + 96)) + ((char) (((languageCode >> 5) & 31) + 96)) + ((char) ((languageCode & 31) + 96)));
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, int rotationDegrees, String language, DrmInitData drmInitData, boolean isQuickTime) throws ParserException {
        stsd.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = stsd.readInt();
            if (childAtomType == Atom.TYPE_avc1 || childAtomType == Atom.TYPE_avc3 || childAtomType == Atom.TYPE_encv || childAtomType == Atom.TYPE_mp4v || childAtomType == Atom.TYPE_hvc1 || childAtomType == Atom.TYPE_hev1 || childAtomType == Atom.TYPE_s263 || childAtomType == Atom.TYPE_vp08 || childAtomType == Atom.TYPE_vp09) {
                parseVideoSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, rotationDegrees, drmInitData, out, i);
            } else if (childAtomType == Atom.TYPE_mp4a || childAtomType == Atom.TYPE_enca || childAtomType == Atom.TYPE_ac_3 || childAtomType == Atom.TYPE_ec_3 || childAtomType == Atom.TYPE_dtsc || childAtomType == Atom.TYPE_dtse || childAtomType == Atom.TYPE_dtsh || childAtomType == Atom.TYPE_dtsl || childAtomType == Atom.TYPE_samr || childAtomType == Atom.TYPE_sawb || childAtomType == Atom.TYPE_lpcm || childAtomType == Atom.TYPE_sowt || childAtomType == Atom.TYPE__mp3 || childAtomType == Atom.TYPE_alac) {
                parseAudioSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, language, isQuickTime, drmInitData, out, i);
            } else if (childAtomType == Atom.TYPE_TTML || childAtomType == Atom.TYPE_tx3g || childAtomType == Atom.TYPE_wvtt || childAtomType == Atom.TYPE_stpp || childAtomType == Atom.TYPE_c608) {
                parseTextSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, language, out);
            } else if (childAtomType == Atom.TYPE_camm) {
                out.format = Format.createSampleFormat(Integer.toString(trackId), MimeTypes.APPLICATION_CAMERA_MOTION, null, -1, null);
            }
            stsd.setPosition(childStartPosition + childAtomSize);
        }
        return out;
    }

    private static void parseTextSampleEntry(ParsableByteArray parent, int atomType, int position, int atomSize, int trackId, String language, StsdData out) throws ParserException {
        String mimeType;
        parent.setPosition((position + 8) + 8);
        List<byte[]> initializationData = null;
        long subsampleOffsetUs = Long.MAX_VALUE;
        if (atomType == Atom.TYPE_TTML) {
            mimeType = MimeTypes.APPLICATION_TTML;
        } else if (atomType == Atom.TYPE_tx3g) {
            mimeType = MimeTypes.APPLICATION_TX3G;
            int sampleDescriptionLength = (atomSize - 8) - 8;
            byte[] sampleDescriptionData = new byte[sampleDescriptionLength];
            parent.readBytes(sampleDescriptionData, 0, sampleDescriptionLength);
            initializationData = Collections.singletonList(sampleDescriptionData);
        } else if (atomType == Atom.TYPE_wvtt) {
            mimeType = MimeTypes.APPLICATION_MP4VTT;
        } else if (atomType == Atom.TYPE_stpp) {
            mimeType = MimeTypes.APPLICATION_TTML;
            subsampleOffsetUs = 0;
        } else if (atomType == Atom.TYPE_c608) {
            mimeType = MimeTypes.APPLICATION_MP4CEA608;
            out.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        out.format = Format.createTextSampleFormat(Integer.toString(trackId), mimeType, null, -1, 0, language, -1, null, subsampleOffsetUs, initializationData);
    }

    private static void parseVideoSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, int rotationDegrees, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        parent.setPosition((position + 8) + 8);
        parent.skipBytes(16);
        int width = parent.readUnsignedShort();
        int height = parent.readUnsignedShort();
        boolean pixelWidthHeightRatioFromPasp = false;
        float pixelWidthHeightRatio = 1.0f;
        parent.skipBytes(50);
        int childPosition = parent.getPosition();
        if (atomType == Atom.TYPE_encv) {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parent, position, size);
            if (sampleEntryEncryptionData != null) {
                atomType = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData == null) {
                    drmInitData = null;
                } else {
                    drmInitData = drmInitData.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                }
                out.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parent.setPosition(childPosition);
        }
        List<byte[]> initializationData = null;
        String mimeType = null;
        byte[] projectionData = null;
        int stereoMode = -1;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childStartPosition = parent.getPosition();
            int childAtomSize = parent.readInt();
            if (childAtomSize != 0 || parent.getPosition() - position != size) {
                Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
                int childAtomType = parent.readInt();
                if (childAtomType == Atom.TYPE_avcC) {
                    Assertions.checkState(mimeType == null);
                    mimeType = "video/avc";
                    parent.setPosition(childStartPosition + 8);
                    AvcConfig avcConfig = AvcConfig.parse(parent);
                    initializationData = avcConfig.initializationData;
                    out.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
                    if (!pixelWidthHeightRatioFromPasp) {
                        pixelWidthHeightRatio = avcConfig.pixelWidthAspectRatio;
                    }
                } else if (childAtomType == Atom.TYPE_hvcC) {
                    Assertions.checkState(mimeType == null);
                    mimeType = MimeTypes.VIDEO_H265;
                    parent.setPosition(childStartPosition + 8);
                    HevcConfig hevcConfig = HevcConfig.parse(parent);
                    initializationData = hevcConfig.initializationData;
                    out.nalUnitLengthFieldLength = hevcConfig.nalUnitLengthFieldLength;
                } else if (childAtomType == Atom.TYPE_vpcC) {
                    Assertions.checkState(mimeType == null);
                    mimeType = atomType == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
                } else if (childAtomType == Atom.TYPE_d263) {
                    Assertions.checkState(mimeType == null);
                    mimeType = MimeTypes.VIDEO_H263;
                } else if (childAtomType == Atom.TYPE_esds) {
                    Assertions.checkState(mimeType == null);
                    Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parent, childStartPosition);
                    mimeType = mimeTypeAndInitializationData.first;
                    initializationData = Collections.singletonList(mimeTypeAndInitializationData.second);
                } else if (childAtomType == Atom.TYPE_pasp) {
                    pixelWidthHeightRatio = parsePaspFromParent(parent, childStartPosition);
                    pixelWidthHeightRatioFromPasp = true;
                } else if (childAtomType == Atom.TYPE_sv3d) {
                    projectionData = parseProjFromParent(parent, childStartPosition, childAtomSize);
                } else if (childAtomType == Atom.TYPE_st3d) {
                    int version = parent.readUnsignedByte();
                    parent.skipBytes(3);
                    if (version == 0) {
                        switch (parent.readUnsignedByte()) {
                            case 0:
                                stereoMode = 0;
                                break;
                            case 1:
                                stereoMode = 1;
                                break;
                            case 2:
                                stereoMode = 2;
                                break;
                            case 3:
                                stereoMode = 3;
                                break;
                            default:
                                break;
                        }
                    }
                }
                childPosition += childAtomSize;
            } else if (mimeType == null) {
                out.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData);
            }
        }
        if (mimeType == null) {
            out.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData);
        }
    }

    private static Pair<long[], long[]> parseEdts(ContainerAtom edtsAtom) {
        if (edtsAtom != null) {
            LeafAtom elst = edtsAtom.getLeafAtomOfType(Atom.TYPE_elst);
            if (elst != null) {
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
        int sampleRate;
        parent.setPosition((position + 8) + 8);
        int quickTimeSoundDescriptionVersion = 0;
        if (isQuickTime) {
            quickTimeSoundDescriptionVersion = parent.readUnsignedShort();
            parent.skipBytes(6);
        } else {
            parent.skipBytes(8);
        }
        if (quickTimeSoundDescriptionVersion == 0 || quickTimeSoundDescriptionVersion == 1) {
            channelCount = parent.readUnsignedShort();
            parent.skipBytes(6);
            sampleRate = parent.readUnsignedFixedPoint1616();
            if (quickTimeSoundDescriptionVersion == 1) {
                parent.skipBytes(16);
            }
        } else if (quickTimeSoundDescriptionVersion == 2) {
            parent.skipBytes(16);
            sampleRate = (int) Math.round(parent.readDouble());
            channelCount = parent.readUnsignedIntToInt();
            parent.skipBytes(20);
        } else {
            return;
        }
        int childPosition = parent.getPosition();
        if (atomType == Atom.TYPE_enca) {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parent, position, size);
            if (sampleEntryEncryptionData != null) {
                atomType = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData == null) {
                    drmInitData = null;
                } else {
                    drmInitData = drmInitData.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                }
                out.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parent.setPosition(childPosition);
        }
        String mimeType = null;
        if (atomType == Atom.TYPE_ac_3) {
            mimeType = MimeTypes.AUDIO_AC3;
        } else if (atomType == Atom.TYPE_ec_3) {
            mimeType = MimeTypes.AUDIO_E_AC3;
        } else if (atomType == Atom.TYPE_dtsc) {
            mimeType = MimeTypes.AUDIO_DTS;
        } else if (atomType == Atom.TYPE_dtsh || atomType == Atom.TYPE_dtsl) {
            mimeType = MimeTypes.AUDIO_DTS_HD;
        } else if (atomType == Atom.TYPE_dtse) {
            mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
        } else if (atomType == Atom.TYPE_samr) {
            mimeType = MimeTypes.AUDIO_AMR_NB;
        } else if (atomType == Atom.TYPE_sawb) {
            mimeType = MimeTypes.AUDIO_AMR_WB;
        } else if (atomType == Atom.TYPE_lpcm || atomType == Atom.TYPE_sowt) {
            mimeType = MimeTypes.AUDIO_RAW;
        } else if (atomType == Atom.TYPE__mp3) {
            mimeType = MimeTypes.AUDIO_MPEG;
        } else if (atomType == Atom.TYPE_alac) {
            mimeType = MimeTypes.AUDIO_ALAC;
        }
        byte[] initializationData = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_esds || (isQuickTime && childAtomType == Atom.TYPE_wave)) {
                int esdsAtomPosition;
                if (childAtomType == Atom.TYPE_esds) {
                    esdsAtomPosition = childPosition;
                } else {
                    esdsAtomPosition = findEsdsPosition(parent, childPosition, childAtomSize);
                }
                if (esdsAtomPosition != -1) {
                    Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parent, esdsAtomPosition);
                    mimeType = mimeTypeAndInitializationData.first;
                    initializationData = (byte[]) mimeTypeAndInitializationData.second;
                    if (MimeTypes.AUDIO_AAC.equals(mimeType)) {
                        Pair<Integer, Integer> audioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData);
                        sampleRate = ((Integer) audioSpecificConfig.first).intValue();
                        channelCount = ((Integer) audioSpecificConfig.second).intValue();
                    }
                }
            } else if (childAtomType == Atom.TYPE_dac3) {
                parent.setPosition(childPosition + 8);
                out.format = Ac3Util.parseAc3AnnexFFormat(parent, Integer.toString(trackId), language, drmInitData);
            } else if (childAtomType == Atom.TYPE_dec3) {
                parent.setPosition(childPosition + 8);
                out.format = Ac3Util.parseEAc3AnnexFFormat(parent, Integer.toString(trackId), language, drmInitData);
            } else if (childAtomType == Atom.TYPE_ddts) {
                out.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
            } else if (childAtomType == Atom.TYPE_alac) {
                initializationData = new byte[childAtomSize];
                parent.setPosition(childPosition);
                parent.readBytes(initializationData, 0, childAtomSize);
            }
            childPosition += childAtomSize;
        }
        if (out.format == null && mimeType != null) {
            List list;
            int pcmEncoding = MimeTypes.AUDIO_RAW.equals(mimeType) ? 2 : -1;
            String num = Integer.toString(trackId);
            if (initializationData == null) {
                list = null;
            } else {
                list = Collections.singletonList(initializationData);
            }
            out.format = Format.createAudioSampleFormat(num, mimeType, null, -1, -1, channelCount, sampleRate, pcmEncoding, list, drmInitData, 0, language);
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
        String mimeType;
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
                mimeType = null;
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
        boolean z = true;
        int childPosition = position + 8;
        int schemeInformationBoxPosition = -1;
        int schemeInformationBoxSize = 0;
        String schemeType = null;
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
        if (!C0542C.CENC_TYPE_cenc.equals(schemeType) && !C0542C.CENC_TYPE_cbc1.equals(schemeType) && !C0542C.CENC_TYPE_cens.equals(schemeType) && !C0542C.CENC_TYPE_cbcs.equals(schemeType)) {
            return null;
        }
        boolean z2;
        Assertions.checkArgument(dataFormat != null, "frma atom is mandatory");
        if (schemeInformationBoxPosition != -1) {
            z2 = true;
        } else {
            z2 = false;
        }
        Assertions.checkArgument(z2, "schi atom is mandatory");
        TrackEncryptionBox encryptionBox = parseSchiFromParent(parent, schemeInformationBoxPosition, schemeInformationBoxSize, schemeType);
        if (encryptionBox == null) {
            z = false;
        }
        Assertions.checkArgument(z, "tenc atom is mandatory");
        return Pair.create(dataFormat, encryptionBox);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size, String schemeType) {
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_tenc) {
                int version = Atom.parseFullAtomVersion(parent.readInt());
                parent.skipBytes(1);
                int defaultCryptByteBlock = 0;
                int defaultSkipByteBlock = 0;
                if (version == 0) {
                    parent.skipBytes(1);
                } else {
                    int patternByte = parent.readUnsignedByte();
                    defaultCryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                    defaultSkipByteBlock = patternByte & 15;
                }
                boolean defaultIsProtected = parent.readUnsignedByte() == 1;
                int defaultPerSampleIvSize = parent.readUnsignedByte();
                byte[] defaultKeyId = new byte[16];
                parent.readBytes(defaultKeyId, 0, defaultKeyId.length);
                byte[] constantIv = null;
                if (defaultIsProtected && defaultPerSampleIvSize == 0) {
                    int constantIvSize = parent.readUnsignedByte();
                    constantIv = new byte[constantIvSize];
                    parent.readBytes(constantIv, 0, constantIvSize);
                }
                return new TrackEncryptionBox(defaultIsProtected, schemeType, defaultPerSampleIvSize, defaultKeyId, defaultCryptByteBlock, defaultSkipByteBlock, constantIv);
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
