package com.google.android.gms.vision;

import android.util.SparseArray;

public abstract class Detector<T>
{
  private Object aKq = new Object();
  private Processor<T> aKr;
  
  public abstract SparseArray<T> detect(Frame paramFrame);
  
  public boolean isOperational()
  {
    return true;
  }
  
  public void receiveFrame(Frame paramFrame)
  {
    synchronized (this.aKq)
    {
      if (this.aKr == null) {
        throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
      }
    }
    Frame.Metadata localMetadata = new Frame.Metadata(paramFrame.getMetadata());
    localMetadata.zzclp();
    paramFrame = new Detections(detect(paramFrame), localMetadata, isOperational());
    this.aKr.receiveDetections(paramFrame);
  }
  
  public void release()
  {
    synchronized (this.aKq)
    {
      if (this.aKr != null)
      {
        this.aKr.release();
        this.aKr = null;
      }
      return;
    }
  }
  
  public boolean setFocus(int paramInt)
  {
    return true;
  }
  
  public void setProcessor(Processor<T> paramProcessor)
  {
    this.aKr = paramProcessor;
  }
  
  public static class Detections<T>
  {
    private SparseArray<T> aKs;
    private Frame.Metadata aKt;
    private boolean aKu;
    
    public Detections(SparseArray<T> paramSparseArray, Frame.Metadata paramMetadata, boolean paramBoolean)
    {
      this.aKs = paramSparseArray;
      this.aKt = paramMetadata;
      this.aKu = paramBoolean;
    }
    
    public boolean detectorIsOperational()
    {
      return this.aKu;
    }
    
    public SparseArray<T> getDetectedItems()
    {
      return this.aKs;
    }
    
    public Frame.Metadata getFrameMetadata()
    {
      return this.aKt;
    }
  }
  
  public static abstract interface Processor<T>
  {
    public abstract void receiveDetections(Detector.Detections<T> paramDetections);
    
    public abstract void release();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/Detector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */