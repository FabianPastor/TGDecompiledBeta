package org.telegram.messenger.exoplayer2.extractor.mp3;

import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Util;

final class ConstantBitrateSeeker
  implements Mp3Extractor.Seeker
{
  private static final int BITS_PER_BYTE = 8;
  private final int bitrate;
  private final long dataSize;
  private final long durationUs;
  private final long firstFramePosition;
  private final int frameSize;
  
  public ConstantBitrateSeeker(long paramLong1, long paramLong2, MpegAudioHeader paramMpegAudioHeader)
  {
    this.firstFramePosition = paramLong2;
    this.frameSize = paramMpegAudioHeader.frameSize;
    this.bitrate = paramMpegAudioHeader.bitrate;
    if (paramLong1 == -1L) {
      this.dataSize = -1L;
    }
    for (this.durationUs = -9223372036854775807L;; this.durationUs = getTimeUs(paramLong1))
    {
      return;
      this.dataSize = (paramLong1 - paramLong2);
    }
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    Object localObject;
    if (this.dataSize == -1L) {
      localObject = new SeekMap.SeekPoints(new SeekPoint(0L, this.firstFramePosition));
    }
    for (;;)
    {
      return (SeekMap.SeekPoints)localObject;
      long l1 = Util.constrainValue(this.bitrate * paramLong / 8000000L / this.frameSize * this.frameSize, 0L, this.dataSize - this.frameSize);
      long l2 = this.firstFramePosition + l1;
      long l3 = getTimeUs(l2);
      localObject = new SeekPoint(l3, l2);
      if ((l3 >= paramLong) || (l1 == this.dataSize - this.frameSize))
      {
        localObject = new SeekMap.SeekPoints((SeekPoint)localObject);
      }
      else
      {
        paramLong = l2 + this.frameSize;
        localObject = new SeekMap.SeekPoints((SeekPoint)localObject, new SeekPoint(getTimeUs(paramLong), paramLong));
      }
    }
  }
  
  public long getTimeUs(long paramLong)
  {
    return Math.max(0L, paramLong - this.firstFramePosition) * 1000000L * 8L / this.bitrate;
  }
  
  public boolean isSeekable()
  {
    if (this.dataSize != -1L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp3/ConstantBitrateSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */