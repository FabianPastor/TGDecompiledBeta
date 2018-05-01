package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class VbriSeeker
  implements Mp3Extractor.Seeker
{
  private static final String TAG = "VbriSeeker";
  private final long durationUs;
  private final long[] positions;
  private final long[] timesUs;
  
  private VbriSeeker(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long paramLong)
  {
    this.timesUs = paramArrayOfLong1;
    this.positions = paramArrayOfLong2;
    this.durationUs = paramLong;
  }
  
  public static VbriSeeker create(long paramLong1, long paramLong2, MpegAudioHeader paramMpegAudioHeader, ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(10);
    int i = paramParsableByteArray.readInt();
    if (i <= 0) {
      paramMpegAudioHeader = null;
    }
    for (;;)
    {
      label22:
      return paramMpegAudioHeader;
      int j = paramMpegAudioHeader.sampleRate;
      long l1 = i;
      if (j >= 32000) {}
      long l2;
      int m;
      long[] arrayOfLong;
      for (i = 1152;; i = 576)
      {
        l2 = Util.scaleLargeTimestamp(l1, i * 1000000L, j);
        int k = paramParsableByteArray.readUnsignedShort();
        m = paramParsableByteArray.readUnsignedShort();
        int n = paramParsableByteArray.readUnsignedShort();
        paramParsableByteArray.skipBytes(2);
        long l3 = paramMpegAudioHeader.frameSize;
        arrayOfLong = new long[k];
        paramMpegAudioHeader = new long[k];
        j = 0;
        l1 = paramLong2;
        if (j >= k) {
          break label256;
        }
        arrayOfLong[j] = (j * l2 / k);
        paramMpegAudioHeader[j] = Math.max(l1, paramLong2 + l3);
        switch (n)
        {
        default: 
          paramMpegAudioHeader = null;
          break label22;
        }
      }
      i = paramParsableByteArray.readUnsignedByte();
      for (;;)
      {
        l1 += i * m;
        j++;
        break;
        i = paramParsableByteArray.readUnsignedShort();
        continue;
        i = paramParsableByteArray.readUnsignedInt24();
        continue;
        i = paramParsableByteArray.readUnsignedIntToInt();
      }
      label256:
      if ((paramLong1 != -1L) && (paramLong1 != l1)) {
        Log.w("VbriSeeker", "VBRI data size mismatch: " + paramLong1 + ", " + l1);
      }
      paramMpegAudioHeader = new VbriSeeker(arrayOfLong, paramMpegAudioHeader, l2);
    }
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    int i = Util.binarySearchFloor(this.timesUs, paramLong, true, true);
    Object localObject = new SeekPoint(this.timesUs[i], this.positions[i]);
    if ((((SeekPoint)localObject).timeUs >= paramLong) || (i == this.timesUs.length - 1)) {}
    for (localObject = new SeekMap.SeekPoints((SeekPoint)localObject);; localObject = new SeekMap.SeekPoints((SeekPoint)localObject, new SeekPoint(this.timesUs[(i + 1)], this.positions[(i + 1)]))) {
      return (SeekMap.SeekPoints)localObject;
    }
  }
  
  public long getTimeUs(long paramLong)
  {
    return this.timesUs[Util.binarySearchFloor(this.positions, paramLong, true, true)];
  }
  
  public boolean isSeekable()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp3/VbriSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */