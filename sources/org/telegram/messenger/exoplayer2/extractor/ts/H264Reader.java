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
    private final NalUnitTargetBuffer pps = new NalUnitTargetBuffer(8, 128);
    private final boolean[] prefixFlags = new boolean[3];
    private SampleReader sampleReader;
    private final NalUnitTargetBuffer sei = new NalUnitTargetBuffer(6, 128);
    private final SeiReader seiReader;
    private final ParsableByteArray seiWrapper = new ParsableByteArray();
    private final NalUnitTargetBuffer sps = new NalUnitTargetBuffer(7, 128);
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

            public void setSliceType(int sliceType) {
                this.sliceType = sliceType;
                this.hasSliceType = true;
            }

            public void setAll(SpsData spsData, int nalRefIdc, int sliceType, int frameNum, int picParameterSetId, boolean fieldPicFlag, boolean bottomFieldFlagPresent, boolean bottomFieldFlag, boolean idrPicFlag, int idrPicId, int picOrderCntLsb, int deltaPicOrderCntBottom, int deltaPicOrderCnt0, int deltaPicOrderCnt1) {
                this.spsData = spsData;
                this.nalRefIdc = nalRefIdc;
                this.sliceType = sliceType;
                this.frameNum = frameNum;
                this.picParameterSetId = picParameterSetId;
                this.fieldPicFlag = fieldPicFlag;
                this.bottomFieldFlagPresent = bottomFieldFlagPresent;
                this.bottomFieldFlag = bottomFieldFlag;
                this.idrPicFlag = idrPicFlag;
                this.idrPicId = idrPicId;
                this.picOrderCntLsb = picOrderCntLsb;
                this.deltaPicOrderCntBottom = deltaPicOrderCntBottom;
                this.deltaPicOrderCnt0 = deltaPicOrderCnt0;
                this.deltaPicOrderCnt1 = deltaPicOrderCnt1;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public boolean isISlice() {
                return this.hasSliceType && (this.sliceType == 7 || this.sliceType == 2);
            }

            private boolean isFirstVclNalUnitOfPicture(SliceHeaderData other) {
                return this.isComplete && !(other.isComplete && this.frameNum == other.frameNum && this.picParameterSetId == other.picParameterSetId && this.fieldPicFlag == other.fieldPicFlag && ((!this.bottomFieldFlagPresent || !other.bottomFieldFlagPresent || this.bottomFieldFlag == other.bottomFieldFlag) && ((this.nalRefIdc == other.nalRefIdc || (this.nalRefIdc != 0 && other.nalRefIdc != 0)) && ((this.spsData.picOrderCountType != 0 || other.spsData.picOrderCountType != 0 || (this.picOrderCntLsb == other.picOrderCntLsb && this.deltaPicOrderCntBottom == other.deltaPicOrderCntBottom)) && ((this.spsData.picOrderCountType != 1 || other.spsData.picOrderCountType != 1 || (this.deltaPicOrderCnt0 == other.deltaPicOrderCnt0 && this.deltaPicOrderCnt1 == other.deltaPicOrderCnt1)) && this.idrPicFlag == other.idrPicFlag && (!this.idrPicFlag || !other.idrPicFlag || this.idrPicId == other.idrPicId))))));
            }
        }

        public SampleReader(TrackOutput output, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
            this.output = output;
            this.allowNonIdrKeyframes = allowNonIdrKeyframes;
            this.detectAccessUnits = detectAccessUnits;
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

        public void startNalUnit(long position, int type, long pesTimeUs) {
            this.nalUnitType = type;
            this.nalUnitTimeUs = pesTimeUs;
            this.nalUnitStartPosition = position;
            if (!(this.allowNonIdrKeyframes && this.nalUnitType == 1)) {
                if (!this.detectAccessUnits) {
                    return;
                }
                if (!(this.nalUnitType == 5 || this.nalUnitType == 1 || this.nalUnitType == 2)) {
                    return;
                }
            }
            SliceHeaderData newSliceHeader = this.previousSliceHeader;
            this.previousSliceHeader = this.sliceHeader;
            this.sliceHeader = newSliceHeader;
            this.sliceHeader.clear();
            this.bufferLength = 0;
            this.isFilling = true;
        }

        public void appendToNalUnit(byte[] data, int offset, int limit) {
            int i = offset;
            if (this.isFilling) {
                int readLength = limit - i;
                if (r0.buffer.length < r0.bufferLength + readLength) {
                    r0.buffer = Arrays.copyOf(r0.buffer, (r0.bufferLength + readLength) * 2);
                }
                System.arraycopy(data, i, r0.buffer, r0.bufferLength, readLength);
                r0.bufferLength += readLength;
                r0.bitArray.reset(r0.buffer, 0, r0.bufferLength);
                if (r0.bitArray.canReadBits(8)) {
                    r0.bitArray.skipBit();
                    int nalRefIdc = r0.bitArray.readBits(2);
                    r0.bitArray.skipBits(5);
                    if (r0.bitArray.canReadExpGolombCodedNum()) {
                        r0.bitArray.readUnsignedExpGolombCodedInt();
                        if (r0.bitArray.canReadExpGolombCodedNum()) {
                            int sliceType = r0.bitArray.readUnsignedExpGolombCodedInt();
                            if (!r0.detectAccessUnits) {
                                r0.isFilling = false;
                                r0.sliceHeader.setSliceType(sliceType);
                            } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                int readUnsignedExpGolombCodedInt = r0.bitArray.readUnsignedExpGolombCodedInt();
                                if (r0.pps.indexOfKey(readUnsignedExpGolombCodedInt) < 0) {
                                    r0.isFilling = false;
                                    return;
                                }
                                PpsData ppsData = (PpsData) r0.pps.get(readUnsignedExpGolombCodedInt);
                                SpsData spsData = (SpsData) r0.sps.get(ppsData.seqParameterSetId);
                                if (spsData.separateColorPlaneFlag) {
                                    if (r0.bitArray.canReadBits(2)) {
                                        r0.bitArray.skipBits(2);
                                    } else {
                                        return;
                                    }
                                }
                                if (r0.bitArray.canReadBits(spsData.frameNumLength)) {
                                    int deltaPicOrderCntBottom;
                                    int deltaPicOrderCnt0;
                                    int deltaPicOrderCnt1;
                                    int picParameterSetId;
                                    boolean fieldPicFlag = false;
                                    boolean bottomFieldFlagPresent = false;
                                    boolean bottomFieldFlag = false;
                                    int frameNum = r0.bitArray.readBits(spsData.frameNumLength);
                                    if (!spsData.frameMbsOnlyFlag) {
                                        if (r0.bitArray.canReadBits(1)) {
                                            fieldPicFlag = r0.bitArray.readBit();
                                            if (fieldPicFlag) {
                                                if (r0.bitArray.canReadBits(1)) {
                                                    bottomFieldFlag = r0.bitArray.readBit();
                                                    bottomFieldFlagPresent = true;
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    boolean bottomFieldFlagPresent2 = bottomFieldFlagPresent;
                                    boolean bottomFieldFlag2 = bottomFieldFlag;
                                    boolean idrPicFlag = r0.nalUnitType == 5;
                                    int idrPicId = 0;
                                    if (idrPicFlag) {
                                        if (r0.bitArray.canReadExpGolombCodedNum()) {
                                            idrPicId = r0.bitArray.readUnsignedExpGolombCodedInt();
                                        } else {
                                            return;
                                        }
                                    }
                                    int idrPicId2 = idrPicId;
                                    if (spsData.picOrderCountType == 0) {
                                        if (r0.bitArray.canReadBits(spsData.picOrderCntLsbLength)) {
                                            i = r0.bitArray.readBits(spsData.picOrderCntLsbLength);
                                            if (ppsData.bottomFieldPicOrderInFramePresentFlag && !fieldPicFlag) {
                                                if (r0.bitArray.canReadExpGolombCodedNum()) {
                                                    deltaPicOrderCntBottom = r0.bitArray.readSignedExpGolombCodedInt();
                                                    deltaPicOrderCnt0 = 0;
                                                    deltaPicOrderCnt1 = 0;
                                                    picParameterSetId = readUnsignedExpGolombCodedInt;
                                                    r0.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, readUnsignedExpGolombCodedInt, fieldPicFlag, bottomFieldFlagPresent2, bottomFieldFlag2, idrPicFlag, idrPicId2, i, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                                    r0.isFilling = false;
                                                }
                                                return;
                                            }
                                        }
                                        return;
                                    } else if (spsData.picOrderCountType != 1 || spsData.deltaPicOrderAlwaysZeroFlag) {
                                        i = 0;
                                    } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                        i = r0.bitArray.readSignedExpGolombCodedInt();
                                        if (!ppsData.bottomFieldPicOrderInFramePresentFlag || fieldPicFlag) {
                                            deltaPicOrderCnt0 = i;
                                            i = 0;
                                            deltaPicOrderCntBottom = 0;
                                            deltaPicOrderCnt1 = 0;
                                            picParameterSetId = readUnsignedExpGolombCodedInt;
                                            r0.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, readUnsignedExpGolombCodedInt, fieldPicFlag, bottomFieldFlagPresent2, bottomFieldFlag2, idrPicFlag, idrPicId2, i, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                            r0.isFilling = false;
                                        } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                            deltaPicOrderCnt0 = i;
                                            deltaPicOrderCnt1 = r0.bitArray.readSignedExpGolombCodedInt();
                                            i = 0;
                                            deltaPicOrderCntBottom = 0;
                                            picParameterSetId = readUnsignedExpGolombCodedInt;
                                            r0.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, readUnsignedExpGolombCodedInt, fieldPicFlag, bottomFieldFlagPresent2, bottomFieldFlag2, idrPicFlag, idrPicId2, i, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                            r0.isFilling = false;
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                    deltaPicOrderCntBottom = 0;
                                    deltaPicOrderCnt0 = 0;
                                    deltaPicOrderCnt1 = 0;
                                    picParameterSetId = readUnsignedExpGolombCodedInt;
                                    r0.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, readUnsignedExpGolombCodedInt, fieldPicFlag, bottomFieldFlagPresent2, bottomFieldFlag2, idrPicFlag, idrPicId2, i, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                    r0.isFilling = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public void endNalUnit(long position, int offset) {
            int i = 0;
            if (this.nalUnitType == 9 || (this.detectAccessUnits && this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))) {
                if (this.readingSample) {
                    outputSample(offset + ((int) (position - this.nalUnitStartPosition)));
                }
                this.samplePosition = this.nalUnitStartPosition;
                this.sampleTimeUs = this.nalUnitTimeUs;
                this.sampleIsKeyframe = false;
                this.readingSample = true;
            }
            boolean z = this.sampleIsKeyframe;
            if (this.nalUnitType != 5) {
                if (!this.allowNonIdrKeyframes || this.nalUnitType != 1 || !this.sliceHeader.isISlice()) {
                    this.sampleIsKeyframe = z | i;
                }
            }
            i = 1;
            this.sampleIsKeyframe = z | i;
        }

        private void outputSample(int offset) {
            this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe, (int) (this.nalUnitStartPosition - this.samplePosition), offset, null);
        }
    }

    public H264Reader(SeiReader seiReader, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
        this.seiReader = seiReader;
        this.allowNonIdrKeyframes = allowNonIdrKeyframes;
        this.detectAccessUnits = detectAccessUnits;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
        this.sampleReader = new SampleReader(this.output, this.allowNonIdrKeyframes, this.detectAccessUnits);
        this.seiReader.createTracks(extractorOutput, idGenerator);
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.pesTimeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        ParsableByteArray parsableByteArray = data;
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = parsableByteArray.data;
        this.totalBytesWritten += (long) data.bytesLeft();
        this.output.sampleData(parsableByteArray, data.bytesLeft());
        int offset2 = offset;
        while (true) {
            int nalUnitOffset = NalUnitUtil.findNalUnit(dataArray, offset2, limit, r7.prefixFlags);
            if (nalUnitOffset == limit) {
                nalUnitData(dataArray, offset2, limit);
                return;
            }
            int nalUnitType = NalUnitUtil.getNalUnitType(dataArray, nalUnitOffset);
            int lengthToNalUnit = nalUnitOffset - offset2;
            if (lengthToNalUnit > 0) {
                nalUnitData(dataArray, offset2, nalUnitOffset);
            }
            int bytesWrittenPastPosition = limit - nalUnitOffset;
            long j = r7.totalBytesWritten - ((long) bytesWrittenPastPosition);
            endNalUnit(j, bytesWrittenPastPosition, lengthToNalUnit < 0 ? -lengthToNalUnit : 0, r7.pesTimeUs);
            startNalUnit(j, nalUnitType, r7.pesTimeUs);
            offset2 = nalUnitOffset + 3;
        }
    }

    public void packetFinished() {
    }

    private void startNalUnit(long position, int nalUnitType, long pesTimeUs) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.startNalUnit(nalUnitType);
            this.pps.startNalUnit(nalUnitType);
        }
        this.sei.startNalUnit(nalUnitType);
        this.sampleReader.startNalUnit(position, nalUnitType, pesTimeUs);
    }

    private void nalUnitData(byte[] dataArray, int offset, int limit) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.appendToNalUnit(dataArray, offset, limit);
            this.pps.appendToNalUnit(dataArray, offset, limit);
        }
        this.sei.appendToNalUnit(dataArray, offset, limit);
        this.sampleReader.appendToNalUnit(dataArray, offset, limit);
    }

    private void endNalUnit(long position, int offset, int discardPadding, long pesTimeUs) {
        int i = discardPadding;
        if (!this.hasOutputFormat || r0.sampleReader.needsSpsPps()) {
            r0.sps.endNalUnit(i);
            r0.pps.endNalUnit(i);
            if (r0.hasOutputFormat) {
                if (r0.sps.isCompleted()) {
                    r0.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength));
                    r0.sps.reset();
                } else if (r0.pps.isCompleted()) {
                    r0.sampleReader.putPps(NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength));
                    r0.pps.reset();
                }
            } else if (r0.sps.isCompleted() && r0.pps.isCompleted()) {
                List<byte[]> initializationData = new ArrayList();
                initializationData.add(Arrays.copyOf(r0.sps.nalData, r0.sps.nalLength));
                initializationData.add(Arrays.copyOf(r0.pps.nalData, r0.pps.nalLength));
                SpsData spsData = NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength);
                PpsData ppsData = NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength);
                TrackOutput trackOutput = r0.output;
                SpsData spsData2 = spsData;
                trackOutput.format(Format.createVideoSampleFormat(r0.formatId, "video/avc", null, -1, -1, spsData.width, spsData.height, -1.0f, initializationData, -1, spsData.pixelWidthAspectRatio, null));
                r0.hasOutputFormat = true;
                r0.sampleReader.putSps(spsData2);
                r0.sampleReader.putPps(ppsData);
                r0.sps.reset();
                r0.pps.reset();
            }
        }
        if (r0.sei.endNalUnit(i)) {
            r0.seiWrapper.reset(r0.sei.nalData, NalUnitUtil.unescapeStream(r0.sei.nalData, r0.sei.nalLength));
            r0.seiWrapper.setPosition(4);
            r0.seiReader.consume(pesTimeUs, r0.seiWrapper);
        } else {
            long j = pesTimeUs;
        }
        r0.sampleReader.endNalUnit(position, offset);
    }
}
