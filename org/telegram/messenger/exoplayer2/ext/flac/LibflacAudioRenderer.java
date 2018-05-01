package org.telegram.messenger.exoplayer2.ext.flac;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;

public class LibflacAudioRenderer
  extends SimpleDecoderAudioRenderer
{
  private static final int NUM_BUFFERS = 16;
  
  public LibflacAudioRenderer()
  {
    this(null, null, new AudioProcessor[0]);
  }
  
  public LibflacAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioProcessor... paramVarArgs)
  {
    super(paramHandler, paramAudioRendererEventListener, paramVarArgs);
  }
  
  protected FlacDecoder createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto)
    throws FlacDecoderException
  {
    return new FlacDecoder(16, 16, paramFormat.initializationData);
  }
  
  protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, Format paramFormat)
  {
    int i = 2;
    if (!"audio/flac".equalsIgnoreCase(paramFormat.sampleMimeType)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (!supportsOutputEncoding(2)) {
        i = 1;
      } else if (supportsFormatDrm(paramDrmSessionManager, paramFormat.drmInitData)) {
        i = 4;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/flac/LibflacAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */