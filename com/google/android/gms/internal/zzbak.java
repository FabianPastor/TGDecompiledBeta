package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbak
  extends zza
  implements Result
{
  public static final Parcelable.Creator<zzbak> CREATOR = new zzbal();
  final int zzaiI;
  private int zzbEq;
  private Intent zzbEr;
  
  public zzbak()
  {
    this(0, null);
  }
  
  zzbak(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.zzaiI = paramInt1;
    this.zzbEq = paramInt2;
    this.zzbEr = paramIntent;
  }
  
  public zzbak(int paramInt, Intent paramIntent)
  {
    this(2, paramInt, paramIntent);
  }
  
  public Status getStatus()
  {
    if (this.zzbEq == 0) {
      return Status.zzazx;
    }
    return Status.zzazB;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbal.zza(this, paramParcel, paramInt);
  }
  
  public int zzPR()
  {
    return this.zzbEq;
  }
  
  public Intent zzPS()
  {
    return this.zzbEr;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */