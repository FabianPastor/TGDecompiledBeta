package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class LineItem
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LineItem> CREATOR = new zzt();
  String description;
  String zzan;
  String zzao;
  String zzca;
  String zzcb;
  int zzcc;
  
  LineItem()
  {
    this.zzcc = 0;
  }
  
  LineItem(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5)
  {
    this.description = paramString1;
    this.zzca = paramString2;
    this.zzcb = paramString3;
    this.zzan = paramString4;
    this.zzcc = paramInt;
    this.zzao = paramString5;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new LineItem(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.description, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzca, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzcb, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzan, false);
    SafeParcelWriter.writeInt(paramParcel, 6, this.zzcc);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzao, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final LineItem build()
    {
      return LineItem.this;
    }
    
    public final Builder setCurrencyCode(String paramString)
    {
      LineItem.this.zzao = paramString;
      return this;
    }
    
    public final Builder setDescription(String paramString)
    {
      LineItem.this.description = paramString;
      return this;
    }
    
    public final Builder setQuantity(String paramString)
    {
      LineItem.this.zzca = paramString;
      return this;
    }
    
    public final Builder setTotalPrice(String paramString)
    {
      LineItem.this.zzan = paramString;
      return this;
    }
    
    public final Builder setUnitPrice(String paramString)
    {
      LineItem.this.zzcb = paramString;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/LineItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */