package com.google.android.gms.vision.barcode.internal.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zztl;
import com.google.android.gms.internal.zztl.zza;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zza;
import java.nio.ByteBuffer;

public class zzb
  extends zza<zzc>
{
  private final BarcodeDetectorOptions aNY;
  
  public zzb(Context paramContext, BarcodeDetectorOptions paramBarcodeDetectorOptions)
  {
    super(paramContext, "BarcodeNativeHandle");
    this.aNY = paramBarcodeDetectorOptions;
    zzcls();
  }
  
  protected zzc zza(zztl paramzztl, Context paramContext)
    throws RemoteException, zztl.zza
  {
    return zzd.zza.zzlf(paramzztl.zzjd("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zze.zzac(paramContext), this.aNY);
  }
  
  public Barcode[] zza(Bitmap paramBitmap, FrameMetadataParcel paramFrameMetadataParcel)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramBitmap = zze.zzac(paramBitmap);
      paramBitmap = ((zzc)zzcls()).zzb(paramBitmap, paramFrameMetadataParcel);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramBitmap);
    }
    return new Barcode[0];
  }
  
  public Barcode[] zza(ByteBuffer paramByteBuffer, FrameMetadataParcel paramFrameMetadataParcel)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramByteBuffer = zze.zzac(paramByteBuffer);
      paramByteBuffer = ((zzc)zzcls()).zza(paramByteBuffer, paramFrameMetadataParcel);
      return paramByteBuffer;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramByteBuffer);
    }
    return new Barcode[0];
  }
  
  protected void zzclp()
    throws RemoteException
  {
    ((zzc)zzcls()).zzclq();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/internal/client/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */