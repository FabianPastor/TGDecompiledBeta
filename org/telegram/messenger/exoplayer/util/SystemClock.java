package org.telegram.messenger.exoplayer.util;

public final class SystemClock
  implements Clock
{
  public long elapsedRealtime()
  {
    return android.os.SystemClock.elapsedRealtime();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/SystemClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */