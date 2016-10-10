package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

public final class zzapk<E>
  extends zzaot<Object>
{
  public static final zzaou bmp = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      paramAnonymouszzapx = paramAnonymouszzapx.bz();
      if ((!(paramAnonymouszzapx instanceof GenericArrayType)) && ((!(paramAnonymouszzapx instanceof Class)) || (!((Class)paramAnonymouszzapx).isArray()))) {
        return null;
      }
      paramAnonymouszzapx = zzapa.zzh(paramAnonymouszzapx);
      return new zzapk(paramAnonymouszzaob, paramAnonymouszzaob.zza(zzapx.zzl(paramAnonymouszzapx)), zzapa.zzf(paramAnonymouszzapx));
    }
  };
  private final Class<E> bmq;
  private final zzaot<E> bmr;
  
  public zzapk(zzaob paramzzaob, zzaot<E> paramzzaot, Class<E> paramClass)
  {
    this.bmr = new zzapv(paramzzaob, paramzzaot, paramClass);
    this.bmq = paramClass;
  }
  
  public void zza(zzaqa paramzzaqa, Object paramObject)
    throws IOException
  {
    if (paramObject == null)
    {
      paramzzaqa.bx();
      return;
    }
    paramzzaqa.bt();
    int i = 0;
    int j = Array.getLength(paramObject);
    while (i < j)
    {
      Object localObject = Array.get(paramObject, i);
      this.bmr.zza(paramzzaqa, localObject);
      i += 1;
    }
    paramzzaqa.bu();
  }
  
  public Object zzb(zzapy paramzzapy)
    throws IOException
  {
    if (paramzzapy.bn() == zzapz.bos)
    {
      paramzzapy.nextNull();
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    paramzzapy.beginArray();
    while (paramzzapy.hasNext()) {
      localArrayList.add(this.bmr.zzb(paramzzapy));
    }
    paramzzapy.endArray();
    paramzzapy = Array.newInstance(this.bmq, localArrayList.size());
    int i = 0;
    while (i < localArrayList.size())
    {
      Array.set(paramzzapy, i, localArrayList.get(i));
      i += 1;
    }
    return paramzzapy;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */