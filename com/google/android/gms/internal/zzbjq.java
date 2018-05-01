package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public class zzbjq
  extends zzbjz<zzbjv>
{
  private final zzbjt zzbPa;
  
  public zzbjq(Context paramContext, zzbjt paramzzbjt)
  {
    super(paramContext, "FaceNativeHandle");
    this.zzbPa = paramzzbjt;
    zzTU();
  }
  
  private Face zza(zzbjr paramzzbjr)
  {
    return new Face(paramzzbjr.id, new PointF(paramzzbjr.centerX, paramzzbjr.centerY), paramzzbjr.width, paramzzbjr.height, paramzzbjr.zzbPb, paramzzbjr.zzbPc, zzb(paramzzbjr), paramzzbjr.zzbPe, paramzzbjr.zzbPf, paramzzbjr.zzbPg);
  }
  
  private Landmark zza(zzbjx paramzzbjx)
  {
    return new Landmark(new PointF(paramzzbjx.x, paramzzbjx.y), paramzzbjx.type);
  }
  
  private Landmark[] zzb(zzbjr paramzzbjr)
  {
    int i = 0;
    paramzzbjr = paramzzbjr.zzbPd;
    if (paramzzbjr == null) {
      return new Landmark[0];
    }
    Landmark[] arrayOfLandmark = new Landmark[paramzzbjr.length];
    while (i < paramzzbjr.length)
    {
      arrayOfLandmark[i] = zza(paramzzbjr[i]);
      i += 1;
    }
    return arrayOfLandmark;
  }
  
  protected void zzTR()
    throws RemoteException
  {
    ((zzbjv)zzTU()).zzTS();
  }
  
  public Face[] zzb(ByteBuffer paramByteBuffer, zzbka paramzzbka)
  {
    if (!isOperational()) {
      return new Face[0];
    }
    try
    {
      paramByteBuffer = zzd.zzA(paramByteBuffer);
      paramByteBuffer = ((zzbjv)zzTU()).zzc(paramByteBuffer, paramzzbka);
      paramzzbka = new Face[paramByteBuffer.length];
      int i = 0;
      while (i < paramByteBuffer.length)
      {
        paramzzbka[i] = zza(paramByteBuffer[i]);
        i += 1;
      }
      return paramzzbka;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("FaceNativeHandle", "Could not call native face detector", paramByteBuffer);
      return new Face[0];
    }
  }
  
  protected zzbjv zzc(DynamiteModule paramDynamiteModule, Context paramContext)
    throws RemoteException, DynamiteModule.zza
  {
    return zzbjw.zza.zzfq(paramDynamiteModule.zzdT("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator")).zza(zzd.zzA(paramContext), this.zzbPa);
  }
  
  public boolean zzoh(int paramInt)
  {
    if (!isOperational()) {
      return false;
    }
    try
    {
      boolean bool = ((zzbjv)zzTU()).zzoh(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("FaceNativeHandle", "Could not call native face detector", localRemoteException);
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */