package org.telegram.messenger.exoplayer2;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.util.MediaClock;

public abstract interface Renderer
  extends PlayerMessage.Target
{
  public static final int STATE_DISABLED = 0;
  public static final int STATE_ENABLED = 1;
  public static final int STATE_STARTED = 2;
  
  public abstract void disable();
  
  public abstract void enable(RendererConfiguration paramRendererConfiguration, Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2)
    throws ExoPlaybackException;
  
  public abstract RendererCapabilities getCapabilities();
  
  public abstract MediaClock getMediaClock();
  
  public abstract int getState();
  
  public abstract SampleStream getStream();
  
  public abstract int getTrackType();
  
  public abstract boolean hasReadStreamToEnd();
  
  public abstract boolean isCurrentStreamFinal();
  
  public abstract boolean isEnded();
  
  public abstract boolean isReady();
  
  public abstract void maybeThrowStreamError()
    throws IOException;
  
  public abstract void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException;
  
  public abstract void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong)
    throws ExoPlaybackException;
  
  public abstract void resetPosition(long paramLong)
    throws ExoPlaybackException;
  
  public abstract void setCurrentStreamFinal();
  
  public abstract void setIndex(int paramInt);
  
  public abstract void start()
    throws ExoPlaybackException;
  
  public abstract void stop()
    throws ExoPlaybackException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/Renderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */