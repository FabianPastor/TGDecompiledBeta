package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;

final class VarintReader
{
  private static final int STATE_BEGIN_READING = 0;
  private static final int STATE_READ_CONTENTS = 1;
  private static final long[] VARINT_LENGTH_MASKS = { 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L };
  private int length;
  private final byte[] scratch = new byte[8];
  private int state;
  
  public static long assembleVarint(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    long l1 = paramArrayOfByte[0] & 0xFF;
    long l2 = l1;
    if (paramBoolean) {
      l2 = l1 & (VARINT_LENGTH_MASKS[(paramInt - 1)] ^ 0xFFFFFFFFFFFFFFFF);
    }
    for (int i = 1; i < paramInt; i++) {
      l2 = l2 << 8 | paramArrayOfByte[i] & 0xFF;
    }
    return l2;
  }
  
  public static int parseUnsignedVarintLength(int paramInt)
  {
    int i = -1;
    for (int j = 0;; j++)
    {
      int k = i;
      if (j < VARINT_LENGTH_MASKS.length)
      {
        if ((VARINT_LENGTH_MASKS[j] & paramInt) != 0L) {
          k = j + 1;
        }
      }
      else {
        return k;
      }
    }
  }
  
  public int getLastLength()
  {
    return this.length;
  }
  
  public long readUnsignedVarint(ExtractorInput paramExtractorInput, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    throws IOException, InterruptedException
  {
    long l;
    if (this.state == 0) {
      if (!paramExtractorInput.readFully(this.scratch, 0, 1, paramBoolean1)) {
        l = -1L;
      }
    }
    for (;;)
    {
      return l;
      this.length = parseUnsignedVarintLength(this.scratch[0] & 0xFF);
      if (this.length == -1) {
        throw new IllegalStateException("No valid varint length mask found");
      }
      this.state = 1;
      if (this.length > paramInt)
      {
        this.state = 0;
        l = -2L;
      }
      else
      {
        if (this.length != 1) {
          paramExtractorInput.readFully(this.scratch, 1, this.length - 1);
        }
        this.state = 0;
        l = assembleVarint(this.scratch, this.length, paramBoolean2);
      }
    }
  }
  
  public void reset()
  {
    this.state = 0;
    this.length = 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mkv/VarintReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */