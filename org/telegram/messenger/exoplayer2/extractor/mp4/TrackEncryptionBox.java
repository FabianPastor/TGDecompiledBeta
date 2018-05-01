package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TrackEncryptionBox
{
  private static final String TAG = "TrackEncryptionBox";
  public final TrackOutput.CryptoData cryptoData;
  public final byte[] defaultInitializationVector;
  public final int initializationVectorSize;
  public final boolean isEncrypted;
  public final String schemeType;
  
  public TrackEncryptionBox(boolean paramBoolean, String paramString, int paramInt1, byte[] paramArrayOfByte1, int paramInt2, int paramInt3, byte[] paramArrayOfByte2)
  {
    int j;
    if (paramInt1 == 0)
    {
      j = 1;
      if (paramArrayOfByte2 != null) {
        break label76;
      }
    }
    for (;;)
    {
      Assertions.checkArgument(i ^ j);
      this.isEncrypted = paramBoolean;
      this.schemeType = paramString;
      this.initializationVectorSize = paramInt1;
      this.defaultInitializationVector = paramArrayOfByte2;
      this.cryptoData = new TrackOutput.CryptoData(schemeToCryptoMode(paramString), paramArrayOfByte1, paramInt2, paramInt3);
      return;
      j = 0;
      break;
      label76:
      i = 0;
    }
  }
  
  private static int schemeToCryptoMode(String paramString)
  {
    int i = 1;
    int j;
    if (paramString == null) {
      j = i;
    }
    for (;;)
    {
      label8:
      return j;
      int k = -1;
      switch (paramString.hashCode())
      {
      }
      for (;;)
      {
        j = i;
        switch (k)
        {
        case 0: 
        case 1: 
        default: 
          Log.w("TrackEncryptionBox", "Unsupported protection scheme type '" + paramString + "'. Assuming AES-CTR crypto mode.");
          j = i;
          break label8;
          if (paramString.equals("cenc"))
          {
            k = 0;
            continue;
            if (paramString.equals("cens"))
            {
              k = 1;
              continue;
              if (paramString.equals("cbc1"))
              {
                k = 2;
                continue;
                if (paramString.equals("cbcs")) {
                  k = 3;
                }
              }
            }
          }
          break;
        }
      }
      j = 2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/TrackEncryptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */