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

public class zzayx
  extends zza
  implements Comparable<zzayx>
{
  public static final Parcelable.Creator<zzayx> CREATOR = new zzayy();
  public final int zzbBB;
  public final zzayz[] zzbBC;
  public final String[] zzbBD;
  public final Map<String, zzayz> zzbBE;
  
  public zzayx(int paramInt, zzayz[] paramArrayOfzzayz, String[] paramArrayOfString)
  {
    this.zzbBB = paramInt;
    this.zzbBC = paramArrayOfzzayz;
    this.zzbBE = new TreeMap();
    int i = paramArrayOfzzayz.length;
    paramInt = 0;
    while (paramInt < i)
    {
      zzayz localzzayz = paramArrayOfzzayz[paramInt];
      this.zzbBE.put(localzzayz.name, localzzayz);
      paramInt += 1;
    }
    this.zzbBD = paramArrayOfString;
    if (this.zzbBD != null) {
      Arrays.sort(this.zzbBD);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof zzayx))
      {
        paramObject = (zzayx)paramObject;
        bool1 = bool2;
        if (this.zzbBB == ((zzayx)paramObject).zzbBB)
        {
          bool1 = bool2;
          if (zzaa.equal(this.zzbBE, ((zzayx)paramObject).zzbBE))
          {
            bool1 = bool2;
            if (Arrays.equals(this.zzbBD, ((zzayx)paramObject).zzbBD)) {
              bool1 = true;
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
    localStringBuilder.append(this.zzbBB);
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    Object localObject = this.zzbBE.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localStringBuilder.append((zzayz)((Iterator)localObject).next());
      localStringBuilder.append(", ");
    }
    localStringBuilder.append(")");
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    if (this.zzbBD != null)
    {
      localObject = this.zzbBD;
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
    zzayy.zza(this, paramParcel, paramInt);
  }
  
  public int zza(zzayx paramzzayx)
  {
    return this.zzbBB - paramzzayx.zzbBB;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */