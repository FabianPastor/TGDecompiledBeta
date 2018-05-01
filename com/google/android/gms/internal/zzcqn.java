package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Base64;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class zzcqn
  extends zza
{
  public static final Parcelable.Creator<zzcqn> CREATOR = new zzcqt();
  private static byte[][] zzazi = new byte[0][];
  private static zzcqn zzbAc = new zzcqn("", null, zzazi, zzazi, zzazi, zzazi, null, null);
  private static final zzcqs zzbAl = new zzcqo();
  private static final zzcqs zzbAm = new zzcqp();
  private static final zzcqs zzbAn = new zzcqq();
  private static final zzcqs zzbAo = new zzcqr();
  private String zzbAd;
  private byte[] zzbAe;
  private byte[][] zzbAf;
  private byte[][] zzbAg;
  private byte[][] zzbAh;
  private byte[][] zzbAi;
  private int[] zzbAj;
  private byte[][] zzbAk;
  
  public zzcqn(String paramString, byte[] paramArrayOfByte, byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, byte[][] paramArrayOfByte3, byte[][] paramArrayOfByte4, int[] paramArrayOfInt, byte[][] paramArrayOfByte5)
  {
    this.zzbAd = paramString;
    this.zzbAe = paramArrayOfByte;
    this.zzbAf = paramArrayOfByte1;
    this.zzbAg = paramArrayOfByte2;
    this.zzbAh = paramArrayOfByte3;
    this.zzbAi = paramArrayOfByte4;
    this.zzbAj = paramArrayOfInt;
    this.zzbAk = paramArrayOfByte5;
  }
  
  private static void zza(StringBuilder paramStringBuilder, String paramString, int[] paramArrayOfInt)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append("=");
    if (paramArrayOfInt == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    paramStringBuilder.append("(");
    int k = paramArrayOfInt.length;
    int j = 1;
    int i = 0;
    while (i < k)
    {
      int m = paramArrayOfInt[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      paramStringBuilder.append(m);
      i += 1;
      j = 0;
    }
    paramStringBuilder.append(")");
  }
  
  private static void zza(StringBuilder paramStringBuilder, String paramString, byte[][] paramArrayOfByte)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append("=");
    if (paramArrayOfByte == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    paramStringBuilder.append("(");
    int k = paramArrayOfByte.length;
    int j = 1;
    int i = 0;
    while (i < k)
    {
      paramString = paramArrayOfByte[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      paramStringBuilder.append("'");
      paramStringBuilder.append(Base64.encodeToString(paramString, 3));
      paramStringBuilder.append("'");
      i += 1;
      j = 0;
    }
    paramStringBuilder.append(")");
  }
  
  private static List<String> zzb(byte[][] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfByte.length);
    int j = paramArrayOfByte.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Base64.encodeToString(paramArrayOfByte[i], 3));
      i += 1;
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  private static List<Integer> zzc(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfInt.length);
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof zzcqn))
    {
      paramObject = (zzcqn)paramObject;
      bool1 = bool2;
      if (zzcqu.equals(this.zzbAd, ((zzcqn)paramObject).zzbAd))
      {
        bool1 = bool2;
        if (Arrays.equals(this.zzbAe, ((zzcqn)paramObject).zzbAe))
        {
          bool1 = bool2;
          if (zzcqu.equals(zzb(this.zzbAf), zzb(((zzcqn)paramObject).zzbAf)))
          {
            bool1 = bool2;
            if (zzcqu.equals(zzb(this.zzbAg), zzb(((zzcqn)paramObject).zzbAg)))
            {
              bool1 = bool2;
              if (zzcqu.equals(zzb(this.zzbAh), zzb(((zzcqn)paramObject).zzbAh)))
              {
                bool1 = bool2;
                if (zzcqu.equals(zzb(this.zzbAi), zzb(((zzcqn)paramObject).zzbAi)))
                {
                  bool1 = bool2;
                  if (zzcqu.equals(zzc(this.zzbAj), zzc(((zzcqn)paramObject).zzbAj)))
                  {
                    bool1 = bool2;
                    if (zzcqu.equals(zzb(this.zzbAk), zzb(((zzcqn)paramObject).zzbAk))) {
                      bool1 = true;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ExperimentTokens");
    localStringBuilder.append("(");
    Object localObject;
    if (this.zzbAd == null)
    {
      localObject = "null";
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(", ");
      localObject = this.zzbAe;
      localStringBuilder.append("direct");
      localStringBuilder.append("=");
      if (localObject != null) {
        break label253;
      }
      localStringBuilder.append("null");
    }
    for (;;)
    {
      localStringBuilder.append(", ");
      zza(localStringBuilder, "GAIA", this.zzbAf);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "PSEUDO", this.zzbAg);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "ALWAYS", this.zzbAh);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "OTHER", this.zzbAi);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "weak", this.zzbAj);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "directs", this.zzbAk);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
      localObject = String.valueOf("'");
      String str1 = this.zzbAd;
      String str2 = String.valueOf("'");
      localObject = String.valueOf(localObject).length() + String.valueOf(str1).length() + String.valueOf(str2).length() + (String)localObject + str1 + str2;
      break;
      label253:
      localStringBuilder.append("'");
      localStringBuilder.append(Base64.encodeToString((byte[])localObject, 3));
      localStringBuilder.append("'");
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbAd, false);
    zzd.zza(paramParcel, 3, this.zzbAe, false);
    zzd.zza(paramParcel, 4, this.zzbAf, false);
    zzd.zza(paramParcel, 5, this.zzbAg, false);
    zzd.zza(paramParcel, 6, this.zzbAh, false);
    zzd.zza(paramParcel, 7, this.zzbAi, false);
    zzd.zza(paramParcel, 8, this.zzbAj, false);
    zzd.zza(paramParcel, 9, this.zzbAk, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcqn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */