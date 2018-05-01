package org.telegram.messenger.exoplayer2.extractor.flv;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.video.AvcConfig;

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

    public void seek() {
    }

    public VideoTagPayloadReader(TrackOutput trackOutput) {
        super(trackOutput);
    }

    protected boolean parseHeader(ParsableByteArray parsableByteArray) throws UnsupportedFormatException {
        parsableByteArray = parsableByteArray.readUnsignedByte();
        int i = (parsableByteArray >> 4) & 15;
        parsableByteArray &= 15;
        if (parsableByteArray != 7) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Video format not supported: ");
            stringBuilder.append(parsableByteArray);
            throw new UnsupportedFormatException(stringBuilder.toString());
        }
        this.frameType = i;
        return i != 5 ? true : null;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) throws ParserException {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        long readInt24 = j + (((long) parsableByteArray.readInt24()) * 1000);
        if (readUnsignedByte == 0 && !this.hasOutputFormat) {
            ParsableByteArray parsableByteArray2 = new ParsableByteArray(new byte[parsableByteArray.bytesLeft()]);
            parsableByteArray.readBytes(parsableByteArray2.data, 0, parsableByteArray.bytesLeft());
            parsableByteArray = AvcConfig.parse(parsableByteArray2);
            this.nalUnitLengthFieldLength = parsableByteArray.nalUnitLengthFieldLength;
            this.output.format(Format.createVideoSampleFormat(null, "video/avc", null, -1, -1, parsableByteArray.width, parsableByteArray.height, -1.0f, parsableByteArray.initializationData, -1, parsableByteArray.pixelWidthAspectRatio, null));
            this.hasOutputFormat = true;
        } else if (readUnsignedByte == 1 && this.hasOutputFormat) {
            byte[] bArr = this.nalLength.data;
            bArr[0] = (byte) 0;
            bArr[1] = (byte) 0;
            bArr[2] = (byte) 0;
            readUnsignedByte = 4 - this.nalUnitLengthFieldLength;
            int i = 0;
            while (parsableByteArray.bytesLeft() > 0) {
                parsableByteArray.readBytes(this.nalLength.data, readUnsignedByte, this.nalUnitLengthFieldLength);
                this.nalLength.setPosition(0);
                int readUnsignedIntToInt = this.nalLength.readUnsignedIntToInt();
                this.nalStartCode.setPosition(0);
                this.output.sampleData(this.nalStartCode, 4);
                i += 4;
                this.output.sampleData(parsableByteArray, readUnsignedIntToInt);
                i += readUnsignedIntToInt;
            }
            this.output.sampleMetadata(readInt24, this.frameType == 1 ? 1 : 0, i, 0, null);
        }
    }
}
