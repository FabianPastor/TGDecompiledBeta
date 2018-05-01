package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzbq;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class zzb<T>
  implements Iterator<T>
{
  protected final DataBuffer<T> zzfvu;
  protected int zzfvv;
  
  public zzb(DataBuffer<T> paramDataBuffer)
  {
    this.zzfvu = ((DataBuffer)zzbq.checkNotNull(paramDataBuffer));
    this.zzfvv = -1;
  }
  
  public boolean hasNext()
  {
    return this.zzfvv < this.zzfvu.getCount() - 1;
  }
  
  public T next()
  {
    if (!hasNext())
    {
      i = this.zzfvv;
      throw new NoSuchElementException(46 + "Cannot advance the iterator beyond " + i);
    }
    DataBuffer localDataBuffer = this.zzfvu;
    int i = this.zzfvv + 1;
    this.zzfvv = i;
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