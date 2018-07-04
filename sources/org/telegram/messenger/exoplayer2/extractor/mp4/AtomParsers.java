package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Pair;
import com.coremedia.iso.boxes.MetaBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0616C;
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
    private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 3;
    private static final String TAG = "AtomParsers";
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
        if (duration == C0616C.TIME_UNSET) {
            duration = tkhdData.duration;
        }
        long movieTimescale = parseMvhd(mvhd.data);
        if (duration == C0616C.TIME_UNSET) {
            durationUs = C0616C.TIME_UNSET;
        } else {
            durationUs = Util.scaleLargeTimestamp(duration, 1000000, movieTimescale);
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

    public static org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable parseStbl(org.telegram.messenger.exoplayer2.extractor.mp4.Track r102, org.telegram.messenger.exoplayer2.extractor.mp4.Atom.ContainerAtom r103, org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder r104) throws org.telegram.messenger.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r86_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) in PHI: PHI: (r86_1 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) = (r86_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox), (r86_2 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox) binds: {(r86_0 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox)=B:2:0x000a, (r86_2 'sampleSizeBox' org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$SampleSizeBox)=B:10:0x0045}
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
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsz;
        r0 = r103;
        r92 = r0.getLeafAtomOfType(r8);
        if (r92 == 0) goto L_0x0032;
    L_0x000a:
        r86 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$StszSampleSizeBox;
        r0 = r86;
        r1 = r92;
        r0.<init>(r1);
    L_0x0013:
        r84 = r86.getSampleCount();
        if (r84 != 0) goto L_0x004f;
    L_0x0019:
        r6 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r8 = 0;
        r7 = new long[r8];
        r8 = 0;
        r8 = new int[r8];
        r9 = 0;
        r10 = 0;
        r10 = new long[r10];
        r11 = 0;
        r11 = new int[r11];
        r12 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r6.<init>(r7, r8, r9, r10, r11, r12);
        r8 = r6;
    L_0x0031:
        return r8;
    L_0x0032:
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stz2;
        r0 = r103;
        r94 = r0.getLeafAtomOfType(r8);
        if (r94 != 0) goto L_0x0045;
    L_0x003c:
        r8 = new org.telegram.messenger.exoplayer2.ParserException;
        r9 = "Track has no sample table size information";
        r8.<init>(r9);
        throw r8;
    L_0x0045:
        r86 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$Stz2SampleSizeBox;
        r0 = r86;
        r1 = r94;
        r0.<init>(r1);
        goto L_0x0013;
    L_0x004f:
        r36 = 0;
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stco;
        r0 = r103;
        r37 = r0.getLeafAtomOfType(r8);
        if (r37 != 0) goto L_0x0065;
    L_0x005b:
        r36 = 1;
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_co64;
        r0 = r103;
        r37 = r0.getLeafAtomOfType(r8);
    L_0x0065:
        r0 = r37;
        r0 = r0.data;
        r35 = r0;
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stsc;
        r0 = r103;
        r8 = r0.getLeafAtomOfType(r8);
        r0 = r8.data;
        r89 = r0;
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stts;
        r0 = r103;
        r8 = r0.getLeafAtomOfType(r8);
        r0 = r8.data;
        r93 = r0;
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_stss;
        r0 = r103;
        r91 = r0.getLeafAtomOfType(r8);
        if (r91 == 0) goto L_0x014a;
    L_0x008d:
        r0 = r91;
        r0 = r0.data;
        r90 = r0;
    L_0x0093:
        r8 = org.telegram.messenger.exoplayer2.extractor.mp4.Atom.TYPE_ctts;
        r0 = r103;
        r43 = r0.getLeafAtomOfType(r8);
        if (r43 == 0) goto L_0x014e;
    L_0x009d:
        r0 = r43;
        r0 = r0.data;
        r42 = r0;
    L_0x00a3:
        r34 = new org.telegram.messenger.exoplayer2.extractor.mp4.AtomParsers$ChunkIterator;
        r0 = r34;
        r1 = r89;
        r2 = r35;
        r3 = r36;
        r0.<init>(r1, r2, r3);
        r8 = 12;
        r0 = r93;
        r0.setPosition(r8);
        r8 = r93.readUnsignedIntToInt();
        r82 = r8 + -1;
        r78 = r93.readUnsignedIntToInt();
        r95 = r93.readUnsignedIntToInt();
        r79 = 0;
        r83 = 0;
        r98 = 0;
        if (r42 == 0) goto L_0x00d8;
    L_0x00cd:
        r8 = 12;
        r0 = r42;
        r0.setPosition(r8);
        r83 = r42.readUnsignedIntToInt();
    L_0x00d8:
        r68 = -1;
        r81 = 0;
        if (r90 == 0) goto L_0x00f1;
    L_0x00de:
        r8 = 12;
        r0 = r90;
        r0.setPosition(r8);
        r81 = r90.readUnsignedIntToInt();
        if (r81 <= 0) goto L_0x0152;
    L_0x00eb:
        r8 = r90.readUnsignedIntToInt();
        r68 = r8 + -1;
    L_0x00f1:
        r8 = r86.isFixedSampleSize();
        if (r8 == 0) goto L_0x0155;
    L_0x00f7:
        r8 = "audio/raw";
        r0 = r102;
        r9 = r0.format;
        r9 = r9.sampleMimeType;
        r8 = r8.equals(r9);
        if (r8 == 0) goto L_0x0155;
    L_0x0106:
        if (r82 != 0) goto L_0x0155;
    L_0x0108:
        if (r83 != 0) goto L_0x0155;
    L_0x010a:
        if (r81 != 0) goto L_0x0155;
    L_0x010c:
        r64 = 1;
    L_0x010e:
        r66 = 0;
        r100 = 0;
        if (r64 != 0) goto L_0x0265;
    L_0x0114:
        r0 = r84;
        r0 = new long[r0];
        r69 = r0;
        r0 = r84;
        r0 = new int[r0];
        r87 = r0;
        r0 = r84;
        r12 = new long[r0];
        r0 = r84;
        r13 = new int[r0];
        r70 = 0;
        r80 = 0;
        r63 = 0;
    L_0x012e:
        r0 = r63;
        r1 = r84;
        if (r0 >= r1) goto L_0x01bc;
    L_0x0134:
        if (r80 != 0) goto L_0x0158;
    L_0x0136:
        r8 = r34.moveNext();
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r8);
        r0 = r34;
        r0 = r0.offset;
        r70 = r0;
        r0 = r34;
        r0 = r0.numSamples;
        r80 = r0;
        goto L_0x0134;
    L_0x014a:
        r90 = 0;
        goto L_0x0093;
    L_0x014e:
        r42 = 0;
        goto L_0x00a3;
    L_0x0152:
        r90 = 0;
        goto L_0x00f1;
    L_0x0155:
        r64 = 0;
        goto L_0x010e;
    L_0x0158:
        if (r42 == 0) goto L_0x016b;
    L_0x015a:
        if (r79 != 0) goto L_0x0169;
    L_0x015c:
        if (r83 <= 0) goto L_0x0169;
    L_0x015e:
        r79 = r42.readUnsignedIntToInt();
        r98 = r42.readInt();
        r83 = r83 + -1;
        goto L_0x015a;
    L_0x0169:
        r79 = r79 + -1;
    L_0x016b:
        r69[r63] = r70;
        r8 = r86.readNextSampleSize();
        r87[r63] = r8;
        r8 = r87[r63];
        r0 = r66;
        if (r8 <= r0) goto L_0x017b;
    L_0x0179:
        r66 = r87[r63];
    L_0x017b:
        r0 = r98;
        r8 = (long) r0;
        r8 = r8 + r100;
        r12[r63] = r8;
        if (r90 != 0) goto L_0x01ba;
    L_0x0184:
        r8 = 1;
    L_0x0185:
        r13[r63] = r8;
        r0 = r63;
        r1 = r68;
        if (r0 != r1) goto L_0x019a;
    L_0x018d:
        r8 = 1;
        r13[r63] = r8;
        r81 = r81 + -1;
        if (r81 <= 0) goto L_0x019a;
    L_0x0194:
        r8 = r90.readUnsignedIntToInt();
        r68 = r8 + -1;
    L_0x019a:
        r0 = r95;
        r8 = (long) r0;
        r100 = r100 + r8;
        r78 = r78 + -1;
        if (r78 != 0) goto L_0x01af;
    L_0x01a3:
        if (r82 <= 0) goto L_0x01af;
    L_0x01a5:
        r78 = r93.readUnsignedIntToInt();
        r95 = r93.readInt();
        r82 = r82 + -1;
    L_0x01af:
        r8 = r87[r63];
        r8 = (long) r8;
        r70 = r70 + r8;
        r80 = r80 + -1;
        r63 = r63 + 1;
        goto L_0x012e;
    L_0x01ba:
        r8 = 0;
        goto L_0x0185;
    L_0x01bc:
        r0 = r98;
        r8 = (long) r0;
        r6 = r100 + r8;
        if (r79 != 0) goto L_0x01d9;
    L_0x01c3:
        r8 = 1;
    L_0x01c4:
        org.telegram.messenger.exoplayer2.util.Assertions.checkArgument(r8);
    L_0x01c7:
        if (r83 <= 0) goto L_0x01dd;
    L_0x01c9:
        r8 = r42.readUnsignedIntToInt();
        if (r8 != 0) goto L_0x01db;
    L_0x01cf:
        r8 = 1;
    L_0x01d0:
        org.telegram.messenger.exoplayer2.util.Assertions.checkArgument(r8);
        r42.readInt();
        r83 = r83 + -1;
        goto L_0x01c7;
    L_0x01d9:
        r8 = 0;
        goto L_0x01c4;
    L_0x01db:
        r8 = 0;
        goto L_0x01d0;
    L_0x01dd:
        if (r81 != 0) goto L_0x01e5;
    L_0x01df:
        if (r78 != 0) goto L_0x01e5;
    L_0x01e1:
        if (r80 != 0) goto L_0x01e5;
    L_0x01e3:
        if (r82 == 0) goto L_0x0237;
    L_0x01e5:
        r8 = "AtomParsers";
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Inconsistent stbl box for track ";
        r9 = r9.append(r10);
        r0 = r102;
        r10 = r0.id;
        r9 = r9.append(r10);
        r10 = ": remainingSynchronizationSamples ";
        r9 = r9.append(r10);
        r0 = r81;
        r9 = r9.append(r0);
        r10 = ", remainingSamplesAtTimestampDelta ";
        r9 = r9.append(r10);
        r0 = r78;
        r9 = r9.append(r0);
        r10 = ", remainingSamplesInChunk ";
        r9 = r9.append(r10);
        r0 = r80;
        r9 = r9.append(r0);
        r10 = ", remainingTimestampDeltaChanges ";
        r9 = r9.append(r10);
        r0 = r82;
        r9 = r9.append(r0);
        r9 = r9.toString();
        android.util.Log.w(r8, r9);
    L_0x0237:
        r8 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r10 = r0.timescale;
        r14 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r6, r8, r10);
        r0 = r102;
        r8 = r0.editListDurations;
        if (r8 == 0) goto L_0x024e;
    L_0x0248:
        r8 = r104.hasGaplessInfo();
        if (r8 == 0) goto L_0x02cd;
    L_0x024e:
        r8 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r10 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r12, r8, r10);
        r8 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r9 = r69;
        r10 = r87;
        r11 = r66;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        goto L_0x0031;
    L_0x0265:
        r0 = r34;
        r8 = r0.length;
        r0 = new long[r8];
        r38 = r0;
        r0 = r34;
        r8 = r0.length;
        r0 = new int[r8];
        r39 = r0;
    L_0x0275:
        r8 = r34.moveNext();
        if (r8 == 0) goto L_0x0290;
    L_0x027b:
        r0 = r34;
        r8 = r0.index;
        r0 = r34;
        r10 = r0.offset;
        r38[r8] = r10;
        r0 = r34;
        r8 = r0.index;
        r0 = r34;
        r9 = r0.numSamples;
        r39[r8] = r9;
        goto L_0x0275;
    L_0x0290:
        r0 = r102;
        r8 = r0.format;
        r8 = r8.pcmEncoding;
        r0 = r102;
        r9 = r0.format;
        r9 = r9.channelCount;
        r59 = org.telegram.messenger.exoplayer2.util.Util.getPcmFrameSize(r8, r9);
        r0 = r95;
        r8 = (long) r0;
        r0 = r59;
        r1 = r38;
        r2 = r39;
        r73 = org.telegram.messenger.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.rechunk(r0, r1, r2, r8);
        r0 = r73;
        r0 = r0.offsets;
        r69 = r0;
        r0 = r73;
        r0 = r0.sizes;
        r87 = r0;
        r0 = r73;
        r0 = r0.maximumSize;
        r66 = r0;
        r0 = r73;
        r12 = r0.timestamps;
        r0 = r73;
        r13 = r0.flags;
        r0 = r73;
        r6 = r0.duration;
        goto L_0x0237;
    L_0x02cd:
        r0 = r102;
        r8 = r0.editListDurations;
        r8 = r8.length;
        r9 = 1;
        if (r8 != r9) goto L_0x037a;
    L_0x02d5:
        r0 = r102;
        r8 = r0.type;
        r9 = 1;
        if (r8 != r9) goto L_0x037a;
    L_0x02dc:
        r8 = r12.length;
        r9 = 2;
        if (r8 < r9) goto L_0x037a;
    L_0x02e0:
        r0 = r102;
        r8 = r0.editListMediaTimes;
        r9 = 0;
        r46 = r8[r9];
        r0 = r102;
        r8 = r0.editListDurations;
        r9 = 0;
        r16 = r8[r9];
        r0 = r102;
        r0 = r0.timescale;
        r18 = r0;
        r0 = r102;
        r0 = r0.movieTimescale;
        r20 = r0;
        r8 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r22 = r46 + r8;
        r17 = r12;
        r18 = r6;
        r20 = r46;
        r8 = canApplyEditWithGaplessInfo(r17, r18, r20, r22);
        if (r8 == 0) goto L_0x037a;
    L_0x030c:
        r74 = r6 - r22;
        r8 = 0;
        r8 = r12[r8];
        r16 = r46 - r8;
        r0 = r102;
        r8 = r0.format;
        r8 = r8.sampleRate;
        r0 = (long) r8;
        r18 = r0;
        r0 = r102;
        r0 = r0.timescale;
        r20 = r0;
        r54 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r0 = r102;
        r8 = r0.format;
        r8 = r8.sampleRate;
        r0 = (long) r8;
        r18 = r0;
        r0 = r102;
        r0 = r0.timescale;
        r20 = r0;
        r16 = r74;
        r56 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r8 = 0;
        r8 = (r54 > r8 ? 1 : (r54 == r8 ? 0 : -1));
        if (r8 != 0) goto L_0x0347;
    L_0x0341:
        r8 = 0;
        r8 = (r56 > r8 ? 1 : (r56 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x037a;
    L_0x0347:
        r8 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r8 = (r54 > r8 ? 1 : (r54 == r8 ? 0 : -1));
        if (r8 > 0) goto L_0x037a;
    L_0x034e:
        r8 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r8 = (r56 > r8 ? 1 : (r56 == r8 ? 0 : -1));
        if (r8 > 0) goto L_0x037a;
    L_0x0355:
        r0 = r54;
        r8 = (int) r0;
        r0 = r104;
        r0.encoderDelay = r8;
        r0 = r56;
        r8 = (int) r0;
        r0 = r104;
        r0.encoderPadding = r8;
        r8 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r10 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r12, r8, r10);
        r8 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r9 = r69;
        r10 = r87;
        r11 = r66;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        goto L_0x0031;
    L_0x037a:
        r0 = r102;
        r8 = r0.editListDurations;
        r8 = r8.length;
        r9 = 1;
        if (r8 != r9) goto L_0x03cf;
    L_0x0382:
        r0 = r102;
        r8 = r0.editListDurations;
        r9 = 0;
        r8 = r8[r9];
        r10 = 0;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 != 0) goto L_0x03cf;
    L_0x038f:
        r0 = r102;
        r8 = r0.editListMediaTimes;
        r9 = 0;
        r46 = r8[r9];
        r63 = 0;
    L_0x0398:
        r8 = r12.length;
        r0 = r63;
        if (r0 >= r8) goto L_0x03b3;
    L_0x039d:
        r8 = r12[r63];
        r16 = r8 - r46;
        r18 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r0 = r0.timescale;
        r20 = r0;
        r8 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r12[r63] = r8;
        r63 = r63 + 1;
        goto L_0x0398;
    L_0x03b3:
        r16 = r6 - r46;
        r18 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r0 = r0.timescale;
        r20 = r0;
        r14 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r8 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r9 = r69;
        r10 = r87;
        r11 = r66;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        goto L_0x0031;
    L_0x03cf:
        r0 = r102;
        r8 = r0.type;
        r9 = 1;
        if (r8 != r9) goto L_0x042e;
    L_0x03d6:
        r72 = 1;
    L_0x03d8:
        r51 = 0;
        r67 = 0;
        r40 = 0;
        r63 = 0;
    L_0x03e0:
        r0 = r102;
        r8 = r0.editListDurations;
        r8 = r8.length;
        r0 = r63;
        if (r0 >= r8) goto L_0x0433;
    L_0x03e9:
        r0 = r102;
        r8 = r0.editListMediaTimes;
        r44 = r8[r63];
        r8 = -1;
        r8 = (r44 > r8 ? 1 : (r44 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x042b;
    L_0x03f5:
        r0 = r102;
        r8 = r0.editListDurations;
        r16 = r8[r63];
        r0 = r102;
        r0 = r0.timescale;
        r18 = r0;
        r0 = r102;
        r0 = r0.movieTimescale;
        r20 = r0;
        r16 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r8 = 1;
        r9 = 1;
        r0 = r44;
        r88 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r12, r0, r8, r9);
        r8 = r44 + r16;
        r10 = 0;
        r0 = r72;
        r58 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r12, r8, r0, r10);
        r8 = r58 - r88;
        r51 = r51 + r8;
        r0 = r67;
        r1 = r88;
        if (r0 == r1) goto L_0x0431;
    L_0x0426:
        r8 = 1;
    L_0x0427:
        r40 = r40 | r8;
        r67 = r58;
    L_0x042b:
        r63 = r63 + 1;
        goto L_0x03e0;
    L_0x042e:
        r72 = 0;
        goto L_0x03d8;
    L_0x0431:
        r8 = 0;
        goto L_0x0427;
    L_0x0433:
        r0 = r51;
        r1 = r84;
        if (r0 == r1) goto L_0x0504;
    L_0x0439:
        r8 = 1;
    L_0x043a:
        r40 = r40 | r8;
        if (r40 == 0) goto L_0x0507;
    L_0x043e:
        r0 = r51;
        r0 = new long[r0];
        r50 = r0;
    L_0x0444:
        if (r40 == 0) goto L_0x050b;
    L_0x0446:
        r0 = r51;
        r0 = new int[r0];
        r52 = r0;
    L_0x044c:
        if (r40 == 0) goto L_0x050f;
    L_0x044e:
        r49 = 0;
    L_0x0450:
        if (r40 == 0) goto L_0x0513;
    L_0x0452:
        r0 = r51;
        r0 = new int[r0];
        r48 = r0;
    L_0x0458:
        r0 = r51;
        r0 = new long[r0];
        r53 = r0;
        r24 = 0;
        r85 = 0;
        r63 = 0;
    L_0x0464:
        r0 = r102;
        r8 = r0.editListDurations;
        r8 = r8.length;
        r0 = r63;
        if (r0 >= r8) goto L_0x051d;
    L_0x046d:
        r0 = r102;
        r8 = r0.editListMediaTimes;
        r44 = r8[r63];
        r0 = r102;
        r8 = r0.editListDurations;
        r16 = r8[r63];
        r8 = -1;
        r8 = (r44 > r8 ? 1 : (r44 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x0517;
    L_0x047f:
        r0 = r102;
        r0 = r0.timescale;
        r18 = r0;
        r0 = r102;
        r0 = r0.movieTimescale;
        r20 = r0;
        r8 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r16, r18, r20);
        r60 = r44 + r8;
        r8 = 1;
        r9 = 1;
        r0 = r44;
        r88 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r12, r0, r8, r9);
        r8 = 0;
        r0 = r60;
        r2 = r72;
        r58 = org.telegram.messenger.exoplayer2.util.Util.binarySearchCeil(r12, r0, r2, r8);
        if (r40 == 0) goto L_0x04cb;
    L_0x04a4:
        r41 = r58 - r88;
        r0 = r69;
        r1 = r88;
        r2 = r50;
        r3 = r85;
        r4 = r41;
        java.lang.System.arraycopy(r0, r1, r2, r3, r4);
        r0 = r87;
        r1 = r88;
        r2 = r52;
        r3 = r85;
        r4 = r41;
        java.lang.System.arraycopy(r0, r1, r2, r3, r4);
        r0 = r88;
        r1 = r48;
        r2 = r85;
        r3 = r41;
        java.lang.System.arraycopy(r13, r0, r1, r2, r3);
    L_0x04cb:
        r65 = r88;
    L_0x04cd:
        r0 = r65;
        r1 = r58;
        if (r0 >= r1) goto L_0x0517;
    L_0x04d3:
        r26 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r0 = r0.movieTimescale;
        r28 = r0;
        r76 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r24, r26, r28);
        r8 = r12[r65];
        r26 = r8 - r44;
        r28 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r0 = r0.timescale;
        r30 = r0;
        r96 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r26, r28, r30);
        r8 = r76 + r96;
        r53[r85] = r8;
        if (r40 == 0) goto L_0x04ff;
    L_0x04f7:
        r8 = r52[r85];
        r0 = r49;
        if (r8 <= r0) goto L_0x04ff;
    L_0x04fd:
        r49 = r87[r65];
    L_0x04ff:
        r85 = r85 + 1;
        r65 = r65 + 1;
        goto L_0x04cd;
    L_0x0504:
        r8 = 0;
        goto L_0x043a;
    L_0x0507:
        r50 = r69;
        goto L_0x0444;
    L_0x050b:
        r52 = r87;
        goto L_0x044c;
    L_0x050f:
        r49 = r66;
        goto L_0x0450;
    L_0x0513:
        r48 = r13;
        goto L_0x0458;
    L_0x0517:
        r24 = r24 + r16;
        r63 = r63 + 1;
        goto L_0x0464;
    L_0x051d:
        r26 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r0 = r0.timescale;
        r28 = r0;
        r32 = org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestamp(r24, r26, r28);
        r62 = 0;
        r63 = 0;
    L_0x052e:
        r0 = r48;
        r8 = r0.length;
        r0 = r63;
        if (r0 >= r8) goto L_0x0545;
    L_0x0535:
        if (r62 != 0) goto L_0x0545;
    L_0x0537:
        r8 = r48[r63];
        r8 = r8 & 1;
        if (r8 == 0) goto L_0x0543;
    L_0x053d:
        r8 = 1;
    L_0x053e:
        r62 = r62 | r8;
        r63 = r63 + 1;
        goto L_0x052e;
    L_0x0543:
        r8 = 0;
        goto L_0x053e;
    L_0x0545:
        if (r62 != 0) goto L_0x0567;
    L_0x0547:
        r8 = "AtomParsers";
        r9 = "Ignoring edit list: Edited sample sequence does not contain a sync sample.";
        android.util.Log.w(r8, r9);
        r8 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r102;
        r10 = r0.timescale;
        org.telegram.messenger.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r12, r8, r10);
        r8 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r9 = r69;
        r10 = r87;
        r11 = r66;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        goto L_0x0031;
    L_0x0567:
        r26 = new org.telegram.messenger.exoplayer2.extractor.mp4.TrackSampleTable;
        r27 = r50;
        r28 = r52;
        r29 = r49;
        r30 = r53;
        r31 = r48;
        r26.<init>(r27, r28, r29, r30, r31, r32);
        r8 = r26;
        goto L_0x0031;
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
            duration = C0616C.TIME_UNSET;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = C0616C.TIME_UNSET;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == C0616C.DEFAULT_BUFFER_SEGMENT_SIZE && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == C0616C.DEFAULT_BUFFER_SEGMENT_SIZE && a11 == 0) {
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
        String mimeType = MimeTypes.getMimeTypeFromMp4ObjectType(parent.readUnsignedByte());
        if (MimeTypes.AUDIO_MPEG.equals(mimeType) || MimeTypes.AUDIO_DTS.equals(mimeType) || MimeTypes.AUDIO_DTS_HD.equals(mimeType)) {
            return Pair.create(mimeType, null);
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
        if (!C0616C.CENC_TYPE_cenc.equals(schemeType) && !C0616C.CENC_TYPE_cbc1.equals(schemeType) && !C0616C.CENC_TYPE_cens.equals(schemeType) && !C0616C.CENC_TYPE_cbcs.equals(schemeType)) {
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

    private static boolean canApplyEditWithGaplessInfo(long[] timestamps, long duration, long editStartTime, long editEndTime) {
        int lastIndex = timestamps.length - 1;
        int latestDelayIndex = Util.constrainValue(3, 0, lastIndex);
        int earliestPaddingIndex = Util.constrainValue(timestamps.length - 3, 0, lastIndex);
        if (timestamps[0] > editStartTime || editStartTime >= timestamps[latestDelayIndex] || timestamps[earliestPaddingIndex] >= editEndTime || editEndTime > duration) {
            return false;
        }
        return true;
    }

    private AtomParsers() {
    }
}
