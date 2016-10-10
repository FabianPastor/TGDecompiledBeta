package org.telegram.messenger.exoplayer.util;

import android.os.HandlerThread;
import android.os.Process;

public final class PriorityHandlerThread
  extends HandlerThread
{
  private final int priority;
  
  public PriorityHandlerThread(String paramString, int paramInt)
  {
    super(paramString);
    this.priority = paramInt;
  }
  
  public void run()
  {
    Process.setThreadPriority(this.priority);
    super.run();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/PriorityHandlerThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */