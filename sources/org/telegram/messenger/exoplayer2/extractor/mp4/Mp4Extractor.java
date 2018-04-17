package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Mp4Extractor implements Extractor, SeekMap {
    private static final int BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
    public static final ExtractorsFactory FACTORY = new C18351();
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
    private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 10485760;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private long[][] accumulatedSampleSizes;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private int firstVideoTrackIndex;
    private final int flags;
    private boolean isQuickTime;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleTrackIndex;
    private Mp4Track[] tracks;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    private static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;

        public Mp4Track(Track track, TrackSampleTable sampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = sampleTable;
            this.trackOutput = trackOutput;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mp4.Mp4Extractor$1 */
    static class C18351 implements ExtractorsFactory {
        C18351() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new Mp4Extractor()};
        }
    }

    public Mp4Extractor() {
        this(0);
    }

    public Mp4Extractor(int flags) {
        this.flags = flags;
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new Stack();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleTrackIndex = -1;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffUnfragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek(long position, long timeUs) {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = 0;
        this.sampleTrackIndex = -1;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        if (position == 0) {
            enterReadingAtomHeaderState();
        } else if (this.tracks != null) {
            updateSampleIndices(timeUs);
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case 0:
                    if (readAtomHeader(input)) {
                        break;
                    }
                    return -1;
                case 1:
                    if (!readAtomPayload(input, seekPosition)) {
                        break;
                    }
                    return 1;
                case 2:
                    return readSample(input, seekPosition);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean isSeekable() {
        return true;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        long j = timeUs;
        if (this.tracks.length == 0) {
            return new SeekPoints(SeekPoint.START);
        }
        int sampleIndex;
        long firstTimeUs;
        long firstOffset;
        long secondTimeUs = C0539C.TIME_UNSET;
        long secondOffset = -1;
        if (r0.firstVideoTrackIndex != -1) {
            TrackSampleTable sampleTable = r0.tracks[r0.firstVideoTrackIndex].sampleTable;
            sampleIndex = getSynchronizationSampleIndex(sampleTable, j);
            if (sampleIndex == -1) {
                return new SeekPoints(SeekPoint.START);
            }
            long sampleTimeUs = sampleTable.timestampsUs[sampleIndex];
            firstTimeUs = sampleTimeUs;
            firstOffset = sampleTable.offsets[sampleIndex];
            if (sampleTimeUs < j && sampleIndex < sampleTable.sampleCount - 1) {
                int secondSampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
                if (!(secondSampleIndex == -1 || secondSampleIndex == sampleIndex)) {
                    secondTimeUs = sampleTable.timestampsUs[secondSampleIndex];
                    secondOffset = sampleTable.offsets[secondSampleIndex];
                }
            }
        } else {
            firstTimeUs = j;
            firstOffset = Long.MAX_VALUE;
        }
        long firstOffset2 = firstOffset;
        for (sampleIndex = 0; sampleIndex < r0.tracks.length; sampleIndex++) {
            if (sampleIndex != r0.firstVideoTrackIndex) {
                TrackSampleTable sampleTable2 = r0.tracks[sampleIndex].sampleTable;
                firstOffset2 = maybeAdjustSeekOffset(sampleTable2, firstTimeUs, firstOffset2);
                if (secondTimeUs != C0539C.TIME_UNSET) {
                    secondOffset = maybeAdjustSeekOffset(sampleTable2, secondTimeUs, secondOffset);
                }
            }
        }
        SeekPoint firstSeekPoint = new SeekPoint(firstTimeUs, firstOffset2);
        if (secondTimeUs == C0539C.TIME_UNSET) {
            return new SeekPoints(firstSeekPoint);
        }
        return new SeekPoints(firstSeekPoint, new SeekPoint(secondTimeUs, secondOffset));
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, 0, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (this.atomSize == 0) {
            long endPosition = input.getLength();
            if (endPosition == -1 && !this.containerAtoms.isEmpty()) {
                endPosition = ((ContainerAtom) this.containerAtoms.peek()).endPosition;
            }
            if (endPosition != -1) {
                this.atomSize = (endPosition - input.getPosition()) + ((long) this.atomHeaderBytesRead);
            }
        }
        if (this.atomSize < ((long) this.atomHeaderBytesRead)) {
            throw new ParserException("Atom size less than header length (unsupported).");
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long endPosition2 = (input.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.add(new ContainerAtom(this.atomType, endPosition2));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(endPosition2);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            Assertions.checkState(this.atomHeaderBytesRead == 8);
            Assertions.checkState(this.atomSize <= 2147483647L);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
            this.parserState = 1;
        } else {
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long atomPayloadSize = this.atomSize - ((long) this.atomHeaderBytesRead);
        long atomEndPosition = input.getPosition() + atomPayloadSize;
        boolean seekRequired = false;
        if (this.atomData != null) {
            input.readFully(this.atomData.data, this.atomHeaderBytesRead, (int) atomPayloadSize);
            if (this.atomType == Atom.TYPE_ftyp) {
                this.isQuickTime = processFtypAtom(this.atomData);
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(new LeafAtom(this.atomType, this.atomData));
            }
        } else if (atomPayloadSize < RELOAD_MINIMUM_SEEK_DISTANCE) {
            input.skipFully((int) atomPayloadSize);
        } else {
            positionHolder.position = input.getPosition() + atomPayloadSize;
            seekRequired = true;
        }
        processAtomEnded(atomEndPosition);
        return seekRequired && this.parserState != 2;
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            ContainerAtom containerAtom = (ContainerAtom) this.containerAtoms.pop();
            if (containerAtom.type == Atom.TYPE_moov) {
                processMoovAtom(containerAtom);
                this.containerAtoms.clear();
                this.parserState = 2;
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(containerAtom);
            }
        }
        if (this.parserState != 2) {
            enterReadingAtomHeaderState();
        }
    }

    private void processMoovAtom(ContainerAtom moov) throws ParserException {
        LeafAtom udta;
        GaplessInfoHolder gaplessInfoHolder;
        Mp4Extractor mp4Extractor = this;
        ContainerAtom containerAtom = moov;
        List<Mp4Track> tracks = new ArrayList();
        Metadata metadata = null;
        GaplessInfoHolder gaplessInfoHolder2 = new GaplessInfoHolder();
        LeafAtom udta2 = containerAtom.getLeafAtomOfType(Atom.TYPE_udta);
        if (udta2 != null) {
            metadata = AtomParsers.parseUdta(udta2, mp4Extractor.isQuickTime);
            if (metadata != null) {
                gaplessInfoHolder2.setFromMetadata(metadata);
            }
        }
        long durationUs = C0539C.TIME_UNSET;
        int firstVideoTrackIndex = -1;
        int i = 0;
        while (i < containerAtom.containerChildren.size()) {
            Metadata metadata2;
            ContainerAtom atom = (ContainerAtom) containerAtom.containerChildren.get(i);
            if (atom.type == Atom.TYPE_trak) {
                Track track = AtomParsers.parseTrak(atom, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), C0539C.TIME_UNSET, null, (mp4Extractor.flags & 1) != 0, mp4Extractor.isQuickTime);
                if (track != null) {
                    TrackSampleTable trackSampleTable = AtomParsers.parseStbl(track, atom.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), gaplessInfoHolder2);
                    if (trackSampleTable.sampleCount != 0) {
                        Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, mp4Extractor.extractorOutput.track(i, track.type));
                        Format format = track.format.copyWithMaxInputSize(trackSampleTable.maximumSize + 30);
                        udta = udta2;
                        if (track.type == 1) {
                            if (gaplessInfoHolder2.hasGaplessInfo()) {
                                format = format.copyWithGaplessInfo(gaplessInfoHolder2.encoderDelay, gaplessInfoHolder2.encoderPadding);
                            }
                            if (metadata != null) {
                                format = format.copyWithMetadata(metadata);
                            }
                        }
                        mp4Track.trackOutput.format(format);
                        metadata2 = metadata;
                        gaplessInfoHolder = gaplessInfoHolder2;
                        metadata = Math.max(durationUs, track.durationUs);
                        if (track.type == 2 && firstVideoTrackIndex == -1) {
                            firstVideoTrackIndex = tracks.size();
                        }
                        tracks.add(mp4Track);
                        durationUs = metadata;
                        i++;
                        udta2 = udta;
                        gaplessInfoHolder2 = gaplessInfoHolder;
                        metadata = metadata2;
                        containerAtom = moov;
                    }
                }
            }
            metadata2 = metadata;
            gaplessInfoHolder = gaplessInfoHolder2;
            udta = udta2;
            i++;
            udta2 = udta;
            gaplessInfoHolder2 = gaplessInfoHolder;
            metadata = metadata2;
            containerAtom = moov;
        }
        gaplessInfoHolder = gaplessInfoHolder2;
        udta = udta2;
        mp4Extractor.firstVideoTrackIndex = firstVideoTrackIndex;
        mp4Extractor.durationUs = durationUs;
        mp4Extractor.tracks = (Mp4Track[]) tracks.toArray(new Mp4Track[tracks.size()]);
        mp4Extractor.accumulatedSampleSizes = calculateAccumulatedSampleSizes(mp4Extractor.tracks);
        mp4Extractor.extractorOutput.endTracks();
        mp4Extractor.extractorOutput.seekMap(mp4Extractor);
    }

    private int readSample(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        int i;
        long position;
        ExtractorInput extractorInput = input;
        long inputPosition = input.getPosition();
        if (this.sampleTrackIndex == -1) {
            r0.sampleTrackIndex = getTrackIndexOfNextReadSample(inputPosition);
            if (r0.sampleTrackIndex == -1) {
                return -1;
            }
        }
        Mp4Track track = r0.tracks[r0.sampleTrackIndex];
        TrackOutput trackOutput = track.trackOutput;
        int sampleIndex = track.sampleIndex;
        long position2 = track.sampleTable.offsets[sampleIndex];
        int sampleSize = track.sampleTable.sizes[sampleIndex];
        long skipAmount = (position2 - inputPosition) + ((long) r0.sampleBytesWritten);
        if (skipAmount < 0) {
            i = 1;
            position = position2;
        } else if (skipAmount >= RELOAD_MINIMUM_SEEK_DISTANCE) {
            r19 = inputPosition;
            i = 1;
            position = position2;
        } else {
            long skipAmount2;
            if (track.track.sampleTransformation == 1) {
                sampleSize -= 8;
                skipAmount2 = skipAmount + 8;
            } else {
                skipAmount2 = skipAmount;
            }
            extractorInput.skipFully((int) skipAmount2);
            if (track.track.nalUnitLengthFieldLength != 0) {
                byte[] nalLengthData = r0.nalLength.data;
                nalLengthData[0] = (byte) 0;
                nalLengthData[1] = (byte) 0;
                nalLengthData[2] = (byte) 0;
                int nalUnitLengthFieldLength = track.track.nalUnitLengthFieldLength;
                int nalUnitLengthFieldLengthDiff = 4 - track.track.nalUnitLengthFieldLength;
                while (r0.sampleBytesWritten < sampleSize) {
                    if (r0.sampleCurrentNalBytesRemaining == 0) {
                        extractorInput.readFully(r0.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                        r19 = inputPosition;
                        r0.nalLength.setPosition(0);
                        r0.sampleCurrentNalBytesRemaining = r0.nalLength.readUnsignedIntToInt();
                        r0.nalStartCode.setPosition(0);
                        i = 4;
                        trackOutput.sampleData(r0.nalStartCode, 4);
                        r0.sampleBytesWritten += 4;
                        sampleSize += nalUnitLengthFieldLengthDiff;
                    } else {
                        r19 = inputPosition;
                        i = 4;
                        inputPosition = trackOutput.sampleData(extractorInput, r0.sampleCurrentNalBytesRemaining, false);
                        r0.sampleBytesWritten += inputPosition;
                        r0.sampleCurrentNalBytesRemaining -= inputPosition;
                    }
                    int i2 = i;
                    inputPosition = r19;
                }
            } else {
                while (r0.sampleBytesWritten < sampleSize) {
                    inputPosition = trackOutput.sampleData(extractorInput, sampleSize - r0.sampleBytesWritten, false);
                    r0.sampleBytesWritten += inputPosition;
                    r0.sampleCurrentNalBytesRemaining -= inputPosition;
                }
            }
            inputPosition = sampleSize;
            long j = skipAmount2;
            trackOutput.sampleMetadata(track.sampleTable.timestampsUs[sampleIndex], track.sampleTable.flags[sampleIndex], inputPosition, 0, null);
            track.sampleIndex++;
            r0.sampleTrackIndex = -1;
            r0.sampleBytesWritten = 0;
            r0.sampleCurrentNalBytesRemaining = 0;
            return 0;
        }
        positionHolder.position = position;
        return i;
    }

    private int getTrackIndexOfNextReadSample(long inputPosition) {
        Mp4Extractor mp4Extractor = this;
        int preferredTrackIndex = -1;
        long preferredAccumulatedBytes = Long.MAX_VALUE;
        long minAccumulatedBytes = Long.MAX_VALUE;
        boolean minAccumulatedBytesRequiresReload = true;
        int minAccumulatedBytesTrackIndex = -1;
        boolean preferredRequiresReload = true;
        long preferredSkipAmount = Long.MAX_VALUE;
        for (int trackIndex = 0; trackIndex < mp4Extractor.tracks.length; trackIndex++) {
            Mp4Track track = mp4Extractor.tracks[trackIndex];
            int sampleIndex = track.sampleIndex;
            if (sampleIndex != track.sampleTable.sampleCount) {
                boolean requiresReload;
                long sampleOffset = track.sampleTable.offsets[sampleIndex];
                long sampleAccumulatedBytes = mp4Extractor.accumulatedSampleSizes[trackIndex][sampleIndex];
                long skipAmount = sampleOffset - inputPosition;
                if (skipAmount >= 0) {
                    if (skipAmount < RELOAD_MINIMUM_SEEK_DISTANCE) {
                        requiresReload = false;
                        if ((!requiresReload && preferredRequiresReload) || (requiresReload == preferredRequiresReload && skipAmount < preferredSkipAmount)) {
                            preferredRequiresReload = requiresReload;
                            preferredSkipAmount = skipAmount;
                            preferredTrackIndex = trackIndex;
                            preferredAccumulatedBytes = sampleAccumulatedBytes;
                        }
                        if (sampleAccumulatedBytes < minAccumulatedBytes) {
                            minAccumulatedBytes = sampleAccumulatedBytes;
                            minAccumulatedBytesRequiresReload = requiresReload;
                            minAccumulatedBytesTrackIndex = trackIndex;
                        }
                    }
                }
                requiresReload = true;
                preferredRequiresReload = requiresReload;
                preferredSkipAmount = skipAmount;
                preferredTrackIndex = trackIndex;
                preferredAccumulatedBytes = sampleAccumulatedBytes;
                if (sampleAccumulatedBytes < minAccumulatedBytes) {
                    minAccumulatedBytes = sampleAccumulatedBytes;
                    minAccumulatedBytesRequiresReload = requiresReload;
                    minAccumulatedBytesTrackIndex = trackIndex;
                }
            }
        }
        if (minAccumulatedBytes != Long.MAX_VALUE && minAccumulatedBytesRequiresReload) {
            if (preferredAccumulatedBytes >= minAccumulatedBytes + MAXIMUM_READ_AHEAD_BYTES_STREAM) {
                return minAccumulatedBytesTrackIndex;
            }
        }
        return preferredTrackIndex;
    }

    private void updateSampleIndices(long timeUs) {
        for (Mp4Track track : this.tracks) {
            TrackSampleTable sampleTable = track.sampleTable;
            int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
            if (sampleIndex == -1) {
                sampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
            }
            track.sampleIndex = sampleIndex;
        }
    }

    private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] tracks) {
        int i;
        Mp4Track[] mp4TrackArr = tracks;
        long[][] accumulatedSampleSizes = new long[mp4TrackArr.length][];
        int[] nextSampleIndex = new int[mp4TrackArr.length];
        long[] nextSampleTimesUs = new long[mp4TrackArr.length];
        boolean[] tracksFinished = new boolean[mp4TrackArr.length];
        for (i = 0; i < mp4TrackArr.length; i++) {
            accumulatedSampleSizes[i] = new long[mp4TrackArr[i].sampleTable.sampleCount];
            nextSampleTimesUs[i] = mp4TrackArr[i].sampleTable.timestampsUs[0];
        }
        long accumulatedSampleSize = 0;
        i = 0;
        while (i < mp4TrackArr.length) {
            int minTimeTrackIndex = -1;
            long minTimeUs = Long.MAX_VALUE;
            int i2 = 0;
            while (i2 < mp4TrackArr.length) {
                if (!tracksFinished[i2] && nextSampleTimesUs[i2] <= r12) {
                    int minTimeTrackIndex2 = i2;
                    minTimeUs = nextSampleTimesUs[i2];
                    minTimeTrackIndex = minTimeTrackIndex2;
                }
                i2++;
            }
            i2 = nextSampleIndex[minTimeTrackIndex];
            accumulatedSampleSizes[minTimeTrackIndex][i2] = accumulatedSampleSize;
            long accumulatedSampleSize2 = accumulatedSampleSize + ((long) mp4TrackArr[minTimeTrackIndex].sampleTable.sizes[i2]);
            i2++;
            nextSampleIndex[minTimeTrackIndex] = i2;
            if (i2 < accumulatedSampleSizes[minTimeTrackIndex].length) {
                nextSampleTimesUs[minTimeTrackIndex] = mp4TrackArr[minTimeTrackIndex].sampleTable.timestampsUs[i2];
            } else {
                tracksFinished[minTimeTrackIndex] = true;
                i++;
            }
            accumulatedSampleSize = accumulatedSampleSize2;
        }
        return accumulatedSampleSizes;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable sampleTable, long seekTimeUs, long offset) {
        int sampleIndex = getSynchronizationSampleIndex(sampleTable, seekTimeUs);
        if (sampleIndex == -1) {
            return offset;
        }
        return Math.min(sampleTable.offsets[sampleIndex], offset);
    }

    private static int getSynchronizationSampleIndex(TrackSampleTable sampleTable, long timeUs) {
        int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
        if (sampleIndex == -1) {
            return sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
        }
        return sampleIndex;
    }

    private static boolean processFtypAtom(ParsableByteArray atomData) {
        atomData.setPosition(8);
        if (atomData.readInt() == BRAND_QUICKTIME) {
            return true;
        }
        atomData.skipBytes(4);
        while (atomData.bytesLeft() > 0) {
            if (atomData.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        if (!(atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_hdlr || atom == Atom.TYPE_stsd || atom == Atom.TYPE_stts || atom == Atom.TYPE_stss || atom == Atom.TYPE_ctts || atom == Atom.TYPE_elst || atom == Atom.TYPE_stsc || atom == Atom.TYPE_stsz || atom == Atom.TYPE_stz2 || atom == Atom.TYPE_stco || atom == Atom.TYPE_co64 || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_ftyp)) {
            if (atom != Atom.TYPE_udta) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        if (!(atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl)) {
            if (atom != Atom.TYPE_edts) {
                return false;
            }
        }
        return true;
    }
}
