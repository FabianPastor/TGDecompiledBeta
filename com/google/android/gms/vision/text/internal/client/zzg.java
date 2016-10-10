package com.google.android.gms.vision.text.internal.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.internal.zzsu.zza;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.internal.client.zza;

public class zzg
  extends zza<zzb>
{
  private final TextRecognizerOptions aLD;
  
  public zzg(Context paramContext, TextRecognizerOptions paramTextRecognizerOptions)
  {
    super(paramContext, "TextNativeHandle");
    this.aLD = paramTextRecognizerOptions;
    zzclt();
  }
  
  public LineBoxParcel[] zza(Bitmap paramBitmap, FrameMetadataParcel paramFrameMetadataParcel, RecognitionOptions paramRecognitionOptions)
  {
    if (!isOperational()) {
      return new LineBoxParcel[0];
    }
    try
    {
      paramBitmap = zze.zzac(paramBitmap);
      paramBitmap = ((zzb)zzclt()).zza(paramBitmap, paramFrameMetadataParcel, paramRecognitionOptions);
      return paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      Log.e("TextNativeHandle", "Error calling native text recognizer", paramBitmap);
    }
    return new LineBoxParcel[0];
  }
  
  protected void zzclq()
    throws RemoteException
  {
    ((zzb)zzclt()).zzclx();
  }
  
  protected zzb zzd(zzsu paramzzsu, Context paramContext)
    throws RemoteException, zzsu.zza
  {
    return zzc.zza.zzlo(paramzzsu.zzjd("com.google.android.gms.vision.text.ChimeraNativeTextRecognizerCreator")).zza(zze.zzac(paramContext), this.aLD);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */