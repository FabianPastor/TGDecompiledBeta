package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

final class zzclp
{
  final String mAppId;
  final String mName;
  final String mOrigin;
  final Object mValue;
  final long zzjjm;
  
  zzclp(String paramString1, String paramString2, String paramString3, long paramLong, Object paramObject)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString3);
    zzbq.checkNotNull(paramObject);
    this.mAppId = paramString1;
    this.mOrigin = paramString2;
    this.mName = paramString3;
    this.zzjjm = paramLong;
    this.mValue = paramObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */