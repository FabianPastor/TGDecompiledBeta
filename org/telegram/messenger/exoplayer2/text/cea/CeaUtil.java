package org.telegram.messenger.exoplayer2.text.cea;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class CeaUtil
{
  private static final int COUNTRY_CODE = 181;
  private static final int PAYLOAD_TYPE_CC = 4;
  private static final int PROVIDER_CODE = 49;
  private static final String TAG = "CeaUtil";
  private static final int USER_DATA_TYPE_CODE = 3;
  private static final int USER_ID = NUM;
  
  public static void consume(long paramLong, ParsableByteArray paramParsableByteArray, TrackOutput[] paramArrayOfTrackOutput)
  {
    while (paramParsableByteArray.bytesLeft() > 1)
    {
      int i = readNon255TerminatedValue(paramParsableByteArray);
      int j = readNon255TerminatedValue(paramParsableByteArray);
      if ((j == -1) || (j > paramParsableByteArray.bytesLeft()))
      {
        Log.w("CeaUtil", "Skipping remainder of malformed SEI NAL unit.");
        paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      }
      else if (isSeiMessageCea608(i, j, paramParsableByteArray))
      {
        paramParsableByteArray.skipBytes(8);
        int k = paramParsableByteArray.readUnsignedByte() & 0x1F;
        paramParsableByteArray.skipBytes(1);
        int m = k * 3;
        int n = paramParsableByteArray.getPosition();
        int i1 = paramArrayOfTrackOutput.length;
        for (i = 0; i < i1; i++)
        {
          TrackOutput localTrackOutput = paramArrayOfTrackOutput[i];
          paramParsableByteArray.setPosition(n);
          localTrackOutput.sampleData(paramParsableByteArray, m);
          localTrackOutput.sampleMetadata(paramLong, 1, m, 0, null);
        }
        paramParsableByteArray.skipBytes(j - (k * 3 + 10));
      }
      else
      {
        paramParsableByteArray.skipBytes(j);
      }
    }
  }
  
  private static boolean isSeiMessageCea608(int paramInt1, int paramInt2, ParsableByteArray paramParsableByteArray)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramInt1 == 4)
    {
      if (paramInt2 >= 8) {
        break label22;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label22:
      paramInt1 = paramParsableByteArray.getPosition();
      int i = paramParsableByteArray.readUnsignedByte();
      int j = paramParsableByteArray.readUnsignedShort();
      paramInt2 = paramParsableByteArray.readInt();
      int k = paramParsableByteArray.readUnsignedByte();
      paramParsableByteArray.setPosition(paramInt1);
      bool2 = bool1;
      if (i == 181)
      {
        bool2 = bool1;
        if (j == 49)
        {
          bool2 = bool1;
          if (paramInt2 == NUM)
          {
            bool2 = bool1;
            if (k == 3) {
              bool2 = true;
            }
          }
        }
      }
    }
  }
  
  private static int readNon255TerminatedValue(ParsableByteArray paramParsableByteArray)
  {
    int i = 0;
    if (paramParsableByteArray.bytesLeft() == 0) {}
    int k;
    for (i = -1;; i = k)
    {
      return i;
      int j = paramParsableByteArray.readUnsignedByte();
      k = i + j;
      i = k;
      if (j == 255) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/cea/CeaUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */