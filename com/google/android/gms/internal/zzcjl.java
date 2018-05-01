package com.google.android.gms.internal;

abstract class zzcjl
  extends zzcjk
{
  private boolean zzdtb;
  
  zzcjl(zzcim paramzzcim)
  {
    super(paramzzcim);
    this.zziwf.zzb(this);
  }
  
  public final void initialize()
  {
    if (this.zzdtb) {
      throw new IllegalStateException("Can't initialize twice");
    }
    if (!zzaxz())
    {
      this.zziwf.zzbak();
      this.zzdtb = true;
    }
  }
  
  final boolean isInitialized()
  {
    return this.zzdtb;
  }
  
  protected abstract boolean zzaxz();
  
  protected void zzayy() {}
  
  public final void zzazw()
  {
    if (this.zzdtb) {
      throw new IllegalStateException("Can't initialize twice");
    }
    zzayy();
    this.zziwf.zzbak();
    this.zzdtb = true;
  }
  
  protected final void zzxf()
  {
    if (!isInitialized()) {
      throw new IllegalStateException("Not initialized");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcjl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */