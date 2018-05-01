package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.List;

public final class zzbi
{
  private final Object zzddc;
  private final List<String> zzgbe;
  
  private zzbi(Object paramObject)
  {
    this.zzddc = zzbq.checkNotNull(paramObject);
    this.zzgbe = new ArrayList();
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(100).append(this.zzddc.getClass().getSimpleName()).append('{');
    int j = this.zzgbe.size();
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append((String)this.zzgbe.get(i));
      if (i < j - 1) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    return '}';
  }
  
  public final zzbi zzg(String paramString, Object paramObject)
  {
    List localList = this.zzgbe;
    paramString = (String)zzbq.checkNotNull(paramString);
    paramObject = String.valueOf(paramObject);
    localList.add(String.valueOf(paramString).length() + 1 + String.valueOf(paramObject).length() + paramString + "=" + (String)paramObject);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */