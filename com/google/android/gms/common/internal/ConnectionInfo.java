package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class ConnectionInfo
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ConnectionInfo> CREATOR = new ConnectionInfoCreator();
  private Bundle zzsf;
  private Feature[] zzsg;
  
  public ConnectionInfo() {}
  
  ConnectionInfo(Bundle paramBundle, Feature[] paramArrayOfFeature)
  {
    this.zzsf = paramBundle;
    this.zzsg = paramArrayOfFeature;
  }
  
  public Feature[] getAvailableFeatures()
  {
    return this.zzsg;
  }
  
  public Bundle getResolutionBundle()
  {
    return this.zzsf;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 1, this.zzsf, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 2, this.zzsg, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ConnectionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */