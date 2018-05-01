package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import org.telegram.messenger.exoplayer2.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto
  implements ExoMediaCrypto
{
  private final boolean forceAllowInsecureDecoderComponents;
  private final MediaCrypto mediaCrypto;
  
  public FrameworkMediaCrypto(MediaCrypto paramMediaCrypto)
  {
    this(paramMediaCrypto, false);
  }
  
  public FrameworkMediaCrypto(MediaCrypto paramMediaCrypto, boolean paramBoolean)
  {
    this.mediaCrypto = ((MediaCrypto)Assertions.checkNotNull(paramMediaCrypto));
    this.forceAllowInsecureDecoderComponents = paramBoolean;
  }
  
  public MediaCrypto getWrappedMediaCrypto()
  {
    return this.mediaCrypto;
  }
  
  public boolean requiresSecureDecoderComponent(String paramString)
  {
    if ((!this.forceAllowInsecureDecoderComponents) && (this.mediaCrypto.requiresSecureDecoderComponent(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/FrameworkMediaCrypto.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */