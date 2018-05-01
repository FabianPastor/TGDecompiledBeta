package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;

public final class IsReadyToPayRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<IsReadyToPayRequest> CREATOR = new zzr();
  ArrayList<Integer> zzai;
  private String zzbu;
  private String zzbv;
  ArrayList<Integer> zzbw;
  boolean zzbx;
  private String zzby;
  
  IsReadyToPayRequest() {}
  
  IsReadyToPayRequest(ArrayList<Integer> paramArrayList1, String paramString1, String paramString2, ArrayList<Integer> paramArrayList2, boolean paramBoolean, String paramString3)
  {
    this.zzai = paramArrayList1;
    this.zzbu = paramString1;
    this.zzbv = paramString2;
    this.zzbw = paramArrayList2;
    this.zzbx = paramBoolean;
    this.zzby = paramString3;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new IsReadyToPayRequest(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeIntegerList(paramParcel, 2, this.zzai, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzbu, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzbv, false);
    SafeParcelWriter.writeIntegerList(paramParcel, 6, this.zzbw, false);
    SafeParcelWriter.writeBoolean(paramParcel, 7, this.zzbx);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzby, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final IsReadyToPayRequest build()
    {
      return IsReadyToPayRequest.this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/IsReadyToPayRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */