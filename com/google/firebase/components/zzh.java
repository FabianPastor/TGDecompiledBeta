package com.google.firebase.components;

import java.util.HashSet;
import java.util.Set;

final class zzh
{
  private final Component<?> zzaj;
  private final Set<zzh> zzak = new HashSet();
  private final Set<zzh> zzal = new HashSet();
  
  zzh(Component<?> paramComponent)
  {
    this.zzaj = paramComponent;
  }
  
  final void zza(zzh paramzzh)
  {
    this.zzak.add(paramzzh);
  }
  
  final void zzb(zzh paramzzh)
  {
    this.zzal.add(paramzzh);
  }
  
  final void zzc(zzh paramzzh)
  {
    this.zzal.remove(paramzzh);
  }
  
  final Set<zzh> zzf()
  {
    return this.zzak;
  }
  
  final Component<?> zzk()
  {
    return this.zzaj;
  }
  
  final boolean zzl()
  {
    return this.zzal.isEmpty();
  }
  
  final boolean zzm()
  {
    return this.zzak.isEmpty();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */