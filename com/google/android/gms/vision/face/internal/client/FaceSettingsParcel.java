package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceSettingsParcel
  extends AbstractSafeParcelable
{
  public static final zzc CREATOR = new zzc();
  public int aLm;
  public int aLn;
  public boolean aLo;
  public boolean aLp;
  public float aLq;
  public int mode;
  public final int versionCode;
  
  public FaceSettingsParcel()
  {
    this.versionCode = 2;
  }
  
  public FaceSettingsParcel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    this.versionCode = paramInt1;
    this.mode = paramInt2;
    this.aLm = paramInt3;
    this.aLn = paramInt4;
    this.aLo = paramBoolean1;
    this.aLp = paramBoolean2;
    this.aLq = paramFloat;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzc.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/FaceSettingsParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */