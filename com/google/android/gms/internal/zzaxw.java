package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzaxw
  extends zza
{
  public static final Parcelable.Creator<zzaxw> CREATOR = new zzaxx();
  final int mVersionCode;
  private final Account zzagg;
  private final String zzajk;
  private final Scope[] zzbCp;
  
  zzaxw(int paramInt, Account paramAccount, Scope[] paramArrayOfScope, String paramString)
  {
    this.mVersionCode = paramInt;
    this.zzagg = paramAccount;
    this.zzbCp = paramArrayOfScope;
    this.zzajk = paramString;
  }
  
  public Account getAccount()
  {
    return this.zzagg;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaxx.zza(this, paramParcel, paramInt);
  }
  
  public Scope[] zzOm()
  {
    return this.zzbCp;
  }
  
  public String zzqN()
  {
    return this.zzajk;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */