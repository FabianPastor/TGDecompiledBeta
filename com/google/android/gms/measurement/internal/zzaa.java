package com.google.android.gms.measurement.internal;

abstract class zzaa
  extends zzz
{
  private boolean cR;
  
  zzaa(zzx paramzzx)
  {
    super(paramzzx);
    this.aqw.zzb(this);
  }
  
  public final void initialize()
  {
    if (this.cR) {
      throw new IllegalStateException("Can't initialize twice");
    }
    zzzy();
    this.aqw.zzbyi();
    this.cR = true;
  }
  
  boolean isInitialized()
  {
    return this.cR;
  }
  
  protected void zzacj()
  {
    if (!isInitialized()) {
      throw new IllegalStateException("Not initialized");
    }
  }
  
  boolean zzbyn()
  {
    return false;
  }
  
  protected abstract void zzzy();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */