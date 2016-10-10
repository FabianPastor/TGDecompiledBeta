package com.google.android.gms.vision.barcode.internal.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.internal.zzsu.zza;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zza;
import java.nio.ByteBuffer;

public class zzb
  extends zza<zzc>
{
  private final BarcodeDetectorOptions aKN;
  
  public zzb(Context paramContext, BarcodeDetectorOptions paramBarcodeDetectorOptions)
  {
    super(paramContext, "BarcodeNativeHandle");
    this.aKN = paramBarcodeDetectorOptions;
    zzclt();
  }
  
  protected zzc zza(zzsu paramzzsu, Context paramContext)
    throws RemoteException, zzsu.zza
  {
    return zzd.zza.zzlk(paramzzsu.zzjd("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zze.zzac(paramContext), this.aKN);
  }
  
  public Barcode[] zza(Bitmap paramBitmap, FrameMetadataParcel paramFrameMetadataParcel)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramBitmap = zze.zzac(paramBitmap);
      paramBitmap = ((zzc)zzclt()).zzb(paramBitmap, paramFrameMetadataParcel);
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
      paramByteBuffer = ((zzc)zzclt()).zza(paramByteBuffer, paramFrameMetadataParcel);
      return paramByteBuffer;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramByteBuffer);
    }
    return new Barcode[0];
  }
  
  protected void zzclq()
    throws RemoteException
  {
    ((zzc)zzclt()).zzclr();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/internal/client/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */