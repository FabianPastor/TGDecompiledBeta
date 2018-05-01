package com.google.android.gms.vision.face.internal.client;

import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.vision.zzj;
import com.google.android.gms.internal.vision.zzk;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public final class zza
  extends zzj<zze>
{
  private final zzc zzbw;
  
  public zza(Context paramContext, zzc paramzzc)
  {
    super(paramContext, "FaceNativeHandle");
    this.zzbw = paramzzc;
    zzh();
  }
  
  public final Face[] zzb(ByteBuffer paramByteBuffer, zzk paramzzk)
  {
    if (!isOperational())
    {
      paramByteBuffer = new Face[0];
      return paramByteBuffer;
    }
    for (;;)
    {
      LandmarkParcel[] arrayOfLandmarkParcel;
      try
      {
        paramByteBuffer = ObjectWrapper.wrap(paramByteBuffer);
        FaceParcel[] arrayOfFaceParcel = ((zze)zzh()).zzc(paramByteBuffer, paramzzk);
        paramzzk = new Face[arrayOfFaceParcel.length];
        int i = 0;
        if (i >= arrayOfFaceParcel.length) {
          break label254;
        }
        FaceParcel localFaceParcel = arrayOfFaceParcel[i];
        int j = localFaceParcel.id;
        PointF localPointF = new PointF(localFaceParcel.centerX, localFaceParcel.centerY);
        float f1 = localFaceParcel.width;
        float f2 = localFaceParcel.height;
        float f3 = localFaceParcel.zzbx;
        float f4 = localFaceParcel.zzby;
        arrayOfLandmarkParcel = localFaceParcel.zzbz;
        if (arrayOfLandmarkParcel == null)
        {
          paramByteBuffer = new Landmark[0];
          paramzzk[i] = new Face(j, localPointF, f1, f2, f3, f4, paramByteBuffer, localFaceParcel.zzca, localFaceParcel.zzcb, localFaceParcel.zzcc);
          i++;
          continue;
        }
      }
      catch (RemoteException paramByteBuffer)
      {
        Log.e("FaceNativeHandle", "Could not call native face detector", paramByteBuffer);
        paramByteBuffer = new Face[0];
      }
      paramByteBuffer = new Landmark[arrayOfLandmarkParcel.length];
      int k = 0;
      for (;;)
      {
        if (k < arrayOfLandmarkParcel.length)
        {
          LandmarkParcel localLandmarkParcel = arrayOfLandmarkParcel[k];
          paramByteBuffer[k] = new Landmark(new PointF(localLandmarkParcel.x, localLandmarkParcel.y), localLandmarkParcel.type);
          k++;
          continue;
          label254:
          paramByteBuffer = paramzzk;
          break;
        }
      }
    }
  }
  
  protected final void zze()
    throws RemoteException
  {
    ((zze)zzh()).zzf();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */