package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzbr
  extends zzbfm
{
  public static final Parcelable.Creator<zzbr> CREATOR = new zzbs();
  private final Account zzebz;
  private int zzeck;
  private final int zzgbl;
  private final GoogleSignInAccount zzgbm;
  
  zzbr(int paramInt1, Account paramAccount, int paramInt2, GoogleSignInAccount paramGoogleSignInAccount)
  {
    this.zzeck = paramInt1;
    this.zzebz = paramAccount;
    this.zzgbl = paramInt2;
    this.zzgbm = paramGoogleSignInAccount;
  }
  
  public zzbr(Account paramAccount, int paramInt, GoogleSignInAccount paramGoogleSignInAccount)
  {
    this(2, paramAccount, paramInt, paramGoogleSignInAccount);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.zzeck);
    zzbfp.zza(paramParcel, 2, this.zzebz, paramInt, false);
    zzbfp.zzc(paramParcel, 3, this.zzgbl);
    zzbfp.zza(paramParcel, 4, this.zzgbm, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */