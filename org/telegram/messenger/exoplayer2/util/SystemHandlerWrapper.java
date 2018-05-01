package org.telegram.messenger.exoplayer2.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class SystemHandlerWrapper
  implements HandlerWrapper
{
  private final Handler handler;
  
  public SystemHandlerWrapper(Handler paramHandler)
  {
    this.handler = paramHandler;
  }
  
  public Looper getLooper()
  {
    return this.handler.getLooper();
  }
  
  public Message obtainMessage(int paramInt)
  {
    return this.handler.obtainMessage(paramInt);
  }
  
  public Message obtainMessage(int paramInt1, int paramInt2, int paramInt3)
  {
    return this.handler.obtainMessage(paramInt1, paramInt2, paramInt3);
  }
  
  public Message obtainMessage(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    return this.handler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject);
  }
  
  public Message obtainMessage(int paramInt, Object paramObject)
  {
    return this.handler.obtainMessage(paramInt, paramObject);
  }
  
  public boolean post(Runnable paramRunnable)
  {
    return this.handler.post(paramRunnable);
  }
  
  public boolean postDelayed(Runnable paramRunnable, long paramLong)
  {
    return this.handler.postDelayed(paramRunnable, paramLong);
  }
  
  public void removeCallbacksAndMessages(Object paramObject)
  {
    this.handler.removeCallbacksAndMessages(paramObject);
  }
  
  public void removeMessages(int paramInt)
  {
    this.handler.removeMessages(paramInt);
  }
  
  public boolean sendEmptyMessage(int paramInt)
  {
    return this.handler.sendEmptyMessage(paramInt);
  }
  
  public boolean sendEmptyMessageAtTime(int paramInt, long paramLong)
  {
    return this.handler.sendEmptyMessageAtTime(paramInt, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/SystemHandlerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */