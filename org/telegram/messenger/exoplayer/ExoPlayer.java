package org.telegram.messenger.exoplayer;

import android.os.Looper;

public abstract interface ExoPlayer
{
  public static final int STATE_BUFFERING = 3;
  public static final int STATE_ENDED = 5;
  public static final int STATE_IDLE = 1;
  public static final int STATE_PREPARING = 2;
  public static final int STATE_READY = 4;
  public static final int TRACK_DEFAULT = 0;
  public static final int TRACK_DISABLED = -1;
  public static final long UNKNOWN_TIME = -1L;
  
  public abstract void addListener(Listener paramListener);
  
  public abstract void blockingSendMessage(ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject);
  
  public abstract int getBufferedPercentage();
  
  public abstract long getBufferedPosition();
  
  public abstract long getCurrentPosition();
  
  public abstract long getDuration();
  
  public abstract boolean getPlayWhenReady();
  
  public abstract Looper getPlaybackLooper();
  
  public abstract int getPlaybackState();
  
  public abstract int getSelectedTrack(int paramInt);
  
  public abstract int getTrackCount(int paramInt);
  
  public abstract MediaFormat getTrackFormat(int paramInt1, int paramInt2);
  
  public abstract boolean isPlayWhenReadyCommitted();
  
  public abstract void prepare(TrackRenderer... paramVarArgs);
  
  public abstract void release();
  
  public abstract void removeListener(Listener paramListener);
  
  public abstract void seekTo(long paramLong);
  
  public abstract void sendMessage(ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject);
  
  public abstract void setPlayWhenReady(boolean paramBoolean);
  
  public abstract void setSelectedTrack(int paramInt1, int paramInt2);
  
  public abstract void stop();
  
  public static abstract interface ExoPlayerComponent
  {
    public abstract void handleMessage(int paramInt, Object paramObject)
      throws ExoPlaybackException;
  }
  
  public static final class Factory
  {
    public static final int DEFAULT_MIN_BUFFER_MS = 2500;
    public static final int DEFAULT_MIN_REBUFFER_MS = 5000;
    
    public static ExoPlayer newInstance(int paramInt)
    {
      return new ExoPlayerImpl(paramInt, 2500, 5000);
    }
    
    public static ExoPlayer newInstance(int paramInt1, int paramInt2, int paramInt3)
    {
      return new ExoPlayerImpl(paramInt1, paramInt2, paramInt3);
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onPlayWhenReadyCommitted();
    
    public abstract void onPlayerError(ExoPlaybackException paramExoPlaybackException);
    
    public abstract void onPlayerStateChanged(boolean paramBoolean, int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/ExoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */