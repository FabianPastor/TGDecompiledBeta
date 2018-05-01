package com.google.android.gms.internal;

public final class zzcfn
{
  private final int mPriority;
  private final boolean zzbqX;
  private final boolean zzbqY;
  
  zzcfn(zzcfl paramzzcfl, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPriority = paramInt;
    this.zzbqX = paramBoolean1;
    this.zzbqY = paramBoolean2;
  }
  
  public final void log(String paramString)
  {
    this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, paramString, null, null, null);
  }
  
  public final void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, paramString, paramObject1, paramObject2, paramObject3);
  }
  
  public final void zze(String paramString, Object paramObject1, Object paramObject2)
  {
    this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, paramString, paramObject1, paramObject2, null);
  }
  
  public final void zzj(String paramString, Object paramObject)
  {
    this.zzbqW.zza(this.mPriority, this.zzbqX, this.zzbqY, paramString, paramObject, null, null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */