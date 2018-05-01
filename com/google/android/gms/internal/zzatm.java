package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class zzatm
{
  final String mAppId;
  final String mName;
  final String mOrigin;
  final long zzaxb;
  final zzato zzbrA;
  final long zzbrz;
  
  zzatm(zzaue paramzzaue, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzac.zzdr(paramString2);
    zzac.zzdr(paramString3);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzaxb = paramLong1;
    this.zzbrz = paramLong2;
    if ((this.zzbrz != 0L) && (this.zzbrz > this.zzaxb)) {
      paramzzaue.zzKl().zzMb().zzj("Event created with reverse previous/current timestamps. appId", zzatx.zzfE(paramString2));
    }
    this.zzbrA = zza(paramzzaue, paramBundle);
  }
  
  private zzatm(zzaue paramzzaue, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, zzato paramzzato)
  {
    zzac.zzdr(paramString2);
    zzac.zzdr(paramString3);
    zzac.zzw(paramzzato);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzaxb = paramLong1;
    this.zzbrz = paramLong2;
    if ((this.zzbrz != 0L) && (this.zzbrz > this.zzaxb)) {
      paramzzaue.zzKl().zzMb().zzj("Event created with reverse previous/current timestamps. appId", zzatx.zzfE(paramString2));
    }
    this.zzbrA = paramzzato;
  }
  
  static zzato zza(zzaue paramzzaue, Bundle paramBundle)
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
          paramzzaue.zzKl().zzLZ().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzaue.zzKh().zzk(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzaue.zzKl().zzMb().zzj("Param value can't be null", str);
            localIterator.remove();
          }
          else
          {
            paramzzaue.zzKh().zza(paramBundle, str, localObject);
          }
        }
      }
      return new zzato(paramBundle);
    }
    return new zzato(new Bundle());
  }
  
  public String toString()
  {
    String str1 = this.mAppId;
    String str2 = this.mName;
    String str3 = String.valueOf(this.zzbrA);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "'" + ", name='" + str2 + "'" + ", params=" + str3 + "}";
  }
  
  zzatm zza(zzaue paramzzaue, long paramLong)
  {
    return new zzatm(paramzzaue, this.mOrigin, this.mAppId, this.mName, this.zzaxb, paramLong, this.zzbrA);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */