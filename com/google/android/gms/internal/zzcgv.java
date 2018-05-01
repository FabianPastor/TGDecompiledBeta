package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import java.util.Iterator;
import java.util.Set;

public final class zzcgv
{
  final String mAppId;
  final String mName;
  private String mOrigin;
  final long zzfij;
  final long zzizi;
  final zzcgx zzizj;
  
  zzcgv(zzcim paramzzcim, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzbq.zzgm(paramString2);
    zzbq.zzgm(paramString3);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzfij = paramLong1;
    this.zzizi = paramLong2;
    if ((this.zzizi != 0L) && (this.zzizi > this.zzfij)) {
      paramzzcim.zzawy().zzazf().zzj("Event created with reverse previous/current timestamps. appId", zzchm.zzjk(paramString2));
    }
    this.zzizj = zza(paramzzcim, paramBundle);
  }
  
  private zzcgv(zzcim paramzzcim, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, zzcgx paramzzcgx)
  {
    zzbq.zzgm(paramString2);
    zzbq.zzgm(paramString3);
    zzbq.checkNotNull(paramzzcgx);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzfij = paramLong1;
    this.zzizi = paramLong2;
    if ((this.zzizi != 0L) && (this.zzizi > this.zzfij)) {
      paramzzcim.zzawy().zzazf().zzj("Event created with reverse previous/current timestamps. appId", zzchm.zzjk(paramString2));
    }
    this.zzizj = paramzzcgx;
  }
  
  private static zzcgx zza(zzcim paramzzcim, Bundle paramBundle)
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
          paramzzcim.zzawy().zzazd().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzcim.zzawu().zzk(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzcim.zzawy().zzazf().zzj("Param value can't be null", paramzzcim.zzawt().zzji(str));
            localIterator.remove();
          }
          else
          {
            paramzzcim.zzawu().zza(paramBundle, str, localObject);
          }
        }
      }
      return new zzcgx(paramBundle);
    }
    return new zzcgx(new Bundle());
  }
  
  public final String toString()
  {
    String str1 = this.mAppId;
    String str2 = this.mName;
    String str3 = String.valueOf(this.zzizj);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "', name='" + str2 + "', params=" + str3 + "}";
  }
  
  final zzcgv zza(zzcim paramzzcim, long paramLong)
  {
    return new zzcgv(paramzzcim, this.mOrigin, this.mAppId, this.mName, this.zzfij, paramLong, this.zzizj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */