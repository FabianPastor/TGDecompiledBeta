package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public class zzbkn
  extends zzbjz<zzbkf>
{
  private final zzbko zzbPx;
  
  public zzbkn(Context paramContext, zzbko paramzzbko)
  {
    super(paramContext, "TextNativeHandle");
    this.zzbPx = paramzzbko;
    zzTU();
  }
  
  protected void zzTR()
    throws RemoteException
  {
    ((zzbkf)zzTU()).zzTY();
  }
  
  public zzbkh[] zza(Bitmap paramBitmap, zzbka paramzzbka, zzbkj paramzzbkj)
  {
    if (!isOperational()) {
      return new zzbkh[0];
    }
    try
    {
      paramBitmap = zzd.zzA(paramBitmap);
      paramBitmap = ((zzbkf)zzTU()).zza(paramBitmap, paramzzbka, paramzzbkj);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("TextNativeHandle", "Error calling native text recognizer", paramBitmap);
    }
    return new zzbkh[0];
  }
  
  protected zzbkf zzd(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zza
  {
    return zzbkg.zza.zzfs(paramDynamiteModule.zzdT("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zzd.zzA(paramContext), this.zzbPx);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */