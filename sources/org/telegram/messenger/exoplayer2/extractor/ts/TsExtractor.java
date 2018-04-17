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
import org.telegram.messenger.exoplayer2.C0542C;
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
    public static final ExtractorsFactory FACTORY = new C18461();
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

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor$1 */
    static class C18461 implements ExtractorsFactory {
        C18461() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new TsExtractor()};
        }
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
            PmtReader pmtReader = this;
            ParsableByteArray parsableByteArray = sectionData;
            if (sectionData.readUnsignedByte() == 2) {
                TimestampAdjuster timestampAdjuster;
                int programNumber;
                int i;
                int i2;
                int i3;
                int i4;
                int remainingEntriesLength;
                int streamType;
                int elementaryPid;
                int esInfoLength;
                EsInfo esInfo;
                TsPayloadReader reader;
                TsExtractor tsExtractor;
                if (!(TsExtractor.this.mode == 1 || TsExtractor.this.mode == 2)) {
                    if (TsExtractor.this.remainingPmts != 1) {
                        timestampAdjuster = new TimestampAdjuster(((TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
                        TsExtractor.this.timestampAdjusters.add(timestampAdjuster);
                        parsableByteArray.skipBytes(2);
                        programNumber = sectionData.readUnsignedShort();
                        i = 5;
                        parsableByteArray.skipBytes(5);
                        parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                        i2 = 4;
                        pmtReader.pmtScratch.skipBits(4);
                        i3 = 12;
                        parsableByteArray.skipBytes(pmtReader.pmtScratch.readBits(12));
                        i4 = 21;
                        if (TsExtractor.this.mode == 2 && TsExtractor.this.id3Reader == null) {
                            TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, null, new byte[0]));
                            TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, 21, 8192));
                        }
                        pmtReader.trackIdToReaderScratch.clear();
                        pmtReader.trackIdToPidScratch.clear();
                        remainingEntriesLength = sectionData.bytesLeft();
                        while (remainingEntriesLength > 0) {
                            parsableByteArray.readBytes(pmtReader.pmtScratch, i);
                            streamType = pmtReader.pmtScratch.readBits(8);
                            pmtReader.pmtScratch.skipBits(3);
                            elementaryPid = pmtReader.pmtScratch.readBits(13);
                            pmtReader.pmtScratch.skipBits(i2);
                            esInfoLength = pmtReader.pmtScratch.readBits(i3);
                            esInfo = readEsInfo(parsableByteArray, esInfoLength);
                            if (streamType == 6) {
                                streamType = esInfo.streamType;
                            }
                            remainingEntriesLength -= esInfoLength + 5;
                            i2 = TsExtractor.this.mode != 2 ? streamType : elementaryPid;
                            if (TsExtractor.this.trackIds.get(i2)) {
                                if (TsExtractor.this.mode == 2 || streamType != r14) {
                                    reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                                } else {
                                    reader = TsExtractor.this.id3Reader;
                                }
                                if (TsExtractor.this.mode != 2 || elementaryPid < pmtReader.trackIdToPidScratch.get(i2, 8192)) {
                                    pmtReader.trackIdToPidScratch.put(i2, elementaryPid);
                                    pmtReader.trackIdToReaderScratch.put(i2, reader);
                                }
                            }
                            i = 5;
                            i2 = 4;
                            i3 = 12;
                            i4 = 21;
                        }
                        streamType = pmtReader.trackIdToPidScratch.size();
                        for (i = 0; i < streamType; i++) {
                            i2 = pmtReader.trackIdToPidScratch.keyAt(i);
                            TsExtractor.this.trackIds.put(i2, true);
                            reader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(i);
                            if (reader == null) {
                                if (reader != TsExtractor.this.id3Reader) {
                                    reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, i2, 8192));
                                }
                                TsExtractor.this.tsPayloadReaders.put(pmtReader.trackIdToPidScratch.valueAt(i), reader);
                            }
                        }
                        if (TsExtractor.this.mode == 2) {
                            i = 0;
                            TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                            tsExtractor = TsExtractor.this;
                            if (TsExtractor.this.mode == 1) {
                                i = TsExtractor.this.remainingPmts - 1;
                            }
                            tsExtractor.remainingPmts = i;
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
                timestampAdjuster = (TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0);
                parsableByteArray.skipBytes(2);
                programNumber = sectionData.readUnsignedShort();
                i = 5;
                parsableByteArray.skipBytes(5);
                parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                i2 = 4;
                pmtReader.pmtScratch.skipBits(4);
                i3 = 12;
                parsableByteArray.skipBytes(pmtReader.pmtScratch.readBits(12));
                i4 = 21;
                TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, null, new byte[0]));
                TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, 21, 8192));
                pmtReader.trackIdToReaderScratch.clear();
                pmtReader.trackIdToPidScratch.clear();
                remainingEntriesLength = sectionData.bytesLeft();
                while (remainingEntriesLength > 0) {
                    parsableByteArray.readBytes(pmtReader.pmtScratch, i);
                    streamType = pmtReader.pmtScratch.readBits(8);
                    pmtReader.pmtScratch.skipBits(3);
                    elementaryPid = pmtReader.pmtScratch.readBits(13);
                    pmtReader.pmtScratch.skipBits(i2);
                    esInfoLength = pmtReader.pmtScratch.readBits(i3);
                    esInfo = readEsInfo(parsableByteArray, esInfoLength);
                    if (streamType == 6) {
                        streamType = esInfo.streamType;
                    }
                    remainingEntriesLength -= esInfoLength + 5;
                    if (TsExtractor.this.mode != 2) {
                    }
                    if (TsExtractor.this.trackIds.get(i2)) {
                        if (TsExtractor.this.mode == 2) {
                        }
                        reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                        pmtReader.trackIdToPidScratch.put(i2, elementaryPid);
                        pmtReader.trackIdToReaderScratch.put(i2, reader);
                    }
                    i = 5;
                    i2 = 4;
                    i3 = 12;
                    i4 = 21;
                }
                streamType = pmtReader.trackIdToPidScratch.size();
                for (i = 0; i < streamType; i++) {
                    i2 = pmtReader.trackIdToPidScratch.keyAt(i);
                    TsExtractor.this.trackIds.put(i2, true);
                    reader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(i);
                    if (reader == null) {
                        if (reader != TsExtractor.this.id3Reader) {
                            reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, i2, 8192));
                        }
                        TsExtractor.this.tsPayloadReaders.put(pmtReader.trackIdToPidScratch.valueAt(i), reader);
                    }
                }
                if (TsExtractor.this.mode == 2) {
                    i = 0;
                    TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                    tsExtractor = TsExtractor.this;
                    if (TsExtractor.this.mode == 1) {
                        i = TsExtractor.this.remainingPmts - 1;
                    }
                    tsExtractor.remainingPmts = i;
                    if (TsExtractor.this.remainingPmts == 0) {
                        TsExtractor.this.output.endTracks();
                        TsExtractor.this.tracksEnded = true;
                    }
                } else if (TsExtractor.this.tracksEnded) {
                    TsExtractor.this.output.endTracks();
                    TsExtractor.this.remainingPmts = 0;
                    TsExtractor.this.tracksEnded = true;
                }
            }
        }

        private EsInfo readEsInfo(ParsableByteArray data, int length) {
            ParsableByteArray parsableByteArray = data;
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
                    language = parsableByteArray.readString(3).trim();
                } else if (descriptorTag == 89) {
                    streamType = 89;
                    dvbSubtitleInfos = new ArrayList();
                    while (data.getPosition() < positionOfNextDescriptor) {
                        String dvbLanguage = parsableByteArray.readString(3).trim();
                        int dvbSubtitlingType = data.readUnsignedByte();
                        byte[] initializationData = new byte[4];
                        parsableByteArray.readBytes(initializationData, 0, 4);
                        dvbSubtitleInfos.add(new DvbSubtitleInfo(dvbLanguage, dvbSubtitlingType, initializationData));
                    }
                }
                parsableByteArray.skipBytes(positionOfNextDescriptor - data.getPosition());
            }
            parsableByteArray.setPosition(descriptorsEndPosition);
            return new EsInfo(streamType, language, dvbSubtitleInfos, Arrays.copyOfRange(parsableByteArray.data, descriptorsStartPosition, descriptorsEndPosition));
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
        if (mode != 1) {
            if (mode != 2) {
                this.timestampAdjusters = new ArrayList();
                this.timestampAdjusters.add(timestampAdjuster);
                this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], 0);
                this.trackIds = new SparseBooleanArray();
                this.tsPayloadReaders = new SparseArray();
                this.continuityCounters = new SparseIntArray();
                resetPayloadReaders();
            }
        }
        this.timestampAdjusters = Collections.singletonList(timestampAdjuster);
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
        output.seekMap(new Unseekable(C0542C.TIME_UNSET));
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
        int bytesLeft;
        int read;
        byte[] data = this.tsPacketBuffer.data;
        if (9400 - this.tsPacketBuffer.getPosition() < TS_PACKET_SIZE) {
            bytesLeft = r0.tsPacketBuffer.bytesLeft();
            if (bytesLeft > 0) {
                System.arraycopy(data, r0.tsPacketBuffer.getPosition(), data, 0, bytesLeft);
            }
            r0.tsPacketBuffer.reset(data, bytesLeft);
        }
        while (r0.tsPacketBuffer.bytesLeft() < TS_PACKET_SIZE) {
            bytesLeft = r0.tsPacketBuffer.limit();
            read = input.read(data, bytesLeft, 9400 - bytesLeft);
            if (read == -1) {
                return -1;
            }
            r0.tsPacketBuffer.setLimit(bytesLeft + read);
        }
        ExtractorInput extractorInput = input;
        bytesLeft = r0.tsPacketBuffer.limit();
        int searchStart = r0.tsPacketBuffer.getPosition();
        read = searchStart;
        while (read < bytesLeft && data[read] != (byte) 71) {
            read++;
        }
        r0.tsPacketBuffer.setPosition(read);
        int endOfPacket = read + TS_PACKET_SIZE;
        if (endOfPacket > bytesLeft) {
            r0.bytesSinceLastSync += read - searchStart;
            if (r0.mode != 2 || r0.bytesSinceLastSync <= 376) {
                return 0;
            }
            throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
        }
        r0.bytesSinceLastSync = 0;
        int tsPacketHeader = r0.tsPacketBuffer.readInt();
        if ((8388608 & tsPacketHeader) != 0) {
            r0.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        boolean payloadExists = true;
        boolean payloadUnitStartIndicator = (4194304 & tsPacketHeader) != 0;
        int pid = (2096896 & tsPacketHeader) >> 8;
        boolean adaptationFieldExists = (tsPacketHeader & 32) != 0;
        if ((tsPacketHeader & 16) == 0) {
            payloadExists = false;
        }
        TsPayloadReader payloadReader = payloadExists ? (TsPayloadReader) r0.tsPayloadReaders.get(pid) : null;
        if (payloadReader == null) {
            r0.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        if (r0.mode != 2) {
            int continuityCounter = tsPacketHeader & 15;
            int previousCounter = r0.continuityCounters.get(pid, continuityCounter - 1);
            r0.continuityCounters.put(pid, continuityCounter);
            if (previousCounter == continuityCounter) {
                r0.tsPacketBuffer.setPosition(endOfPacket);
                return 0;
            } else if (continuityCounter != ((previousCounter + 1) & 15)) {
                payloadReader.seek();
            }
        }
        if (adaptationFieldExists) {
            r0.tsPacketBuffer.skipBytes(r0.tsPacketBuffer.readUnsignedByte());
        }
        r0.tsPacketBuffer.setLimit(endOfPacket);
        payloadReader.consume(r0.tsPacketBuffer, payloadUnitStartIndicator);
        r0.tsPacketBuffer.setLimit(bytesLeft);
        r0.tsPacketBuffer.setPosition(endOfPacket);
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
