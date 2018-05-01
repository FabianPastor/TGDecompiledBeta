package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class ResolveAccountResponse
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ResolveAccountResponse> CREATOR = new ResolveAccountResponseCreator();
  private final int zzal;
  private ConnectionResult zzeu;
  private boolean zzhs;
  private IBinder zzqv;
  private boolean zzuv;
  
  ResolveAccountResponse(int paramInt, IBinder paramIBinder, ConnectionResult paramConnectionResult, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.zzal = paramInt;
    this.zzqv = paramIBinder;
    this.zzeu = paramConnectionResult;
    this.zzhs = paramBoolean1;
    this.zzuv = paramBoolean2;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof ResolveAccountResponse))
      {
        bool = false;
      }
      else
      {
        paramObject = (ResolveAccountResponse)paramObject;
        if ((!this.zzeu.equals(((ResolveAccountResponse)paramObject).zzeu)) || (!getAccountAccessor().equals(((ResolveAccountResponse)paramObject).getAccountAccessor()))) {
          bool = false;
        }
      }
    }
  }
  
  public IAccountAccessor getAccountAccessor()
  {
    return IAccountAccessor.Stub.asInterface(this.zzqv);
  }
  
  public ConnectionResult getConnectionResult()
  {
    return this.zzeu;
  }
  
  public boolean getSaveDefaultAccount()
  {
    return this.zzhs;
  }
  
  public boolean isFromCrossClientAuth()
  {
    return this.zzuv;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeIBinder(paramParcel, 2, this.zzqv, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getConnectionResult(), paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 4, getSaveDefaultAccount());
    SafeParcelWriter.writeBoolean(paramParcel, 5, isFromCrossClientAuth());
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ResolveAccountResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */