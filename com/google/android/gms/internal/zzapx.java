package com.google.android.gms.internal;

import java.lang.reflect.Type;

public class zzapx<T>
{
  final Type bmR;
  final Class<? super T> bnV;
  final int bnW;
  
  protected zzapx()
  {
    this.bmR = zzq(getClass());
    this.bnV = zzapa.zzf(this.bmR);
    this.bnW = this.bmR.hashCode();
  }
  
  zzapx(Type paramType)
  {
    this.bmR = zzapa.zze((Type)zzaoz.zzy(paramType));
    this.bnV = zzapa.zzf(this.bmR);
    this.bnW = this.bmR.hashCode();
  }
  
  public static zzapx<?> zzl(Type paramType)
  {
    return new zzapx(paramType);
  }
  
  static Type zzq(Class<?> paramClass)
  {
    paramClass = paramClass.getGenericSuperclass();
    if ((paramClass instanceof Class)) {
      throw new RuntimeException("Missing type parameter.");
    }
    return zzapa.zze(((java.lang.reflect.ParameterizedType)paramClass).getActualTypeArguments()[0]);
  }
  
  public static <T> zzapx<T> zzr(Class<T> paramClass)
  {
    return new zzapx(paramClass);
  }
  
  public final Class<? super T> by()
  {
    return this.bnV;
  }
  
  public final Type bz()
  {
    return this.bmR;
  }
  
  public final boolean equals(Object paramObject)
  {
    return ((paramObject instanceof zzapx)) && (zzapa.zza(this.bmR, ((zzapx)paramObject).bmR));
  }
  
  public final int hashCode()
  {
    return this.bnW;
  }
  
  public final String toString()
  {
    return zzapa.zzg(this.bmR);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */