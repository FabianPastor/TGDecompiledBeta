package org.telegram.messenger.exoplayer.util;

import android.widget.MediaController.MediaPlayerControl;
import org.telegram.messenger.exoplayer.ExoPlayer;

public class PlayerControl
  implements MediaController.MediaPlayerControl
{
  private final ExoPlayer exoPlayer;
  
  public PlayerControl(ExoPlayer paramExoPlayer)
  {
    this.exoPlayer = paramExoPlayer;
  }
  
  public boolean canPause()
  {
    return true;
  }
  
  public boolean canSeekBackward()
  {
    return true;
  }
  
  public boolean canSeekForward()
  {
    return true;
  }
  
  public int getAudioSessionId()
  {
    throw new UnsupportedOperationException();
  }
  
  public int getBufferPercentage()
  {
    return this.exoPlayer.getBufferedPercentage();
  }
  
  public int getCurrentPosition()
  {
    if (this.exoPlayer.getDuration() == -1L) {
      return 0;
    }
    return (int)this.exoPlayer.getCurrentPosition();
  }
  
  public int getDuration()
  {
    if (this.exoPlayer.getDuration() == -1L) {
      return 0;
    }
    return (int)this.exoPlayer.getDuration();
  }
  
  public boolean isPlaying()
  {
    return this.exoPlayer.getPlayWhenReady();
  }
  
  public void pause()
  {
    this.exoPlayer.setPlayWhenReady(false);
  }
  
  public void seekTo(int paramInt)
  {
    if (this.exoPlayer.getDuration() == -1L) {}
    for (long l = 0L;; l = Math.min(Math.max(0, paramInt), getDuration()))
    {
      this.exoPlayer.seekTo(l);
      return;
    }
  }
  
  public void start()
  {
    this.exoPlayer.setPlayWhenReady(true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/PlayerControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */