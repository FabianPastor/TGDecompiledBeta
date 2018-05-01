package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class zzaqi
  extends zzapk<Object>
{
  public static final zzapl bpG = new zzapl()
  {
    public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
    {
      if (paramAnonymouszzaqo.bB() == Object.class) {
        return new zzaqi(paramAnonymouszzaos, null);
      }
      return null;
    }
  };
  private final zzaos boC;
  
  private zzaqi(zzaos paramzzaos)
  {
    this.boC = paramzzaos;
  }
  
  public void zza(zzaqr paramzzaqr, Object paramObject)
    throws IOException
  {
    if (paramObject == null)
    {
      paramzzaqr.bA();
      return;
    }
    zzapk localzzapk = this.boC.zzk(paramObject.getClass());
    if ((localzzapk instanceof zzaqi))
    {
      paramzzaqr.by();
      paramzzaqr.bz();
      return;
    }
    localzzapk.zza(paramzzaqr, paramObject);
  }
  
  public Object zzb(zzaqp paramzzaqp)
    throws IOException
  {
    Object localObject = paramzzaqp.bq();
    switch (2.bpW[localObject.ordinal()])
    {
    default: 
      throw new IllegalStateException();
    case 1: 
      localObject = new ArrayList();
      paramzzaqp.beginArray();
      while (paramzzaqp.hasNext()) {
        ((List)localObject).add(zzb(paramzzaqp));
      }
      paramzzaqp.endArray();
      return localObject;
    case 2: 
      localObject = new zzapw();
      paramzzaqp.beginObject();
      while (paramzzaqp.hasNext()) {
        ((Map)localObject).put(paramzzaqp.nextName(), zzb(paramzzaqp));
      }
      paramzzaqp.endObject();
      return localObject;
    case 3: 
      return paramzzaqp.nextString();
    case 4: 
      return Double.valueOf(paramzzaqp.nextDouble());
    case 5: 
      return Boolean.valueOf(paramzzaqp.nextBoolean());
    }
    paramzzaqp.nextNull();
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */