package org.telegram.messenger.exoplayer2.extractor.mp3;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
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

    private static boolean headersMatch(int i, long j) {
        return ((long) (i & MPEG_AUDIO_HEADER_MASK)) == (j & -128000);
    }

    public void release() {
    }

    public Mp3Extractor() {
        this(0);
    }

    public Mp3Extractor(int i) {
        this(i, C0542C.TIME_UNSET);
    }

    public Mp3Extractor(int i, long j) {
        this.flags = i;
        this.forcedFirstSampleTimestampUs = j;
        this.scratch = new ParsableByteArray((int) 10);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = 1;
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return synchronize(extractorInput, true);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = this.extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
    }

    public void seek(long j, long j2) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C0542C.TIME_UNSET;
        this.samplesRead = 0;
        this.sampleBytesRemaining = 0;
    }

    public int read(org.telegram.messenger.exoplayer2.extractor.ExtractorInput r20, org.telegram.messenger.exoplayer2.extractor.PositionHolder r21) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r19 = this;
        r0 = r19;
        r1 = r0.synchronizedHeaderData;
        if (r1 != 0) goto L_0x000f;
    L_0x0006:
        r1 = 0;
        r2 = r20;
        r0.synchronize(r2, r1);	 Catch:{ EOFException -> 0x000d }
        goto L_0x0011;
    L_0x000d:
        r1 = -1;
        return r1;
    L_0x000f:
        r2 = r20;
    L_0x0011:
        r1 = r0.seeker;
        if (r1 != 0) goto L_0x006f;
    L_0x0015:
        r1 = r19.maybeReadSeekFrame(r20);
        r0.seeker = r1;
        r1 = r0.seeker;
        if (r1 == 0) goto L_0x002d;
    L_0x001f:
        r1 = r0.seeker;
        r1 = r1.isSeekable();
        if (r1 != 0) goto L_0x0033;
    L_0x0027:
        r1 = r0.flags;
        r1 = r1 & 1;
        if (r1 == 0) goto L_0x0033;
    L_0x002d:
        r1 = r19.getConstantBitrateSeeker(r20);
        r0.seeker = r1;
    L_0x0033:
        r1 = r0.extractorOutput;
        r3 = r0.seeker;
        r1.seekMap(r3);
        r1 = r0.trackOutput;
        r3 = 0;
        r4 = r0.synchronizedHeader;
        r4 = r4.mimeType;
        r5 = 0;
        r6 = -1;
        r7 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r8 = r0.synchronizedHeader;
        r8 = r8.channels;
        r9 = r0.synchronizedHeader;
        r9 = r9.sampleRate;
        r10 = -1;
        r11 = r0.gaplessInfoHolder;
        r11 = r11.encoderDelay;
        r12 = r0.gaplessInfoHolder;
        r12 = r12.encoderPadding;
        r13 = 0;
        r14 = 0;
        r16 = 0;
        r15 = r0.flags;
        r15 = r15 & 2;
        if (r15 == 0) goto L_0x0064;
    L_0x0060:
        r15 = 0;
    L_0x0061:
        r17 = r15;
        goto L_0x0067;
    L_0x0064:
        r15 = r0.metadata;
        goto L_0x0061;
    L_0x0067:
        r15 = 0;
        r3 = org.telegram.messenger.exoplayer2.Format.createAudioSampleFormat(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);
        r1.format(r3);
    L_0x006f:
        r1 = r19.readSample(r20);
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor.read(org.telegram.messenger.exoplayer2.extractor.ExtractorInput, org.telegram.messenger.exoplayer2.extractor.PositionHolder):int");
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (!extractorInput.peekFully(this.scratch.data, 0, 4, true)) {
                return -1;
            }
            this.scratch.setPosition(0);
            int readInt = this.scratch.readInt();
            if (headersMatch(readInt, (long) this.synchronizedHeaderData)) {
                if (MpegAudioHeader.getFrameSize(readInt) != -1) {
                    MpegAudioHeader.populateHeader(readInt, this.synchronizedHeader);
                    if (this.basisTimeUs == C0542C.TIME_UNSET) {
                        this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                        if (this.forcedFirstSampleTimestampUs != C0542C.TIME_UNSET) {
                            this.basisTimeUs += this.forcedFirstSampleTimestampUs - this.seeker.getTimeUs(0);
                        }
                    }
                    this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
                }
            }
            extractorInput.skipFully(1);
            this.synchronizedHeaderData = 0;
            return 0;
        }
        extractorInput = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (extractorInput == -1) {
            return -1;
        }
        this.sampleBytesRemaining -= extractorInput;
        if (this.sampleBytesRemaining > null) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.basisTimeUs + ((this.samplesRead * C0542C.MICROS_PER_SECOND) / ((long) this.synchronizedHeader.sampleRate)), 1, this.synchronizedHeader.frameSize, 0, null);
        this.samplesRead += (long) this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = 0;
        return 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean synchronize(ExtractorInput extractorInput, boolean z) throws IOException, InterruptedException {
        int peekPosition;
        int i;
        int i2;
        int i3;
        int i4 = z ? 16384 : 131072;
        extractorInput.resetPeekPosition();
        if (extractorInput.getPosition() == 0) {
            peekId3Data(extractorInput);
            peekPosition = (int) extractorInput.getPeekPosition();
            if (!z) {
                extractorInput.skipFully(peekPosition);
            }
            i = 0;
            i2 = i;
            i3 = peekPosition;
            peekPosition = i2;
        } else {
            peekPosition = 0;
            i = peekPosition;
            i2 = i;
            i3 = i2;
        }
        while (true) {
            if (!extractorInput.peekFully(this.scratch.data, 0, 4, peekPosition > 0)) {
                break;
            }
            this.scratch.setPosition(0);
            int readInt = this.scratch.readInt();
            if (i == 0 || headersMatch(readInt, (long) i)) {
                int frameSize = MpegAudioHeader.getFrameSize(readInt);
                if (frameSize != -1) {
                    peekPosition++;
                    if (peekPosition != 1) {
                        if (peekPosition == 4) {
                            break;
                        }
                    }
                    MpegAudioHeader.populateHeader(readInt, this.synchronizedHeader);
                    i = readInt;
                    extractorInput.advancePeekPosition(frameSize - 4);
                }
            }
            peekPosition = i2 + 1;
            if (i2 == i4) {
                break;
            }
            if (z) {
                extractorInput.resetPeekPosition();
                extractorInput.advancePeekPosition(i3 + peekPosition);
            } else {
                extractorInput.skipFully(1);
            }
            i = 0;
            i2 = peekPosition;
            peekPosition = i;
        }
        if (z) {
            extractorInput.skipFully(i3 + i2);
        } else {
            extractorInput.resetPeekPosition();
        }
        this.synchronizedHeaderData = i;
        return true;
    }

    private void peekId3Data(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int i = 0;
        while (true) {
            extractorInput.peekFully(this.scratch.data, 0, 10);
            this.scratch.setPosition(0);
            if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                extractorInput.resetPeekPosition();
                extractorInput.advancePeekPosition(i);
                return;
            }
            this.scratch.skipBytes(3);
            int readSynchSafeInt = this.scratch.readSynchSafeInt();
            int i2 = 10 + readSynchSafeInt;
            if (this.metadata == null) {
                Object obj = new byte[i2];
                System.arraycopy(this.scratch.data, 0, obj, 0, 10);
                extractorInput.peekFully(obj, 10, readSynchSafeInt);
                this.metadata = new Id3Decoder((this.flags & 2) != 0 ? GaplessInfoHolder.GAPLESS_INFO_ID3_FRAME_PREDICATE : null).decode(obj, i2);
                if (this.metadata != null) {
                    this.gaplessInfoHolder.setFromMetadata(this.metadata);
                }
            } else {
                extractorInput.advancePeekPosition(readSynchSafeInt);
            }
            i += i2;
        }
    }

    private Seeker maybeReadSeekFrame(ExtractorInput extractorInput) throws IOException, InterruptedException {
        Seeker create;
        ParsableByteArray parsableByteArray = new ParsableByteArray(this.synchronizedHeader.frameSize);
        extractorInput.peekFully(parsableByteArray.data, 0, this.synchronizedHeader.frameSize);
        int i = 21;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
        }
        int i2 = i;
        int seekFrameHeader = getSeekFrameHeader(parsableByteArray, i2);
        if (seekFrameHeader != SEEK_HEADER_XING) {
            if (seekFrameHeader != SEEK_HEADER_INFO) {
                if (seekFrameHeader == SEEK_HEADER_VBRI) {
                    create = VbriSeeker.create(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader, parsableByteArray);
                    extractorInput.skipFully(this.synchronizedHeader.frameSize);
                } else {
                    create = null;
                    extractorInput.resetPeekPosition();
                }
                return create;
            }
        }
        create = XingSeeker.create(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader, parsableByteArray);
        if (!(create == null || this.gaplessInfoHolder.hasGaplessInfo())) {
            extractorInput.resetPeekPosition();
            extractorInput.advancePeekPosition(i2 + 141);
            extractorInput.peekFully(this.scratch.data, 0, 3);
            this.scratch.setPosition(0);
            this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
        }
        extractorInput.skipFully(this.synchronizedHeader.frameSize);
        if (!(create == null || create.isSeekable() || seekFrameHeader != SEEK_HEADER_INFO)) {
            return getConstantBitrateSeeker(extractorInput);
        }
        return create;
    }

    private Seeker getConstantBitrateSeeker(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader);
    }

    private static int getSeekFrameHeader(ParsableByteArray parsableByteArray, int i) {
        if (parsableByteArray.limit() >= i + 4) {
            parsableByteArray.setPosition(i);
            i = parsableByteArray.readInt();
            if (i == SEEK_HEADER_XING || i == SEEK_HEADER_INFO) {
                return i;
            }
        }
        if (parsableByteArray.limit() >= 40) {
            parsableByteArray.setPosition(36);
            if (parsableByteArray.readInt() == SEEK_HEADER_VBRI) {
                return SEEK_HEADER_VBRI;
            }
        }
        return null;
    }
}
