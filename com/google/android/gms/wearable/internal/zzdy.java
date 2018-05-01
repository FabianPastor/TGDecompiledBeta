package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.ConnectionConfiguration;

public final class zzdy
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzdy> CREATOR = new zzdz();
  private final int statusCode;
  private final ConnectionConfiguration[] zzdw;
  
  public zzdy(int paramInt, ConnectionConfiguration[] paramArrayOfConnectionConfiguration)
  {
    this.statusCode = paramInt;
    this.zzdw = paramArrayOfConnectionConfiguration;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.statusCode);
    SafeParcelWriter.writeTypedArray(paramParcel, 3, this.zzdw, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzdy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */