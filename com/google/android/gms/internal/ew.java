package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.vision.barcode.Barcode;
import java.nio.ByteBuffer;

public final class ew
  extends fb<ex>
{
  private final eu zzbNh;
  
  public ew(Context paramContext, eu parameu)
  {
    super(paramContext, "BarcodeNativeHandle");
    this.zzbNh = parameu;
    zzDR();
  }
  
  protected final void zzDO()
    throws RemoteException
  {
    if (isOperational()) {
      ((ex)zzDR()).zzDP();
    }
  }
  
  public final Barcode[] zza(Bitmap paramBitmap, fc paramfc)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramBitmap = zzn.zzw(paramBitmap);
      paramBitmap = ((ex)zzDR()).zzb(paramBitmap, paramfc);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramBitmap);
    }
    return new Barcode[0];
  }
  
  public final Barcode[] zza(ByteBuffer paramByteBuffer, fc paramfc)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramByteBuffer = zzn.zzw(paramByteBuffer);
      paramByteBuffer = ((ex)zzDR()).zza(paramByteBuffer, paramfc);
      return paramByteBuffer;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramByteBuffer);
    }
    return new Barcode[0];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */