package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class zzapv<T>
  extends zzaot<T>
{
  private final zzaot<T> bkU;
  private final zzaob bmQ;
  private final Type bmR;
  
  zzapv(zzaob paramzzaob, zzaot<T> paramzzaot, Type paramType)
  {
    this.bmQ = paramzzaob;
    this.bkU = paramzzaot;
    this.bmR = paramType;
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
  
  public void zza(zzaqa paramzzaqa, T paramT)
    throws IOException
  {
    zzaot localzzaot = this.bkU;
    Type localType = zzb(this.bmR, paramT);
    if (localType != this.bmR)
    {
      localzzaot = this.bmQ.zza(zzapx.zzl(localType));
      if ((localzzaot instanceof zzaps.zza)) {
        break label52;
      }
    }
    for (;;)
    {
      localzzaot.zza(paramzzaqa, paramT);
      return;
      label52:
      if (!(this.bkU instanceof zzaps.zza)) {
        localzzaot = this.bkU;
      }
    }
  }
  
  public T zzb(zzapy paramzzapy)
    throws IOException
  {
    return (T)this.bkU.zzb(paramzzapy);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */