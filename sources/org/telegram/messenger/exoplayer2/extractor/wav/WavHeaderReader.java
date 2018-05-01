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

        private ChunkHeader(int i, long j) {
            this.id = i;
            this.size = j;
        }

        public static ChunkHeader peek(ExtractorInput extractorInput, ParsableByteArray parsableByteArray) throws IOException, InterruptedException {
            extractorInput.peekFully(parsableByteArray.data, 0, 8);
            parsableByteArray.setPosition(0);
            return new ChunkHeader(parsableByteArray.readInt(), parsableByteArray.readLittleEndianUnsignedInt());
        }
    }

    WavHeaderReader() {
    }

    public static WavHeader peek(ExtractorInput extractorInput) throws IOException, InterruptedException {
        Assertions.checkNotNull(extractorInput);
        ParsableByteArray parsableByteArray = new ParsableByteArray(16);
        if (ChunkHeader.peek(extractorInput, parsableByteArray).id != Util.getIntegerCodeForString("RIFF")) {
            return null;
        }
        int i = 4;
        extractorInput.peekFully(parsableByteArray.data, 0, 4);
        parsableByteArray.setPosition(0);
        int readInt = parsableByteArray.readInt();
        if (readInt != Util.getIntegerCodeForString("WAVE")) {
            extractorInput = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported RIFF format: ");
            stringBuilder.append(readInt);
            Log.e(extractorInput, stringBuilder.toString());
            return null;
        }
        ChunkHeader peek = ChunkHeader.peek(extractorInput, parsableByteArray);
        while (peek.id != Util.getIntegerCodeForString("fmt ")) {
            extractorInput.advancePeekPosition((int) peek.size);
            peek = ChunkHeader.peek(extractorInput, parsableByteArray);
        }
        Assertions.checkState(peek.size >= 16);
        extractorInput.peekFully(parsableByteArray.data, 0, 16);
        parsableByteArray.setPosition(0);
        int readLittleEndianUnsignedShort = parsableByteArray.readLittleEndianUnsignedShort();
        int readLittleEndianUnsignedShort2 = parsableByteArray.readLittleEndianUnsignedShort();
        int readLittleEndianUnsignedIntToInt = parsableByteArray.readLittleEndianUnsignedIntToInt();
        int readLittleEndianUnsignedIntToInt2 = parsableByteArray.readLittleEndianUnsignedIntToInt();
        int readLittleEndianUnsignedShort3 = parsableByteArray.readLittleEndianUnsignedShort();
        int readLittleEndianUnsignedShort4 = parsableByteArray.readLittleEndianUnsignedShort();
        int i2 = (readLittleEndianUnsignedShort2 * readLittleEndianUnsignedShort4) / 8;
        if (readLittleEndianUnsignedShort3 != i2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Expected block alignment: ");
            stringBuilder2.append(i2);
            stringBuilder2.append("; got: ");
            stringBuilder2.append(readLittleEndianUnsignedShort3);
            throw new ParserException(stringBuilder2.toString());
        }
        int i3;
        if (readLittleEndianUnsignedShort != 1) {
            if (readLittleEndianUnsignedShort == 3) {
                if (readLittleEndianUnsignedShort4 != 32) {
                    i = 0;
                }
                i3 = i;
                if (i3 != 0) {
                    extractorInput = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Unsupported WAV bit depth ");
                    stringBuilder.append(readLittleEndianUnsignedShort4);
                    stringBuilder.append(" for type ");
                    stringBuilder.append(readLittleEndianUnsignedShort);
                    Log.e(extractorInput, stringBuilder.toString());
                    return null;
                }
                extractorInput.advancePeekPosition(((int) peek.size) - 16);
                return new WavHeader(readLittleEndianUnsignedShort2, readLittleEndianUnsignedIntToInt, readLittleEndianUnsignedIntToInt2, readLittleEndianUnsignedShort3, readLittleEndianUnsignedShort4, i3);
            } else if (readLittleEndianUnsignedShort != TYPE_WAVE_FORMAT_EXTENSIBLE) {
                extractorInput = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unsupported WAV format type: ");
                stringBuilder.append(readLittleEndianUnsignedShort);
                Log.e(extractorInput, stringBuilder.toString());
                return null;
            }
        }
        i3 = Util.getPcmEncoding(readLittleEndianUnsignedShort4);
        if (i3 != 0) {
            extractorInput.advancePeekPosition(((int) peek.size) - 16);
            return new WavHeader(readLittleEndianUnsignedShort2, readLittleEndianUnsignedIntToInt, readLittleEndianUnsignedIntToInt2, readLittleEndianUnsignedShort3, readLittleEndianUnsignedShort4, i3);
        }
        extractorInput = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported WAV bit depth ");
        stringBuilder.append(readLittleEndianUnsignedShort4);
        stringBuilder.append(" for type ");
        stringBuilder.append(readLittleEndianUnsignedShort);
        Log.e(extractorInput, stringBuilder.toString());
        return null;
    }

    public static void skipToData(ExtractorInput extractorInput, WavHeader wavHeader) throws IOException, InterruptedException {
        Assertions.checkNotNull(extractorInput);
        Assertions.checkNotNull(wavHeader);
        extractorInput.resetPeekPosition();
        ParsableByteArray parsableByteArray = new ParsableByteArray(8);
        ChunkHeader peek = ChunkHeader.peek(extractorInput, parsableByteArray);
        while (peek.id != Util.getIntegerCodeForString(DataSchemeDataSource.SCHEME_DATA)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring unknown WAV chunk: ");
            stringBuilder.append(peek.id);
            Log.w(str, stringBuilder.toString());
            long j = 8 + peek.size;
            if (peek.id == Util.getIntegerCodeForString("RIFF")) {
                j = 12;
            }
            if (j > 2147483647L) {
                wavHeader = new StringBuilder();
                wavHeader.append("Chunk is too large (~2GB+) to skip; id: ");
                wavHeader.append(peek.id);
                throw new ParserException(wavHeader.toString());
            }
            extractorInput.skipFully((int) j);
            peek = ChunkHeader.peek(extractorInput, parsableByteArray);
        }
        extractorInput.skipFully(8);
        wavHeader.setDataBounds(extractorInput.getPosition(), peek.size);
    }
}
