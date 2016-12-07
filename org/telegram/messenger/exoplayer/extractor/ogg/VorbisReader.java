package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.ogg.VorbisUtil.CommentHeader;
import org.telegram.messenger.exoplayer.extractor.ogg.VorbisUtil.Mode;
import org.telegram.messenger.exoplayer.extractor.ogg.VorbisUtil.VorbisIdHeader;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class VorbisReader extends StreamReader implements SeekMap {
    private static final long LARGEST_EXPECTED_PAGE_SIZE = 8000;
    private long audioStartPosition;
    private CommentHeader commentHeader;
    private long duration;
    private long elapsedSamples;
    private long inputLength;
    private final OggSeeker oggSeeker = new OggSeeker();
    private int previousPacketBlockSize;
    private boolean seenFirstAudioPacket;
    private long targetGranule = -1;
    private long totalSamples;
    private VorbisIdHeader vorbisIdHeader;
    private VorbisSetup vorbisSetup;

    static final class VorbisSetup {
        public final CommentHeader commentHeader;
        public final int iLogModes;
        public final VorbisIdHeader idHeader;
        public final Mode[] modes;
        public final byte[] setupHeaderData;

        public VorbisSetup(VorbisIdHeader idHeader, CommentHeader commentHeader, byte[] setupHeaderData, Mode[] modes, int iLogModes) {
            this.idHeader = idHeader;
            this.commentHeader = commentHeader;
            this.setupHeaderData = setupHeaderData;
            this.modes = modes;
            this.iLogModes = iLogModes;
        }
    }

    VorbisReader() {
    }

    static boolean verifyBitstreamType(ParsableByteArray data) {
        try {
            return VorbisUtil.verifyVorbisHeaderCapturePattern(1, data, true);
        } catch (ParserException e) {
            return false;
        }
    }

    public void seek() {
        super.seek();
        this.previousPacketBlockSize = 0;
        this.elapsedSamples = 0;
        this.seenFirstAudioPacket = false;
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.totalSamples == 0) {
            long j;
            if (this.vorbisSetup == null) {
                this.inputLength = input.getLength();
                this.vorbisSetup = readSetupHeaders(input, this.scratch);
                this.audioStartPosition = input.getPosition();
                this.extractorOutput.seekMap(this);
                if (this.inputLength != -1) {
                    seekPosition.position = Math.max(0, input.getLength() - LARGEST_EXPECTED_PAGE_SIZE);
                    return 1;
                }
            }
            if (this.inputLength == -1) {
                j = -1;
            } else {
                j = this.oggParser.readGranuleOfLastPage(input);
            }
            this.totalSamples = j;
            ArrayList<byte[]> codecInitialisationData = new ArrayList();
            codecInitialisationData.add(this.vorbisSetup.idHeader.data);
            codecInitialisationData.add(this.vorbisSetup.setupHeaderData);
            if (this.inputLength == -1) {
                j = -1;
            } else {
                j = (this.totalSamples * C.MICROS_PER_SECOND) / this.vorbisSetup.idHeader.sampleRate;
            }
            this.duration = j;
            this.trackOutput.format(MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_VORBIS, this.vorbisSetup.idHeader.bitrateNominal, 65025, this.duration, this.vorbisSetup.idHeader.channels, (int) this.vorbisSetup.idHeader.sampleRate, codecInitialisationData, null));
            if (this.inputLength != -1) {
                this.oggSeeker.setup(this.inputLength - this.audioStartPosition, this.totalSamples);
                seekPosition.position = this.audioStartPosition;
                return 1;
            }
        }
        if (!this.seenFirstAudioPacket && this.targetGranule > -1) {
            OggUtil.skipToNextPage(input);
            long position = this.oggSeeker.getNextSeekPosition(this.targetGranule, input);
            if (position != -1) {
                seekPosition.position = position;
                return 1;
            }
            this.elapsedSamples = this.oggParser.skipToPageOfGranule(input, this.targetGranule);
            this.previousPacketBlockSize = this.vorbisIdHeader.blockSize0;
            this.seenFirstAudioPacket = true;
        }
        if (!this.oggParser.readPacket(input, this.scratch)) {
            return -1;
        }
        if ((this.scratch.data[0] & 1) != 1) {
            int packetBlockSize = decodeBlockSize(this.scratch.data[0], this.vorbisSetup);
            int samplesInPacket = this.seenFirstAudioPacket ? (this.previousPacketBlockSize + packetBlockSize) / 4 : 0;
            if (this.elapsedSamples + ((long) samplesInPacket) >= this.targetGranule) {
                appendNumberOfSamples(this.scratch, (long) samplesInPacket);
                long timeUs = (this.elapsedSamples * C.MICROS_PER_SECOND) / this.vorbisSetup.idHeader.sampleRate;
                this.trackOutput.sampleData(this.scratch, this.scratch.limit());
                this.trackOutput.sampleMetadata(timeUs, 1, this.scratch.limit(), 0, null);
                this.targetGranule = -1;
            }
            this.seenFirstAudioPacket = true;
            this.elapsedSamples += (long) samplesInPacket;
            this.previousPacketBlockSize = packetBlockSize;
        }
        this.scratch.reset();
        return 0;
    }

    VorbisSetup readSetupHeaders(ExtractorInput input, ParsableByteArray scratch) throws IOException, InterruptedException {
        if (this.vorbisIdHeader == null) {
            this.oggParser.readPacket(input, scratch);
            this.vorbisIdHeader = VorbisUtil.readVorbisIdentificationHeader(scratch);
            scratch.reset();
        }
        if (this.commentHeader == null) {
            this.oggParser.readPacket(input, scratch);
            this.commentHeader = VorbisUtil.readVorbisCommentHeader(scratch);
            scratch.reset();
        }
        this.oggParser.readPacket(input, scratch);
        byte[] setupHeaderData = new byte[scratch.limit()];
        System.arraycopy(scratch.data, 0, setupHeaderData, 0, scratch.limit());
        Mode[] modes = VorbisUtil.readVorbisModes(scratch, this.vorbisIdHeader.channels);
        int iLogModes = VorbisUtil.iLog(modes.length - 1);
        scratch.reset();
        return new VorbisSetup(this.vorbisIdHeader, this.commentHeader, setupHeaderData, modes, iLogModes);
    }

    static void appendNumberOfSamples(ParsableByteArray buffer, long packetSampleCount) {
        buffer.setLimit(buffer.limit() + 4);
        buffer.data[buffer.limit() - 4] = (byte) ((int) (packetSampleCount & 255));
        buffer.data[buffer.limit() - 3] = (byte) ((int) ((packetSampleCount >>> 8) & 255));
        buffer.data[buffer.limit() - 2] = (byte) ((int) ((packetSampleCount >>> 16) & 255));
        buffer.data[buffer.limit() - 1] = (byte) ((int) ((packetSampleCount >>> 24) & 255));
    }

    private static int decodeBlockSize(byte firstByteOfAudioPacket, VorbisSetup vorbisSetup) {
        if (vorbisSetup.modes[OggUtil.readBits(firstByteOfAudioPacket, vorbisSetup.iLogModes, 1)].blockFlag) {
            return vorbisSetup.idHeader.blockSize1;
        }
        return vorbisSetup.idHeader.blockSize0;
    }

    public boolean isSeekable() {
        return (this.vorbisSetup == null || this.inputLength == -1) ? false : true;
    }

    public long getPosition(long timeUs) {
        if (timeUs == 0) {
            this.targetGranule = -1;
            return this.audioStartPosition;
        }
        this.targetGranule = (this.vorbisSetup.idHeader.sampleRate * timeUs) / C.MICROS_PER_SECOND;
        return Math.max(this.audioStartPosition, (((this.inputLength - this.audioStartPosition) * timeUs) / this.duration) - 4000);
    }
}
