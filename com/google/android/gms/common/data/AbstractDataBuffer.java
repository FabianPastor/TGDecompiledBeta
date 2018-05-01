package com.google.android.gms.common.data;

import java.util.Iterator;

public abstract class AbstractDataBuffer<T>
  implements DataBuffer<T>
{
  protected final DataHolder mDataHolder;
  
  protected AbstractDataBuffer(DataHolder paramDataHolder)
  {
    this.mDataHolder = paramDataHolder;
  }
  
  public int getCount()
  {
    if (this.mDataHolder == null) {}
    for (int i = 0;; i = this.mDataHolder.getCount()) {
      return i;
    }
  }
  
  public Iterator<T> iterator()
  {
    return new DataBufferIterator(this);
  }
  
  public void release()
  {
    if (this.mDataHolder != null) {
      this.mDataHolder.close();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/AbstractDataBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */