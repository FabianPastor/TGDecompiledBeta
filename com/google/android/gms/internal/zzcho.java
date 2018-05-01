package com.google.android.gms.internal;

public final class zzcho
{
  private final int mPriority;
  private final boolean zzjcf;
  private final boolean zzjcg;
  
  zzcho(zzchm paramzzchm, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPriority = paramInt;
    this.zzjcf = paramBoolean1;
    this.zzjcg = paramBoolean2;
  }
  
  public final void log(String paramString)
  {
    this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, paramString, null, null, null);
  }
  
  public final void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, paramString, paramObject1, paramObject2, paramObject3);
  }
  
  public final void zze(String paramString, Object paramObject1, Object paramObject2)
  {
    this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, paramString, paramObject1, paramObject2, null);
  }
  
  public final void zzj(String paramString, Object paramObject)
  {
    this.zzjce.zza(this.mPriority, this.zzjcf, this.zzjcg, paramString, paramObject, null, null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcho.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */