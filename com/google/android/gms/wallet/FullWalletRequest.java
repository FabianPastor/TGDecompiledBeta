package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class FullWalletRequest
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<FullWalletRequest> CREATOR = new zzm();
  String zzaw;
  String zzax;
  Cart zzbh;
  
  FullWalletRequest() {}
  
  FullWalletRequest(String paramString1, String paramString2, Cart paramCart)
  {
    this.zzaw = paramString1;
    this.zzax = paramString2;
    this.zzbh = paramCart;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new FullWalletRequest(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzaw, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzax, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzbh, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final FullWalletRequest build()
    {
      return FullWalletRequest.this;
    }
    
    public final Builder setCart(Cart paramCart)
    {
      FullWalletRequest.this.zzbh = paramCart;
      return this;
    }
    
    public final Builder setGoogleTransactionId(String paramString)
    {
      FullWalletRequest.this.zzaw = paramString;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/FullWalletRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */