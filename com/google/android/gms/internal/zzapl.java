package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

public final class zzapl
  implements zzaou
{
  private final zzapb bkM;
  
  public zzapl(zzapb paramzzapb)
  {
    this.bkM = paramzzapb;
  }
  
  public <T> zzaot<T> zza(zzaob paramzzaob, zzapx<T> paramzzapx)
  {
    Type localType = paramzzapx.bz();
    Class localClass = paramzzapx.by();
    if (!Collection.class.isAssignableFrom(localClass)) {
      return null;
    }
    localType = zzapa.zza(localType, localClass);
    return new zza(paramzzaob, localType, paramzzaob.zza(zzapx.zzl(localType)), this.bkM.zzb(paramzzapx));
  }
  
  private static final class zza<E>
    extends zzaot<Collection<E>>
  {
    private final zzaot<E> bms;
    private final zzapg<? extends Collection<E>> bmt;
    
    public zza(zzaob paramzzaob, Type paramType, zzaot<E> paramzzaot, zzapg<? extends Collection<E>> paramzzapg)
    {
      this.bms = new zzapv(paramzzaob, paramzzaot, paramType);
      this.bmt = paramzzapg;
    }
    
    public void zza(zzaqa paramzzaqa, Collection<E> paramCollection)
      throws IOException
    {
      if (paramCollection == null)
      {
        paramzzaqa.bx();
        return;
      }
      paramzzaqa.bt();
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        Object localObject = paramCollection.next();
        this.bms.zza(paramzzaqa, localObject);
      }
      paramzzaqa.bu();
    }
    
    public Collection<E> zzj(zzapy paramzzapy)
      throws IOException
    {
      if (paramzzapy.bn() == zzapz.bos)
      {
        paramzzapy.nextNull();
        return null;
      }
      Collection localCollection = (Collection)this.bmt.bg();
      paramzzapy.beginArray();
      while (paramzzapy.hasNext()) {
        localCollection.add(this.bms.zzb(paramzzapy));
      }
      paramzzapy.endArray();
      return localCollection;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */