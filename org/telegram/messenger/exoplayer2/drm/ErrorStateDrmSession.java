package org.telegram.messenger.exoplayer2.drm;

import java.util.Map;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class ErrorStateDrmSession<T extends ExoMediaCrypto>
  implements DrmSession<T>
{
  private final DrmSession.DrmSessionException error;
  
  public ErrorStateDrmSession(DrmSession.DrmSessionException paramDrmSessionException)
  {
    this.error = ((DrmSession.DrmSessionException)Assertions.checkNotNull(paramDrmSessionException));
  }
  
  public DrmSession.DrmSessionException getError()
  {
    return this.error;
  }
  
  public T getMediaCrypto()
  {
    return null;
  }
  
  public byte[] getOfflineLicenseKeySetId()
  {
    return null;
  }
  
  public int getState()
  {
    return 1;
  }
  
  public Map<String, String> queryKeyStatus()
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/ErrorStateDrmSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */