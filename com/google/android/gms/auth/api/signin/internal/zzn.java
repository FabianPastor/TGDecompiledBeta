package com.google.android.gms.auth.api.signin.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzn
  extends zza
{
  public static final Parcelable.Creator<zzn> CREATOR = new zzm();
  private Bundle mBundle;
  private int versionCode;
  private int zzamr;
  
  zzn(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    this.versionCode = paramInt1;
    this.zzamr = paramInt2;
    this.mBundle = paramBundle;
  }
  
  public zzn(GoogleSignInOptionsExtension paramGoogleSignInOptionsExtension)
  {
    this(1, 1, paramGoogleSignInOptionsExtension.toBundle());
  }
  
  public final int getType()
  {
    return this.zzamr;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.versionCode);
    zzd.zzc(paramParcel, 2, this.zzamr);
    zzd.zza(paramParcel, 3, this.mBundle, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */