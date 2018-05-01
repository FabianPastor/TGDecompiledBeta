package com.google.android.gms.wallet.firstparty;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzm
  extends zza
{
  public static final Parcelable.Creator<zzm> CREATOR = new zzn();
  int zzbRO;
  Bundle zzbRP;
  String zzbRQ;
  
  public zzm()
  {
    this.zzbRO = 0;
    this.zzbRP = new Bundle();
    this.zzbRQ = "";
  }
  
  zzm(int paramInt, Bundle paramBundle, String paramString)
  {
    this.zzbRP = paramBundle;
    this.zzbRO = paramInt;
    this.zzbRQ = paramString;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzn.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */