package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.util.Assertions;

public abstract class TrackRenderer
  implements ExoPlayer.ExoPlayerComponent
{
  public static final long END_OF_TRACK_US = -3L;
  public static final long MATCH_LONGEST_US = -2L;
  protected static final int STATE_ENABLED = 2;
  protected static final int STATE_PREPARED = 1;
  protected static final int STATE_RELEASED = -1;
  protected static final int STATE_STARTED = 3;
  protected static final int STATE_UNPREPARED = 0;
  public static final long UNKNOWN_TIME_US = -1L;
  private int state;
  
  final void disable()
    throws ExoPlaybackException
  {
    if (this.state == 2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      onDisabled();
      return;
    }
  }
  
  protected abstract boolean doPrepare(long paramLong)
    throws ExoPlaybackException;
  
  protected abstract void doSomeWork(long paramLong1, long paramLong2)
    throws ExoPlaybackException;
  
  final void enable(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    boolean bool = true;
    if (this.state == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      this.state = 2;
      onEnabled(paramInt, paramLong, paramBoolean);
      return;
      bool = false;
    }
  }
  
  protected abstract long getBufferedPositionUs();
  
  protected abstract long getDurationUs();
  
  protected abstract MediaFormat getFormat(int paramInt);
  
  protected MediaClock getMediaClock()
  {
    return null;
  }
  
  protected final int getState()
  {
    return this.state;
  }
  
  protected abstract int getTrackCount();
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {}
  
  protected abstract boolean isEnded();
  
  protected abstract boolean isReady();
  
  protected abstract void maybeThrowError()
    throws ExoPlaybackException;
  
  protected void onDisabled()
    throws ExoPlaybackException
  {}
  
  protected void onEnabled(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onReleased()
    throws ExoPlaybackException
  {}
  
  protected void onStarted()
    throws ExoPlaybackException
  {}
  
  protected void onStopped()
    throws ExoPlaybackException
  {}
  
  final int prepare(long paramLong)
    throws ExoPlaybackException
  {
    int i = 1;
    boolean bool;
    if (this.state == 0)
    {
      bool = true;
      Assertions.checkState(bool);
      if (!doPrepare(paramLong)) {
        break label41;
      }
    }
    for (;;)
    {
      this.state = i;
      return this.state;
      bool = false;
      break;
      label41:
      i = 0;
    }
  }
  
  final void release()
    throws ExoPlaybackException
  {
    if ((this.state != 2) && (this.state != 3) && (this.state != -1)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = -1;
      onReleased();
      return;
    }
  }
  
  protected abstract void seekTo(long paramLong)
    throws ExoPlaybackException;
  
  final void start()
    throws ExoPlaybackException
  {
    if (this.state == 2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 3;
      onStarted();
      return;
    }
  }
  
  final void stop()
    throws ExoPlaybackException
  {
    if (this.state == 3) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 2;
      onStopped();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/TrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */