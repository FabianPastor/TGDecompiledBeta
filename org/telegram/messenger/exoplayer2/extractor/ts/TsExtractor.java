package org.telegram.messenger.exoplayer2.extractor.ts;

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
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.Factory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
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
    public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
    private static final int TS_SYNC_BYTE = 71;
    private final SparseIntArray continuityCounters;
    private TsPayloadReader id3Reader;
    private final boolean mapByType;
    private ExtractorOutput output;
    private final Factory payloadReaderFactory;
    private final TimestampAdjuster timestampAdjuster;
    private final SparseBooleanArray trackIds;
    private boolean tracksEnded;
    private final ParsableByteArray tsPacketBuffer;
    private final SparseArray<TsPayloadReader> tsPayloadReaders;
    private final ParsableBitArray tsScratch;

    private class PatReader implements SectionPayloadReader {
        private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            sectionData.skipBytes(8);
            int programCount = sectionData.bytesLeft() / 4;
            for (int i = 0; i < programCount; i++) {
                sectionData.readBytes(this.patScratch, 4);
                int programNumber = this.patScratch.readBits(16);
                this.patScratch.skipBits(3);
                if (programNumber == 0) {
                    this.patScratch.skipBits(13);
                } else {
                    int pid = this.patScratch.readBits(13);
                    TsExtractor.this.tsPayloadReaders.put(pid, new SectionReader(new PmtReader(pid)));
                }
            }
        }
    }

    private class PmtReader implements SectionPayloadReader {
        private static final int TS_PMT_DESC_AC3 = 106;
        private static final int TS_PMT_DESC_DTS = 123;
        private static final int TS_PMT_DESC_EAC3 = 122;
        private static final int TS_PMT_DESC_ISO639_LANG = 10;
        private static final int TS_PMT_DESC_REGISTRATION = 5;
        private final int pid;
        private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);

        public PmtReader(int pid) {
            this.pid = pid;
        }

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            sectionData.skipBytes(10);
            sectionData.readBytes(this.pmtScratch, 2);
            this.pmtScratch.skipBits(4);
            sectionData.skipBytes(this.pmtScratch.readBits(12));
            if (TsExtractor.this.mapByType && TsExtractor.this.id3Reader == null) {
                TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, new byte[0]));
                TsExtractor.this.id3Reader.init(TsExtractor.this.timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(21, 8192));
            }
            int remainingEntriesLength = sectionData.bytesLeft();
            while (remainingEntriesLength > 0) {
                int trackId;
                sectionData.readBytes(this.pmtScratch, 5);
                int streamType = this.pmtScratch.readBits(8);
                this.pmtScratch.skipBits(3);
                int elementaryPid = this.pmtScratch.readBits(13);
                this.pmtScratch.skipBits(4);
                int esInfoLength = this.pmtScratch.readBits(12);
                EsInfo esInfo = readEsInfo(sectionData, esInfoLength);
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
                    TsPayloadReader reader;
                    TsExtractor.this.trackIds.put(trackId, true);
                    if (TsExtractor.this.mapByType && streamType == 21) {
                        reader = TsExtractor.this.id3Reader;
                    } else {
                        reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                        if (reader != null) {
                            reader.init(TsExtractor.this.timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(trackId, 8192));
                        }
                    }
                    if (reader != null) {
                        TsExtractor.this.tsPayloadReaders.put(elementaryPid, reader);
                    }
                }
            }
            if (!TsExtractor.this.mapByType) {
                TsExtractor.this.tsPayloadReaders.remove(0);
                TsExtractor.this.tsPayloadReaders.remove(this.pid);
                TsExtractor.this.output.endTracks();
            } else if (!TsExtractor.this.tracksEnded) {
                TsExtractor.this.output.endTracks();
            }
            TsExtractor.this.tracksEnded = true;
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
            return new EsInfo(streamType, language, Arrays.copyOfRange(data.data, descriptorsStartPosition, descriptorsEndPosition));
        }
    }

    public TsExtractor() {
        this(new TimestampAdjuster(0));
    }

    public TsExtractor(TimestampAdjuster timestampAdjuster) {
        this(timestampAdjuster, new DefaultTsPayloadReaderFactory(), false);
    }

    public TsExtractor(TimestampAdjuster timestampAdjuster, Factory payloadReaderFactory, boolean mapByType) {
        this.timestampAdjuster = timestampAdjuster;
        this.payloadReaderFactory = (Factory) Assertions.checkNotNull(payloadReaderFactory);
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
            if (payloadExists) {
                this.tsPacketBuffer.setPosition(endOfPacket);
                return 0;
            }
        } else if (continuityCounter != (previousCounter + 1) % 16) {
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
                payloadReader.consume(this.tsPacketBuffer, payloadUnitStartIndicator);
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
        SparseArray<TsPayloadReader> initialPayloadReaders = this.payloadReaderFactory.createInitialPayloadReaders();
        int initialPayloadReadersSize = initialPayloadReaders.size();
        for (int i = 0; i < initialPayloadReadersSize; i++) {
            this.tsPayloadReaders.put(initialPayloadReaders.keyAt(i), initialPayloadReaders.valueAt(i));
        }
        this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
        this.id3Reader = null;
    }
}
