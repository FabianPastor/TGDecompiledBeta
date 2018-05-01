package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import java.util.Arrays;

public final class aae
{
  private String zzakv;
  
  public aae(@Nullable String paramString)
  {
    this.zzakv = paramString;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof aae)) {
      return false;
    }
    paramObject = (aae)paramObject;
    return zzbe.equal(this.zzakv, ((aae)paramObject).zzakv);
  }
  
  @Nullable
  public final String getToken()
  {
    return this.zzakv;
  }
  
  public final int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.zzakv });
  }
  
  public final String toString()
  {
    return zzbe.zzt(this).zzg("token", this.zzakv).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */