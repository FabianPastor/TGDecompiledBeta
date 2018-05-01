package org.telegram.messenger.exoplayer2.util;

import android.os.Handler.Callback;
import android.os.Looper;

public abstract interface Clock
{
  public static final Clock DEFAULT = new SystemClock();
  
  public abstract HandlerWrapper createHandler(Looper paramLooper, Handler.Callback paramCallback);
  
  public abstract long elapsedRealtime();
  
  public abstract void sleep(long paramLong);
  
  public abstract long uptimeMillis();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/Clock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */