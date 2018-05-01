package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzaa.zza;

public class zzbtj
{
  private String zzaiJ;
  
  public zzbtj(@Nullable String paramString)
  {
    this.zzaiJ = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzbtj)) {
      return false;
    }
    paramObject = (zzbtj)paramObject;
    return zzaa.equal(this.zzaiJ, ((zzbtj)paramObject).zzaiJ);
  }
  
  @Nullable
  public String getToken()
  {
    return this.zzaiJ;
  }
  
  public int hashCode()
  {
    return zzaa.hashCode(new Object[] { this.zzaiJ });
  }
  
  public String toString()
  {
    return zzaa.zzv(this).zzg("token", this.zzaiJ).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbtj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */