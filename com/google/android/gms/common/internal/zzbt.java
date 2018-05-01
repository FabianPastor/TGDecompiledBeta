package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzbt
  extends zzbfm
{
  public static final Parcelable.Creator<zzbt> CREATOR = new zzbu();
  private int zzeck;
  private ConnectionResult zzfoo;
  private boolean zzfri;
  private IBinder zzgbn;
  private boolean zzgbo;
  
  zzbt(int paramInt, IBinder paramIBinder, ConnectionResult paramConnectionResult, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.zzeck = paramInt;
    this.zzgbn = paramIBinder;
    this.zzfoo = paramConnectionResult;
    this.zzfri = paramBoolean1;
    this.zzgbo = paramBoolean2;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbt)) {
        return false;
      }
      paramObject = (zzbt)paramObject;
    } while ((this.zzfoo.equals(((zzbt)paramObject).zzfoo)) && (zzalp().equals(((zzbt)paramObject).zzalp())));
    return false;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.zzeck);
    zzbfp.zza(paramParcel, 2, this.zzgbn, false);
    zzbfp.zza(paramParcel, 3, this.zzfoo, paramInt, false);
    zzbfp.zza(paramParcel, 4, this.zzfri);
    zzbfp.zza(paramParcel, 5, this.zzgbo);
    zzbfp.zzai(paramParcel, i);
  }
  
  public final ConnectionResult zzahf()
  {
    return this.zzfoo;
  }
  
  public final zzan zzalp()
  {
    IBinder localIBinder = this.zzgbn;
    if (localIBinder == null) {
      return null;
    }
    IInterface localIInterface = localIBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
    if ((localIInterface instanceof zzan)) {
      return (zzan)localIInterface;
    }
    return new zzap(localIBinder);
  }
  
  public final boolean zzalq()
  {
    return this.zzfri;
  }
  
  public final boolean zzalr()
  {
    return this.zzgbo;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */