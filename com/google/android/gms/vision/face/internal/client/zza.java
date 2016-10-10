package com.google.android.gms.vision.face.internal.client;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.internal.zzsu.zza;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import java.nio.ByteBuffer;

public class zza
  extends com.google.android.gms.vision.internal.client.zza<zzd>
{
  private final FaceSettingsParcel aLf;
  
  public zza(Context paramContext, FaceSettingsParcel paramFaceSettingsParcel)
  {
    super(paramContext, "FaceNativeHandle");
    this.aLf = paramFaceSettingsParcel;
    zzclt();
  }
  
  private Face zza(FaceParcel paramFaceParcel)
  {
    return new Face(paramFaceParcel.id, new PointF(paramFaceParcel.centerX, paramFaceParcel.centerY), paramFaceParcel.width, paramFaceParcel.height, paramFaceParcel.aLg, paramFaceParcel.aLh, zzb(paramFaceParcel), paramFaceParcel.aLj, paramFaceParcel.aLk, paramFaceParcel.aLl);
  }
  
  private Landmark zza(LandmarkParcel paramLandmarkParcel)
  {
    return new Landmark(new PointF(paramLandmarkParcel.x, paramLandmarkParcel.y), paramLandmarkParcel.type);
  }
  
  private Landmark[] zzb(FaceParcel paramFaceParcel)
  {
    int i = 0;
    paramFaceParcel = paramFaceParcel.aLi;
    if (paramFaceParcel == null) {
      return new Landmark[0];
    }
    Landmark[] arrayOfLandmark = new Landmark[paramFaceParcel.length];
    while (i < paramFaceParcel.length)
    {
      arrayOfLandmark[i] = zza(paramFaceParcel[i]);
      i += 1;
    }
    return arrayOfLandmark;
  }
  
  public boolean zzabr(int paramInt)
  {
    if (!isOperational()) {
      return false;
    }
    try
    {
      boolean bool = ((zzd)zzclt()).zzabr(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("FaceNativeHandle", "Could not call native face detector", localRemoteException);
    }
    return false;
  }
  
  public Face[] zzb(ByteBuffer paramByteBuffer, FrameMetadataParcel paramFrameMetadataParcel)
  {
    if (!isOperational()) {
      return new Face[0];
    }
    try
    {
      paramByteBuffer = com.google.android.gms.dynamic.zze.zzac(paramByteBuffer);
      paramByteBuffer = ((zzd)zzclt()).zzc(paramByteBuffer, paramFrameMetadataParcel);
      paramFrameMetadataParcel = new Face[paramByteBuffer.length];
      int i = 0;
      while (i < paramByteBuffer.length)
      {
        paramFrameMetadataParcel[i] = zza(paramByteBuffer[i]);
        i += 1;
      }
      return paramFrameMetadataParcel;
    }
    catch (RemoteException paramByteBuffer)
    {
      Log.e("FaceNativeHandle", "Could not call native face detector", paramByteBuffer);
      return new Face[0];
    }
  }
  
  protected zzd zzc(zzsu paramzzsu, Context paramContext)
    throws RemoteException, zzsu.zza
  {
    return zze.zza.zzlm(paramzzsu.zzjd("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator")).zza(com.google.android.gms.dynamic.zze.zzac(paramContext), this.aLf);
  }
  
  protected void zzclq()
    throws RemoteException
  {
    ((zzd)zzclt()).zzclr();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */