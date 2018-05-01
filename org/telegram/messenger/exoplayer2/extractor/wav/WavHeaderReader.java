package org.telegram.messenger.exoplayer2.extractor.wav;

import android.util.Log;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class WavHeaderReader
{
  private static final String TAG = "WavHeaderReader";
  private static final int TYPE_FLOAT = 3;
  private static final int TYPE_PCM = 1;
  private static final int TYPE_WAVE_FORMAT_EXTENSIBLE = 65534;
  
  public static WavHeader peek(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    ParsableByteArray localParsableByteArray = new ParsableByteArray(16);
    if (ChunkHeader.peek(paramExtractorInput, localParsableByteArray).id != Util.getIntegerCodeForString("RIFF")) {
      paramExtractorInput = null;
    }
    for (;;)
    {
      return paramExtractorInput;
      paramExtractorInput.peekFully(localParsableByteArray.data, 0, 4);
      localParsableByteArray.setPosition(0);
      int i = localParsableByteArray.readInt();
      if (i != Util.getIntegerCodeForString("WAVE"))
      {
        Log.e("WavHeaderReader", "Unsupported RIFF format: " + i);
        paramExtractorInput = null;
      }
      else
      {
        for (ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray); localChunkHeader.id != Util.getIntegerCodeForString("fmt "); localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray)) {
          paramExtractorInput.advancePeekPosition((int)localChunkHeader.size);
        }
        if (localChunkHeader.size >= 16L) {}
        int j;
        int k;
        int m;
        int n;
        int i1;
        int i2;
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          paramExtractorInput.peekFully(localParsableByteArray.data, 0, 16);
          localParsableByteArray.setPosition(0);
          j = localParsableByteArray.readLittleEndianUnsignedShort();
          k = localParsableByteArray.readLittleEndianUnsignedShort();
          m = localParsableByteArray.readLittleEndianUnsignedIntToInt();
          n = localParsableByteArray.readLittleEndianUnsignedIntToInt();
          i1 = localParsableByteArray.readLittleEndianUnsignedShort();
          i2 = localParsableByteArray.readLittleEndianUnsignedShort();
          i = k * i2 / 8;
          if (i1 == i) {
            break;
          }
          throw new ParserException("Expected block alignment: " + i + "; got: " + i1);
        }
        switch (j)
        {
        default: 
          Log.e("WavHeaderReader", "Unsupported WAV format type: " + j);
          paramExtractorInput = null;
          break;
        case 1: 
        case 65534: 
          i = Util.getPcmEncoding(i2);
          if (i == 0)
          {
            Log.e("WavHeaderReader", "Unsupported WAV bit depth " + i2 + " for type " + j);
            paramExtractorInput = null;
          }
          break;
        case 3: 
          if (i2 == 32) {}
          for (i = 4;; i = 0) {
            break;
          }
          paramExtractorInput.advancePeekPosition((int)localChunkHeader.size - 16);
          paramExtractorInput = new WavHeader(k, m, n, i1, i2, i);
        }
      }
    }
  }
  
  public static void skipToData(ExtractorInput paramExtractorInput, WavHeader paramWavHeader)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    Assertions.checkNotNull(paramWavHeader);
    paramExtractorInput.resetPeekPosition();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(8);
    for (ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray); localChunkHeader.id != Util.getIntegerCodeForString("data"); localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray))
    {
      Log.w("WavHeaderReader", "Ignoring unknown WAV chunk: " + localChunkHeader.id);
      long l = 8L + localChunkHeader.size;
      if (localChunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
        l = 12L;
      }
      if (l > 2147483647L) {
        throw new ParserException("Chunk is too large (~2GB+) to skip; id: " + localChunkHeader.id);
      }
      paramExtractorInput.skipFully((int)l);
    }
    paramExtractorInput.skipFully(8);
    paramWavHeader.setDataBounds(paramExtractorInput.getPosition(), localChunkHeader.size);
  }
  
  private static final class ChunkHeader
  {
    public static final int SIZE_IN_BYTES = 8;
    public final int id;
    public final long size;
    
    private ChunkHeader(int paramInt, long paramLong)
    {
      this.id = paramInt;
      this.size = paramLong;
    }
    
    public static ChunkHeader peek(ExtractorInput paramExtractorInput, ParsableByteArray paramParsableByteArray)
      throws IOException, InterruptedException
    {
      paramExtractorInput.peekFully(paramParsableByteArray.data, 0, 8);
      paramParsableByteArray.setPosition(0);
      return new ChunkHeader(paramParsableByteArray.readInt(), paramParsableByteArray.readLittleEndianUnsignedInt());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/wav/WavHeaderReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */