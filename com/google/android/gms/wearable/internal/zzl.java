package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class zzl
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzl> CREATOR = new zzm();
  private int id;
  @Nullable
  private final String packageName;
  private final String zzbf;
  @Nullable
  private final String zzbg;
  private final String zzbh;
  private final String zzbi;
  private final String zzbj;
  @Nullable
  private final String zzbk;
  private final byte zzbl;
  private final byte zzbm;
  private final byte zzbn;
  private final byte zzbo;
  
  public zzl(int paramInt, String paramString1, @Nullable String paramString2, String paramString3, String paramString4, String paramString5, @Nullable String paramString6, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, @Nullable String paramString7)
  {
    this.id = paramInt;
    this.zzbf = paramString1;
    this.zzbg = paramString2;
    this.zzbh = paramString3;
    this.zzbi = paramString4;
    this.zzbj = paramString5;
    this.zzbk = paramString6;
    this.zzbl = ((byte)paramByte1);
    this.zzbm = ((byte)paramByte2);
    this.zzbn = ((byte)paramByte3);
    this.zzbo = ((byte)paramByte4);
    this.packageName = paramString7;
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
        paramObject = (zzl)paramObject;
        if (this.id != ((zzl)paramObject).id)
        {
          bool = false;
        }
        else if (this.zzbl != ((zzl)paramObject).zzbl)
        {
          bool = false;
        }
        else if (this.zzbm != ((zzl)paramObject).zzbm)
        {
          bool = false;
        }
        else if (this.zzbn != ((zzl)paramObject).zzbn)
        {
          bool = false;
        }
        else if (this.zzbo != ((zzl)paramObject).zzbo)
        {
          bool = false;
        }
        else if (!this.zzbf.equals(((zzl)paramObject).zzbf))
        {
          bool = false;
        }
        else
        {
          if (this.zzbg != null)
          {
            if (this.zzbg.equals(((zzl)paramObject).zzbg)) {}
          }
          else {
            while (((zzl)paramObject).zzbg != null)
            {
              bool = false;
              break;
            }
          }
          if (!this.zzbh.equals(((zzl)paramObject).zzbh))
          {
            bool = false;
          }
          else if (!this.zzbi.equals(((zzl)paramObject).zzbi))
          {
            bool = false;
          }
          else if (!this.zzbj.equals(((zzl)paramObject).zzbj))
          {
            bool = false;
          }
          else
          {
            if (this.zzbk != null)
            {
              if (this.zzbk.equals(((zzl)paramObject).zzbk)) {}
            }
            else {
              while (((zzl)paramObject).zzbk != null)
              {
                bool = false;
                break;
              }
            }
            if (this.packageName != null) {
              bool = this.packageName.equals(((zzl)paramObject).packageName);
            } else if (((zzl)paramObject).packageName != null) {
              bool = false;
            }
          }
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = this.id;
    int k = this.zzbf.hashCode();
    int m;
    int n;
    int i1;
    int i2;
    if (this.zzbg != null)
    {
      m = this.zzbg.hashCode();
      n = this.zzbh.hashCode();
      i1 = this.zzbi.hashCode();
      i2 = this.zzbj.hashCode();
      if (this.zzbk == null) {
        break label188;
      }
    }
    label188:
    for (int i3 = this.zzbk.hashCode();; i3 = 0)
    {
      int i4 = this.zzbl;
      int i5 = this.zzbm;
      int i6 = this.zzbn;
      int i7 = this.zzbo;
      if (this.packageName != null) {
        i = this.packageName.hashCode();
      }
      return (((((i3 + ((((m + ((j + 31) * 31 + k) * 31) * 31 + n) * 31 + i1) * 31 + i2) * 31) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i;
      m = 0;
      break;
    }
  }
  
  public final String toString()
  {
    int i = this.id;
    String str1 = this.zzbf;
    String str2 = this.zzbg;
    String str3 = this.zzbh;
    String str4 = this.zzbi;
    String str5 = this.zzbj;
    String str6 = this.zzbk;
    int j = this.zzbl;
    int k = this.zzbm;
    int m = this.zzbn;
    int n = this.zzbo;
    String str7 = this.packageName;
    return String.valueOf(str1).length() + 211 + String.valueOf(str2).length() + String.valueOf(str3).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + String.valueOf(str6).length() + String.valueOf(str7).length() + "AncsNotificationParcelable{, id=" + i + ", appId='" + str1 + '\'' + ", dateTime='" + str2 + '\'' + ", notificationText='" + str3 + '\'' + ", title='" + str4 + '\'' + ", subtitle='" + str5 + '\'' + ", displayName='" + str6 + '\'' + ", eventId=" + j + ", eventFlags=" + k + ", categoryId=" + m + ", categoryCount=" + n + ", packageName='" + str7 + '\'' + '}';
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.id);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzbf, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzbg, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzbh, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzbi, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzbj, false);
    if (this.zzbk == null) {}
    for (String str = this.zzbf;; str = this.zzbk)
    {
      SafeParcelWriter.writeString(paramParcel, 8, str, false);
      SafeParcelWriter.writeByte(paramParcel, 9, this.zzbl);
      SafeParcelWriter.writeByte(paramParcel, 10, this.zzbm);
      SafeParcelWriter.writeByte(paramParcel, 11, this.zzbn);
      SafeParcelWriter.writeByte(paramParcel, 12, this.zzbo);
      SafeParcelWriter.writeString(paramParcel, 13, this.packageName, false);
      SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */