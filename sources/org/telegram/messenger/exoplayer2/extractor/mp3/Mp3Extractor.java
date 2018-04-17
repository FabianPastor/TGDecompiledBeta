package org.telegram.messenger.exoplayer2.extractor.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Mp3Extractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C18391();
    public static final int FLAG_DISABLE_ID3_METADATA = 2;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_SNIFF_BYTES = 16384;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int MPEG_AUDIO_HEADER_MASK = -128000;
    private static final int SCRATCH_LENGTH = 10;
    private static final int SEEK_HEADER_INFO = Util.getIntegerCodeForString("Info");
    private static final int SEEK_HEADER_UNSET = 0;
    private static final int SEEK_HEADER_VBRI = Util.getIntegerCodeForString("VBRI");
    private static final int SEEK_HEADER_XING = Util.getIntegerCodeForString("Xing");
    private long basisTimeUs;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private final long forcedFirstSampleTimestampUs;
    private final GaplessInfoHolder gaplessInfoHolder;
    private Metadata metadata;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor$1 */
    static class C18391 implements ExtractorsFactory {
        C18391() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new Mp3Extractor()};
        }
    }

    interface Seeker extends SeekMap {
        long getTimeUs(long j);
    }

    public Mp3Extractor() {
        this(0);
    }

    public Mp3Extractor(int flags) {
        this(flags, C0542C.TIME_UNSET);
    }

    public Mp3Extractor(int flags, long forcedFirstSampleTimestampUs) {
        this.flags = flags;
        this.forcedFirstSampleTimestampUs = forcedFirstSampleTimestampUs;
        this.scratch = new ParsableByteArray(10);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = C0542C.TIME_UNSET;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return synchronize(input, true);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = this.extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
    }

    public void seek(long position, long timeUs) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C0542C.TIME_UNSET;
        this.samplesRead = 0;
        this.sampleBytesRemaining = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0) {
            try {
                synchronize(input, false);
            } catch (EOFException e) {
                return -1;
            }
        }
        ExtractorInput extractorInput = input;
        if (r1.seeker == null) {
            r1.seeker = maybeReadSeekFrame(input);
            if (r1.seeker == null || !(r1.seeker.isSeekable() || (r1.flags & 1) == 0)) {
                r1.seeker = getConstantBitrateSeeker(input);
            }
            r1.extractorOutput.seekMap(r1.seeker);
            r1.trackOutput.format(Format.createAudioSampleFormat(null, r1.synchronizedHeader.mimeType, null, -1, 4096, r1.synchronizedHeader.channels, r1.synchronizedHeader.sampleRate, -1, r1.gaplessInfoHolder.encoderDelay, r1.gaplessInfoHolder.encoderPadding, null, null, 0, null, (r1.flags & 2) != 0 ? null : r1.metadata));
        }
        return readSample(input);
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int sampleHeaderData;
        ExtractorInput extractorInput2 = extractorInput;
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (!extractorInput2.peekFully(r0.scratch.data, 0, 4, true)) {
                return -1;
            }
            r0.scratch.setPosition(0);
            sampleHeaderData = r0.scratch.readInt();
            if (headersMatch(sampleHeaderData, (long) r0.synchronizedHeaderData)) {
                if (MpegAudioHeader.getFrameSize(sampleHeaderData) != -1) {
                    MpegAudioHeader.populateHeader(sampleHeaderData, r0.synchronizedHeader);
                    if (r0.basisTimeUs == C0542C.TIME_UNSET) {
                        r0.basisTimeUs = r0.seeker.getTimeUs(extractorInput.getPosition());
                        if (r0.forcedFirstSampleTimestampUs != C0542C.TIME_UNSET) {
                            r0.basisTimeUs += r0.forcedFirstSampleTimestampUs - r0.seeker.getTimeUs(0);
                        }
                    }
                    r0.sampleBytesRemaining = r0.synchronizedHeader.frameSize;
                }
            }
            extractorInput2.skipFully(1);
            r0.synchronizedHeaderData = 0;
            return 0;
        }
        sampleHeaderData = r0.trackOutput.sampleData(extractorInput2, r0.sampleBytesRemaining, true);
        if (sampleHeaderData == -1) {
            return -1;
        }
        r0.sampleBytesRemaining -= sampleHeaderData;
        if (r0.sampleBytesRemaining > 0) {
            return 0;
        }
        r0.trackOutput.sampleMetadata(r0.basisTimeUs + ((r0.samplesRead * C0542C.MICROS_PER_SECOND) / ((long) r0.synchronizedHeader.sampleRate)), 1, r0.synchronizedHeader.frameSize, 0, null);
        r0.samplesRead += (long) r0.synchronizedHeader.samplesPerFrame;
        r0.sampleBytesRemaining = 0;
        return 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean synchronize(ExtractorInput input, boolean sniffing) throws IOException, InterruptedException {
        int validFrameCount = 0;
        int candidateSynchronizedHeaderData = 0;
        int peekedId3Bytes = 0;
        int searchedBytes = 0;
        int searchLimitBytes = sniffing ? 16384 : 131072;
        input.resetPeekPosition();
        if (input.getPosition() == 0) {
            peekId3Data(input);
            peekedId3Bytes = (int) input.getPeekPosition();
            if (!sniffing) {
                input.skipFully(peekedId3Bytes);
            }
        }
        while (true) {
            if (!input.peekFully(this.scratch.data, 0, 4, validFrameCount > 0)) {
                break;
            }
            int frameSize;
            this.scratch.setPosition(0);
            int headerData = this.scratch.readInt();
            if (candidateSynchronizedHeaderData == 0 || headersMatch(headerData, (long) candidateSynchronizedHeaderData)) {
                frameSize = MpegAudioHeader.getFrameSize(headerData);
                int frameSize2 = frameSize;
                if (frameSize != -1) {
                    validFrameCount++;
                    if (validFrameCount != 1) {
                        if (validFrameCount == 4) {
                            break;
                        }
                    }
                    MpegAudioHeader.populateHeader(headerData, this.synchronizedHeader);
                    candidateSynchronizedHeaderData = headerData;
                    input.advancePeekPosition(frameSize2 - 4);
                }
            }
            frameSize = searchedBytes + 1;
            if (searchedBytes == searchLimitBytes) {
                break;
            }
            validFrameCount = 0;
            candidateSynchronizedHeaderData = 0;
            if (sniffing) {
                input.resetPeekPosition();
                input.advancePeekPosition(peekedId3Bytes + frameSize);
            } else {
                input.skipFully(1);
            }
            searchedBytes = frameSize;
            if (sniffing) {
                input.resetPeekPosition();
            } else {
                input.skipFully(peekedId3Bytes + searchedBytes);
            }
            this.synchronizedHeaderData = candidateSynchronizedHeaderData;
            return true;
        }
        if (sniffing) {
            input.resetPeekPosition();
        } else {
            input.skipFully(peekedId3Bytes + searchedBytes);
        }
        this.synchronizedHeaderData = candidateSynchronizedHeaderData;
        return true;
    }

    private void peekId3Data(ExtractorInput input) throws IOException, InterruptedException {
        int peekedId3Bytes = 0;
        while (true) {
            input.peekFully(this.scratch.data, 0, 10);
            this.scratch.setPosition(0);
            if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                input.resetPeekPosition();
                input.advancePeekPosition(peekedId3Bytes);
                return;
            }
            this.scratch.skipBytes(3);
            int framesLength = this.scratch.readSynchSafeInt();
            int tagLength = 10 + framesLength;
            if (this.metadata == null) {
                byte[] id3Data = new byte[tagLength];
                System.arraycopy(this.scratch.data, 0, id3Data, 0, 10);
                input.peekFully(id3Data, 10, framesLength);
                this.metadata = new Id3Decoder((this.flags & 2) != 0 ? GaplessInfoHolder.GAPLESS_INFO_ID3_FRAME_PREDICATE : null).decode(id3Data, tagLength);
                if (this.metadata != null) {
                    this.gaplessInfoHolder.setFromMetadata(this.metadata);
                }
            } else {
                input.advancePeekPosition(framesLength);
            }
            peekedId3Bytes += tagLength;
        }
    }

    private Seeker maybeReadSeekFrame(ExtractorInput input) throws IOException, InterruptedException {
        int xingBase;
        int seekHeader;
        Seeker seeker;
        ParsableByteArray frame = new ParsableByteArray(this.synchronizedHeader.frameSize);
        input.peekFully(frame.data, 0, this.synchronizedHeader.frameSize);
        int i = 21;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
                xingBase = i;
                seekHeader = getSeekFrameHeader(frame, xingBase);
                if (seekHeader != SEEK_HEADER_XING) {
                    if (seekHeader == SEEK_HEADER_INFO) {
                        if (seekHeader == SEEK_HEADER_VBRI) {
                            seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                            input.skipFully(this.synchronizedHeader.frameSize);
                        } else {
                            seeker = null;
                            input.resetPeekPosition();
                        }
                        return seeker;
                    }
                }
                seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                if (!(seeker == null || this.gaplessInfoHolder.hasGaplessInfo())) {
                    input.resetPeekPosition();
                    input.advancePeekPosition(xingBase + 141);
                    input.peekFully(this.scratch.data, 0, 3);
                    this.scratch.setPosition(0);
                    this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
                }
                input.skipFully(this.synchronizedHeader.frameSize);
                if (!(seeker == null || seeker.isSeekable() || seekHeader != SEEK_HEADER_INFO)) {
                    return getConstantBitrateSeeker(input);
                }
                return seeker;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
            xingBase = i;
            seekHeader = getSeekFrameHeader(frame, xingBase);
            if (seekHeader != SEEK_HEADER_XING) {
                if (seekHeader == SEEK_HEADER_INFO) {
                    if (seekHeader == SEEK_HEADER_VBRI) {
                        seeker = null;
                        input.resetPeekPosition();
                    } else {
                        seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                        input.skipFully(this.synchronizedHeader.frameSize);
                    }
                    return seeker;
                }
            }
            seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
            input.resetPeekPosition();
            input.advancePeekPosition(xingBase + 141);
            input.peekFully(this.scratch.data, 0, 3);
            this.scratch.setPosition(0);
            this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
            input.skipFully(this.synchronizedHeader.frameSize);
            return getConstantBitrateSeeker(input);
        }
        xingBase = i;
        seekHeader = getSeekFrameHeader(frame, xingBase);
        if (seekHeader != SEEK_HEADER_XING) {
            if (seekHeader == SEEK_HEADER_INFO) {
                if (seekHeader == SEEK_HEADER_VBRI) {
                    seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                    input.skipFully(this.synchronizedHeader.frameSize);
                } else {
                    seeker = null;
                    input.resetPeekPosition();
                }
                return seeker;
            }
        }
        seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
        input.resetPeekPosition();
        input.advancePeekPosition(xingBase + 141);
        input.peekFully(this.scratch.data, 0, 3);
        this.scratch.setPosition(0);
        this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
        input.skipFully(this.synchronizedHeader.frameSize);
        return getConstantBitrateSeeker(input);
    }

    private Seeker getConstantBitrateSeeker(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(input.getLength(), input.getPosition(), this.synchronizedHeader);
    }

    private static boolean headersMatch(int headerA, long headerB) {
        return ((long) (MPEG_AUDIO_HEADER_MASK & headerA)) == (headerB & -128000);
    }

    private static int getSeekFrameHeader(ParsableByteArray frame, int xingBase) {
        if (frame.limit() >= xingBase + 4) {
            frame.setPosition(xingBase);
            int headerData = frame.readInt();
            if (headerData == SEEK_HEADER_XING || headerData == SEEK_HEADER_INFO) {
                return headerData;
            }
        }
        if (frame.limit() >= 40) {
            frame.setPosition(36);
            if (frame.readInt() == SEEK_HEADER_VBRI) {
                return SEEK_HEADER_VBRI;
            }
        }
        return 0;
    }
}
