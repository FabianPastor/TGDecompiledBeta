package com.google.android.gms.vision.barcode;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.internal.eu;
import com.google.android.gms.internal.ew;
import com.google.android.gms.internal.fc;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

public final class BarcodeDetector
  extends Detector<Barcode>
{
  private final ew zzbNg;
  
  private BarcodeDetector()
  {
    throw new IllegalStateException("Default constructor called");
  }
  
  private BarcodeDetector(ew paramew)
  {
    this.zzbNg = paramew;
  }
  
  public final SparseArray<Barcode> detect(Frame paramFrame)
  {
    if (paramFrame == null) {
      throw new IllegalArgumentException("No frame supplied.");
    }
    Object localObject1 = fc.zzc(paramFrame);
    if (paramFrame.getBitmap() != null)
    {
      localObject1 = this.zzbNg.zza(paramFrame.getBitmap(), (fc)localObject1);
      paramFrame = (Frame)localObject1;
      if (localObject1 == null) {
        throw new IllegalArgumentException("Internal barcode detector error; check logcat output.");
      }
    }
    else
    {
      paramFrame = paramFrame.getGrayscaleImageData();
      paramFrame = this.zzbNg.zza(paramFrame, (fc)localObject1);
    }
    localObject1 = new SparseArray(paramFrame.length);
    int j = paramFrame.length;
    int i = 0;
    while (i < j)
    {
      Object localObject2 = paramFrame[i];
      ((SparseArray)localObject1).append(((Barcode)localObject2).rawValue.hashCode(), localObject2);
      i += 1;
    }
    return (SparseArray<Barcode>)localObject1;
  }
  
  public final boolean isOperational()
  {
    return this.zzbNg.isOperational();
  }
  
  public final void release()
  {
    super.release();
    this.zzbNg.zzDQ();
  }
  
  public static class Builder
  {
    private Context mContext;
    private eu zzbNh;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
      this.zzbNh = new eu();
    }
    
    public BarcodeDetector build()
    {
      return new BarcodeDetector(new ew(this.mContext, this.zzbNh), null);
    }
    
    public Builder setBarcodeFormats(int paramInt)
    {
      this.zzbNh.zzbNi = paramInt;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/BarcodeDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */