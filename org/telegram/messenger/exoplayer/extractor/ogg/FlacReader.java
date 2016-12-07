package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.FlacSeekTable;
import org.telegram.messenger.exoplayer.util.FlacStreamInfo;
import org.telegram.messenger.exoplayer.util.FlacUtil;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class FlacReader extends StreamReader {
    private static final byte AUDIO_PACKET_TYPE = (byte) -1;
    private static final byte SEEKTABLE_PACKET_TYPE = (byte) 3;
    private boolean firstAudioPacketProcessed;
    private FlacSeekTable seekTable;
    private FlacStreamInfo streamInfo;

    FlacReader() {
    }

    static boolean verifyBitstreamType(ParsableByteArray data) {
        return data.readUnsignedByte() == 127 && data.readUnsignedInt() == NUM;
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        long position = input.getPosition();
        if (!this.oggParser.readPacket(input, this.scratch)) {
            return -1;
        }
        byte[] data = this.scratch.data;
        if (this.streamInfo == null) {
            this.streamInfo = new FlacStreamInfo(data, 17);
            byte[] metadata = Arrays.copyOfRange(data, 9, this.scratch.limit());
            metadata[4] = Byte.MIN_VALUE;
            this.trackOutput.format(MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_FLAC, this.streamInfo.bitRate(), -1, this.streamInfo.durationUs(), this.streamInfo.channels, this.streamInfo.sampleRate, Collections.singletonList(metadata), null));
        } else if (data[0] == AUDIO_PACKET_TYPE) {
            if (!this.firstAudioPacketProcessed) {
                if (this.seekTable != null) {
                    this.extractorOutput.seekMap(this.seekTable.createSeekMap(position, (long) this.streamInfo.sampleRate));
                    this.seekTable = null;
                } else {
                    this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                }
                this.firstAudioPacketProcessed = true;
            }
            this.trackOutput.sampleData(this.scratch, this.scratch.limit());
            this.scratch.setPosition(0);
            this.trackOutput.sampleMetadata(FlacUtil.extractSampleTimestamp(this.streamInfo, this.scratch), 1, this.scratch.limit(), 0, null);
        } else if ((data[0] & 127) == 3 && this.seekTable == null) {
            this.seekTable = FlacSeekTable.parseSeekTable(this.scratch);
        }
        this.scratch.reset();
        return 0;
    }
}
