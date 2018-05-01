package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class zzawc
  extends zza
  implements Comparable<zzawc>
{
  public static final Parcelable.Creator<zzawc> CREATOR = new zzawd();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final zza zzbzx = new zza();
  final int mVersionCode;
  public final String name;
  final String zzaFy;
  final boolean zzbgG;
  final double zzbgI;
  final long zzbzt;
  final byte[] zzbzu;
  public final int zzbzv;
  public final int zzbzw;
  
  zzawc(int paramInt1, String paramString1, long paramLong, boolean paramBoolean, double paramDouble, String paramString2, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    this.mVersionCode = paramInt1;
    this.name = paramString1;
    this.zzbzt = paramLong;
    this.zzbgG = paramBoolean;
    this.zzbgI = paramDouble;
    this.zzaFy = paramString2;
    this.zzbzu = paramArrayOfByte;
    this.zzbzv = paramInt2;
    this.zzbzw = paramInt3;
  }
  
  private static int compare(byte paramByte1, byte paramByte2)
  {
    return paramByte1 - paramByte2;
  }
  
  private static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2) {
      return -1;
    }
    if (paramInt1 == paramInt2) {
      return 0;
    }
    return 1;
  }
  
  private static int compare(long paramLong1, long paramLong2)
  {
    if (paramLong1 < paramLong2) {
      return -1;
    }
    if (paramLong1 == paramLong2) {
      return 0;
    }
    return 1;
  }
  
  private static int compare(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return 0;
    }
    if (paramString1 == null) {
      return -1;
    }
    if (paramString2 == null) {
      return 1;
    }
    return paramString1.compareTo(paramString2);
  }
  
  private static int compare(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == paramBoolean2) {
      return 0;
    }
    if (paramBoolean1) {
      return 1;
    }
    return -1;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if ((paramObject != null) && ((paramObject instanceof zzawc)))
    {
      paramObject = (zzawc)paramObject;
      if ((this.mVersionCode != ((zzawc)paramObject).mVersionCode) || (!zzaa.equal(this.name, ((zzawc)paramObject).name)) || (this.zzbzv != ((zzawc)paramObject).zzbzv) || (this.zzbzw != ((zzawc)paramObject).zzbzw)) {
        bool = false;
      }
      do
      {
        do
        {
          do
          {
            return bool;
            switch (this.zzbzv)
            {
            default: 
              int i = this.zzbzv;
              throw new AssertionError(31 + "Invalid enum value: " + i);
            }
          } while (this.zzbzt == ((zzawc)paramObject).zzbzt);
          return false;
        } while (this.zzbgG == ((zzawc)paramObject).zzbgG);
        return false;
      } while (this.zzbgI == ((zzawc)paramObject).zzbgI);
      return false;
      return zzaa.equal(this.zzaFy, ((zzawc)paramObject).zzaFy);
      return Arrays.equals(this.zzbzu, ((zzawc)paramObject).zzbzu);
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    zza(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzawd.zza(this, paramParcel, paramInt);
  }
  
  public int zza(zzawc paramzzawc)
  {
    int j = 0;
    int i = 0;
    int k = this.name.compareTo(paramzzawc.name);
    if (k != 0) {
      i = k;
    }
    do
    {
      return i;
      k = compare(this.zzbzv, paramzzawc.zzbzv);
      if (k != 0) {
        return k;
      }
      switch (this.zzbzv)
      {
      default: 
        i = this.zzbzv;
        throw new AssertionError(31 + "Invalid enum value: " + i);
      case 1: 
        return compare(this.zzbzt, paramzzawc.zzbzt);
      case 2: 
        return compare(this.zzbgG, paramzzawc.zzbgG);
      case 3: 
        return Double.compare(this.zzbgI, paramzzawc.zzbgI);
      case 4: 
        return compare(this.zzaFy, paramzzawc.zzaFy);
      }
    } while (this.zzbzu == paramzzawc.zzbzu);
    if (this.zzbzu == null) {
      return -1;
    }
    i = j;
    if (paramzzawc.zzbzu == null) {
      return 1;
    }
    do
    {
      i += 1;
      if (i >= Math.min(this.zzbzu.length, paramzzawc.zzbzu.length)) {
        break;
      }
      j = compare(this.zzbzu[i], paramzzawc.zzbzu[i]);
    } while (j == 0);
    return j;
    return compare(this.zzbzu.length, paramzzawc.zzbzu.length);
  }
  
  public String zza(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Flag(");
    paramStringBuilder.append(this.mVersionCode);
    paramStringBuilder.append(", ");
    paramStringBuilder.append(this.name);
    paramStringBuilder.append(", ");
    switch (this.zzbzv)
    {
    default: 
      paramStringBuilder = this.name;
      int i = this.zzbzv;
      throw new AssertionError(String.valueOf(paramStringBuilder).length() + 27 + "Invalid type: " + paramStringBuilder + ", " + i);
    case 1: 
      paramStringBuilder.append(this.zzbzt);
    }
    for (;;)
    {
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.zzbzv);
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.zzbzw);
      paramStringBuilder.append(")");
      return paramStringBuilder.toString();
      paramStringBuilder.append(this.zzbgG);
      continue;
      paramStringBuilder.append(this.zzbgI);
      continue;
      paramStringBuilder.append("'");
      paramStringBuilder.append(this.zzaFy);
      paramStringBuilder.append("'");
      continue;
      if (this.zzbzu == null)
      {
        paramStringBuilder.append("null");
      }
      else
      {
        paramStringBuilder.append("'");
        paramStringBuilder.append(new String(this.zzbzu, UTF_8));
        paramStringBuilder.append("'");
      }
    }
  }
  
  public static class zza
    implements Comparator<zzawc>
  {
    public int zza(zzawc paramzzawc1, zzawc paramzzawc2)
    {
      if (paramzzawc1.zzbzw == paramzzawc2.zzbzw) {
        return paramzzawc1.name.compareTo(paramzzawc2.name);
      }
      return paramzzawc1.zzbzw - paramzzawc2.zzbzw;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzawc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */