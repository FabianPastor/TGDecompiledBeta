package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzn;

public final class fq
  extends fb<fg>
{
  private final fr zzbNV;
  
  public fq(Context paramContext, fr paramfr)
  {
    super(paramContext, "TextNativeHandle");
    this.zzbNV = paramfr;
    zzDR();
  }
  
  protected final void zzDO()
    throws RemoteException
  {
    ((fg)zzDR()).zzDS();
  }
  
  public final fk[] zza(Bitmap paramBitmap, fc paramfc, fm paramfm)
  {
    if (!isOperational()) {
      return new fk[0];
    }
    try
    {
      paramBitmap = zzn.zzw(paramBitmap);
      paramBitmap = ((fg)zzDR()).zza(paramBitmap, paramfc, paramfm);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("TextNativeHandle", "Error calling native text recognizer", paramBitmap);
    }
    return new fk[0];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */