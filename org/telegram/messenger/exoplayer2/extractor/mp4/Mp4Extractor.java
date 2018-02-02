package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.C;
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
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new Mp4Extractor()};
        }
    };
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
        if (this.tracks.length == 0) {
            return new SeekPoints(SeekPoint.START);
        }
        TrackSampleTable sampleTable;
        long firstTimeUs;
        long firstOffset;
        long secondTimeUs = C.TIME_UNSET;
        long secondOffset = -1;
        if (this.firstVideoTrackIndex != -1) {
            sampleTable = this.tracks[this.firstVideoTrackIndex].sampleTable;
            int sampleIndex = getSynchronizationSampleIndex(sampleTable, timeUs);
            if (sampleIndex == -1) {
                return new SeekPoints(SeekPoint.START);
            }
            long sampleTimeUs = sampleTable.timestampsUs[sampleIndex];
            firstTimeUs = sampleTimeUs;
            firstOffset = sampleTable.offsets[sampleIndex];
            if (sampleTimeUs < timeUs && sampleIndex < sampleTable.sampleCount - 1) {
                int secondSampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
                if (!(secondSampleIndex == -1 || secondSampleIndex == sampleIndex)) {
                    secondTimeUs = sampleTable.timestampsUs[secondSampleIndex];
                    secondOffset = sampleTable.offsets[secondSampleIndex];
                }
            }
        } else {
            firstTimeUs = timeUs;
            firstOffset = Long.MAX_VALUE;
        }
        for (int i = 0; i < this.tracks.length; i++) {
            if (i != this.firstVideoTrackIndex) {
                sampleTable = this.tracks[i].sampleTable;
                firstOffset = maybeAdjustSeekOffset(sampleTable, firstTimeUs, firstOffset);
                if (secondTimeUs != C.TIME_UNSET) {
                    secondOffset = maybeAdjustSeekOffset(sampleTable, secondTimeUs, secondOffset);
                }
            }
        }
        SeekPoint firstSeekPoint = new SeekPoint(firstTimeUs, firstOffset);
        if (secondTimeUs == C.TIME_UNSET) {
            return new SeekPoints(firstSeekPoint);
        }
        return new SeekPoints(firstSeekPoint, new SeekPoint(secondTimeUs, secondOffset));
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        long endPosition;
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
            endPosition = input.getLength();
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
            endPosition = (input.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.add(new ContainerAtom(this.atomType, endPosition));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(endPosition);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            boolean z;
            if (this.atomHeaderBytesRead == 8) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
            if (this.atomSize <= 2147483647L) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
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
        if (!seekRequired || this.parserState == 2) {
            return false;
        }
        return true;
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
        int firstVideoTrackIndex = -1;
        long durationUs = C.TIME_UNSET;
        List<Mp4Track> tracks = new ArrayList();
        Metadata metadata = null;
        GaplessInfoHolder gaplessInfoHolder = new GaplessInfoHolder();
        LeafAtom udta = moov.getLeafAtomOfType(Atom.TYPE_udta);
        if (udta != null) {
            metadata = AtomParsers.parseUdta(udta, this.isQuickTime);
            if (metadata != null) {
                gaplessInfoHolder.setFromMetadata(metadata);
            }
        }
        for (int i = 0; i < moov.containerChildren.size(); i++) {
            ContainerAtom atom = (ContainerAtom) moov.containerChildren.get(i);
            if (atom.type == Atom.TYPE_trak) {
                Track track = AtomParsers.parseTrak(atom, moov.getLeafAtomOfType(Atom.TYPE_mvhd), C.TIME_UNSET, null, (this.flags & 1) != 0, this.isQuickTime);
                if (track != null) {
                    TrackSampleTable trackSampleTable = AtomParsers.parseStbl(track, atom.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), gaplessInfoHolder);
                    if (trackSampleTable.sampleCount != 0) {
                        Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, this.extractorOutput.track(i, track.type));
                        Format format = track.format.copyWithMaxInputSize(trackSampleTable.maximumSize + 30);
                        if (track.type == 1) {
                            if (gaplessInfoHolder.hasGaplessInfo()) {
                                format = format.copyWithGaplessInfo(gaplessInfoHolder.encoderDelay, gaplessInfoHolder.encoderPadding);
                            }
                            if (metadata != null) {
                                format = format.copyWithMetadata(metadata);
                            }
                        }
                        mp4Track.trackOutput.format(format);
                        durationUs = Math.max(durationUs, track.durationUs);
                        if (track.type == 2 && firstVideoTrackIndex == -1) {
                            firstVideoTrackIndex = tracks.size();
                        }
                        tracks.add(mp4Track);
                    }
                }
            }
        }
        this.firstVideoTrackIndex = firstVideoTrackIndex;
        this.durationUs = durationUs;
        this.tracks = (Mp4Track[]) tracks.toArray(new Mp4Track[tracks.size()]);
        this.accumulatedSampleSizes = calculateAccumulatedSampleSizes(this.tracks);
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(this);
    }

    private int readSample(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long inputPosition = input.getPosition();
        if (this.sampleTrackIndex == -1) {
            this.sampleTrackIndex = getTrackIndexOfNextReadSample(inputPosition);
            if (this.sampleTrackIndex == -1) {
                return -1;
            }
        }
        Mp4Track track = this.tracks[this.sampleTrackIndex];
        TrackOutput trackOutput = track.trackOutput;
        int sampleIndex = track.sampleIndex;
        long position = track.sampleTable.offsets[sampleIndex];
        int sampleSize = track.sampleTable.sizes[sampleIndex];
        long skipAmount = (position - inputPosition) + ((long) this.sampleBytesWritten);
        if (skipAmount < 0 || skipAmount >= RELOAD_MINIMUM_SEEK_DISTANCE) {
            positionHolder.position = position;
            return 1;
        }
        if (track.track.sampleTransformation == 1) {
            skipAmount += 8;
            sampleSize -= 8;
        }
        input.skipFully((int) skipAmount);
        int writtenBytes;
        if (track.track.nalUnitLengthFieldLength != 0) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            int nalUnitLengthFieldLength = track.track.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track.track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    input.readFully(this.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                    this.nalLength.setPosition(0);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(0);
                    trackOutput.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                    sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    writtenBytes = trackOutput.sampleData(input, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += writtenBytes;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes;
                }
            }
        } else {
            while (this.sampleBytesWritten < sampleSize) {
                writtenBytes = trackOutput.sampleData(input, sampleSize - this.sampleBytesWritten, false);
                this.sampleBytesWritten += writtenBytes;
                this.sampleCurrentNalBytesRemaining -= writtenBytes;
            }
        }
        trackOutput.sampleMetadata(track.sampleTable.timestampsUs[sampleIndex], track.sampleTable.flags[sampleIndex], sampleSize, 0, null);
        track.sampleIndex++;
        this.sampleTrackIndex = -1;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        return 0;
    }

    private int getTrackIndexOfNextReadSample(long inputPosition) {
        long preferredSkipAmount = Long.MAX_VALUE;
        boolean preferredRequiresReload = true;
        int preferredTrackIndex = -1;
        long preferredAccumulatedBytes = Long.MAX_VALUE;
        long minAccumulatedBytes = Long.MAX_VALUE;
        boolean minAccumulatedBytesRequiresReload = true;
        int minAccumulatedBytesTrackIndex = -1;
        for (int trackIndex = 0; trackIndex < this.tracks.length; trackIndex++) {
            Mp4Track track = this.tracks[trackIndex];
            int sampleIndex = track.sampleIndex;
            if (sampleIndex != track.sampleTable.sampleCount) {
                long sampleOffset = track.sampleTable.offsets[sampleIndex];
                long sampleAccumulatedBytes = this.accumulatedSampleSizes[trackIndex][sampleIndex];
                long skipAmount = sampleOffset - inputPosition;
                boolean requiresReload = skipAmount < 0 || skipAmount >= RELOAD_MINIMUM_SEEK_DISTANCE;
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
        return (minAccumulatedBytes == Long.MAX_VALUE || !minAccumulatedBytesRequiresReload || preferredAccumulatedBytes < MAXIMUM_READ_AHEAD_BYTES_STREAM + minAccumulatedBytes) ? preferredTrackIndex : minAccumulatedBytesTrackIndex;
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
        long[][] accumulatedSampleSizes = new long[tracks.length][];
        int[] nextSampleIndex = new int[tracks.length];
        long[] nextSampleTimesUs = new long[tracks.length];
        boolean[] tracksFinished = new boolean[tracks.length];
        for (i = 0; i < tracks.length; i++) {
            accumulatedSampleSizes[i] = new long[tracks[i].sampleTable.sampleCount];
            nextSampleTimesUs[i] = tracks[i].sampleTable.timestampsUs[0];
        }
        long accumulatedSampleSize = 0;
        int finishedTracks = 0;
        while (finishedTracks < tracks.length) {
            long minTimeUs = Long.MAX_VALUE;
            int minTimeTrackIndex = -1;
            i = 0;
            while (i < tracks.length) {
                if (!tracksFinished[i] && nextSampleTimesUs[i] <= minTimeUs) {
                    minTimeTrackIndex = i;
                    minTimeUs = nextSampleTimesUs[i];
                }
                i++;
            }
            int trackSampleIndex = nextSampleIndex[minTimeTrackIndex];
            accumulatedSampleSizes[minTimeTrackIndex][trackSampleIndex] = accumulatedSampleSize;
            accumulatedSampleSize += (long) tracks[minTimeTrackIndex].sampleTable.sizes[trackSampleIndex];
            trackSampleIndex++;
            nextSampleIndex[minTimeTrackIndex] = trackSampleIndex;
            if (trackSampleIndex < accumulatedSampleSizes[minTimeTrackIndex].length) {
                nextSampleTimesUs[minTimeTrackIndex] = tracks[minTimeTrackIndex].sampleTable.timestampsUs[trackSampleIndex];
            } else {
                tracksFinished[minTimeTrackIndex] = true;
                finishedTracks++;
            }
        }
        return accumulatedSampleSizes;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable sampleTable, long seekTimeUs, long offset) {
        int sampleIndex = getSynchronizationSampleIndex(sampleTable, seekTimeUs);
        return sampleIndex == -1 ? offset : Math.min(sampleTable.offsets[sampleIndex], offset);
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
        return atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_hdlr || atom == Atom.TYPE_stsd || atom == Atom.TYPE_stts || atom == Atom.TYPE_stss || atom == Atom.TYPE_ctts || atom == Atom.TYPE_elst || atom == Atom.TYPE_stsc || atom == Atom.TYPE_stsz || atom == Atom.TYPE_stz2 || atom == Atom.TYPE_stco || atom == Atom.TYPE_co64 || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_ftyp || atom == Atom.TYPE_udta;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_edts;
    }
}
