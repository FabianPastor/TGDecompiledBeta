package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Configuration
  extends AbstractSafeParcelable
  implements Comparable<Configuration>
{
  public static final Parcelable.Creator<Configuration> CREATOR = new zza();
  public final int aAs;
  public final Flag[] aAt;
  public final String[] aAu;
  public final Map<String, Flag> aAv;
  final int mVersionCode;
  
  Configuration(int paramInt1, int paramInt2, Flag[] paramArrayOfFlag, String[] paramArrayOfString)
  {
    this.mVersionCode = paramInt1;
    this.aAs = paramInt2;
    this.aAt = paramArrayOfFlag;
    this.aAv = new TreeMap();
    paramInt2 = paramArrayOfFlag.length;
    paramInt1 = 0;
    while (paramInt1 < paramInt2)
    {
      Flag localFlag = paramArrayOfFlag[paramInt1];
      this.aAv.put(localFlag.name, localFlag);
      paramInt1 += 1;
    }
    this.aAu = paramArrayOfString;
    if (this.aAu != null) {
      Arrays.sort(this.aAu);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof Configuration))
      {
        paramObject = (Configuration)paramObject;
        bool1 = bool2;
        if (this.mVersionCode == ((Configuration)paramObject).mVersionCode)
        {
          bool1 = bool2;
          if (this.aAs == ((Configuration)paramObject).aAs)
          {
            bool1 = bool2;
            if (zzz.equal(this.aAv, ((Configuration)paramObject).aAv))
            {
              bool1 = bool2;
              if (Arrays.equals(this.aAu, ((Configuration)paramObject).aAu)) {
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
    localStringBuilder.append(this.aAs);
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    Object localObject = this.aAv.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localStringBuilder.append((Flag)((Iterator)localObject).next());
      localStringBuilder.append(", ");
    }
    localStringBuilder.append(")");
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    if (this.aAu != null)
    {
      localObject = this.aAu;
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
    zza.zza(this, paramParcel, paramInt);
  }
  
  public int zza(Configuration paramConfiguration)
  {
    return this.aAs - paramConfiguration.aAs;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/phenotype/Configuration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */