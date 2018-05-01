package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.C0542C;
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
    public static final ExtractorsFactory FACTORY = new C18411();
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

        public Mp4Track(Track track, TrackSampleTable trackSampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = trackSampleTable;
            this.trackOutput = trackOutput;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mp4.Mp4Extractor$1 */
    static class C18411 implements ExtractorsFactory {
        C18411() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new Mp4Extractor()};
        }
    }

    public boolean isSeekable() {
        return true;
    }

    public void release() {
    }

    public Mp4Extractor() {
        this(0);
    }

    public Mp4Extractor(int i) {
        this.flags = i;
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new Stack();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleTrackIndex = -1;
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return Sniffer.sniffUnfragmented(extractorInput);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    public void seek(long j, long j2) {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = 0;
        this.sampleTrackIndex = -1;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        if (j == 0) {
            enterReadingAtomHeaderState();
        } else if (this.tracks != null) {
            updateSampleIndices(j2);
        }
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case 0:
                    if (readAtomHeader(extractorInput)) {
                        break;
                    }
                    return -1;
                case 1:
                    if (!readAtomPayload(extractorInput, positionHolder)) {
                        break;
                    }
                    return 1;
                case 2:
                    return readSample(extractorInput, positionHolder);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public SeekPoints getSeekPoints(long j) {
        if (this.tracks.length == 0) {
            return new SeekPoints(SeekPoint.START);
        }
        int synchronizationSampleIndex;
        long j2;
        long j3;
        long j4;
        long j5;
        if (this.firstVideoTrackIndex != -1) {
            TrackSampleTable trackSampleTable = this.tracks[this.firstVideoTrackIndex].sampleTable;
            synchronizationSampleIndex = getSynchronizationSampleIndex(trackSampleTable, j);
            if (synchronizationSampleIndex == -1) {
                return new SeekPoints(SeekPoint.START);
            }
            j2 = trackSampleTable.timestampsUs[synchronizationSampleIndex];
            j3 = trackSampleTable.offsets[synchronizationSampleIndex];
            if (j2 < j && synchronizationSampleIndex < trackSampleTable.sampleCount - 1) {
                j = trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
                if (!(j == -1 || j == synchronizationSampleIndex)) {
                    j4 = trackSampleTable.timestampsUs[j];
                    j = trackSampleTable.offsets[j];
                    j5 = j;
                    j = j2;
                }
            }
            j = -1;
            j4 = C0542C.TIME_UNSET;
            j5 = j;
            j = j2;
        } else {
            j3 = Long.MAX_VALUE;
            j5 = -1;
            j4 = C0542C.TIME_UNSET;
        }
        for (synchronizationSampleIndex = 0; synchronizationSampleIndex < this.tracks.length; synchronizationSampleIndex++) {
            if (synchronizationSampleIndex != this.firstVideoTrackIndex) {
                TrackSampleTable trackSampleTable2 = this.tracks[synchronizationSampleIndex].sampleTable;
                j2 = maybeAdjustSeekOffset(trackSampleTable2, j, j3);
                if (j4 != C0542C.TIME_UNSET) {
                    j5 = maybeAdjustSeekOffset(trackSampleTable2, j4, j5);
                }
                j3 = j2;
            }
        }
        SeekPoint seekPoint = new SeekPoint(j, j3);
        if (j4 == C0542C.TIME_UNSET) {
            return new SeekPoints(seekPoint);
        }
        return new SeekPoints(seekPoint, new SeekPoint(j4, j5));
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        long length;
        if (this.atomHeaderBytesRead == 0) {
            if (!extractorInput.readFully(this.atomHeader.data, 0, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            extractorInput.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (this.atomSize == 0) {
            length = extractorInput.getLength();
            if (length == -1 && !this.containerAtoms.isEmpty()) {
                length = ((ContainerAtom) this.containerAtoms.peek()).endPosition;
            }
            if (length != -1) {
                this.atomSize = (length - extractorInput.getPosition()) + ((long) this.atomHeaderBytesRead);
            }
        }
        if (this.atomSize < ((long) this.atomHeaderBytesRead)) {
            throw new ParserException("Atom size less than header length (unsupported).");
        }
        if (shouldParseContainerAtom(this.atomType)) {
            length = (extractorInput.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.add(new ContainerAtom(this.atomType, length));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(length);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType) != null) {
            Assertions.checkState(this.atomHeaderBytesRead == 8 ? 1 : null);
            Assertions.checkState(this.atomSize <= 2147483647L ? 1 : null);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
            this.parserState = 1;
        } else {
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        long j = this.atomSize - ((long) this.atomHeaderBytesRead);
        long position = extractorInput.getPosition() + j;
        if (this.atomData != null) {
            extractorInput.readFully(this.atomData.data, this.atomHeaderBytesRead, (int) j);
            if (this.atomType == Atom.TYPE_ftyp) {
                this.isQuickTime = processFtypAtom(this.atomData);
            } else if (this.containerAtoms.isEmpty() == null) {
                ((ContainerAtom) this.containerAtoms.peek()).add(new LeafAtom(this.atomType, this.atomData));
            }
        } else if (j < RELOAD_MINIMUM_SEEK_DISTANCE) {
            extractorInput.skipFully((int) j);
        } else {
            positionHolder.position = extractorInput.getPosition() + j;
            extractorInput = 1;
            processAtomEnded(position);
            if (extractorInput != null || this.parserState == 2) {
                return false;
            }
            return true;
        }
        extractorInput = null;
        processAtomEnded(position);
        if (extractorInput != null) {
        }
        return false;
    }

    private void processAtomEnded(long j) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == j) {
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

    private void processMoovAtom(ContainerAtom containerAtom) throws ParserException {
        Metadata parseUdta;
        Mp4Extractor mp4Extractor = this;
        ContainerAtom containerAtom2 = containerAtom;
        List arrayList = new ArrayList();
        GaplessInfoHolder gaplessInfoHolder = new GaplessInfoHolder();
        LeafAtom leafAtomOfType = containerAtom2.getLeafAtomOfType(Atom.TYPE_udta);
        if (leafAtomOfType != null) {
            parseUdta = AtomParsers.parseUdta(leafAtomOfType, mp4Extractor.isQuickTime);
            if (parseUdta != null) {
                gaplessInfoHolder.setFromMetadata(parseUdta);
            }
        } else {
            parseUdta = null;
        }
        long j = C0542C.TIME_UNSET;
        int i = -1;
        for (int i2 = 0; i2 < containerAtom2.containerChildren.size(); i2++) {
            ContainerAtom containerAtom3 = (ContainerAtom) containerAtom2.containerChildren.get(i2);
            if (containerAtom3.type == Atom.TYPE_trak) {
                Track parseTrak = AtomParsers.parseTrak(containerAtom3, containerAtom2.getLeafAtomOfType(Atom.TYPE_mvhd), C0542C.TIME_UNSET, null, (mp4Extractor.flags & 1) != 0, mp4Extractor.isQuickTime);
                if (parseTrak != null) {
                    TrackSampleTable parseStbl = AtomParsers.parseStbl(parseTrak, containerAtom3.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), gaplessInfoHolder);
                    if (parseStbl.sampleCount != 0) {
                        Mp4Track mp4Track = new Mp4Track(parseTrak, parseStbl, mp4Extractor.extractorOutput.track(i2, parseTrak.type));
                        Format copyWithMaxInputSize = parseTrak.format.copyWithMaxInputSize(parseStbl.maximumSize + 30);
                        if (parseTrak.type == 1) {
                            if (gaplessInfoHolder.hasGaplessInfo()) {
                                copyWithMaxInputSize = copyWithMaxInputSize.copyWithGaplessInfo(gaplessInfoHolder.encoderDelay, gaplessInfoHolder.encoderPadding);
                            }
                            if (parseUdta != null) {
                                copyWithMaxInputSize = copyWithMaxInputSize.copyWithMetadata(parseUdta);
                            }
                        }
                        mp4Track.trackOutput.format(copyWithMaxInputSize);
                        j = Math.max(j, parseTrak.durationUs);
                        if (parseTrak.type == 2 && i == -1) {
                            i = arrayList.size();
                        }
                        arrayList.add(mp4Track);
                    }
                }
            }
        }
        mp4Extractor.firstVideoTrackIndex = i;
        mp4Extractor.durationUs = j;
        mp4Extractor.tracks = (Mp4Track[]) arrayList.toArray(new Mp4Track[arrayList.size()]);
        mp4Extractor.accumulatedSampleSizes = calculateAccumulatedSampleSizes(mp4Extractor.tracks);
        mp4Extractor.extractorOutput.endTracks();
        mp4Extractor.extractorOutput.seekMap(mp4Extractor);
    }

    private int readSample(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        long position = extractorInput.getPosition();
        if (this.sampleTrackIndex == -1) {
            this.sampleTrackIndex = getTrackIndexOfNextReadSample(position);
            if (this.sampleTrackIndex == -1) {
                return -1;
            }
        }
        Mp4Track mp4Track = this.tracks[this.sampleTrackIndex];
        TrackOutput trackOutput = mp4Track.trackOutput;
        int i = mp4Track.sampleIndex;
        long j = mp4Track.sampleTable.offsets[i];
        int i2 = mp4Track.sampleTable.sizes[i];
        long j2 = (j - position) + ((long) this.sampleBytesWritten);
        if (j2 >= 0) {
            if (j2 < RELOAD_MINIMUM_SEEK_DISTANCE) {
                if (mp4Track.track.sampleTransformation == 1) {
                    i2 -= 8;
                    j2 += 8;
                }
                extractorInput.skipFully((int) j2);
                if (mp4Track.track.nalUnitLengthFieldLength != null) {
                    positionHolder = this.nalLength.data;
                    positionHolder[0] = null;
                    positionHolder[1] = null;
                    positionHolder[2] = null;
                    positionHolder = mp4Track.track.nalUnitLengthFieldLength;
                    int i3 = 4 - mp4Track.track.nalUnitLengthFieldLength;
                    while (this.sampleBytesWritten < i2) {
                        if (this.sampleCurrentNalBytesRemaining == 0) {
                            extractorInput.readFully(this.nalLength.data, i3, positionHolder);
                            this.nalLength.setPosition(0);
                            this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                            this.nalStartCode.setPosition(0);
                            trackOutput.sampleData(this.nalStartCode, 4);
                            this.sampleBytesWritten += 4;
                            i2 += i3;
                        } else {
                            int sampleData = trackOutput.sampleData(extractorInput, this.sampleCurrentNalBytesRemaining, false);
                            this.sampleBytesWritten += sampleData;
                            this.sampleCurrentNalBytesRemaining -= sampleData;
                        }
                    }
                } else {
                    while (this.sampleBytesWritten < i2) {
                        positionHolder = trackOutput.sampleData(extractorInput, i2 - this.sampleBytesWritten, false);
                        this.sampleBytesWritten += positionHolder;
                        this.sampleCurrentNalBytesRemaining -= positionHolder;
                    }
                }
                int i4 = i2;
                trackOutput.sampleMetadata(mp4Track.sampleTable.timestampsUs[i], mp4Track.sampleTable.flags[i], i4, 0, null);
                mp4Track.sampleIndex += 1;
                this.sampleTrackIndex = -1;
                this.sampleBytesWritten = 0;
                this.sampleCurrentNalBytesRemaining = 0;
                return 0;
            }
        }
        positionHolder.position = j;
        return 1;
    }

    private int getTrackIndexOfNextReadSample(long j) {
        Mp4Extractor mp4Extractor = this;
        int i = -1;
        int i2 = i;
        long j2 = Long.MAX_VALUE;
        Object obj = 1;
        long j3 = Long.MAX_VALUE;
        Object obj2 = 1;
        long j4 = Long.MAX_VALUE;
        for (int i3 = 0; i3 < mp4Extractor.tracks.length; i3++) {
            Mp4Track mp4Track = mp4Extractor.tracks[i3];
            int i4 = mp4Track.sampleIndex;
            if (i4 != mp4Track.sampleTable.sampleCount) {
                Object obj3;
                long j5 = mp4Track.sampleTable.offsets[i4];
                long j6 = mp4Extractor.accumulatedSampleSizes[i3][i4];
                long j7 = j5 - j;
                if (j7 >= 0) {
                    if (j7 < RELOAD_MINIMUM_SEEK_DISTANCE) {
                        obj3 = null;
                        if ((obj3 == null && r13 != null) || (obj3 == r13 && j7 < r14)) {
                            obj2 = obj3;
                            i = i3;
                            j3 = j6;
                            j4 = j7;
                        }
                        if (j6 < j2) {
                            obj = obj3;
                            i2 = i3;
                            j2 = j6;
                        }
                    }
                }
                obj3 = 1;
                obj2 = obj3;
                i = i3;
                j3 = j6;
                j4 = j7;
                if (j6 < j2) {
                    obj = obj3;
                    i2 = i3;
                    j2 = j6;
                }
            }
        }
        if (j2 == Long.MAX_VALUE || r8 == null) {
            return i;
        }
        return j3 < j2 + MAXIMUM_READ_AHEAD_BYTES_STREAM ? i : i2;
    }

    private void updateSampleIndices(long j) {
        for (Mp4Track mp4Track : this.tracks) {
            TrackSampleTable trackSampleTable = mp4Track.sampleTable;
            int indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(j);
            if (indexOfEarlierOrEqualSynchronizationSample == -1) {
                indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
            }
            mp4Track.sampleIndex = indexOfEarlierOrEqualSynchronizationSample;
        }
    }

    private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] mp4TrackArr) {
        int i;
        long[][] jArr = new long[mp4TrackArr.length][];
        int[] iArr = new int[mp4TrackArr.length];
        long[] jArr2 = new long[mp4TrackArr.length];
        boolean[] zArr = new boolean[mp4TrackArr.length];
        for (i = 0; i < mp4TrackArr.length; i++) {
            jArr[i] = new long[mp4TrackArr[i].sampleTable.sampleCount];
            jArr2[i] = mp4TrackArr[i].sampleTable.timestampsUs[0];
        }
        long j = 0;
        i = 0;
        while (i < mp4TrackArr.length) {
            int i2 = -1;
            long j2 = Long.MAX_VALUE;
            int i3 = 0;
            while (i3 < mp4TrackArr.length) {
                if (!zArr[i3] && jArr2[i3] <= r11) {
                    j2 = jArr2[i3];
                    i2 = i3;
                }
                i3++;
            }
            i3 = iArr[i2];
            jArr[i2][i3] = j;
            long j3 = j + ((long) mp4TrackArr[i2].sampleTable.sizes[i3]);
            i3++;
            iArr[i2] = i3;
            if (i3 < jArr[i2].length) {
                jArr2[i2] = mp4TrackArr[i2].sampleTable.timestampsUs[i3];
            } else {
                zArr[i2] = true;
                i++;
            }
            j = j3;
        }
        return jArr;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable trackSampleTable, long j, long j2) {
        j = getSynchronizationSampleIndex(trackSampleTable, j);
        if (j == -1) {
            return j2;
        }
        return Math.min(trackSampleTable.offsets[j], j2);
    }

    private static int getSynchronizationSampleIndex(TrackSampleTable trackSampleTable, long j) {
        int indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(j);
        return indexOfEarlierOrEqualSynchronizationSample == -1 ? trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j) : indexOfEarlierOrEqualSynchronizationSample;
    }

    private static boolean processFtypAtom(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        if (parsableByteArray.readInt() == BRAND_QUICKTIME) {
            return true;
        }
        parsableByteArray.skipBytes(4);
        while (parsableByteArray.bytesLeft() > 0) {
            if (parsableByteArray.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return null;
    }

    private static boolean shouldParseLeafAtom(int i) {
        if (!(i == Atom.TYPE_mdhd || i == Atom.TYPE_mvhd || i == Atom.TYPE_hdlr || i == Atom.TYPE_stsd || i == Atom.TYPE_stts || i == Atom.TYPE_stss || i == Atom.TYPE_ctts || i == Atom.TYPE_elst || i == Atom.TYPE_stsc || i == Atom.TYPE_stsz || i == Atom.TYPE_stz2 || i == Atom.TYPE_stco || i == Atom.TYPE_co64 || i == Atom.TYPE_tkhd || i == Atom.TYPE_ftyp)) {
            if (i != Atom.TYPE_udta) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldParseContainerAtom(int i) {
        if (!(i == Atom.TYPE_moov || i == Atom.TYPE_trak || i == Atom.TYPE_mdia || i == Atom.TYPE_minf || i == Atom.TYPE_stbl)) {
            if (i != Atom.TYPE_edts) {
                return false;
            }
        }
        return true;
    }
}
