package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.StandaloneMediaClock;

final class DefaultMediaClock
  implements MediaClock
{
  private final PlaybackParameterListener listener;
  private MediaClock rendererClock;
  private Renderer rendererClockSource;
  private final StandaloneMediaClock standaloneMediaClock;
  
  public DefaultMediaClock(PlaybackParameterListener paramPlaybackParameterListener, Clock paramClock)
  {
    this.listener = paramPlaybackParameterListener;
    this.standaloneMediaClock = new StandaloneMediaClock(paramClock);
  }
  
  private void ensureSynced()
  {
    long l = this.rendererClock.getPositionUs();
    this.standaloneMediaClock.resetPosition(l);
    PlaybackParameters localPlaybackParameters = this.rendererClock.getPlaybackParameters();
    if (!localPlaybackParameters.equals(this.standaloneMediaClock.getPlaybackParameters()))
    {
      this.standaloneMediaClock.setPlaybackParameters(localPlaybackParameters);
      this.listener.onPlaybackParametersChanged(localPlaybackParameters);
    }
  }
  
  private boolean isUsingRendererClock()
  {
    if ((this.rendererClockSource != null) && (!this.rendererClockSource.isEnded()) && ((this.rendererClockSource.isReady()) || (!this.rendererClockSource.hasReadStreamToEnd()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    if (this.rendererClock != null) {}
    for (PlaybackParameters localPlaybackParameters = this.rendererClock.getPlaybackParameters();; localPlaybackParameters = this.standaloneMediaClock.getPlaybackParameters()) {
      return localPlaybackParameters;
    }
  }
  
  public long getPositionUs()
  {
    if (isUsingRendererClock()) {}
    for (long l = this.rendererClock.getPositionUs();; l = this.standaloneMediaClock.getPositionUs()) {
      return l;
    }
  }
  
  public void onRendererDisabled(Renderer paramRenderer)
  {
    if (paramRenderer == this.rendererClockSource)
    {
      this.rendererClock = null;
      this.rendererClockSource = null;
    }
  }
  
  public void onRendererEnabled(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    MediaClock localMediaClock = paramRenderer.getMediaClock();
    if ((localMediaClock != null) && (localMediaClock != this.rendererClock))
    {
      if (this.rendererClock != null) {
        throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
      }
      this.rendererClock = localMediaClock;
      this.rendererClockSource = paramRenderer;
      this.rendererClock.setPlaybackParameters(this.standaloneMediaClock.getPlaybackParameters());
      ensureSynced();
    }
  }
  
  public void resetPosition(long paramLong)
  {
    this.standaloneMediaClock.resetPosition(paramLong);
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    PlaybackParameters localPlaybackParameters = paramPlaybackParameters;
    if (this.rendererClock != null) {
      localPlaybackParameters = this.rendererClock.setPlaybackParameters(paramPlaybackParameters);
    }
    this.standaloneMediaClock.setPlaybackParameters(localPlaybackParameters);
    this.listener.onPlaybackParametersChanged(localPlaybackParameters);
    return localPlaybackParameters;
  }
  
  public void start()
  {
    this.standaloneMediaClock.start();
  }
  
  public void stop()
  {
    this.standaloneMediaClock.stop();
  }
  
  public long syncAndGetPositionUs()
  {
    if (isUsingRendererClock()) {
      ensureSynced();
    }
    for (long l = this.rendererClock.getPositionUs();; l = this.standaloneMediaClock.getPositionUs()) {
      return l;
    }
  }
  
  public static abstract interface PlaybackParameterListener
  {
    public abstract void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/DefaultMediaClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */