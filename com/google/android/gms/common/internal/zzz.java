package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzz
  extends zzbfm
{
  public static final Parcelable.Creator<zzz> CREATOR = new zzaa();
  private int version;
  private int zzfzr;
  private int zzfzs;
  String zzfzt;
  IBinder zzfzu;
  Scope[] zzfzv;
  Bundle zzfzw;
  Account zzfzx;
  zzc[] zzfzy;
  
  public zzz(int paramInt)
  {
    this.version = 3;
    this.zzfzs = zzf.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    this.zzfzr = paramInt;
  }
  
  zzz(int paramInt1, int paramInt2, int paramInt3, String paramString, IBinder paramIBinder, Scope[] paramArrayOfScope, Bundle paramBundle, Account paramAccount, zzc[] paramArrayOfzzc)
  {
    this.version = paramInt1;
    this.zzfzr = paramInt2;
    this.zzfzs = paramInt3;
    if ("com.google.android.gms".equals(paramString))
    {
      this.zzfzt = "com.google.android.gms";
      if (paramInt1 >= 2) {
        break label148;
      }
      paramString = (String)localObject2;
      if (paramIBinder != null)
      {
        if (paramIBinder != null) {
          break label105;
        }
        paramString = (String)localObject1;
        label64:
        paramString = zza.zza(paramString);
      }
    }
    for (this.zzfzx = paramString;; this.zzfzx = paramAccount)
    {
      this.zzfzv = paramArrayOfScope;
      this.zzfzw = paramBundle;
      this.zzfzy = paramArrayOfzzc;
      return;
      this.zzfzt = paramString;
      break;
      label105:
      paramString = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
      if ((paramString instanceof zzan))
      {
        paramString = (zzan)paramString;
        break label64;
      }
      paramString = new zzap(paramIBinder);
      break label64;
      label148:
      this.zzfzu = paramIBinder;
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.version);
    zzbfp.zzc(paramParcel, 2, this.zzfzr);
    zzbfp.zzc(paramParcel, 3, this.zzfzs);
    zzbfp.zza(paramParcel, 4, this.zzfzt, false);
    zzbfp.zza(paramParcel, 5, this.zzfzu, false);
    zzbfp.zza(paramParcel, 6, this.zzfzv, paramInt, false);
    zzbfp.zza(paramParcel, 7, this.zzfzw, false);
    zzbfp.zza(paramParcel, 8, this.zzfzx, paramInt, false);
    zzbfp.zza(paramParcel, 10, this.zzfzy, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */