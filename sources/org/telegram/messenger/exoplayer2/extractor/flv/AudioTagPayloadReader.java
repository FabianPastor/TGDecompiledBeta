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

    public AudioTagPayloadReader(TrackOutput output) {
        super(output);
    }

    public void seek() {
    }

    protected boolean parseHeader(ParsableByteArray data) throws UnsupportedFormatException {
        if (this.hasParsedAudioDataHeader) {
            data.skipBytes(1);
        } else {
            int header = data.readUnsignedByte();
            r0.audioFormat = (header >> 4) & 15;
            if (r0.audioFormat == 2) {
                r0.output.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_MPEG, null, -1, -1, 1, AUDIO_SAMPLING_RATE_TABLE[(header >> 2) & 3], null, null, 0, null));
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
                r0.output.format(Format.createAudioSampleFormat(null, r0.audioFormat == 7 ? MimeTypes.AUDIO_ALAW : MimeTypes.AUDIO_MLAW, null, -1, -1, 1, 8000, (header & 1) == 1 ? 2 : 3, null, null, 0, null));
                r0.hasOutputFormat = true;
            }
            r0.hasParsedAudioDataHeader = true;
            ParsableByteArray parsableByteArray = data;
        }
        return true;
    }

    protected void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        ParsableByteArray parsableByteArray = data;
        if (this.audioFormat == 2) {
            int sampleSize = data.bytesLeft();
            r0.output.sampleData(parsableByteArray, sampleSize);
            r0.output.sampleMetadata(timeUs, 1, sampleSize, 0, null);
            return;
        }
        sampleSize = data.readUnsignedByte();
        if (sampleSize == 0 && !r0.hasOutputFormat) {
            byte[] audioSpecificConfig = new byte[data.bytesLeft()];
            parsableByteArray.readBytes(audioSpecificConfig, 0, audioSpecificConfig.length);
            Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(audioSpecificConfig);
            r0.output.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_AAC, null, -1, -1, ((Integer) audioParams.second).intValue(), ((Integer) audioParams.first).intValue(), Collections.singletonList(audioSpecificConfig), null, 0, null));
            r0.hasOutputFormat = true;
        } else if (r0.audioFormat != 10 || sampleSize == 1) {
            int sampleSize2 = data.bytesLeft();
            r0.output.sampleData(parsableByteArray, sampleSize2);
            r0.output.sampleMetadata(timeUs, 1, sampleSize2, 0, null);
        }
    }
}
