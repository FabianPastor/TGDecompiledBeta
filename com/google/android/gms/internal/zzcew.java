package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.Iterator;

public final class zzcew
  extends zza
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzcew> CREATOR = new zzcey();
  private final Bundle zzbpJ;
  
  zzcew(Bundle paramBundle)
  {
    this.zzbpJ = paramBundle;
  }
  
  final Object get(String paramString)
  {
    return this.zzbpJ.get(paramString);
  }
  
  public final Iterator<String> iterator()
  {
    return new zzcex(this);
  }
  
  public final int size()
  {
    return this.zzbpJ.size();
  }
  
  public final String toString()
  {
    return this.zzbpJ.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, zzyt(), false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final Bundle zzyt()
  {
    return new Bundle(this.zzbpJ);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */