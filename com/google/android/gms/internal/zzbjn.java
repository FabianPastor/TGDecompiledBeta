package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.barcode.Barcode;
import java.nio.ByteBuffer;

public class zzbjn
  extends zzbjz<zzbjo>
{
  private final zzbjl zzbOI;
  
  public zzbjn(Context paramContext, zzbjl paramzzbjl)
  {
    super(paramContext, "BarcodeNativeHandle");
    this.zzbOI = paramzzbjl;
    zzTU();
  }
  
  protected void zzTR()
    throws RemoteException
  {
    ((zzbjo)zzTU()).zzTS();
  }
  
  protected zzbjo zza(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zza
  {
    return zzbjp.zza.zzfo(paramDynamiteModule.zzdT("com.google.android.gms.vision.barcode.ChimeraNativeBarcodeDetectorCreator")).zza(zzd.zzA(paramContext), this.zzbOI);
  }
  
  public Barcode[] zza(Bitmap paramBitmap, zzbka paramzzbka)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramBitmap = zzd.zzA(paramBitmap);
      paramBitmap = ((zzbjo)zzTU()).zzb(paramBitmap, paramzzbka);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramBitmap);
    }
    return new Barcode[0];
  }
  
  public Barcode[] zza(ByteBuffer paramByteBuffer, zzbka paramzzbka)
  {
    if (!isOperational()) {
      return new Barcode[0];
    }
    try
    {
      paramByteBuffer = zzd.zzA(paramByteBuffer);
      paramByteBuffer = ((zzbjo)zzTU()).zza(paramByteBuffer, paramzzbka);
      return paramByteBuffer;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("BarcodeNativeHandle", "Error calling native barcode detector", paramByteBuffer);
    }
    return new Barcode[0];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */