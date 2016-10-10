package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class zzapr
  extends zzaot<Object>
{
  public static final zzaou bmp = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      if (paramAnonymouszzapx.by() == Object.class) {
        return new zzapr(paramAnonymouszzaob, null);
      }
      return null;
    }
  };
  private final zzaob bll;
  
  private zzapr(zzaob paramzzaob)
  {
    this.bll = paramzzaob;
  }
  
  public void zza(zzaqa paramzzaqa, Object paramObject)
    throws IOException
  {
    if (paramObject == null)
    {
      paramzzaqa.bx();
      return;
    }
    zzaot localzzaot = this.bll.zzk(paramObject.getClass());
    if ((localzzaot instanceof zzapr))
    {
      paramzzaqa.bv();
      paramzzaqa.bw();
      return;
    }
    localzzaot.zza(paramzzaqa, paramObject);
  }
  
  public Object zzb(zzapy paramzzapy)
    throws IOException
  {
    Object localObject = paramzzapy.bn();
    switch (2.bmF[localObject.ordinal()])
    {
    default: 
      throw new IllegalStateException();
    case 1: 
      localObject = new ArrayList();
      paramzzapy.beginArray();
      while (paramzzapy.hasNext()) {
        ((List)localObject).add(zzb(paramzzapy));
      }
      paramzzapy.endArray();
      return localObject;
    case 2: 
      localObject = new zzapf();
      paramzzapy.beginObject();
      while (paramzzapy.hasNext()) {
        ((Map)localObject).put(paramzzapy.nextName(), zzb(paramzzapy));
      }
      paramzzapy.endObject();
      return localObject;
    case 3: 
      return paramzzapy.nextString();
    case 4: 
      return Double.valueOf(paramzzapy.nextDouble());
    case 5: 
      return Boolean.valueOf(paramzzapy.nextBoolean());
    }
    paramzzapy.nextNull();
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */