package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class zzayz
  extends zza
  implements Comparable<zzayz>
{
  public static final Parcelable.Creator<zzayz> CREATOR = new zzaza();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final zza zzbBJ = new zza();
  public final String name;
  final String zzaGV;
  final long zzbBF;
  final byte[] zzbBG;
  public final int zzbBH;
  public final int zzbBI;
  final boolean zzbhn;
  final double zzbhp;
  
  public zzayz(String paramString1, long paramLong, boolean paramBoolean, double paramDouble, String paramString2, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.name = paramString1;
    this.zzbBF = paramLong;
    this.zzbhn = paramBoolean;
    this.zzbhp = paramDouble;
    this.zzaGV = paramString2;
    this.zzbBG = paramArrayOfByte;
    this.zzbBH = paramInt1;
    this.zzbBI = paramInt2;
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
    if ((paramObject != null) && ((paramObject instanceof zzayz)))
    {
      paramObject = (zzayz)paramObject;
      if ((!zzaa.equal(this.name, ((zzayz)paramObject).name)) || (this.zzbBH != ((zzayz)paramObject).zzbBH) || (this.zzbBI != ((zzayz)paramObject).zzbBI)) {
        bool = false;
      }
      do
      {
        do
        {
          do
          {
            return bool;
            switch (this.zzbBH)
            {
            default: 
              int i = this.zzbBH;
              throw new AssertionError(31 + "Invalid enum value: " + i);
            }
          } while (this.zzbBF == ((zzayz)paramObject).zzbBF);
          return false;
        } while (this.zzbhn == ((zzayz)paramObject).zzbhn);
        return false;
      } while (this.zzbhp == ((zzayz)paramObject).zzbhp);
      return false;
      return zzaa.equal(this.zzaGV, ((zzayz)paramObject).zzaGV);
      return Arrays.equals(this.zzbBG, ((zzayz)paramObject).zzbBG);
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
    zzaza.zza(this, paramParcel, paramInt);
  }
  
  public int zza(zzayz paramzzayz)
  {
    int j = 0;
    int i = 0;
    int k = this.name.compareTo(paramzzayz.name);
    if (k != 0) {
      i = k;
    }
    do
    {
      return i;
      k = compare(this.zzbBH, paramzzayz.zzbBH);
      if (k != 0) {
        return k;
      }
      switch (this.zzbBH)
      {
      default: 
        i = this.zzbBH;
        throw new AssertionError(31 + "Invalid enum value: " + i);
      case 1: 
        return compare(this.zzbBF, paramzzayz.zzbBF);
      case 2: 
        return compare(this.zzbhn, paramzzayz.zzbhn);
      case 3: 
        return Double.compare(this.zzbhp, paramzzayz.zzbhp);
      case 4: 
        return compare(this.zzaGV, paramzzayz.zzaGV);
      }
    } while (this.zzbBG == paramzzayz.zzbBG);
    if (this.zzbBG == null) {
      return -1;
    }
    i = j;
    if (paramzzayz.zzbBG == null) {
      return 1;
    }
    do
    {
      i += 1;
      if (i >= Math.min(this.zzbBG.length, paramzzayz.zzbBG.length)) {
        break;
      }
      j = compare(this.zzbBG[i], paramzzayz.zzbBG[i]);
    } while (j == 0);
    return j;
    return compare(this.zzbBG.length, paramzzayz.zzbBG.length);
  }
  
  public String zza(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Flag(");
    paramStringBuilder.append(this.name);
    paramStringBuilder.append(", ");
    switch (this.zzbBH)
    {
    default: 
      paramStringBuilder = this.name;
      int i = this.zzbBH;
      throw new AssertionError(String.valueOf(paramStringBuilder).length() + 27 + "Invalid type: " + paramStringBuilder + ", " + i);
    case 1: 
      paramStringBuilder.append(this.zzbBF);
    }
    for (;;)
    {
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.zzbBH);
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.zzbBI);
      paramStringBuilder.append(")");
      return paramStringBuilder.toString();
      paramStringBuilder.append(this.zzbhn);
      continue;
      paramStringBuilder.append(this.zzbhp);
      continue;
      paramStringBuilder.append("'");
      paramStringBuilder.append(this.zzaGV);
      paramStringBuilder.append("'");
      continue;
      if (this.zzbBG == null)
      {
        paramStringBuilder.append("null");
      }
      else
      {
        paramStringBuilder.append("'");
        paramStringBuilder.append(new String(this.zzbBG, UTF_8));
        paramStringBuilder.append("'");
      }
    }
  }
  
  public static class zza
    implements Comparator<zzayz>
  {
    public int zza(zzayz paramzzayz1, zzayz paramzzayz2)
    {
      if (paramzzayz1.zzbBI == paramzzayz2.zzbBI) {
        return paramzzayz1.name.compareTo(paramzzayz2.name);
      }
      return paramzzayz1.zzbBI - paramzzayz2.zzbBI;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */