package com.google.android.gms.internal;

import java.io.IOException;

public final class aem
  extends adp
{
  private static volatile aem[] zzcum;
  public String zzcun = "";
  
  public aem()
  {
    this.zzcsx = -1;
  }
  
  public static aem[] zzMh()
  {
    if (zzcum == null) {}
    synchronized (adn.zzcsw)
    {
      if (zzcum == null) {
        zzcum = new aem[0];
      }
      return zzcum;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if ((this.zzcun != null) && (!this.zzcun.equals(""))) {
      paramadh.zzl(1, this.zzcun);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzcun != null)
    {
      i = j;
      if (!this.zzcun.equals("")) {
        i = j + adh.zzm(1, this.zzcun);
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */