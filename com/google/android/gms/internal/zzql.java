package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzz;

public final class zzql<O extends Api.ApiOptions>
{
  private final Api<O> vS;
  private final O xw;
  private final boolean yo;
  private final int yp;
  
  private zzql(Api<O> paramApi)
  {
    this.yo = true;
    this.vS = paramApi;
    this.xw = null;
    this.yp = System.identityHashCode(this);
  }
  
  private zzql(Api<O> paramApi, O paramO)
  {
    this.yo = false;
    this.vS = paramApi;
    this.xw = paramO;
    this.yp = zzz.hashCode(new Object[] { this.vS, this.xw });
  }
  
  public static <O extends Api.ApiOptions> zzql<O> zza(Api<O> paramApi, O paramO)
  {
    return new zzql(paramApi, paramO);
  }
  
  public static <O extends Api.ApiOptions> zzql<O> zzb(Api<O> paramApi)
  {
    return new zzql(paramApi);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzql)) {
        return false;
      }
      paramObject = (zzql)paramObject;
    } while ((!this.yo) && (!((zzql)paramObject).yo) && (zzz.equal(this.vS, ((zzql)paramObject).vS)) && (zzz.equal(this.xw, ((zzql)paramObject).xw)));
    return false;
  }
  
  public int hashCode()
  {
    return this.yp;
  }
  
  public String zzarl()
  {
    return this.vS.getName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzql.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */