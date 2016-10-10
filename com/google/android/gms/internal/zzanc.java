package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;

public class zzanc
{
  private String fG;
  
  public zzanc(@Nullable String paramString)
  {
    this.fG = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzanc)) {
      return false;
    }
    paramObject = (zzanc)paramObject;
    return zzab.equal(this.fG, ((zzanc)paramObject).fG);
  }
  
  @Nullable
  public String getToken()
  {
    return this.fG;
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { this.fG });
  }
  
  public String toString()
  {
    return zzab.zzx(this).zzg("token", this.fG).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzanc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */