package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class zzaqm<T>
  extends zzapk<T>
{
  private final zzapk<T> bol;
  private final zzaos bqh;
  private final Type bqi;
  
  zzaqm(zzaos paramzzaos, zzapk<T> paramzzapk, Type paramType)
  {
    this.bqh = paramzzaos;
    this.bol = paramzzapk;
    this.bqi = paramType;
  }
  
  private Type zzb(Type paramType, Object paramObject)
  {
    Object localObject = paramType;
    if (paramObject != null) {
      if ((paramType != Object.class) && (!(paramType instanceof TypeVariable)))
      {
        localObject = paramType;
        if (!(paramType instanceof Class)) {}
      }
      else
      {
        localObject = paramObject.getClass();
      }
    }
    return (Type)localObject;
  }
  
  public void zza(zzaqr paramzzaqr, T paramT)
    throws IOException
  {
    zzapk localzzapk = this.bol;
    Type localType = zzb(this.bqi, paramT);
    if (localType != this.bqi)
    {
      localzzapk = this.bqh.zza(zzaqo.zzl(localType));
      if ((localzzapk instanceof zzaqj.zza)) {
        break label52;
      }
    }
    for (;;)
    {
      localzzapk.zza(paramzzaqr, paramT);
      return;
      label52:
      if (!(this.bol instanceof zzaqj.zza)) {
        localzzapk = this.bol;
      }
    }
  }
  
  public T zzb(zzaqp paramzzaqp)
    throws IOException
  {
    return (T)this.bol.zzb(paramzzaqp);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */