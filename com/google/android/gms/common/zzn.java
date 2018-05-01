package com.google.android.gms.common;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzat;
import com.google.android.gms.common.internal.zzau;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.internal.zzev;

public final class zzn
  extends zzbfm
{
  public static final Parcelable.Creator<zzn> CREATOR = new zzo();
  private final String zzflg;
  private final zzh zzflh;
  private final boolean zzfli;
  
  zzn(String paramString, IBinder paramIBinder, boolean paramBoolean)
  {
    this.zzflg = paramString;
    this.zzflh = zzak(paramIBinder);
    this.zzfli = paramBoolean;
  }
  
  zzn(String paramString, zzh paramzzh, boolean paramBoolean)
  {
    this.zzflg = paramString;
    this.zzflh = paramzzh;
    this.zzfli = paramBoolean;
  }
  
  private static zzh zzak(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    for (;;)
    {
      try
      {
        paramIBinder = zzau.zzam(paramIBinder).zzaga();
        if (paramIBinder == null)
        {
          paramIBinder = null;
          if (paramIBinder == null) {
            break label60;
          }
          paramIBinder = new zzi(paramIBinder);
          return paramIBinder;
        }
      }
      catch (RemoteException paramIBinder)
      {
        Log.e("GoogleCertificatesQuery", "Could not unwrap certificate", paramIBinder);
        return null;
      }
      paramIBinder = (byte[])com.google.android.gms.dynamic.zzn.zzx(paramIBinder);
      continue;
      label60:
      Log.e("GoogleCertificatesQuery", "Could not unwrap certificate");
      paramIBinder = null;
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 1, this.zzflg, false);
    if (this.zzflh == null) {
      Log.w("GoogleCertificatesQuery", "certificate binder is null");
    }
    for (IBinder localIBinder = null;; localIBinder = this.zzflh.asBinder())
    {
      zzbfp.zza(paramParcel, 2, localIBinder, false);
      zzbfp.zza(paramParcel, 3, this.zzfli);
      zzbfp.zzai(paramParcel, paramInt);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */