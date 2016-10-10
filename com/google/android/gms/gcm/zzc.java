package com.google.android.gms.gcm;

import android.os.Bundle;

public class zzc
{
  public static final zzc aff = new zzc(0, 30, 3600);
  public static final zzc afg = new zzc(1, 30, 3600);
  private final int afh;
  private final int afi;
  private final int afj;
  
  private zzc(int paramInt1, int paramInt2, int paramInt3)
  {
    this.afh = paramInt1;
    this.afi = paramInt2;
    this.afj = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzc)) {
        return false;
      }
      paramObject = (zzc)paramObject;
    } while ((((zzc)paramObject).afh == this.afh) && (((zzc)paramObject).afi == this.afi) && (((zzc)paramObject).afj == this.afj));
    return false;
  }
  
  public int hashCode()
  {
    return ((this.afh + 1 ^ 0xF4243) * 1000003 ^ this.afi) * 1000003 ^ this.afj;
  }
  
  public String toString()
  {
    int i = this.afh;
    int j = this.afi;
    int k = this.afj;
    return 74 + "policy=" + i + " initial_backoff=" + j + " maximum_backoff=" + k;
  }
  
  public Bundle zzaj(Bundle paramBundle)
  {
    paramBundle.putInt("retry_policy", this.afh);
    paramBundle.putInt("initial_backoff_seconds", this.afi);
    paramBundle.putInt("maximum_backoff_seconds", this.afj);
    return paramBundle;
  }
  
  public int zzboc()
  {
    return this.afh;
  }
  
  public int zzbod()
  {
    return this.afi;
  }
  
  public int zzboe()
  {
    return this.afj;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */