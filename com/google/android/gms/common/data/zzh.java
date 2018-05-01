package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

public final class zzh<T>
  extends zzb<T>
{
  private T zzaFQ;
  
  public zzh(DataBuffer<T> paramDataBuffer)
  {
    super(paramDataBuffer);
  }
  
  public final T next()
  {
    if (!hasNext())
    {
      int i = this.zzaFv;
      throw new NoSuchElementException(46 + "Cannot advance the iterator beyond " + i);
    }
    this.zzaFv += 1;
    if (this.zzaFv == 0)
    {
      this.zzaFQ = this.zzaFu.get(0);
      if (!(this.zzaFQ instanceof zzc))
      {
        String str = String.valueOf(this.zzaFQ.getClass());
        throw new IllegalStateException(String.valueOf(str).length() + 44 + "DataBuffer reference of type " + str + " is not movable");
      }
    }
    else
    {
      ((zzc)this.zzaFQ).zzar(this.zzaFv);
    }
    return (T)this.zzaFQ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */