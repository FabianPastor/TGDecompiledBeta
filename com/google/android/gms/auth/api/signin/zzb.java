package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import java.util.ArrayList;

public class zzb
  implements Parcelable.Creator<GoogleSignInOptions>
{
  static void zza(GoogleSignInOptions paramGoogleSignInOptions, Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramGoogleSignInOptions.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 2, paramGoogleSignInOptions.zzahj(), false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramGoogleSignInOptions.getAccount(), paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 4, paramGoogleSignInOptions.zzahk());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 5, paramGoogleSignInOptions.zzahl());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 6, paramGoogleSignInOptions.zzahm());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 7, paramGoogleSignInOptions.zzahn(), false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 8, paramGoogleSignInOptions.zzaho(), false);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, i);
  }
  
  public GoogleSignInOptions zzaw(Parcel paramParcel)
  {
    String str1 = null;
    boolean bool1 = false;
    int j = zza.zzcq(paramParcel);
    String str2 = null;
    boolean bool2 = false;
    boolean bool3 = false;
    Account localAccount = null;
    ArrayList localArrayList = null;
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        localArrayList = zza.zzc(paramParcel, k, Scope.CREATOR);
        break;
      case 3: 
        localAccount = (Account)zza.zza(paramParcel, k, Account.CREATOR);
        break;
      case 4: 
        bool3 = zza.zzc(paramParcel, k);
        break;
      case 5: 
        bool2 = zza.zzc(paramParcel, k);
        break;
      case 6: 
        bool1 = zza.zzc(paramParcel, k);
        break;
      case 7: 
        str2 = zza.zzq(paramParcel, k);
        break;
      case 8: 
        str1 = zza.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new GoogleSignInOptions(i, localArrayList, localAccount, bool3, bool2, bool1, str2, str1);
  }
  
  public GoogleSignInOptions[] zzdh(int paramInt)
  {
    return new GoogleSignInOptions[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */