package org.telegram.messenger.exoplayer.extractor.flv;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.volley.DefaultRetryPolicy;

final class VideoTagPayloadReader extends TagPayloadReader {
    private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
    private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int VIDEO_CODEC_AVC = 7;
    private static final int VIDEO_FRAME_KEYFRAME = 1;
    private static final int VIDEO_FRAME_VIDEO_INFO = 5;
    private int frameType;
    private boolean hasOutputFormat;
    private final ParsableByteArray nalLength = new ParsableByteArray(4);
    private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    private int nalUnitLengthFieldLength;

    private static final class AvcSequenceHeaderData {
        public final int height;
        public final List<byte[]> initializationData;
        public final int nalUnitLengthFieldLength;
        public final float pixelWidthAspectRatio;
        public final int width;

        public AvcSequenceHeaderData(List<byte[]> initializationData, int nalUnitLengthFieldLength, int width, int height, float pixelWidthAspectRatio) {
            this.initializationData = initializationData;
            this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
            this.width = width;
            this.height = height;
        }
    }

    public VideoTagPayloadReader(TrackOutput output) {
        super(output);
    }

    public void seek() {
    }

    protected boolean parseHeader(ParsableByteArray data) throws UnsupportedFormatException {
        int header = data.readUnsignedByte();
        int frameType = (header >> 4) & 15;
        int videoCodec = header & 15;
        if (videoCodec != 7) {
            throw new UnsupportedFormatException("Video format not supported: " + videoCodec);
        }
        this.frameType = frameType;
        return frameType != 5;
    }

    protected void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        int packetType = data.readUnsignedByte();
        timeUs += ((long) data.readUnsignedInt24()) * 1000;
        if (packetType == 0 && !this.hasOutputFormat) {
            ParsableByteArray parsableByteArray = new ParsableByteArray(new byte[data.bytesLeft()]);
            data.readBytes(parsableByteArray.data, 0, data.bytesLeft());
            AvcSequenceHeaderData avcData = parseAvcCodecPrivate(parsableByteArray);
            this.nalUnitLengthFieldLength = avcData.nalUnitLengthFieldLength;
            this.output.format(MediaFormat.createVideoFormat(null, "video/avc", -1, -1, getDurationUs(), avcData.width, avcData.height, avcData.initializationData, -1, avcData.pixelWidthAspectRatio));
            this.hasOutputFormat = true;
        } else if (packetType == 1) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            int nalUnitLengthFieldLengthDiff = 4 - this.nalUnitLengthFieldLength;
            int bytesWritten = 0;
            while (data.bytesLeft() > 0) {
                data.readBytes(this.nalLength.data, nalUnitLengthFieldLengthDiff, this.nalUnitLengthFieldLength);
                this.nalLength.setPosition(0);
                int bytesToWrite = this.nalLength.readUnsignedIntToInt();
                this.nalStartCode.setPosition(0);
                this.output.sampleData(this.nalStartCode, 4);
                bytesWritten += 4;
                this.output.sampleData(data, bytesToWrite);
                bytesWritten += bytesToWrite;
            }
            this.output.sampleMetadata(timeUs, this.frameType == 1 ? 1 : 0, bytesWritten, 0, null);
        }
    }

    private AvcSequenceHeaderData parseAvcCodecPrivate(ParsableByteArray buffer) throws ParserException {
        boolean z;
        buffer.setPosition(4);
        int nalUnitLengthFieldLength = (buffer.readUnsignedByte() & 3) + 1;
        if (nalUnitLengthFieldLength != 3) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        List<byte[]> initializationData = new ArrayList();
        int numSequenceParameterSets = buffer.readUnsignedByte() & 31;
        for (int i = 0; i < numSequenceParameterSets; i++) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        int numPictureParameterSets = buffer.readUnsignedByte();
        for (int j = 0; j < numPictureParameterSets; j++) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        float pixelWidthAspectRatio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        int width = -1;
        int height = -1;
        if (numSequenceParameterSets > 0) {
            ParsableBitArray spsDataBitArray = new ParsableBitArray((byte[]) initializationData.get(0));
            spsDataBitArray.setPosition((nalUnitLengthFieldLength + 1) * 8);
            SpsData sps = NalUnitUtil.parseSpsNalUnit(spsDataBitArray);
            width = sps.width;
            height = sps.height;
            pixelWidthAspectRatio = sps.pixelWidthAspectRatio;
        }
        return new AvcSequenceHeaderData(initializationData, nalUnitLengthFieldLength, width, height, pixelWidthAspectRatio);
    }
}
