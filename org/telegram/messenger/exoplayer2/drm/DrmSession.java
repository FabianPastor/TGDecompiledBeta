package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

@TargetApi(16)
public abstract interface DrmSession<T extends ExoMediaCrypto>
{
  public static final int STATE_ERROR = 1;
  public static final int STATE_OPENED = 3;
  public static final int STATE_OPENED_WITH_KEYS = 4;
  public static final int STATE_OPENING = 2;
  public static final int STATE_RELEASED = 0;
  
  public abstract DrmSessionException getError();
  
  public abstract T getMediaCrypto();
  
  public abstract byte[] getOfflineLicenseKeySetId();
  
  public abstract int getState();
  
  public abstract Map<String, String> queryKeyStatus();
  
  public static class DrmSessionException
    extends Exception
  {
    public DrmSessionException(Throwable paramThrowable)
    {
      super();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface State {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/DrmSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */