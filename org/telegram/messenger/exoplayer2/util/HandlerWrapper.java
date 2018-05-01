package org.telegram.messenger.exoplayer2.util;

import android.os.Looper;
import android.os.Message;

public abstract interface HandlerWrapper
{
  public abstract Looper getLooper();
  
  public abstract Message obtainMessage(int paramInt);
  
  public abstract Message obtainMessage(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract Message obtainMessage(int paramInt1, int paramInt2, int paramInt3, Object paramObject);
  
  public abstract Message obtainMessage(int paramInt, Object paramObject);
  
  public abstract boolean post(Runnable paramRunnable);
  
  public abstract boolean postDelayed(Runnable paramRunnable, long paramLong);
  
  public abstract void removeCallbacksAndMessages(Object paramObject);
  
  public abstract void removeMessages(int paramInt);
  
  public abstract boolean sendEmptyMessage(int paramInt);
  
  public abstract boolean sendEmptyMessageAtTime(int paramInt, long paramLong);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/HandlerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */