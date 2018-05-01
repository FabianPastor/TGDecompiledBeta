package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.PlaybackParameters;

public abstract interface MediaClock
{
  public abstract PlaybackParameters getPlaybackParameters();
  
  public abstract long getPositionUs();
  
  public abstract PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/MediaClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */