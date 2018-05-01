package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzaxp
  extends zza
  implements Result
{
  public static final Parcelable.Creator<zzaxp> CREATOR = new zzaxq();
  final int mVersionCode;
  private int zzbCl;
  private Intent zzbCm;
  
  public zzaxp()
  {
    this(0, null);
  }
  
  zzaxp(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mVersionCode = paramInt1;
    this.zzbCl = paramInt2;
    this.zzbCm = paramIntent;
  }
  
  public zzaxp(int paramInt, Intent paramIntent)
  {
    this(2, paramInt, paramIntent);
  }
  
  public Status getStatus()
  {
    if (this.zzbCl == 0) {
      return Status.zzayh;
    }
    return Status.zzayl;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaxq.zza(this, paramParcel, paramInt);
  }
  
  public int zzOk()
  {
    return this.zzbCl;
  }
  
  public Intent zzOl()
  {
    return this.zzbCm;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */