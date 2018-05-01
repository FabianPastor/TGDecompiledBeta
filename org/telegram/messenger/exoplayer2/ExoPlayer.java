package org.telegram.messenger.exoplayer2;

import android.os.Looper;
import org.telegram.messenger.exoplayer2.source.MediaSource;

public abstract interface ExoPlayer
  extends Player
{
  @Deprecated
  public static final int REPEAT_MODE_ALL = 2;
  @Deprecated
  public static final int REPEAT_MODE_OFF = 0;
  @Deprecated
  public static final int REPEAT_MODE_ONE = 1;
  @Deprecated
  public static final int STATE_BUFFERING = 2;
  @Deprecated
  public static final int STATE_ENDED = 4;
  @Deprecated
  public static final int STATE_IDLE = 1;
  @Deprecated
  public static final int STATE_READY = 3;
  
  @Deprecated
  public abstract void blockingSendMessages(ExoPlayerMessage... paramVarArgs);
  
  public abstract PlayerMessage createMessage(PlayerMessage.Target paramTarget);
  
  public abstract Looper getPlaybackLooper();
  
  public abstract void prepare(MediaSource paramMediaSource);
  
  public abstract void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2);
  
  @Deprecated
  public abstract void sendMessages(ExoPlayerMessage... paramVarArgs);
  
  public abstract void setSeekParameters(SeekParameters paramSeekParameters);
  
  @Deprecated
  public static abstract interface EventListener
    extends Player.EventListener
  {}
  
  @Deprecated
  public static abstract interface ExoPlayerComponent
    extends PlayerMessage.Target
  {}
  
  @Deprecated
  public static final class ExoPlayerMessage
  {
    public final Object message;
    public final int messageType;
    public final PlayerMessage.Target target;
    
    @Deprecated
    public ExoPlayerMessage(PlayerMessage.Target paramTarget, int paramInt, Object paramObject)
    {
      this.target = paramTarget;
      this.messageType = paramInt;
      this.message = paramObject;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ExoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */