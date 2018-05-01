package com.google.android.gms.internal.measurement;

abstract class zzhk
  extends zzhj
{
  private boolean zzvj;
  
  zzhk(zzgl paramzzgl)
  {
    super(paramzzgl);
    this.zzacr.zzb(this);
  }
  
  final boolean isInitialized()
  {
    if (this.zzvj) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected final void zzch()
  {
    if (!isInitialized()) {
      throw new IllegalStateException("Not initialized");
    }
  }
  
  protected abstract boolean zzhh();
  
  protected void zzig() {}
  
  public final void zzkd()
  {
    if (this.zzvj) {
      throw new IllegalStateException("Can't initialize twice");
    }
    zzig();
    this.zzacr.zzjz();
    this.zzvj = true;
  }
  
  public final void zzm()
  {
    if (this.zzvj) {
      throw new IllegalStateException("Can't initialize twice");
    }
    if (!zzhh())
    {
      this.zzacr.zzjz();
      this.zzvj = true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzhk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */