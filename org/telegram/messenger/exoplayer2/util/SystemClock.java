package org.telegram.messenger.exoplayer2.util;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;

final class SystemClock
  implements Clock
{
  public HandlerWrapper createHandler(Looper paramLooper, Handler.Callback paramCallback)
  {
    return new SystemHandlerWrapper(new Handler(paramLooper, paramCallback));
  }
  
  public long elapsedRealtime()
  {
    return android.os.SystemClock.elapsedRealtime();
  }
  
  public void sleep(long paramLong)
  {
    android.os.SystemClock.sleep(paramLong);
  }
  
  public long uptimeMillis()
  {
    return android.os.SystemClock.uptimeMillis();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/SystemClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */