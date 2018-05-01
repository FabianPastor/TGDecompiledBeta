package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class zzasx
{
  final String mName;
  final String zzVQ;
  final long zzavX;
  final String zzbqG;
  final long zzbqH;
  final zzasz zzbqI;
  
  zzasx(zzatp paramzzatp, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzac.zzdv(paramString2);
    zzac.zzdv(paramString3);
    this.zzVQ = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.zzbqG = paramString3;
    this.zzavX = paramLong1;
    this.zzbqH = paramLong2;
    if ((this.zzbqH != 0L) && (this.zzbqH > this.zzavX)) {
      paramzzatp.zzJt().zzLc().zzj("Event created with reverse previous/current timestamps. appId", zzati.zzfI(paramString2));
    }
    this.zzbqI = zza(paramzzatp, paramBundle);
  }
  
  private zzasx(zzatp paramzzatp, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, zzasz paramzzasz)
  {
    zzac.zzdv(paramString2);
    zzac.zzdv(paramString3);
    zzac.zzw(paramzzasz);
    this.zzVQ = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.zzbqG = paramString3;
    this.zzavX = paramLong1;
    this.zzbqH = paramLong2;
    if ((this.zzbqH != 0L) && (this.zzbqH > this.zzavX)) {
      paramzzatp.zzJt().zzLc().zzj("Event created with reverse previous/current timestamps. appId", zzati.zzfI(paramString2));
    }
    this.zzbqI = paramzzasz;
  }
  
  static zzasz zza(zzatp paramzzatp, Bundle paramBundle)
  {
    if ((paramBundle != null) && (!paramBundle.isEmpty()))
    {
      paramBundle = new Bundle(paramBundle);
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str == null)
        {
          paramzzatp.zzJt().zzLa().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzatp.zzJp().zzl(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzatp.zzJt().zzLc().zzj("Param value can't be null", str);
            localIterator.remove();
          }
          else
          {
            paramzzatp.zzJp().zza(paramBundle, str, localObject);
          }
        }
      }
      return new zzasz(paramBundle);
    }
    return new zzasz(new Bundle());
  }
  
  public String toString()
  {
    String str1 = this.zzVQ;
    String str2 = this.mName;
    String str3 = String.valueOf(this.zzbqI);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "'" + ", name='" + str2 + "'" + ", params=" + str3 + "}";
  }
  
  zzasx zza(zzatp paramzzatp, long paramLong)
  {
    return new zzasx(paramzzatp, this.zzbqG, this.zzVQ, this.mName, this.zzavX, paramLong, this.zzbqI);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */