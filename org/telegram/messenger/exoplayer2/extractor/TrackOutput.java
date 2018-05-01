package org.telegram.messenger.exoplayer2.extractor;

import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface TrackOutput
{
  public abstract void format(Format paramFormat);
  
  public abstract int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException;
  
  public abstract void sampleData(ParsableByteArray paramParsableByteArray, int paramInt);
  
  public abstract void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, CryptoData paramCryptoData);
  
  public static final class CryptoData
  {
    public final int clearBlocks;
    public final int cryptoMode;
    public final int encryptedBlocks;
    public final byte[] encryptionKey;
    
    public CryptoData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    {
      this.cryptoMode = paramInt1;
      this.encryptionKey = paramArrayOfByte;
      this.encryptedBlocks = paramInt2;
      this.clearBlocks = paramInt3;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (CryptoData)paramObject;
          if ((this.cryptoMode != ((CryptoData)paramObject).cryptoMode) || (this.encryptedBlocks != ((CryptoData)paramObject).encryptedBlocks) || (this.clearBlocks != ((CryptoData)paramObject).clearBlocks) || (!Arrays.equals(this.encryptionKey, ((CryptoData)paramObject).encryptionKey))) {
            bool = false;
          }
        }
      }
    }
    
    public int hashCode()
    {
      return ((this.cryptoMode * 31 + Arrays.hashCode(this.encryptionKey)) * 31 + this.encryptedBlocks) * 31 + this.clearBlocks;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/TrackOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */