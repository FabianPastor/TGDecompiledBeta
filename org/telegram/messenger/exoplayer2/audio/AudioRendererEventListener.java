package org.telegram.messenger.exoplayer2.audio;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract interface AudioRendererEventListener
{
  public abstract void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2);
  
  public abstract void onAudioDisabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onAudioEnabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onAudioInputFormatChanged(Format paramFormat);
  
  public abstract void onAudioSessionId(int paramInt);
  
  public abstract void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2);
  
  public static final class EventDispatcher
  {
    private final Handler handler;
    private final AudioRendererEventListener listener;
    
    public EventDispatcher(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
    {
      if (paramAudioRendererEventListener != null) {}
      for (paramHandler = (Handler)Assertions.checkNotNull(paramHandler);; paramHandler = null)
      {
        this.handler = paramHandler;
        this.listener = paramAudioRendererEventListener;
        return;
      }
    }
    
    public void audioSessionId(final int paramInt)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioSessionId(paramInt);
          }
        });
      }
    }
    
    public void audioTrackUnderrun(final int paramInt, final long paramLong1, long paramLong2)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioSinkUnderrun(paramInt, paramLong1, this.val$elapsedSinceLastFeedMs);
          }
        });
      }
    }
    
    public void decoderInitialized(final String paramString, final long paramLong1, long paramLong2)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioDecoderInitialized(paramString, paramLong1, this.val$initializationDurationMs);
          }
        });
      }
    }
    
    public void disabled(final DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            paramDecoderCounters.ensureUpdated();
            AudioRendererEventListener.this.onAudioDisabled(paramDecoderCounters);
          }
        });
      }
    }
    
    public void enabled(final DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioEnabled(paramDecoderCounters);
          }
        });
      }
    }
    
    public void inputFormatChanged(final Format paramFormat)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioInputFormatChanged(paramFormat);
          }
        });
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */