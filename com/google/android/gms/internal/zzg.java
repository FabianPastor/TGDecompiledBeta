package com.google.android.gms.internal;

public final class zzg
  implements zzx
{
  private int zzn = 2500;
  private int zzo;
  private final int zzp = 1;
  private final float zzq = 1.0F;
  
  public zzg()
  {
    this(2500, 1, 1.0F);
  }
  
  private zzg(int paramInt1, int paramInt2, float paramFloat) {}
  
  public final int zza()
  {
    return this.zzn;
  }
  
  public final void zza(zzaa paramzzaa)
    throws zzaa
  {
    this.zzo += 1;
    this.zzn = ((int)(this.zzn + this.zzn * this.zzq));
    if (this.zzo <= this.zzp) {}
    for (int i = 1; i == 0; i = 0) {
      throw paramzzaa;
    }
  }
  
  public final int zzb()
  {
    return this.zzo;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */