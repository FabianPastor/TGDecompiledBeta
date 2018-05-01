package com.google.android.gms.gcm;

import android.os.Bundle;

public final class zzi
{
  public static final zzi zzbgb = new zzi(0, 30, 3600);
  private static zzi zzbgc = new zzi(1, 30, 3600);
  private final int zzbgd;
  private final int zzbge;
  private final int zzbgf;
  
  private zzi(int paramInt1, int paramInt2, int paramInt3)
  {
    this.zzbgd = paramInt1;
    this.zzbge = 30;
    this.zzbgf = 3600;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzi)) {
        return false;
      }
      paramObject = (zzi)paramObject;
    } while ((((zzi)paramObject).zzbgd == this.zzbgd) && (((zzi)paramObject).zzbge == this.zzbge) && (((zzi)paramObject).zzbgf == this.zzbgf));
    return false;
  }
  
  public final int hashCode()
  {
    return ((this.zzbgd + 1 ^ 0xF4243) * 1000003 ^ this.zzbge) * 1000003 ^ this.zzbgf;
  }
  
  public final String toString()
  {
    int i = this.zzbgd;
    int j = this.zzbge;
    int k = this.zzbgf;
    return 74 + "policy=" + i + " initial_backoff=" + j + " maximum_backoff=" + k;
  }
  
  public final int zzvE()
  {
    return this.zzbgd;
  }
  
  public final int zzvF()
  {
    return this.zzbge;
  }
  
  public final int zzvG()
  {
    return this.zzbgf;
  }
  
  public final Bundle zzx(Bundle paramBundle)
  {
    paramBundle.putInt("retry_policy", this.zzbgd);
    paramBundle.putInt("initial_backoff_seconds", this.zzbge);
    paramBundle.putInt("maximum_backoff_seconds", this.zzbgf);
    return paramBundle;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */