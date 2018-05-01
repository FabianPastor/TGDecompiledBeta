package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class Flag
  extends AbstractSafeParcelable
  implements Comparable<Flag>
{
  public static final Parcelable.Creator<Flag> CREATOR = new zzb();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final zza aAA = new zza();
  final String Fe;
  final long aAw;
  final byte[] aAx;
  public final int aAy;
  public final int aAz;
  final boolean ahI;
  final double ahK;
  final int mVersionCode;
  public final String name;
  
  Flag(int paramInt1, String paramString1, long paramLong, boolean paramBoolean, double paramDouble, String paramString2, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    this.mVersionCode = paramInt1;
    this.name = paramString1;
    this.aAw = paramLong;
    this.ahI = paramBoolean;
    this.ahK = paramDouble;
    this.Fe = paramString2;
    this.aAx = paramArrayOfByte;
    this.aAy = paramInt2;
    this.aAz = paramInt3;
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
    if ((paramObject != null) && ((paramObject instanceof Flag)))
    {
      paramObject = (Flag)paramObject;
      if ((this.mVersionCode != ((Flag)paramObject).mVersionCode) || (!zzz.equal(this.name, ((Flag)paramObject).name)) || (this.aAy != ((Flag)paramObject).aAy) || (this.aAz != ((Flag)paramObject).aAz)) {
        bool = false;
      }
      do
      {
        do
        {
          do
          {
            return bool;
            switch (this.aAy)
            {
            default: 
              int i = this.aAy;
              throw new AssertionError(31 + "Invalid enum value: " + i);
            }
          } while (this.aAw == ((Flag)paramObject).aAw);
          return false;
        } while (this.ahI == ((Flag)paramObject).ahI);
        return false;
      } while (this.ahK == ((Flag)paramObject).ahK);
      return false;
      return zzz.equal(this.Fe, ((Flag)paramObject).Fe);
      return Arrays.equals(this.aAx, ((Flag)paramObject).aAx);
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
    zzb.zza(this, paramParcel, paramInt);
  }
  
  public int zza(Flag paramFlag)
  {
    int j = 0;
    int i = 0;
    int k = this.name.compareTo(paramFlag.name);
    if (k != 0) {
      i = k;
    }
    do
    {
      return i;
      k = compare(this.aAy, paramFlag.aAy);
      if (k != 0) {
        return k;
      }
      switch (this.aAy)
      {
      default: 
        i = this.aAy;
        throw new AssertionError(31 + "Invalid enum value: " + i);
      case 1: 
        return compare(this.aAw, paramFlag.aAw);
      case 2: 
        return compare(this.ahI, paramFlag.ahI);
      case 3: 
        return Double.compare(this.ahK, paramFlag.ahK);
      case 4: 
        return compare(this.Fe, paramFlag.Fe);
      }
    } while (this.aAx == paramFlag.aAx);
    if (this.aAx == null) {
      return -1;
    }
    i = j;
    if (paramFlag.aAx == null) {
      return 1;
    }
    do
    {
      i += 1;
      if (i >= Math.min(this.aAx.length, paramFlag.aAx.length)) {
        break;
      }
      j = compare(this.aAx[i], paramFlag.aAx[i]);
    } while (j == 0);
    return j;
    return compare(this.aAx.length, paramFlag.aAx.length);
  }
  
  public String zza(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Flag(");
    paramStringBuilder.append(this.mVersionCode);
    paramStringBuilder.append(", ");
    paramStringBuilder.append(this.name);
    paramStringBuilder.append(", ");
    switch (this.aAy)
    {
    default: 
      paramStringBuilder = this.name;
      int i = this.aAy;
      throw new AssertionError(String.valueOf(paramStringBuilder).length() + 27 + "Invalid type: " + paramStringBuilder + ", " + i);
    case 1: 
      paramStringBuilder.append(this.aAw);
    }
    for (;;)
    {
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.aAy);
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.aAz);
      paramStringBuilder.append(")");
      return paramStringBuilder.toString();
      paramStringBuilder.append(this.ahI);
      continue;
      paramStringBuilder.append(this.ahK);
      continue;
      paramStringBuilder.append("'");
      paramStringBuilder.append(this.Fe);
      paramStringBuilder.append("'");
      continue;
      if (this.aAx == null)
      {
        paramStringBuilder.append("null");
      }
      else
      {
        paramStringBuilder.append("'");
        paramStringBuilder.append(new String(this.aAx, UTF_8));
        paramStringBuilder.append("'");
      }
    }
  }
  
  public static class zza
    implements Comparator<Flag>
  {
    public int zza(Flag paramFlag1, Flag paramFlag2)
    {
      if (paramFlag1.aAz == paramFlag2.aAz) {
        return paramFlag1.name.compareTo(paramFlag2.name);
      }
      return paramFlag1.aAz - paramFlag2.aAz;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/phenotype/Flag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */