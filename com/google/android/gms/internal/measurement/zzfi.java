package com.google.android.gms.internal.measurement;

public final class zzfi
{
  private final int priority;
  private final boolean zzajd;
  private final boolean zzaje;
  
  zzfi(zzfg paramzzfg, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.priority = paramInt;
    this.zzajd = paramBoolean1;
    this.zzaje = paramBoolean2;
  }
  
  public final void log(String paramString)
  {
    this.zzajc.zza(this.priority, this.zzajd, this.zzaje, paramString, null, null, null);
  }
  
  public final void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    this.zzajc.zza(this.priority, this.zzajd, this.zzaje, paramString, paramObject1, paramObject2, paramObject3);
  }
  
  public final void zze(String paramString, Object paramObject1, Object paramObject2)
  {
    this.zzajc.zza(this.priority, this.zzajd, this.zzaje, paramString, paramObject1, paramObject2, null);
  }
  
  public final void zzg(String paramString, Object paramObject)
  {
    this.zzajc.zza(this.priority, this.zzajd, this.zzaje, paramString, paramObject, null, null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */