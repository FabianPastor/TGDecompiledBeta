package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class zzh
{
  final String aoq;
  final long aor;
  final EventParams aos;
  final String mName;
  final long tr;
  final String zzcpe;
  
  zzh(zzx paramzzx, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzac.zzhz(paramString2);
    zzac.zzhz(paramString3);
    this.zzcpe = paramString2;
    this.mName = paramString3;
    paramString2 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString2 = null;
    }
    this.aoq = paramString2;
    this.tr = paramLong1;
    this.aor = paramLong2;
    if ((this.aor != 0L) && (this.aor > this.tr)) {
      paramzzx.zzbvg().zzbwe().log("Event created with reverse previous/current timestamps");
    }
    this.aos = zza(paramzzx, paramBundle);
  }
  
  private zzh(zzx paramzzx, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, EventParams paramEventParams)
  {
    zzac.zzhz(paramString2);
    zzac.zzhz(paramString3);
    zzac.zzy(paramEventParams);
    this.zzcpe = paramString2;
    this.mName = paramString3;
    paramString2 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString2 = null;
    }
    this.aoq = paramString2;
    this.tr = paramLong1;
    this.aor = paramLong2;
    if ((this.aor != 0L) && (this.aor > this.tr)) {
      paramzzx.zzbvg().zzbwe().log("Event created with reverse previous/current timestamps");
    }
    this.aos = paramEventParams;
  }
  
  static EventParams zza(zzx paramzzx, Bundle paramBundle)
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
          paramzzx.zzbvg().zzbwc().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzx.zzbvc().zzl(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzx.zzbvg().zzbwe().zzj("Param value can't be null", str);
            localIterator.remove();
          }
          else
          {
            paramzzx.zzbvc().zza(paramBundle, str, localObject);
          }
        }
      }
      return new EventParams(paramBundle);
    }
    return new EventParams(new Bundle());
  }
  
  public String toString()
  {
    String str1 = this.zzcpe;
    String str2 = this.mName;
    String str3 = String.valueOf(this.aos);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "'" + ", name='" + str2 + "'" + ", params=" + str3 + "}";
  }
  
  zzh zza(zzx paramzzx, long paramLong)
  {
    return new zzh(paramzzx, this.aoq, this.zzcpe, this.mName, this.tr, paramLong, this.aos);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */