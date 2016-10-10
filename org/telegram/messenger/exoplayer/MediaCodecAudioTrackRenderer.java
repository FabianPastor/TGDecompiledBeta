package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer.audio.AudioTrack;
import org.telegram.messenger.exoplayer.audio.AudioTrack.InitializationException;
import org.telegram.messenger.exoplayer.audio.AudioTrack.WriteException;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.util.MimeTypes;

@TargetApi(16)
public class MediaCodecAudioTrackRenderer
  extends MediaCodecTrackRenderer
  implements MediaClock
{
  public static final int MSG_SET_PLAYBACK_PARAMS = 2;
  public static final int MSG_SET_VOLUME = 1;
  private boolean allowPositionDiscontinuity;
  private int audioSessionId;
  private final AudioTrack audioTrack;
  private boolean audioTrackHasData;
  private long currentPositionUs;
  private final EventListener eventListener;
  private long lastFeedElapsedRealtimeMs;
  private boolean passthroughEnabled;
  private android.media.MediaFormat passthroughMediaFormat;
  private int pcmEncoding;
  
  public MediaCodecAudioTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramSampleSource, paramMediaCodecSelector, null, true);
  }
  
  public MediaCodecAudioTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramSampleSource, paramMediaCodecSelector, null, true, paramHandler, paramEventListener);
  }
  
  public MediaCodecAudioTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean)
  {
    this(paramSampleSource, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, null, null);
  }
  
  public MediaCodecAudioTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramSampleSource, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramEventListener, null, 3);
  }
  
  public MediaCodecAudioTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener, AudioCapabilities paramAudioCapabilities, int paramInt)
  {
    this(new SampleSource[] { paramSampleSource }, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramEventListener, paramAudioCapabilities, paramInt);
  }
  
  public MediaCodecAudioTrackRenderer(SampleSource[] paramArrayOfSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener, AudioCapabilities paramAudioCapabilities, int paramInt)
  {
    super(paramArrayOfSampleSource, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramEventListener);
    this.eventListener = paramEventListener;
    this.audioSessionId = 0;
    this.audioTrack = new AudioTrack(paramAudioCapabilities, paramInt);
  }
  
  private void notifyAudioTrackInitializationError(final AudioTrack.InitializationException paramInitializationException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackInitializationError(paramInitializationException);
        }
      });
    }
  }
  
  private void notifyAudioTrackUnderrun(final int paramInt, final long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackUnderrun(paramInt, paramLong1, this.val$elapsedSinceLastFeedMs);
        }
      });
    }
  }
  
  private void notifyAudioTrackWriteError(final AudioTrack.WriteException paramWriteException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackWriteError(paramWriteException);
        }
      });
    }
  }
  
  protected boolean allowPassthrough(String paramString)
  {
    return this.audioTrack.isPassthroughSupported(paramString);
  }
  
  protected void configureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, android.media.MediaFormat paramMediaFormat, MediaCrypto paramMediaCrypto)
  {
    String str = paramMediaFormat.getString("mime");
    if (this.passthroughEnabled)
    {
      paramMediaFormat.setString("mime", "audio/raw");
      paramMediaCodec.configure(paramMediaFormat, null, paramMediaCrypto, 0);
      paramMediaFormat.setString("mime", str);
      this.passthroughMediaFormat = paramMediaFormat;
      return;
    }
    paramMediaCodec.configure(paramMediaFormat, null, paramMediaCrypto, 0);
    this.passthroughMediaFormat = null;
  }
  
  protected DecoderInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    if (allowPassthrough(paramString))
    {
      DecoderInfo localDecoderInfo = paramMediaCodecSelector.getPassthroughDecoderInfo();
      if (localDecoderInfo != null)
      {
        this.passthroughEnabled = true;
        return localDecoderInfo;
      }
    }
    this.passthroughEnabled = false;
    return super.getDecoderInfo(paramMediaCodecSelector, paramString, paramBoolean);
  }
  
  protected MediaClock getMediaClock()
  {
    return this;
  }
  
  public long getPositionUs()
  {
    long l = this.audioTrack.getCurrentPositionUs(isEnded());
    if (l != Long.MIN_VALUE) {
      if (!this.allowPositionDiscontinuity) {
        break label42;
      }
    }
    for (;;)
    {
      this.currentPositionUs = l;
      this.allowPositionDiscontinuity = false;
      return this.currentPositionUs;
      label42:
      l = Math.max(this.currentPositionUs, l);
    }
  }
  
  protected void handleAudioTrackDiscontinuity() {}
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    switch (paramInt)
    {
    default: 
      super.handleMessage(paramInt, paramObject);
      return;
    case 1: 
      this.audioTrack.setVolume(((Float)paramObject).floatValue());
      return;
    }
    this.audioTrack.setPlaybackParams((PlaybackParams)paramObject);
  }
  
  protected boolean handlesTrack(MediaCodecSelector paramMediaCodecSelector, MediaFormat paramMediaFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    boolean bool2 = false;
    paramMediaFormat = paramMediaFormat.mimeType;
    boolean bool1 = bool2;
    if (MimeTypes.isAudio(paramMediaFormat)) {
      if ((!"audio/x-unknown".equals(paramMediaFormat)) && ((!allowPassthrough(paramMediaFormat)) || (paramMediaCodecSelector.getPassthroughDecoderInfo() == null)))
      {
        bool1 = bool2;
        if (paramMediaCodecSelector.getDecoderInfo(paramMediaFormat, false) == null) {}
      }
      else
      {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean isEnded()
  {
    return (super.isEnded()) && (!this.audioTrack.hasPendingData());
  }
  
  protected boolean isReady()
  {
    return (this.audioTrack.hasPendingData()) || (super.isReady());
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onDisabled()
    throws ExoPlaybackException
  {
    this.audioSessionId = 0;
    try
    {
      this.audioTrack.release();
      return;
    }
    finally
    {
      super.onDisabled();
    }
  }
  
  protected void onDiscontinuity(long paramLong)
    throws ExoPlaybackException
  {
    super.onDiscontinuity(paramLong);
    this.audioTrack.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
  }
  
  protected void onInputFormatChanged(MediaFormatHolder paramMediaFormatHolder)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramMediaFormatHolder);
    if ("audio/raw".equals(paramMediaFormatHolder.format.mimeType)) {}
    for (int i = paramMediaFormatHolder.format.pcmEncoding;; i = 2)
    {
      this.pcmEncoding = i;
      return;
    }
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, android.media.MediaFormat paramMediaFormat)
  {
    int i;
    if (this.passthroughMediaFormat != null)
    {
      i = 1;
      if (i == 0) {
        break label69;
      }
      paramMediaCodec = this.passthroughMediaFormat.getString("mime");
      label23:
      if (i == 0) {
        break label75;
      }
      paramMediaFormat = this.passthroughMediaFormat;
    }
    label69:
    label75:
    for (;;)
    {
      i = paramMediaFormat.getInteger("channel-count");
      int j = paramMediaFormat.getInteger("sample-rate");
      this.audioTrack.configure(paramMediaCodec, i, j, this.pcmEncoding);
      return;
      i = 0;
      break;
      paramMediaCodec = "audio/raw";
      break label23;
    }
  }
  
  protected void onOutputStreamEnded()
  {
    this.audioTrack.handleEndOfStream();
  }
  
  protected void onStarted()
  {
    super.onStarted();
    this.audioTrack.play();
  }
  
  protected void onStopped()
  {
    this.audioTrack.pause();
    super.onStopped();
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, MediaCodec.BufferInfo paramBufferInfo, int paramInt, boolean paramBoolean)
    throws ExoPlaybackException
  {
    if ((this.passthroughEnabled) && ((paramBufferInfo.flags & 0x2) != 0))
    {
      paramMediaCodec.releaseOutputBuffer(paramInt, false);
      return true;
    }
    if (paramBoolean)
    {
      paramMediaCodec.releaseOutputBuffer(paramInt, false);
      paramMediaCodec = this.codecCounters;
      paramMediaCodec.skippedOutputBufferCount += 1;
      this.audioTrack.handleDiscontinuity();
      return true;
    }
    if (!this.audioTrack.isInitialized()) {}
    do
    {
      for (;;)
      {
        try
        {
          if (this.audioSessionId != 0)
          {
            this.audioTrack.initialize(this.audioSessionId);
            this.audioTrackHasData = false;
            if (getState() == 3) {
              this.audioTrack.play();
            }
          }
        }
        catch (AudioTrack.InitializationException paramMediaCodec)
        {
          int i;
          notifyAudioTrackInitializationError(paramMediaCodec);
          throw new ExoPlaybackException(paramMediaCodec);
        }
        try
        {
          i = this.audioTrack.handleBuffer(paramByteBuffer, paramBufferInfo.offset, paramBufferInfo.size, paramBufferInfo.presentationTimeUs);
          this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
          if ((i & 0x1) != 0)
          {
            handleAudioTrackDiscontinuity();
            this.allowPositionDiscontinuity = true;
          }
          if ((i & 0x2) == 0) {
            break;
          }
          paramMediaCodec.releaseOutputBuffer(paramInt, false);
          paramMediaCodec = this.codecCounters;
          paramMediaCodec.renderedOutputBufferCount += 1;
          return true;
        }
        catch (AudioTrack.WriteException paramMediaCodec)
        {
          long l;
          notifyAudioTrackWriteError(paramMediaCodec);
          throw new ExoPlaybackException(paramMediaCodec);
        }
        this.audioSessionId = this.audioTrack.initialize();
        onAudioSessionId(this.audioSessionId);
      }
      paramBoolean = this.audioTrackHasData;
      this.audioTrackHasData = this.audioTrack.hasPendingData();
    } while ((!paramBoolean) || (this.audioTrackHasData) || (getState() != 3));
    paramLong2 = SystemClock.elapsedRealtime();
    l = this.lastFeedElapsedRealtimeMs;
    paramLong1 = this.audioTrack.getBufferSizeUs();
    if (paramLong1 == -1L) {}
    for (paramLong1 = -1L;; paramLong1 /= 1000L)
    {
      notifyAudioTrackUnderrun(this.audioTrack.getBufferSize(), paramLong1, paramLong2 - l);
      break;
    }
    return false;
  }
  
  public static abstract interface EventListener
    extends MediaCodecTrackRenderer.EventListener
  {
    public abstract void onAudioTrackInitializationError(AudioTrack.InitializationException paramInitializationException);
    
    public abstract void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2);
    
    public abstract void onAudioTrackWriteError(AudioTrack.WriteException paramWriteException);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/MediaCodecAudioTrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */