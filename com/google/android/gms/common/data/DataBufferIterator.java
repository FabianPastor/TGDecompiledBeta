package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DataBufferIterator<T>
  implements Iterator<T>
{
  protected final DataBuffer<T> mDataBuffer;
  protected int mPosition;
  
  public DataBufferIterator(DataBuffer<T> paramDataBuffer)
  {
    this.mDataBuffer = ((DataBuffer)Preconditions.checkNotNull(paramDataBuffer));
    this.mPosition = -1;
  }
  
  public boolean hasNext()
  {
    if (this.mPosition < this.mDataBuffer.getCount() - 1) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public T next()
  {
    if (!hasNext())
    {
      i = this.mPosition;
      throw new NoSuchElementException(46 + "Cannot advance the iterator beyond " + i);
    }
    DataBuffer localDataBuffer = this.mDataBuffer;
    int i = this.mPosition + 1;
    this.mPosition = i;
    return (T)localDataBuffer.get(i);
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataBufferIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */