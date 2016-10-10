package com.google.android.gms.internal;

public final class zzapn
  implements zzaou
{
  private final zzapb bkM;
  
  public zzapn(zzapb paramzzapb)
  {
    this.bkM = paramzzapb;
  }
  
  static zzaot<?> zza(zzapb paramzzapb, zzaob paramzzaob, zzapx<?> paramzzapx, zzaov paramzzaov)
  {
    paramzzaov = paramzzaov.value();
    if (zzaot.class.isAssignableFrom(paramzzaov)) {
      return (zzaot)paramzzapb.zzb(zzapx.zzr(paramzzaov)).bg();
    }
    if (zzaou.class.isAssignableFrom(paramzzaov)) {
      return ((zzaou)paramzzapb.zzb(zzapx.zzr(paramzzaov)).bg()).zza(paramzzaob, paramzzapx);
    }
    throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
  }
  
  public <T> zzaot<T> zza(zzaob paramzzaob, zzapx<T> paramzzapx)
  {
    zzaov localzzaov = (zzaov)paramzzapx.by().getAnnotation(zzaov.class);
    if (localzzaov == null) {
      return null;
    }
    return zza(this.bkM, paramzzaob, paramzzapx, localzzaov);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */