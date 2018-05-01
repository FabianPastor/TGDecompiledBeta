package org.telegram.messenger.exoplayer2.extractor.wav;

import android.util.Log;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class WavHeaderReader {
    private static final String TAG = "WavHeaderReader";
    private static final int TYPE_FLOAT = 3;
    private static final int TYPE_PCM = 1;
    private static final int TYPE_WAVE_FORMAT_EXTENSIBLE = 65534;

    private static final class ChunkHeader {
        public static final int SIZE_IN_BYTES = 8;
        public final int id;
        public final long size;

        private ChunkHeader(int id, long size) {
            this.id = id;
            this.size = size;
        }

        public static ChunkHeader peek(ExtractorInput input, ParsableByteArray scratch) throws IOException, InterruptedException {
            input.peekFully(scratch.data, 0, 8);
            scratch.setPosition(0);
            return new ChunkHeader(scratch.readInt(), scratch.readLittleEndianUnsignedInt());
        }
    }

    WavHeaderReader() {
    }

    public static WavHeader peek(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        ParsableByteArray scratch = new ParsableByteArray(16);
        if (ChunkHeader.peek(input, scratch).id != Util.getIntegerCodeForString("RIFF")) {
            return null;
        }
        input.peekFully(scratch.data, 0, 4);
        scratch.setPosition(0);
        int riffFormat = scratch.readInt();
        if (riffFormat != Util.getIntegerCodeForString("WAVE")) {
            Log.e(TAG, "Unsupported RIFF format: " + riffFormat);
            return null;
        }
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString("fmt ")) {
            input.advancePeekPosition((int) chunkHeader.size);
            chunkHeader = ChunkHeader.peek(input, scratch);
        }
        Assertions.checkState(chunkHeader.size >= 16);
        input.peekFully(scratch.data, 0, 16);
        scratch.setPosition(0);
        int type = scratch.readLittleEndianUnsignedShort();
        int numChannels = scratch.readLittleEndianUnsignedShort();
        int sampleRateHz = scratch.readLittleEndianUnsignedIntToInt();
        int averageBytesPerSecond = scratch.readLittleEndianUnsignedIntToInt();
        int blockAlignment = scratch.readLittleEndianUnsignedShort();
        int bitsPerSample = scratch.readLittleEndianUnsignedShort();
        int expectedBlockAlignment = (numChannels * bitsPerSample) / 8;
        if (blockAlignment != expectedBlockAlignment) {
            throw new ParserException("Expected block alignment: " + expectedBlockAlignment + "; got: " + blockAlignment);
        }
        int encoding;
        switch (type) {
            case 1:
            case TYPE_WAVE_FORMAT_EXTENSIBLE /*65534*/:
                encoding = Util.getPcmEncoding(bitsPerSample);
                break;
            case 3:
                encoding = bitsPerSample == 32 ? 4 : 0;
                break;
            default:
                Log.e(TAG, "Unsupported WAV format type: " + type);
                return null;
        }
        if (encoding == 0) {
            Log.e(TAG, "Unsupported WAV bit depth " + bitsPerSample + " for type " + type);
            return null;
        }
        input.advancePeekPosition(((int) chunkHeader.size) - 16);
        return new WavHeader(numChannels, sampleRateHz, averageBytesPerSecond, blockAlignment, bitsPerSample, encoding);
    }

    public static void skipToData(ExtractorInput input, WavHeader wavHeader) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        Assertions.checkNotNull(wavHeader);
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(8);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString(DataSchemeDataSource.SCHEME_DATA)) {
            Log.w(TAG, "Ignoring unknown WAV chunk: " + chunkHeader.id);
            long bytesToSkip = 8 + chunkHeader.size;
            if (chunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
                bytesToSkip = 12;
            }
            if (bytesToSkip > 2147483647L) {
                throw new ParserException("Chunk is too large (~2GB+) to skip; id: " + chunkHeader.id);
            }
            input.skipFully((int) bytesToSkip);
            chunkHeader = ChunkHeader.peek(input, scratch);
        }
        input.skipFully(8);
        wavHeader.setDataBounds(input.getPosition(), chunkHeader.size);
    }
}
