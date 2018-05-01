package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.widget.RemoteViews;

public final class zzdku
  extends zzbfm
{
  public static final Parcelable.Creator<zzdku> CREATOR = new zzdkv();
  private String[] zzleh;
  private int[] zzlei;
  private RemoteViews zzlej;
  private byte[] zzlek;
  
  private zzdku() {}
  
  public zzdku(String[] paramArrayOfString, int[] paramArrayOfInt, RemoteViews paramRemoteViews, byte[] paramArrayOfByte)
  {
    this.zzleh = paramArrayOfString;
    this.zzlei = paramArrayOfInt;
    this.zzlej = paramRemoteViews;
    this.zzlek = paramArrayOfByte;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 1, this.zzleh, false);
    zzbfp.zza(paramParcel, 2, this.zzlei, false);
    zzbfp.zza(paramParcel, 3, this.zzlej, paramInt, false);
    zzbfp.zza(paramParcel, 4, this.zzlek, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdku.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */