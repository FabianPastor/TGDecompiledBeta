package com.google.android.gms.internal;

import java.util.Map.Entry;
import java.util.Set;

public final class zzaok
  extends zzaoh
{
  private final zzapf<String, zzaoh> ble = new zzapf();
  
  private zzaoh zzcm(Object paramObject)
  {
    if (paramObject == null) {
      return zzaoj.bld;
    }
    return new zzaon(paramObject);
  }
  
  public Set<Map.Entry<String, zzaoh>> entrySet()
  {
    return this.ble.entrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (((paramObject instanceof zzaok)) && (((zzaok)paramObject).ble.equals(this.ble)));
  }
  
  public boolean has(String paramString)
  {
    return this.ble.containsKey(paramString);
  }
  
  public int hashCode()
  {
    return this.ble.hashCode();
  }
  
  public void zza(String paramString, zzaoh paramzzaoh)
  {
    Object localObject = paramzzaoh;
    if (paramzzaoh == null) {
      localObject = zzaoj.bld;
    }
    this.ble.put(paramString, localObject);
  }
  
  public void zzb(String paramString, Boolean paramBoolean)
  {
    zza(paramString, zzcm(paramBoolean));
  }
  
  public void zzcb(String paramString1, String paramString2)
  {
    zza(paramString1, zzcm(paramString2));
  }
  
  public zzaoh zzuo(String paramString)
  {
    return (zzaoh)this.ble.get(paramString);
  }
  
  public zzaoe zzup(String paramString)
  {
    return (zzaoe)this.ble.get(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaok.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */