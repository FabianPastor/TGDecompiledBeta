package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil.PpsData;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.ParsableNalUnitBitArray;

public final class H264Reader implements ElementaryStreamReader {
    private static final int NAL_UNIT_TYPE_PPS = 8;
    private static final int NAL_UNIT_TYPE_SEI = 6;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private final boolean allowNonIdrKeyframes;
    private final boolean detectAccessUnits;
    private String formatId;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final NalUnitTargetBuffer pps = new NalUnitTargetBuffer(true, 128);
    private final boolean[] prefixFlags = new boolean[3];
    private SampleReader sampleReader;
    private final NalUnitTargetBuffer sei = new NalUnitTargetBuffer(true, 128);
    private final SeiReader seiReader;
    private final ParsableByteArray seiWrapper = new ParsableByteArray();
    private final NalUnitTargetBuffer sps = new NalUnitTargetBuffer(true, 128);
    private long totalBytesWritten;

    private static final class SampleReader {
        private static final int DEFAULT_BUFFER_SIZE = 128;
        private static final int NAL_UNIT_TYPE_AUD = 9;
        private static final int NAL_UNIT_TYPE_IDR = 5;
        private static final int NAL_UNIT_TYPE_NON_IDR = 1;
        private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
        private final boolean allowNonIdrKeyframes;
        private final ParsableNalUnitBitArray bitArray = new ParsableNalUnitBitArray(this.buffer, 0, 0);
        private byte[] buffer = new byte[128];
        private int bufferLength;
        private final boolean detectAccessUnits;
        private boolean isFilling;
        private long nalUnitStartPosition;
        private long nalUnitTimeUs;
        private int nalUnitType;
        private final TrackOutput output;
        private final SparseArray<PpsData> pps = new SparseArray();
        private SliceHeaderData previousSliceHeader = new SliceHeaderData();
        private boolean readingSample;
        private boolean sampleIsKeyframe;
        private long samplePosition;
        private long sampleTimeUs;
        private SliceHeaderData sliceHeader = new SliceHeaderData();
        private final SparseArray<SpsData> sps = new SparseArray();

        private static final class SliceHeaderData {
            private static final int SLICE_TYPE_ALL_I = 7;
            private static final int SLICE_TYPE_I = 2;
            private boolean bottomFieldFlag;
            private boolean bottomFieldFlagPresent;
            private int deltaPicOrderCnt0;
            private int deltaPicOrderCnt1;
            private int deltaPicOrderCntBottom;
            private boolean fieldPicFlag;
            private int frameNum;
            private boolean hasSliceType;
            private boolean idrPicFlag;
            private int idrPicId;
            private boolean isComplete;
            private int nalRefIdc;
            private int picOrderCntLsb;
            private int picParameterSetId;
            private int sliceType;
            private SpsData spsData;

            private SliceHeaderData() {
            }

            public void clear() {
                this.hasSliceType = false;
                this.isComplete = false;
            }

            public void setSliceType(int i) {
                this.sliceType = i;
                this.hasSliceType = true;
            }

            public void setAll(SpsData spsData, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3, boolean z4, int i5, int i6, int i7, int i8, int i9) {
                this.spsData = spsData;
                this.nalRefIdc = i;
                this.sliceType = i2;
                this.frameNum = i3;
                this.picParameterSetId = i4;
                this.fieldPicFlag = z;
                this.bottomFieldFlagPresent = z2;
                this.bottomFieldFlag = z3;
                this.idrPicFlag = z4;
                this.idrPicId = i5;
                this.picOrderCntLsb = i6;
                this.deltaPicOrderCntBottom = i7;
                this.deltaPicOrderCnt0 = i8;
                this.deltaPicOrderCnt1 = i9;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public boolean isISlice() {
                return this.hasSliceType && (this.sliceType == 7 || this.sliceType == 2);
            }

            private boolean isFirstVclNalUnitOfPicture(SliceHeaderData sliceHeaderData) {
                if (this.isComplete) {
                    if (!sliceHeaderData.isComplete || this.frameNum != sliceHeaderData.frameNum || this.picParameterSetId != sliceHeaderData.picParameterSetId || this.fieldPicFlag != sliceHeaderData.fieldPicFlag) {
                        return true;
                    }
                    if (this.bottomFieldFlagPresent && sliceHeaderData.bottomFieldFlagPresent && this.bottomFieldFlag != sliceHeaderData.bottomFieldFlag) {
                        return true;
                    }
                    if (this.nalRefIdc != sliceHeaderData.nalRefIdc && (this.nalRefIdc == 0 || sliceHeaderData.nalRefIdc == 0)) {
                        return true;
                    }
                    if (this.spsData.picOrderCountType == 0 && sliceHeaderData.spsData.picOrderCountType == 0 && (this.picOrderCntLsb != sliceHeaderData.picOrderCntLsb || this.deltaPicOrderCntBottom != sliceHeaderData.deltaPicOrderCntBottom)) {
                        return true;
                    }
                    if ((this.spsData.picOrderCountType == 1 && sliceHeaderData.spsData.picOrderCountType == 1 && (this.deltaPicOrderCnt0 != sliceHeaderData.deltaPicOrderCnt0 || this.deltaPicOrderCnt1 != sliceHeaderData.deltaPicOrderCnt1)) || this.idrPicFlag != sliceHeaderData.idrPicFlag) {
                        return true;
                    }
                    if (this.idrPicFlag && sliceHeaderData.idrPicFlag && this.idrPicId != sliceHeaderData.idrPicId) {
                        return true;
                    }
                }
                return false;
            }
        }

        public SampleReader(TrackOutput trackOutput, boolean z, boolean z2) {
            this.output = trackOutput;
            this.allowNonIdrKeyframes = z;
            this.detectAccessUnits = z2;
            reset();
        }

        public boolean needsSpsPps() {
            return this.detectAccessUnits;
        }

        public void putSps(SpsData spsData) {
            this.sps.append(spsData.seqParameterSetId, spsData);
        }

        public void putPps(PpsData ppsData) {
            this.pps.append(ppsData.picParameterSetId, ppsData);
        }

        public void reset() {
            this.isFilling = false;
            this.readingSample = false;
            this.sliceHeader.clear();
        }

        public void startNalUnit(long j, int i, long j2) {
            this.nalUnitType = i;
            this.nalUnitTimeUs = j2;
            this.nalUnitStartPosition = j;
            if (this.allowNonIdrKeyframes == null || this.nalUnitType != 1) {
                if (this.detectAccessUnits == null) {
                    return;
                }
                if (!(this.nalUnitType == 5 || this.nalUnitType == 1 || this.nalUnitType == 2)) {
                    return;
                }
            }
            j = this.previousSliceHeader;
            this.previousSliceHeader = this.sliceHeader;
            this.sliceHeader = j;
            this.sliceHeader.clear();
            this.bufferLength = 0;
            this.isFilling = true;
        }

        public void appendToNalUnit(byte[] bArr, int i, int i2) {
            int i3 = i;
            if (this.isFilling) {
                int i4 = i2 - i3;
                if (r0.buffer.length < r0.bufferLength + i4) {
                    r0.buffer = Arrays.copyOf(r0.buffer, (r0.bufferLength + i4) * 2);
                }
                System.arraycopy(bArr, i3, r0.buffer, r0.bufferLength, i4);
                r0.bufferLength += i4;
                r0.bitArray.reset(r0.buffer, 0, r0.bufferLength);
                if (r0.bitArray.canReadBits(8)) {
                    r0.bitArray.skipBit();
                    int readBits = r0.bitArray.readBits(2);
                    r0.bitArray.skipBits(5);
                    if (r0.bitArray.canReadExpGolombCodedNum()) {
                        r0.bitArray.readUnsignedExpGolombCodedInt();
                        if (r0.bitArray.canReadExpGolombCodedNum()) {
                            int readUnsignedExpGolombCodedInt = r0.bitArray.readUnsignedExpGolombCodedInt();
                            if (!r0.detectAccessUnits) {
                                r0.isFilling = false;
                                r0.sliceHeader.setSliceType(readUnsignedExpGolombCodedInt);
                            } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                int readUnsignedExpGolombCodedInt2 = r0.bitArray.readUnsignedExpGolombCodedInt();
                                if (r0.pps.indexOfKey(readUnsignedExpGolombCodedInt2) < 0) {
                                    r0.isFilling = false;
                                    return;
                                }
                                PpsData ppsData = (PpsData) r0.pps.get(readUnsignedExpGolombCodedInt2);
                                SpsData spsData = (SpsData) r0.sps.get(ppsData.seqParameterSetId);
                                if (spsData.separateColorPlaneFlag) {
                                    if (r0.bitArray.canReadBits(2)) {
                                        r0.bitArray.skipBits(2);
                                    } else {
                                        return;
                                    }
                                }
                                if (r0.bitArray.canReadBits(spsData.frameNumLength)) {
                                    boolean z;
                                    boolean z2;
                                    boolean readBit;
                                    boolean z3;
                                    int i5;
                                    int i6;
                                    int i7;
                                    int i8;
                                    int i9;
                                    int readBits2 = r0.bitArray.readBits(spsData.frameNumLength);
                                    if (spsData.frameMbsOnlyFlag) {
                                        z = false;
                                        z2 = z;
                                    } else if (r0.bitArray.canReadBits(1)) {
                                        boolean readBit2 = r0.bitArray.readBit();
                                        if (!readBit2) {
                                            z = readBit2;
                                            z2 = false;
                                        } else if (r0.bitArray.canReadBits(1)) {
                                            z = readBit2;
                                            z2 = true;
                                            readBit = r0.bitArray.readBit();
                                            z3 = r0.nalUnitType != 5;
                                            if (z3) {
                                                i5 = 0;
                                            } else if (!r0.bitArray.canReadExpGolombCodedNum()) {
                                                i5 = r0.bitArray.readUnsignedExpGolombCodedInt();
                                            } else {
                                                return;
                                            }
                                            if (spsData.picOrderCountType != 0) {
                                                if (!r0.bitArray.canReadBits(spsData.picOrderCntLsbLength)) {
                                                    i4 = r0.bitArray.readBits(spsData.picOrderCntLsbLength);
                                                    if (ppsData.bottomFieldPicOrderInFramePresentFlag || z) {
                                                        i6 = i4;
                                                        i7 = false;
                                                    } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                                        i7 = r0.bitArray.readSignedExpGolombCodedInt();
                                                        i6 = i4;
                                                        i8 = 0;
                                                        i9 = i8;
                                                        r0.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, readBit, z3, i5, i6, i7, i8, i9);
                                                        r0.isFilling = false;
                                                    } else {
                                                        return;
                                                    }
                                                }
                                                return;
                                            } else if (spsData.picOrderCountType == 1 || spsData.deltaPicOrderAlwaysZeroFlag) {
                                                i6 = false;
                                                i7 = i6;
                                            } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                                i4 = r0.bitArray.readSignedExpGolombCodedInt();
                                                if (!ppsData.bottomFieldPicOrderInFramePresentFlag || z) {
                                                    i8 = i4;
                                                    i6 = 0;
                                                    i7 = i6;
                                                    i9 = i7;
                                                    r0.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, readBit, z3, i5, i6, i7, i8, i9);
                                                    r0.isFilling = false;
                                                } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                                    i9 = r0.bitArray.readSignedExpGolombCodedInt();
                                                    i8 = i4;
                                                    i6 = 0;
                                                    i7 = i6;
                                                    r0.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, readBit, z3, i5, i6, i7, i8, i9);
                                                    r0.isFilling = false;
                                                } else {
                                                    return;
                                                }
                                            } else {
                                                return;
                                            }
                                            i8 = i7;
                                            i9 = i8;
                                            r0.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, readBit, z3, i5, i6, i7, i8, i9);
                                            r0.isFilling = false;
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                    readBit = z2;
                                    if (r0.nalUnitType != 5) {
                                    }
                                    if (z3) {
                                        i5 = 0;
                                    } else if (!r0.bitArray.canReadExpGolombCodedNum()) {
                                        i5 = r0.bitArray.readUnsignedExpGolombCodedInt();
                                    } else {
                                        return;
                                    }
                                    if (spsData.picOrderCountType != 0) {
                                        if (spsData.picOrderCountType == 1) {
                                        }
                                        i6 = false;
                                        i7 = i6;
                                    } else if (!r0.bitArray.canReadBits(spsData.picOrderCntLsbLength)) {
                                        i4 = r0.bitArray.readBits(spsData.picOrderCntLsbLength);
                                        if (ppsData.bottomFieldPicOrderInFramePresentFlag) {
                                        }
                                        i6 = i4;
                                        i7 = false;
                                    } else {
                                        return;
                                    }
                                    i8 = i7;
                                    i9 = i8;
                                    r0.sliceHeader.setAll(spsData, readBits, readUnsignedExpGolombCodedInt, readBits2, readUnsignedExpGolombCodedInt2, z, z2, readBit, z3, i5, i6, i7, i8, i9);
                                    r0.isFilling = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public void endNalUnit(long j, int i) {
            int i2 = 0;
            if (this.nalUnitType == 9 || (this.detectAccessUnits && this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))) {
                if (this.readingSample) {
                    outputSample(i + ((int) (j - this.nalUnitStartPosition)));
                }
                this.samplePosition = this.nalUnitStartPosition;
                this.sampleTimeUs = this.nalUnitTimeUs;
                this.sampleIsKeyframe = false;
                this.readingSample = true;
            }
            j = this.sampleIsKeyframe;
            if (this.nalUnitType == 5 || (this.allowNonIdrKeyframes && this.nalUnitType == 1 && this.sliceHeader.isISlice())) {
                i2 = 1;
            }
            this.sampleIsKeyframe = j | i2;
        }

        private void outputSample(int i) {
            this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe, (int) (this.nalUnitStartPosition - this.samplePosition), i, null);
        }
    }

    public void packetFinished() {
    }

    public H264Reader(SeiReader seiReader, boolean z, boolean z2) {
        this.seiReader = seiReader;
        this.allowNonIdrKeyframes = z;
        this.detectAccessUnits = z2;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 2);
        this.sampleReader = new SampleReader(this.output, this.allowNonIdrKeyframes, this.detectAccessUnits);
        this.seiReader.createTracks(extractorOutput, trackIdGenerator);
    }

    public void packetStarted(long j, boolean z) {
        this.pesTimeUs = j;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        byte[] bArr = parsableByteArray.data;
        this.totalBytesWritten += (long) parsableByteArray.bytesLeft();
        this.output.sampleData(parsableByteArray, parsableByteArray.bytesLeft());
        while (true) {
            int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, this.prefixFlags);
            if (findNalUnit == limit) {
                nalUnitData(bArr, position, limit);
                return;
            }
            int nalUnitType = NalUnitUtil.getNalUnitType(bArr, findNalUnit);
            int i = findNalUnit - position;
            if (i > 0) {
                nalUnitData(bArr, position, findNalUnit);
            }
            int i2 = limit - findNalUnit;
            long j = this.totalBytesWritten - ((long) i2);
            endNalUnit(j, i2, i < 0 ? -i : 0, this.pesTimeUs);
            startNalUnit(j, nalUnitType, this.pesTimeUs);
            position = findNalUnit + 3;
        }
    }

    private void startNalUnit(long j, int i, long j2) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.startNalUnit(i);
            this.pps.startNalUnit(i);
        }
        this.sei.startNalUnit(i);
        this.sampleReader.startNalUnit(j, i, j2);
    }

    private void nalUnitData(byte[] bArr, int i, int i2) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.appendToNalUnit(bArr, i, i2);
            this.pps.appendToNalUnit(bArr, i, i2);
        }
        this.sei.appendToNalUnit(bArr, i, i2);
        this.sampleReader.appendToNalUnit(bArr, i, i2);
    }

    private void endNalUnit(long j, int i, int i2, long j2) {
        int i3 = i2;
        if (!this.hasOutputFormat || r0.sampleReader.needsSpsPps()) {
            r0.sps.endNalUnit(i3);
            r0.pps.endNalUnit(i3);
            if (r0.hasOutputFormat) {
                if (r0.sps.isCompleted()) {
                    r0.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength));
                    r0.sps.reset();
                } else if (r0.pps.isCompleted()) {
                    r0.sampleReader.putPps(NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength));
                    r0.pps.reset();
                }
            } else if (r0.sps.isCompleted() && r0.pps.isCompleted()) {
                List arrayList = new ArrayList();
                arrayList.add(Arrays.copyOf(r0.sps.nalData, r0.sps.nalLength));
                arrayList.add(Arrays.copyOf(r0.pps.nalData, r0.pps.nalLength));
                SpsData parseSpsNalUnit = NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength);
                PpsData parsePpsNalUnit = NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength);
                r0.output.format(Format.createVideoSampleFormat(r0.formatId, "video/avc", null, -1, -1, parseSpsNalUnit.width, parseSpsNalUnit.height, -1.0f, arrayList, -1, parseSpsNalUnit.pixelWidthAspectRatio, null));
                r0.hasOutputFormat = true;
                r0.sampleReader.putSps(parseSpsNalUnit);
                r0.sampleReader.putPps(parsePpsNalUnit);
                r0.sps.reset();
                r0.pps.reset();
            }
        }
        if (r0.sei.endNalUnit(i2)) {
            r0.seiWrapper.reset(r0.sei.nalData, NalUnitUtil.unescapeStream(r0.sei.nalData, r0.sei.nalLength));
            r0.seiWrapper.setPosition(4);
            r0.seiReader.consume(j2, r0.seiWrapper);
        }
        r0.sampleReader.endNalUnit(j, i);
    }
}
