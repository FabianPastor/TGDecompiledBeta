package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class zzb<T>
  implements Iterator<T>
{
  protected final DataBuffer<T> zH;
  protected int zI;
  
  public zzb(DataBuffer<T> paramDataBuffer)
  {
    this.zH = ((DataBuffer)zzac.zzy(paramDataBuffer));
    this.zI = -1;
  }
  
  public boolean hasNext()
  {
    return this.zI < this.zH.getCount() - 1;
  }
  
  public T next()
  {
    if (!hasNext())
    {
      i = this.zI;
      throw new NoSuchElementException(46 + "Cannot advance the iterator beyond " + i);
    }
    DataBuffer localDataBuffer = this.zH;
    int i = this.zI + 1;
    this.zI = i;
    return (T)localDataBuffer.get(i);
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */