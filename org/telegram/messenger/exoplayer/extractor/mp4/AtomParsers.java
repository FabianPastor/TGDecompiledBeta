package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.extractor.mp4.FixedSampleSizeRechunker.Results;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.volley.DefaultRetryPolicy;

final class AtomParsers {

    private static final class AvcCData {
        public final List<byte[]> initializationData;
        public final int nalUnitLengthFieldLength;
        public final float pixelWidthAspectRatio;

        public AvcCData(List<byte[]> initializationData, int nalUnitLengthFieldLength, float pixelWidthAspectRatio) {
            this.initializationData = initializationData;
            this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
        }
    }

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
        public MediaFormat mediaFormat;
        public int nalUnitLengthFieldLength = -1;
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

    public static Track parseTrak(ContainerAtom trak, LeafAtom mvhd, long duration, boolean isQuickTime) {
        ContainerAtom mdia = trak.getContainerAtomOfType(Atom.TYPE_mdia);
        int trackType = parseHdlr(mdia.getLeafAtomOfType(Atom.TYPE_hdlr).data);
        if (trackType != Track.TYPE_soun && trackType != Track.TYPE_vide && trackType != Track.TYPE_text && trackType != Track.TYPE_sbtl && trackType != Track.TYPE_subt) {
            return null;
        }
        long durationUs;
        TkhdData tkhdData = parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        if (duration == -1) {
            duration = tkhdData.duration;
        }
        long movieTimescale = parseMvhd(mvhd.data);
        if (duration == -1) {
            durationUs = -1;
        } else {
            durationUs = Util.scaleLargeTimestamp(duration, C.MICROS_PER_SECOND, movieTimescale);
        }
        ContainerAtom stbl = mdia.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        Pair<Long, String> mdhdData = parseMdhd(mdia.getLeafAtomOfType(Atom.TYPE_mdhd).data);
        StsdData stsdData = parseStsd(stbl.getLeafAtomOfType(Atom.TYPE_stsd).data, tkhdData.id, durationUs, tkhdData.rotationDegrees, (String) mdhdData.second, isQuickTime);
        Pair<long[], long[]> edtsData = parseEdts(trak.getContainerAtomOfType(Atom.TYPE_edts));
        if (stsdData.mediaFormat == null) {
            return null;
        }
        return new Track(tkhdData.id, trackType, ((Long) mdhdData.first).longValue(), movieTimescale, durationUs, stsdData.mediaFormat, stsdData.trackEncryptionBoxes, stsdData.nalUnitLengthFieldLength, (long[]) edtsData.first, (long[]) edtsData.second);
    }

    public static TrackSampleTable parseStbl(Track track, ContainerAtom stblAtom) throws ParserException {
        LeafAtom stszAtom = stblAtom.getLeafAtomOfType(Atom.TYPE_stsz);
        SampleSizeBox stszSampleSizeBox;
        if (stszAtom != null) {
            stszSampleSizeBox = new StszSampleSizeBox(stszAtom);
        } else {
            LeafAtom stz2Atom = stblAtom.getLeafAtomOfType(Atom.TYPE_stz2);
            if (stz2Atom == null) {
                throw new ParserException("Track has no sample table size information");
            }
            stszSampleSizeBox = new Stz2SampleSizeBox(stz2Atom);
        }
        int sampleCount = sampleSizeBox.getSampleCount();
        if (sampleCount == 0) {
            return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
        }
        Object offsets;
        Object sizes;
        long[] timestamps;
        Object flags;
        int i;
        boolean chunkOffsetsAreLongs = false;
        LeafAtom chunkOffsetsAtom = stblAtom.getLeafAtomOfType(Atom.TYPE_stco);
        if (chunkOffsetsAtom == null) {
            chunkOffsetsAreLongs = true;
            chunkOffsetsAtom = stblAtom.getLeafAtomOfType(Atom.TYPE_co64);
        }
        ParsableByteArray chunkOffsets = chunkOffsetsAtom.data;
        ParsableByteArray stsc = stblAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
        ParsableByteArray stts = stblAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
        LeafAtom stssAtom = stblAtom.getLeafAtomOfType(Atom.TYPE_stss);
        ParsableByteArray stss = stssAtom != null ? stssAtom.data : null;
        LeafAtom cttsAtom = stblAtom.getLeafAtomOfType(Atom.TYPE_ctts);
        ParsableByteArray ctts = cttsAtom != null ? cttsAtom.data : null;
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
        int nextSynchronizationSampleIndex = -1;
        int remainingSynchronizationSamples = 0;
        if (stss != null) {
            stss.setPosition(12);
            remainingSynchronizationSamples = stss.readUnsignedIntToInt();
            if (remainingSynchronizationSamples > 0) {
                nextSynchronizationSampleIndex = stss.readUnsignedIntToInt() - 1;
            } else {
                stss = null;
            }
        }
        boolean isRechunkable = sampleSizeBox.isFixedSampleSize() && MimeTypes.AUDIO_RAW.equals(track.mediaFormat.mimeType) && remainingTimestampDeltaChanges == 0 && remainingTimestampOffsetChanges == 0 && remainingSynchronizationSamples == 0;
        int maximumSize = 0;
        if (isRechunkable) {
            long[] chunkOffsetsBytes = new long[chunkIterator.length];
            int[] chunkSampleCounts = new int[chunkIterator.length];
            while (chunkIterator.moveNext()) {
                chunkOffsetsBytes[chunkIterator.index] = chunkIterator.offset;
                chunkSampleCounts[chunkIterator.index] = chunkIterator.numSamples;
            }
            Results rechunkedResults = FixedSampleSizeRechunker.rechunk(sampleSizeBox.readNextSampleSize(), chunkOffsetsBytes, chunkSampleCounts, (long) timestampDeltaInTimeUnits);
            offsets = rechunkedResults.offsets;
            sizes = rechunkedResults.sizes;
            maximumSize = rechunkedResults.maximumSize;
            timestamps = rechunkedResults.timestamps;
            flags = rechunkedResults.flags;
        } else {
            boolean z;
            offsets = new long[sampleCount];
            sizes = new int[sampleCount];
            timestamps = new long[sampleCount];
            flags = new int[sampleCount];
            long timestampTimeUnits = 0;
            long offset = 0;
            int remainingSamplesInChunk = 0;
            for (i = 0; i < sampleCount; i++) {
                while (remainingSamplesInChunk == 0) {
                    Assertions.checkState(chunkIterator.moveNext());
                    offset = chunkIterator.offset;
                    remainingSamplesInChunk = chunkIterator.numSamples;
                }
                if (ctts != null) {
                    while (remainingSamplesAtTimestampOffset == 0 && remainingTimestampOffsetChanges > 0) {
                        remainingSamplesAtTimestampOffset = ctts.readUnsignedIntToInt();
                        timestampOffset = ctts.readInt();
                        remainingTimestampOffsetChanges--;
                    }
                    remainingSamplesAtTimestampOffset--;
                }
                offsets[i] = offset;
                sizes[i] = sampleSizeBox.readNextSampleSize();
                if (sizes[i] > maximumSize) {
                    maximumSize = sizes[i];
                }
                timestamps[i] = ((long) timestampOffset) + timestampTimeUnits;
                flags[i] = stss == null ? 1 : 0;
                if (i == nextSynchronizationSampleIndex) {
                    flags[i] = 1;
                    remainingSynchronizationSamples--;
                    if (remainingSynchronizationSamples > 0) {
                        nextSynchronizationSampleIndex = stss.readUnsignedIntToInt() - 1;
                    }
                }
                timestampTimeUnits += (long) timestampDeltaInTimeUnits;
                remainingSamplesAtTimestampDelta--;
                if (remainingSamplesAtTimestampDelta == 0 && remainingTimestampDeltaChanges > 0) {
                    remainingSamplesAtTimestampDelta = stts.readUnsignedIntToInt();
                    timestampDeltaInTimeUnits = stts.readUnsignedIntToInt();
                    remainingTimestampDeltaChanges--;
                }
                offset += (long) sizes[i];
                remainingSamplesInChunk--;
            }
            Assertions.checkArgument(remainingSamplesAtTimestampOffset == 0);
            while (remainingTimestampOffsetChanges > 0) {
                Assertions.checkArgument(ctts.readUnsignedIntToInt() == 0);
                ctts.readInt();
                remainingTimestampOffsetChanges--;
            }
            Assertions.checkArgument(remainingSynchronizationSamples == 0);
            Assertions.checkArgument(remainingSamplesAtTimestampDelta == 0);
            Assertions.checkArgument(remainingSamplesInChunk == 0);
            if (remainingTimestampDeltaChanges == 0) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkArgument(z);
        }
        if (track.editListDurations == null) {
            Util.scaleLargeTimestampsInPlace(timestamps, C.MICROS_PER_SECOND, track.timescale);
            return new TrackSampleTable(offsets, sizes, maximumSize, timestamps, flags);
        } else if (track.editListDurations.length == 1 && track.editListDurations[0] == 0) {
            for (i = 0; i < timestamps.length; i++) {
                timestamps[i] = Util.scaleLargeTimestamp(timestamps[i] - track.editListMediaTimes[0], C.MICROS_PER_SECOND, track.timescale);
            }
            return new TrackSampleTable(offsets, sizes, maximumSize, timestamps, flags);
        } else {
            long mediaTime;
            long duration;
            int startIndex;
            int endIndex;
            Object editedOffsets;
            Object editedSizes;
            int editedMaximumSize;
            Object editedFlags;
            int editedSampleCount = 0;
            int nextSampleIndex = 0;
            boolean copyMetadata = false;
            for (i = 0; i < track.editListDurations.length; i++) {
                mediaTime = track.editListMediaTimes[i];
                if (mediaTime != -1) {
                    duration = Util.scaleLargeTimestamp(track.editListDurations[i], track.timescale, track.movieTimescale);
                    startIndex = Util.binarySearchCeil(timestamps, mediaTime, true, true);
                    endIndex = Util.binarySearchCeil(timestamps, mediaTime + duration, true, false);
                    editedSampleCount += endIndex - startIndex;
                    copyMetadata |= nextSampleIndex != startIndex ? 1 : 0;
                    nextSampleIndex = endIndex;
                }
            }
            copyMetadata |= editedSampleCount != sampleCount ? 1 : 0;
            if (copyMetadata) {
                editedOffsets = new long[editedSampleCount];
            } else {
                editedOffsets = offsets;
            }
            if (copyMetadata) {
                editedSizes = new int[editedSampleCount];
            } else {
                editedSizes = sizes;
            }
            if (copyMetadata) {
                editedMaximumSize = 0;
            } else {
                editedMaximumSize = maximumSize;
            }
            if (copyMetadata) {
                editedFlags = new int[editedSampleCount];
            } else {
                editedFlags = flags;
            }
            long[] editedTimestamps = new long[editedSampleCount];
            long pts = 0;
            int sampleIndex = 0;
            for (i = 0; i < track.editListDurations.length; i++) {
                mediaTime = track.editListMediaTimes[i];
                duration = track.editListDurations[i];
                if (mediaTime != -1) {
                    long endMediaTime = mediaTime + Util.scaleLargeTimestamp(duration, track.timescale, track.movieTimescale);
                    startIndex = Util.binarySearchCeil(timestamps, mediaTime, true, true);
                    endIndex = Util.binarySearchCeil(timestamps, endMediaTime, true, false);
                    if (copyMetadata) {
                        int count = endIndex - startIndex;
                        System.arraycopy(offsets, startIndex, editedOffsets, sampleIndex, count);
                        System.arraycopy(sizes, startIndex, editedSizes, sampleIndex, count);
                        System.arraycopy(flags, startIndex, editedFlags, sampleIndex, count);
                    }
                    for (int j = startIndex; j < endIndex; j++) {
                        editedTimestamps[sampleIndex] = Util.scaleLargeTimestamp(pts, C.MICROS_PER_SECOND, track.movieTimescale) + Util.scaleLargeTimestamp(timestamps[j] - mediaTime, C.MICROS_PER_SECOND, track.timescale);
                        if (copyMetadata && editedSizes[sampleIndex] > editedMaximumSize) {
                            editedMaximumSize = sizes[j];
                        }
                        sampleIndex++;
                    }
                }
                pts += duration;
            }
            boolean hasSyncSample = false;
            for (i = 0; i < editedFlags.length && !hasSyncSample; i++) {
                hasSyncSample |= (editedFlags[i] & 1) != 0 ? 1 : 0;
            }
            if (hasSyncSample) {
                return new TrackSampleTable(editedOffsets, editedSizes, editedMaximumSize, editedTimestamps, editedFlags);
            }
            throw new ParserException("The edited sample sequence does not contain a sync sample.");
        }
    }

    public static GaplessInfo parseUdta(LeafAtom udtaAtom, boolean isQuickTime) {
        if (isQuickTime) {
            return null;
        }
        ParsableByteArray udtaData = udtaAtom.data;
        udtaData.setPosition(8);
        while (udtaData.bytesLeft() >= 8) {
            int atomSize = udtaData.readInt();
            if (udtaData.readInt() == Atom.TYPE_meta) {
                udtaData.setPosition(udtaData.getPosition() - 8);
                udtaData.setLimit(udtaData.getPosition() + atomSize);
                return parseMetaAtom(udtaData);
            }
            udtaData.skipBytes(atomSize - 8);
        }
        return null;
    }

    private static GaplessInfo parseMetaAtom(ParsableByteArray data) {
        data.skipBytes(12);
        ParsableByteArray ilst = new ParsableByteArray();
        while (data.bytesLeft() >= 8) {
            int payloadSize = data.readInt() - 8;
            if (data.readInt() == Atom.TYPE_ilst) {
                ilst.reset(data.data, data.getPosition() + payloadSize);
                ilst.setPosition(data.getPosition());
                GaplessInfo gaplessInfo = parseIlst(ilst);
                if (gaplessInfo != null) {
                    return gaplessInfo;
                }
            }
            data.skipBytes(payloadSize);
        }
        return null;
    }

    private static GaplessInfo parseIlst(ParsableByteArray ilst) {
        while (ilst.bytesLeft() > 0) {
            int endPosition = ilst.getPosition() + ilst.readInt();
            if (ilst.readInt() == Atom.TYPE_DASHES) {
                String lastCommentMean = null;
                String lastCommentName = null;
                String lastCommentData = null;
                while (ilst.getPosition() < endPosition) {
                    int length = ilst.readInt() - 12;
                    int key = ilst.readInt();
                    ilst.skipBytes(4);
                    if (key == Atom.TYPE_mean) {
                        lastCommentMean = ilst.readString(length);
                    } else if (key == Atom.TYPE_name) {
                        lastCommentName = ilst.readString(length);
                    } else if (key == Atom.TYPE_data) {
                        ilst.skipBytes(4);
                        lastCommentData = ilst.readString(length - 4);
                    } else {
                        ilst.skipBytes(length);
                    }
                }
                if (!(lastCommentName == null || lastCommentData == null || !"com.apple.iTunes".equals(lastCommentMean))) {
                    return GaplessInfo.createFromComment(lastCommentName, lastCommentData);
                }
            }
            ilst.setPosition(endPosition);
        }
        return null;
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
            duration = -1;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = -1;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == 65536 && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == 65536 && a11 == 0) {
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
        return hdlr.readInt();
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
        return Pair.create(Long.valueOf(timescale), "" + ((char) (((languageCode >> 10) & 31) + 96)) + ((char) (((languageCode >> 5) & 31) + 96)) + ((char) ((languageCode & 31) + 96)));
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, long durationUs, int rotationDegrees, String language, boolean isQuickTime) {
        stsd.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = stsd.readInt();
            if (childAtomType == Atom.TYPE_avc1 || childAtomType == Atom.TYPE_avc3 || childAtomType == Atom.TYPE_encv || childAtomType == Atom.TYPE_mp4v || childAtomType == Atom.TYPE_hvc1 || childAtomType == Atom.TYPE_hev1 || childAtomType == Atom.TYPE_s263 || childAtomType == Atom.TYPE_vp08 || childAtomType == Atom.TYPE_vp09) {
                parseVideoSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, durationUs, rotationDegrees, out, i);
            } else if (childAtomType == Atom.TYPE_mp4a || childAtomType == Atom.TYPE_enca || childAtomType == Atom.TYPE_ac_3 || childAtomType == Atom.TYPE_ec_3 || childAtomType == Atom.TYPE_dtsc || childAtomType == Atom.TYPE_dtse || childAtomType == Atom.TYPE_dtsh || childAtomType == Atom.TYPE_dtsl || childAtomType == Atom.TYPE_samr || childAtomType == Atom.TYPE_sawb || childAtomType == Atom.TYPE_lpcm || childAtomType == Atom.TYPE_sowt) {
                parseAudioSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, durationUs, language, isQuickTime, out, i);
            } else if (childAtomType == Atom.TYPE_TTML) {
                out.mediaFormat = MediaFormat.createTextFormat(Integer.toString(trackId), MimeTypes.APPLICATION_TTML, -1, durationUs, language);
            } else if (childAtomType == Atom.TYPE_tx3g) {
                out.mediaFormat = MediaFormat.createTextFormat(Integer.toString(trackId), MimeTypes.APPLICATION_TX3G, -1, durationUs, language);
            } else if (childAtomType == Atom.TYPE_wvtt) {
                out.mediaFormat = MediaFormat.createTextFormat(Integer.toString(trackId), MimeTypes.APPLICATION_MP4VTT, -1, durationUs, language);
            } else if (childAtomType == Atom.TYPE_stpp) {
                out.mediaFormat = MediaFormat.createTextFormat(Integer.toString(trackId), MimeTypes.APPLICATION_TTML, -1, durationUs, language, 0);
            }
            stsd.setPosition(childStartPosition + childAtomSize);
        }
        return out;
    }

    private static void parseVideoSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, long durationUs, int rotationDegrees, StsdData out, int entryIndex) {
        parent.setPosition(position + 8);
        parent.skipBytes(24);
        int width = parent.readUnsignedShort();
        int height = parent.readUnsignedShort();
        boolean pixelWidthHeightRatioFromPasp = false;
        float pixelWidthHeightRatio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        parent.skipBytes(50);
        int childPosition = parent.getPosition();
        if (atomType == Atom.TYPE_encv) {
            parseSampleEntryEncryptionData(parent, position, size, out, entryIndex);
            parent.setPosition(childPosition);
        }
        List<byte[]> initializationData = null;
        String mimeType = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childStartPosition = parent.getPosition();
            int childAtomSize = parent.readInt();
            if (childAtomSize == 0 && parent.getPosition() - position == size) {
                break;
            }
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_avcC) {
                Assertions.checkState(mimeType == null);
                mimeType = "video/avc";
                AvcCData avcCData = parseAvcCFromParent(parent, childStartPosition);
                initializationData = avcCData.initializationData;
                out.nalUnitLengthFieldLength = avcCData.nalUnitLengthFieldLength;
                if (!pixelWidthHeightRatioFromPasp) {
                    pixelWidthHeightRatio = avcCData.pixelWidthAspectRatio;
                }
            } else if (childAtomType == Atom.TYPE_hvcC) {
                Assertions.checkState(mimeType == null);
                mimeType = MimeTypes.VIDEO_H265;
                Pair<List<byte[]>, Integer> hvcCData = parseHvcCFromParent(parent, childStartPosition);
                initializationData = hvcCData.first;
                out.nalUnitLengthFieldLength = ((Integer) hvcCData.second).intValue();
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
            } else if (childAtomType == Atom.TYPE_vpcC) {
                Assertions.checkState(mimeType == null);
                mimeType = atomType == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
            }
            childPosition += childAtomSize;
        }
        if (mimeType != null) {
            out.mediaFormat = MediaFormat.createVideoFormat(Integer.toString(trackId), mimeType, -1, -1, durationUs, width, height, initializationData, rotationDegrees, pixelWidthHeightRatio);
        }
    }

    private static AvcCData parseAvcCFromParent(ParsableByteArray parent, int position) {
        parent.setPosition((position + 8) + 4);
        int nalUnitLengthFieldLength = (parent.readUnsignedByte() & 3) + 1;
        if (nalUnitLengthFieldLength == 3) {
            throw new IllegalStateException();
        }
        int j;
        List<byte[]> initializationData = new ArrayList();
        float pixelWidthAspectRatio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        int numSequenceParameterSets = parent.readUnsignedByte() & 31;
        for (j = 0; j < numSequenceParameterSets; j++) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(parent));
        }
        int numPictureParameterSets = parent.readUnsignedByte();
        for (j = 0; j < numPictureParameterSets; j++) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(parent));
        }
        if (numSequenceParameterSets > 0) {
            ParsableBitArray spsDataBitArray = new ParsableBitArray((byte[]) initializationData.get(0));
            spsDataBitArray.setPosition((nalUnitLengthFieldLength + 1) * 8);
            pixelWidthAspectRatio = NalUnitUtil.parseSpsNalUnit(spsDataBitArray).pixelWidthAspectRatio;
        }
        return new AvcCData(initializationData, nalUnitLengthFieldLength, pixelWidthAspectRatio);
    }

    private static Pair<List<byte[]>, Integer> parseHvcCFromParent(ParsableByteArray parent, int position) {
        int i;
        parent.setPosition((position + 8) + 21);
        int lengthSizeMinusOne = parent.readUnsignedByte() & 3;
        int numberOfArrays = parent.readUnsignedByte();
        int csdLength = 0;
        int csdStartPosition = parent.getPosition();
        for (i = 0; i < numberOfArrays; i++) {
            int j;
            parent.skipBytes(1);
            int numberOfNalUnits = parent.readUnsignedShort();
            for (j = 0; j < numberOfNalUnits; j++) {
                int nalUnitLength = parent.readUnsignedShort();
                csdLength += nalUnitLength + 4;
                parent.skipBytes(nalUnitLength);
            }
        }
        parent.setPosition(csdStartPosition);
        byte[] buffer = new byte[csdLength];
        int bufferPosition = 0;
        for (i = 0; i < numberOfArrays; i++) {
            parent.skipBytes(1);
            numberOfNalUnits = parent.readUnsignedShort();
            for (j = 0; j < numberOfNalUnits; j++) {
                nalUnitLength = parent.readUnsignedShort();
                System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, buffer, bufferPosition, NalUnitUtil.NAL_START_CODE.length);
                bufferPosition += NalUnitUtil.NAL_START_CODE.length;
                System.arraycopy(parent.data, parent.getPosition(), buffer, bufferPosition, nalUnitLength);
                bufferPosition += nalUnitLength;
                parent.skipBytes(nalUnitLength);
            }
        }
        return Pair.create(csdLength == 0 ? null : Collections.singletonList(buffer), Integer.valueOf(lengthSizeMinusOne + 1));
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

    private static void parseAudioSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, long durationUs, String language, boolean isQuickTime, StsdData out, int entryIndex) {
        int channelCount;
        int sampleRate;
        parent.setPosition(position + 8);
        int quickTimeSoundDescriptionVersion = 0;
        if (isQuickTime) {
            parent.skipBytes(8);
            quickTimeSoundDescriptionVersion = parent.readUnsignedShort();
            parent.skipBytes(6);
        } else {
            parent.skipBytes(16);
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
            atomType = parseSampleEntryEncryptionData(parent, position, size, out, entryIndex);
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
                out.mediaFormat = Ac3Util.parseAc3AnnexFFormat(parent, Integer.toString(trackId), durationUs, language);
            } else if (childAtomType == Atom.TYPE_dec3) {
                parent.setPosition(childPosition + 8);
                out.mediaFormat = Ac3Util.parseEAc3AnnexFFormat(parent, Integer.toString(trackId), durationUs, language);
            } else if (childAtomType == Atom.TYPE_ddts) {
                out.mediaFormat = MediaFormat.createAudioFormat(Integer.toString(trackId), mimeType, -1, -1, durationUs, channelCount, sampleRate, null, language);
            }
            childPosition += childAtomSize;
        }
        if (out.mediaFormat == null && mimeType != null) {
            List list;
            int pcmEncoding = MimeTypes.AUDIO_RAW.equals(mimeType) ? 2 : -1;
            String num = Integer.toString(trackId);
            if (initializationData == null) {
                list = null;
            } else {
                list = Collections.singletonList(initializationData);
            }
            out.mediaFormat = MediaFormat.createAudioFormat(num, mimeType, -1, -1, durationUs, channelCount, sampleRate, list, language, pcmEncoding);
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

    private static int parseSampleEntryEncryptionData(ParsableByteArray parent, int position, int size, StsdData out, int entryIndex) {
        boolean z = true;
        int childPosition = parent.getPosition();
        while (childPosition - position < size) {
            boolean z2;
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (childAtomSize > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            Assertions.checkArgument(z2, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_sinf) {
                Pair<Integer, TrackEncryptionBox> result = parseSinfFromParent(parent, childPosition, childAtomSize);
                Integer dataFormat = result.first;
                if (dataFormat == null) {
                    z = false;
                }
                Assertions.checkArgument(z, "frma atom is mandatory");
                out.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) result.second;
                return dataFormat.intValue();
            }
            childPosition += childAtomSize;
        }
        return 0;
    }

    private static Pair<Integer, TrackEncryptionBox> parseSinfFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        TrackEncryptionBox trackEncryptionBox = null;
        Integer dataFormat = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_frma) {
                dataFormat = Integer.valueOf(parent.readInt());
            } else if (childAtomType == Atom.TYPE_schm) {
                parent.skipBytes(4);
                parent.readInt();
                parent.readInt();
            } else if (childAtomType == Atom.TYPE_schi) {
                trackEncryptionBox = parseSchiFromParent(parent, childPosition, childAtomSize);
            }
            childPosition += childAtomSize;
        }
        return Pair.create(dataFormat, trackEncryptionBox);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size) {
        boolean defaultIsEncrypted = true;
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_tenc) {
                parent.skipBytes(6);
                if (parent.readUnsignedByte() != 1) {
                    defaultIsEncrypted = false;
                }
                int defaultInitVectorSize = parent.readUnsignedByte();
                byte[] defaultKeyId = new byte[16];
                parent.readBytes(defaultKeyId, 0, defaultKeyId.length);
                return new TrackEncryptionBox(defaultIsEncrypted, defaultInitVectorSize, defaultKeyId);
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
