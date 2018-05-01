package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class zza
  extends com.google.android.gms.common.internal.safeparcel.zza
{
  public static final Parcelable.Creator<zza> CREATOR = new zzb();
  byte[] zzbRE;
  byte[] zzbRF;
  zzm zzbRG;
  
  public zza(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, zzm paramzzm)
  {
    this.zzbRE = paramArrayOfByte1;
    this.zzbRF = paramArrayOfByte2;
    this.zzbRG = paramzzm;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */