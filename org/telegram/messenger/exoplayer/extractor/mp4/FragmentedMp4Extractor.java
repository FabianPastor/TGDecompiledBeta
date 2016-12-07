package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public class FragmentedMp4Extractor implements Extractor {
    private static final int FLAG_SIDELOADED = 4;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[]{(byte) -94, (byte) 57, (byte) 79, (byte) 82, (byte) 90, (byte) -101, (byte) 79, ClosedCaptionCtrl.MISC_CHAN_1, (byte) -94, (byte) 68, (byte) 108, (byte) 66, (byte) 124, (byte) 100, (byte) -115, (byte) -12};
    private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString(CencSampleEncryptionInformationGroupEntry.TYPE);
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private final ParsableByteArray encryptionSignalByte;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private final Track sideloadedTrack;
    private final SparseArray<TrackBundle> trackBundles;

    private static final class TrackBundle {
        public int currentSampleIndex;
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
            this.output.format(track.mediaFormat);
            reset();
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = 0;
        }
    }

    public FragmentedMp4Extractor() {
        this(0);
    }

    public FragmentedMp4Extractor(int flags) {
        this(flags, null);
    }

    public FragmentedMp4Extractor(int flags, Track sideloadedTrack) {
        this.sideloadedTrack = sideloadedTrack;
        this.flags = (sideloadedTrack != null ? 4 : 0) | flags;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.encryptionSignalByte = new ParsableByteArray(1);
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new Stack();
        this.trackBundles = new SparseArray();
        enterReadingAtomHeaderState();
    }

    public final boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    public final void init(ExtractorOutput output) {
        this.extractorOutput = output;
        if (this.sideloadedTrack != null) {
            TrackBundle bundle = new TrackBundle(output.track(0));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
            this.trackBundles.put(0, bundle);
            this.extractorOutput.endTracks();
        }
    }

    public final void seek() {
        int trackCount = this.trackBundles.size();
        for (int i = 0; i < trackCount; i++) {
            ((TrackBundle) this.trackBundles.valueAt(i)).reset();
        }
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
    }

    public final void release() {
    }

    public final int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
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
        }
        long atomPosition = input.getPosition() - ((long) this.atomHeaderBytesRead);
        if (this.atomType == Atom.TYPE_moof) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                TrackFragment fragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
                fragment.auxiliaryDataPosition = atomPosition;
                fragment.dataPosition = atomPosition;
            }
        }
        if (this.atomType == Atom.TYPE_mdat) {
            this.currentTrackBundle = null;
            this.endOfMdatPosition = this.atomSize + atomPosition;
            if (!this.haveOutputSeekMap) {
                this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                this.haveOutputSeekMap = true;
            }
            this.parserState = 2;
            return true;
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long endPosition = (input.getPosition() + this.atomSize) - 8;
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
            this.extractorOutput.seekMap(parseSidx(leaf.data, inputPosition));
            this.haveOutputSeekMap = true;
        } else if (leaf.type == Atom.TYPE_emsg) {
            parseEmsg(leaf.data, inputPosition);
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

    private void onMoovContainerAtomRead(ContainerAtom moov) {
        int i;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        List<LeafAtom> moovLeafChildren = moov.leafChildren;
        int moovLeafChildrenSize = moovLeafChildren.size();
        Mapped drmInitData = null;
        for (i = 0; i < moovLeafChildrenSize; i++) {
            LeafAtom child = (LeafAtom) moovLeafChildren.get(i);
            if (child.type == Atom.TYPE_pssh) {
                if (drmInitData == null) {
                    drmInitData = new Mapped();
                }
                byte[] psshData = child.data.data;
                if (PsshAtomUtil.parseUuid(psshData) == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    drmInitData.put(PsshAtomUtil.parseUuid(psshData), new SchemeInitData(MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        if (drmInitData != null) {
            this.extractorOutput.drmInitData(drmInitData);
        }
        ContainerAtom mvex = moov.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray();
        long duration = -1;
        int mvexChildrenSize = mvex.leafChildren.size();
        for (i = 0; i < mvexChildrenSize; i++) {
            LeafAtom atom = (LeafAtom) mvex.leafChildren.get(i);
            if (atom.type == Atom.TYPE_trex) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                defaultSampleValuesArray.put(((Integer) trexData.first).intValue(), trexData.second);
            } else {
                if (atom.type == Atom.TYPE_mehd) {
                    duration = parseMehd(atom.data);
                }
            }
        }
        SparseArray<Track> tracks = new SparseArray();
        int moovContainerChildrenSize = moov.containerChildren.size();
        for (i = 0; i < moovContainerChildrenSize; i++) {
            Track track;
            ContainerAtom atom2 = (ContainerAtom) moov.containerChildren.get(i);
            if (atom2.type == Atom.TYPE_trak) {
                track = AtomParsers.parseTrak(atom2, moov.getLeafAtomOfType(Atom.TYPE_mvhd), duration, false);
                if (track != null) {
                    tracks.put(track.id, track);
                }
            }
        }
        int trackCount = tracks.size();
        if (this.trackBundles.size() == 0) {
            for (i = 0; i < trackCount; i++) {
                this.trackBundles.put(((Track) tracks.valueAt(i)).id, new TrackBundle(this.extractorOutput.track(i)));
            }
            this.extractorOutput.endTracks();
        } else {
            Assertions.checkState(this.trackBundles.size() == trackCount);
        }
        for (i = 0; i < trackCount; i++) {
            track = (Track) tracks.valueAt(i);
            ((TrackBundle) this.trackBundles.get(track.id)).init(track, (DefaultSampleValues) defaultSampleValuesArray.get(track.id));
        }
    }

    private void onMoofContainerAtomRead(ContainerAtom moof) throws ParserException {
        parseMoof(moof, this.trackBundles, this.flags, this.extendedTypeScratch);
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
        if (traf.getChildAtomOfTypeCount(Atom.TYPE_trun) != 1) {
            throw new ParserException("Trun count in traf != 1 (unsupported).");
        }
        TrackBundle trackBundle = parseTfhd(traf.getLeafAtomOfType(Atom.TYPE_tfhd).data, trackBundleArray, flags);
        if (trackBundle != null) {
            TrackFragment fragment = trackBundle.fragment;
            long decodeTime = fragment.nextFragmentDecodeTime;
            trackBundle.reset();
            if (traf.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (flags & 2) == 0) {
                decodeTime = parseTfdt(traf.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTrun(trackBundle, decodeTime, flags, traf.getLeafAtomOfType(Atom.TYPE_trun).data);
            LeafAtom saiz = traf.getLeafAtomOfType(Atom.TYPE_saiz);
            if (saiz != null) {
                parseSaiz(trackBundle.track.sampleDescriptionEncryptionBoxes[fragment.header.sampleDescriptionIndex], saiz.data, fragment);
            }
            LeafAtom saio = traf.getLeafAtomOfType(Atom.TYPE_saio);
            if (saio != null) {
                parseSaio(saio.data, fragment);
            }
            LeafAtom senc = traf.getLeafAtomOfType(Atom.TYPE_senc);
            if (senc != null) {
                parseSenc(senc.data, fragment);
            }
            LeafAtom sbgp = traf.getLeafAtomOfType(Atom.TYPE_sbgp);
            LeafAtom sgpd = traf.getLeafAtomOfType(Atom.TYPE_sgpd);
            if (!(sbgp == null || sgpd == null)) {
                parseSgpd(sbgp.data, sgpd.data, fragment);
            }
            int childrenSize = traf.leafChildren.size();
            for (int i = 0; i < childrenSize; i++) {
                LeafAtom atom = (LeafAtom) traf.leafChildren.get(i);
                if (atom.type == Atom.TYPE_uuid) {
                    parseUuid(atom.data, fragment, extendedTypeScratch);
                }
            }
        }
    }

    private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz, TrackFragment out) throws ParserException {
        int vectorSize = encryptionBox.initializationVectorSize;
        saiz.setPosition(8);
        if ((Atom.parseFullAtomFlags(saiz.readInt()) & 1) == 1) {
            saiz.skipBytes(8);
        }
        int defaultSampleInfoSize = saiz.readUnsignedByte();
        int sampleCount = saiz.readUnsignedIntToInt();
        if (sampleCount != out.length) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
        }
        int totalSize = 0;
        if (defaultSampleInfoSize == 0) {
            boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
            for (int i = 0; i < sampleCount; i++) {
                int sampleInfoSize = saiz.readUnsignedByte();
                totalSize += sampleInfoSize;
                sampleHasSubsampleEncryptionTable[i] = sampleInfoSize > vectorSize;
            }
        } else {
            totalSize = 0 + (defaultSampleInfoSize * sampleCount);
            Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, defaultSampleInfoSize > vectorSize);
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
            throw new ParserException("Unexpected saio entry count: " + entryCount);
        }
        int version = Atom.parseFullAtomVersion(fullAtom);
        out.auxiliaryDataPosition = (version == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong()) + out.auxiliaryDataPosition;
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles, int flags) {
        tfhd.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(tfhd.readInt());
        int trackId = tfhd.readInt();
        if ((flags & 4) != 0) {
            trackId = 0;
        }
        TrackBundle trackBundle = (TrackBundle) trackBundles.get(trackId);
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

    private static void parseTrun(TrackBundle trackBundle, long decodeTime, int flags, ParsableByteArray trun) {
        trun.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(trun.readInt());
        Track track = trackBundle.track;
        TrackFragment fragment = trackBundle.fragment;
        DefaultSampleValues defaultSampleValues = fragment.header;
        int sampleCount = trun.readUnsignedIntToInt();
        if ((atomFlags & 1) != 0) {
            fragment.dataPosition += (long) trun.readInt();
        }
        boolean firstSampleFlagsPresent = (atomFlags & 4) != 0;
        int firstSampleFlags = defaultSampleValues.flags;
        if (firstSampleFlagsPresent) {
            firstSampleFlags = trun.readUnsignedIntToInt();
        }
        boolean sampleDurationsPresent = (atomFlags & 256) != 0;
        boolean sampleSizesPresent = (atomFlags & 512) != 0;
        boolean sampleFlagsPresent = (atomFlags & 1024) != 0;
        boolean sampleCompositionTimeOffsetsPresent = (atomFlags & 2048) != 0;
        long edtsOffset = 0;
        if (track.editListDurations != null && track.editListDurations.length == 1 && track.editListDurations[0] == 0) {
            edtsOffset = Util.scaleLargeTimestamp(track.editListMediaTimes[0], 1000, track.timescale);
        }
        fragment.initTables(sampleCount);
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
        long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
        boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;
        long timescale = track.timescale;
        long cumulativeTime = decodeTime;
        boolean workaroundEveryVideoFrameIsSyncFrame = track.type == Track.TYPE_vide && (flags & 1) != 0;
        int i = 0;
        while (i < sampleCount) {
            int sampleDuration = sampleDurationsPresent ? trun.readUnsignedIntToInt() : defaultSampleValues.duration;
            int sampleSize = sampleSizesPresent ? trun.readUnsignedIntToInt() : defaultSampleValues.size;
            int sampleFlags = (i == 0 && firstSampleFlagsPresent) ? firstSampleFlags : sampleFlagsPresent ? trun.readInt() : defaultSampleValues.flags;
            if (sampleCompositionTimeOffsetsPresent) {
                sampleCompositionTimeOffsetTable[i] = (int) (((long) (trun.readInt() * 1000)) / timescale);
            } else {
                sampleCompositionTimeOffsetTable[i] = 0;
            }
            sampleDecodingTimeTable[i] = Util.scaleLargeTimestamp(cumulativeTime, 1000, timescale) - edtsOffset;
            sampleSizeTable[i] = sampleSize;
            boolean z = ((sampleFlags >> 16) & 1) == 0 && (!workaroundEveryVideoFrameIsSyncFrame || i == 0);
            sampleIsSyncFrameTable[i] = z;
            cumulativeTime += (long) sampleDuration;
            i++;
        }
        fragment.nextFragmentDecodeTime = cumulativeTime;
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
        senc.setPosition(offset + 8);
        int flags = Atom.parseFullAtomFlags(senc.readInt());
        if ((flags & 1) != 0) {
            throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
        }
        boolean subsampleEncryption;
        if ((flags & 2) != 0) {
            subsampleEncryption = true;
        } else {
            subsampleEncryption = false;
        }
        int sampleCount = senc.readUnsignedIntToInt();
        if (sampleCount != out.length) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
        }
        Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
        out.initEncryptionData(senc.bytesLeft());
        out.fillEncryptionData(senc);
    }

    private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, TrackFragment out) throws ParserException {
        sbgp.setPosition(8);
        int sbgpFullAtom = sbgp.readInt();
        if (sbgp.readInt() == SAMPLE_GROUP_TYPE_seig) {
            if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
                sbgp.skipBytes(4);
            }
            if (sbgp.readInt() != 1) {
                throw new ParserException("Entry count in sbgp != 1 (unsupported).");
            }
            sgpd.setPosition(8);
            int sgpdFullAtom = sgpd.readInt();
            if (sgpd.readInt() == SAMPLE_GROUP_TYPE_seig) {
                int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
                if (sgpdVersion == 1) {
                    if (sgpd.readUnsignedInt() == 0) {
                        throw new ParserException("Variable length decription in sgpd found (unsupported)");
                    }
                } else if (sgpdVersion >= 2) {
                    sgpd.skipBytes(4);
                }
                if (sgpd.readUnsignedInt() != 1) {
                    throw new ParserException("Entry count in sgpd != 1 (unsupported).");
                }
                boolean isProtected;
                sgpd.skipBytes(2);
                if (sgpd.readUnsignedByte() == 1) {
                    isProtected = true;
                } else {
                    isProtected = false;
                }
                if (isProtected) {
                    int initVectorSize = sgpd.readUnsignedByte();
                    byte[] keyId = new byte[16];
                    sgpd.readBytes(keyId, 0, keyId.length);
                    out.definesEncryptionData = true;
                    out.trackEncryptionBox = new TrackEncryptionBox(isProtected, initVectorSize, keyId);
                }
            }
        }
    }

    protected void parseEmsg(ParsableByteArray atom, long inputPosition) throws ParserException {
    }

    private static ChunkIndex parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long earliestPresentationTime;
        atom.setPosition(8);
        int version = Atom.parseFullAtomVersion(atom.readInt());
        atom.skipBytes(4);
        long timescale = atom.readUnsignedInt();
        long offset = inputPosition;
        if (version == 0) {
            earliestPresentationTime = atom.readUnsignedInt();
            offset += atom.readUnsignedInt();
        } else {
            earliestPresentationTime = atom.readUnsignedLongToLong();
            offset += atom.readUnsignedLongToLong();
        }
        atom.skipBytes(2);
        int referenceCount = atom.readUnsignedShort();
        int[] sizes = new int[referenceCount];
        long[] offsets = new long[referenceCount];
        long[] durationsUs = new long[referenceCount];
        long[] timesUs = new long[referenceCount];
        long time = earliestPresentationTime;
        long timeUs = Util.scaleLargeTimestamp(time, C.MICROS_PER_SECOND, timescale);
        for (int i = 0; i < referenceCount; i++) {
            int firstInt = atom.readInt();
            if ((Integer.MIN_VALUE & firstInt) != 0) {
                throw new ParserException("Unhandled indirect reference");
            }
            long referenceDuration = atom.readUnsignedInt();
            sizes[i] = ConnectionsManager.DEFAULT_DATACENTER_ID & firstInt;
            offsets[i] = offset;
            timesUs[i] = timeUs;
            time += referenceDuration;
            timeUs = Util.scaleLargeTimestamp(time, C.MICROS_PER_SECOND, timescale);
            durationsUs[i] = timeUs - timesUs[i];
            atom.skipBytes(4);
            offset += (long) sizes[i];
        }
        return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
    }

    private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        TrackBundle nextTrackBundle = null;
        long nextDataOffset = Long.MAX_VALUE;
        int trackBundlesSize = this.trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
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
        int bytesToSkip = (int) (nextDataOffset - input.getPosition());
        if (bytesToSkip < 0) {
            throw new ParserException("Offset to encryption data was negative.");
        }
        input.skipFully(bytesToSkip);
        nextTrackBundle.fragment.fillEncryptionData(input);
    }

    private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
        if (this.parserState == 3) {
            if (this.currentTrackBundle == null) {
                this.currentTrackBundle = getNextFragmentRun(this.trackBundles);
                int bytesToSkip;
                if (this.currentTrackBundle == null) {
                    bytesToSkip = (int) (this.endOfMdatPosition - input.getPosition());
                    if (bytesToSkip < 0) {
                        throw new ParserException("Offset to end of mdat was negative.");
                    }
                    input.skipFully(bytesToSkip);
                    enterReadingAtomHeaderState();
                    return false;
                }
                bytesToSkip = (int) (this.currentTrackBundle.fragment.dataPosition - input.getPosition());
                if (bytesToSkip < 0) {
                    throw new ParserException("Offset to sample data was negative.");
                }
                input.skipFully(bytesToSkip);
            }
            this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
            if (this.currentTrackBundle.fragment.definesEncryptionData) {
                this.sampleBytesWritten = appendSampleEncryptionData(this.currentTrackBundle);
                this.sampleSize += this.sampleBytesWritten;
            } else {
                this.sampleBytesWritten = 0;
            }
            this.parserState = 4;
            this.sampleCurrentNalBytesRemaining = 0;
        }
        TrackFragment fragment = this.currentTrackBundle.fragment;
        Track track = this.currentTrackBundle.track;
        TrackOutput output = this.currentTrackBundle.output;
        int sampleIndex = this.currentTrackBundle.currentSampleIndex;
        if (track.nalUnitLengthFieldLength != -1) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            int nalUnitLengthFieldLength = track.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    input.readFully(this.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                    this.nalLength.setPosition(0);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(0);
                    output.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                    this.sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    int writtenBytes = output.sampleData(input, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += writtenBytes;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes;
                }
            }
        } else {
            while (this.sampleBytesWritten < this.sampleSize) {
                this.sampleBytesWritten += output.sampleData(input, this.sampleSize - this.sampleBytesWritten, false);
            }
        }
        long sampleTimeUs = fragment.getSamplePresentationTime(sampleIndex) * 1000;
        int sampleFlags = (fragment.definesEncryptionData ? 2 : 0) | (fragment.sampleIsSyncFrameTable[sampleIndex] ? 1 : 0);
        int sampleDescriptionIndex = fragment.header.sampleDescriptionIndex;
        byte[] encryptionKey = null;
        if (fragment.definesEncryptionData) {
            encryptionKey = fragment.trackEncryptionBox != null ? fragment.trackEncryptionBox.keyId : track.sampleDescriptionEncryptionBoxes[sampleDescriptionIndex].keyId;
        }
        output.sampleMetadata(sampleTimeUs, sampleFlags, this.sampleSize, 0, encryptionKey);
        TrackBundle trackBundle = this.currentTrackBundle;
        trackBundle.currentSampleIndex++;
        if (this.currentTrackBundle.currentSampleIndex == fragment.length) {
            this.currentTrackBundle = null;
        }
        this.parserState = 3;
        return true;
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = Long.MAX_VALUE;
        int trackBundlesSize = trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackBundle trackBundle = (TrackBundle) trackBundles.valueAt(i);
            if (trackBundle.currentSampleIndex != trackBundle.fragment.length) {
                long trunOffset = trackBundle.fragment.dataPosition;
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private int appendSampleEncryptionData(TrackBundle trackBundle) {
        int i;
        TrackFragment trackFragment = trackBundle.fragment;
        ParsableByteArray sampleEncryptionData = trackFragment.sampleEncryptionData;
        int vectorSize = (trackFragment.trackEncryptionBox != null ? trackFragment.trackEncryptionBox : trackBundle.track.sampleDescriptionEncryptionBoxes[trackFragment.header.sampleDescriptionIndex]).initializationVectorSize;
        boolean subsampleEncryption = trackFragment.sampleHasSubsampleEncryptionTable[trackBundle.currentSampleIndex];
        byte[] bArr = this.encryptionSignalByte.data;
        if (subsampleEncryption) {
            i = 128;
        } else {
            i = 0;
        }
        bArr[0] = (byte) (i | vectorSize);
        this.encryptionSignalByte.setPosition(0);
        TrackOutput output = trackBundle.output;
        output.sampleData(this.encryptionSignalByte, 1);
        output.sampleData(sampleEncryptionData, vectorSize);
        if (!subsampleEncryption) {
            return vectorSize + 1;
        }
        int subsampleCount = sampleEncryptionData.readUnsignedShort();
        sampleEncryptionData.skipBytes(-2);
        int subsampleDataLength = (subsampleCount * 6) + 2;
        output.sampleData(sampleEncryptionData, subsampleDataLength);
        return (vectorSize + 1) + subsampleDataLength;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_sbgp || atom == Atom.TYPE_sgpd || atom == Atom.TYPE_uuid || atom == Atom.TYPE_elst || atom == Atom.TYPE_mehd || atom == Atom.TYPE_emsg;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex || atom == Atom.TYPE_edts;
    }
}
