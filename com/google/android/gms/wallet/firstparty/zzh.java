package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzh
  extends zza
{
  public static final Parcelable.Creator<zzh> CREATOR = new zzi();
  zzm zzbRK;
  boolean zzbRL;
  
  zzh() {}
  
  zzh(zzm paramzzm, boolean paramBoolean)
  {
    this.zzbRK = paramzzm;
    this.zzbRL = paramBoolean;
    if (paramzzm == null) {
      throw new NullPointerException("WalletCustomTheme is required");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzi.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */