package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class AdtsReader implements ElementaryStreamReader {
    private static final int CRC_SIZE = 2;
    private static final int HEADER_SIZE = 5;
    private static final int ID3_HEADER_SIZE = 10;
    private static final byte[] ID3_IDENTIFIER = new byte[]{(byte) 73, (byte) 68, (byte) 51};
    private static final int ID3_SIZE_OFFSET = 6;
    private static final int MATCH_STATE_FF = 512;
    private static final int MATCH_STATE_I = 768;
    private static final int MATCH_STATE_ID = 1024;
    private static final int MATCH_STATE_START = 256;
    private static final int MATCH_STATE_VALUE_SHIFT = 8;
    private static final int STATE_FINDING_SAMPLE = 0;
    private static final int STATE_READING_ADTS_HEADER = 2;
    private static final int STATE_READING_ID3_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 3;
    private static final String TAG = "AdtsReader";
    private final ParsableBitArray adtsScratch;
    private int bytesRead;
    private TrackOutput currentOutput;
    private long currentSampleDuration;
    private final boolean exposeId3;
    private String formatId;
    private boolean hasCrc;
    private boolean hasOutputFormat;
    private final ParsableByteArray id3HeaderBuffer;
    private TrackOutput id3Output;
    private final String language;
    private int matchState;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    public void packetFinished() {
    }

    public AdtsReader(boolean z) {
        this(z, null);
    }

    public AdtsReader(boolean z, String str) {
        this.adtsScratch = new ParsableBitArray(new byte[7]);
        this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, 10));
        setFindingSampleState();
        this.exposeId3 = z;
        this.language = str;
    }

    public void seek() {
        setFindingSampleState();
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 1);
        if (this.exposeId3) {
            trackIdGenerator.generateNewId();
            this.id3Output = extractorOutput.track(trackIdGenerator.getTrackId(), 4);
            this.id3Output.format(Format.createSampleFormat(trackIdGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
            return;
        }
        this.id3Output = new DummyTrackOutput();
    }

    public void packetStarted(long j, boolean z) {
        this.timeUs = j;
    }

    public void consume(ParsableByteArray parsableByteArray) throws ParserException {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    findNextSample(parsableByteArray);
                    break;
                case 1:
                    if (!continueRead(parsableByteArray, this.id3HeaderBuffer.data, 10)) {
                        break;
                    }
                    parseId3Header();
                    break;
                case 2:
                    if (!continueRead(parsableByteArray, this.adtsScratch.data, this.hasCrc ? 7 : 5)) {
                        break;
                    }
                    parseAdtsHeader();
                    break;
                case 3:
                    readSample(parsableByteArray);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        parsableByteArray.readBytes(bArr, this.bytesRead, min);
        this.bytesRead += min;
        return this.bytesRead == i ? true : null;
    }

    private void setFindingSampleState() {
        this.state = 0;
        this.bytesRead = 0;
        this.matchState = 256;
    }

    private void setReadingId3HeaderState() {
        this.state = 1;
        this.bytesRead = ID3_IDENTIFIER.length;
        this.sampleSize = 0;
        this.id3HeaderBuffer.setPosition(0);
    }

    private void setReadingSampleState(TrackOutput trackOutput, long j, int i, int i2) {
        this.state = 3;
        this.bytesRead = i;
        this.currentOutput = trackOutput;
        this.currentSampleDuration = j;
        this.sampleSize = i2;
    }

    private void setReadingAdtsHeaderState() {
        this.state = 2;
        this.bytesRead = 0;
    }

    private void findNextSample(ParsableByteArray parsableByteArray) {
        byte[] bArr = parsableByteArray.data;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        while (position < limit) {
            int i = position + 1;
            position = bArr[position] & 255;
            if (this.matchState != 512 || position < PsExtractor.VIDEO_STREAM_MASK || position == 255) {
                position |= this.matchState;
                if (position == 329) {
                    this.matchState = MATCH_STATE_I;
                } else if (position == 511) {
                    this.matchState = 512;
                } else if (position == 836) {
                    this.matchState = 1024;
                } else if (position == 1075) {
                    setReadingId3HeaderState();
                    parsableByteArray.setPosition(i);
                    return;
                } else if (this.matchState != 256) {
                    this.matchState = 256;
                    i--;
                }
                position = i;
            } else {
                boolean z = true;
                if ((position & 1) != 0) {
                    z = false;
                }
                this.hasCrc = z;
                setReadingAdtsHeaderState();
                parsableByteArray.setPosition(i);
                return;
            }
        }
        parsableByteArray.setPosition(position);
    }

    private void parseId3Header() {
        this.id3Output.sampleData(this.id3HeaderBuffer, 10);
        this.id3HeaderBuffer.setPosition(6);
        setReadingSampleState(this.id3Output, 0, 10, this.id3HeaderBuffer.readSynchSafeInt() + 10);
    }

    private void parseAdtsHeader() throws ParserException {
        int readBits;
        this.adtsScratch.setPosition(0);
        if (this.hasOutputFormat) {
            r6.adtsScratch.skipBits(10);
        } else {
            readBits = r6.adtsScratch.readBits(2) + 1;
            if (readBits != 2) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Detected audio object type: ");
                stringBuilder.append(readBits);
                stringBuilder.append(", but assuming AAC LC.");
                Log.w(str, stringBuilder.toString());
                readBits = 2;
            }
            int readBits2 = r6.adtsScratch.readBits(4);
            r6.adtsScratch.skipBits(1);
            Object buildAacAudioSpecificConfig = CodecSpecificDataUtil.buildAacAudioSpecificConfig(readBits, readBits2, r6.adtsScratch.readBits(3));
            Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(buildAacAudioSpecificConfig);
            Format createAudioSampleFormat = Format.createAudioSampleFormat(r6.formatId, MimeTypes.AUDIO_AAC, null, -1, -1, ((Integer) parseAacAudioSpecificConfig.second).intValue(), ((Integer) parseAacAudioSpecificConfig.first).intValue(), Collections.singletonList(buildAacAudioSpecificConfig), null, 0, r6.language);
            r6.sampleDurationUs = NUM / ((long) createAudioSampleFormat.sampleRate);
            r6.output.format(createAudioSampleFormat);
            r6.hasOutputFormat = true;
        }
        r6.adtsScratch.skipBits(4);
        readBits = (r6.adtsScratch.readBits(13) - 2) - 5;
        if (r6.hasCrc) {
            readBits -= 2;
        }
        setReadingSampleState(r6.output, r6.sampleDurationUs, 0, readBits);
    }

    private void readSample(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
        this.currentOutput.sampleData(parsableByteArray, min);
        this.bytesRead += min;
        if (this.bytesRead == this.sampleSize) {
            this.currentOutput.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
            this.timeUs += this.currentSampleDuration;
            setFindingSampleState();
        }
    }
}
