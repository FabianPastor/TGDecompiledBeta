package com.google.android.gms.vision.barcode;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.internal.client.BarcodeDetectorOptions;
import com.google.android.gms.vision.barcode.internal.client.zzb;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;

public final class BarcodeDetector
  extends Detector<Barcode>
{
  private final zzb aKM;
  
  private BarcodeDetector()
  {
    throw new IllegalStateException("Default constructor called");
  }
  
  private BarcodeDetector(zzb paramzzb)
  {
    this.aKM = paramzzb;
  }
  
  public SparseArray<Barcode> detect(Frame paramFrame)
  {
    if (paramFrame == null) {
      throw new IllegalArgumentException("No frame supplied.");
    }
    Object localObject1 = FrameMetadataParcel.zzc(paramFrame);
    if (paramFrame.getBitmap() != null)
    {
      localObject1 = this.aKM.zza(paramFrame.getBitmap(), (FrameMetadataParcel)localObject1);
      paramFrame = (Frame)localObject1;
      if (localObject1 == null) {
        throw new IllegalArgumentException("Internal barcode detector error; check logcat output.");
      }
    }
    else
    {
      paramFrame = paramFrame.getGrayscaleImageData();
      paramFrame = this.aKM.zza(paramFrame, (FrameMetadataParcel)localObject1);
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
  
  public boolean isOperational()
  {
    return this.aKM.isOperational();
  }
  
  public void release()
  {
    super.release();
    this.aKM.zzcls();
  }
  
  public static class Builder
  {
    private BarcodeDetectorOptions aKN;
    private Context mContext;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
      this.aKN = new BarcodeDetectorOptions();
    }
    
    public BarcodeDetector build()
    {
      return new BarcodeDetector(new zzb(this.mContext, this.aKN), null);
    }
    
    public Builder setBarcodeFormats(int paramInt)
    {
      this.aKN.aKO = paramInt;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/BarcodeDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */