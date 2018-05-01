package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public class InitializeBuyFlowRequest
  extends zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<InitializeBuyFlowRequest> CREATOR = new zzl();
  private final int zzaiI;
  byte[][] zzbRN;
  
  InitializeBuyFlowRequest(int paramInt, byte[][] paramArrayOfByte)
  {
    this.zzaiI = paramInt;
    this.zzbRN = paramArrayOfByte;
  }
  
  public int getVersionCode()
  {
    return this.zzaiI;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzl.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/InitializeBuyFlowRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */