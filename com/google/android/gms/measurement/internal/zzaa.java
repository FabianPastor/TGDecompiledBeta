package com.google.android.gms.measurement.internal;

abstract class zzaa
  extends zzz
{
  private boolean aJ;
  
  zzaa(zzx paramzzx)
  {
    super(paramzzx);
    this.anq.zzb(this);
  }
  
  public final void initialize()
  {
    if (this.aJ) {
      throw new IllegalStateException("Can't initialize twice");
    }
    zzym();
    this.anq.zzbxo();
    this.aJ = true;
  }
  
  boolean isInitialized()
  {
    return this.aJ;
  }
  
  protected void zzaax()
  {
    if (!isInitialized()) {
      throw new IllegalStateException("Not initialized");
    }
  }
  
  boolean zzbxt()
  {
    return false;
  }
  
  protected abstract void zzym();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */