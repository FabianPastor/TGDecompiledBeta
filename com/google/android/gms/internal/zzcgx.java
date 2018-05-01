package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Iterator;

public final class zzcgx
  extends zzbfm
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzcgx> CREATOR = new zzcgz();
  private final Bundle zzebe;
  
  zzcgx(Bundle paramBundle)
  {
    this.zzebe = paramBundle;
  }
  
  final Object get(String paramString)
  {
    return this.zzebe.get(paramString);
  }
  
  final Double getDouble(String paramString)
  {
    return Double.valueOf(this.zzebe.getDouble(paramString));
  }
  
  final Long getLong(String paramString)
  {
    return Long.valueOf(this.zzebe.getLong(paramString));
  }
  
  final String getString(String paramString)
  {
    return this.zzebe.getString(paramString);
  }
  
  public final Iterator<String> iterator()
  {
    return new zzcgy(this);
  }
  
  public final int size()
  {
    return this.zzebe.size();
  }
  
  public final String toString()
  {
    return this.zzebe.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 2, zzayx(), false);
    zzbfp.zzai(paramParcel, paramInt);
  }
  
  public final Bundle zzayx()
  {
    return new Bundle(this.zzebe);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */