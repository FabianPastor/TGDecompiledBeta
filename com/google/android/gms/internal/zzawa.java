package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class zzawa
  extends zza
  implements Comparable<zzawa>
{
  public static final Parcelable.Creator<zzawa> CREATOR = new zzawb();
  final int mVersionCode;
  public final int zzbzp;
  public final zzawc[] zzbzq;
  public final String[] zzbzr;
  public final Map<String, zzawc> zzbzs;
  
  zzawa(int paramInt1, int paramInt2, zzawc[] paramArrayOfzzawc, String[] paramArrayOfString)
  {
    this.mVersionCode = paramInt1;
    this.zzbzp = paramInt2;
    this.zzbzq = paramArrayOfzzawc;
    this.zzbzs = new TreeMap();
    paramInt2 = paramArrayOfzzawc.length;
    paramInt1 = 0;
    while (paramInt1 < paramInt2)
    {
      zzawc localzzawc = paramArrayOfzzawc[paramInt1];
      this.zzbzs.put(localzzawc.name, localzzawc);
      paramInt1 += 1;
    }
    this.zzbzr = paramArrayOfString;
    if (this.zzbzr != null) {
      Arrays.sort(this.zzbzr);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof zzawa))
      {
        paramObject = (zzawa)paramObject;
        bool1 = bool2;
        if (this.mVersionCode == ((zzawa)paramObject).mVersionCode)
        {
          bool1 = bool2;
          if (this.zzbzp == ((zzawa)paramObject).zzbzp)
          {
            bool1 = bool2;
            if (zzaa.equal(this.zzbzs, ((zzawa)paramObject).zzbzs))
            {
              bool1 = bool2;
              if (Arrays.equals(this.zzbzr, ((zzawa)paramObject).zzbzr)) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Configuration(");
    localStringBuilder.append(this.mVersionCode);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.zzbzp);
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    Object localObject = this.zzbzs.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localStringBuilder.append((zzawc)((Iterator)localObject).next());
      localStringBuilder.append(", ");
    }
    localStringBuilder.append(")");
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    if (this.zzbzr != null)
    {
      localObject = this.zzbzr;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localStringBuilder.append(localObject[i]);
        localStringBuilder.append(", ");
        i += 1;
      }
    }
    localStringBuilder.append("null");
    localStringBuilder.append(")");
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzawb.zza(this, paramParcel, paramInt);
  }
  
  public int zza(zzawa paramzzawa)
  {
    return this.zzbzp - paramzzawa.zzbzp;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzawa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */