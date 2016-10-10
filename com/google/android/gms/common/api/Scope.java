package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public final class Scope
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<Scope> CREATOR = new zzg();
  final int mVersionCode;
  private final String vX;
  
  Scope(int paramInt, String paramString)
  {
    zzac.zzh(paramString, "scopeUri must not be null or empty");
    this.mVersionCode = paramInt;
    this.vX = paramString;
  }
  
  public Scope(String paramString)
  {
    this(1, paramString);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Scope)) {
      return false;
    }
    return this.vX.equals(((Scope)paramObject).vX);
  }
  
  public int hashCode()
  {
    return this.vX.hashCode();
  }
  
  public String toString()
  {
    return this.vX;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzg.zza(this, paramParcel, paramInt);
  }
  
  public String zzaqg()
  {
    return this.vX;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Scope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */