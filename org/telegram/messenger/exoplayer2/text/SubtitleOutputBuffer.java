package org.telegram.messenger.exoplayer2.text;

import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.OutputBuffer;

public abstract class SubtitleOutputBuffer
  extends OutputBuffer
  implements Subtitle
{
  private long subsampleOffsetUs;
  private Subtitle subtitle;
  
  public void clear()
  {
    super.clear();
    this.subtitle = null;
  }
  
  public List<Cue> getCues(long paramLong)
  {
    return this.subtitle.getCues(paramLong - this.subsampleOffsetUs);
  }
  
  public long getEventTime(int paramInt)
  {
    return this.subtitle.getEventTime(paramInt) + this.subsampleOffsetUs;
  }
  
  public int getEventTimeCount()
  {
    return this.subtitle.getEventTimeCount();
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    return this.subtitle.getNextEventTimeIndex(paramLong - this.subsampleOffsetUs);
  }
  
  public abstract void release();
  
  public void setContent(long paramLong1, Subtitle paramSubtitle, long paramLong2)
  {
    this.timeUs = paramLong1;
    this.subtitle = paramSubtitle;
    paramLong1 = paramLong2;
    if (paramLong2 == Long.MAX_VALUE) {
      paramLong1 = this.timeUs;
    }
    this.subsampleOffsetUs = paramLong1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SubtitleOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */