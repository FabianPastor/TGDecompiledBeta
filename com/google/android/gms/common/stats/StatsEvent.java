package com.google.android.gms.common.stats;

import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public abstract class StatsEvent
  extends zza
  implements ReflectedParcelable
{
  public abstract int getEventType();
  
  public abstract long getTimeMillis();
  
  public String toString()
  {
    long l1 = getTimeMillis();
    String str1 = String.valueOf("\t");
    int i = getEventType();
    String str2 = String.valueOf("\t");
    long l2 = zzrV();
    String str3 = String.valueOf(zzrW());
    return String.valueOf(str1).length() + 51 + String.valueOf(str2).length() + String.valueOf(str3).length() + l1 + str1 + i + str2 + l2 + str3;
  }
  
  public abstract long zzrV();
  
  public abstract String zzrW();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/StatsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */