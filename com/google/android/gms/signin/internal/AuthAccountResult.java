package com.google.android.gms.signin.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class AuthAccountResult
  extends AbstractSafeParcelable
  implements Result
{
  public static final Parcelable.Creator<AuthAccountResult> CREATOR = new zza();
  private int aAf;
  private Intent aAg;
  final int mVersionCode;
  
  public AuthAccountResult()
  {
    this(0, null);
  }
  
  AuthAccountResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mVersionCode = paramInt1;
    this.aAf = paramInt2;
    this.aAg = paramIntent;
  }
  
  public AuthAccountResult(int paramInt, Intent paramIntent)
  {
    this(2, paramInt, paramIntent);
  }
  
  public Status getStatus()
  {
    if (this.aAf == 0) {
      return Status.vY;
    }
    return Status.wc;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  public int zzcdg()
  {
    return this.aAf;
  }
  
  public Intent zzcdh()
  {
    return this.aAg;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/AuthAccountResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */