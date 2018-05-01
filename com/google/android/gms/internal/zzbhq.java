package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public class zzbhq
  extends zzbhc<zzbhi>
{
  private final zzbhr zzbNz;
  
  public zzbhq(Context paramContext, zzbhr paramzzbhr)
  {
    super(paramContext, "TextNativeHandle");
    this.zzbNz = paramzzbhr;
    zzSq();
  }
  
  protected void zzSn()
    throws RemoteException
  {
    ((zzbhi)zzSq()).zzSu();
  }
  
  public zzbhk[] zza(Bitmap paramBitmap, zzbhd paramzzbhd, zzbhm paramzzbhm)
  {
    if (!isOperational()) {
      return new zzbhk[0];
    }
    try
    {
      paramBitmap = zze.zzA(paramBitmap);
      paramBitmap = ((zzbhi)zzSq()).zza(paramBitmap, paramzzbhd, paramzzbhm);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("TextNativeHandle", "Error calling native text recognizer", paramBitmap);
    }
    return new zzbhk[0];
  }
  
  protected zzbhi zzd(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zza
  {
    return zzbhj.zza.zzfl(paramDynamiteModule.zzdX("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zze.zzA(paramContext), this.zzbNz);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */