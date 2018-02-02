package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.DvbSubtitleInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.Factory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("AC-3"));
    private static final int BUFFER_SIZE = 9400;
    private static final long E_AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("EAC3"));
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new TsExtractor()};
        }
    };
    private static final long HEVC_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("HEVC"));
    private static final int MAX_PID_PLUS_ONE = 8192;
    public static final int MODE_HLS = 2;
    public static final int MODE_MULTI_PMT = 0;
    public static final int MODE_SINGLE_PMT = 1;
    private static final int SNIFF_TS_PACKET_COUNT = 5;
    private static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    public static final int TS_STREAM_TYPE_AAC_ADTS = 15;
    public static final int TS_STREAM_TYPE_AAC_LATM = 17;
    public static final int TS_STREAM_TYPE_AC3 = 129;
    public static final int TS_STREAM_TYPE_DTS = 138;
    public static final int TS_STREAM_TYPE_DVBSUBS = 89;
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
    private int bytesSinceLastSync;
    private final SparseIntArray continuityCounters;
    private TsPayloadReader id3Reader;
    private final int mode;
    private ExtractorOutput output;
    private final Factory payloadReaderFactory;
    private int remainingPmts;
    private final List<TimestampAdjuster> timestampAdjusters;
    private final SparseBooleanArray trackIds;
    private boolean tracksEnded;
    private final ParsableByteArray tsPacketBuffer;
    private final SparseArray<TsPayloadReader> tsPayloadReaders;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    private class PatReader implements SectionPayloadReader {
        private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            if (sectionData.readUnsignedByte() == 0) {
                sectionData.skipBytes(7);
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
                        TsExtractor.this.remainingPmts = TsExtractor.this.remainingPmts + 1;
                    }
                }
                if (TsExtractor.this.mode != 2) {
                    TsExtractor.this.tsPayloadReaders.remove(0);
                }
            }
        }
    }

    private class PmtReader implements SectionPayloadReader {
        private static final int TS_PMT_DESC_AC3 = 106;
        private static final int TS_PMT_DESC_DTS = 123;
        private static final int TS_PMT_DESC_DVBSUBS = 89;
        private static final int TS_PMT_DESC_EAC3 = 122;
        private static final int TS_PMT_DESC_ISO639_LANG = 10;
        private static final int TS_PMT_DESC_REGISTRATION = 5;
        private final int pid;
        private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
        private final SparseIntArray trackIdToPidScratch = new SparseIntArray();
        private final SparseArray<TsPayloadReader> trackIdToReaderScratch = new SparseArray();

        public PmtReader(int pid) {
            this.pid = pid;
        }

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            if (sectionData.readUnsignedByte() == 2) {
                TimestampAdjuster timestampAdjuster;
                int trackId;
                TsPayloadReader reader;
                if (TsExtractor.this.mode == 1 || TsExtractor.this.mode == 2 || TsExtractor.this.remainingPmts == 1) {
                    timestampAdjuster = (TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0);
                } else {
                    timestampAdjuster = new TimestampAdjuster(((TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
                    TsExtractor.this.timestampAdjusters.add(timestampAdjuster);
                }
                sectionData.skipBytes(2);
                int programNumber = sectionData.readUnsignedShort();
                sectionData.skipBytes(5);
                sectionData.readBytes(this.pmtScratch, 2);
                this.pmtScratch.skipBits(4);
                sectionData.skipBytes(this.pmtScratch.readBits(12));
                if (TsExtractor.this.mode == 2 && TsExtractor.this.id3Reader == null) {
                    TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, null, new byte[0]));
                    TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, 21, 8192));
                }
                this.trackIdToReaderScratch.clear();
                this.trackIdToPidScratch.clear();
                int remainingEntriesLength = sectionData.bytesLeft();
                while (remainingEntriesLength > 0) {
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
                    if (TsExtractor.this.mode == 2) {
                        trackId = streamType;
                    } else {
                        trackId = elementaryPid;
                    }
                    if (!TsExtractor.this.trackIds.get(trackId)) {
                        if (TsExtractor.this.mode == 2 && streamType == 21) {
                            reader = TsExtractor.this.id3Reader;
                        } else {
                            reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                        }
                        if (TsExtractor.this.mode != 2 || elementaryPid < this.trackIdToPidScratch.get(trackId, 8192)) {
                            this.trackIdToPidScratch.put(trackId, elementaryPid);
                            this.trackIdToReaderScratch.put(trackId, reader);
                        }
                    }
                }
                int trackIdCount = this.trackIdToPidScratch.size();
                for (int i = 0; i < trackIdCount; i++) {
                    trackId = this.trackIdToPidScratch.keyAt(i);
                    TsExtractor.this.trackIds.put(trackId, true);
                    reader = (TsPayloadReader) this.trackIdToReaderScratch.valueAt(i);
                    if (reader != null) {
                        if (reader != TsExtractor.this.id3Reader) {
                            reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, trackId, 8192));
                        }
                        TsExtractor.this.tsPayloadReaders.put(this.trackIdToPidScratch.valueAt(i), reader);
                    }
                }
                if (TsExtractor.this.mode != 2) {
                    TsExtractor.this.tsPayloadReaders.remove(this.pid);
                    TsExtractor.this.remainingPmts = TsExtractor.this.mode == 1 ? 0 : TsExtractor.this.remainingPmts - 1;
                    if (TsExtractor.this.remainingPmts == 0) {
                        TsExtractor.this.output.endTracks();
                        TsExtractor.this.tracksEnded = true;
                    }
                } else if (!TsExtractor.this.tracksEnded) {
                    TsExtractor.this.output.endTracks();
                    TsExtractor.this.remainingPmts = 0;
                    TsExtractor.this.tracksEnded = true;
                }
            }
        }

        private EsInfo readEsInfo(ParsableByteArray data, int length) {
            int descriptorsStartPosition = data.getPosition();
            int descriptorsEndPosition = descriptorsStartPosition + length;
            int streamType = -1;
            String language = null;
            List<DvbSubtitleInfo> dvbSubtitleInfos = null;
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
                    language = data.readString(3).trim();
                } else if (descriptorTag == 89) {
                    streamType = 89;
                    dvbSubtitleInfos = new ArrayList();
                    while (data.getPosition() < positionOfNextDescriptor) {
                        String dvbLanguage = data.readString(3).trim();
                        int dvbSubtitlingType = data.readUnsignedByte();
                        byte[] initializationData = new byte[4];
                        data.readBytes(initializationData, 0, 4);
                        dvbSubtitleInfos.add(new DvbSubtitleInfo(dvbLanguage, dvbSubtitlingType, initializationData));
                    }
                }
                data.skipBytes(positionOfNextDescriptor - data.getPosition());
            }
            data.setPosition(descriptorsEndPosition);
            return new EsInfo(streamType, language, dvbSubtitleInfos, Arrays.copyOfRange(data.data, descriptorsStartPosition, descriptorsEndPosition));
        }
    }

    public TsExtractor() {
        this(0);
    }

    public TsExtractor(int defaultTsPayloadReaderFlags) {
        this(1, defaultTsPayloadReaderFlags);
    }

    public TsExtractor(int mode, int defaultTsPayloadReaderFlags) {
        this(mode, new TimestampAdjuster(0), new DefaultTsPayloadReaderFactory(defaultTsPayloadReaderFlags));
    }

    public TsExtractor(int mode, TimestampAdjuster timestampAdjuster, Factory payloadReaderFactory) {
        this.payloadReaderFactory = (Factory) Assertions.checkNotNull(payloadReaderFactory);
        this.mode = mode;
        if (mode == 1 || mode == 2) {
            this.timestampAdjusters = Collections.singletonList(timestampAdjuster);
        } else {
            this.timestampAdjusters = new ArrayList();
            this.timestampAdjusters.add(timestampAdjuster);
        }
        this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], 0);
        this.trackIds = new SparseBooleanArray();
        this.tsPayloadReaders = new SparseArray();
        this.continuityCounters = new SparseIntArray();
        resetPayloadReaders();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] buffer = this.tsPacketBuffer.data;
        input.peekFully(buffer, 0, 940);
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

    public void seek(long position, long timeUs) {
        int timestampAdjustersCount = this.timestampAdjusters.size();
        for (int i = 0; i < timestampAdjustersCount; i++) {
            ((TimestampAdjuster) this.timestampAdjusters.get(i)).reset();
        }
        this.tsPacketBuffer.reset();
        this.continuityCounters.clear();
        resetPayloadReaders();
        this.bytesSinceLastSync = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int limit;
        byte[] data = this.tsPacketBuffer.data;
        if (9400 - this.tsPacketBuffer.getPosition() < TS_PACKET_SIZE) {
            int bytesLeft = this.tsPacketBuffer.bytesLeft();
            if (bytesLeft > 0) {
                System.arraycopy(data, this.tsPacketBuffer.getPosition(), data, 0, bytesLeft);
            }
            this.tsPacketBuffer.reset(data, bytesLeft);
        }
        while (this.tsPacketBuffer.bytesLeft() < TS_PACKET_SIZE) {
            limit = this.tsPacketBuffer.limit();
            int read = input.read(data, limit, 9400 - limit);
            if (read == -1) {
                return -1;
            }
            this.tsPacketBuffer.setLimit(limit + read);
        }
        limit = this.tsPacketBuffer.limit();
        int position = this.tsPacketBuffer.getPosition();
        int searchStart = position;
        while (position < limit && data[position] != TS_SYNC_BYTE) {
            position++;
        }
        this.tsPacketBuffer.setPosition(position);
        int endOfPacket = position + TS_PACKET_SIZE;
        if (endOfPacket > limit) {
            this.bytesSinceLastSync += position - searchStart;
            if (this.mode != 2 || this.bytesSinceLastSync <= 376) {
                return 0;
            }
            throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
        }
        this.bytesSinceLastSync = 0;
        int tsPacketHeader = this.tsPacketBuffer.readInt();
        if ((8388608 & tsPacketHeader) != 0) {
            this.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        boolean payloadUnitStartIndicator = (4194304 & tsPacketHeader) != 0;
        int pid = (2096896 & tsPacketHeader) >> 8;
        boolean adaptationFieldExists = (tsPacketHeader & 32) != 0;
        TsPayloadReader payloadReader = (tsPacketHeader & 16) != 0 ? (TsPayloadReader) this.tsPayloadReaders.get(pid) : null;
        if (payloadReader == null) {
            this.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        if (this.mode != 2) {
            int continuityCounter = tsPacketHeader & 15;
            int previousCounter = this.continuityCounters.get(pid, continuityCounter - 1);
            this.continuityCounters.put(pid, continuityCounter);
            if (previousCounter == continuityCounter) {
                this.tsPacketBuffer.setPosition(endOfPacket);
                return 0;
            } else if (continuityCounter != ((previousCounter + 1) & 15)) {
                payloadReader.seek();
            }
        }
        if (adaptationFieldExists) {
            this.tsPacketBuffer.skipBytes(this.tsPacketBuffer.readUnsignedByte());
        }
        this.tsPacketBuffer.setLimit(endOfPacket);
        payloadReader.consume(this.tsPacketBuffer, payloadUnitStartIndicator);
        this.tsPacketBuffer.setLimit(limit);
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
