package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.internal.zzz.zza;

public class zzant
{
  private String hN;
  
  public zzant(@Nullable String paramString)
  {
    this.hN = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzant)) {
      return false;
    }
    paramObject = (zzant)paramObject;
    return zzz.equal(this.hN, ((zzant)paramObject).hN);
  }
  
  @Nullable
  public String getToken()
  {
    return this.hN;
  }
  
  public int hashCode()
  {
    return zzz.hashCode(new Object[] { this.hN });
  }
  
  public String toString()
  {
    return zzz.zzx(this).zzg("token", this.hN).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */