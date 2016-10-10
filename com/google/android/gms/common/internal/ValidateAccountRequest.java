package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

@Deprecated
public class ValidateAccountRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ValidateAccountRequest> CREATOR = new zzak();
  final IBinder AW;
  private final Scope[] AX;
  private final int De;
  private final Bundle Df;
  private final String Dg;
  final int mVersionCode;
  
  ValidateAccountRequest(int paramInt1, int paramInt2, IBinder paramIBinder, Scope[] paramArrayOfScope, Bundle paramBundle, String paramString)
  {
    this.mVersionCode = paramInt1;
    this.De = paramInt2;
    this.AW = paramIBinder;
    this.AX = paramArrayOfScope;
    this.Df = paramBundle;
    this.Dg = paramString;
  }
  
  public String getCallingPackage()
  {
    return this.Dg;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzak.zza(this, paramParcel, paramInt);
  }
  
  public Scope[] zzavj()
  {
    return this.AX;
  }
  
  public int zzavl()
  {
    return this.De;
  }
  
  public Bundle zzavm()
  {
    return this.Df;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ValidateAccountRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */