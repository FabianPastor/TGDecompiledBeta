package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.text.cea.CeaUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class FragmentedMp4Extractor implements Extractor {
    private static final Format EMSG_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);
    public static final ExtractorsFactory FACTORY = new C18401();
    public static final int FLAG_ENABLE_EMSG_TRACK = 4;
    private static final int FLAG_SIDELOADED = 8;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 16;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[]{(byte) -94, (byte) 57, (byte) 79, (byte) 82, (byte) 90, (byte) -101, (byte) 79, (byte) 20, (byte) -94, (byte) 68, (byte) 108, (byte) 66, (byte) 124, (byte) 100, (byte) -115, (byte) -12};
    private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private final TrackOutput additionalEmsgTrackOutput;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private TrackOutput[] cea608TrackOutputs;
    private final List<Format> closedCaptionFormats;
    private final Stack<ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private final ParsableByteArray defaultInitializationVector;
    private long durationUs;
    private TrackOutput[] emsgTrackOutputs;
    private final ParsableByteArray encryptionSignalByte;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalBuffer;
    private final ParsableByteArray nalPrefix;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int pendingMetadataSampleBytes;
    private final ArrayDeque<MetadataSampleInfo> pendingMetadataSampleInfos;
    private boolean processSeiNalUnitPayload;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private long segmentIndexEarliestPresentationTimeUs;
    private final DrmInitData sideloadedDrmInitData;
    private final Track sideloadedTrack;
    private final TimestampAdjuster timestampAdjuster;
    private final SparseArray<TrackBundle> trackBundles;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    private static final class MetadataSampleInfo {
        public final long presentationTimeDeltaUs;
        public final int size;

        public MetadataSampleInfo(long presentationTimeDeltaUs, int size) {
            this.presentationTimeDeltaUs = presentationTimeDeltaUs;
            this.size = size;
        }
    }

    private static final class TrackBundle {
        public int currentSampleInTrackRun;
        public int currentSampleIndex;
        public int currentTrackRunIndex;
        public DefaultSampleValues defaultSampleValues;
        public final TrackFragment fragment = new TrackFragment();
        public final TrackOutput output;
        public Track track;

        public TrackBundle(TrackOutput output) {
            this.output = output;
        }

        public void init(Track track, DefaultSampleValues defaultSampleValues) {
            this.track = (Track) Assertions.checkNotNull(track);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues);
            this.output.format(track.format);
            reset();
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = 0;
            this.currentTrackRunIndex = 0;
            this.currentSampleInTrackRun = 0;
        }

        public void updateDrmInitData(DrmInitData drmInitData) {
            TrackEncryptionBox encryptionBox = this.track.getSampleDescriptionEncryptionBox(this.fragment.header.sampleDescriptionIndex);
            this.output.format(this.track.format.copyWithDrmInitData(drmInitData.copyWithSchemeType(encryptionBox != null ? encryptionBox.schemeType : null)));
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor$1 */
    static class C18401 implements ExtractorsFactory {
        C18401() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new FragmentedMp4Extractor()};
        }
    }

    public FragmentedMp4Extractor() {
        this(0);
    }

    public FragmentedMp4Extractor(int flags) {
        this(flags, null);
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster) {
        this(flags, timestampAdjuster, null, null);
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack, DrmInitData sideloadedDrmInitData) {
        this(flags, timestampAdjuster, sideloadedTrack, sideloadedDrmInitData, Collections.emptyList());
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack, DrmInitData sideloadedDrmInitData, List<Format> closedCaptionFormats) {
        this(flags, timestampAdjuster, sideloadedTrack, sideloadedDrmInitData, closedCaptionFormats, null);
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack, DrmInitData sideloadedDrmInitData, List<Format> closedCaptionFormats, TrackOutput additionalEmsgTrackOutput) {
        this.flags = (sideloadedTrack != null ? 8 : 0) | flags;
        this.timestampAdjuster = timestampAdjuster;
        this.sideloadedTrack = sideloadedTrack;
        this.sideloadedDrmInitData = sideloadedDrmInitData;
        this.closedCaptionFormats = Collections.unmodifiableList(closedCaptionFormats);
        this.additionalEmsgTrackOutput = additionalEmsgTrackOutput;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalPrefix = new ParsableByteArray(5);
        this.nalBuffer = new ParsableByteArray();
        this.encryptionSignalByte = new ParsableByteArray(1);
        this.defaultInitializationVector = new ParsableByteArray();
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new Stack();
        this.pendingMetadataSampleInfos = new ArrayDeque();
        this.trackBundles = new SparseArray();
        this.durationUs = C0542C.TIME_UNSET;
        this.segmentIndexEarliestPresentationTimeUs = C0542C.TIME_UNSET;
        enterReadingAtomHeaderState();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        if (this.sideloadedTrack != null) {
            TrackBundle bundle = new TrackBundle(output.track(0, this.sideloadedTrack.type));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
            this.trackBundles.put(0, bundle);
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
        }
    }

    public void seek(long position, long timeUs) {
        int trackCount = this.trackBundles.size();
        for (int i = 0; i < trackCount; i++) {
            ((TrackBundle) this.trackBundles.valueAt(i)).reset();
        }
        this.pendingMetadataSampleInfos.clear();
        this.pendingMetadataSampleBytes = 0;
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
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
                    readAtomPayload(input);
                    break;
                case 2:
                    readEncryptionData(input);
                    break;
                default:
                    if (!readSample(input)) {
                        break;
                    }
                    return 0;
            }
        }
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
        long atomPosition = input.getPosition() - ((long) this.atomHeaderBytesRead);
        if (this.atomType == Atom.TYPE_moof) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                TrackFragment fragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
                fragment.atomPosition = atomPosition;
                fragment.auxiliaryDataPosition = atomPosition;
                fragment.dataPosition = atomPosition;
            }
        }
        if (this.atomType == Atom.TYPE_mdat) {
            this.currentTrackBundle = null;
            this.endOfMdatPosition = atomPosition + this.atomSize;
            if (!this.haveOutputSeekMap) {
                this.extractorOutput.seekMap(new Unseekable(this.durationUs, atomPosition));
                this.haveOutputSeekMap = true;
            }
            this.parserState = 2;
            return true;
        }
        if (shouldParseContainerAtom(this.atomType)) {
            endPosition = (input.getPosition() + this.atomSize) - 8;
            this.containerAtoms.add(new ContainerAtom(this.atomType, endPosition));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(endPosition);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            if (this.atomHeaderBytesRead != 8) {
                throw new ParserException("Leaf atom defines extended atom size (unsupported).");
            } else if (this.atomSize > 2147483647L) {
                throw new ParserException("Leaf atom with length > NUM (unsupported).");
            } else {
                this.atomData = new ParsableByteArray((int) this.atomSize);
                System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
                this.parserState = 1;
            }
        } else if (this.atomSize > 2147483647L) {
            throw new ParserException("Skipping atom with length > NUM (unsupported).");
        } else {
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
        int atomPayloadSize = ((int) this.atomSize) - this.atomHeaderBytesRead;
        if (this.atomData != null) {
            input.readFully(this.atomData.data, 8, atomPayloadSize);
            onLeafAtomRead(new LeafAtom(this.atomType, this.atomData), input.getPosition());
        } else {
            input.skipFully(atomPayloadSize);
        }
        processAtomEnded(input.getPosition());
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            onContainerAtomRead((ContainerAtom) this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private void onLeafAtomRead(LeafAtom leaf, long inputPosition) throws ParserException {
        if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(leaf);
        } else if (leaf.type == Atom.TYPE_sidx) {
            Pair<Long, ChunkIndex> result = parseSidx(leaf.data, inputPosition);
            this.segmentIndexEarliestPresentationTimeUs = ((Long) result.first).longValue();
            this.extractorOutput.seekMap((SeekMap) result.second);
            this.haveOutputSeekMap = true;
        } else if (leaf.type == Atom.TYPE_emsg) {
            onEmsgLeafAtomRead(leaf.data);
        }
    }

    private void onContainerAtomRead(ContainerAtom container) throws ParserException {
        if (container.type == Atom.TYPE_moov) {
            onMoovContainerAtomRead(container);
        } else if (container.type == Atom.TYPE_moof) {
            onMoofContainerAtomRead(container);
        } else if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(container);
        }
    }

    private void onMoovContainerAtomRead(ContainerAtom moov) throws ParserException {
        DrmInitData drmInitData;
        int i;
        SparseArray<Track> tracks;
        ContainerAtom containerAtom = moov;
        boolean z = true;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        if (r0.sideloadedDrmInitData != null) {
            drmInitData = r0.sideloadedDrmInitData;
        } else {
            drmInitData = getDrmInitDataFromAtoms(containerAtom.leafChildren);
        }
        DrmInitData drmInitData2 = drmInitData;
        ContainerAtom mvex = containerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray();
        int mvexChildrenSize = mvex.leafChildren.size();
        long duration = C0542C.TIME_UNSET;
        for (i = 0; i < mvexChildrenSize; i++) {
            LeafAtom atom = (LeafAtom) mvex.leafChildren.get(i);
            if (atom.type == Atom.TYPE_trex) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                defaultSampleValuesArray.put(((Integer) trexData.first).intValue(), trexData.second);
            } else if (atom.type == Atom.TYPE_mehd) {
                duration = parseMehd(atom.data);
            }
        }
        SparseArray<Track> tracks2 = new SparseArray();
        int moovContainerChildrenSize = containerAtom.containerChildren.size();
        i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= moovContainerChildrenSize) {
                break;
            }
            int i3;
            int moovContainerChildrenSize2;
            ContainerAtom atom2 = (ContainerAtom) containerAtom.containerChildren.get(i2);
            if (atom2.type == Atom.TYPE_trak) {
                i3 = i2;
                moovContainerChildrenSize2 = moovContainerChildrenSize;
                tracks = tracks2;
                Track track = AtomParsers.parseTrak(atom2, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), duration, drmInitData2, (r0.flags & 16) != 0, null);
                if (track != null) {
                    tracks.put(track.id, track);
                }
            } else {
                i3 = i2;
                moovContainerChildrenSize2 = moovContainerChildrenSize;
                tracks = tracks2;
            }
            i = i3 + 1;
            tracks2 = tracks;
            moovContainerChildrenSize = moovContainerChildrenSize2;
        }
        tracks = tracks2;
        i = tracks.size();
        int i4;
        if (r0.trackBundles.size() == 0) {
            i4 = 0;
            while (true) {
                int i5 = i4;
                if (i5 < i) {
                    Track track2 = (Track) tracks.valueAt(i5);
                    TrackBundle trackBundle = new TrackBundle(r0.extractorOutput.track(i5, track2.type));
                    trackBundle.init(track2, (DefaultSampleValues) defaultSampleValuesArray.get(track2.id));
                    r0.trackBundles.put(track2.id, trackBundle);
                    ContainerAtom mvex2 = mvex;
                    r0.durationUs = Math.max(r0.durationUs, track2.durationUs);
                    i4 = i5 + 1;
                    mvex = mvex2;
                    containerAtom = moov;
                } else {
                    maybeInitExtraTracks();
                    r0.extractorOutput.endTracks();
                    return;
                }
            }
        }
        if (r0.trackBundles.size() != i) {
            z = false;
        }
        Assertions.checkState(z);
        i4 = 0;
        while (true) {
            int i6 = i4;
            if (i6 < i) {
                Track track3 = (Track) tracks.valueAt(i6);
                ((TrackBundle) r0.trackBundles.get(track3.id)).init(track3, (DefaultSampleValues) defaultSampleValuesArray.get(track3.id));
                i4 = i6 + 1;
            } else {
                return;
            }
        }
    }

    private void onMoofContainerAtomRead(ContainerAtom moof) throws ParserException {
        DrmInitData drmInitData;
        parseMoof(moof, this.trackBundles, this.flags, this.extendedTypeScratch);
        if (this.sideloadedDrmInitData != null) {
            drmInitData = null;
        } else {
            drmInitData = getDrmInitDataFromAtoms(moof.leafChildren);
        }
        if (drmInitData != null) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                ((TrackBundle) this.trackBundles.valueAt(i)).updateDrmInitData(drmInitData);
            }
        }
    }

    private void maybeInitExtraTracks() {
        int i;
        int i2 = 0;
        if (this.emsgTrackOutputs == null) {
            int emsgTrackOutputCount;
            int emsgTrackOutputCount2;
            this.emsgTrackOutputs = new TrackOutput[2];
            i = 0;
            if (this.additionalEmsgTrackOutput != null) {
                emsgTrackOutputCount = 0 + 1;
                this.emsgTrackOutputs[0] = this.additionalEmsgTrackOutput;
                i = emsgTrackOutputCount;
            }
            if ((this.flags & 4) != 0) {
                emsgTrackOutputCount2 = i + 1;
                this.emsgTrackOutputs[i] = this.extractorOutput.track(this.trackBundles.size(), 4);
                i = emsgTrackOutputCount2;
            }
            this.emsgTrackOutputs = (TrackOutput[]) Arrays.copyOf(this.emsgTrackOutputs, i);
            for (TrackOutput eventMessageTrackOutput : this.emsgTrackOutputs) {
                eventMessageTrackOutput.format(EMSG_FORMAT);
            }
        }
        if (this.cea608TrackOutputs == null) {
            this.cea608TrackOutputs = new TrackOutput[this.closedCaptionFormats.size()];
            while (true) {
                i = i2;
                if (i < this.cea608TrackOutputs.length) {
                    TrackOutput output = this.extractorOutput.track((this.trackBundles.size() + 1) + i, 3);
                    output.format((Format) this.closedCaptionFormats.get(i));
                    this.cea608TrackOutputs[i] = output;
                    i2 = i + 1;
                } else {
                    return;
                }
            }
        }
    }

    private void onEmsgLeafAtomRead(ParsableByteArray atom) {
        ParsableByteArray parsableByteArray = atom;
        if (this.emsgTrackOutputs.length != 0) {
            parsableByteArray.setPosition(12);
            int sampleSize = atom.bytesLeft();
            atom.readNullTerminatedString();
            atom.readNullTerminatedString();
            long presentationTimeDeltaUs = Util.scaleLargeTimestamp(atom.readUnsignedInt(), C0542C.MICROS_PER_SECOND, atom.readUnsignedInt());
            for (TrackOutput emsgTrackOutput : r0.emsgTrackOutputs) {
                parsableByteArray.setPosition(12);
                emsgTrackOutput.sampleData(parsableByteArray, sampleSize);
            }
            if (r0.segmentIndexEarliestPresentationTimeUs != C0542C.TIME_UNSET) {
                TrackOutput[] trackOutputArr = r0.emsgTrackOutputs;
                int length = trackOutputArr.length;
                int i = 0;
                while (i < length) {
                    int i2 = i;
                    trackOutputArr[i].sampleMetadata(r0.segmentIndexEarliestPresentationTimeUs + presentationTimeDeltaUs, 1, sampleSize, 0, null);
                    i = i2 + 1;
                }
            } else {
                r0.pendingMetadataSampleInfos.addLast(new MetadataSampleInfo(presentationTimeDeltaUs, sampleSize));
                r0.pendingMetadataSampleBytes += sampleSize;
            }
        }
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
        trex.setPosition(12);
        return Pair.create(Integer.valueOf(trex.readInt()), new DefaultSampleValues(trex.readUnsignedIntToInt() - 1, trex.readUnsignedIntToInt(), trex.readUnsignedIntToInt(), trex.readInt()));
    }

    private static long parseMehd(ParsableByteArray mehd) {
        mehd.setPosition(8);
        return Atom.parseFullAtomVersion(mehd.readInt()) == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
    }

    private static void parseMoof(ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        int moofContainerChildrenSize = moof.containerChildren.size();
        for (int i = 0; i < moofContainerChildrenSize; i++) {
            ContainerAtom child = (ContainerAtom) moof.containerChildren.get(i);
            if (child.type == Atom.TYPE_traf) {
                parseTraf(child, trackBundleArray, flags, extendedTypeScratch);
            }
        }
    }

    private static void parseTraf(ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        ContainerAtom containerAtom = traf;
        int i = flags;
        LeafAtom tfhd = containerAtom.getLeafAtomOfType(Atom.TYPE_tfhd);
        TrackBundle trackBundle = parseTfhd(tfhd.data, trackBundleArray, i);
        if (trackBundle != null) {
            TrackFragment fragment = trackBundle.fragment;
            long decodeTime = fragment.nextFragmentDecodeTime;
            trackBundle.reset();
            if (containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (i & 2) == 0) {
                decodeTime = parseTfdt(containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTruns(containerAtom, trackBundle, decodeTime, i);
            TrackEncryptionBox encryptionBox = trackBundle.track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
            LeafAtom saiz = containerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
            if (saiz != null) {
                parseSaiz(encryptionBox, saiz.data, fragment);
            }
            LeafAtom saio = containerAtom.getLeafAtomOfType(Atom.TYPE_saio);
            if (saio != null) {
                parseSaio(saio.data, fragment);
            }
            LeafAtom senc = containerAtom.getLeafAtomOfType(Atom.TYPE_senc);
            if (senc != null) {
                parseSenc(senc.data, fragment);
            }
            LeafAtom sbgp = containerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
            LeafAtom sgpd = containerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
            if (sbgp == null || sgpd == null) {
            } else {
                String str;
                ParsableByteArray parsableByteArray = sbgp.data;
                ParsableByteArray parsableByteArray2 = sgpd.data;
                if (encryptionBox != null) {
                    str = encryptionBox.schemeType;
                } else {
                    str = null;
                }
                parseSgpd(parsableByteArray, parsableByteArray2, str, fragment);
            }
            i = containerAtom.leafChildren.size();
            int i2 = 0;
            while (i2 < i) {
                LeafAtom atom = (LeafAtom) containerAtom.leafChildren.get(i2);
                int leafChildrenSize = i;
                if (atom.type == Atom.TYPE_uuid) {
                    parseUuid(atom.data, fragment, extendedTypeScratch);
                } else {
                    byte[] bArr = extendedTypeScratch;
                }
                i2++;
                i = leafChildrenSize;
                containerAtom = traf;
            }
            i = extendedTypeScratch;
        }
    }

    private static void parseTruns(ContainerAtom traf, TrackBundle trackBundle, long decodeTime, int flags) {
        TrackBundle trackBundle2 = trackBundle;
        List<LeafAtom> leafChildren = traf.leafChildren;
        int leafChildrenSize = leafChildren.size();
        int i = 0;
        int trunCount = 0;
        int totalSampleCount = 0;
        for (int i2 = 0; i2 < leafChildrenSize; i2++) {
            LeafAtom atom = (LeafAtom) leafChildren.get(i2);
            if (atom.type == Atom.TYPE_trun) {
                ParsableByteArray trunData = atom.data;
                trunData.setPosition(12);
                int trunSampleCount = trunData.readUnsignedIntToInt();
                if (trunSampleCount > 0) {
                    totalSampleCount += trunSampleCount;
                    trunCount++;
                }
            }
        }
        trackBundle2.currentTrackRunIndex = 0;
        trackBundle2.currentSampleInTrackRun = 0;
        trackBundle2.currentSampleIndex = 0;
        trackBundle2.fragment.initTables(trunCount, totalSampleCount);
        int trunStartPosition = 0;
        int trunIndex = 0;
        while (true) {
            int i3 = i;
            if (i3 < leafChildrenSize) {
                LeafAtom trun = (LeafAtom) leafChildren.get(i3);
                if (trun.type == Atom.TYPE_trun) {
                    int trunIndex2 = trunIndex + 1;
                    trunStartPosition = parseTrun(trackBundle2, trunIndex, decodeTime, flags, trun.data, trunStartPosition);
                    trunIndex = trunIndex2;
                }
                i = i3 + 1;
            } else {
                return;
            }
        }
    }

    private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz, TrackFragment out) throws ParserException {
        int vectorSize = encryptionBox.initializationVectorSize;
        saiz.setPosition(8);
        boolean subsampleEncryption = true;
        if ((Atom.parseFullAtomFlags(saiz.readInt()) & 1) == 1) {
            saiz.skipBytes(8);
        }
        int defaultSampleInfoSize = saiz.readUnsignedByte();
        int sampleCount = saiz.readUnsignedIntToInt();
        if (sampleCount != out.sampleCount) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length mismatch: ");
            stringBuilder.append(sampleCount);
            stringBuilder.append(", ");
            stringBuilder.append(out.sampleCount);
            throw new ParserException(stringBuilder.toString());
        }
        int totalSize;
        if (defaultSampleInfoSize == 0) {
            boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
            totalSize = 0;
            for (int totalSize2 = 0; totalSize2 < sampleCount; totalSize2++) {
                int sampleInfoSize = saiz.readUnsignedByte();
                totalSize += sampleInfoSize;
                sampleHasSubsampleEncryptionTable[totalSize2] = sampleInfoSize > vectorSize;
            }
        } else {
            if (defaultSampleInfoSize <= vectorSize) {
                subsampleEncryption = false;
            }
            totalSize = 0 + (defaultSampleInfoSize * sampleCount);
            Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
        }
        out.initEncryptionData(totalSize);
    }

    private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
        saio.setPosition(8);
        int fullAtom = saio.readInt();
        if ((Atom.parseFullAtomFlags(fullAtom) & 1) == 1) {
            saio.skipBytes(8);
        }
        int entryCount = saio.readUnsignedIntToInt();
        if (entryCount != 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected saio entry count: ");
            stringBuilder.append(entryCount);
            throw new ParserException(stringBuilder.toString());
        }
        out.auxiliaryDataPosition += Atom.parseFullAtomVersion(fullAtom) == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong();
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles, int flags) {
        tfhd.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(tfhd.readInt());
        TrackBundle trackBundle = (TrackBundle) trackBundles.get((flags & 8) == 0 ? tfhd.readInt() : 0);
        if (trackBundle == null) {
            return null;
        }
        if ((atomFlags & 1) != 0) {
            long baseDataPosition = tfhd.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = baseDataPosition;
            trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        trackBundle.fragment.header = new DefaultSampleValues((atomFlags & 2) != 0 ? tfhd.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex, (atomFlags & 8) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.duration, (atomFlags & 16) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.size, (atomFlags & 32) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.flags);
        return trackBundle;
    }

    private static long parseTfdt(ParsableByteArray tfdt) {
        tfdt.setPosition(8);
        return Atom.parseFullAtomVersion(tfdt.readInt()) == 1 ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
    }

    private static int parseTrun(TrackBundle trackBundle, int index, long decodeTime, int flags, ParsableByteArray trun, int trackRunStart) {
        int firstSampleFlags;
        boolean[] sampleIsSyncFrameTable;
        Track track;
        boolean firstSampleFlagsPresent;
        DefaultSampleValues defaultSampleValues;
        boolean sampleDurationsPresent;
        boolean sampleSizesPresent;
        TrackBundle trackBundle2 = trackBundle;
        trun.setPosition(8);
        int fullAtom = trun.readInt();
        int atomFlags = Atom.parseFullAtomFlags(fullAtom);
        Track track2 = trackBundle2.track;
        TrackFragment fragment = trackBundle2.fragment;
        DefaultSampleValues defaultSampleValues2 = fragment.header;
        fragment.trunLength[index] = trun.readUnsignedIntToInt();
        fragment.trunDataPosition[index] = fragment.dataPosition;
        if ((atomFlags & 1) != 0) {
            long[] jArr = fragment.trunDataPosition;
            jArr[index] = jArr[index] + ((long) trun.readInt());
        }
        boolean firstSampleFlagsPresent2 = (atomFlags & 4) != 0;
        int firstSampleFlags2 = defaultSampleValues2.flags;
        if (firstSampleFlagsPresent2) {
            firstSampleFlags2 = trun.readUnsignedIntToInt();
        }
        boolean sampleDurationsPresent2 = (atomFlags & 256) != 0;
        boolean sampleSizesPresent2 = (atomFlags & 512) != 0;
        boolean sampleFlagsPresent = (atomFlags & 1024) != 0;
        boolean sampleCompositionTimeOffsetsPresent = (atomFlags & 2048) != 0;
        long edtsOffset = 0;
        if (track2.editListDurations != null && track2.editListDurations.length == 1 && track2.editListDurations[0] == 0) {
            firstSampleFlags = firstSampleFlags2;
            edtsOffset = Util.scaleLargeTimestamp(track2.editListMediaTimes[0], 1000, track2.timescale);
        } else {
            firstSampleFlags = firstSampleFlags2;
        }
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
        long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
        long cumulativeTime = fragment.sampleIsSyncFrameTable;
        boolean workaroundEveryVideoFrameIsSyncFrame = track2.type == 2 && (flags & 1) != 0;
        fullAtom = trackRunStart + fragment.trunLength[index];
        int[] sampleSizeTable2 = sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable2 = sampleCompositionTimeOffsetTable;
        long timescale = track2.timescale;
        if (index > 0) {
            sampleIsSyncFrameTable = cumulativeTime;
            cumulativeTime = fragment.nextFragmentDecodeTime;
        } else {
            sampleIsSyncFrameTable = cumulativeTime;
            cumulativeTime = decodeTime;
        }
        long cumulativeTime2 = cumulativeTime;
        int i = trackRunStart;
        while (i < fullAtom) {
            int readUnsignedIntToInt;
            int sampleFlags;
            boolean z;
            int sampleDuration = sampleDurationsPresent2 ? trun.readUnsignedIntToInt() : defaultSampleValues2.duration;
            if (sampleSizesPresent2) {
                readUnsignedIntToInt = trun.readUnsignedIntToInt();
                track = track2;
            } else {
                track = track2;
                readUnsignedIntToInt = defaultSampleValues2.size;
            }
            int sampleSize = readUnsignedIntToInt;
            if (i == 0 && firstSampleFlagsPresent2) {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                sampleFlags = firstSampleFlags;
            } else if (sampleFlagsPresent) {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                sampleFlags = trun.readInt();
            } else {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                sampleFlags = defaultSampleValues2.flags;
            }
            if (sampleCompositionTimeOffsetsPresent) {
                defaultSampleValues = defaultSampleValues2;
                sampleDurationsPresent = sampleDurationsPresent2;
                sampleSizesPresent = sampleSizesPresent2;
                sampleCompositionTimeOffsetTable2[i] = (int) ((((long) trun.readInt()) * 1000) / timescale);
                z = false;
            } else {
                defaultSampleValues = defaultSampleValues2;
                sampleDurationsPresent = sampleDurationsPresent2;
                sampleSizesPresent = sampleSizesPresent2;
                z = false;
                sampleCompositionTimeOffsetTable2[i] = 0;
            }
            sampleDecodingTimeTable[i] = Util.scaleLargeTimestamp(cumulativeTime2, 1000, timescale) - edtsOffset;
            sampleSizeTable2[i] = sampleSize;
            boolean z2 = (((sampleFlags >> 16) & 1) != 0 || (workaroundEveryVideoFrameIsSyncFrame && i != 0)) ? z : true;
            sampleIsSyncFrameTable[i] = z2;
            cumulativeTime2 += (long) sampleDuration;
            i++;
            track2 = track;
            firstSampleFlagsPresent2 = firstSampleFlagsPresent;
            defaultSampleValues2 = defaultSampleValues;
            sampleDurationsPresent2 = sampleDurationsPresent;
            sampleSizesPresent2 = sampleSizesPresent;
            workaroundEveryVideoFrameIsSyncFrame = workaroundEveryVideoFrameIsSyncFrame;
            ParsableByteArray parsableByteArray = trun;
        }
        track = track2;
        defaultSampleValues = defaultSampleValues2;
        firstSampleFlagsPresent = firstSampleFlagsPresent2;
        sampleDurationsPresent = sampleDurationsPresent2;
        sampleSizesPresent = sampleSizesPresent2;
        fragment.nextFragmentDecodeTime = cumulativeTime2;
        return fullAtom;
    }

    private static void parseUuid(ParsableByteArray uuid, TrackFragment out, byte[] extendedTypeScratch) throws ParserException {
        uuid.setPosition(8);
        uuid.readBytes(extendedTypeScratch, 0, 16);
        if (Arrays.equals(extendedTypeScratch, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            parseSenc(uuid, 16, out);
        }
    }

    private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
        parseSenc(senc, 0, out);
    }

    private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out) throws ParserException {
        senc.setPosition(8 + offset);
        int flags = Atom.parseFullAtomFlags(senc.readInt());
        if ((flags & 1) != 0) {
            throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
        }
        boolean subsampleEncryption = (flags & 2) != 0;
        int sampleCount = senc.readUnsignedIntToInt();
        if (sampleCount != out.sampleCount) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length mismatch: ");
            stringBuilder.append(sampleCount);
            stringBuilder.append(", ");
            stringBuilder.append(out.sampleCount);
            throw new ParserException(stringBuilder.toString());
        }
        Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
        out.initEncryptionData(senc.bytesLeft());
        out.fillEncryptionData(senc);
    }

    private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, String schemeType, TrackFragment out) throws ParserException {
        ParsableByteArray parsableByteArray = sbgp;
        ParsableByteArray parsableByteArray2 = sgpd;
        TrackFragment trackFragment = out;
        parsableByteArray.setPosition(8);
        int sbgpFullAtom = sbgp.readInt();
        if (sbgp.readInt() == SAMPLE_GROUP_TYPE_seig) {
            if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
                parsableByteArray.skipBytes(4);
            }
            if (sbgp.readInt() != 1) {
                throw new ParserException("Entry count in sbgp != 1 (unsupported).");
            }
            parsableByteArray2.setPosition(8);
            int sgpdFullAtom = sgpd.readInt();
            if (sgpd.readInt() == SAMPLE_GROUP_TYPE_seig) {
                int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
                if (sgpdVersion == 1) {
                    if (sgpd.readUnsignedInt() == 0) {
                        throw new ParserException("Variable length description in sgpd found (unsupported)");
                    }
                } else if (sgpdVersion >= 2) {
                    parsableByteArray2.skipBytes(4);
                }
                if (sgpd.readUnsignedInt() != 1) {
                    throw new ParserException("Entry count in sgpd != 1 (unsupported).");
                }
                parsableByteArray2.skipBytes(1);
                int patternByte = sgpd.readUnsignedByte();
                int cryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                int skipByteBlock = patternByte & 15;
                boolean isProtected = sgpd.readUnsignedByte() == 1;
                if (isProtected) {
                    int perSampleIvSize = sgpd.readUnsignedByte();
                    byte[] keyId = new byte[16];
                    parsableByteArray2.readBytes(keyId, 0, keyId.length);
                    byte[] constantIv = null;
                    if (isProtected && perSampleIvSize == 0) {
                        int constantIvSize = sgpd.readUnsignedByte();
                        constantIv = new byte[constantIvSize];
                        parsableByteArray2.readBytes(constantIv, 0, constantIvSize);
                    }
                    byte[] constantIv2 = constantIv;
                    trackFragment.definesEncryptionData = true;
                    trackFragment.trackEncryptionBox = new TrackEncryptionBox(isProtected, schemeType, perSampleIvSize, keyId, cryptByteBlock, skipByteBlock, constantIv2);
                }
            }
        }
    }

    private static Pair<Long, ChunkIndex> parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long earliestPresentationTime;
        long offset;
        ParsableByteArray parsableByteArray = atom;
        parsableByteArray.setPosition(8);
        int fullAtom = atom.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        parsableByteArray.skipBytes(4);
        long timescale = atom.readUnsignedInt();
        long offset2 = inputPosition;
        if (version == 0) {
            earliestPresentationTime = atom.readUnsignedInt();
            offset = offset2 + atom.readUnsignedInt();
        } else {
            earliestPresentationTime = atom.readUnsignedLongToLong();
            offset = offset2 + atom.readUnsignedLongToLong();
        }
        long offset3 = offset;
        offset = earliestPresentationTime;
        long earliestPresentationTimeUs = Util.scaleLargeTimestamp(offset, C0542C.MICROS_PER_SECOND, timescale);
        parsableByteArray.skipBytes(2);
        int referenceCount = atom.readUnsignedShort();
        int[] sizes = new int[referenceCount];
        long[] offsets = new long[referenceCount];
        long[] durationsUs = new long[referenceCount];
        long[] timesUs = new long[referenceCount];
        long time = offset;
        long timeUs = earliestPresentationTimeUs;
        int i = 0;
        while (true) {
            int fullAtom2 = fullAtom;
            fullAtom = i;
            long earliestPresentationTime2;
            int referenceCount2;
            if (fullAtom < referenceCount) {
                i = atom.readInt();
                if ((Integer.MIN_VALUE & i) != 0) {
                    throw new ParserException("Unhandled indirect reference");
                }
                int version2 = version;
                long[] durationsUs2 = durationsUs;
                long referenceDuration = atom.readUnsignedInt();
                sizes[fullAtom] = ConnectionsManager.DEFAULT_DATACENTER_ID & i;
                offsets[fullAtom] = offset3;
                timesUs[fullAtom] = timeUs;
                long time2 = time + referenceDuration;
                version = offsets;
                earliestPresentationTime2 = offset;
                long[] durationsUs3 = durationsUs2;
                referenceCount2 = referenceCount;
                int[] sizes2 = sizes;
                long earliestPresentationTimeUs2 = earliestPresentationTimeUs;
                timeUs = Util.scaleLargeTimestamp(time2, C0542C.MICROS_PER_SECOND, timescale);
                durationsUs3[fullAtom] = timeUs - timesUs[fullAtom];
                parsableByteArray.skipBytes(4);
                i = fullAtom + 1;
                offsets = version;
                offset3 += (long) sizes2[fullAtom];
                durationsUs = durationsUs3;
                sizes = sizes2;
                fullAtom = fullAtom2;
                version = version2;
                time = time2;
                offset = earliestPresentationTime2;
                referenceCount = referenceCount2;
                earliestPresentationTimeUs = earliestPresentationTimeUs2;
            } else {
                referenceCount2 = referenceCount;
                earliestPresentationTime2 = offset;
                return Pair.create(Long.valueOf(earliestPresentationTimeUs), new ChunkIndex(sizes, offsets, durationsUs, timesUs));
            }
        }
    }

    private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        int i;
        TrackBundle nextTrackBundle = null;
        long nextDataOffset = Long.MAX_VALUE;
        int trackBundlesSize = this.trackBundles.size();
        for (i = 0; i < trackBundlesSize; i++) {
            TrackFragment trackFragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
            if (trackFragment.sampleEncryptionDataNeedsFill && trackFragment.auxiliaryDataPosition < nextDataOffset) {
                nextDataOffset = trackFragment.auxiliaryDataPosition;
                nextTrackBundle = (TrackBundle) this.trackBundles.valueAt(i);
            }
        }
        if (nextTrackBundle == null) {
            this.parserState = 3;
            return;
        }
        i = (int) (nextDataOffset - input.getPosition());
        if (i < 0) {
            throw new ParserException("Offset to encryption data was negative.");
        }
        input.skipFully(i);
        nextTrackBundle.fragment.fillEncryptionData(input);
    }

    private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
        int bytesToSkip;
        ExtractorInput extractorInput = input;
        int i = 4;
        boolean z = true;
        boolean z2 = false;
        if (this.parserState == 3) {
            if (r0.currentTrackBundle == null) {
                TrackBundle currentTrackBundle = getNextFragmentRun(r0.trackBundles);
                if (currentTrackBundle == null) {
                    bytesToSkip = (int) (r0.endOfMdatPosition - input.getPosition());
                    if (bytesToSkip < 0) {
                        throw new ParserException("Offset to end of mdat was negative.");
                    }
                    extractorInput.skipFully(bytesToSkip);
                    enterReadingAtomHeaderState();
                    return false;
                }
                int bytesToSkip2 = (int) (currentTrackBundle.fragment.trunDataPosition[currentTrackBundle.currentTrackRunIndex] - input.getPosition());
                if (bytesToSkip2 < 0) {
                    Log.w(TAG, "Ignoring negative offset to sample data.");
                    bytesToSkip2 = 0;
                }
                extractorInput.skipFully(bytesToSkip2);
                r0.currentTrackBundle = currentTrackBundle;
            }
            r0.sampleSize = r0.currentTrackBundle.fragment.sampleSizeTable[r0.currentTrackBundle.currentSampleIndex];
            if (r0.currentTrackBundle.fragment.definesEncryptionData) {
                r0.sampleBytesWritten = appendSampleEncryptionData(r0.currentTrackBundle);
                r0.sampleSize += r0.sampleBytesWritten;
            } else {
                r0.sampleBytesWritten = 0;
            }
            if (r0.currentTrackBundle.track.sampleTransformation == 1) {
                r0.sampleSize -= 8;
                extractorInput.skipFully(8);
            }
            r0.parserState = 4;
            r0.sampleCurrentNalBytesRemaining = 0;
        }
        TrackFragment fragment = r0.currentTrackBundle.fragment;
        Track track = r0.currentTrackBundle.track;
        TrackOutput output = r0.currentTrackBundle.output;
        int sampleIndex = r0.currentTrackBundle.currentSampleIndex;
        long j = 1000;
        if (track.nalUnitLengthFieldLength != 0) {
            byte[] nalPrefixData = r0.nalPrefix.data;
            nalPrefixData[0] = (byte) 0;
            nalPrefixData[1] = (byte) 0;
            nalPrefixData[2] = (byte) 0;
            int nalUnitPrefixLength = track.nalUnitLengthFieldLength + 1;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (r0.sampleBytesWritten < r0.sampleSize) {
                if (r0.sampleCurrentNalBytesRemaining == 0) {
                    extractorInput.readFully(nalPrefixData, nalUnitLengthFieldLengthDiff, nalUnitPrefixLength);
                    r0.nalPrefix.setPosition(z2);
                    r0.sampleCurrentNalBytesRemaining = r0.nalPrefix.readUnsignedIntToInt() - z;
                    r0.nalStartCode.setPosition(z2);
                    output.sampleData(r0.nalStartCode, i);
                    output.sampleData(r0.nalPrefix, z);
                    boolean z3 = (r0.cea608TrackOutputs.length <= 0 || !NalUnitUtil.isNalUnitSei(track.format.sampleMimeType, nalPrefixData[i])) ? z2 : z;
                    r0.processSeiNalUnitPayload = z3;
                    r0.sampleBytesWritten += 5;
                    r0.sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    if (r0.processSeiNalUnitPayload) {
                        r0.nalBuffer.reset(r0.sampleCurrentNalBytesRemaining);
                        extractorInput.readFully(r0.nalBuffer.data, z2, r0.sampleCurrentNalBytesRemaining);
                        output.sampleData(r0.nalBuffer, r0.sampleCurrentNalBytesRemaining);
                        bytesToSkip = r0.sampleCurrentNalBytesRemaining;
                        i = NalUnitUtil.unescapeStream(r0.nalBuffer.data, r0.nalBuffer.limit());
                        r0.nalBuffer.setPosition(MimeTypes.VIDEO_H265.equals(track.format.sampleMimeType));
                        r0.nalBuffer.setLimit(i);
                        CeaUtil.consume(fragment.getSamplePresentationTime(sampleIndex) * j, r0.nalBuffer, r0.cea608TrackOutputs);
                    } else {
                        bytesToSkip = output.sampleData(extractorInput, r0.sampleCurrentNalBytesRemaining, false);
                    }
                    r0.sampleBytesWritten += bytesToSkip;
                    r0.sampleCurrentNalBytesRemaining -= bytesToSkip;
                }
                i = 4;
                z = true;
                z2 = false;
                j = 1000;
            }
        } else {
            while (r0.sampleBytesWritten < r0.sampleSize) {
                r0.sampleBytesWritten += output.sampleData(extractorInput, r0.sampleSize - r0.sampleBytesWritten, false);
            }
        }
        long sampleTimeUs = fragment.getSamplePresentationTime(sampleIndex) * 1000;
        if (r0.timestampAdjuster != null) {
            sampleTimeUs = r0.timestampAdjuster.adjustSampleTimestamp(sampleTimeUs);
        }
        int sampleFlags = fragment.sampleIsSyncFrameTable[sampleIndex];
        CryptoData cryptoData = null;
        if (fragment.definesEncryptionData) {
            TrackEncryptionBox encryptionBox;
            sampleFlags |= NUM;
            if (fragment.trackEncryptionBox != null) {
                encryptionBox = fragment.trackEncryptionBox;
            } else {
                encryptionBox = track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
            }
            cryptoData = encryptionBox.cryptoData;
        }
        output.sampleMetadata(sampleTimeUs, sampleFlags, r0.sampleSize, 0, cryptoData);
        outputPendingMetadataSamples(sampleTimeUs);
        TrackBundle trackBundle = r0.currentTrackBundle;
        trackBundle.currentSampleIndex++;
        trackBundle = r0.currentTrackBundle;
        trackBundle.currentSampleInTrackRun++;
        if (r0.currentTrackBundle.currentSampleInTrackRun == fragment.trunLength[r0.currentTrackBundle.currentTrackRunIndex]) {
            trackBundle = r0.currentTrackBundle;
            trackBundle.currentTrackRunIndex++;
            r0.currentTrackBundle.currentSampleInTrackRun = 0;
            r0.currentTrackBundle = null;
        }
        r0.parserState = 3;
        return true;
    }

    private void outputPendingMetadataSamples(long sampleTimeUs) {
        while (!this.pendingMetadataSampleInfos.isEmpty()) {
            MetadataSampleInfo sampleInfo = (MetadataSampleInfo) this.pendingMetadataSampleInfos.removeFirst();
            this.pendingMetadataSampleBytes -= sampleInfo.size;
            for (TrackOutput emsgTrackOutput : this.emsgTrackOutputs) {
                emsgTrackOutput.sampleMetadata(sampleTimeUs + sampleInfo.presentationTimeDeltaUs, 1, sampleInfo.size, this.pendingMetadataSampleBytes, null);
            }
        }
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = Long.MAX_VALUE;
        int trackBundlesSize = trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackBundle trackBundle = (TrackBundle) trackBundles.valueAt(i);
            if (trackBundle.currentTrackRunIndex != trackBundle.fragment.trunCount) {
                long trunOffset = trackBundle.fragment.trunDataPosition[trackBundle.currentTrackRunIndex];
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private int appendSampleEncryptionData(TrackBundle trackBundle) {
        TrackEncryptionBox encryptionBox;
        ParsableByteArray initializationVectorData;
        int vectorSize;
        TrackFragment trackFragment = trackBundle.fragment;
        int sampleDescriptionIndex = trackFragment.header.sampleDescriptionIndex;
        if (trackFragment.trackEncryptionBox != null) {
            encryptionBox = trackFragment.trackEncryptionBox;
        } else {
            encryptionBox = trackBundle.track.getSampleDescriptionEncryptionBox(sampleDescriptionIndex);
        }
        if (encryptionBox.initializationVectorSize != 0) {
            initializationVectorData = trackFragment.sampleEncryptionData;
            vectorSize = encryptionBox.initializationVectorSize;
        } else {
            vectorSize = encryptionBox.defaultInitializationVector;
            this.defaultInitializationVector.reset(vectorSize, vectorSize.length);
            initializationVectorData = this.defaultInitializationVector;
            vectorSize = vectorSize.length;
        }
        boolean subsampleEncryption = trackFragment.sampleHasSubsampleEncryptionTable[trackBundle.currentSampleIndex];
        this.encryptionSignalByte.data[0] = (byte) ((subsampleEncryption ? 128 : 0) | vectorSize);
        this.encryptionSignalByte.setPosition(0);
        TrackOutput output = trackBundle.output;
        output.sampleData(this.encryptionSignalByte, 1);
        output.sampleData(initializationVectorData, vectorSize);
        if (!subsampleEncryption) {
            return 1 + vectorSize;
        }
        ParsableByteArray subsampleEncryptionData = trackFragment.sampleEncryptionData;
        int subsampleCount = subsampleEncryptionData.readUnsignedShort();
        subsampleEncryptionData.skipBytes(-2);
        int subsampleDataLength = 2 + (6 * subsampleCount);
        output.sampleData(subsampleEncryptionData, subsampleDataLength);
        return (1 + vectorSize) + subsampleDataLength;
    }

    private static DrmInitData getDrmInitDataFromAtoms(List<LeafAtom> leafChildren) {
        List schemeDatas = null;
        int leafChildrenSize = leafChildren.size();
        for (int i = 0; i < leafChildrenSize; i++) {
            LeafAtom child = (LeafAtom) leafChildren.get(i);
            if (child.type == Atom.TYPE_pssh) {
                if (schemeDatas == null) {
                    schemeDatas = new ArrayList();
                }
                byte[] psshData = child.data.data;
                UUID uuid = PsshAtomUtil.parseUuid(psshData);
                if (uuid == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    schemeDatas.add(new SchemeData(uuid, MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        return schemeDatas == null ? null : new DrmInitData(schemeDatas);
    }

    private static boolean shouldParseLeafAtom(int atom) {
        if (!(atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_uuid || atom == Atom.TYPE_sbgp || atom == Atom.TYPE_sgpd || atom == Atom.TYPE_elst || atom == Atom.TYPE_mehd)) {
            if (atom != Atom.TYPE_emsg) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        if (!(atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex)) {
            if (atom != Atom.TYPE_edts) {
                return false;
            }
        }
        return true;
    }
}
