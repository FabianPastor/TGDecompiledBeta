package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;

public final class Cart
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<Cart> CREATOR = new zzg();
  String zzan;
  String zzao;
  ArrayList<LineItem> zzap;
  
  Cart()
  {
    this.zzap = new ArrayList();
  }
  
  Cart(String paramString1, String paramString2, ArrayList<LineItem> paramArrayList)
  {
    this.zzan = paramString1;
    this.zzao = paramString2;
    this.zzap = paramArrayList;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new Cart(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzan, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzao, false);
    SafeParcelWriter.writeTypedList(paramParcel, 4, this.zzap, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final Builder addLineItem(LineItem paramLineItem)
    {
      Cart.this.zzap.add(paramLineItem);
      return this;
    }
    
    public final Cart build()
    {
      return Cart.this;
    }
    
    public final Builder setCurrencyCode(String paramString)
    {
      Cart.this.zzao = paramString;
      return this;
    }
    
    public final Builder setTotalPrice(String paramString)
    {
      Cart.this.zzan = paramString;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/Cart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */