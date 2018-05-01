package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzc
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzc> CREATOR = new zzd();
  public int mode;
  public int zzcd;
  public int zzce;
  public boolean zzcf;
  public boolean zzcg;
  public float zzch;
  
  public zzc() {}
  
  public zzc(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    this.mode = paramInt1;
    this.zzcd = paramInt2;
    this.zzce = paramInt3;
    this.zzcf = paramBoolean1;
    this.zzcg = paramBoolean2;
    this.zzch = paramFloat;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.mode);
    SafeParcelWriter.writeInt(paramParcel, 3, this.zzcd);
    SafeParcelWriter.writeInt(paramParcel, 4, this.zzce);
    SafeParcelWriter.writeBoolean(paramParcel, 5, this.zzcf);
    SafeParcelWriter.writeBoolean(paramParcel, 6, this.zzcg);
    SafeParcelWriter.writeFloat(paramParcel, 7, this.zzch);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */