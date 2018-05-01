package org.telegram.messenger.exoplayer.drm;

import android.annotation.TargetApi;

@TargetApi(16)
public abstract interface DrmSessionManager<T extends ExoMediaCrypto>
{
  public static final int STATE_CLOSED = 1;
  public static final int STATE_ERROR = 0;
  public static final int STATE_OPENED = 3;
  public static final int STATE_OPENED_WITH_KEYS = 4;
  public static final int STATE_OPENING = 2;
  
  public abstract void close();
  
  public abstract Exception getError();
  
  public abstract T getMediaCrypto();
  
  public abstract int getState();
  
  public abstract void open(DrmInitData paramDrmInitData);
  
  public abstract boolean requiresSecureDecoderComponent(String paramString);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/drm/DrmSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */