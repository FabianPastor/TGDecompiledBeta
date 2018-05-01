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

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        }

        public void consume(ParsableByteArray parsableByteArray) {
            if (parsableByteArray.readUnsignedByte() == 0) {
                parsableByteArray.skipBytes(7);
                int bytesLeft = parsableByteArray.bytesLeft() / 4;
                for (int i = 0; i < bytesLeft; i++) {
                    parsableByteArray.readBytes(this.patScratch, 4);
                    int readBits = this.patScratch.readBits(16);
                    this.patScratch.skipBits(3);
                    if (readBits == 0) {
                        this.patScratch.skipBits(13);
                    } else {
                        readBits = this.patScratch.readBits(13);
                        TsExtractor.this.tsPayloadReaders.put(readBits, new SectionReader(new PmtReader(readBits)));
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

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        }

        public PmtReader(int i) {
            this.pid = i;
        }

        public void consume(ParsableByteArray parsableByteArray) {
            PmtReader pmtReader = this;
            ParsableByteArray parsableByteArray2 = parsableByteArray;
            if (parsableByteArray.readUnsignedByte() == 2) {
                TimestampAdjuster timestampAdjuster;
                int readUnsignedShort;
                int i;
                int i2;
                int bytesLeft;
                int readBits;
                int readBits2;
                int readBits3;
                EsInfo readEsInfo;
                Object createPayloadReader;
                int size;
                TsPayloadReader tsPayloadReader;
                TsExtractor tsExtractor;
                int i3 = 0;
                if (!(TsExtractor.this.mode == 1 || TsExtractor.this.mode == 2)) {
                    if (TsExtractor.this.remainingPmts != 1) {
                        timestampAdjuster = new TimestampAdjuster(((TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
                        TsExtractor.this.timestampAdjusters.add(timestampAdjuster);
                        parsableByteArray2.skipBytes(2);
                        readUnsignedShort = parsableByteArray.readUnsignedShort();
                        i = 5;
                        parsableByteArray2.skipBytes(5);
                        parsableByteArray2.readBytes(pmtReader.pmtScratch, 2);
                        i2 = 4;
                        pmtReader.pmtScratch.skipBits(4);
                        parsableByteArray2.skipBytes(pmtReader.pmtScratch.readBits(12));
                        if (TsExtractor.this.mode == 2 && TsExtractor.this.id3Reader == null) {
                            TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, null, new byte[0]));
                            TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(readUnsignedShort, 21, 8192));
                        }
                        pmtReader.trackIdToReaderScratch.clear();
                        pmtReader.trackIdToPidScratch.clear();
                        bytesLeft = parsableByteArray.bytesLeft();
                        while (bytesLeft > 0) {
                            parsableByteArray2.readBytes(pmtReader.pmtScratch, i);
                            readBits = pmtReader.pmtScratch.readBits(8);
                            pmtReader.pmtScratch.skipBits(3);
                            readBits2 = pmtReader.pmtScratch.readBits(13);
                            pmtReader.pmtScratch.skipBits(i2);
                            readBits3 = pmtReader.pmtScratch.readBits(12);
                            readEsInfo = readEsInfo(parsableByteArray2, readBits3);
                            if (readBits == 6) {
                                readBits = readEsInfo.streamType;
                            }
                            bytesLeft -= readBits3 + 5;
                            i2 = TsExtractor.this.mode != 2 ? readBits : readBits2;
                            if (TsExtractor.this.trackIds.get(i2)) {
                                if (TsExtractor.this.mode == 2 || readBits != 21) {
                                    createPayloadReader = TsExtractor.this.payloadReaderFactory.createPayloadReader(readBits, readEsInfo);
                                } else {
                                    createPayloadReader = TsExtractor.this.id3Reader;
                                }
                                if (TsExtractor.this.mode == 2 || readBits2 < pmtReader.trackIdToPidScratch.get(i2, 8192)) {
                                    pmtReader.trackIdToPidScratch.put(i2, readBits2);
                                    pmtReader.trackIdToReaderScratch.put(i2, createPayloadReader);
                                }
                            }
                            i = 5;
                            i2 = 4;
                        }
                        size = pmtReader.trackIdToPidScratch.size();
                        for (i = 0; i < size; i++) {
                            bytesLeft = pmtReader.trackIdToPidScratch.keyAt(i);
                            TsExtractor.this.trackIds.put(bytesLeft, true);
                            tsPayloadReader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(i);
                            if (tsPayloadReader == null) {
                                if (tsPayloadReader != TsExtractor.this.id3Reader) {
                                    tsPayloadReader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(readUnsignedShort, bytesLeft, 8192));
                                }
                                TsExtractor.this.tsPayloadReaders.put(pmtReader.trackIdToPidScratch.valueAt(i), tsPayloadReader);
                            }
                        }
                        if (TsExtractor.this.mode == 2) {
                            TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                            tsExtractor = TsExtractor.this;
                            if (TsExtractor.this.mode == 1) {
                                i3 = TsExtractor.this.remainingPmts - 1;
                            }
                            tsExtractor.remainingPmts = i3;
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
                parsableByteArray2.skipBytes(2);
                readUnsignedShort = parsableByteArray.readUnsignedShort();
                i = 5;
                parsableByteArray2.skipBytes(5);
                parsableByteArray2.readBytes(pmtReader.pmtScratch, 2);
                i2 = 4;
                pmtReader.pmtScratch.skipBits(4);
                parsableByteArray2.skipBytes(pmtReader.pmtScratch.readBits(12));
                TsExtractor.this.id3Reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(21, new EsInfo(21, null, null, new byte[0]));
                TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(readUnsignedShort, 21, 8192));
                pmtReader.trackIdToReaderScratch.clear();
                pmtReader.trackIdToPidScratch.clear();
                bytesLeft = parsableByteArray.bytesLeft();
                while (bytesLeft > 0) {
                    parsableByteArray2.readBytes(pmtReader.pmtScratch, i);
                    readBits = pmtReader.pmtScratch.readBits(8);
                    pmtReader.pmtScratch.skipBits(3);
                    readBits2 = pmtReader.pmtScratch.readBits(13);
                    pmtReader.pmtScratch.skipBits(i2);
                    readBits3 = pmtReader.pmtScratch.readBits(12);
                    readEsInfo = readEsInfo(parsableByteArray2, readBits3);
                    if (readBits == 6) {
                        readBits = readEsInfo.streamType;
                    }
                    bytesLeft -= readBits3 + 5;
                    if (TsExtractor.this.mode != 2) {
                    }
                    if (TsExtractor.this.trackIds.get(i2)) {
                        if (TsExtractor.this.mode == 2) {
                        }
                        createPayloadReader = TsExtractor.this.payloadReaderFactory.createPayloadReader(readBits, readEsInfo);
                        if (TsExtractor.this.mode == 2) {
                        }
                        pmtReader.trackIdToPidScratch.put(i2, readBits2);
                        pmtReader.trackIdToReaderScratch.put(i2, createPayloadReader);
                    }
                    i = 5;
                    i2 = 4;
                }
                size = pmtReader.trackIdToPidScratch.size();
                for (i = 0; i < size; i++) {
                    bytesLeft = pmtReader.trackIdToPidScratch.keyAt(i);
                    TsExtractor.this.trackIds.put(bytesLeft, true);
                    tsPayloadReader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(i);
                    if (tsPayloadReader == null) {
                        if (tsPayloadReader != TsExtractor.this.id3Reader) {
                            tsPayloadReader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(readUnsignedShort, bytesLeft, 8192));
                        }
                        TsExtractor.this.tsPayloadReaders.put(pmtReader.trackIdToPidScratch.valueAt(i), tsPayloadReader);
                    }
                }
                if (TsExtractor.this.mode == 2) {
                    TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                    tsExtractor = TsExtractor.this;
                    if (TsExtractor.this.mode == 1) {
                        i3 = TsExtractor.this.remainingPmts - 1;
                    }
                    tsExtractor.remainingPmts = i3;
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

        private EsInfo readEsInfo(ParsableByteArray parsableByteArray, int i) {
            int position = parsableByteArray.getPosition();
            i += position;
            String str = null;
            int i2 = -1;
            List list = null;
            while (parsableByteArray.getPosition() < i) {
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                int position2 = parsableByteArray.getPosition() + parsableByteArray.readUnsignedByte();
                if (readUnsignedByte == 5) {
                    long readUnsignedInt = parsableByteArray.readUnsignedInt();
                    if (readUnsignedInt != TsExtractor.AC3_FORMAT_IDENTIFIER) {
                        if (readUnsignedInt == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
                            i2 = TsExtractor.TS_STREAM_TYPE_E_AC3;
                            parsableByteArray.skipBytes(position2 - parsableByteArray.getPosition());
                        } else {
                            if (readUnsignedInt == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
                                i2 = 36;
                            }
                            parsableByteArray.skipBytes(position2 - parsableByteArray.getPosition());
                        }
                    }
                } else if (readUnsignedByte != TS_PMT_DESC_AC3) {
                    if (readUnsignedByte != TS_PMT_DESC_EAC3) {
                        if (readUnsignedByte == TS_PMT_DESC_DTS) {
                            i2 = TsExtractor.TS_STREAM_TYPE_DTS;
                        } else if (readUnsignedByte == 10) {
                            str = parsableByteArray.readString(3).trim();
                        } else if (readUnsignedByte == 89) {
                            List arrayList = new ArrayList();
                            while (parsableByteArray.getPosition() < position2) {
                                String trim = parsableByteArray.readString(3).trim();
                                readUnsignedByte = parsableByteArray.readUnsignedByte();
                                byte[] bArr = new byte[4];
                                parsableByteArray.readBytes(bArr, 0, 4);
                                arrayList.add(new DvbSubtitleInfo(trim, readUnsignedByte, bArr));
                            }
                            list = arrayList;
                            i2 = 89;
                        }
                        parsableByteArray.skipBytes(position2 - parsableByteArray.getPosition());
                    }
                    i2 = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    parsableByteArray.skipBytes(position2 - parsableByteArray.getPosition());
                }
                i2 = TsExtractor.TS_STREAM_TYPE_AC3;
                parsableByteArray.skipBytes(position2 - parsableByteArray.getPosition());
            }
            parsableByteArray.setPosition(i);
            return new EsInfo(i2, str, list, Arrays.copyOfRange(parsableByteArray.data, position, i));
        }
    }

    public void release() {
    }

    public TsExtractor() {
        this(0);
    }

    public TsExtractor(int i) {
        this(1, i);
    }

    public TsExtractor(int i, int i2) {
        this(i, new TimestampAdjuster(0), new DefaultTsPayloadReaderFactory(i2));
    }

    public TsExtractor(int i, TimestampAdjuster timestampAdjuster, Factory factory) {
        this.payloadReaderFactory = (Factory) Assertions.checkNotNull(factory);
        this.mode = i;
        if (i != 1) {
            if (i != 2) {
                this.timestampAdjusters = new ArrayList();
                this.timestampAdjusters.add(timestampAdjuster);
                this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], null);
                this.trackIds = new SparseBooleanArray();
                this.tsPayloadReaders = new SparseArray();
                this.continuityCounters = new SparseIntArray();
                resetPayloadReaders();
            }
        }
        this.timestampAdjusters = Collections.singletonList(timestampAdjuster);
        this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], null);
        this.trackIds = new SparseBooleanArray();
        this.tsPayloadReaders = new SparseArray();
        this.continuityCounters = new SparseIntArray();
        resetPayloadReaders();
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        byte[] bArr = this.tsPacketBuffer.data;
        extractorInput.peekFully(bArr, 0, 940);
        int i = 0;
        while (i < TS_PACKET_SIZE) {
            int i2 = 0;
            while (i2 != 5) {
                if (bArr[(i2 * TS_PACKET_SIZE) + i] != (byte) 71) {
                    i++;
                } else {
                    i2++;
                }
            }
            extractorInput.skipFully(i);
            return true;
        }
        return false;
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
        extractorOutput.seekMap(new Unseekable(C0542C.TIME_UNSET));
    }

    public void seek(long j, long j2) {
        j = this.timestampAdjusters.size();
        for (j2 = 0; j2 < j; j2++) {
            ((TimestampAdjuster) this.timestampAdjusters.get(j2)).reset();
        }
        this.tsPacketBuffer.reset();
        this.continuityCounters.clear();
        resetPayloadReaders();
        this.bytesSinceLastSync = 0;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        int bytesLeft;
        positionHolder = this.tsPacketBuffer.data;
        if (9400 - this.tsPacketBuffer.getPosition() < TS_PACKET_SIZE) {
            bytesLeft = this.tsPacketBuffer.bytesLeft();
            if (bytesLeft > 0) {
                System.arraycopy(positionHolder, this.tsPacketBuffer.getPosition(), positionHolder, 0, bytesLeft);
            }
            this.tsPacketBuffer.reset(positionHolder, bytesLeft);
        }
        while (this.tsPacketBuffer.bytesLeft() < TS_PACKET_SIZE) {
            bytesLeft = this.tsPacketBuffer.limit();
            int read = extractorInput.read(positionHolder, bytesLeft, 9400 - bytesLeft);
            if (read == -1) {
                return -1;
            }
            this.tsPacketBuffer.setLimit(bytesLeft + read);
        }
        PositionHolder limit = this.tsPacketBuffer.limit();
        bytesLeft = this.tsPacketBuffer.getPosition();
        int i = bytesLeft;
        while (i < limit && positionHolder[i] != (byte) 71) {
            i++;
        }
        this.tsPacketBuffer.setPosition(i);
        positionHolder = i + TS_PACKET_SIZE;
        if (positionHolder > limit) {
            this.bytesSinceLastSync += i - bytesLeft;
            if (this.mode != 2 || this.bytesSinceLastSync <= 376) {
                return 0;
            }
            throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
        }
        this.bytesSinceLastSync = 0;
        bytesLeft = this.tsPacketBuffer.readInt();
        if ((8388608 & bytesLeft) != 0) {
            this.tsPacketBuffer.setPosition(positionHolder);
            return 0;
        }
        boolean z = (4194304 & bytesLeft) != 0;
        int i2 = (2096896 & bytesLeft) >> 8;
        int i3 = (bytesLeft & 32) != 0 ? 1 : 0;
        TsPayloadReader tsPayloadReader = ((bytesLeft & 16) != 0 ? 1 : 0) != 0 ? (TsPayloadReader) this.tsPayloadReaders.get(i2) : null;
        if (tsPayloadReader == null) {
            this.tsPacketBuffer.setPosition(positionHolder);
            return 0;
        }
        if (this.mode != 2) {
            bytesLeft &= 15;
            read = this.continuityCounters.get(i2, bytesLeft - 1);
            this.continuityCounters.put(i2, bytesLeft);
            if (read == bytesLeft) {
                this.tsPacketBuffer.setPosition(positionHolder);
                return 0;
            } else if (bytesLeft != ((read + 1) & 15)) {
                tsPayloadReader.seek();
            }
        }
        if (i3 != 0) {
            this.tsPacketBuffer.skipBytes(this.tsPacketBuffer.readUnsignedByte());
        }
        this.tsPacketBuffer.setLimit(positionHolder);
        tsPayloadReader.consume(this.tsPacketBuffer, z);
        this.tsPacketBuffer.setLimit(limit);
        this.tsPacketBuffer.setPosition(positionHolder);
        return 0;
    }

    private void resetPayloadReaders() {
        this.trackIds.clear();
        this.tsPayloadReaders.clear();
        SparseArray createInitialPayloadReaders = this.payloadReaderFactory.createInitialPayloadReaders();
        int size = createInitialPayloadReaders.size();
        for (int i = 0; i < size; i++) {
            this.tsPayloadReaders.put(createInitialPayloadReaders.keyAt(i), createInitialPayloadReaders.valueAt(i));
        }
        this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
        this.id3Reader = null;
    }
}
