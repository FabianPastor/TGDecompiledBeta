package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzi
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzi> CREATOR = new zzj();
  private final String value;
  private byte zzbd;
  private final byte zzbe;
  
  public zzi(byte paramByte1, byte paramByte2, String paramString)
  {
    this.zzbd = ((byte)paramByte1);
    this.zzbe = ((byte)paramByte2);
    this.value = paramString;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzi)paramObject;
        if (this.zzbd != ((zzi)paramObject).zzbd) {
          bool = false;
        } else if (this.zzbe != ((zzi)paramObject).zzbe) {
          bool = false;
        } else if (!this.value.equals(((zzi)paramObject).value)) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return ((this.zzbd + 31) * 31 + this.zzbe) * 31 + this.value.hashCode();
  }
  
  public final String toString()
  {
    int i = this.zzbd;
    int j = this.zzbe;
    String str = this.value;
    return String.valueOf(str).length() + 73 + "AmsEntityUpdateParcelable{, mEntityId=" + i + ", mAttributeId=" + j + ", mValue='" + str + '\'' + '}';
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeByte(paramParcel, 2, this.zzbd);
    SafeParcelWriter.writeByte(paramParcel, 3, this.zzbe);
    SafeParcelWriter.writeString(paramParcel, 4, this.value, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */