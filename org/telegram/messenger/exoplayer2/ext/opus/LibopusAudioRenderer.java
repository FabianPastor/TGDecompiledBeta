package org.telegram.messenger.exoplayer2.ext.opus;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;

public final class LibopusAudioRenderer
  extends SimpleDecoderAudioRenderer
{
  private static final int INITIAL_INPUT_BUFFER_SIZE = 5760;
  private static final int NUM_BUFFERS = 16;
  private OpusDecoder decoder;
  
  public LibopusAudioRenderer()
  {
    this(null, null, new AudioProcessor[0]);
  }
  
  public LibopusAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, boolean paramBoolean, AudioProcessor... paramVarArgs)
  {
    super(paramHandler, paramAudioRendererEventListener, null, paramDrmSessionManager, paramBoolean, paramVarArgs);
  }
  
  public LibopusAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioProcessor... paramVarArgs)
  {
    super(paramHandler, paramAudioRendererEventListener, paramVarArgs);
  }
  
  protected OpusDecoder createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto)
    throws OpusDecoderException
  {
    this.decoder = new OpusDecoder(16, 16, 5760, paramFormat.initializationData, paramExoMediaCrypto);
    return this.decoder;
  }
  
  protected Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), 2, null, null, 0, null);
  }
  
  protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, Format paramFormat)
  {
    int i = 2;
    if (!"audio/opus".equalsIgnoreCase(paramFormat.sampleMimeType)) {
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/opus/LibopusAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */