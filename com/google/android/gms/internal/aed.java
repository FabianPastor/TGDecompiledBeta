package com.google.android.gms.internal;

import java.io.IOException;

public final class aed
  extends adg
{
  private static volatile aed[] zzctX;
  public String zzctY = "";
  
  public aed()
  {
    this.zzcsi = -1;
  }
  
  public static aed[] zzMf()
  {
    if (zzctX == null) {}
    synchronized (ade.zzcsh)
    {
      if (zzctX == null) {
        zzctX = new aed[0];
      }
      return zzctX;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if ((this.zzctY != null) && (!this.zzctY.equals(""))) {
      paramacy.zzl(1, this.zzctY);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzctY != null)
    {
      i = j;
      if (!this.zzctY.equals("")) {
        i = j + acy.zzm(1, this.zzctY);
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */