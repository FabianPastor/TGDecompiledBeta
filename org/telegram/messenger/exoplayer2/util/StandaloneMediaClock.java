package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.PlaybackParameters;

public final class StandaloneMediaClock
  implements MediaClock
{
  private long baseElapsedMs;
  private long baseUs;
  private final Clock clock;
  private PlaybackParameters playbackParameters;
  private boolean started;
  
  public StandaloneMediaClock(Clock paramClock)
  {
    this.clock = paramClock;
    this.playbackParameters = PlaybackParameters.DEFAULT;
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return this.playbackParameters;
  }
  
  public long getPositionUs()
  {
    long l1 = this.baseUs;
    long l2 = l1;
    if (this.started)
    {
      l2 = this.clock.elapsedRealtime() - this.baseElapsedMs;
      if (this.playbackParameters.speed != 1.0F) {
        break label50;
      }
    }
    label50:
    for (l2 = l1 + C.msToUs(l2);; l2 = l1 + this.playbackParameters.getMediaTimeUsForPlayoutTimeMs(l2)) {
      return l2;
    }
  }
  
  public void resetPosition(long paramLong)
  {
    this.baseUs = paramLong;
    if (this.started) {
      this.baseElapsedMs = this.clock.elapsedRealtime();
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    if (this.started) {
      resetPosition(getPositionUs());
    }
    this.playbackParameters = paramPlaybackParameters;
    return paramPlaybackParameters;
  }
  
  public void start()
  {
    if (!this.started)
    {
      this.baseElapsedMs = this.clock.elapsedRealtime();
      this.started = true;
    }
  }
  
  public void stop()
  {
    if (this.started)
    {
      resetPosition(getPositionUs());
      this.started = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/StandaloneMediaClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */