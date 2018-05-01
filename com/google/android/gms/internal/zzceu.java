package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import java.util.Iterator;
import java.util.Set;

public final class zzceu
{
  final String mAppId;
  final String mName;
  private String mOrigin;
  final long zzayS;
  final long zzbpE;
  final zzcew zzbpF;
  
  zzceu(zzcgl paramzzcgl, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzbo.zzcF(paramString2);
    zzbo.zzcF(paramString3);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzayS = paramLong1;
    this.zzbpE = paramLong2;
    if ((this.zzbpE != 0L) && (this.zzbpE > this.zzayS)) {
      paramzzcgl.zzwF().zzyz().zzj("Event created with reverse previous/current timestamps. appId", zzcfl.zzdZ(paramString2));
    }
    this.zzbpF = zza(paramzzcgl, paramBundle);
  }
  
  private zzceu(zzcgl paramzzcgl, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, zzcew paramzzcew)
  {
    zzbo.zzcF(paramString2);
    zzbo.zzcF(paramString3);
    zzbo.zzu(paramzzcew);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzayS = paramLong1;
    this.zzbpE = paramLong2;
    if ((this.zzbpE != 0L) && (this.zzbpE > this.zzayS)) {
      paramzzcgl.zzwF().zzyz().zzj("Event created with reverse previous/current timestamps. appId", zzcfl.zzdZ(paramString2));
    }
    this.zzbpF = paramzzcew;
  }
  
  private static zzcew zza(zzcgl paramzzcgl, Bundle paramBundle)
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
          paramzzcgl.zzwF().zzyx().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzcgl.zzwB().zzk(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzcgl.zzwF().zzyz().zzj("Param value can't be null", paramzzcgl.zzwA().zzdX(str));
            localIterator.remove();
          }
          else
          {
            paramzzcgl.zzwB().zza(paramBundle, str, localObject);
          }
        }
      }
      return new zzcew(paramBundle);
    }
    return new zzcew(new Bundle());
  }
  
  public final String toString()
  {
    String str1 = this.mAppId;
    String str2 = this.mName;
    String str3 = String.valueOf(this.zzbpF);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "', name='" + str2 + "', params=" + str3 + "}";
  }
  
  final zzceu zza(zzcgl paramzzcgl, long paramLong)
  {
    return new zzceu(paramzzcgl, this.mOrigin, this.mAppId, this.mName, this.zzayS, paramLong, this.zzbpF);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzceu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */