package com.google.android.gms.internal.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.widget.RemoteViews;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzj
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzj> CREATOR = new zzk();
  private String[] zzex;
  private int[] zzey;
  private RemoteViews zzez;
  private byte[] zzfa;
  
  private zzj() {}
  
  public zzj(String[] paramArrayOfString, int[] paramArrayOfInt, RemoteViews paramRemoteViews, byte[] paramArrayOfByte)
  {
    this.zzex = paramArrayOfString;
    this.zzey = paramArrayOfInt;
    this.zzez = paramRemoteViews;
    this.zzfa = paramArrayOfByte;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeStringArray(paramParcel, 1, this.zzex, false);
    SafeParcelWriter.writeIntArray(paramParcel, 2, this.zzey, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzez, paramInt, false);
    SafeParcelWriter.writeByteArray(paramParcel, 4, this.zzfa, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */