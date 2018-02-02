package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Pair;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class LatmReader implements ElementaryStreamReader {
    private static final int INITIAL_BUFFER_SIZE = 1024;
    private static final int STATE_FINDING_SYNC_1 = 0;
    private static final int STATE_FINDING_SYNC_2 = 1;
    private static final int STATE_READING_HEADER = 2;
    private static final int STATE_READING_SAMPLE = 3;
    private static final int SYNC_BYTE_FIRST = 86;
    private static final int SYNC_BYTE_SECOND = 224;
    private int audioMuxVersionA;
    private int bytesRead;
    private int channelCount;
    private Format format;
    private String formatId;
    private int frameLengthType;
    private final String language;
    private int numSubframes;
    private long otherDataLenBits;
    private boolean otherDataPresent;
    private TrackOutput output;
    private final ParsableBitArray sampleBitArray = new ParsableBitArray(this.sampleDataBuffer.data);
    private final ParsableByteArray sampleDataBuffer = new ParsableByteArray(1024);
    private long sampleDurationUs;
    private int sampleRateHz;
    private int sampleSize;
    private int secondHeaderByte;
    private int state;
    private boolean streamMuxRead;
    private long timeUs;

    public LatmReader(String language) {
        this.language = language;
    }

    public void seek() {
        this.state = 0;
        this.streamMuxRead = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 1);
        this.formatId = idGenerator.getFormatId();
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) throws ParserException {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    if (data.readUnsignedByte() != SYNC_BYTE_FIRST) {
                        break;
                    }
                    this.state = 1;
                    break;
                case 1:
                    int secondByte = data.readUnsignedByte();
                    if ((secondByte & 224) != 224) {
                        if (secondByte == SYNC_BYTE_FIRST) {
                            break;
                        }
                        this.state = 0;
                        break;
                    }
                    this.secondHeaderByte = secondByte;
                    this.state = 2;
                    break;
                case 2:
                    this.sampleSize = ((this.secondHeaderByte & -225) << 8) | data.readUnsignedByte();
                    if (this.sampleSize > this.sampleDataBuffer.data.length) {
                        resetBufferForSize(this.sampleSize);
                    }
                    this.bytesRead = 0;
                    this.state = 3;
                    break;
                case 3:
                    int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                    data.readBytes(this.sampleBitArray.data, this.bytesRead, bytesToRead);
                    this.bytesRead += bytesToRead;
                    if (this.bytesRead != this.sampleSize) {
                        break;
                    }
                    this.sampleBitArray.setPosition(0);
                    parseAudioMuxElement(this.sampleBitArray);
                    this.state = 0;
                    break;
                default:
                    break;
            }
        }
    }

    public void packetFinished() {
    }

    private void parseAudioMuxElement(ParsableBitArray data) throws ParserException {
        if (!data.readBit()) {
            this.streamMuxRead = true;
            parseStreamMuxConfig(data);
        } else if (!this.streamMuxRead) {
            return;
        }
        if (this.audioMuxVersionA != 0) {
            throw new ParserException();
        } else if (this.numSubframes != 0) {
            throw new ParserException();
        } else {
            parsePayloadMux(data, parsePayloadLengthInfo(data));
            if (this.otherDataPresent) {
                data.skipBits((int) this.otherDataLenBits);
            }
        }
    }

    private void parseStreamMuxConfig(ParsableBitArray data) throws ParserException {
        int audioMuxVersion = data.readBits(1);
        this.audioMuxVersionA = audioMuxVersion == 1 ? data.readBits(1) : 0;
        if (this.audioMuxVersionA == 0) {
            if (audioMuxVersion == 1) {
                latmGetValue(data);
            }
            if (data.readBit()) {
                this.numSubframes = data.readBits(6);
                int numProgram = data.readBits(4);
                int numLayer = data.readBits(3);
                if (numProgram == 0 && numLayer == 0) {
                    if (audioMuxVersion == 0) {
                        int startPosition = data.getPosition();
                        int readBits = parseAudioSpecificConfig(data);
                        data.setPosition(startPosition);
                        byte[] initData = new byte[((readBits + 7) / 8)];
                        data.readBits(initData, 0, readBits);
                        Format format = Format.createAudioSampleFormat(this.formatId, MimeTypes.AUDIO_AAC, null, -1, -1, this.channelCount, this.sampleRateHz, Collections.singletonList(initData), null, 0, this.language);
                        if (!format.equals(this.format)) {
                            this.format = format;
                            this.sampleDurationUs = NUM / ((long) format.sampleRate);
                            this.output.format(format);
                        }
                    } else {
                        data.skipBits(((int) latmGetValue(data)) - parseAudioSpecificConfig(data));
                    }
                    parseFrameLength(data);
                    this.otherDataPresent = data.readBit();
                    this.otherDataLenBits = 0;
                    if (this.otherDataPresent) {
                        if (audioMuxVersion == 1) {
                            this.otherDataLenBits = latmGetValue(data);
                        } else {
                            boolean otherDataLenEsc;
                            do {
                                otherDataLenEsc = data.readBit();
                                this.otherDataLenBits = (this.otherDataLenBits << 8) + ((long) data.readBits(8));
                            } while (otherDataLenEsc);
                        }
                    }
                    if (data.readBit()) {
                        data.skipBits(8);
                        return;
                    }
                    return;
                }
                throw new ParserException();
            }
            throw new ParserException();
        }
        throw new ParserException();
    }

    private void parseFrameLength(ParsableBitArray data) {
        this.frameLengthType = data.readBits(3);
        switch (this.frameLengthType) {
            case 0:
                data.skipBits(8);
                return;
            case 1:
                data.skipBits(9);
                return;
            case 3:
            case 4:
            case 5:
                data.skipBits(6);
                return;
            case 6:
            case 7:
                data.skipBits(1);
                return;
            default:
                return;
        }
    }

    private int parseAudioSpecificConfig(ParsableBitArray data) throws ParserException {
        int bitsLeft = data.bitsLeft();
        Pair<Integer, Integer> config = CodecSpecificDataUtil.parseAacAudioSpecificConfig(data, true);
        this.sampleRateHz = ((Integer) config.first).intValue();
        this.channelCount = ((Integer) config.second).intValue();
        return bitsLeft - data.bitsLeft();
    }

    private int parsePayloadLengthInfo(ParsableBitArray data) throws ParserException {
        int muxSlotLengthBytes = 0;
        if (this.frameLengthType == 0) {
            int tmp;
            do {
                tmp = data.readBits(8);
                muxSlotLengthBytes += tmp;
            } while (tmp == 255);
            return muxSlotLengthBytes;
        }
        throw new ParserException();
    }

    private void parsePayloadMux(ParsableBitArray data, int muxLengthBytes) {
        int bitPosition = data.getPosition();
        if ((bitPosition & 7) == 0) {
            this.sampleDataBuffer.setPosition(bitPosition >> 3);
        } else {
            data.readBits(this.sampleDataBuffer.data, 0, muxLengthBytes * 8);
            this.sampleDataBuffer.setPosition(0);
        }
        this.output.sampleData(this.sampleDataBuffer, muxLengthBytes);
        this.output.sampleMetadata(this.timeUs, 1, muxLengthBytes, 0, null);
        this.timeUs += this.sampleDurationUs;
    }

    private void resetBufferForSize(int newSize) {
        this.sampleDataBuffer.reset(newSize);
        this.sampleBitArray.reset(this.sampleDataBuffer.data);
    }

    private static long latmGetValue(ParsableBitArray data) {
        return (long) data.readBits((data.readBits(2) + 1) * 8);
    }
}
