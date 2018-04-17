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
        ExtractorInput extractorInput = input;
        Assertions.checkNotNull(input);
        ParsableByteArray scratch = new ParsableByteArray(16);
        if (ChunkHeader.peek(extractorInput, scratch).id != Util.getIntegerCodeForString("RIFF")) {
            return null;
        }
        int i = 4;
        extractorInput.peekFully(scratch.data, 0, 4);
        scratch.setPosition(0);
        int riffFormat = scratch.readInt();
        if (riffFormat != Util.getIntegerCodeForString("WAVE")) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported RIFF format: ");
            stringBuilder.append(riffFormat);
            Log.e(str, stringBuilder.toString());
            return null;
        }
        ChunkHeader chunkHeader = ChunkHeader.peek(extractorInput, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString("fmt ")) {
            extractorInput.advancePeekPosition((int) chunkHeader.size);
            chunkHeader = ChunkHeader.peek(extractorInput, scratch);
        }
        Assertions.checkState(chunkHeader.size >= 16);
        extractorInput.peekFully(scratch.data, 0, 16);
        scratch.setPosition(0);
        int type = scratch.readLittleEndianUnsignedShort();
        int numChannels = scratch.readLittleEndianUnsignedShort();
        int sampleRateHz = scratch.readLittleEndianUnsignedIntToInt();
        int averageBytesPerSecond = scratch.readLittleEndianUnsignedIntToInt();
        int blockAlignment = scratch.readLittleEndianUnsignedShort();
        int bitsPerSample = scratch.readLittleEndianUnsignedShort();
        int expectedBlockAlignment = (numChannels * bitsPerSample) / 8;
        if (blockAlignment != expectedBlockAlignment) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Expected block alignment: ");
            stringBuilder.append(expectedBlockAlignment);
            stringBuilder.append("; got: ");
            stringBuilder.append(blockAlignment);
            throw new ParserException(stringBuilder.toString());
        }
        if (type != 1) {
            if (type == 3) {
                if (bitsPerSample != 32) {
                    i = 0;
                }
                if (i != 0) {
                    str = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Unsupported WAV bit depth ");
                    stringBuilder2.append(bitsPerSample);
                    stringBuilder2.append(" for type ");
                    stringBuilder2.append(type);
                    Log.e(str, stringBuilder2.toString());
                    return null;
                }
                extractorInput.advancePeekPosition(((int) chunkHeader.size) - 16);
                return new WavHeader(numChannels, sampleRateHz, averageBytesPerSecond, blockAlignment, bitsPerSample, i);
            } else if (type != TYPE_WAVE_FORMAT_EXTENSIBLE) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unsupported WAV format type: ");
                stringBuilder.append(type);
                Log.e(str, stringBuilder.toString());
                return null;
            }
        }
        i = Util.getPcmEncoding(bitsPerSample);
        if (i != 0) {
            extractorInput.advancePeekPosition(((int) chunkHeader.size) - 16);
            return new WavHeader(numChannels, sampleRateHz, averageBytesPerSecond, blockAlignment, bitsPerSample, i);
        }
        str = TAG;
        StringBuilder stringBuilder22 = new StringBuilder();
        stringBuilder22.append("Unsupported WAV bit depth ");
        stringBuilder22.append(bitsPerSample);
        stringBuilder22.append(" for type ");
        stringBuilder22.append(type);
        Log.e(str, stringBuilder22.toString());
        return null;
    }

    public static void skipToData(ExtractorInput input, WavHeader wavHeader) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        Assertions.checkNotNull(wavHeader);
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(8);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString(DataSchemeDataSource.SCHEME_DATA)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring unknown WAV chunk: ");
            stringBuilder.append(chunkHeader.id);
            Log.w(str, stringBuilder.toString());
            long bytesToSkip = 8 + chunkHeader.size;
            if (chunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
                bytesToSkip = 12;
            }
            if (bytesToSkip > 2147483647L) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Chunk is too large (~2GB+) to skip; id: ");
                stringBuilder2.append(chunkHeader.id);
                throw new ParserException(stringBuilder2.toString());
            }
            input.skipFully((int) bytesToSkip);
            chunkHeader = ChunkHeader.peek(input, scratch);
        }
        input.skipFully(8);
        wavHeader.setDataBounds(input.getPosition(), chunkHeader.size);
    }
}
