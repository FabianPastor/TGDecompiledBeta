package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class ResolveAccountResponse
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ResolveAccountResponse> CREATOR = new zzae();
  IBinder AW;
  private boolean CX;
  final int mVersionCode;
  private ConnectionResult vm;
  private boolean xz;
  
  ResolveAccountResponse(int paramInt, IBinder paramIBinder, ConnectionResult paramConnectionResult, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mVersionCode = paramInt;
    this.AW = paramIBinder;
    this.vm = paramConnectionResult;
    this.xz = paramBoolean1;
    this.CX = paramBoolean2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof ResolveAccountResponse)) {
        return false;
      }
      paramObject = (ResolveAccountResponse)paramObject;
    } while ((this.vm.equals(((ResolveAccountResponse)paramObject).vm)) && (zzavd().equals(((ResolveAccountResponse)paramObject).zzavd())));
    return false;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzae.zza(this, paramParcel, paramInt);
  }
  
  public zzr zzavd()
  {
    return zzr.zza.zzdr(this.AW);
  }
  
  public ConnectionResult zzave()
  {
    return this.vm;
  }
  
  public boolean zzavf()
  {
    return this.xz;
  }
  
  public boolean zzavg()
  {
    return this.CX;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ResolveAccountResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */