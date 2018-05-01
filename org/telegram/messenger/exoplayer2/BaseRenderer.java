package org.telegram.messenger.exoplayer2;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;

public abstract class BaseRenderer
  implements Renderer, RendererCapabilities
{
  private RendererConfiguration configuration;
  private int index;
  private boolean readEndOfStream;
  private int state;
  private SampleStream stream;
  private boolean streamIsFinal;
  private long streamOffsetUs;
  private final int trackType;
  
  public BaseRenderer(int paramInt)
  {
    this.trackType = paramInt;
    this.readEndOfStream = true;
  }
  
  protected static boolean supportsFormatDrm(DrmSessionManager<?> paramDrmSessionManager, DrmInitData paramDrmInitData)
  {
    boolean bool;
    if (paramDrmInitData == null) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if (paramDrmSessionManager == null) {
        bool = false;
      } else {
        bool = paramDrmSessionManager.canAcquireSession(paramDrmInitData);
      }
    }
  }
  
  public final void disable()
  {
    boolean bool = true;
    if (this.state == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      this.state = 0;
      this.stream = null;
      this.streamIsFinal = false;
      onDisabled();
      return;
      bool = false;
    }
  }
  
  public final void enable(RendererConfiguration paramRendererConfiguration, Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.state == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.configuration = paramRendererConfiguration;
      this.state = 1;
      onEnabled(paramBoolean);
      replaceStream(paramArrayOfFormat, paramSampleStream, paramLong2);
      onPositionReset(paramLong1, paramBoolean);
      return;
    }
  }
  
  public final RendererCapabilities getCapabilities()
  {
    return this;
  }
  
  protected final RendererConfiguration getConfiguration()
  {
    return this.configuration;
  }
  
  protected final int getIndex()
  {
    return this.index;
  }
  
  public MediaClock getMediaClock()
  {
    return null;
  }
  
  public final int getState()
  {
    return this.state;
  }
  
  public final SampleStream getStream()
  {
    return this.stream;
  }
  
  public final int getTrackType()
  {
    return this.trackType;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {}
  
  public final boolean hasReadStreamToEnd()
  {
    return this.readEndOfStream;
  }
  
  public final boolean isCurrentStreamFinal()
  {
    return this.streamIsFinal;
  }
  
  protected final boolean isSourceReady()
  {
    if (this.readEndOfStream) {}
    for (boolean bool = this.streamIsFinal;; bool = this.stream.isReady()) {
      return bool;
    }
  }
  
  public final void maybeThrowStreamError()
    throws IOException
  {
    this.stream.maybeThrowError();
  }
  
  protected void onDisabled() {}
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onStarted()
    throws ExoPlaybackException
  {}
  
  protected void onStopped()
    throws ExoPlaybackException
  {}
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {}
  
  protected final int readSource(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    int i = -4;
    int j = this.stream.readData(paramFormatHolder, paramDecoderInputBuffer, paramBoolean);
    if (j == -4)
    {
      if (paramDecoderInputBuffer.isEndOfStream())
      {
        this.readEndOfStream = true;
        if (this.streamIsFinal) {}
        for (j = i;; j = -3) {
          return j;
        }
      }
      paramDecoderInputBuffer.timeUs += this.streamOffsetUs;
    }
    for (;;)
    {
      break;
      if (j == -5)
      {
        paramDecoderInputBuffer = paramFormatHolder.format;
        if (paramDecoderInputBuffer.subsampleOffsetUs != Long.MAX_VALUE) {
          paramFormatHolder.format = paramDecoderInputBuffer.copyWithSubsampleOffsetUs(paramDecoderInputBuffer.subsampleOffsetUs + this.streamOffsetUs);
        }
      }
    }
  }
  
  public final void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong)
    throws ExoPlaybackException
  {
    if (!this.streamIsFinal) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.stream = paramSampleStream;
      this.readEndOfStream = false;
      this.streamOffsetUs = paramLong;
      onStreamChanged(paramArrayOfFormat, paramLong);
      return;
    }
  }
  
  public final void resetPosition(long paramLong)
    throws ExoPlaybackException
  {
    this.streamIsFinal = false;
    this.readEndOfStream = false;
    onPositionReset(paramLong, false);
  }
  
  public final void setCurrentStreamFinal()
  {
    this.streamIsFinal = true;
  }
  
  public final void setIndex(int paramInt)
  {
    this.index = paramInt;
  }
  
  protected int skipSource(long paramLong)
  {
    return this.stream.skipData(paramLong - this.streamOffsetUs);
  }
  
  public final void start()
    throws ExoPlaybackException
  {
    boolean bool = true;
    if (this.state == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      this.state = 2;
      onStarted();
      return;
      bool = false;
    }
  }
  
  public final void stop()
    throws ExoPlaybackException
  {
    if (this.state == 2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      onStopped();
      return;
    }
  }
  
  public int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/BaseRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */