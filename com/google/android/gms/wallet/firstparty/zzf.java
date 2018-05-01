package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzf
  extends zza
{
  public static final Parcelable.Creator<zzf> CREATOR = new zzg();
  byte[] zzbRJ;
  
  zzf()
  {
    this(new byte[0]);
  }
  
  public zzf(byte[] paramArrayOfByte)
  {
    this.zzbRJ = paramArrayOfByte;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzg.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */