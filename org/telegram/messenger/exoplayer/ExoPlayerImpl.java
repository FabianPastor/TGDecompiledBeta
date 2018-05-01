package org.telegram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

final class ExoPlayerImpl
  implements ExoPlayer
{
  private static final String TAG = "ExoPlayerImpl";
  private final Handler eventHandler;
  private final ExoPlayerImplInternal internalPlayer;
  private final CopyOnWriteArraySet<ExoPlayer.Listener> listeners;
  private int pendingPlayWhenReadyAcks;
  private boolean playWhenReady;
  private int playbackState;
  private final int[] selectedTrackIndices;
  private final MediaFormat[][] trackFormats;
  
  @SuppressLint({"HandlerLeak"})
  public ExoPlayerImpl(int paramInt1, int paramInt2, int paramInt3)
  {
    Log.i("ExoPlayerImpl", "Init 1.5.10");
    this.playWhenReady = false;
    this.playbackState = 1;
    this.listeners = new CopyOnWriteArraySet();
    this.trackFormats = new MediaFormat[paramInt1][];
    this.selectedTrackIndices = new int[paramInt1];
    this.eventHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        ExoPlayerImpl.this.handleEvent(paramAnonymousMessage);
      }
    };
    this.internalPlayer = new ExoPlayerImplInternal(this.eventHandler, this.playWhenReady, this.selectedTrackIndices, paramInt2, paramInt3);
  }
  
  public void addListener(ExoPlayer.Listener paramListener)
  {
    this.listeners.add(paramListener);
  }
  
  public void blockingSendMessage(ExoPlayer.ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject)
  {
    this.internalPlayer.blockingSendMessage(paramExoPlayerComponent, paramInt, paramObject);
  }
  
  public int getBufferedPercentage()
  {
    long l1 = 100L;
    long l2 = getBufferedPosition();
    long l3 = getDuration();
    if ((l2 == -1L) || (l3 == -1L)) {
      return 0;
    }
    if (l3 == 0L) {}
    for (;;)
    {
      return (int)l1;
      l1 = 100L * l2 / l3;
    }
  }
  
  public long getBufferedPosition()
  {
    return this.internalPlayer.getBufferedPosition();
  }
  
  public long getCurrentPosition()
  {
    return this.internalPlayer.getCurrentPosition();
  }
  
  public long getDuration()
  {
    return this.internalPlayer.getDuration();
  }
  
  public boolean getPlayWhenReady()
  {
    return this.playWhenReady;
  }
  
  public Looper getPlaybackLooper()
  {
    return this.internalPlayer.getPlaybackLooper();
  }
  
  public int getPlaybackState()
  {
    return this.playbackState;
  }
  
  public int getSelectedTrack(int paramInt)
  {
    return this.selectedTrackIndices[paramInt];
  }
  
  public int getTrackCount(int paramInt)
  {
    if (this.trackFormats[paramInt] != null) {
      return this.trackFormats[paramInt].length;
    }
    return 0;
  }
  
  public MediaFormat getTrackFormat(int paramInt1, int paramInt2)
  {
    return this.trackFormats[paramInt1][paramInt2];
  }
  
  void handleEvent(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    }
    for (;;)
    {
      return;
      System.arraycopy(paramMessage.obj, 0, this.trackFormats, 0, this.trackFormats.length);
      this.playbackState = paramMessage.arg1;
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext()) {
        ((ExoPlayer.Listener)paramMessage.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
      }
      continue;
      this.playbackState = paramMessage.arg1;
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext()) {
        ((ExoPlayer.Listener)paramMessage.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
      }
      continue;
      this.pendingPlayWhenReadyAcks -= 1;
      if (this.pendingPlayWhenReadyAcks == 0)
      {
        paramMessage = this.listeners.iterator();
        while (paramMessage.hasNext()) {
          ((ExoPlayer.Listener)paramMessage.next()).onPlayWhenReadyCommitted();
        }
        continue;
        paramMessage = (ExoPlaybackException)paramMessage.obj;
        Iterator localIterator = this.listeners.iterator();
        while (localIterator.hasNext()) {
          ((ExoPlayer.Listener)localIterator.next()).onPlayerError(paramMessage);
        }
      }
    }
  }
  
  public boolean isPlayWhenReadyCommitted()
  {
    return this.pendingPlayWhenReadyAcks == 0;
  }
  
  public void prepare(TrackRenderer... paramVarArgs)
  {
    Arrays.fill(this.trackFormats, null);
    this.internalPlayer.prepare(paramVarArgs);
  }
  
  public void release()
  {
    this.internalPlayer.release();
    this.eventHandler.removeCallbacksAndMessages(null);
  }
  
  public void removeListener(ExoPlayer.Listener paramListener)
  {
    this.listeners.remove(paramListener);
  }
  
  public void seekTo(long paramLong)
  {
    this.internalPlayer.seekTo(paramLong);
  }
  
  public void sendMessage(ExoPlayer.ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject)
  {
    this.internalPlayer.sendMessage(paramExoPlayerComponent, paramInt, paramObject);
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    if (this.playWhenReady != paramBoolean)
    {
      this.playWhenReady = paramBoolean;
      this.pendingPlayWhenReadyAcks += 1;
      this.internalPlayer.setPlayWhenReady(paramBoolean);
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((ExoPlayer.Listener)localIterator.next()).onPlayerStateChanged(paramBoolean, this.playbackState);
      }
    }
  }
  
  public void setSelectedTrack(int paramInt1, int paramInt2)
  {
    if (this.selectedTrackIndices[paramInt1] != paramInt2)
    {
      this.selectedTrackIndices[paramInt1] = paramInt2;
      this.internalPlayer.setRendererSelectedTrack(paramInt1, paramInt2);
    }
  }
  
  public void stop()
  {
    this.internalPlayer.stop();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/ExoPlayerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */