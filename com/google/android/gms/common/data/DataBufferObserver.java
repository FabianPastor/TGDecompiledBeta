package com.google.android.gms.common.data;

public abstract interface DataBufferObserver
{
  public abstract void onDataChanged();
  
  public abstract void onDataRangeChanged(int paramInt1, int paramInt2);
  
  public abstract void onDataRangeInserted(int paramInt1, int paramInt2);
  
  public abstract void onDataRangeMoved(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void onDataRangeRemoved(int paramInt1, int paramInt2);
  
  public static abstract interface Observable
  {
    public abstract void addObserver(DataBufferObserver paramDataBufferObserver);
    
    public abstract void removeObserver(DataBufferObserver paramDataBufferObserver);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataBufferObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */