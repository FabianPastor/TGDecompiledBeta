package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class Flag
  extends AbstractSafeParcelable
  implements Comparable<Flag>
{
  public static final Parcelable.Creator<Flag> CREATOR = new zzb();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final zza axt = new zza();
  final String Dr;
  final double afA;
  final boolean afy;
  final long axp;
  final byte[] axq;
  public final int axr;
  public final int axs;
  final int mVersionCode;
  public final String name;
  
  Flag(int paramInt1, String paramString1, long paramLong, boolean paramBoolean, double paramDouble, String paramString2, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    this.mVersionCode = paramInt1;
    this.name = paramString1;
    this.axp = paramLong;
    this.afy = paramBoolean;
    this.afA = paramDouble;
    this.Dr = paramString2;
    this.axq = paramArrayOfByte;
    this.axr = paramInt2;
    this.axs = paramInt3;
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
      if ((this.mVersionCode != ((Flag)paramObject).mVersionCode) || (!zzab.equal(this.name, ((Flag)paramObject).name)) || (this.axr != ((Flag)paramObject).axr) || (this.axs != ((Flag)paramObject).axs)) {
        bool = false;
      }
      do
      {
        do
        {
          do
          {
            return bool;
            switch (this.axr)
            {
            default: 
              int i = this.axr;
              throw new AssertionError(31 + "Invalid enum value: " + i);
            }
          } while (this.axp == ((Flag)paramObject).axp);
          return false;
        } while (this.afy == ((Flag)paramObject).afy);
        return false;
      } while (this.afA == ((Flag)paramObject).afA);
      return false;
      return zzab.equal(this.Dr, ((Flag)paramObject).Dr);
      return Arrays.equals(this.axq, ((Flag)paramObject).axq);
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Flag(");
    localStringBuilder.append(this.mVersionCode);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.name);
    localStringBuilder.append(", ");
    switch (this.axr)
    {
    default: 
      int i = this.axr;
      throw new AssertionError(31 + "Invalid enum value: " + i);
    case 1: 
      localStringBuilder.append(this.axp);
    }
    for (;;)
    {
      localStringBuilder.append(", ");
      localStringBuilder.append(this.axr);
      localStringBuilder.append(", ");
      localStringBuilder.append(this.axs);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
      localStringBuilder.append(this.afy);
      continue;
      localStringBuilder.append(this.afA);
      continue;
      localStringBuilder.append("'");
      localStringBuilder.append(this.Dr);
      localStringBuilder.append("'");
      continue;
      if (this.axq == null)
      {
        localStringBuilder.append("null");
      }
      else
      {
        localStringBuilder.append("'");
        localStringBuilder.append(new String(this.axq, UTF_8));
        localStringBuilder.append("'");
      }
    }
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
      k = compare(this.axr, paramFlag.axr);
      if (k != 0) {
        return k;
      }
      switch (this.axr)
      {
      default: 
        i = this.axr;
        throw new AssertionError(31 + "Invalid enum value: " + i);
      case 1: 
        return compare(this.axp, paramFlag.axp);
      case 2: 
        return compare(this.afy, paramFlag.afy);
      case 3: 
        return Double.compare(this.afA, paramFlag.afA);
      case 4: 
        return compare(this.Dr, paramFlag.Dr);
      }
    } while (this.axq == paramFlag.axq);
    if (this.axq == null) {
      return -1;
    }
    i = j;
    if (paramFlag.axq == null) {
      return 1;
    }
    do
    {
      i += 1;
      if (i >= Math.min(this.axq.length, paramFlag.axq.length)) {
        break;
      }
      j = compare(this.axq[i], paramFlag.axq[i]);
    } while (j == 0);
    return j;
    return compare(this.axq.length, paramFlag.axq.length);
  }
  
  public static class zza
    implements Comparator<Flag>
  {
    public int zza(Flag paramFlag1, Flag paramFlag2)
    {
      if (paramFlag1.axs == paramFlag2.axs) {
        return paramFlag1.name.compareTo(paramFlag2.name);
      }
      return paramFlag1.axs - paramFlag2.axs;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/phenotype/Flag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */