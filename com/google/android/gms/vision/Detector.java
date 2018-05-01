package com.google.android.gms.vision;

import javax.annotation.concurrent.GuardedBy;

public abstract class Detector<T>
{
  private final Object zzac = new Object();
  @GuardedBy("mProcessorLock")
  private Processor<T> zzad;
  
  public boolean isOperational()
  {
    return true;
  }
  
  public void release()
  {
    synchronized (this.zzac)
    {
      if (this.zzad != null)
      {
        this.zzad.release();
        this.zzad = null;
      }
      return;
    }
  }
  
  public static abstract interface Processor<T>
  {
    public abstract void release();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/Detector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */