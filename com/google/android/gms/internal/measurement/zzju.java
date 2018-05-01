package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;

final class zzju
{
  final String name;
  final Object value;
  final String zzaek;
  final long zzaqu;
  final String zztd;
  
  zzju(String paramString1, String paramString2, String paramString3, long paramLong, Object paramObject)
  {
    Preconditions.checkNotEmpty(paramString1);
    Preconditions.checkNotEmpty(paramString3);
    Preconditions.checkNotNull(paramObject);
    this.zztd = paramString1;
    this.zzaek = paramString2;
    this.name = paramString3;
    this.zzaqu = paramLong;
    this.value = paramObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzju.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */