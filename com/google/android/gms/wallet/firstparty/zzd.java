package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

@Deprecated
public final class zzd
  extends zza
{
  public static final Parcelable.Creator<zzd> CREATOR = new zze();
  byte[] zzbRH;
  byte[] zzbRI;
  
  zzd()
  {
    this(null, null);
  }
  
  public zzd(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    this.zzbRH = paramArrayOfByte1;
    this.zzbRI = paramArrayOfByte2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zze.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */