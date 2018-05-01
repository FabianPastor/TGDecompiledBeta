package org.telegram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import org.telegram.messenger.exoplayer.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto
  implements ExoMediaCrypto
{
  private final MediaCrypto mediaCrypto;
  
  FrameworkMediaCrypto(MediaCrypto paramMediaCrypto)
  {
    this.mediaCrypto = ((MediaCrypto)Assertions.checkNotNull(paramMediaCrypto));
  }
  
  public MediaCrypto getWrappedMediaCrypto()
  {
    return this.mediaCrypto;
  }
  
  public boolean requiresSecureDecoderComponent(String paramString)
  {
    return this.mediaCrypto.requiresSecureDecoderComponent(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/drm/FrameworkMediaCrypto.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */