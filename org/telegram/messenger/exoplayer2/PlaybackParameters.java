package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Assertions;

public final class PlaybackParameters
{
  public static final PlaybackParameters DEFAULT = new PlaybackParameters(1.0F, 1.0F);
  public final float pitch;
  private final int scaledUsPerMs;
  public final float speed;
  
  public PlaybackParameters(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 > 0.0F)
    {
      bool2 = true;
      Assertions.checkArgument(bool2);
      if (paramFloat2 <= 0.0F) {
        break label62;
      }
    }
    label62:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Assertions.checkArgument(bool2);
      this.speed = paramFloat1;
      this.pitch = paramFloat2;
      this.scaledUsPerMs = Math.round(1000.0F * paramFloat1);
      return;
      bool2 = false;
      break;
    }
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
        paramObject = (PlaybackParameters)paramObject;
        if ((this.speed != ((PlaybackParameters)paramObject).speed) || (this.pitch != ((PlaybackParameters)paramObject).pitch)) {
          bool = false;
        }
      }
    }
  }
  
  public long getMediaTimeUsForPlayoutTimeMs(long paramLong)
  {
    return this.scaledUsPerMs * paramLong;
  }
  
  public int hashCode()
  {
    return (Float.floatToRawIntBits(this.speed) + 527) * 31 + Float.floatToRawIntBits(this.pitch);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/PlaybackParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */