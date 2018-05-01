package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.AudioSink;
import org.telegram.messenger.exoplayer2.audio.DefaultAudioSink;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class FfmpegAudioRenderer
  extends SimpleDecoderAudioRenderer
{
  private static final int INITIAL_INPUT_BUFFER_SIZE = 5760;
  private static final int NUM_BUFFERS = 16;
  private FfmpegDecoder decoder;
  private final boolean enableFloatOutput;
  
  public FfmpegAudioRenderer()
  {
    this(null, null, new AudioProcessor[0]);
  }
  
  public FfmpegAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioSink paramAudioSink, boolean paramBoolean)
  {
    super(paramHandler, paramAudioRendererEventListener, null, false, paramAudioSink);
    this.enableFloatOutput = paramBoolean;
  }
  
  public FfmpegAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioProcessor... paramVarArgs)
  {
    this(paramHandler, paramAudioRendererEventListener, new DefaultAudioSink(null, paramVarArgs), false);
  }
  
  private boolean isOutputSupported(Format paramFormat)
  {
    if ((shouldUseFloatOutput(paramFormat)) || (supportsOutputEncoding(2))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean shouldUseFloatOutput(Format paramFormat)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (this.enableFloatOutput)
    {
      if (supportsOutputEncoding(4)) {
        break label23;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      label21:
      return bool2;
      label23:
      String str = paramFormat.sampleMimeType;
      int i = -1;
      switch (str.hashCode())
      {
      }
      for (;;)
      {
        bool2 = bool1;
        switch (i)
        {
        case 1: 
        default: 
          bool2 = true;
          break label21;
          if (str.equals("audio/raw"))
          {
            i = 0;
            continue;
            if (str.equals("audio/ac3")) {
              i = 1;
            }
          }
          break;
        }
      }
      if ((paramFormat.pcmEncoding != Integer.MIN_VALUE) && (paramFormat.pcmEncoding != NUM))
      {
        bool2 = bool1;
        if (paramFormat.pcmEncoding != 4) {}
      }
      else
      {
        bool2 = true;
      }
    }
  }
  
  protected FfmpegDecoder createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto)
    throws FfmpegDecoderException
  {
    this.decoder = new FfmpegDecoder(16, 16, 5760, paramFormat.sampleMimeType, paramFormat.initializationData, shouldUseFloatOutput(paramFormat));
    return this.decoder;
  }
  
  public Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), this.decoder.getEncoding(), null, null, 0, null);
  }
  
  protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, Format paramFormat)
  {
    String str = paramFormat.sampleMimeType;
    int i;
    if (!MimeTypes.isAudio(str)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if ((!FfmpegLibrary.supportsFormat(str)) || (!isOutputSupported(paramFormat))) {
        i = 1;
      } else if (!supportsFormatDrm(paramDrmSessionManager, paramFormat.drmInitData)) {
        i = 2;
      } else {
        i = 4;
      }
    }
  }
  
  public final int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException
  {
    return 8;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/ffmpeg/FfmpegAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */