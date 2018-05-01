package com.google.android.gms.common.internal;

public class GmsServiceEndpoint
{
  private final String mPackageName;
  private final int zztq;
  private final String zzue;
  private final boolean zzuf;
  
  public GmsServiceEndpoint(String paramString1, String paramString2, boolean paramBoolean, int paramInt)
  {
    this.mPackageName = paramString1;
    this.zzue = paramString2;
    this.zzuf = paramBoolean;
    this.zztq = paramInt;
  }
  
  final int getBindFlags()
  {
    return this.zztq;
  }
  
  final String getPackageName()
  {
    return this.mPackageName;
  }
  
  final String zzcw()
  {
    return this.zzue;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GmsServiceEndpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */