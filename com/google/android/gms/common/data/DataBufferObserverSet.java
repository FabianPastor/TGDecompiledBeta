package com.google.android.gms.common.data;

import java.util.HashSet;
import java.util.Iterator;

public final class DataBufferObserverSet
  implements DataBufferObserver, DataBufferObserver.Observable
{
  private HashSet<DataBufferObserver> zJ = new HashSet();
  
  public void addObserver(DataBufferObserver paramDataBufferObserver)
  {
    this.zJ.add(paramDataBufferObserver);
  }
  
  public void clear()
  {
    this.zJ.clear();
  }
  
  public boolean hasObservers()
  {
    return !this.zJ.isEmpty();
  }
  
  public void onDataChanged()
  {
    Iterator localIterator = this.zJ.iterator();
    while (localIterator.hasNext()) {
      ((DataBufferObserver)localIterator.next()).onDataChanged();
    }
  }
  
  public void onDataRangeChanged(int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.zJ.iterator();
    while (localIterator.hasNext()) {
      ((DataBufferObserver)localIterator.next()).onDataRangeChanged(paramInt1, paramInt2);
    }
  }
  
  public void onDataRangeInserted(int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.zJ.iterator();
    while (localIterator.hasNext()) {
      ((DataBufferObserver)localIterator.next()).onDataRangeInserted(paramInt1, paramInt2);
    }
  }
  
  public void onDataRangeMoved(int paramInt1, int paramInt2, int paramInt3)
  {
    Iterator localIterator = this.zJ.iterator();
    while (localIterator.hasNext()) {
      ((DataBufferObserver)localIterator.next()).onDataRangeMoved(paramInt1, paramInt2, paramInt3);
    }
  }
  
  public void onDataRangeRemoved(int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.zJ.iterator();
    while (localIterator.hasNext()) {
      ((DataBufferObserver)localIterator.next()).onDataRangeRemoved(paramInt1, paramInt2);
    }
  }
  
  public void removeObserver(DataBufferObserver paramDataBufferObserver)
  {
    this.zJ.remove(paramDataBufferObserver);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataBufferObserverSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */