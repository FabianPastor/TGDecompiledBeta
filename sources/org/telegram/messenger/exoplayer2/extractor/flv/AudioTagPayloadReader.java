package org.telegram.messenger.exoplayer2.extractor.flv;

import android.util.Pair;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class AudioTagPayloadReader extends TagPayloadReader {
    private static final int AAC_PACKET_TYPE_AAC_RAW = 1;
    private static final int AAC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int AUDIO_FORMAT_AAC = 10;
    private static final int AUDIO_FORMAT_ALAW = 7;
    private static final int AUDIO_FORMAT_MP3 = 2;
    private static final int AUDIO_FORMAT_ULAW = 8;
    private static final int[] AUDIO_SAMPLING_RATE_TABLE = new int[]{5512, 11025, 22050, 44100};
    private int audioFormat;
    private boolean hasOutputFormat;
    private boolean hasParsedAudioDataHeader;

    public void seek() {
    }

    public AudioTagPayloadReader(TrackOutput trackOutput) {
        super(trackOutput);
    }

    protected boolean parseHeader(ParsableByteArray parsableByteArray) throws UnsupportedFormatException {
        if (this.hasParsedAudioDataHeader) {
            parsableByteArray.skipBytes(1);
        } else {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            r0.audioFormat = (readUnsignedByte >> 4) & 15;
            if (r0.audioFormat == 2) {
                r0.output.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_MPEG, null, -1, -1, 1, AUDIO_SAMPLING_RATE_TABLE[(readUnsignedByte >> 2) & 3], null, null, 0, null));
                r0.hasOutputFormat = true;
            } else {
                if (r0.audioFormat != 7) {
                    if (r0.audioFormat != 8) {
                        if (r0.audioFormat != 10) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Audio format not supported: ");
                            stringBuilder.append(r0.audioFormat);
                            throw new UnsupportedFormatException(stringBuilder.toString());
                        }
                    }
                }
                r0.output.format(Format.createAudioSampleFormat(null, r0.audioFormat == 7 ? MimeTypes.AUDIO_ALAW : MimeTypes.AUDIO_MLAW, null, -1, -1, 1, 8000, (readUnsignedByte & 1) == 1 ? 2 : 3, null, null, 0, null));
                r0.hasOutputFormat = true;
            }
            r0.hasParsedAudioDataHeader = true;
        }
        return true;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) throws ParserException {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        if (this.audioFormat == 2) {
            int bytesLeft = parsableByteArray.bytesLeft();
            r0.output.sampleData(parsableByteArray2, bytesLeft);
            r0.output.sampleMetadata(j, 1, bytesLeft, 0, null);
            return;
        }
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        if (readUnsignedByte == 0 && !r0.hasOutputFormat) {
            Object obj = new byte[parsableByteArray.bytesLeft()];
            parsableByteArray2.readBytes(obj, 0, obj.length);
            Pair parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(obj);
            r0.output.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_AAC, null, -1, -1, ((Integer) parseAacAudioSpecificConfig.second).intValue(), ((Integer) parseAacAudioSpecificConfig.first).intValue(), Collections.singletonList(obj), null, 0, null));
            r0.hasOutputFormat = true;
        } else if (r0.audioFormat != 10 || readUnsignedByte == 1) {
            int bytesLeft2 = parsableByteArray.bytesLeft();
            r0.output.sampleData(parsableByteArray2, bytesLeft2);
            r0.output.sampleMetadata(j, 1, bytesLeft2, 0, null);
        }
    }
}
