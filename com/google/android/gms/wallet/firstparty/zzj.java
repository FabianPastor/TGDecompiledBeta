package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzj
  extends zza
{
  public static final Parcelable.Creator<zzj> CREATOR = new zzk();
  byte[] zzbRM;
  
  zzj()
  {
    this(new byte[0]);
  }
  
  public zzj(byte[] paramArrayOfByte)
  {
    this.zzbRM = paramArrayOfByte;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzk.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */