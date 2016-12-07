package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.extractor.ts.ElementaryStreamReader.EsInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.ElementaryStreamReader.Factory;
import org.telegram.messenger.exoplayer2.extractor.ts.ElementaryStreamReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("AC-3"));
    private static final int BUFFER_PACKET_COUNT = 5;
    private static final int BUFFER_SIZE = 940;
    private static final long E_AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("EAC3"));
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new TsExtractor()};
        }
    };
    private static final long HEVC_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("HEVC"));
    private static final int MAX_PID_PLUS_ONE = 8192;
    private static final String TAG = "TsExtractor";
    private static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    public static final int TS_STREAM_TYPE_AAC = 15;
    public static final int TS_STREAM_TYPE_AC3 = 129;
    public static final int TS_STREAM_TYPE_DTS = 138;
    public static final int TS_STREAM_TYPE_E_AC3 = 135;
    public static final int TS_STREAM_TYPE_H262 = 2;
    public static final int TS_STREAM_TYPE_H264 = 27;
    public static final int TS_STREAM_TYPE_H265 = 36;
    public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
    public static final int TS_STREAM_TYPE_ID3 = 21;
    public static final int TS_STREAM_TYPE_MPA = 3;
    public static final int TS_STREAM_TYPE_MPA_LSF = 4;
    private static final int TS_SYNC_BYTE = 71;
    private final SparseIntArray continuityCounters;
    private ElementaryStreamReader id3Reader;
    private final boolean mapByType;
    private ExtractorOutput output;
    private final Factory streamReaderFactory;
    private final TimestampAdjuster timestampAdjuster;
    private final SparseBooleanArray trackIds;
    private boolean tracksEnded;
    private final ParsableByteArray tsPacketBuffer;
    private final SparseArray<TsPayloadReader> tsPayloadReaders;
    private final ParsableBitArray tsScratch;

    private static abstract class TsPayloadReader {
        public abstract void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput);

        public abstract void seek();

        private TsPayloadReader() {
        }
    }

    private class PatReader extends TsPayloadReader {
        private int crc;
        private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);
        private int sectionBytesRead;
        private final ParsableByteArray sectionData = new ParsableByteArray();
        private int sectionLength;

        public PatReader() {
            super();
        }

        public void seek() {
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                data.skipBytes(data.readUnsignedByte());
                data.readBytes(this.patScratch, 3);
                this.patScratch.skipBits(12);
                this.sectionLength = this.patScratch.readBits(12);
                this.sectionBytesRead = 0;
                this.crc = Util.crc(this.patScratch.data, 0, 3, -1);
                this.sectionData.reset(this.sectionLength);
            }
            int bytesToRead = Math.min(data.bytesLeft(), this.sectionLength - this.sectionBytesRead);
            data.readBytes(this.sectionData.data, this.sectionBytesRead, bytesToRead);
            this.sectionBytesRead += bytesToRead;
            if (this.sectionBytesRead >= this.sectionLength && Util.crc(this.sectionData.data, 0, this.sectionLength, this.crc) == 0) {
                this.sectionData.skipBytes(5);
                int programCount = (this.sectionLength - 9) / 4;
                for (int i = 0; i < programCount; i++) {
                    this.sectionData.readBytes(this.patScratch, 4);
                    int programNumber = this.patScratch.readBits(16);
                    this.patScratch.skipBits(3);
                    if (programNumber == 0) {
                        this.patScratch.skipBits(13);
                    } else {
                        int pid = this.patScratch.readBits(13);
                        TsExtractor.this.tsPayloadReaders.put(pid, new PmtReader(pid));
                    }
                }
            }
        }
    }

    private static final class PesReader extends TsPayloadReader {
        private static final int HEADER_SIZE = 9;
        private static final int MAX_HEADER_EXTENSION_SIZE = 10;
        private static final int PES_SCRATCH_SIZE = 10;
        private static final int STATE_FINDING_HEADER = 0;
        private static final int STATE_READING_BODY = 3;
        private static final int STATE_READING_HEADER = 1;
        private static final int STATE_READING_HEADER_EXTENSION = 2;
        private int bytesRead;
        private boolean dataAlignmentIndicator;
        private boolean dtsFlag;
        private int extendedHeaderLength;
        private int payloadSize;
        private final ElementaryStreamReader pesPayloadReader;
        private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[10]);
        private boolean ptsFlag;
        private boolean seenFirstDts;
        private int state = 0;
        private long timeUs;
        private final TimestampAdjuster timestampAdjuster;

        public PesReader(ElementaryStreamReader pesPayloadReader, TimestampAdjuster timestampAdjuster) {
            super();
            this.pesPayloadReader = pesPayloadReader;
            this.timestampAdjuster = timestampAdjuster;
        }

        public void seek() {
            this.state = 0;
            this.bytesRead = 0;
            this.seenFirstDts = false;
            this.pesPayloadReader.seek();
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                switch (this.state) {
                    case 2:
                        Log.w(TsExtractor.TAG, "Unexpected start indicator reading extended header");
                        break;
                    case 3:
                        if (this.payloadSize != -1) {
                            Log.w(TsExtractor.TAG, "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
                        }
                        this.pesPayloadReader.packetFinished();
                        break;
                }
                setState(1);
            }
            while (data.bytesLeft() > 0) {
                switch (this.state) {
                    case 0:
                        data.skipBytes(data.bytesLeft());
                        break;
                    case 1:
                        if (!continueRead(data, this.pesScratch.data, 9)) {
                            break;
                        }
                        setState(parseHeader() ? 2 : 0);
                        break;
                    case 2:
                        if (continueRead(data, this.pesScratch.data, Math.min(10, this.extendedHeaderLength)) && continueRead(data, null, this.extendedHeaderLength)) {
                            parseHeaderExtension();
                            this.pesPayloadReader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
                            setState(3);
                            break;
                        }
                    case 3:
                        int padding;
                        int readLength = data.bytesLeft();
                        if (this.payloadSize == -1) {
                            padding = 0;
                        } else {
                            padding = readLength - this.payloadSize;
                        }
                        if (padding > 0) {
                            readLength -= padding;
                            data.setLimit(data.getPosition() + readLength);
                        }
                        this.pesPayloadReader.consume(data);
                        if (this.payloadSize == -1) {
                            break;
                        }
                        this.payloadSize -= readLength;
                        if (this.payloadSize != 0) {
                            break;
                        }
                        this.pesPayloadReader.packetFinished();
                        setState(1);
                        break;
                    default:
                        break;
                }
            }
        }

        private void setState(int state) {
            this.state = state;
            this.bytesRead = 0;
        }

        private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
            int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
            if (bytesToRead <= 0) {
                return true;
            }
            if (target == null) {
                source.skipBytes(bytesToRead);
            } else {
                source.readBytes(target, this.bytesRead, bytesToRead);
            }
            this.bytesRead += bytesToRead;
            if (this.bytesRead != targetLength) {
                return false;
            }
            return true;
        }

        private boolean parseHeader() {
            this.pesScratch.setPosition(0);
            int startCodePrefix = this.pesScratch.readBits(24);
            if (startCodePrefix != 1) {
                Log.w(TsExtractor.TAG, "Unexpected start code prefix: " + startCodePrefix);
                this.payloadSize = -1;
                return false;
            }
            this.pesScratch.skipBits(8);
            int packetLength = this.pesScratch.readBits(16);
            this.pesScratch.skipBits(5);
            this.dataAlignmentIndicator = this.pesScratch.readBit();
            this.pesScratch.skipBits(2);
            this.ptsFlag = this.pesScratch.readBit();
            this.dtsFlag = this.pesScratch.readBit();
            this.pesScratch.skipBits(6);
            this.extendedHeaderLength = this.pesScratch.readBits(8);
            if (packetLength == 0) {
                this.payloadSize = -1;
            } else {
                this.payloadSize = ((packetLength + 6) - 9) - this.extendedHeaderLength;
            }
            return true;
        }

        private void parseHeaderExtension() {
            this.pesScratch.setPosition(0);
            this.timeUs = C.TIME_UNSET;
            if (this.ptsFlag) {
                this.pesScratch.skipBits(4);
                long pts = ((long) this.pesScratch.readBits(3)) << 30;
                this.pesScratch.skipBits(1);
                pts |= (long) (this.pesScratch.readBits(15) << 15);
                this.pesScratch.skipBits(1);
                pts |= (long) this.pesScratch.readBits(15);
                this.pesScratch.skipBits(1);
                if (!this.seenFirstDts && this.dtsFlag) {
                    this.pesScratch.skipBits(4);
                    long dts = ((long) this.pesScratch.readBits(3)) << 30;
                    this.pesScratch.skipBits(1);
                    dts |= (long) (this.pesScratch.readBits(15) << 15);
                    this.pesScratch.skipBits(1);
                    dts |= (long) this.pesScratch.readBits(15);
                    this.pesScratch.skipBits(1);
                    this.timestampAdjuster.adjustTsTimestamp(dts);
                    this.seenFirstDts = true;
                }
                this.timeUs = this.timestampAdjuster.adjustTsTimestamp(pts);
            }
        }
    }

    private class PmtReader extends TsPayloadReader {
        private static final int TS_PMT_DESC_AC3 = 106;
        private static final int TS_PMT_DESC_DTS = 123;
        private static final int TS_PMT_DESC_EAC3 = 122;
        private static final int TS_PMT_DESC_ISO639_LANG = 10;
        private static final int TS_PMT_DESC_REGISTRATION = 5;
        private int crc;
        private final int pid;
        private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
        private int sectionBytesRead;
        private final ParsableByteArray sectionData = new ParsableByteArray();
        private int sectionLength;

        public PmtReader(int pid) {
            super();
            this.pid = pid;
        }

        public void seek() {
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                data.skipBytes(data.readUnsignedByte());
                data.readBytes(this.pmtScratch, 3);
                this.pmtScratch.skipBits(12);
                this.sectionLength = this.pmtScratch.readBits(12);
                this.sectionBytesRead = 0;
                this.crc = Util.crc(this.pmtScratch.data, 0, 3, -1);
                this.sectionData.reset(this.sectionLength);
            }
            int bytesToRead = Math.min(data.bytesLeft(), this.sectionLength - this.sectionBytesRead);
            data.readBytes(this.sectionData.data, this.sectionBytesRead, bytesToRead);
            this.sectionBytesRead += bytesToRead;
            if (this.sectionBytesRead >= this.sectionLength && Util.crc(this.sectionData.data, 0, this.sectionLength, this.crc) == 0) {
                this.sectionData.skipBytes(7);
                this.sectionData.readBytes(this.pmtScratch, 2);
                this.pmtScratch.skipBits(4);
                int programInfoLength = this.pmtScratch.readBits(12);
                this.sectionData.skipBytes(programInfoLength);
                if (TsExtractor.this.mapByType && TsExtractor.this.id3Reader == null) {
                    TsExtractor.this.id3Reader = TsExtractor.this.streamReaderFactory.createStreamReader(21, new EsInfo(21, null, new byte[0]));
                    TsExtractor.this.id3Reader.init(output, new TrackIdGenerator(21, 8192));
                }
                int remainingEntriesLength = ((this.sectionLength - 9) - programInfoLength) - 4;
                while (remainingEntriesLength > 0) {
                    int trackId;
                    this.sectionData.readBytes(this.pmtScratch, 5);
                    int streamType = this.pmtScratch.readBits(8);
                    this.pmtScratch.skipBits(3);
                    int elementaryPid = this.pmtScratch.readBits(13);
                    this.pmtScratch.skipBits(4);
                    int esInfoLength = this.pmtScratch.readBits(12);
                    EsInfo esInfo = readEsInfo(this.sectionData, esInfoLength);
                    if (streamType == 6) {
                        streamType = esInfo.streamType;
                    }
                    remainingEntriesLength -= esInfoLength + 5;
                    if (TsExtractor.this.mapByType) {
                        trackId = streamType;
                    } else {
                        trackId = elementaryPid;
                    }
                    if (!TsExtractor.this.trackIds.get(trackId)) {
                        ElementaryStreamReader pesPayloadReader;
                        TsExtractor.this.trackIds.put(trackId, true);
                        if (TsExtractor.this.mapByType && streamType == 21) {
                            pesPayloadReader = TsExtractor.this.id3Reader;
                        } else {
                            pesPayloadReader = TsExtractor.this.streamReaderFactory.createStreamReader(streamType, esInfo);
                            pesPayloadReader.init(output, new TrackIdGenerator(trackId, 8192));
                        }
                        if (pesPayloadReader != null) {
                            TsExtractor.this.tsPayloadReaders.put(elementaryPid, new PesReader(pesPayloadReader, TsExtractor.this.timestampAdjuster));
                        }
                    }
                }
                if (!TsExtractor.this.mapByType) {
                    TsExtractor.this.tsPayloadReaders.remove(0);
                    TsExtractor.this.tsPayloadReaders.remove(this.pid);
                    output.endTracks();
                } else if (!TsExtractor.this.tracksEnded) {
                    output.endTracks();
                }
                TsExtractor.this.tracksEnded = true;
            }
        }

        private EsInfo readEsInfo(ParsableByteArray data, int length) {
            int descriptorsStartPosition = data.getPosition();
            int descriptorsEndPosition = descriptorsStartPosition + length;
            int streamType = -1;
            String language = null;
            while (data.getPosition() < descriptorsEndPosition) {
                int descriptorTag = data.readUnsignedByte();
                int positionOfNextDescriptor = data.getPosition() + data.readUnsignedByte();
                if (descriptorTag == 5) {
                    long formatIdentifier = data.readUnsignedInt();
                    if (formatIdentifier == TsExtractor.AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                    } else if (formatIdentifier == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    } else if (formatIdentifier == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
                        streamType = 36;
                    }
                } else if (descriptorTag == TS_PMT_DESC_AC3) {
                    streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                } else if (descriptorTag == TS_PMT_DESC_EAC3) {
                    streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                } else if (descriptorTag == TS_PMT_DESC_DTS) {
                    streamType = TsExtractor.TS_STREAM_TYPE_DTS;
                } else if (descriptorTag == 10) {
                    language = new String(data.data, data.getPosition(), 3).trim();
                }
                data.skipBytes(positionOfNextDescriptor - data.getPosition());
            }
            data.setPosition(descriptorsEndPosition);
            return new EsInfo(streamType, language, Arrays.copyOfRange(this.sectionData.data, descriptorsStartPosition, descriptorsEndPosition));
        }
    }

    public TsExtractor() {
        this(new TimestampAdjuster(0));
    }

    public TsExtractor(TimestampAdjuster timestampAdjuster) {
        this(timestampAdjuster, new DefaultStreamReaderFactory(), false);
    }

    public TsExtractor(TimestampAdjuster timestampAdjuster, Factory customReaderFactory, boolean mapByType) {
        this.timestampAdjuster = timestampAdjuster;
        this.streamReaderFactory = (Factory) Assertions.checkNotNull(customReaderFactory);
        this.mapByType = mapByType;
        this.tsPacketBuffer = new ParsableByteArray((int) BUFFER_SIZE);
        this.tsScratch = new ParsableBitArray(new byte[3]);
        this.trackIds = new SparseBooleanArray();
        this.tsPayloadReaders = new SparseArray();
        this.continuityCounters = new SparseIntArray();
        resetPayloadReaders();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] buffer = this.tsPacketBuffer.data;
        input.peekFully(buffer, 0, BUFFER_SIZE);
        int j = 0;
        while (j < TS_PACKET_SIZE) {
            int i = 0;
            while (i != 5) {
                if (buffer[(i * TS_PACKET_SIZE) + j] != (byte) 71) {
                    j++;
                } else {
                    i++;
                }
            }
            input.skipFully(j);
            return true;
        }
        return false;
    }

    public void init(ExtractorOutput output) {
        this.output = output;
        output.seekMap(new Unseekable(C.TIME_UNSET));
    }

    public void seek(long position) {
        this.timestampAdjuster.reset();
        this.tsPacketBuffer.reset();
        this.continuityCounters.clear();
        resetPayloadReaders();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int limit;
        byte[] data = this.tsPacketBuffer.data;
        if (940 - this.tsPacketBuffer.getPosition() < TS_PACKET_SIZE) {
            int bytesLeft = this.tsPacketBuffer.bytesLeft();
            if (bytesLeft > 0) {
                System.arraycopy(data, this.tsPacketBuffer.getPosition(), data, 0, bytesLeft);
            }
            this.tsPacketBuffer.reset(data, bytesLeft);
        }
        while (this.tsPacketBuffer.bytesLeft() < TS_PACKET_SIZE) {
            limit = this.tsPacketBuffer.limit();
            int read = input.read(data, limit, 940 - limit);
            if (read == -1) {
                return -1;
            }
            this.tsPacketBuffer.setLimit(limit + read);
        }
        limit = this.tsPacketBuffer.limit();
        int position = this.tsPacketBuffer.getPosition();
        while (position < limit && data[position] != TS_SYNC_BYTE) {
            position++;
        }
        this.tsPacketBuffer.setPosition(position);
        int endOfPacket = position + TS_PACKET_SIZE;
        if (endOfPacket > limit) {
            return 0;
        }
        this.tsPacketBuffer.skipBytes(1);
        this.tsPacketBuffer.readBytes(this.tsScratch, 3);
        if (this.tsScratch.readBit()) {
            this.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        boolean payloadUnitStartIndicator = this.tsScratch.readBit();
        this.tsScratch.skipBits(1);
        int pid = this.tsScratch.readBits(13);
        this.tsScratch.skipBits(2);
        boolean adaptationFieldExists = this.tsScratch.readBit();
        boolean payloadExists = this.tsScratch.readBit();
        boolean discontinuityFound = false;
        int continuityCounter = this.tsScratch.readBits(4);
        int previousCounter = this.continuityCounters.get(pid, continuityCounter - 1);
        this.continuityCounters.put(pid, continuityCounter);
        if (previousCounter == continuityCounter) {
            this.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        if (continuityCounter != (previousCounter + 1) % 16) {
            discontinuityFound = true;
        }
        if (adaptationFieldExists) {
            this.tsPacketBuffer.skipBytes(this.tsPacketBuffer.readUnsignedByte());
        }
        if (payloadExists) {
            TsPayloadReader payloadReader = (TsPayloadReader) this.tsPayloadReaders.get(pid);
            if (payloadReader != null) {
                if (discontinuityFound) {
                    payloadReader.seek();
                }
                this.tsPacketBuffer.setLimit(endOfPacket);
                payloadReader.consume(this.tsPacketBuffer, payloadUnitStartIndicator, this.output);
                Assertions.checkState(this.tsPacketBuffer.getPosition() <= endOfPacket);
                this.tsPacketBuffer.setLimit(limit);
            }
        }
        this.tsPacketBuffer.setPosition(endOfPacket);
        return 0;
    }

    private void resetPayloadReaders() {
        this.trackIds.clear();
        this.tsPayloadReaders.clear();
        this.tsPayloadReaders.put(0, new PatReader());
        this.id3Reader = null;
    }
}
